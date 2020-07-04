## Profile

1. spring.profiles.active 默认是 default

2. 如果没有指定 profile 则无论在什么环境都会被加载

3. code

   ```java
   // @Profile("dev")
   @PropertySource("classpath:/db.properties")
   @Configuration
   public class ProfileConfig implements EmbeddedValueResolverAware {

       @Value("${db.user}")
       private String user;
       private StringValueResolver valueResolver;
       private String  driverClass;

       @Profile("test")
       @Bean("testDataSource")
       public DataSource dataSourceTest(@Value("${db.password}")String pwd) throws Exception{
           DruidDataSource dataSource = new DruidDataSource();
           dataSource.setUsername(user);
           dataSource.setPassword(pwd);
           dataSource.setUrl("jdbc:mysql://localhost:3306/test");
           dataSource.setDriverClassName(driverClass);
           return dataSource;
       }

       @Profile("dev")
       @Bean("devDataSource")
       public DataSource dataSourceDev(@Value("${db.password}")String pwd) throws Exception{
           DruidDataSource dataSource = new DruidDataSource();
           dataSource.setUsername(user);
           dataSource.setPassword(pwd);
           dataSource.setUrl("jdbc:mysql://localhost:3306/dev");
           dataSource.setDriverClassName(driverClass);
           return dataSource;
       }

       @Profile("prod")
       @Bean("prodDataSource")
       public DataSource dataSourceProd(@Value("${db.password}")String pwd) throws Exception{
           DruidDataSource dataSource = new DruidDataSource();
           dataSource.setUsername(user);
           dataSource.setPassword(pwd);
           dataSource.setUrl("jdbc:mysql://localhost:3306/prod");
           dataSource.setDriverClassName(driverClass);
           return dataSource;
       }

       @Override
       public void setEmbeddedValueResolver(StringValueResolver resolver) {
           this.valueResolver = resolver;
           driverClass = valueResolver.resolveStringValue("${db.driverClass}");
       }
   }
   ```

   - test

   ```java
   @Test
   public void test01() {
       // create null applicationContext
       AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
       // 2. 设置需要激活的环境
       applicationContext.getEnvironment().setActiveProfiles("dev");
       // 3. 注册主配置类
       applicationContext.register(ProfileConfig.class);
       // 4. 启动刷新容器
       applicationContext.refresh();

       String[] names = applicationContext.getBeanNamesForType(DataSource.class);
       Arrays.stream(names).forEach(System.out::println);

       applicationContext.close();
   }
   ```
