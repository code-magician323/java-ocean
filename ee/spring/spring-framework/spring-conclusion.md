## spring

### bean 创建

1. 基于 class 构建
   ```xml
   <bean id="a" class="cn.edu.ntu.spring.ioc.A"/>
   ```
2. 构造方法构建
3. 静态方法构建
4. FactoryBean 构建

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
4. 方法注入: lookup-method

### [bean lifecycle](https://github.com/Alice52/java-ocean/issues/116#issuecomment-629587378)

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
