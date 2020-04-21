## JDBC

### Quick Start

1. dependency

   ```xml
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
   </dependency>
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-jdbc</artifactId>
   </dependency>
   ```

2. config DataSource: `DataSourceProperties`

   ```yaml
   spring:
     datasource:
       username: root
       password: Yu1252068782?
       url: jdbc:mysql://101.132.45.28:3306/tutorials
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```

3. test connection

   ```java
   // default datasource: HikariDataSource
   @Autowired DataSource dataSource;
   @Autowired JdbcTemplate jdbcTemplate;

   @Test
   public void testDataSource() throws SQLException {
       Connection connection = dataSource.getConnection();
       LOG.info("DataSource: {}, and Connection: {}", dataSource, connection);
       LOG.info("JdbcTemplate: {}", jdbcTemplate);
   }
   ```

4. AutoConfiguration: **`DataSourceAutoConfiguration` + `DataSourceConfiguration` + `DataSourceProperties`**

   ```java
   // DataSourceConfiguration
   org.apache.tomcat.jdbc.pool.DataSource.class
   HikariDataSource.class
   org.apache.commons.dbcp2.BasicDataSource.class

   // customize DataSource
   @Configuration
   @ConditionalOnMissingBean(DataSource.class)
   @ConditionalOnProperty(name = "spring.datasource.type")
   static class Generic {
       @Bean
       public DataSource dataSource(DataSourceProperties properties) {
           // initializeDataSourceBuilder() in DataSourceProperties
           // DataSource result = (DataSource)BeanUtils.instantiateClass(type); // build()
           return properties.initializeDataSourceBuilder().build();
       }
   }
   ```

### Driud

1. dependency

   ```xml
   <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>druid</artifactId>
       <version>1.1.21</version>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-jdbc</artifactId>
   </dependency>
   ```

2. config DataSource: `DataSourceProperties`

   ```yaml
   spring:
     datasource:
       username: root
       password: Yu125***782?
       url: jdbc:mysql://101.132.45.28:3306/tutorials
       driver-class-name: com.mysql.cj.jdbc.Driver

       type: com.alibaba.druid.pool.DruidDataSource
       initialSize: 5
       minIdle: 5
       maxActive: 20
       maxWait: 60000
       timeBetweenEvictionRunsMillis: 60000
       minEvictableIdleTimeMillis: 300000
       validationQuery: SELECT 1 FROM DUAL
       testWhileIdle: true
       testOnBorrow: false
       testOnReturn: false
       poolPreparedStatements: true
       # Configure monitoring statistics interception filters.
       # If removed, the monitoring interface sql cannot collect statistics.
       # Wall is used for the firewall
       # slf4j is logger
       filters: stat,wall, slf4j
       maxPoolPreparedStatementPerConnectionSize: 20
       useGlobalDataSourceStat: true
       connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
   ```

3. add DataSource to IOC container

   ```java
   @Configuration
   public class DruidConfiguration {
       private static final Logger LOG = LoggerFactory.getLogger(DruidConfiguration.class);
       @Bean
       @ConfigurationProperties(prefix = "spring.datasource")
       public DruidDataSource configDruid() {
           return new DruidDataSource();
       }

       // datasource management
       @Bean
       public ServletRegistrationBean<StatViewServlet> configStatViewServlet() {
           ServletRegistrationBean<StatViewServlet> statServletBean =
               new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
           Map<String, String> initParams = new HashMap<>();
           initParams.put(ResourceServlet.PARAM_NAME_USERNAME, "admin");
           initParams.put(ResourceServlet.PARAM_NAME_PASSWORD, "admin");
           initParams.put(ResourceServlet.PARAM_NAME_ALLOW, "");
           initParams.put(ResourceServlet.PARAM_NAME_DENY, "192.168.43.143");
           statServletBean.setInitParameters(initParams);

           return statServletBean;
       }

       @Bean
       public FilterRegistrationBean<WebStatFilter> configWebStatFilter() {
           FilterRegistrationBean<WebStatFilter> webFilterBean = new FilterRegistrationBean<>();
           webFilterBean.setFilter(new WebStatFilter());
           Map<String, String> initParams = new HashMap<>();
           initParams.put(WebStatFilter.PARAM_NAME_EXCLUSIONS, "*.js,*.css,/druid/*");
           webFilterBean.setInitParameters(initParams);
           webFilterBean.setUrlPatterns(Arrays.asList("/*"));

           return webFilterBean;
       }
   }
   ```

4. test connection

   ```java
   // default datasource: HikariDataSource
   @Autowired DataSource dataSource;
   @Autowired JdbcTemplate jdbcTemplate;

   @Test
   public void testDataSource() throws SQLException {
       Connection connection = dataSource.getConnection();
       LOG.info("DataSource: {}, and Connection: {}", dataSource, connection);
       LOG.info("JdbcTemplate: {}", jdbcTemplate);
   }
   ```

5. url: http://localhost:8080/druid

   ```java
   // DataSourceConfiguration
   org.apache.tomcat.jdbc.pool.DataSource.class
   HikariDataSource.class
   org.apache.commons.dbcp2.BasicDataSource.class

   // customize DataSource
   @Configuration
   @ConditionalOnMissingBean(DataSource.class)
   @ConditionalOnProperty(name = "spring.datasource.type")
   static class Generic {
       @Bean
       public DataSource dataSource(DataSourceProperties properties) {
           // initializeDataSourceBuilder() in DataSourceProperties
           // DataSource result = (DataSource)BeanUtils.instantiateClass(type); // build()
           return properties.initializeDataSourceBuilder().build();
       }
   }
   ```
