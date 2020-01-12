## issue

### 1. sequence of component

#### load

1. load web.xml, pasre <listener> and <context-param> tag
2. create ServletContext
3. convert <context-param> and set to ServletContext
4. create <listener> instance

#### execute

1. Listener
2. Filter: execute by define order
3. Interceptor
   - preHandle: Sequential call
4. Servlet[DispatcherServlet]
5. Interceptor
   - postHandler: Call in reverse order
   - afterCompletion: Call in reverse order
     > 1. postHandler called when all interceptors in the interceptor chain execute successfully
     > 2. afterCompletion is called only if preHandle returns true
     > 3. make two Interceptor execute sequential: extends WebMvcConfigurerAdapter or implement WebMvcConfigurer, then overwrite or implement addInterceptor() method

### 2. static resources

- question

  - 若将 DispatcherServlet 请求映射配置为 /, 则 Spring MVC 将捕获 WEB 容器的所有请求, 包括静态资源的请求, SpringMVC 会将他们当成一个普通请求处理, 因找不到对应处理器将导致错误。<br/>
  - 在 SpringMVC 的配置文件中配置 <mvc:default-servlet-handler/> 的方式解决静态资源的问题: <br/>
    <mvc:default-servlet-handler/> 将在 SpringMVC 上下文中定义一个 DefaultServletHttpRequestHandler,
    它会对进入 DispatcherServlet 的请求进行筛查, 如果发现是没有经过映射的请求, 就将该请求交由 WEB 应用服务器默认的 Servlet 处理,
    如果不是静态资源的请求, 才由 DispatcherServlet 继续处理.<br/>
  - 一般 WEB 应用服务器默认的 Servlet 的名称都是 default。若所使用的 WEB 服务器[Tomcat]的默认 Servlet 名称不是 default, 则需要通过 default-servlet-name 属性显式指定.<br/>

- solution

  ```xml
  <!-- web.xml -->
  <servlet-mapping>
    <servlet-name>DispatcherServlet</servlet-name>
    <!-- defience between /* and /: /* donot handle suffix request eg. .jsp -->
    <url-pattern>/</url-pattern>
  </servlet-mapping>

  <!-- spring config.xml: handle static resources -->
  <mvc:default-servlet-handler/>
  <mvc:annotation-driven/>
  ```

### 3. globalization fmt

- define i18n_en_US.properties
- config spring mvc.xml

  ```xml
  <!-- Configuring internationalized resource files -->
  <bean id="messageSource"
      class="org.springframework.context.support.ResourceBundleMessageSource">
      <property name="basename" value="i18n"></property>
  </bean>
  ```

- fmt:message use it
- theroy: `LocaleResolver`

### 4. <mvc:annotation-driven>

- apply

  - configuring <mvc:view-controller> or <mvc:default-servlet-handler/> will invalidate other request paths
  - spring mvc inject RequestMappingHandlerMapping/RequestMappingHandlerAdapter/ExceptionHandlerExceptionResolver beans
  - Support for type conversion of form parameters using a ConversionService instance
  - Support for @NumberFormat annotation, @DateTimeFormat to format data
  - Support for @Valid to validate JavaBean according to JSR 303
  - Support for @RequestBody and @ResponseBody annotation

- principle

  - Start some new component objects to replace the old ones, thus implementing some new and more powerful features.

- explian <mvc:default-servlet-handler> and <mvc:annotation-driven>

  - no <mvc:default-servlet-handler> and <mvc:annotation-driven>

  ```java
  // DispatcherServlet -- handlerAdapters property
  HttpRequestHandlerAdapter
  SimpleControlleraHandlerAdapter
  AnnotationMethodHandlerAdapter
  ```

  - has <mvc:default-servlet-handler> and no <mvc:annotation-driven>

  ```java
  // DispatcherServlet -- handlerAdapters property
  HttpRequestHandlerAdapter
  SimpleControlleraHandlerAdapter
  ```

  - has <mvc:default-servlet-handler> and <mvc:annotation-driven>

  ```java
  // DispatcherServlet -- handlerAdapters property
  HttpRequestHandlerAdapter
  SimpleControlleraHandlerAdapter
  // RequestMappingHandlerAdapter replace AnnotationMethodHandlerAdapter
  RequestMappingHandlerAdapter
  ```

### 5. how to create spring IOC container

- no web env
  - create IOC container in mian or junit test
- web env

  - web application start up and create spring IOC container
  - solution:

  ```java
  1. config listener to listen ServletContext Object
  2. then create IOC container
  3. bind IOC container to ServeletContext to share IOC container component
  ```

  - sample

  ```xml
  <!-- web.xml -->
  <!-- enable spring IOC -->
  <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:ApplicationContext.xml</param-value>
  </context-param>
  <listener>
      <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>

  <!-- enable spring mvc IOC -->
  <servlet>
      <display-name>DispatcherServlet</display-name>
      <servlet-name>MVCDispatcherServlet</servlet-name>
      <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
      <init-param>
      <param-name>contextConfigLocation</param-name>
      <!-- create container: param-name default: /WEB-INF/<servlet-name>-servlet.xml -->
      <param-value>classpath*:Spring-mvc-applicaitionContext.xml</param-value>
      </init-param>
      <load-on-startup>1</load-on-startup>
  </servlet>

  <servlet-mapping>
      <servlet-name>MVCDispatcherServlet</servlet-name>
      <url-pattern>/*</url-pattern>
  </servlet-mapping>
  ```

### 6. spring and spring mvc config

- spring can config database, transaction etc

  ```xml
  <context:component-scan base-package="cn.edu.ntu.*">
      <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
  </context:component-scan>
  ```

- spring mvc just manage @controller handler

  ```xml
  <context:component-scan base-package="cn.edu.ntu.*" use-default-filters="false">
          <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
  </context:component-scan>
  ```

- in this case: spring IOC container is parent container, mvc IOC container is child container. So, child contianer can be access to parent container, but parent container cannot access to child contianner.

- get applicationContext container

  ```java
  @RequestMapping(value = "/servlet/{Id}")
  public JsonResult getContainerFServletContext(
          HttpSession session) throws ServletException, IOException {

      JsonResult result = new JsonResult();

      ServletContext servletContext = session.getServletContext();

      ApplicationContext ctx = (ApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
      WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

      return result;
  }
  ```

### 7. form repeated submission

- method: donot forward, use redirect
