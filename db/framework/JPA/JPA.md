**Table of Contents** _generated with [DocToc](https://github.com/thlorenz/doctoc)_

- [JPA[Hibernate]](#jpahibernate)
  - [简介](#%E7%AE%80%E4%BB%8B)
  - [Quick Start](#quick-start)
  - [JPA Annotation](#jpa-annotation)
  - [JPA API](#jpa-api)
    - [Persistence](#persistence)
    - [EntityManagerFactory](#entitymanagerfactory)
    - [EntityManager: `持久化核心`](#entitymanager-%E6%8C%81%E4%B9%85%E5%8C%96%E6%A0%B8%E5%BF%83)
      - [Knowledge](#knowledge)
      - [API](#api)
    - [EntityTransaction](#entitytransaction)
  - [Mapping Relation](#mapping-relation)
    - [单向多对一 和 单向一对多](#%E5%8D%95%E5%90%91%E5%A4%9A%E5%AF%B9%E4%B8%80-%E5%92%8C-%E5%8D%95%E5%90%91%E4%B8%80%E5%AF%B9%E5%A4%9A)
    - [双向一对一](#%E5%8F%8C%E5%90%91%E4%B8%80%E5%AF%B9%E4%B8%80)
    - [双向多对多](#%E5%8F%8C%E5%90%91%E5%A4%9A%E5%AF%B9%E5%A4%9A)
    - [总结](#%E6%80%BB%E7%BB%93)
  - [JAP Cache](#jap-cache)
    - [二级缓存](#%E4%BA%8C%E7%BA%A7%E7%BC%93%E5%AD%98)
  - [JPQL[Java Persistence Query Language]](#jpqljava-persistence-query-language)
- [Integration Spring](#integration-spring)
  - [QUCIK START](#qucik-start)
  - [LocalContainerEntityManagerFactoryBean[recommand]](#localcontainerentitymanagerfactorybeanrecommand)
  - [LocalEntityManagerFactoryBean](#localentitymanagerfactorybean)
  - [JNDI](#jndi)
- [Integration Spring Data](#integration-spring-data)
  - [QUICK START](#quick-start)
  - [Repository 具体](#repository-%E5%85%B7%E4%BD%93)

## JPA[Hibernate]

### 简介

- 1. JPA[Java Persistence API] 是由 sun 公司制定的 `ORM` 规范; JDBC 是由 sun 公司制定的 `连接数据库` 规范.
- 2. Hibernate 是一种实现了 JPA 规范的 ORM 框架, `JPA 是 Hibernate 功能的一个子集`.
- 3. 优点:
  ```
  1. 标准化
  2. 简单易用, 集成方便
  3. 可媲美JDBC的查询能力[JPQL]
  4. 支持面向对象的高级特性
  ```
- 4. JPA 技术范畴:
  ```
  1. ORM 映射元数据: XML 和 注解
  2. JPA 的 API
  3. 查询语言(JPQL)
  ```

### Quick Start

- 1. create maven project, and the dependency is on the following:

  ```xml
  <!--  hibernate 依赖 -->
  <dependency>
      <groupId>org.hibernate</groupId>
      <artifactId>hibernate-entitymanager</artifactId>
      <version>5.4.1.Final</version>
  </dependency>

  <!-- mysql 驱动 -->
  <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.13</version>
  </dependency>
  ```

- 2. Confige persistence.xml, including but not limit: persistence-unit's name, provider of JPA, Data source, show_sql, format_sql. **`Notice the persistence.xml file msut be in source/ META-INF/.`** The demo configration is on the following:

  ```xml
  <persistence-unit name="NewPersistenceUnit">
      <!--
        配置使用什么ORM 产品作为JPA 的实现
          1.实际上配置的是 javax.persistence.spi.PersistenceProvider 接口的实现类
          2.若JPA项目中只有一个JPA的实现产品, 则也可以不配置该节点.
      -->
      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
      <!-- 添加持久化类-->
      <class>com.augmentum.jpa.JPAEntity</class>

      <!--
        配置二级缓存的策略
          ALL: 所有的实体类都被缓存
          NONE: 所有的实体类都不被缓存.
          ENABLE_SELECTIVE: 标识 @Cacheable(true) 注解的实体类将被缓存
          DISABLE_SELECTIVE: 缓存除标识 @Cacheable(false) 以外的所有实体类
          UNSPECIFIED: 默认值, JPA 产品默认值将被使用
      -->
      <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>

      <properties>
          <!-- 连接数据库的基本信息 -->
          <property name="hibernate.connection.url" value="jdbc:mysql://101.132.45.28:3306/jpa?useSSL=false&amp;serverTimezone=Asia/Shanghai"/>
          <property name="hibernate.connection.driver_class" value="com.mysql.cj.jdbc.Driver"/>
          <property name="hibernate.connection.username" value="root"/>
          <property name="hibernate.connection.password" value="Yu1252068782?"/>

          <!-- 配置JPA 实现产品的基本属性, 配置hibernate 的基本属性 -->
          <!-- 自动显示SQL -->
          <property name="hibernate.show_sql" value="true"/>
          <!-- 格式化sql -->
          <property name="hibernate.format_sql" value="true"/>
          <!--生成数据表的策略-->
          <!--注意这个属性, 自动生成的文件前面没有 hibernate, 要加上 hibernate -->
          <property name="hibernate.hbm2ddl.auto" value="update"/>
          <!-- 使用 MySQL8Dialect -->
          <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
      </properties>
  ```

- 3. create the jpa entity.

  ```java
  @Table(name = "JPAEntity")
  @Entity
  public class JPAEntity {

      private Integer id;
      private String lastName;

      @GeneratedValue(strategy = GenerationType.AUTO)
      @Id
      public Integer getId() {
          return id;
      }

      public void setId(Integer id) {
          this.id = id;
      }

      @Column(name = "Last_Name")
      public String getLastName() {
          return lastName;
      }

      public void setLastName(String lastName) {
          this.lastName = lastName;
      }
  }
  ```

- 4. Finsh to create table info in DB by script.
  ```java
  // 1. 创建 EntityManagerFactory
  String persistenceUnitName = "NewPersistenceUnit";
  EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
  // 2. 创建 EntityManager
  EntityManager entityManager = entityManagerFactory.createEntityManager();
  // 3. 开启事务
  EntityTransaction transaction = entityManager.getTransaction();
  transaction.begin();
  // 4. 进行持久化操作
  JPAEntity jpaEntity= new JPAEntity();
  jpaEntity.setAge(13);
  jpaEntity.setEmail("jellily@qq.com");
  jpaEntity.setLastName("jellily");
  entityManager.persist(jpaEntity);
  // 5. 提交事务
  transaction.commit();
  // 6. 关闭 EntityManager
  entityManager.close();
  // 7. 关闭 EntityManagerFactory
  entityManagerFactory.close();
  ```
- 5. Check in the table info in DB. That is OK!

### JPA Annotation

- @Entity
  - 表明该类为实体类, 将映射到指定的数据库表.
- @Table: 实体类与数据库表名称不一致.

  ```java
  @Table(name = "TABLE_NAME")
  ```

  - name 属性: 指定数据库表名
  - 用 table 来生成主键详解: 数据库中有一张数据表 `jpa_id_generators` 用于生产主键值. `好处在于数据库的迁移性`
    ```java
    @TableGenerator(name="ID_GENERATOR",
      table="jpa_id_generators",
      pkColumnName="PK_NAME",
      pkColumnValue="CUSTOMER_ID",
      valueColumnName="PK_VALUE",
      allocationSize=100)
    @GeneratedValue(strategy=GenerationType.TABLE,generator="ID_GENERATOR")
    ```
  - catalog 和 schema: 分别用于设置所属数据库目录和模式

- @Id
  - 表明主键列
  - [推荐]置于属性的 getter 方法之前
- @GeneratedValue
  ```java
  @GeneratedValue(strategy = GenerationType.AUTO)
  ```
  - 标注主键的生成策略
  - type
    - IDENTITY: 自增主键字段
    - AUTO[默认]: JPA 自动选择合适的策略
    - SEQUENCE: 序列产生主键, @SequenceGenerator 注解指定序列名, MySql 不支持这种方式
    - TABLE: 通过表产生主键, 框架借由表模拟序列产生主键, 使用该策略可以使应用更易于数据库移植
- @Basic: 默认的字段注解[不写]
- @Column: 指定实体的属性对应数据表的字段
  - name: 数据库字段名
  - columnDefinition: 数据库字段数据类型
  - unique
  - nullable
  - length
- @Transient
  - 不映射到数据库字段
- @Temporal: 指定 Date 数据类型的精度; 数据库中的 Date 类型有三种: TEAR, TIME, DATE, DATETIME, TIMESTAMP

  ```java
  @Temporal(TemporalType.TIME)
  ```

  - type:
    - TIME: 时间
    - DATE: 日期
    - DATETIME: 时间日期

- @Cacheable(true)
  - 表示根据 `persistence.xml` 中配置的缓存机制标识是否缓存.

### JPA API

#### Persistence

- createEntityManagerFactory(String persistenceUnitName) 用来创建 `EntityManagerFactory` 实例

#### EntityManagerFactory

- createEntityManager(): 用来创建 `EntityManager` 实例
- createEntityManager(Map map): map 提供 EntityManager 的属性
- isOpen(): 检查 EntityManagerFactory 是否处于打开状态, 默认打开[只有调用 close()方法才关闭]
- close(): 关闭 EntityManagerFactory, 释放资源

#### EntityManager: `持久化核心`

##### Knowledge

- 实体的状态:
  ```js
  新建状态: 新创建的对象, 尚未拥有持久性主键[临时对象].
  持久化状态: 已经拥有持久性主键并和持久化建立了上下文环境[有主键的对象,但是没有和数据库关系]
  游离状态: 拥有持久化主键, 但是没有与持久化建立上下文环境
  删除状态: 拥有持久化主键, 已经和持久化建立上下文环境, 但是从数据库中删除.
  ```

##### API

- find(Class<T> entityClass,Object primaryKey)
- getReference(Class<T> entityClass,Object primaryKey)
  - 在 entityManager 关闭的情况下, 可能会出现懒加载引起的异常
- persist(T entity)
  - 如果有持久化主键会报错, hibernate 中 save 不会
- remove(T entity)
  - 不能移除游离对象, hibernate 中 delete 可以
- merge(T entity)
  - entity 为临时对象时, 会创建一个新的对象, 把 entity 赋值给新的对象后, 向数据保存新对象. `[所以只新对象有 ID]`
  - entity 为游离对象时 `[具有 OID]`, entityManager 的缓存也没有, 数据库内也没有, 则会创建一个新的对象, 把 entity 赋值给新的对象后, 向数据保存新对象. `[插入数据库数据主键是新生成的ID, 不是OID]`
  - entity 为游离对象时 `[具有 OID]`, entityManager 的缓存也没有, 数据库内有[查出], 则会将游离对象数据赋值给数据库内的那条数据, 执行 update 操作.
  - entity 为游离对象时 `[具有 OID]`, entityManager 的缓存有, 则会将游离对象数据赋值给缓存内的那条数据, 执行 update 操作.
  - [图解]
    ![avatar](/static/image/jpa/JPA-merge.png)
- EntityTransaction
- flush()
- setFlushMode(FlushModeType flushMode)
- getFlushMode()
- refresh(T entity)
- clear()
- contains(T entity)
- isOpen()
- close()
- createQuery (String qlString)

  ```java
  String jpql = "FROM Customer c WHERE c.age > ?";
  // 默认情况下, 若只查询部分属性, 则将返回 Object[] 类型的结果. 或者 Object[] 类型的 List.
  // 也可以在实体类中创建对应的构造器, 然后再 JPQL 语句中利用对应的构造器返回实体类的对象.
  String jpql = "SELECT new Customer(c.lastName, c.age) FROM Customer c WHERE c.id > ?";
  Query query = entityManager.createQuery(jpql).setParameter(1, 1);;
  List<Customer> customers = query.getResultList();

  // 在 entity 上有注解
  // @NamedQuery(name="testNamedQuery", query="FROM Customer c WHERE c.id = ?")
  Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 3);
  Customer customer = (Customer) query.getSingleResult();
  ```

- createNamedQuery (String name)

  ```java
  // 适用于在实体类前使用 @NamedQuery 标记的查询语句
  String sql = "SELECT age FROM jpa_cutomers WHERE id = ?";
  Query query = entityManager.createNativeQuery(sql).setParameter(1, 3);

  Object result = query.getSingleResult();
  ```

- createNativeQuery (String sqlString)

  ```java
  // 适用于本地 SQL
  String sql = "SELECT age FROM jpa_cutomers WHERE id = ?";
  Query query = entityManager.createNativeQuery(sql).setParameter(1, 3);
  Object result = query.getSingleResult();
  ```

- createNativeQuery (String sqls, String resultSetMapping)
- getTransaction()

- setHint(QueryHints.HINT_CACHEABLE, true)

  ```java
  // 使用 hibernate 的查询缓存
  String jpql = "FROM Customer c WHERE c.age > ?";
  Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true).setParameter(1, 1);
  List<Customer> customers = query.getResultList();
  ```

- JPQL 的关联查询同 HQL 的关联查询

  ```java
  String jpql = "FROM Customer c LEFT OUTER JOIN FETCH c.orders WHERE c.id = ?";
  Customer customer = (Customer) entityManager.createQuery(jpql).setParameter(1, 12).getSingleResult();

  String jpql = "FROM Customer c LEFT OUTER JOIN c.orders WHERE c.id = ?";
  List<Object[]> result = entityManager.createQuery(jpql).setParameter(1, 12).getResultList();
  ```

#### EntityTransaction

- begin ()
- commit ()
- rollback ()
- setRollbackOnly ()
- getRollbackOnly ()
- isActive ()

### Mapping Relation

#### 单向多对一 和 单向一对多

- 在 one 方指定 @OneToMany , 设置 mappedBy 属性[指定谁是维护关系的一方]
- 在 many 方指定 @ManyToOne 注释, 并使用 @JoinColumn 指定外键名称
- cascade 标识级联删除

- one

```java
@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.REMOVE}, mappedBy="customer")
public Set<Order> getOrders() {
  return orders;
}
```

- many

```java
@JoinColumn(name="CUSTOMER_ID")
@ManyToOne(fetch=FetchType.LAZY)fetch=FetchType.LAZY)
public Customer getCustomer() {
  return customer;
}
```

#### 双向一对一

- @OneToOne 注释中指定 mappedBy
- 关系维护端(owner side)上建立外键列指向关系被维护端的主键列
- 懒加载
  - 查取关系维护方 manger 时, 可以懒加载被维护方 department
  - 查取关系被维护方 department 时, manger 一定会被查取

```java
@OneToOne(mappedBy="mgr")
public Department getDept() {
  return dept;
}

@JoinColumn(name="MGR_ID", unique=true)
@OneToOne(fetch=FetchType.LAZY)
public Manager getMgr() {
  return mgr;
}
```

#### 双向多对多

- @ManyToMany mappedBy 指定一个关系维护端

```java
// 维护方 ANNOTATION
@ManyToMany
@JoinTable(name="中间表名称",
  joinColumns=@joinColumn(name="本类的外键",
  referencedColumnName="本类与外键对应的主键"),
  inversejoinColumns=@JoinColumn(name="对方类的外键",
  referencedColunName="对方类与外键对应的主键")
)

// 被维护方 ANNOTATION: 指定关系由 xxx 维护
@OneToOne(mappedBy="xxx")
```

#### 总结

- 多对一和一对多的情况下, 关系应该交给多的一方维护; 且插入数据时应该先插入一的一方
- `先插入不维护关系的一方`
- 如果延迟加载要起作用, 就必须设置一个代理对象.因此 OneToOne 不需要在被维护方上[写懒加载](#双向一对一)

### JAP Cache

#### 二级缓存

- 1. `persistence.xml` 文件中配置缓存机制, 和相关的缓存需要的信息, 如使用什么缓存等.

  ```xml
  <persistence-unit name="jpa-1" transaction-type="RESOURCE_LOCAL">

    <!-- 其他信息 -->
    <!--
      配置二级缓存的策略
        ALL: 所有的实体类都被缓存
        NONE: 所有的实体类都不被缓存.
        ENABLE_SELECTIVE: 标识 @Cacheable(true) 注解的实体类将被缓存
        DISABLE_SELECTIVE: 缓存除标识 @Cacheable(false) 以外的所有实体类
        UNSPECIFIED: 默认值, JPA 产品默认值将被使用
    -->
    <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>

    <properties>
      <!-- 二级缓存相关: ehcache-->
      <property name="hibernate.cache.use_second_level_cache" value="true"/>
      <property name="hibernate.cache.region.factory_class" value="org.hibernate.cache.ehcache.EhCacheRegionFactory"/>
      <property name="hibernate.cache.use_query_cache" value="true"/>
    </properties>
  </persistence-unit>
  ```

- 2. @Cacheable(true)
- 3. 需要 ehcache 的相关配置信息

### JPQL[Java Persistence Query Language]

- 使用 Hibernate 的查询缓存
- ORDER BY 和 GROUP BY
- 关联查询
- 子查询 和 JPQL 函数
- UPDATE 和 DELETE

## Integration Spring

### QUCIK START

- 1. `pom.xml` dependency

  ```xml
  <dependencies>
      <!-- junit test -->
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.12</version>
          <scope>test</scope>
      </dependency>

      <!--spring web jar包-->
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-web</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-webmvc</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>

      <!--spring jar包-->
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-context</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-beans</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-aop</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-core</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>

      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-aspects</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-tx</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-orm</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>

      <!--JPA jar 包-->
      <!-- mysql driver-->
      <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>8.0.17</version>
      </dependency>

      <!--hibernate-->
      <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-entitymanager</artifactId>
          <version>5.4.4.Final</version>
      </dependency>
      <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-c3p0</artifactId>
          <version>5.4.4.Final</version>
      </dependency>

      <!--日志jar-->
      <dependency>
          <groupId>ch.qos.logback</groupId>
          <artifactId>logback-classic</artifactId>
          <version>1.2.3</version>
      </dependency>
      <dependency>
          <groupId>ch.qos.logback</groupId>
          <artifactId>logback-core</artifactId>
          <version>1.2.3</version>
      </dependency>
      <dependency>
          <groupId>org.slf4j</groupId>
          <artifactId>slf4j-api</artifactId>
          <version>1.7.25</version>
      </dependency>

      <!--json-->
      <dependency>
          <groupId>com.alibaba</groupId>
          <artifactId>fastjson</artifactId>
          <version>1.2.59</version>
      </dependency>

      <!-- web -->
      <dependency>
          <groupId>javax.servlet</groupId>
          <artifactId>javax.servlet-api</artifactId>
          <version>3.0.1</version>
          <scope>provided</scope>
      </dependency>
      <dependency>
          <groupId>commons-httpclient</groupId>
          <artifactId>commons-httpclient</artifactId>
          <version>3.0</version>
      </dependency>
      <dependency>
          <groupId>org.apache.tomcat.embed</groupId>
          <artifactId>tomcat-embed-websocket</artifactId>
          <version>8.0.23</version>
          <scope>provided</scope>
      </dependency>

      <!-- XPath -->
      <dependency>
          <groupId>jaxen</groupId>
          <artifactId>jaxen</artifactId>
          <version>1.1.6</version>
      </dependency>
  </dependencies>
  ```

- 2. 配置 `ApplicationContext.xml` 文件

  ```xml
  <!-- 1. 配置自动扫描的包 IOC -->
  <context:component-scan base-package="com.augmentum.jpa"></context:component-scan>

  <!-- 2. 配置 C3P0 数据源 -->
  <context:property-placeholder location="classpath:db.properties"/>
  <bean id="dataSource"
        class="com.mchange.v2.c3p0.ComboPooledDataSource">
      <property name="user" value="${jdbc.user}"></property>
      <property name="password" value="${jdbc.password}"></property>
      <property name="driverClass" value="${jdbc.driverClass}"></property>
      <property name="jdbcUrl" value="${jdbc.jdbcUrl}"></property>
  </bean>

  <!-- 3. 配置 EntityManagerFactory -->
  <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
    <property name="dataSource" ref="dataSource"></property>
    <!-- 3.1 配置 JPA 提供商的适配器. 可以通过内部 bean 的方式来配置 -->
    <property name="jpaVendorAdapter">
        <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"></bean>
    </property>
    <!-- 3.2 配置实体类所在的包 -->
    <property name="packagesToScan" value="com.augmentum.jpa.entities"></property>
    <!-- 3.3 配置 JPA 的基本属性. 例如 JPA 实现产品的属性 -->
    <property name="jpaProperties">
        <props>
            <prop key="hibernate.show_sql">true</prop>
            <prop key="hibernate.format_sql">true</prop>
            <prop key="hibernate.hbm2ddl.auto">update</prop>
        </props>
    </property>
  </bean>

  <!-- 4. 配置 JPA 使用的事务管理器 -->
  <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
      <property name="entityManagerFactory" ref="entityManagerFactory"></property>
  </bean>
  <tx:annotation-driven transaction-manager="transactionManager"/>
  ```

- 3. @Repository

  ```java
  @PersistenceContext // 获取到和当前事务关联的 EntityManager 对象
  private EntityManager entityManager;
  ```

### LocalContainerEntityManagerFactoryBean[recommand]

- 1. 适用于所有环境的 FactoryBean;
- 2. 能全面控制 EntityManagerFactory 配置: 如指定 Spring 定义的 DataSource 等等

### LocalEntityManagerFactoryBean

- 1. 适用于那些 `仅` 使用 JPA 进行数据访问的项目;
- 2. 该 FactoryBean 将根据 JPA PersistenceProvider 自动检测配置文件进行工作, 一般从 `resources/META-INF/persistence.xml` 读取配置信息;
- 3. 不能设置 Spring 中定义的 DataSource, 且不支持 Spring 管理的全局事务

### JNDI

- 1. 用于从 Java EE 服务器获取指定的 EntityManagerFactory;
- 2. 这种方式在进行 Spring 事务管理时一般要使用 JTA 事务管理

## [Integration Spring Data](https://docs.spring.io/spring-data/jpa/docs/2.1.10.RELEASE/reference/html/#repositories.create-instances.spring)

### QUICK START

- 1. `pom.xml` dependency

  ```xml
  <dependencies>
      <!-- junit test -->
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.12</version>
          <scope>test</scope>
      </dependency>

      <!--spring web jar包-->
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-web</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-webmvc</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>

      <!--spring jar包-->
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-context</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-beans</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-aop</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-core</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>

      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-aspects</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-tx</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-orm</artifactId>
          <version>5.1.9.RELEASE</version>
      </dependency>

      <!--JPA jar 包-->
      <!-- mysql driver-->
      <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>8.0.17</version>
      </dependency>

      <!--hibernate-->
      <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-entitymanager</artifactId>
          <version>5.4.4.Final</version>
      </dependency>
      <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-c3p0</artifactId>
          <version>5.4.4.Final</version>
      </dependency>

      <!--日志jar-->
      <dependency>
          <groupId>ch.qos.logback</groupId>
          <artifactId>logback-classic</artifactId>
          <version>1.2.3</version>
      </dependency>
      <dependency>
          <groupId>ch.qos.logback</groupId>
          <artifactId>logback-core</artifactId>
          <version>1.2.3</version>
      </dependency>
      <dependency>
          <groupId>org.slf4j</groupId>
          <artifactId>slf4j-api</artifactId>
          <version>1.7.25</version>
      </dependency>

      <!--json-->
      <dependency>
          <groupId>com.alibaba</groupId>
          <artifactId>fastjson</artifactId>
          <version>1.2.59</version>
      </dependency>

      <!-- web -->
      <dependency>
          <groupId>javax.servlet</groupId>
          <artifactId>javax.servlet-api</artifactId>
          <version>3.0.1</version>
          <scope>provided</scope>
      </dependency>
      <dependency>
          <groupId>commons-httpclient</groupId>
          <artifactId>commons-httpclient</artifactId>
          <version>3.0</version>
      </dependency>
      <dependency>
          <groupId>org.apache.tomcat.embed</groupId>
          <artifactId>tomcat-embed-websocket</artifactId>
          <version>8.0.23</version>
          <scope>provided</scope>
      </dependency>

      <!-- XPath -->
      <dependency>
          <groupId>jaxen</groupId>
          <artifactId>jaxen</artifactId>
          <version>1.1.6</version>
      </dependency>
  </dependencies>
  ```

- 2. 配置 `ApplicationContext.xml` 文件

  - [EntityManagerFactory-entityManagerFactory-jpaPropertyMap 参考](https://www.cnblogs.com/henuyuxiang/p/6676824.html)

  ```xml
  <!-- 1. 配置自动扫描的包 IOC -->
  <context:component-scan base-package="com.augmentum.jpa"></context:component-scan>

  <!-- 2. 配置 DruidDataSource 数据源 -->
  <context:property-placeholder location="classpath:db.properties"/>
  <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
      <property name="username" value="${user}"></property>
      <property name="password" value="${password}"></property>
      <property name="driverClassName" value="${driverClass}"></property>
      <property name="url" value="${jdbcUrl}"></property>
  </bean>

  <!-- 3. 配置 EntityManagerFactory -->
  <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
    <property name="dataSource" ref="dataSource"></property>
    <!-- 3.1 配置 JPA 提供商的适配器. 可以通过内部 bean 的方式来配置 -->
    <property name="jpaVendorAdapter">
         <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
              <property name="generateDdl" value="false"/>
              <property name="database" value="MYSQL"/>
              <property name="databasePlatform" value="org.hibernate.dialect.MySQL5InnoDBDialect"/>
              <property name="showSql" value="true"/>
          </bean>
    </property>
    <!-- 3.2 配置实体类所在的包 -->
    <property name="packagesToScan" value="com.augmentum.jpa.entities"></property>
    <!-- 3.3 用于指定一些高级特性,如事务管理等 -->
    <property name="jpaDialect">
        <bean class="org.springframework.orm.jpa.vendor.HibernateJpaDialect"/>
    </property>
    <!-- 3.4 配置 JPA 的基本属性. 例如 JPA 实现产品的属性 -->
    <property name="jpaPropertyMap">
          <map>
            <!-- 将Hibernate查询中的符号映射到SQL查询中的符号 (符号可能是函数名或常量名字). 取值 hqlLiteral=SQL_LITERAL, hqlFunction=SQLFUNC -->
            <entry key="hibernate.query.substitutions" value="true 1, false 0"/>
            <!--为Hibernate关联的批量抓取设置默认数量.取值 建议的取值为4, 8, 和16-->
            <entry key="hibernate.default_batch_fetch_size" value="16"/>
            <!-- 如果开启, Hibernate将收集有助于性能调节的统计数据. -->
            <entry key="hibernate.generate_statistics" value="true"/>

            <!-- 非零值，允许Hibernate使用JDBC2的批量更新. 取值 建议取5到30之间的值 -->
            <!-- The first property tells Hibernate to collect inserts in batches of four. The order_inserts property tells Hibernate to take the time to group inserts by entity, creating larger batches. -->
            <entry key="hibernate.jdbc.batch_size" value="16"/>
            <entry key=" hibernate.order_inserts" value="true"/>

            <!-- 为单向关联(一对一, 多对一)的外连接抓取（outer join fetch）树设置最大深度. 值为0意味着将关闭默认的外连接抓取. 取值 建议在0到3之间取值 -->
            <entry key="hibernate.max_fetch_depth" value="2"/>
            <!-- 开启CGLIB来替代运行时反射机制(系统级属性). 反射机制有时在除错时比较有用. 注意即使关闭这个优化, Hibernate还是需要CGLIB. 你不能在hibernate.cfg.xml中设置此属性. 取值 true | false -->
            <entry key="hibernate.bytecode.use_reflection_optimizer" value="true"/>
            <!-- 能用来完全禁止使用二级缓存. 对那些在类的映射定义中指定<cache>的类，会默认开启二级缓存. 取值 true|false -->
            <entry key="hibernate.cache.use_second_level_cache" value="false"/>
            <!-- 允许查询缓存, 个别查询仍然需要被设置为可缓存的. 取值 true|false -->
            <entry key="hibernate.cache.use_query_cache" value="false"/>
            <!-- the strategy genrerate table in db  -->
            <entry key="hibernate.hbm2ddl.auto" value="update"/>
            <!-- 输出所有SQL语句到控制台. 有一个另外的选择是把org.hibernate.SQL这个log category设为debug。 eg. true | false -->
            <entry key="hibernate.show_sql" value="true"/>
          </map>
      </property>
  </bean>

  <!-- 4. 配置 JPA 使用的事务管理器 -->
  <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
      <property name="entityManagerFactory" ref="entityManagerFactory"></property>
  </bean>
  <tx:annotation-driven transaction-manager="transactionManager"/>

  <!-- 5. 事务 -->
  <tx:advice id="txAdvice">
      <tx:attributes>
          <tx:method name="*"/>
          <tx:method name="get*" read-only="true"/>
          <tx:method name="find*" read-only="true"/>
          <tx:method name="select*" read-only="true"/>
          <tx:method name="delete*"/>
          <tx:method name="update*"/>
          <tx:method name="add*"/>
          <tx:method name="insert*"/>
      </tx:attributes>
  </tx:advice>

  <!-- 5.1 配置事务切入点, 以及把事务切入点和事务属性关联起来 -->
  <aop:config>
      <aop:pointcut id="pointcut" expression="execution(* com.augmentum.springdata.service.*.*(..))"/>
      <!--&lt;!&ndash;<aop:advisor pointcut-ref="pointcut" advice-ref="txAdvice"/>&ndash;&gt;-->
  </aop:config>

  <!-- 6. 配置 SpringData -->
  <!-- 加入 jpa 的命名空间; base-package: 扫描 Repository Bean 所在的 package -->
  <jpa:repositories base-package="com.augmentum.springdata" repository-impl-postfix="Impl"
                    transaction-manager-ref="transactionManager" entity-manager-factory-ref="entityManagerFactory"/>

  <!-- 7. AOP 配置自动为匹配 aspectJ 注解的 Java 类生成代理对象-->
  <aop:aspectj-autoproxy proxy-target-class="false"/>
  ```

- 3. 创建 entity 和 repository, 并按规定声明接口即可

- 4. 注意
  ```java
  @PersistenceContext // 获取到和当前事务关联的 EntityManager 对象
  private EntityManager entityManager;
  ```

### Repository 具体

- repository 关系继承图
  ![avatar](/static/image/jpa/RepositoryImpl.png)

  - Repository: 仅仅是一个标识, 表明任何继承它的均为仓库接口类[被 IOC 管理]

    ```java
    // 方式一
    @Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2")
    List<Person> testQueryAnnotationParams1(String lastName, String email);

    // 方式二
    @Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
    List<Person> testQueryAnnotationParams2(@Param("email") String email, @Param("lastName") String lastName);

    // 方式三: 设置 nativeQuery=true 即可以使用原生的 SQL 查询
    @Query(value="SELECT count(id) FROM person", nativeQuery=true)
    long getTotalCount();

    // like
    @Query("SELECT p FROM Person p WHERE p.lastName LIKE %?1% OR p.email LIKE %?2%")
    List<Person> testQueryAnnotationLikeParam(String lastName, String email);

    //可以通过自定义的 JPQL 完成 UPDATE 和 DELETE 操作. 注意: JPQL 不支持使用 INSERT
    //在 @Query 注解中编写 JPQL 语句, 但必须使用 @Modifying 进行修饰. 以通知 SpringData, 这是一个 UPDATE 或 DELETE 操作
    //UPDATE 或 DELETE 操作需要使用事务, 此时需要定义 Service 层. 在 Service 层的方法上添加事务操作.
    //默认情况下, SpringData 的每个方法上有事务, 但都是一个只读事务. 他们不能完成修改操作!
    @Modifying
    @Query("UPDATE Person p SET p.email = :email WHERE id = :id")
    void updatePersonEmail(@Param("id") Integer id, @Param("email") String email);
    ```

  - CrudRepository: 继承 Repository, 实现了一组 CRUD 相关的方法

    ```java
    // CrudRepository 接口提供了最基本的对实体类的添删改查操作 
    //保存单个实体
    T save(T entity); 
    //保存集合    
    Iterable<T> save(Iterable<? extends T> entities);   
    //根据id查找实体
    T findOne(ID id);        
    //根据id判断实体是否存在
    boolean exists(ID id);        
    //查询所有实体,不用或慎用!
    Iterable<T> findAll();        
    //查询实体数量   
    long count();     
    //根据Id删除实体    
    void delete(ID id);    
    //删除一个实体 
    void delete(T entity);
    //删除一个实体的集合
    void delete(Iterable<? extends T> entities);
    //删除所有实体,不用或慎用!         
    void deleteAll();
    ```

  - PagingAndSortingRepository: 继承 CrudRepository, 实现了一组分页排序相关的方法

    ```java
    // 该接口提供了分页与排序功能
    //排序
    Iterable<T> findAll(Sort sort);
    //分页查询(含排序功能)
    Page<T> findAll(Pageable pageable);
    ```

  - JpaRepository: 继承 PagingAndSortingRepository, 实现一组 JPA 规范相关的方法

    ```java
    // 该接口提供了JPA的相关功能
    //查找所有实体
    List<T> findAll();
    //排序、查找所有实体
    List<T> findAll(Sort sort);
    //保存集合
    List<T> save(Iterable<? extends T> entities);
    //执行缓存与数据库同步
    void flush();
    //强制执行持久化
    T saveAndFlush(T entity);
    //删除一个实体集合
    void deleteInBatch(Iterable<T> entities);
    ```

  - 自定义的 XxxxRepository 需要继承 JpaRepository, 这样的 XxxxRepository 接口就具备了通用的数据访问控制层的能力
    - 1. 为某一个 Repository 上添加自定义方法
    ```java
    // 1.1 定义一个接口: 声明要添加的, 并自实现的方法
    // 1.2 提供该接口的实现类: 类名需在要声明的 Repository 后添加 Impl, 并实现方法
    // 1.3 声明 Repository 接口, 并继承 1) 声明的接口
    // 1.4 使用.
    // 1.5 注意: 默认情况下, Spring Data 会在 base-package 中查找 "接口名Impl" 作为实现类. 也可以通过　repository-impl-postfix　声明后缀.
    ```
    ![avatar](/static/image/jpa/defineRepository.png)
    - 2.  为所有的 Repository 都添加自实现的方法
    ```java
    // 2.1 声明一个接口, 在该接口中声明需要自定义的方法, 且该接口需要继承 Spring Data 的 Repository.
    // 2.2 提供 1 所声明的接口的实现类. 且继承 SimpleJpaRepository, 并提供方法的实现
    // 2.3 定义 JpaRepositoryFactoryBean 的实现类, 使其生成 1) 定义的接口实现类的对象
    // 2.4 修改 <jpa:repositories /> 节点的 factory-class 属性指向 3) 的全类名
    // 2.5 注意: 全局的扩展实现类不要用 Imp 作为后缀名, 或为全局扩展接口添加 @NoRepositoryBean 注解告知  Spring Data: Spring Data 不把其作为 Repository
    ```

- JpaSpecificationExecutor: 不属于 Repository 体系, 实现一组 JPA Criteria 查询相关的方法

  ```java
  // 1. 不属于Repository体系, 实现一组 JPA Criteria 查询相关的方法 
  // 2. Specification: 封装 JPA Criteria 查询条件. 通常使用匿名内部类的方式来创建该接口的对象
  ```

- notice

  - 1. 可以明确在属性之间加上 "\_" 以显式表达意图, 如 "findByUser_DepUuid()" 或者 "findByUserDep_uuid()", 表示使用内部类属性的字段
  - 2. @Query 自定义 SQL 查询
  - 3. JpaRepository saveAll(Iterator<>) does each insert separately; 假批量, 可以使用 entityManager 或 JdbcTemplate 进行批量操作 或者配置 `ApllicationContext.xml` 文件的 `entityManagerFactory` -- `jpaPropertyMap` -- `hibernate.jdbc.batch_size` -- `hibernate.order_inserts`

- Supported keywords inside method names

  |      Keyword      |                         Sample                          |                          JPQL snippet                          |
  | :---------------: | :-----------------------------------------------------: | :------------------------------------------------------------: |
  |        And        |               findByLastnameAndFirstname                |          … where x.lastname = ?1 and x.firstname = ?2          |
  |        Or         |                findByLastnameOrFirstname                |          … where x.lastname = ?1 or x.firstname = ?2           |
  |     Is,Equals     | findByFirstname,findByFirstnameIs,findByFirstnameEquals |                    … where x.firstname = ?1                    |
  |      Between      |                 findByStartDateBetween                  |             … where x.startDate between ?1 and ?2              |
  |     LessThan      |                    findByAgeLessThan                    |                       … where x.age < ?1                       |
  |   LessThanEqual   |                 findByAgeLessThanEqual                  |                      … where x.age <= ?1                       |
  |    GreaterThan    |                  findByAgeGreaterThan                   |                       … where x.age > ?1                       |
  | GreaterThanEqual  |                findByAgeGreaterThanEqual                |                      … where x.age >= ?1                       |
  |       After       |                  findByStartDateAfter                   |                    … where x.startDate > ?1                    |
  |      Before       |                  findByStartDateBefore                  |                    … where x.startDate < ?1                    |
  |      IsNull       |                     findByAgeIsNull                     |                     … where x.age is null                      |
  | IsNotNull,NotNull |                  findByAge(Is)NotNull                   |                     … where x.age not null                     |
  |       Like        |                   findByFirstnameLike                   |                  … where x.firstname like ?1                   |
  |      NotLike      |                 findByFirstnameNotLike                  |                … where x.firstname not like ?1                 |
  |   StartingWith    |               findByFirstnameStartingWith               | … where x.firstname like ?1 (parameter bound with appended %)  |
  |    EndingWith     |                findByFirstnameEndingWith                | … where x.firstname like ?1 (parameter bound with prepended %) |
  |    Containing     |                findByFirstnameContaining                |   … where x.firstname like ?1 (parameter bound wrapped in %)   |
  |      OrderBy      |              findByAgeOrderByLastnameDesc               |          … where x.age = ?1 order by x.lastname desc           |
  |        Not        |                    findByLastnameNot                    |                    … where x.lastname <> ?1                    |
  |        In         |            findByAgeIn(Collection<Age> ages)            |                      … where x.age in ?1                       |
  |       NotIn       |          findByAgeNotIn(Collection<Age> ages)           |                    … where x.age not in ?1                     |
  |       True        |                   findByActiveTrue()                    |                    … where x.active = true                     |
  |       False       |                   findByActiveFalse()                   |                    … where x.active = false                    |
  |    IgnoreCase     |                findByFirstnameIgnoreCase                |             … where UPPER(x.firstame) = UPPER(?1)              |

- Query keywords

  |           Keyword expressions            |   Logical keyword   |
  | :--------------------------------------: | :-----------------: |
  |                   And                    |         AND         |
  |                    Or                    |         OR          |
  |              After, IsAfter              |        AFTER        |
  |             Before, IsBefore             |       BEFORE        |
  |    Containing, IsContaining, Contains    |     CONTAINING      |
  |            Between, IsBetween            |       BETWEEN       |
  |    EndingWith, IsEndingWith, EndsWith    |     ENDING_WITH     |
  |                  Exists                  |       EXISTS        |
  |              False, IsFalse              |        FALSE        |
  |        GreaterThan, IsGreaterThan        |    GREATER_THAN     |
  |   GreaterThanEqual, IsGreaterThanEqual   | GREATER_THAN_EQUALS |
  |                 In, IsIn                 |         IN          |
  |       Is, Equals, (or no keyword)        |         IS          |
  |              IsEmpty, Empty              |      IS_EMPTY       |
  |           IsNotEmpty, NotEmpty           |    IS_NOT_EMPTY     |
  |            NotNull, IsNotNull            |     IS_NOT_NULL     |
  |             Null, IsNullAnd              |       IS_NULL       |
  |           LessThan, IsLessThan           |      LESS_THAN      |
  |      LessThanEqual, IsLessThanEqual      |   LESS_THAN_EQUAL   |
  |               Like, IsLike               |        LIKE         |
  |               Near, IsNear               |        NEAR         |
  |                Not, IsNot                |         NOT         |
  |              NotIn, IsNotIn              |       NOT_IN        |
  |            NotLike, IsNotLike            |      NOT_LIKE       |
  |       Regex, MatchesRegex, Matches       |        REGEX        |
  | StartingWith, IsStartingWith, StartsWith |    STARTING_WITH    |
  |               True, IsTrue               |        TRUE         |
  |             Within, IsWithin             |       WITHIN        |

- Query return types

  | Return type                                                                                                                                                                                                                                             | Description                                                                                                                                                                                                                                                       |
  | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
  | void                                                                                                                                                                                                                                                    | Denotes no return value.                                                                                                                                                                                                                                          |
  | Primitives                                                                                                                                                                                                                                              | Java primitives.                                                                                                                                                                                                                                                  |
  | Wrapper types                                                                                                                                                                                                                                           | Java wrapper types.                                                                                                                                                                                                                                               |
  | T                                                                                                                                                                                                                                                       | An unique entity. Expects the query method to return one result at most. If no result is found, null is returned. More than one result triggers an IncorrectResultSizeDataAccessException.                                                                        |
  | Iterator<T>                                                                                                                                                                                                                                             | An Iterator.                                                                                                                                                                                                                                                      |
  | Collection<T>                                                                                                                                                                                                                                           | A Collection.                                                                                                                                                                                                                                                     |
  | List<T>                                                                                                                                                                                                                                                 | A List.                                                                                                                                                                                                                                                           |
  | Optional<T>                                                                                                                                                                                                                                             | A Java 8 or Guava Optional. Expects the query method to return one result at most. If no result is found, Optional.empty() or Optional.absent() is returned. More than one result triggers an IncorrectResultSizeDataAccessException.                             |
  | Option<T>                                                                                                                                                                                                                                               | Either a Scala or Javaslang Option type. Semantically the same behavior as Java 8’s Optional, described earlier.                                                                                                                                                  |
  | Stream<T>                                                                                                                                                                                                                                               | A Java 8 Stream.                                                                                                                                                                                                                                                  |
  | Future<T>                                                                                                                                                                                                                                               | A Future. Expects a method to be annotated with @Async and requires Spring’s asynchronous method execution capability to be enabled.                                                                                                                              |
  | CompletableFuture<T>                                                                                                                                                                                                                                    | A Java 8 CompletableFuture. Expects a method to be annotated with @Async and requires Spring’s asynchronous method execution capability to be enabled.                                                                                                            |
  | ListenableFuture                                                                                                                                                                                                                                        | A org.springframework.util.concurrent.ListenableFuture. Expects a method to be annotated with @Async and requires Spring’s asynchronous method execution capability to be enabled.                                                                                |
  | Slice                                                                                                                                                                                                                                                   |
  | A sized chunk of data with an indication of whether there is more data available. Requires a Pageable method parameter.                                                                                                                                 |
  | Page<T>                                                                                                                                                                                                                                                 | A Slice with additional information, such as the total number of results. Requires a Pageable method parameter.                                                                                                                                                   |
  | GeoResult<T>                                                                                                                                                                                                                                            |
  | A result entry with additional information, such as the distance to a reference location.                                                                                                                                                               |
  | GeoResults<T>                                                                                                                                                                                                                                           | A list of GeoResult<T> with additional information, such as the average distance to a reference location.                                                                                                                                                         |
  | GeoPage<T>                                                                                                                                                                                                                                              |
  | A Page with GeoResult<T>, such as the average distance to a reference location.                                                                                                                                                                         |
  | Mono<T>                                                                                                                                                                                                                                                 | A Project Reactor Mono emitting zero or one element using reactive repositories. Expects the query method to return one result at most. If no result is found, Mono.empty() is returned. More than one result triggers an IncorrectResultSizeDataAccessException. |
  | Flux<T>                                                                                                                                                                                                                                                 | A Project Reactor Flux emitting zero, one, or many elements using reactive repositories. Queries returning Flux can emit also an infinite number of elements.                                                                                                     |
  | Single<T>                                                                                                                                                                                                                                               |
  | A RxJava Single emitting a single element using reactive repositories. Expects the query method to return one result at most. If no result is found, Mono.empty() is returned. More than one result triggers an IncorrectResultSizeDataAccessException. |
  | Maybe<T>                                                                                                                                                                                                                                                | A RxJava Maybe emitting zero or one element using reactive repositories. Expects the query method to return one result at most. If no result is found, Mono.empty() is returned. More than one result triggers an IncorrectResultSizeDataAccessException.         |
  | Flowable<T>                                                                                                                                                                                                                                             | A RxJava Flowable emitting zero, one, or many elements using reactive repositories. Queries returning Flowable can emit also an infinite number of elements.                                                                                                      |
