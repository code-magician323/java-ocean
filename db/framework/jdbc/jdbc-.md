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

### 2.connection: `尽量晚创建, 尽量早的释放`

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

4. `一个数据库连接就是一个 Socket 连接`

### 3.PreparedStatement

![avatar](/static/image/db/jdbc-execute.png)

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

   ```xml
   <!--
   public static List<T> query(String sql) throws Exception {

    // get T type for create istance
    private Class<T> type;
    Class clazz = this.getClass();
    // 获取父类的类型
    // getGenericSuperclass()用来获取当前类的父类的类型
    // ParameterizedType 表示的是带泛型的类型
    ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
    // 获取具体的泛型类型 getActualTypeArguments 获取具体的泛型的类型
    // 这个方法会返回一个 Type 的数组
    Type[] types = parameterizedType.getActualTypeArguments();
    // 获取具体的泛型的类型
    type = (Class<T>) types[0];

    List<T> list = new ArrayList<>();
    initConnection();

    Statement statement = conn.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    ResultSetMetaData rsmd = resultSet.getMetaData();

    int columnCount = rsmd.getColumnCount();

    while (resultSet.next()) {
      T t = type.newInstance();
      for (int i = 0; i < columnCount; i++) {
        String columnName = rsmd.getColumnLabel(i + 1);
        Object columnVal = resultSet.getObject(columnName);

        Field field = type.getDeclaredField(columnName);
        field.setAccessible(true);
        field.set(t, columnVal);
      }
      list.add(t);
    }

    return list;
   }
    -->
   ```

2) `PrepatedStatement: SQL 语句被预编译并存储在此对象中, 可以使用此对象多次高效地执行该语句`

   - 它表示一条预编译过的 SQL 语句
   - 使用 ? 作为占位符, 通过 setXX(index, value) 设置值, index start from 1
   - advantage

     - perfect performance
     - high speed: DBServer will cache executable code, next execution will just set paramter and no compile
       - statement 语句中, 即使是相同操作但因为数据内容不一样, 所以整个语句本身不能匹配, 没有缓存语句的意义.
       - `事实是没有数据库会对普通语句编译后的执行代码缓存`
     - no SQL inject issue

   - prepareStatement = conn.prepareStatement("insert into customers(name,email,birth,photo)values(?,?,?,?)");
   - prepareStatement.setObject(i, args[i]);
   - prepareStatement.execute();

3) `CallableStatement: 用于执行 SQL 存储过程`

4) ResultSet

   - 以 `逻辑表格的形式` 封装了执行数据库操作的结果集
   - ResultSet 对象维护了一个指向当前数据行的游标, next() 将其指向下一行
   - ResultSet resultSet = statement.executeQuery(sql);
   - ResultSet resultSet = prepareStatement.executeQuery(sql);
   - getXxx(int index) and getXxx(String columnName) to get result

5) ResultSetMetaData

   - 个描述 ResultSet 的对象的对象, 即描述数据库和表相关信息
   - `ResultSetMetaData meta = rs.getMetaData();`
     - getColumnName(int column): 获取指定列的名称
     - getColumnLabel(int column): 获取指定列的别名
     - getColumnCount(): 返回当前 ResultSet 对象中的列数
     - getColumnTypeName(int column): 检索指定列的数据库特定的类型名称
     - getColumnDisplaySize(int column): 指示指定列的大标准宽度, 以字符为单位
     - isNullable(int column): 指示指定列中的值是否可以为 null
     - isAutoIncrement(int column): 指示是否自动为指定列进行编号, 这样这些列仍然是只读的

### 4.data type mapping

|     java type      |          SQL type          |
| :----------------: | :------------------------: |
|      boolean       |            BIT             |
|       `byte`       |         `TINYINT`          |
|       short        |         `SMALLINT`         |
|        int         |          INTEGER           |
|        long        |           BIGINT           |
|       String       | CHAR, VARCHAR, LONGVARCHAR |
|     byte array     |    BINARY , VAR BINARY     |
|   java.sql.Date    |            DATE            |
|   java.sql.Time    |            TIME            |
| java.sql.Timestamp |         TIMESTAMP          |

1. BLOB: 是一个可以存储大量数据的二进制大型对象容器

   - 数据库存储大文件会导致性能下降
   - PreparedStatement 才能 插入 BLOB 类型数据

   |    type    | size |
   | :--------: | :--: |
   |  TinyBlob  | 255b |
   |    Blob    | 65K  |
   | MediumBlob | 16M  |
   |  LongBlob  |  4G  |

   - `xxx too large`: modify my.ini about `max_allowed_packet=16M`, then restart mysql
   - update: prepareStatement.setBlob(4, new FileInputStream("PATH"));
   - select: Blob photo = rs.getBlob(5); InputStream is = photo.getBinaryStream();

### 5.batch: ?rewriteBatchedStatements=true 开启 MySQL 批量支持

1. type
   - 多条 SQL 语句的批量处理
   - 一个 SQL 语句的批量传参
2. API

   - addBatch(String): 添加需要批量处理的 SQL 语句或是参数
   - executeBatch(): 执行批量处理语句
   - clearBatch(): 清空缓存的数据

3. code

   ```java
   // 20000条: 625
   // 1000000条: 14733
   public void testInsert1() throws Exception {
       Connection conn = JDBCUtils.getConnection();
       String sql = "insert into goods(name)values(?)";
       PreparedStatement ps = conn.prepareStatement(sql);
       for(int i = 1; i <= 1000000; i++) {
           ps.setString(1, "name_" + i);
           //1."攒" SQL
           ps.addBatch();
           if(i % 500 == 0){
               //2.执行
               ps.executeBatch();
               //3.清空
               ps.clearBatch();
           }
       }
   }
   ```

4. optimize: 使用 Connection 的 setAutoCommit(false) / commit()

   ```java
   // 1000000条: 4978
   public void testInsert1() throws Exception {
       Connection conn = JDBCUtils.getConnection();
       //1.设置为不自动提交数据
       conn.setAutoCommit(false);

       String sql = "insert into goods(name)values(?)";
       PreparedStatement ps = conn.prepareStatement(sql);
       for(int i = 1; i <= 1000000; i++) {
           ps.setString(1, "name_" + i);
           ps.addBatch();
           if(i % 500 == 0){
               ps.executeBatch();
               ps.clearBatch();
           }
       }
       //2.提交数据
       conn.commit();
   }
   ```

### 6.transaction

1. [reference]()

2. 当一个 Connection 对象被创建时, 默认情况下是自动提交事务
3. 关闭数据库 Connection, 数据就会自动的提交
   - 如果多个操作, 每个操作使用的是自己单独的连接, 则无法保证事务
   - 即同一个事务的多个操作必须在同一个连接下
4. setAutoCommit(false): 取消自动提交事务
   - setAutoCommit(true) 尤其是在使用数据库连接池技术时, 执行 close()方法前, 建议恢复自动提交状态
5. commit(): 提交事务
6. rollback(): 回滚事务

7. ACID

### 7.pool

---

## 补充

1. SQL 注入

   - SQL 注入是利用某些系统没有对用户输入的数据进行充分的检查, 而在用户输入数据中`注入非法的 SQL 语句段或命令`, 从而利用系统的 SQL 引擎完成恶意行为的做法
   - e.g: SELECT user, password FROM user_table WHERE user='`a' OR 1 =`' AND password = '`OR '1' ='1`'
   - --> SELECT user, password FROM user_table WHERE user='a' OR 1 = '`AND password =`' OR `'1' ='1'`
   - solution: use `PrepatedStatement` to replace `Statement`

## conlusion

1. Java 与数据库交互涉及到的相关 Java API 中的索引都从 1 开始
