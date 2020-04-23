## JDBC: Java Database Connectivity

### 1.introduce

1. JDBC 是 Java 访问数据库的基石, JDO/Hibernate/MyBatis 等只是更好的封装了 JDBC

2. definition:

   - 是一个 `独立` 于特定数据库管理系统, `通用` 的 SQL 数据库存取和操作的公共接口 API
   - 定义了用来访问数据库的标准 Java 类库[java.sql,javax.sql], 使用这些类库可以以一种`标准的方法`, 方便地访问 `不同的数据库资源`

3. diagram

   ![avatar](/static/image/db/jdbc-introduce.png)

   - 面向 Java Application: Java API
   - 面向 DataBase Driver: Java Driver API

### 2.connection

1. java.sql.Driver

   - 所有 JDBC 驱动程序需要实现的接口
   - 通过使用 java.sql.DriverManager 去调用
     - com.mysql.jdbc.Driver
     - ...
   - 加载与注册 JDBC 驱动

     - load: Class.forName("com.mysql.jdbc.Driver”);
     - register: DriverManager.registerDriver(com.mysql.jdbc.Driver): `一般不需要显示的书写`

       - 因为 Driver 接口的驱动程序类都包含了静态代码块
       - 在这个静态代码块中, 会调用 DriverManager.registerDriver() 方法来注册自身的一个实例

       ![avatar](/static/image/db/jdbc-driver.png)

2. URL: 标识一个`被注册的驱动程序`, 驱动程序管理器通过这个 URL 选择正确的驱动程序, 从而建立到数据库的连接

   - 协议: JDBC URL 中的协议总是 jdbc
   - 子协议: 子协议用于标识一个数据库驱动程序
   - 子名称：一种标识数据库的方法: 主机名 + 端口号 + 数据库名

   ![avatar](/static/image/db/jdbc-url.png)

   - sample

   ```properties
   jdbc:mysql://localhost:3306/atguigu?useUnicode=true&characterEncoding=utf8
   jdbc:mysql://localhost:3306/atguigu?user=root&password=123456
   ```

3. Connection: it will first init Driver, which contains register operation

   - com.mysql.jdbc.Driver

   ```java
   Driver driver = new com.mysql.jdbc.Driver();

   Properties info = new Properties();
   info.setProperty("user", "root");
   info.setProperty("password", "abc123");
   Connection conn = driver.connect("jdbc:mysql://localhost:3306/test", info);
   ```

   - Reflect Driver

   ```java
   Class clazz = Class.forName("com.mysql.jdbc.Driver");
   Driver driver = (Driver) clazz.newInstance();

   Properties info = new Properties();
   info.setProperty("user", "root");
   info.setProperty("password", "abc123");
   Connection conn = driver.connect("jdbc:mysql://localhost:3306/test", info);
   ```

   - DriverManager

   ```java
   // may not do register, it will auto register in static method in driver
   // Class clazz = Class.forName("com.mysql.jdbc.Driver");
   // Driver driver = (Driver) clazz.newInstance();
   // DriverManager.registerDriver(driver);
   Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "abc123");
   ```

   - DriverManager + .properties

   ```java
   InputStream in = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
   Properties props = new Properties();
   props.load(in);
   Class.forName(props.getProperty("driverClass"));
   Connection conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("user"), props.getProperty("password"));
   ```

4. 一个数据库连接就是一个 Socket 连接

### 3.PreparedStatement

1. `Statement: 用于执行静态 SQL 语句并返回它所生成结果的对象`

   - conn.createStatement()
   - statement.executeQuery("SQL"): query
   - int excuteUpdate(String sql): INSERT, UPDATE, DELETE
   - resultSet.getMetaData(): 数据库表相关信息
   - resultSet.getObject(columnName): 获取某一列的值

   - 缺点

     - 存在拼串操作, 繁琐
     - 存在 SQL 注入问题

   - code

   ```java
   public static <T> List<T> query(String sql, Class<T> clazz) throws Exception {
    List<T> list = new ArrayList<>();
    initConnection();

    Statement statement = conn.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    ResultSetMetaData rsmd = resultSet.getMetaData();

    int columnCount = rsmd.getColumnCount();

    while (resultSet.next()) {
      T t = clazz.newInstance();
      for (int i = 0; i < columnCount; i++) {
        String columnName = rsmd.getColumnLabel(i + 1);
        Object columnVal = resultSet.getObject(columnName);

        Field field = clazz.getDeclaredField(columnName);
        field.setAccessible(true);
        field.set(t, columnVal);
      }
      list.add(t);
    }

    return list;
   }
   ```

2. `PrepatedStatement: SQL 语句被预编译并存储在此对象中, 可以使用此对象多次高效地执行该语句`

3. `CallableStatement: 用于执行 SQL 存储过程`

### 4.BLOB

### 5.batch

### 6.transaction

### 7.pool

---

## 补充

1. SQL 注入

   - SQL 注入是利用某些系统没有对用户输入的数据进行充分的检查, 而在用户输入数据中`注入非法的 SQL 语句段或命令`, 从而利用系统的 SQL 引擎完成恶意行为的做法
   - e.g: SELECT user, password FROM user_table WHERE user='`a' OR 1 =`' AND password = '`OR '1' ='1`'
   - --> SELECT user, password FROM user_table WHERE user='a' OR 1 = '`AND password =`' OR `'1' ='1'`
   - solution: use `PrepatedStatement` to replace `Statement`
