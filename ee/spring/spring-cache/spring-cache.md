## Spring Boot 与缓存

### JSR107

1. Java Caching 定义了 5 个核心接口

   - CachingProvider: 定义了创建, 配置, 获取, 管理和控制多个 CacheManager
   - 一个应用可以在运行期访问多个 CachingProvider

2. CacheManager

   - 定义了创建, 配置, 获取, 管理和控制多个唯一命名的 Cache
   - 这些 Cache 存在于 CacheManager 的上下文中
   - 一个 CacheManager 仅被一个 CachingProvider 所拥有

3. Cache

   - 一个类似 Map 的数据结构并临时存储以 Key 为索引的值
   - 一个 Cache 仅被一个 CacheManager 所拥有

4. Entry

   - 一个存储在 Cache 中的 key-value 对

5. Expiry

   - 每一个存储在 Cache 中的条目有一个定义的有效期
   - 一旦超过这个时间, 条目为过期的状态
   - 一旦过期, 条目将不可访问, 更新和删除
   - 缓存有效期可以通过 ExpiryPolicy 设置

![avatar](/static/image/spring/cache-arth.png)

### Spring 缓存抽象

1. Spring 从 3.1 开始定义了 `org.springframework.cache.Cache` 和 `org.springframework.cache.CacheManager` 接口来统一不同的缓存技术

2. Cache 接口有以下功能：

   - 为缓存的组件规范定义, 包含缓存的各种操作集合;
   - Spring 提供了各种 xxxCache 的实现;
     1. RedisCache
     2. EhCacheCache
     3. ConcurrentMapCache 等

![avatar](/static/image/spring/cache-arth-manager.png)

### 重要缓存注解及概念

| Cache          | 缓存接口, 定义缓存操作             |
| :------------- | ---------------------------------- |
| CacheManager   | 缓存管理器: 管理 Cache 组件        |
| @Cacheable     | 根据方法的请求参数对其结果进行缓存 |
| @CacheEvict    | 清空缓存                           |
| @CachePut      | 更新缓存                           |
| @EnableCaching | 开启基于注解的缓存                 |
| keyGenerator   | 缓存数据时 key 生成策略            |
| serialize      | 缓存数据时 value 序列化策略        |

1. `@Cacheable/@CachePut/@CacheEvict` 主要的参数

   - value: 缓存名称, 字符串/字符数组形式;

     - eg: `@Cacheable(value="mycache")`
     - eg: `@Cacheable(value={"cache1","cache2"}`

   - key 缓存的 key, 需要按照 SpEL 表达式编写, 如果不指定则按照方法所有参数进行组合;

     - eg: `@Cacheable(value="testcache",key="#userName")`

   - keyGenerator: key 的生成器; 可以自己指定 key 的生成器的组件 id

   - 注意：key/keyGenerator：二选一使用;

   - condition: 缓存条件, 使用 SpEL 编写, 在调用方法之前之后都能判断;

     - eg: `@Cacheable(value="testcache",condition="#userName.length()>2")`

   - unless(@CachePut, @Cacheable)用于否决缓存的条件, 只在方法执行之后判断;

     - eg: `@Cacheable(value="testcache",unless="#result ==null")`

   - beforeInvocation(@CacheEvict) 是否在执行前清空缓存, 默认为 false, false 情况下方法执行异常则不会清空;

     - eg: `@CachEvict(value="testcache", beforeInvocation=true)`

   - allEntries(@CacheEvict)是否清空所有缓存内容, 默认为 false;
     - eg: `@CachEvict(value="testcache",allEntries=true)`

2. 缓存可用的 SpEL 表达式

   - root: 表示根对象, 不可省略

     1. 被调用方法名 methodName: `#root.methodName`
     2. 被调用方法 method: `#root.method.name`
     3. 目标对象 target: `#root.target`
     4. 被调用的目标对象类 targetClass: `#root.targetClass`
     5. 被调用的方法的参数列表 args: `#root.args[0]`
     6. 方法调用使用的缓存列表 caches: `#root.caches[0].name`

   - 参数名: 方法参数的名字.

     1. `#参数名, #p0 或 #a0`,
     2. `0` 代表参数的索引
     3. `#iban, #a0, #p0`

   - 方法执行后的返回值
     1. 仅当方法执行之后的判断有效, 如'unless', @CachePut, @CacheEvict 的表达式 beforeInvocation=false
     2. eg: `#result`

3. 定义每个 key 的 TTL

   ```java
   // 可以在这里指定某个 key 的过期时间
   @Bean
   RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
       return builder -> {
           Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
           configurationMap.put(GlobalCacheContants.MODULE_MCDONALDS_ALLSTART_PHASE_KEY,
                   RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
           builder.withInitialCacheConfigurations(configurationMap);
       };
   }
   ```

4. 空值缓存的问题: unless 成立则不缓存

   ```java
   // list
   @Cacheable(value = GlobalCacheContants.MODULE_MCDONALDS_ALLSTART_PHASE_KEY, key = "'list'" , unless = "#result.data.size() == 0")

   // object
   @Cacheable(value = GlobalCacheContants.MODULE_MCDONALDS_ALLSTART_PHASE_KEY, key = "#id", unless = "#result.data.id == null")

   // raw string
   @CacheEvict(value = GlobalCacheContants.MODULE_MCDONALDS_ALLSTART_PHASE_KEY, key = "'list'")
   ```

### quick-start

1. dependency

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-cache</artifactId>
   </dependency>
   ```

2. @EnableCaching 开启缓存

3. sample

   - `@Cacheable, @CachePut, @CacheEvict的使用`

     ```Java
     @Service
     public class EmployeeService {
         @Autowired
         private EmployeeMapper employeeMapper;

         @Cacheable(value={"emp"},
                 key = "#id+#root.methodName+#root.caches[0].name",
                 condition = "#a0>1",
                 unless = "#p0==2"
         )
         public Employee getEmpById(Integer id) {
             System.out.println("查询员工："+id);
             return employeeMapper.getEmpById(id);
         }

         @CachePut(value = {"emp"},key = "#employee.id" )
         public Employee updateEmp(Employee employee) {
             System.out.println("更新员工"+employee);
             employeeMapper.updateEmp(employee);
             return employee;
         }

         @CacheEvict(value = {"emp"},allEntries = true,beforeInvocation = true)
         public Integer delEmp(Integer id){
             int i=1/0;
             System.out.println("删除员工："+id);
             employeeMapper.delEmp(id);
             return id;
         }

         @Caching(
             cacheable = {
                 @Cacheable(/*value="emp",*/key = "#lastName")
             },
             put = {
                 @CachePut(/*value="emp",*/key = "#result.id"),
                 @CachePut(/*value="emp",*/key = "#result.email")
             }
         )
         public Employee getEmpByLastName(String lastName){
             return employeeMapper.getEmpByLastName(lastName);
         }
     }
     ```

   - 自定义 KeyGenerator

     ```Java
     @Configuration
     public class MyCacheConfig {
         @Bean("myKeyGenerator")
         public KeyGenerator myKeyGenerator() {
             return new KeyGenerator(){
                 @Override
                 public Object generate(Object target, Method method, Object... params) {
                     return method.getName()+"["+ Arrays.asList(params).toString()+"----"+target+"]";
                 }
             };
         }
     }
     ```

4. 工作原理

   - 缓存的自动配置类 `CacheAutoConfiguration` 向容器中导入了 `CacheConfigurationImportSelector`, 此类的 `selectImports()` 方法添加了许多配置类, 其中 `SimpleCacheConfiguration` 默认生效

     1. GenericCacheConfiguration​
     2. JCacheCacheConfiguration​
     3. EhCacheCacheConfiguration​
     4. HazelcastCacheConfiguration​
     5. InfinispanCacheConfiguration​
     6. CouchbaseCacheConfiguration​
     7. RedisCacheConfiguration​
     8. CaffeineCacheConfiguration​
     9. GuavaCacheConfiguration​
     10. **`SimpleCacheConfiguration[默认]`**
     11. NoOpCacheConfiguration

     ```java
     @Import({ CacheConfigurationImportSelector.class, CacheManagerEntityManagerFactoryDependsOnPostProcessor.class })
     public class CacheAutoConfiguration {
         static class CacheConfigurationImportSelector implements ImportSelector {
             @Override
             public String[] selectImports(AnnotationMetadata importingClassMetadata) {
                 CacheType[] types = CacheType.values();
                 String[] imports = new String[types.length];
                 for (int i = 0; i < types.length; i++) {
                     // 将即将导入的各配置类存入字符数组内
                     imports[i] = CacheConfigurations.getConfigurationClass(types[i]);
                 }
                 return imports;
             }
         }
     }
     ```

   - `SimpleCacheConfiguration` 向容器中导入了 `ConcurrentMapCacheManager`

     ```java
     @Configuration(proxyBeanMethods = false)
     @ConditionalOnMissingBean(CacheManager.class)
     @Conditional(CacheCondition.class)
     // ConcurrentMapCacheManager 使用 ConcurrentMap 以 k-v 的方式存储缓存缓存
     class SimpleCacheConfiguration {
         // 向容器中导入 ConcurrentMapCacheManager
         @Bean
         ConcurrentMapCacheManager cacheManager(CacheProperties cacheProperties,
                 CacheManagerCustomizers cacheManagerCustomizers) {
             ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
             List<String> cacheNames = cacheProperties.getCacheNames();
             if (!cacheNames.isEmpty()) {
                 cacheManager.setCacheNames(cacheNames);
             }
             return cacheManagerCustomizers.customize(cacheManager);
         }
     }
     ```

   - @Cacheable 运行流程:

     1. 方法运行之前, 先去查询 Cache(缓存组件), 按照 cacheNames 指定的名字获取; (CacheManager 先获取相应的缓存), 第一次获取缓存如果没有 Cache 组件会自动创建,并以 cacheNames-cache 对放入 ConcurrentMap.

        ```java
        public class ConcurrentMapCacheManager implements CacheManager, BeanClassLoaderAware {
            private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

            private boolean dynamic = true;

            // 获取缓存
            public Cache getCache(String name) {
                Cache cache = this.cacheMap.get(name);
                // 如果没有缓存会自动创建
                if (cache == null && this.dynamic) {
                    synchronized (this.cacheMap) {
                        cache = this.cacheMap.get(name);
                        if (cache == null) {
                            cache = createConcurrentMapCache(name);
                            this.cacheMap.put(name, cache);
                        }
                    }
                }
                return cache;
            }
        }
        ```

     2. 去 Cache 中查找缓存的内容, 使用一个 key, 默认就是方法的参数; key 是按照某种策略生成的; 默认是使用 keyGenerator 生成的, 默认使用 SimpleKeyGenerator[默认策略]生成 key;

        ```java
        public class SimpleKeyGenerator implements KeyGenerator {
            // SimpleKeyGenerator的生成规则
            public static Object generateKey(Object... params) {
                // 若无参, 则返回空key
                if (params.length == 0) {
                    return SimpleKey.EMPTY;
                }
                if (params.length == 1) {
                    Object param = params[0];
                    if (param != null && !param.getClass().isArray()) {
                        // 1个参数, 则直接返回该参数
                        return param;
                    }
                }
                // 多个参数返回数组
                return new SimpleKey(params);
            }
        }
        ```

     3. 没有查到缓存就调用目标方法;
     4. 将目标方法返回的结果, 放进缓存中

     5. 核心: `在 @Cacheable 标注方法执行前执行 CacheAspectSupport 的execute() 方法, 在该方法中会以一定的规则生成key, 并尝试在缓存中通过该key获取值, 若通过key获取到值则直接返回, 不用执行@Cacheable标注方法, 否则执行该方法获得返回值`

        ```java
        public abstract class CacheAspectSupport extends AbstractCacheInvoker
                implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {
            //在执行@Cacheable标注的方法前执行此方法
            @Nullable
            private Object execute(final CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
                if (contexts.isSynchronized()) {
                    CacheOperationContext context = contexts.get(CacheableOperation.class).iterator().next();
                    if (isConditionPassing(context, CacheOperationExpressionEvaluator.NO_RESULT)) {
                        Object key = generateKey(context, CacheOperationExpressionEvaluator.NO_RESULT);
                        Cache cache = context.getCaches().iterator().next();
                        try {
                            return wrapCacheValue(method, cache.get(key, () -> unwrapReturnValue(invokeOperation(invoker))));
                        }
                        catch (Cache.ValueRetrievalException ex) {
                            throw (CacheOperationInvoker.ThrowableWrapper) ex.getCause();
                        }
                    }
                    else {
                        return invokeOperation(invoker);
                    }
                }

                processCacheEvicts(contexts.get(CacheEvictOperation.class), true,
                        CacheOperationExpressionEvaluator.NO_RESULT);

                // 见findCachedItem方法
                //此方法通过一定规则生成的key找cache, 若没找到则返回null
                Cache.ValueWrapper cacheHit = findCachedItem(contexts.get(CacheableOperation.class));

                List<CachePutRequest> cachePutRequests = new LinkedList<>();
                if (cacheHit == null) {
                    collectPutRequests(contexts.get(CacheableOperation.class),
                            CacheOperationExpressionEvaluator.NO_RESULT, cachePutRequests);
                }

                Object cacheValue;
                Object returnValue;

                if (cacheHit != null && !hasCachePut(contexts)) {
                    // 如果通过该key找到缓存, 且无@cacheput,则直接返回cacheValue
                    cacheValue = cacheHit.get();
                    returnValue = wrapCacheValue(method, cacheValue);
                }
                else {
                    // 若通过该key未找到缓存, 则执行@cacheable标注方法
                    returnValue = invokeOperation(invoker);
                    cacheValue = unwrapReturnValue(returnValue);
                }

                // Collect any explicit @CachePuts
                collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);

                // Process any collected put requests, either from @CachePut or a @Cacheable miss
                for (CachePutRequest cachePutRequest : cachePutRequests) {
                    cachePutRequest.apply(cacheValue);
                }

                // Process any late evictions
                processCacheEvicts(contexts.get(CacheEvictOperation.class), false, cacheValue);

                return returnValue;
            }

            @Nullable
            private Cache.ValueWrapper findCachedItem(Collection<CacheOperationContext> contexts) {
                Object result = CacheOperationExpressionEvaluator.NO_RESULT;
                for (CacheOperationContext context : contexts) {
                    if (isConditionPassing(context, result)) {
                        //通过一定规则生成key值(生成规则见generateKey方法)
                        Object key = generateKey(context, result);
                        //通过生成的key寻找缓存
                        Cache.ValueWrapper cached = findInCaches(context, key);
                        if (cached != null) {
                            return cached;
                        }
                        else {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No cache entry for key '" + key + "' in cache(s) " + context.getCacheNames());
                            }
                        }
                    }
                }
                return null;
            }

            //key的生成策略
            @Nullable
            protected Object generateKey(@Nullable Object result) {
                //如果@Cacheable设置了属性key, 则根据设置值生成key
                if (StringUtils.hasText(this.metadata.operation.getKey())) {
                    EvaluationContext evaluationContext = createEvaluationContext(result);
                    return evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
                }
                //否则使用keyGenerator生成key, 默认keyGenerator为SimpleKeyGenerator
                return this.metadata.keyGenerator.generate(this.target, this.metadata.method, this.args);
            }
        }
        ```
