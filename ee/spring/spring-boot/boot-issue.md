## 1. diff between @Bean and @Component

    ```java
    // this condiftion can use @Component add to  IOC container success
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)

    // this condiftion can not use @Component add to  IOC container, should use @Bean
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
    ```

## 2. diff between WebMvcConfigurationSupport, WebMvcConfigurerAdapter,WebMvcConfigurer
