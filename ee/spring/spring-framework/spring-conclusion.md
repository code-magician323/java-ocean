## spring

### bean 创建: 只是创建, 需要通过`@Component` 或者 `<context:component-scan>` 才会将 bean 放入 `IOC`

1. 基于 class 构建

   ```xml
   <bean id="a" class="cn.edu.ntu.spring.ioc.A"/>
   ```

2. 构造方法构建

   - name: 构造方法参数变量名称
   - type: 参数类型
   - index: 参数索引
   - value: 参数值
   - ref: 引用其他 bean 对象

   ```xml
   <bean id="a" class="cn.edu.ntu.spring.ioc.A">
      <constructor-arg name="name" type="java.lang.String" value="zack"/>
      <constructor-arg index="1" type="java.lang.String" value="12"/>
   </bean>
   ```

3. 静态方法构建

   ```xml
   <!-- build is static method: return A object -->
   <bean class="cn.edu.ntu.spring.ioc.A" factory-method="build">
      <!-- this is arg for build method -->
      <constructor-arg index="1" type="java.lang.String" value="12"/>
   </bean>
   ```

4. FactoryBean 构建

   ```xml
   <!-- AFactoryBean implement FactoryBean -->
   <bean class="cn.edu.ntu.spring.ioc.AFactoryBean">
      <property name="name" value="zack">
   </bean>
   ```

### 依赖注入

1. set 方法注入

   ```xml
   <bean id="a" class="cn.edu.ntu.spring.ioc.A">
       <property name="b" ref="B">
   </bean>
   ```

2. 构造方法注入

   ```xml
   <bean id="a" class="cn.edu.ntu.spring.ioc.A">
       <constructor-arg name="b">
           <bean class="cn.edu.ntu.spring.ioc.B"/>
       </constructor-arg>
   </bean>
   ```

3. 自动注入: byName, byType
4. 方法注入: lookup-method`[一个单例bean依赖一个多实例bean]`

   - `该操作是基于动态代理技术, 重新生成一个继承至目标类, 然后重写抽象方法达到注入的目的`
   - `还可以实现 ApplicationContextAware, BeanFactoryAware 接口来获取 BeanFactory 实例, 直接调用 getBean 方法获取新实例`

   ```xml
   <bean class="cn.edu.ntu.spring.ioc.B">
      <lookup-method name="getHi"/>
   </bean>
   ```

   ```java
   public abstract class B {

      public void sayhello() {
         getHi().sayHi();
      }

      public abstract Hi getHi();
   }
   ```

### 获取 bean 对象

1. 普通的 IOC 容器中的 bean 对象

   - ApplicationContext

     ```java
     ApplicationContext ac = new FileSystemXmlApplicationContext("applicationContext.xml");
     ac.getBean("userService");

     <bean id="userService" class="com.cloud.service.impl.UserServiceImpl"></bean>
     ```

   - WebApplicationContext
     ```java
     ApplicationContext ac1 = WebApplicationContextUtils.getRequiredWebApplicationContext(ServletContext sc);
     ApplicationContext ac2 = WebApplicationContextUtils.getWebApplicationContext(ServletContext sc);
     ac1.getBean("beanId");
     ac2.getBean("beanId");
     ```
   - ApplicationContextAware

     ```java
      @Component
      public class SpringUtils implements ApplicationContextAware {

          private static ApplicationContext applicationContext;

          public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
              SpringUtils.applicationContext = applicationContext;
          }

          public static <T> T getBean(String beanName) {
              if(applicationContext.containsBean(beanName)){
                  return (T) applicationContext.getBean(beanName);
              }else{
                  return null;
              }
          }

          public static <T> Map<String, T> getBeansOfType(Class<T> baseType){
              return applicationContext.getBeansOfType(baseType);
          }
      }
     ```

   - ApplicationObjectSupport

     ```java
     @Service
     public class SpringContextHelper2 extends ApplicationObjectSupport {
        //提供一个接口，获取容器中的Bean实例，根据名称获取
        public Object getBean(String beanName)
        {
           return getApplicationContext().getBean(beanName);
        }
     }
     ```

   - WebApplicationObjectSupport

2. 被 AOP 封装一遍后的 bean 对象

   - **aop 代理底层使用的是`接口`的相关参数创建动态代理对象, 所以可以获取 IOC 中接口对象**

### [bean lifecycle](https://github.com/Alice52/java-ocean/issues/116#issuecomment-629587378)

1. xml 中配置的信息都会体现在 BeanDefinition[没有 id 和 name] 中

   ![avatar](/static/image/spring/spring-ioc-bean.png)

2. bean xml node 上的 id 和 name 是帮助注册到 bean 的注册中心[BeanDefinitionRegistry extends AliasRegistry]

   ```java
   // beanName is value of id, and in BeanDefinitionRegistry
   // 一个 id 只能有一个 bean, 但是一个bean可以有多个 id
   // 所以通过别名[name 属性]去获取 BeanDefinition 是不可以的
   // 没有写 ID 的则会默认使用 全类名#index 作为 id
   void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

   // name in AliasRegistry
   void registerAlias(String name, String alias);
   ```

3. xml --> BeanDefinition --> BeanDefinitionRegister

   ![avatar](/static/image/spring/spring-ioc-bean-definition.png)

#### simple version

1.  create bean by `BeanFactory`
2.  set property for bean
3.  pass bean to postProcessBeforeInitialization
4.  bean init method
5.  pass bean to postProcessAfterInitialization
6.  `use bean`
7.  bean destroy method

#### complex version

1. [bean definition] BeanDefinitionRegistryPostProcessor
2. [factory] BeanFactoryPostProcessor
3. [create] InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation

   - [InstantiationAwareBeanPostProcessor](https://blog.csdn.net/u010634066/article/details/80321854)
   - 主要作用在于目标对象的实例化过程中需要处理的事情, 包括实例化对象的前后过程以及实例的属性设置

4. [create] InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation
5. [init] BeanPostProcessor#postProcessBeforeInitialization
6. [init] @PostConstruct
7. [init] InitializingBean#afterPropertiesSet
8. [init] @Bean#init
9. [init] BeanPostProcessor#postProcessAfterInitialization
10. [init] SmartInitializingSingleton#afterSingletonsInstantiated
11. [destroy] @PreDestroy
12. [destroy] DisposableBean#destroy
13. [destroy] @Bean#destroy

---

### xxAware: 获取 spring 底层的组件, 只需要自定义的组件实现 xxAware 接口

![avatar](/static/image/spring/annotation-aware.png)

1. ioc container: ApplicationContextAware: 获得当前的 application context 从而调用容器的服务
2. bean factory: BeanFactoryAware: 获得当前 bean Factory, 从而调用容器的服务
3. ApplicationEventPublisherAware: 应用时间发布器, 用于发布事件
4. ServletContextAware
5. MessageSourceAware: 得到 message source 从而得到文本信息
6. ResourceLoaderAware: 获取资源加载器, 可以获得外部资源文件
7. NotificationPublisherAware
8. EnvironmentAware
9. EmbeddedValueResolverAware
10. ImportAware
11. ServletConfigAware
12. LoadTimeWeaverAware
13. BeanNameAware: 获得到容器中 Bean 的名称
14. BeanClassLoaderAware

### [Aware](https://www.jianshu.com/p/5865c5c3d0a3)

1. introduce

   - Aware 是一个具有标识作用的超级接口
   - 实现该接口的 bean 是具有被 spring 容器通知的能力的
   - 被通知的方式就是通过回调
   - 直接或间接实现了这个接口的类, 都具有被 spring 容器通知的能力

2. 以此可以通过实现 xxAware 接口获取 spring 的容器资源

3. 不能使用 @AutoWire

   - EnvironmentAware
   - EmbeddedValueResolverAware
   - ResourceLoaderAware
   - ApplicationEventPublisherAware
   - MessageSourceAware
   - ApplicationContextAware