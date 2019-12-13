## Spring MVC

### Introduce

1. feature
   - Born to have integration with spring
   - Support Rest API and JSON response
   - DispatcherServlet
   - Handler and filter
   - ModelAndView.
   - Request-response model
2. framework diagram

### Quick Start

- web.xml: config ServletDispatcher

  ```xml
  <!-- encoding -->
  <filter>
      <filter-name>EncodingFilter</filter-name>
      <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
      <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
      </init-param>
  </filter>
  <filter-mapping>
      <filter-name>EncodingFilter</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>

  <!-- Rest API Filter-->
  <!-- delete request
    <form action="user/1" method="post">
      <input type="hidden" name="_method" value="DELETE"/>
    </form>
  -->
  <filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
  </filter>

  <filter-mapping>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>

  <!-- Servlet Context Listener -->
  <listener>
      <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>

  <!-- Servlet Configuration -->
  <servlet>
      <descriptio/>
      <display-name>DispatcherServlet</display-name>
      <servlet-name>DispatcherServlet</servlet-name>
      <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
      <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
      </init-param>
      <init-param>
        <!-- create container: param-name default: /WEB-INF/<servlet-name>-servlet.xml -->
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:ApplicationContext.xml</param-value>
      </init-param>
      <!-- speciy Servlet create time: created when request arrival; created on startup[default]. -->
      <!-- number is squence of loading Servlet -->
      <load-on-startup>1</load-on-startup>
  </servlet>
  <!-- URL space mappings -->
  <servlet-mapping>
      <servlet-name>DispatcherServlet</servlet-name>
      <!-- defience between /* and /: /* donot handle suffix request eg. .jsp -->
      <url-pattern>/</url-pattern>
  </servlet-mapping>
  ```

- applicationContext.xml: config container

  ```xml
  <!-- spring container -->
  <context:component-scan />
  <!-- ViewResolver:  f-b will not ned this -->
  <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
  </bean>
  <bean/>
  <context:placeholder/>

  <!-- directly configure response page: no need to go through the controller to execute the result -->
  <mvc:view-controller path="/success" view-name="success"/>
  <!-- handle static resources -->
  <mvc:default-servlet-handler/>
  <!-- configuring <mvc:view-controller> or <mvc:default-servlet-handler/> will invalidate other request paths -->
  <!-- spring mvc inspect RequestMappingHandlerMapping/RequestMappingHandlerAdapter/ExceptionHandlerExceptionResolver beans -->
  <mvc:annotation-driven/>
  ```

- java code[Controller]
- deploy in Tomcat

### Annotation

- guidline
  ```java
  @Controller
  @RestController
  @RequestMapping
  @PathVariable // Get path value eg. user/{id}
  @RequestParam // Get paramters eg. user?name=zack
  @CookieValue
  @Resource // javax.annotation.Resource
  @Autowired
  @ModelAttribute // Add to Method or Parameter
  @SessionAttributes 
  @ResponseBody
  ```

1. @RequestMapping && @PathVariable

   - source code

   ```java
   /**
   * Annotation for mapping web requests onto methods in request-handling classes
   * with flexible method signatures.
   * @since 2.5
   * @see GetMapping
   * @see PostMapping
   * @see PutMapping
   * @see DeleteMapping
   * @see PatchMapping
   */
   @Target({ElementType.TYPE, ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   @Mapping
   public @interface RequestMapping {

       // Assign a name to this mapping.
       String name() default "";

       // The primary mapping expressed by this annotation.
       @AliasFor("path")
       String[] value() default {};

       //The path mapping URIs (e.g. {@code "/profile"}).
       @AliasFor("value")
       String[] path() default {};

       // The HTTP request methods to map to, narrowing the primary mapping:
       // GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
       RequestMethod[] method() default {};

       // The parameters of the mapped request, narrowing the primary mapping.
       String[] params() default {};

       // The headers of the mapped request, narrowing the primary mapping.
       String[] headers() default {};

       // Narrows the primary mapping by media types that can be consumed by the
       // mapped handler. Consists of one or more media types one of which must
       // match to the request {@code Content-Type} header. Examples:
       // <pre class="code">
       // consumes = "text/plain"
       // consumes = {"text/plain", "application/*"}
       // consumes = MediaType.TEXT_PLAIN_VALUE
       // </pre>
       String[] consumes() default {};

       // Narrows the primary mapping by media types that can be produced by the
       // mapped handler. Consists of one or more media types one of which must
       // be chosen via content negotiation against the "acceptable" media types
       // of the request. Typically those are extracted from the {@code "Accept"}
       // header but may be derived from query parameters, or other. Examples:
       // <pre class="code">
       // produces = "text/plain"
       // produces = {"text/plain", "application/*"}
       // produces = MediaType.TEXT_PLAIN_VALUE
       // produces = "text/plain;charset=UTF-8"
       // </pre>
       String[] produces() default {};
   }

   /**
    * Annotation which indicates that a method parameter should be bound to a URI template
    * variable. Supported for {@link RequestMapping} annotated handler methods.
    */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface PathVariable {

        // Alias for {@link #name}.
        @AliasFor("name")
        String value() default "";

        // The name of the path variable to bind to.
        @AliasFor("value")
        String name() default "";

        // Whether the path variable is required.
        boolean required() default true;
    }
   ```

   - target: @Target({ElementType.TYPE, ElementType.METHOD})
   - function: request arrive DispatcherServlet, then will according to @RequestMapping annotation to specify executive method

2. @RequestParam & @RequestHeader & @CookieValue

   - source code

   ```java
   /**
   * RequestParam: Annotation which indicates that a method parameter should be bound to a web request parameter.
   * RequestHeader: Annotation which indicates that a method parameter should be bound to a web request header.
   * CookieValue: Annotation which indicates that a method parameter should be bound to an HTTP cookie.
   */
   @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   public @interface RequestParam & RequestHeader & CookieValue {

       // Alias for {@link #name}.
       @AliasFor("name")
       String value() default "";

       // The name of the request parameter to bind to.
       @AliasFor("value")
       String name() default "";

       // Whether the parameter is required.
       boolean required() default true;

       // The default value to use as a fallback when the request parameter is
       // not provided or has an empty value.
       String defaultValue() default ValueConstants.DEFAULT_NONE;
   }
   ```

   - target: @Target(ElementType.PARAMETER)
   - function: get url var name eg. http://***/base/hello/1?name=zack

3. @ModelAttribute

   - source code

   ```java
   /**
   * Annotation that binds a method parameter or method return value
   * to a named model attribute, exposed to a web view. Supported
   * for controller classes with {@link RequestMapping @RequestMapping}
   * methods.
   */
   @Target({ElementType.PARAMETER, ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   public @interface ModelAttribute {

       @AliasFor("name")
       String value() default "";

       @AliasFor("value")
       String name() default "";

       // Allows declaring data binding disabled directly on an {@code @ModelAttribute}
       // method parameter or on the attribute returned from an {@code @ModelAttribute}
       // method, both of which would prevent data binding for that attribute.
       boolean binding() default true;
   }
   ```

   - processor

   ```
   1. 有 @ModelAttribute() 修饰的方法, 从 DB 中获取对象
       1.1 put 进入 request 中, 也放入 implicitModel 中
       1.2 request 参数中可以封装成对象的数据覆盖[修改]之前put的数据[ implicitModel 中的]
       1.3 将改变后的对象当做参数传入, 并放入 request中
   2. 没有 @ModelAttribute() 修饰的方法
       2.1 会通过反射创建一个对象
       2.2 将 request 参数封装成对象当做参数传入, 并放入 request中

   - 执行 @ModelAttribute 注解修饰的方法: 从数据库中取出对象, 把对象放入到了 Map 中. 键为: user
   - SpringMVC 从 Map 中取出 User 对象, 并把表单的请求参数赋给该 User 对象的对应属性.
   - SpringMVC 把上述对象传入目标方法的参数.
   ```

   - source code

   ```java
   /**
   * 1. 有 @ModelAttribute 标记的方法, 会在每个目标方法执行之前被 SpringMVC 调用!
   * 2. @ModelAttribute 注解也可以来修饰目标方法 POJO 类型的入参, 其 value 属性值有如下的作用:
   *   1). SpringMVC 会使用 value 属性值在 implicitModel 中查找对应的对象, 若存在则会直接传入到目标方法的入参中.
   *   2). SpringMVC 会一 value 为 key, POJO 类型的对象为 value, 存入到 request 中.
   */
   @ModelAttribute
   public void getUser(@RequestParam(value="id",required=false) Integer id,
           Map<String, Object> map){
       System.out.println("modelAttribute method");
       if(id != null){
           // 模拟从数据库中获取对象
           User user = new User(1, "Tom", "123456", "tom@atguigu.com", 12);
           System.out.println("从数据库中获取一个对象: " + user);

           map.put("user", user);
       }
   }

   /**
   * 运行流程:
   * 1. 执行 @ModelAttribute 注解修饰的方法: 从数据库中取出对象, 把对象放入到了 Map 中. 键为: user
   * 2. SpringMVC 从 Map 中取出 User 对象, 并把表单的请求参数赋给该 User 对象的对应属性.
   * 3. SpringMVC 把上述对象传入目标方法的参数.
   *
   * 注意: 在 @ModelAttribute 修饰的方法中, 放入到 Map 时的键需要和目标方法入参类型的第一个字母小写的字符串一致!
   *
   * SpringMVC 确定目标方法 POJO 类型入参的过程

       前置:
           @ModelAttribute 修饰方法, 则put方法会向implicitModel中保存值
   * 1. 确定一个 key:
   * 1). 若目标方法的 POJO 类型的参数木有使用 @ModelAttribute 作为修饰, 则 key 为 POJO 类名第一个字母的小写
   * 2). 若使用了  @ModelAttribute 来修饰, 则 key 为 @ModelAttribute 注解的 value 属性值.
   * 2. 在 implicitModel 中查找 key 对应的对象, 若存在, 则作为入参传入
   * 1). 若在 @ModelAttribute 标记的方法中在 Map 中保存过, 且 key 和 1的 确定的 key 一致, 则会获取到. 【这里会将@ModelAttribute 修饰方法中put进去的值, 修改为request参数中的值】
   * 3. 若 implicitModel 中不存在 key 对应的对象, 则检查当前的 Handler 是否使用 @SessionAttributes 注解修饰,
   * 若使用了该注解, 且 @SessionAttributes 注解的 value 属性值中包含了 key, 则会从 HttpSession 中来获取 key 所
   * 对应的 value 值, 若存在则直接传入到目标方法的入参中. 若不存在则将抛出异常.
   * 4. 若 Handler 没有标识 @SessionAttributes 注解或 @SessionAttributes 注解的 value 值中不包含 key, 则
   * 会通过反射来创建 POJO 类型的参数, 传入为目标方法的参数
   * 5. SpringMVC 会把 key 和 POJO 类型的对象保存到 implicitModel 中, 进而会保存到 request 中.
   *
   * 源代码分析的流程
   * 1. 调用 @ModelAttribute 注解修饰的方法. 实际上把 @ModelAttribute 方法中 Map 中的数据放在了 implicitModel 中.
   * 2. 解析请求处理器的目标参数, 实际上该目标参数来自于 WebDataBinder 对象的 target 属性
   * 1). 创建 WebDataBinder 对象:
   * ①. 确定 objectName 属性: 若传入的 attrName 属性值为 "", 则 objectName 为类名第一个字母小写.
   * *注意: attrName. 若目标方法的 POJO 属性使用了 @ModelAttribute 来修饰, 则 attrName 值即为 @ModelAttribute
   * 的 value 属性值
   *
   * ②. 确定 target 属性:
   * 	> 在 implicitModel 中查找 attrName 对应的属性值. 若存在, ok
   * 	> *若不存在: 则验证当前 Handler 是否使用了 @SessionAttributes 进行修饰, 若使用了, 则尝试从 Session 中
   * 获取 attrName 所对应的属性值. 若 session 中没有对应的属性值, 则抛出了异常.
   * 	> 若 Handler 没有使用 @SessionAttributes 进行修饰, 或 @SessionAttributes 中没有使用 value 值指定的 key
   * 和 attrName 相匹配, 则通过反射创建了 POJO 对象
   *
   * 2). SpringMVC 把表单的请求参数赋给了 WebDataBinder 的 target 对应的属性.
   * 3). *SpringMVC 会把 WebDataBinder 的 attrName 和 target 给到 implicitModel.
   * 近而传到 request 域对象中.
   * 4). 把 WebDataBinder 的 target 作为参数传递给目标方法的入参.
   */
   @RequestMapping("/testModelAttribute")
   public String testModelAttribute(User user){
       System.out.println("修改: " + user);
       return SUCCESS;
   }
   ```

4. @SessionAttributes: Scratch a property in the model into the HttpSession so that it can be shared between multiple requests

   - @SessionAttributes 通过属性名添加；模型属性的对象类型添加
   - @SessionAttributes(types=User.class) 会将隐含模型中所有类型为 User.class [map 中的 value]的属性添加到会话中。
   - @SessionAttributes(value={“user1”, “user2”}
   - @SessionAttributes(types={User.class, Dept.class})
   - @SessionAttributes(value={“user1”, “user2”}, types={Dept.class})

   ```java
   @Controller
   @SessionAttributes("user")
   @RequestMapping("/hel")
   public class HelloWOrld{
       @ModelAttribute("user")
       public User getUser (){
           User user = new User()；
           user.setAge(10);
           return user;
       }

       @RequestMapping("/handler")
       public String delete( @ModelAttribute("user") User user){
           ....
       }

       @RequestMapping("/delete")
       public String handle(Map<String, Object> map){
           map.put("time", new Data());
           //这里可以获取到user的值
           User user = (User) map.get("user");
       }
   }
   ```

- demo

  ```java
  // http://***/base/hello/1?name=zack&age=18
  @RequestMapping(value = "/hello/{Id}"
      , method = {RequestMethod.DELETE, RequestMethod.GET}
      , params = {"!UserCode", "Access_Token", "age=12"}
      , headers = "Accept-Language"
      , consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public JsonResult hello(
      HttpServletRequest request // need servlet-api dependency
      , HttpServletResponse reqponse // need servlet-api dependency
      , User user // Automatic matching by request parameter name and POJO attribute name
      , @PathVariable("Id") String id
      , @RequestParam("name") String name
      , @RequestParam(value = "age", required = false, defaultValue = "0") int age
      , @RequestHeader(value = "Accept-Language", required = false, defaultValue = "0") String acceptLanguage
      , @CookieValue(value = "JSESSIONID", required = false, defaultValue = "0") String sessionId){

          request.getRequestDispatcher("URL").forward(request, reqponse);
          reqponse.sendRedirect("URL");

          return null;
      }
  ```

### Filter

- execute before Servlet[DispatcherServlet]
- more filter execute by define order

- demo

  ```xml
  <!-- web.xml -->
  <!-- encoding -->
  <filter>
      <filter-name>EncodingFilter</filter-name>
      <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
      <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
      </init-param>
  </filter>
  <filter-mapping>
      <filter-name>EncodingFilter</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>

  <!-- rest api -->
  <!--
      delete request
      <form action="user/1" method="post">
          <input type="hidden" name="_method" value="DELETE"/>
      </form>
  -->
  <filter>
      <filter-name>HiddenHttpMethodFilter</filter-name>
      <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
  </filter>

  <filter-mapping>
      <filter-name>HiddenHttpMethodFilter</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
  ```

### Listener

1. create spring IOC container
2. java code: spring mvc impliment class[ContextLoaderListener]

   ```java
   public class CunstomerServletContextListener implements ServletContextListener {

   @Override
   // created when tomcat container startup
   public void contextInitialized(ServletContextEvent sce) {
       ApplicationContext ctx = new ClassPathXmlApplicationContext("ApplicationContext.xml");
       ServletContext servletContext = sce.getServletContext();
       servletContext.setAttribute("applicationContext", ctx);
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce) {}
   }

   /**
   * @author zack
   * @create 2019-11-11 21:23
   * @function controller handler means servlet
   */
   public class CustomerServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
       ServletContext context = getServletContext();
       ApplicationContext ctx = (ApplicationContext) context.getAttribute("applicationContext");
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
       super.doPost(req, resp);
   }
   }
   ```

### Converter

1. function: Data type conversion, data type formatting, data verification
2. <mvc:annotation-driven/>
   - configuring <mvc:view-controller> or <mvc:default-servlet-handler/> will invalidate other request paths
   - spring mvc inspect RequestMappingHandlerMapping/RequestMappingHandlerAdapter/ExceptionHandlerExceptionResolver beans
   - Support for type conversion of form parameters using a ConversionService instance
   - Support for @NumberFormat annotation, @DateTimeFormat to format data
   - Support for @Valid to validate JavaBean according to JSR 303
   - Support for @RequestBody and @ResponseBody annotation
3. config spring mvc.xml
   ```xml
   <mvc:annotation-driven conversion-service="conversionService"></mvc:annotation-driven>
   <!-- config ConversionService -->
   <bean id="conversionService" class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
       <property name="converters">
           <set>
               <ref bean="employeeConverter"/>
           </set>
       </property>
   </bean>
   ```

### HandlerInterceptor

- java code

```java
@Component
// execute sequence according to config sequence, same as Filter
public class CustomerInterceptor implements HandlerInterceptor {

  @Override
  // from first to last
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    return false;
  }

  @Override
  // from last to first
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {}

  @Override
  // from last to first
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {}
}
```

- comfig spring config.xml

```xml
 <mvc:interceptors>
    <!-- intercept all request -->
    <ref bean="custmInterceptor"/>

    <!-- intercept specify request -->
    <mvc:interceptor>
        <mvc:mapping path="/users"/>
        <mvc:exclude-mapping path="/list"/>
        <ref bean="custmInterceptor"/>
    </mvc:interceptor>
</mvc:interceptors>
```

1. handlerMapping
2. HandlerAdapter
3. Handler

### ModelAndView[了解]

- spring mvc will set ModelAndView's model data to request scope
- spring mvc will envople handler result[@controller] to ModelAndView Object
  ![avatar](/static/image/spring/ModelAndView.png)

1. ModelAndView

   ```java
   @RequestMapping("/testModelAndView")
   public ModelAndView testModelAndView(){
       System.out.println("testModelAndView");
       String viewName = "success";
       ModelAndView mv = new ModelAndView(viewName );
       mv.addObject("time",new Date().toString());
       return mv;
   }
   ```

2. Model & Map

   ```java
   @RequestMapping("/testMap2")
   public String testMap2(Map<String, Object> map, Model model, ModelMap modelMap){
       System.out.println(map.getClass().getName());
       map.put("names", Arrays.asList("Tom","Jerry","Kite"));
       model.addAttribute("model", "org.springframework.ui.Model");
       modelMap.put("modelMap", "org.springframework.ui.ModelMap");

       System.out.println(map == model);  // true
       System.out.println(map == modelMap); // true
       System.out.println(model == modelMap); // true

       System.out.println(map.getClass().getName());  // BindingAwareModelMap
       System.out.println(model.getClass().getName()); // BindingAwareModelMap
       System.out.println(modelMap.getClass().getName()); // BindingAwareModelMap

       return "success";
   }
   ```

3. ViewResolver: InternalResourceViewResolver

   ```java
   /**
   * Interface to be implemented by objects that can resolve views by name.
   */
   public interface ViewResolver {

       // Resolve the given view by name.
       @Nullable
       View resolveViewName(String viewName, Locale locale) throws Exception;
   }
   ```

4. LocalResolver
5. View: InternalResourceView & JstlView

   - InternalResourceView

   ```java
   /**
   * MVC View for a web interaction. Implementations are responsible for rendering
   * content, and exposing the model. A single view exposes multiple model attributes.
   */
   public interface View {

       // Name of the {@link HttpServletRequest} attribute that contains the response status code.
       String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";

       // Name of the {@link HttpServletRequest} attribute that contains a Map with path variables.
       // The map consists of String-based URI template variable names as keys and their corresponding
       // Object-based values -- extracted from segments of the URL and type converted.
       String PATH_VARIABLES = View.class.getName() + ".pathVariables";

       // The {@link org.springframework.http.MediaType} selected during content negotiation,
       // which may be more specific than the one the View is configured with. For example:
       // "application/vnd.example-v1+xml" vs "application/*+xml".
       String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";

       // Return the content type of the view, if predetermined.
       @Nullable
       default String getContentType() {
           return null;
       }

       // Render the view given the specified model.
       // <p>The first step will be preparing the request: In the JSP case, this would mean
       // setting model objects as request attributes. The second step will be the actual
       // rendering of the view, for example including the JSP via a RequestDispatcher.
       void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
               throws Exception;
       }
   ```

   - BeanNameViewResolver: Custom View: 需要配置 BeanNameViewResolver 解析器 [使用 view 的名字解析视图]

   ```java
   @Component
   public class HelloView implements View{
       @Override
       public String getContentType() {
           return "text/html";
       }

       @Override
       public void render(Map<String, ?> model, HttpServletRequest request,
               HttpServletResponse response) throws Exception {
           response.getWriter().print("hello view, time: " + new Date());
       }
   }
   ```

   ```xml
   <!-- 配置视图  BeanNameViewResolver 解析器: 使用视图的名字来解析视图 -->
   <!-- 通过 order 属性来定义视图解析器的优先级, order 值越小优先级越高 -->
   <bean class="org.springframework.web.servlet.view.BeanNameViewResolver">
       <property name="order" value="100"></property>
   </bean>
   ```

   - function: Render the model data and present the data in the model to the client, mainly to complete the forwarding or redirect operation.

   - conclusion

   ```xml
    a) 视图的作用:rent方法渲染模型数据, 将模型里的数据以某种形式呈现给客户。
            视图解析器的作用: 将逻辑视图[视图名], 转换为物理视图
    b) Spring MVC 内部将返回String, View , model类型的方法装配成一个ModelAndView 对象,
        借助视图解析器（ViewResolver implement ViewResolver接口）得到最终的视图对象（View）[jsp,Excel ect].
        视图对象由视图解析器负责实例化。由于视图是无状态的, 所以他们不会有线程安全的问题;
    c) 视图分类:
        URL:
            InternalResourceView 【默认试图将JSP或其他资源封装成View】
            JstlView: 支持JSTL国际化标签功能
        文档视图:
            AbstractExcelView: Excel文档视图抽象类, 基于POI构造Excel文档。
            AbstractPdfView: Excel文档视图抽象类, 基于iText构造PDF文档。
        报表视图、JSON视图等

    d) Spring WEB [*context.xml] 上下文中配置一种或多种解析策略, 并指定他们之间的先后顺序[order属性: order越小优先级越高].
    e) 视图解析器分类:
        解析为Bean的名字:
            BeanNameViewResolver: 将视图解析为一个Bean, Bean的Id相当于视图名
        解析为URL:
            InternalResourceViewReslover: 将视图名解析为一个URL文件
                <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
                    <property name="prefix" value="/WEB-INF/views/"></property>
                    <property name="suffix" value=".jsp"></property>
                </bean>
            JasperReportsViewResolver:
        魔板文件视图等
   ```

6. conclusion

   - 传入参数为对象的话, 会根据 request 的参数封装成这个对象;
   - 传入参数为 map 时会自动放入 request 域对象中
   - 有 @ModelAttribute 标记的方法, 会在每个目标方法执行之前被 SpringMVC 调用!

     - ModelAndView: 处理方法返回值类型为 ModelAndView 时, 方法体即可通过该对象添加模型数据: 其既包含视图信息, 也包含模型数据信息
     - 添加模型数据:
       - MoelAndView addObject(String attributeName, Object attributeValue)
       - ModelAndView addAllObject(Map<String, ?> modelMap)
     - 设置视图:

       - void setView(View view)
       - void setViewName(String view

     - Map 及 Model: **ModelMap 或 java.uti.Map 时, 处理方法返回时, Map 中的数据会自动添加到模型中.**
     - Spring MVC 在内部使用了一个 org.springframework.ui.Model 接口存储模型数据
     - **Spring MVC 在调用方法前会创建一个隐含的模型对象作为模型数据的存储容器.**
     - 如果方法的入参为 Map 或 Model 类型, Spring MVC 会将隐含模型的引用传递给这些入参。
     - 在方法体内, 开发者可以通过这个入参对象访问到模型中的所有数据, 也可以向模型中添加新的属性数据

     ```java
     @ModelAttribute("user")
     public User getUser (){
         User user = new User()；
         user.setAge(10);
             return user;
     }

     @RequestMapping("/delete")
     public String handle(Map<String, Object> map){
         map.put("time", new Data());
         //这里可以获取到user的值
         User user = (User) map.get("user");
     }
     ```

### Processing flows

#### Quick Start

1. start up application

   - start up tomcat will load `DispatcherServlet` in /webapp/WEB-INF/web.xml,
   - then load spring config file `ApplicationContext.xml` to create container as configed in web.xml
   - spring will scan component and work as annatation
   - @controller will marked requestHandler, then can handle request.

2. UI request
   - request arrive `web.xml` and pattern with tag <url-pattern>,
   - then get which DispatcherServlet will handle this request
   - then this request will be sended to @controller
   - @controller method will return result[json]
   - ViewResolver will get result and combine `physical view path: prefix + result + suffix`
   - forwaord to specify VIEW

#### flow

- request --> filter --> preHandler --> dispatcherServlet --> postHandler

// TODO

- diagram
  ![avatar](/static/image/spring/spring-mvc-processor.png)

### Rest API

- HTTP protocol is a stateless protocol, that is, all states are saved on the server side.

- GET/POST/PUT/DELETE

- integration spring mvc

  ```xml
  <!-- web.xml -->

  <filter>
      <filter-name>HiddenHttpMethodFilter</filter-name>
      <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
  </filter>

  <filter-mapping>
      <filter-name>HiddenHttpMethodFilter</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
  ```

  ```jsp
  <!-- delete request -->
  <form action="user/1" method="post">
    <input type="hidden" name="_method" value="DELETE"/>
  </form>
  ```

- \_method explain

  ```java
  public class HiddenHttpMethodFilter extends OncePerRequestFilter {

      private static final List<String> ALLOWED_METHODS =
              Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT.name(),
                      HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));

      //Default method parameter: {@code _method}.
      public static final String DEFAULT_METHOD_PARAM = "_method";

      private String methodParam = DEFAULT_METHOD_PARAM;

      // Set the parameter name to look for HTTP methods.
      public void setMethodParam(String methodParam) {
          Assert.hasText(methodParam, "'methodParam' must not be empty");
          this.methodParam = methodParam;
      }

      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
              throws ServletException, IOException {

          HttpServletRequest requestToUse = request;

          if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
              String paramValue = request.getParameter(this.methodParam);
              if (StringUtils.hasLength(paramValue)) {
                  String method = paramValue.toUpperCase(Locale.ENGLISH);
                  if (ALLOWED_METHODS.contains(method)) {
                      requestToUse = new HttpMethodRequestWrapper(request, method);
                  }
              }
          }

          filterChain.doFilter(requestToUse, response);
      }

      // Simple {@link HttpServletRequest} wrapper that returns the supplied method for {@link HttpServletRequest#getMethod()}.
      private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {
          private final String method;
          public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
              super(request);
              this.method = method;
          }

          @Override
          public String getMethod() {
              return this.method;
          }
      }
  }
  ```

- REST update 请求实现: lastName 不能修改, 所以不展示

  ```java
  // get modify employee
  @RequestMapping(value="/emp/{id}", method=RequestMethod.GET)
  public String input(@PathVariable("id") Integer id, Map<String, Object> map){
      map.put("employee", employeeDao.get(id));
      map.put("departments", departmentDao.getDepartments());
      return "input";
  }

  //lastName 不能修改, 所以不展示
  <form:form action="${pageContext.request.contextPath }/emp" method="POST"
      modelAttribute="employee">
      <c:if test="${employee.id == null }">
          LastName: <form:input path="lastName"/>
      </c:if>
      <c:if test="${employee.id != null }">
          <form:hidden path="id"/>
          <input type="hidden" name="_method" value="PUT"/>
      </c:if>
  </form:form>
  // 为了lastName不被修改, 且不会变为null
  @ModelAttribute
  public void getEmployee(@RequestParam(value="id",required=false) Integer id,
          Map<String, Object> map){
      if(id != null){
          map.put("employee", employeeDao.get(id));
      }
  }

  //将 form 传来的数据封装成 Employee 对象
  @RequestMapping(value="/emp", method=RequestMethod.PUT)
  public String update(Employee employee){
      employeeDao.save(employee);

      return "redirect:/emps";
  }
  ```

  ```xml
  <mvc:view-controller path="/success" view-name="success">
  <mvc:annotation-driven></mvc:annotation-driven>
  <mvc:default-servlet-handler/>
  ```

  ```xml
  <!-- 配置 web.xml 文件用 Spring 将 POST 转换为 PUT 请求 -->
  <!-- 配置 HiddenHttpMethodFilter: 把 POST 请求转为 DELETE、PUT 请求 -->
  <filter>
      <filter-name>HiddenHttpMethodFilter</filter-name>
      <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
  </filter>

  <filter-mapping>
      <filter-name>HiddenHttpMethodFilter</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>

  <form action="" method="POST">
      <input type="hidden" name="_method" value="DELETE"/>
  </form>
  ```

  ```jsp
  <!-- View -->
  <a href="emp/${emp.id}">Edit</a>

  <form:form action="${pageContext.request.contextPath }/emp" method="POST"
      modelAttribute="employee">
      <c:if test="${employee.id == null }">
          LastName: <form:input path="lastName"/>
      </c:if>
      <c:if test="${employee.id != null }">
          <form:hidden path="id"/>
          <input type="hidden" name="_method" value="PUT"/>
      </c:if>
  </form:form>
  ```

### JOSN

- @ResponseBody
- HttpMessageConverter

### Model

- POJO: Plain Ordinary Java Object
  ```java
  PO: after a POJO is persisted
  DTO: directly using it to pass and transfer
  VO: directly used to correspond to the presentation layer
  ```
- VO: Value[View] Object, Serving the logic
- PO[Entity]: persistent object
- DTO: Data Transfer Object,
- BO: Business Object, combining PO
- JavaBean: Reusable Component
- DAO: Data Access Object
  ```java
  POJO persist to PO
  comnine PO to VO or DTO
  ```

---

## 1. static resources

1.  question

    - 若将 DispatcherServlet 请求映射配置为 /, 则 Spring MVC 将捕获 WEB 容器的所有请求, 包括静态资源的请求, SpringMVC 会将他们当成一个普通请求处理, 因找不到对应处理器将导致错误。<br/>
    - 在 SpringMVC 的配置文件中配置 <mvc:default-servlet-handler/> 的方式解决静态资源的问题: <br/>
      <mvc:default-servlet-handler/> 将在 SpringMVC 上下文中定义一个 DefaultServletHttpRequestHandler,
      它会对进入 DispatcherServlet 的请求进行筛查, 如果发现是没有经过映射的请求, 就将该请求交由 WEB 应用服务器默认的 Servlet 处理,
      如果不是静态资源的请求, 才由 DispatcherServlet 继续处理.<br/>
    - 一般 WEB 应用服务器默认的 Servlet 的名称都是 default。若所使用的 WEB 服务器[Tomcat]的默认 Servlet 名称不是 default, 则需要通过 default-servlet-name 属性显式指定.<br/>

2.  solution

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

## 2. globalization fmt

- define i18n_en_US.properties
- config spring mvc.xml
  ```xml
  <!-- Configuring internationalized resource files -->
  <bean id="messageSource"
      class="org.springframework.context.support.ResourceBundleMessageSource">
      <property name="basename" value="i18n"></property>
  </bean>
  ```

## 3. <mvc:annotation-driven> explain:

- apply

  - configuring <mvc:view-controller> or <mvc:default-servlet-handler/> will invalidate other request paths
  - spring mvc inspect RequestMappingHandlerMapping/RequestMappingHandlerAdapter/ExceptionHandlerExceptionResolver beans
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

## 4. how to create spring IOC container

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

## 5. spring and spring mvc config

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
