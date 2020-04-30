## spring annotation

### qiuck start

1. pom

   ```xml
   <!-- it contain beans, core, aop,expression jar -->
   <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>${spring.version}</version>
   </dependency>
   ```

2. @Configuration is equivalent to bean.xml

   - xml config

   ```xml
   <bean id="person" class="cn.edu.ntu.javaee.annotation.model.Person">
       <property name="age" value="18"/>
       <property name="name" value="zack"/>
   </bean>
   ```

   ```java
   @Test
    public void testIocContainer() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        Person person = (Person) applicationContext.getBean("person");
        log.info(String.valueOf(person));
    }

    @Test
    public void testIocObjects() {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        String[] definitionNames = applicationContext.getBeanDefinitionNames();

        Arrays.stream(definitionNames).forEach(System.out::println);
    }
   ```

   - annotation config

   ```java
   /*  method name is bean name in IOC container, but if specify value, which will be bean name.</br> */
   @Bean(value = "person")
   public Person injectPerson() {
     return new Person(19, "alice52");
   }
   ```

   ```java
   @Test
   public void testIocContainer() {
       ApplicationContext applicationContext =
           new AnnotationConfigApplicationContext(HelloConfig.class);
       Person person = applicationContext.getBean(Person.class);
       log.info(String.valueOf(person));
   }

   @Test
   public void testIocObjects() {

       ApplicationContext applicationContext =
               new AnnotationConfigApplicationContext(HelloConfig.class);
       String[] definitionNames = applicationContext.getBeanDefinitionNames();

       Arrays.stream(definitionNames).forEach(System.out::println);
   }
   ```

3. create singleton object and put it to IOC container

   - need can be scanned: **`<context:component-scan>`** or `@ComponentScan`

     ```xml
     <context:component-scan base-package="cn.edu.ntu.javaee.annotation" >
         <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
     </context:component-scan>
     <!-- config use-default-filters="false" to use include-filter -->
     <!--
         <context:component-scan base-package="cn.edu.ntu.javaee.annotation" use-default-filters="false">
             <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
         </context:component-scan>
     -->
     ```

     ```java
     /* <code>@ComponentScan</code> includeFilters usage is: useDefaultFilters = false. <br> */
     @ComponentScan(
         basePackages = {"cn.edu.ntu.javaee.annotation"},
         excludeFilters = {
            @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {Controller.class})
        })
     ```

   - tell IOC to control it: `beans.xml` or `@Component`

---

## conclusion

### put object to IOC container

1. @Component/@Controller + @ComponentScan

   - 如果 @ComponentScan 使用自定义的方式或者指定包含某些类时[ASSIGNABLE_TYPE, REGEX, CUSTOM], 没有 `@Component/@Controller` 也可以成功注入 IOC
   - ANNOTATION 必须使用 `@Component/@Controller` 才能成功注入 IOC

2. @Bean 将第三方包中的组件归置到 IOC 管理

3. @Import

   - @Import
   - ImportSelector
   - ImportBeanDefinitionRegistrar
   - Spring FactoryBean

---

### bean lifecycle

- **bean create -- bean init[`对象创建完成且赋值好之后`] -- use -- destroy**

  - singleton:
    - init: 容器创建时创建对象, 并调用 Init() 方法
    - destroy: 容器关闭时调用
  - prototype:
    - init: 第一次使用时才会创建对象, 并调用 Init() 方法
    - destroy: 容器只会创建这个 Bean 但是不会销毁[管理], 如果需要则自己手动销毁

1. BeanPostProcessor
2. **`@PostConstruct && @PreDestroy`**
3. **InitializingBean && DisposableBean**
4. `@Bean(value = "person", initMethod = "init", destroyMethod = "destroy")`

### get IOC container

1. configuration

   ```java
   @Configuration
   public class IocContainer implements ApplicationContextAware {
       private ApplicationContext applicationContext;

       public ApplicationContext getApplicationContext() {
           return this.applicationContext;
       }

       @Override
       public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
           this.applicationContext = applicationContext;
       }

       @Bean
       public Person person() {
           return new Person();
       }
   }
   ```

2. junit test

   ```java
   @Slf4j
   public class IocContainerTest {
       private ApplicationContext applicationContext = new AnnotationConfigApplicationContext(IocContainer.class);

       @Test
       public void testGetIocContainer() {
           IocContainer contextBean = applicationContext.getBean(IocContainer.class);
           ApplicationContext applicationContext = contextBean.getApplicationContext();

           Person person0 = applicationContext.getBean(Person.class);
           log.info(String.valueOf(person0));

           Person person = applicationContext.getBean(Person.class);
           log.info(String.valueOf(person));

           Assert.isTrue(person0 == person);
           Assert.isFalse(applicationContext == contextBean);
       }
   }
   ```

---

### common annotation

1. @Component

2. @Service

3. @Repository

4. @Controller

5. @Autowired

   - byType

6. @Resource

   - byName

7. @Inject
