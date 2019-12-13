## SpringBoot

---

## spring boot code

1. pom.xml
2. applicationContext.xml
3. config bean
4. code logic

---

## 配置类

```java
// 1. 配置类, 相当于 spring 的配置文件[注入属性之类的]
@Configuration + @Bean
// 2. spring-boot 默认不支持 XML 文件向IOC容器中注入, 可以使用其开启。
@ImportResource(locations = { "classpath:testService.xml" })
```

## property inspect

```java
// method 1:
// 从配置文件中读取数据并且装配到这个 bean 对象
// 将上面的配置文件数据单独成一个文件, 并引入, 必须和 @ConfigurationProperties() 一起使用
@PropertySource(value = { "classpath:person.properties" })
@ConfigurationProperties(prefix = "person")
public class Person {
 ...
}

// method 2:
@Configuration
public class DruidConfig{

  @ConfigurationProperties(prefix="spring.datasource")
  @Bean
  public DataSource DruidConfig(){
    ...
  }
  ...
}

// method 3:
public calss Person {
   // 从配置文件中读取属性值
   // @Value("#{2*3}")、@Value("${person.last-name}")
   @Value("${person.last-name}")
   private Integer age;
   ....
   // 静态类中注入 xx.properties 文件中的值时要提供 set 方法.
   @Value("${user.api.host}")
   public void setUserApiHost(String host) {
     USER_API_HOST = host;
   }
}

```

## springBoot 启动时让方法自动执行的几种实现方式

1. 实现 ServletContextAware 接口并重写其 setServletContext 方法

   ```java
   // 注意: 该方法会在填充完普通 Bean 的属性, 但是还没有进行 Bean 的初始化之前执行　
   @Component
   public class TestStarted implements ServletContextAware {
       /**
       * 在填充普通bean属性之后但在初始化之前调用
       * 类似于initializingbean的afterpropertiesset或自定义init方法的回调
       *
       */
       @Override
       public void setServletContext(ServletContext servletContext) {
           System.out.println("setServletContext方法");
       }
   }
   ```

2. 实现 ServletContextListener 接口

   ```java
   /**
    * 在初始化Web应用程序中的任何过滤器或servlet之前, 将通知所有servletContextListener上下文初始化.
    */
   @Override
   public void contextInitialized(ServletContextEvent sce) {
       //ServletContext servletContext = sce.getServletContext();
       System.out.println("执行contextInitialized方法");
   }
   ```

3. 将要执行的方法所在的类交个 spring 容器扫描(@Component),并且在要执行的方法上添加 @PostConstruct 注解或者静态代码块执行

   ```java
   @Component
   public class Test2 {
       //静态代码块会在依赖注入后自动执行,并优先执行
       static{
           System.out.println("---static--");
       }
       /**
       *  @Postcontruct’在依赖注入完成后自动调用
       */
       @PostConstruct
       public static void haha(){
           System.out.println("@Postcontruct’在依赖注入完成后自动调用");
       }
   }
   ```

4. 实现 ApplicationRunner 接口

   ```java
   /**
    * 用于指示bean包含在SpringApplication中时应运行的接口. 可以定义多个applicationrunner bean
    * 在同一应用程序上下文中, 可以使用有序接口或@order注释对其进行排序.
    */
   @Override
   public void run(ApplicationArguments args) throws Exception {
       System.out.println("ApplicationRunner的run方法");
   }
   ```

5. 实现 CommandLineRunner 接口
   ```java
   /**
    * 用于指示bean包含在SpringApplication中时应运行的接口. 可以在同一应用程序上下文中定义多个commandlinerunner bean, 并且可以使用有序接口或@order注释对其进行排序.
    * 如果需要访问applicationArguments而不是原始字符串数组, 请考虑使用applicationrunner.
    */
   @Override
   public void run(String... ) throws Exception {
       System.out.println("CommandLineRunner的run方法");
   }
   ```

## SpringBoot 中使用 @Value() 只能给普通变量注入值, 不能直接给静态变量赋值

- 给普通变量赋值时，直接在变量声明之上添加@Value()注解即可
  ```java
  @Value("${queue.direct}")
  private String queueName;
  ```
- 当要给静态变量注入值的时候，若是在静态变量声明之上直接添加@Value()注解是无效的

  - 需要在类上加入@Component 注解
  - 书写 setXX 方法

  ```java
  private String queueName;

  @Value("${queue.direct}")
  public void setQueueName(String queueName)
  {
      this.queueName = queueName;
  }
  ```

## Spring-Boot 2.0 静态资源处理:

- **spring-boot 2.0 静态资源处理: 这里会使用 MyMVCConfig, 也使用 springboot 帮忙配置的 WebMvcConfigurer [可以是多个]addInterceptors() 添加拦截器, 放过静态资源.**

  ```java
  @Configuration
  public class MyMVCConfig implements WebMvcConfigurer {
  	@Bean
  	public WebMvcConfigurer webMvcConfigurer() {

        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {

                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                // 阻止表单重复提交
                registry.addViewController("/main.html").setViewName("dashboard");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/","/index.html","user/login","/static/**","/webjars/**");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/","classpath:/public/","classpath:/resource/");
                registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };

        return webMvcConfigurer;
    }
  }
  ```

## @component

- 1. @component 的使用问题:

  ```java
  // 这个可以直接使用 @Component 加入到 springboot 容器中
  @Bean
  @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)

  // 这个就不可以直接使用 @Component 加入到 springboot 容器中；要使用 @Configuration 类的 @bean 方法加入
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
  ```

## spring boot 的 error 机制

- **处理的相关类:**

```java
errorpagecustomeizer
basicerrorcontroller[处理/error请求]
deafulterrorviewresolver
defaulterrorattributes
```

- **处理流程:**

```java
occur exception -->  spring的 @ControllerAdvice + @ExceptionHandler 对异常处理(转发/error)
---> BasicErrorController[JSON/html] ---> getErrorAttributes()
---> deafulterrorviewresolver 得到 view ---> MyErrorAttibutes[自定义的]
---> DefaultErrorAttributes ---> getErrorAttributes() -----> BasicErrorController[JSON/html]
```

- **流程是上面的样子, 代码没有什么意思: 因为在 thymeleaf 中可以直接取 request 的数据[Spring ExceptionHandler 放入的数据].**

## spring boot test class

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ActivemqSenderApplication.class)
@WebAppConfiguration
```
