## Mybatis

### introduce

1. defination

   - 可 `自定义 SQL, 存储过程, 高级映射` 的持久层框架
   - 免除了 JDBC 代码, 设置参数, 获取结果集的工作

2. 简单的 XML 或 Anotation 来 `配置` 和 `映射` 原始类型/接口/POJO 为`数据库中的记录`

3. SqlSessionFactoryBuilder can use xml or configuration to create SqlSessionFactory

   - should be only one in app, for free up resources about loading xml
   - xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE configuration
   PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
   "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
   <environments default="development">
      <environment id="development">
         <transactionManager type="JDBC"/>
         <dataSource type="POOLED">
         <property name="driver" value="${driver}"/>
         <property name="url" value="${url}"/>
         <property name="username" value="${username}"/>
         <property name="password" value="${password}"/>
         </dataSource>
      </environment>
   </environments>
   <mappers>
      <mapper resource="org/mybatis/example/BlogMapper.xml"/>
   </mappers>
   </configuration>
   ```

   - code

   ```java
   String resource = "org/mybatis/example/mybatis-config.xml";
   InputStream inputStream = Resources.getResourceAsStream(resource);
   SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
   ```

   - build without xml config

   ```java
   DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
   TransactionFactory transactionFactory = new JdbcTransactionFactory();
   Environment environment = new Environment("development", transactionFactory, dataSource);
   Configuration configuration = new Configuration(environment);
   configuration.addMapper(BlogMapper.class);
   SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
   ```

4. SqlSessionFactory

- should be only one in app, for free up resources about loading xml

5. SqlSession

   - **`SqlSession 的实例不是线程安全的, 因此是不能被共享的, 所以它的最佳的作用域是请求或方法作用域`**
   - 每次收到 HTTP 请求, 就可以打开一个 SqlSession, 返回一个响应后, 就关闭它

   - code

   ```java
   SqlSession sqlSession = sqlSessionFactory.openSession();
   Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);

   BlogMapper mapper = session.getMapper(BlogMapper.class);
   Blog blog = mapper.selectBlog(101);
   ```

6. 映射器实例

   - 方法作用域内
   - 映射器实例应该在调用它们的方法中被获取, 使用完毕之后即可丢弃, 小于 SqlSession Scope

   - code

   ```java
   BlogMapper mapper = session.getMapper(BlogMapper.class);
   ```

---

### XML Config

1. structure

   ```markdown
   configuration[配置]

   - properties[属性]
   - settings[设置]
   - typeAliases[类型别名]
   - typeHandlers[类型处理器]
   - objectFactory[对象工厂]
   - plugins[插件]
   - environments[环境配置]
     - environment[环境变量]
       - transactionManager[事务管理器]
       - dataSource[数据源]
   - databaseIdProvider[数据库厂商标识]
   - mappers[映射器]
   ```

2. properties: it is equaved to db.properties, which is define in external file

   ```xml
   <!-- define -->
   <properties resource="org/mybatis/example/config.properties">
      <!-- it will overwrite this property -->
      <property name="username" value="dev_user"/>
   </properties>

   <!-- retrieve -->
   <dataSource type="POOLED">
   <!-- value="${username:DEFAULT_VALUE}" is disable default -->
      <property name="username" value="${username:DEFAULT_VALUE}"/>
   </dataSource>
   ```

   - 设置好的属性可以在整个配置文件中用来替换需要动态配置的属性值

   - properties default value

   ```xml
   <properties>
      <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"/>
   </properties>
   ```

   - special case

   ```xml
   <!-- definition -->
   <properties>
      <!--
         if define this kind property, and we aslo want use default value feature:
            - so should define separator
       -->
      <property name="db:username" value="true"/>
        <property name="org.apache.ibatis.parsing.PropertyParser.default-value-separator" value="?:"/>
   </properties>

   <!-- usage -->
   <dataSource type="POOLED">
      <property name="username" value="${db:username?:ut_user}"/>
   </dataSource>
   ```

3. settings

```xml
<settings>
  <!-- enable cache about Mapper class -->
  <setting name="cacheEnabled" value="true"/>
  <!-- fetchType can overwirte the property -->
  <setting name="lazyLoadingEnabled" value="false"/>
  <!-- enabled will load all the lazy properties of the object. -->
  <setting name="aggressiveLazyLoading" value="false"/>
  <setting name="multipleResultSetsEnabled" value="true"/>
  <setting name="useColumnLabel" value="true"/>
  <setting name="useGeneratedKeys" value="false"/>
  <!-- 指定 MyBatis 应如何自动映射列到字段或属性: NONE, PARTIAL, FULL -->
  <setting name="autoMappingBehavior" value="PARTIAL"/>
  <!-- 指定发现自动映射目标未知列[或未知属性类型]的行为: NONE, WARNING, FAILING-->
  <setting name="autoMappingUnknownColumnBehavior" value="NONE"/>
  <!-- SIMPL, REUSE, BATCH -->
  <setting name="defaultExecutorType" value="SIMPLE"/>
  <setting name="defaultStatementTimeout" value="null"/>
  <!-- 建议设置值: default value is null -->
  <setting name="defaultFetchSize" value="100"/>
  <!-- FORWARD_ONLY | SCROLL_SENSITIVE | SCROLL_INSENSITIVE | DEFAULT -->
  <setting name="defaultResultSetType" value="null"/>
  <!-- 允许在嵌套语句中使用分页: false is enable feature -->
  <setting name="safeRowBoundsEnabled" value="false"/>
  <!-- 在嵌套语句中使用结果处理器: false is enable -->
  <setting name="safeResultHandlerEnabled" value="true"/>
  <!-- A_COLUMN mapping to aColumn -->
  <setting name="mapUnderscoreToCamelCase" value="false"/>
  <!-- SESSION | STATEMENT -->
  <setting name="localCacheScope" value="SESSION"/>
  <!-- 当没有为参数指定特定的 JDBC 类型时, 空值的默认 JDBC 类型 -->
  <setting name="jdbcTypeForNull" value="OTHER"/>
  <!-- 指定对象的哪些方法触发一次延迟加载 -->
  <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
  <setting name="callSettersOnNulls" value="false"/>
  <setting name="returnInstanceForEmptyRow" value="false"/>
  <setting name="logPrefix" value="null"/>
  <!-- SLF4J | LOG4J | LOG4J2 | JDK_LOGGING | COMMONS_LOGGING | STDOUT_LOGGING | NO_LOGGING -->
  <setting name="logImpl" value="null"/>
   <!-- 指定 Mybatis 创建可延迟加载对象 所用到的代理工具 -->
  <setting name="proxyFactory" value="null"/>
  <setting name="vfsImpl" value="null"/>
  <setting name="useActualParamName" value="true"/>
  <setting name="configurationFactory" value="null"/>
</settings>
```

4. typeAliases: 为 Java 类一个别名, 意在降低冗余的`全限定`类名

```xml
<typeAliases>
  <typeAlias alias="Blog" type="domain.blog.Blog"/>
</typeAliases>

<typeAliases>
   <!-- Bean 的首字母小写的非限定类名来作为它的别名 -->
  <package name="domain.blog"/>
</typeAliases>

@Alias("author") 优先级更高
```

5. typeHandlers: MyBatis 在设置预处理语句[PreparedStatement]中的参数或从结果集中取出一个值时, 都会用类型处理器将获取到的值以合适的方式转换成 Java 类型

- config

```xml
<typeHandlers>
  <typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
</typeHandlers>
```

|           handler            |    java type     |      jdbc type      |
| :--------------------------: | :--------------: | :-----------------: |
|      BooleanTypeHandler      | Boolean, boolean |       BOOLEAN       |
|       ByteTypeHandler        |    Byte, byte    |    NUMERIC, BYTE    |
|       ShortTypeHandler       |   Short, short   |  NUMERIC, SMALLINT  |
|      IntegerTypeHandler      |   Integer, int   |  NUMERIC, INTEGER   |
| long float double BigDecimal |       same       |        same         |
|      StringTypeHandler       |      String      |    CHAR, VARCHAR    |
|       ClobTypeHandler        |      String      |  CLOB, LONGVARCHAR  |
|      NStringTypeHandler      |      String      |   NVARCHAR, NCHAR   |
|     ByteArrayTypeHandler     |      byte[]      |        byte         |
|       BlobTypeHandler        |      byte[]      | BLOB, LONGVARBINARY |
|       DateTypeHandler        |       Date       |      TIMESTAMP      |
|      InstantTypeHandler      |     Instant      |      TIMESTAMP      |
|   LocalDateTimeTypeHandler   |  LocalDateTime   |      TIMESTAMP      |
|     LocalDateTypeHandler     |    LocalDate     |        DATE         |
|     LocalTimeTypeHandler     |    LocalTime     |        TIME         |

- 可以自定义 handler: `org.apache.ibatis.type.BaseTypeHandler`

6. objectFactory: 每次 MyBatis 创建结果对象的新实例时, 它都会使用一个对象工厂实例来完成实例化工作

   ```xml
   <objectFactory type="org.mybatis.example.ExampleObjectFactory">
      <property name="someProperty" value="100"/>
   </objectFactory>
   ```

7. environments: transactionManager + dataSource

```xml
<environments default="development">
  <environment id="development">
    <transactionManager type="JDBC">
      <property name="..." value="..."/>
    </transactionManager>
    <dataSource type="POOLED">
      <property name="driver" value="${driver}"/>
      <property name="url" value="${url}"/>
      <property name="username" value="${username}"/>
      <property name="password" value="${password}"/>
    </dataSource>
  </environment>
</environments>
```

8. mappers

```xml
<mappers>
   <!-- 使用相对于类路径的资源引用 -->
   <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
   <!-- 使用完全限定资源定位符[URL] -->
   <mapper url="file:///var/mappers/AuthorMapper.xml"/>
   <!-- 使用映射器接口实现类的完全限定类名 -->
   <mapper class="org.mybatis.builder.BlogMapper"/>
   <!-- 将包内的映射器接口实现全部注册为映射器 -->
   <package name="org.mybatis.builder"/>
</mappers>
```

### XML Mapper

1. structure

   - cache: 缓存配置
   - cache-ref: 引用其它命名空间的缓存配置
   - resultMap: 从数据库结果集中加载对象, _是最复杂也是最强大的元素_
   - sql: 可重用语句块
   - insert:
   - update:
   - delete:
   - select:

2. select

   - syntax

   ```xml
   <select id="selectPerson" parameterType="int" resultType="hashmap">
      SELECT * FROM PERSON WHERE ID = #{id}
   </select>
   ```

---

## conlusion

1. the diff of `${}` and `#{}`

   - `#{}` means _Statement_, 占位符; 替换是在 DBMS 中进行; 会对替换值加`'`; {}的值无默认值且与参数名无关
   - `${}` means _PreparedStatement_, 拼接符; 替换是在 DBMS 外进行; 不会对替换值加`'`; {}的值有默认值 \${value}

   - flow

     ```xml
     #{}：select * from t_user where uid=#{uid}
     #{}：select * from t_user where uid=#{arg0}
     #{}：select * from t_user where uid=#{param1}
     ${}：select * from t_user where uid= '${uid}'
     ${}：select * from t_user where uid= '${arg0}'
     ${}：select * from t_user where uid= '${param1}'

     #{}：select * from t_user where uid= ?
     ${}：select * from t_user where uid= '1'

     #{}：select * from t_user where uid= '1'
     ${}：select * from t_user where uid= '1'
     ```

   - conclusion
     1. 参数一律都建议使用注解@Param("")
     2. 能用 #{} 的地方就用 #{}, 不用或少用 \${}
     3. 表名作参数时, 必须用 \${}
     4. order by 时, 必须用 \${}: due to index
     5. 使用 ${} 时, 要注意何时加或不加单引号, 即 ${} 和 '\${}'

---

## Reference

1. [office-web](https://mybatis.org/mybatis-3/zh)

2. [\${} and #{}](https://blog.csdn.net/siwuxie095/article/details/79190856)
