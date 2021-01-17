## spring session

### quick-start

1. 导入依赖

   ```xml
   <dependency>
       <groupId>org.springframework.session</groupId>
       <artifactId>spring-session-data-redis</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

2. 修改配置

   ```yaml
   spring:
   redis:
     host: 192.168.56.102
   session:
     store-type: redis
   ```

3. 添加注解

   ```java
   @EnableRedisHttpSession
   public class GulimallAuthServerApplication {
   ```

   - 自定义配置

     1. 由于默认使用 jdk 进行序列化，通过导入 RedisSerializer 修改为 json 序列化
     2. 并且通过修改 CookieSerializer 扩大 session 的作用域至\*\*.gulimall.com

     ```java
     @Configuration
     public class GulimallSessionConfig {

         @Bean
         public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
             return new GenericJackson2JsonRedisSerializer();
         }

         @Bean
         public CookieSerializer cookieSerializer() {
             DefaultCookieSerializer serializer = new DefaultCookieSerializer();
             serializer.setCookieName("GULISESSIONID");
             serializer.setDomainName("gulimall.com");
             return serializer;
         }
     }
     ```

4. SpringSession 核心原理 - 装饰者模式

   - `@EnableRedisHttpSession` 导入了 `RedisHttpSessionConfiguration`
   - 给容器中添加了 SessionRepository 组件 `RedisOperationsSessionRepository`, 封装了 redis 对 session 的 CRUD
   - SessionRepositoryFilter: 对每个请求进行 filter

     1. 创建的时候, 就自动从容其中获取 `SessionRepository`
     2. 原始的 `request, response` 都被使用 **SessionRepositoryRequestWrapper**, **SessionRepositoryResponseWrapper**
     3. 之后获取 session 时调用的 SessionRepositoryRequestWrapper#getSession()
     4. getSession() 底层时使用 SessionRepository 的 CRUD

        ```java
        // 原生的获取 session 时是通过 HttpServletRequest 获取的
        // 这里对 request 进行包装，并且重写了包装 request 的 getSession()方法
        @Override
        protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository);

            // 对原生的request、response进行包装: SessionRepositoryRequestWrapper 有自己的一套实现
            SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryRequestWrapper(
                    request, response, this.servletContext);
            SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryResponseWrapper(
                    wrappedRequest, response);

            try {
                filterChain.doFilter(wrappedRequest, wrappedResponse);
            }
            finally {
                wrappedRequest.commitSession();
            }
        }
        ```
