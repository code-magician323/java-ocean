## lifecycle

- **bean create -- bean init[`对象创建完成且赋值好之后`] -- use -- destroy**

  - singleton:
    - init: 容器创建时创建对象, 并调用 Init() 方法
    - destroy: 容器关闭时调用
  - prototype:
    - init: 第一次使用时才会创建对象, 并调用 Init() 方法
    - destroy: 容器只会创建这个 Bean 但是不会销毁[管理], 如果需要则自己手动销毁

### @Bean

1. specify init and destroy method

   - xml comfig

   ```xml
   <bean id="person" class="cn.edu.ntu.javaee.annotation.common.model.Person" init-method="" destroy-method="">
       <property name="age" value="18"/>
       <property name="name" value="zack"/>
   </bean>
   ```

   - annotation

   ```java
   @Bean(value = "person", initMethod = "init", destroyMethod = "destroy")
   public Person injectPerson() {
       return new Person();
   }
   ```

   - test

   ```java
   Person person = applicationContext.getBean(Person.class);
   log.info(String.valueOf(person));

   ClassPathXmlApplicationContext context = (ClassPathXmlApplicationContext) this.applicationContext;
   context.close();

   AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) this.applicationContext;
   context.close();
   ```

### InitializingBean#afterPropertiesSet && DisposableBean#destroy

1. Dog model

   ```java
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   @Slf4j
   public class Dog implements InitializingBean, DisposableBean {
       private Integer age;
       private String name;
       private String color;

       @Override
       public void afterPropertiesSet() throws Exception {
           log.info("1. Person object afterPropertiesSet method execute.");
       }

       public void init() {
           log.info("2. Person object init method execute.");
       }

       @Override
       public void destroy() throws Exception {
           log.info("3. Person object destroy[DisposableBean] method execute.");
       }

       public void destroy0() {
           log.info("4. Person object destroy0 method execute.");
       }
   }
   ```

2. usage
   ```java
   @Bean(value = "dog", initMethod = "init", destroyMethod = "destroy0")
   public Dog injectPerson() { return new Dog();  }
   ```

### @PostConstruct[在 bean 创建完成并赋值之后执行] && @PreDestroy[bean 被从容器移除之前]

1. Dog model

   ```java
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   @Slf4j
   public class Dog implements InitializingBean, DisposableBean {
       private Integer age;
       private String name;
       private String color;

       @PostConstruct
       public void init1() {
           log.info("1. Person object init1 method execute.");
       }

       @Override
       public void afterPropertiesSet() throws Exception {
           log.info("2. Person object afterPropertiesSet method execute.");
       }

       public void init() {
           log.info("3. Person object init method execute.");
       }

       @PreDestroy
       public void destroy1() {
           log.info("4. Person object destroy1 method execute.");
       }

       @Override
       public void destroy() throws Exception {
           log.info("5. Person object destroy[DisposableBean] method execute.");
       }

       public void destroy0() {
           log.info("6. Person object destroy0 method execute.");
       }
   }
   ```

2. usage
   ```java
   @Bean(value = "dog", initMethod = "init", destroyMethod = "destroy0")
   public Dog injectPerson() { return new Dog();  }
   ```

### BeanPostProcessor: postProcessBeforeInitialization && postProcessAfterInitialization

- processor flow

```java
BeanPostProcessor

populateBean(beanName, mbd, instanceWrapper); // set value of bean
initializeBean
{
   applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
   invokeInitMethods(beanName, wrappedBean, mbd); // execute custom init
   applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}
```

1. Dog model

   ```java
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   @Slf4j
   public class Dog implements InitializingBean, DisposableBean {
       private Integer age;
       private String name;
       private String color;

       @PostConstruct
       public void init1() {
           log.info("2. Person object init1 method execute.");
       }

       @Override
       public void afterPropertiesSet() throws Exception {
           log.info("3. Person object afterPropertiesSet method execute.");
       }

       public void init() {
           log.info("4. Person object init method execute.");
       }

       @PreDestroy
       public void destroy1() {
           log.info("6. Person object destroy1 method execute.");
       }

       @Override
       public void destroy() throws Exception {
           log.info("7. Person object destroy[DisposableBean] method execute.");
       }

       public void destroy0() {
           log.info("8. Person object destroy0 method execute.");
       }
   }
   ```

2. CustomBeanPostProcessor

   ```java
   @Slf4j
   public class CustomBeanPostProcessor implements BeanPostProcessor {

       @Override
       public Object postProcessBeforeInitialization(Object bean, String beanName)
           throws BeansException {
           log.info("1. postProcessBeforeInitialization, bean: {}, beanName: {}", bean, beanName);
           return bean;
       }

       @Override
       public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
           log.info("5. postProcessAfterInitialization, bean: {}, beanName: {}", bean, beanName);
           return bean;
       }
   }
   ```

3. usage

   ```java
   @Configuration
   @ComponentScan(
       basePackages = "cn.edu.ntu.javaee.annotation",
       includeFilters = {
       @ComponentScan.Filter(
           type = FilterType.ASSIGNABLE_TYPE,
           classes = {CustomBeanPostProcessor.class})
       })
   public class BeanInitAndDestroyInter {

   @Bean(value = "dog", initMethod = "init", destroyMethod = "destroy0")
       public Dog injectPerson() {
           return new Dog();
       }
   }
   ```

4. junit test

   ```java
   @Test
   public void testBeanInitAndDestroy() {
       Dog dog = applicationContext.getBean(Dog.class);
       log.info(String.valueOf(dog));

       AnnotationConfigApplicationContext context =
           (AnnotationConfigApplicationContext) this.applicationContext;
       context.close();
   }
   ```
