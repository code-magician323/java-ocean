## xxAware: 获取 spring 底层的组件, 只需要自定义的组件实现 xxAware 接口

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

## get bean from ioc container

// TODO:

1. get ioc container, then get by type or bean name
2. get beanfatory, the get from factory
