## Redis 与缓存

### quick-start

1. depenedency

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

2. RedisTemplate

   - RedisAutoConfiguration 向容器中导入了两个类
     1. RedisTemplate<Object, Object> redisTemplate
     2. RedisTemplate<String, String> StringRedisTemplate

3. Redis 常见的五大数据类型: `String(字符串)、List(列表)、Set(集合)、Hash(散列)、ZSet(有序集合)`

   - stringRedisTemplate.opsForValue()[String(字符串)]
   - stringRedisTemplate.opsForList()[List(列表)]
   - stringRedisTemplate.opsForSet()[Set(集合)]
   - stringRedisTemplate.opsForHash()[Hash(散列)]
   - stringRedisTemplate.opsForZSet()[ZSet(有序集合)]

4. 序列化数据: redis 默认是使用 jdk 的序列化数据, 且对象必须继承序列化接口

   ```java
   @Configuration
   public class MyRedisConfig {
       // 这里必须用 GenericJackson2JsonRedisSerializer 进行value的序列化解析
       // 如果使用 Jackson2JsonRedisSerializer, 序列化的json没有 "@class": "cn.edu.ustc.springboot.bean.Employee", 在读取缓存时会报类型转换异常
       @Bean
       public org.springframework.data.redis.cache.RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
           CacheProperties.Redis redisProperties = cacheProperties.getRedis();
           org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
                   .defaultCacheConfig();
           // 设置json为序列化器
           config = config.serializeValuesWith(
                   RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
           // 设置配置文件中的各项参数
           if (redisProperties.getTimeToLive() != null) {
               config = config.entryTtl(redisProperties.getTimeToLive());
           }
           if (redisProperties.getKeyPrefix() != null) {
               config = config.prefixKeysWith(redisProperties.getKeyPrefix());
           }
           if (!redisProperties.isCacheNullValues()) {
               config = config.disableCachingNullValues();
           }
           if (!redisProperties.isUseKeyPrefix()) {
               config = config.disableKeyPrefix();
           }
           return config;
       }
   }
   ```

5. Redis 缓存原理

   - 配置类 RedisCacheConfiguration 向容器中导入了其定制的 RedisCacheManager, 在默认的 RedisCacheManager 的配置中, 是使用 jdk 序列化 value 值

     ```java
     @Configuration(proxyBeanMethods = false)
     @ConditionalOnClass(RedisConnectionFactory.class)
     @AutoConfigureAfter(RedisAutoConfiguration.class)
     @ConditionalOnBean(RedisConnectionFactory.class)
     @ConditionalOnMissingBean(CacheManager.class)
     @Conditional(CacheCondition.class)
     class RedisCacheConfiguration {

         //向容器中导入RedisCacheManager
         @Bean
         RedisCacheManager cacheManager(CacheProperties cacheProperties, CacheManagerCustomizers cacheManagerCustomizers,
                 ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                 ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                 RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
             //使用determineConfiguration()的返回值生成RedisCacheManagerBuilder
             //调用了RedisCacheManagerBuilder的cacheDefaults()方法(见下一代码块)
             RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(
                     determineConfiguration(cacheProperties, redisCacheConfiguration, resourceLoader.getClassLoader()));
             List<String> cacheNames = cacheProperties.getCacheNames();
             if (!cacheNames.isEmpty()) {
                 builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
             }
             redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
             //使用RedisCacheManagerBuilder的build()方法创建RedisCacheManager并进行定制操作
             return cacheManagerCustomizers.customize(builder.build());
         }

         private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
                 CacheProperties cacheProperties,
                 ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                 ClassLoader classLoader) {
             //如果容器中存在RedisCacheConfiguration就直接返回, 否则调用RedisCacheConfiguration()创建
             return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(cacheProperties, classLoader));
         }

         //createConfiguration()定义了其序列化value的规则
         private org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(
                 CacheProperties cacheProperties, ClassLoader classLoader) {
             Redis redisProperties = cacheProperties.getRedis();
             org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
                     .defaultCacheConfig();
             //使用jdk序列化器对value进行序列化
             config = config.serializeValuesWith(
                     SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
             //设置properties文件中设置的各项属性
             if (redisProperties.getTimeToLive() != null) {
                 config = config.entryTtl(redisProperties.getTimeToLive());
             }
             if (redisProperties.getKeyPrefix() != null) {
                 config = config.prefixKeysWith(redisProperties.getKeyPrefix());
             }
             if (!redisProperties.isCacheNullValues()) {
                 config = config.disableCachingNullValues();
             }
             if (!redisProperties.isUseKeyPrefix()) {
                 config = config.disableKeyPrefix();
             }
             return config;
         }

     }
     ```

   - RedisCacheManager 的直接构造类, 该类保存了配置类 RedisCacheConfiguration, 该配置在会传递给 RedisCacheManager

     ```java
     public static class RedisCacheManagerBuilder {
         private final RedisCacheWriter cacheWriter;
         // 默认缓存配置使用RedisCacheConfiguration的默认配置
         // 该默认配置缓存时默认将k按字符串存储, v按jdk序列化数据存储(见下一代码块)
         private RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
         private final Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
         private boolean enableTransactions;
         boolean allowInFlightCacheCreation = true;

         private RedisCacheManagerBuilder(RedisCacheWriter cacheWriter) {
             this.cacheWriter = cacheWriter;
         }

         //传 入RedisCacheManagerBuilder使用的缓存配置规则RedisCacheConfiguration类
         public RedisCacheManagerBuilder cacheDefaults(RedisCacheConfiguration defaultCacheConfiguration) {
             Assert.notNull(defaultCacheConfiguration, "DefaultCacheConfiguration must not be null!");
             this.defaultCacheConfiguration = defaultCacheConfiguration;
             return this;
         }

         // 使用默认defaultCacheConfiguration创建RedisCacheManager
         public RedisCacheManager build() {
             RedisCacheManager cm = new RedisCacheManager(cacheWriter, defaultCacheConfiguration, initialCaches,
                     allowInFlightCacheCreation);
             cm.setTransactionAware(enableTransactions);

             return cm;
         }
     }
     ```

   - RedisCacheConfiguration 保存了许多缓存规则, 这些规则都保存在 RedisCacheManagerBuilder 的 RedisCacheConfiguration defaultCacheConfiguration 属性中, 并且当 RedisCacheManagerBuilder 创建 RedisCacheManager 传递过去

     ```java
     public class RedisCacheConfiguration {

         private final Duration ttl;
         private final boolean cacheNullValues;
         private final CacheKeyPrefix keyPrefix;
         private final boolean usePrefix;
         private final SerializationPair<String> keySerializationPair;
         private final SerializationPair<Object> valueSerializationPair;
         private final ConversionService conversionService;

         //默认缓存配置
         public static RedisCacheConfiguration defaultCacheConfig(@Nullable ClassLoader classLoader) {
                 DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
                 registerDefaultConverters(conversionService);
                 return new RedisCacheConfiguration(
                     Duration.ZERO,
                     true,
                     true,
                     CacheKeyPrefix.simple(),
                     SerializationPair.fromSerializer(RedisSerializer.string()),//key使用字符串
                     SerializationPair.fromSerializer(RedisSerializer.java(classLoader)),
                     conversionService);
             //value按jdk序列化存储
         }
     }
     ```

   - RedisCacheManager 在创建 RedisCache 时将 RedisCacheConfiguration 传递过去, 并在创建 RedisCache 时通过 createRedisCache()起作用

     ```java
     public class RedisCacheManager extends AbstractTransactionSupportingCacheManager {
         private final RedisCacheWriter cacheWriter;
         private final RedisCacheConfiguration defaultCacheConfig;
         private final Map<String, RedisCacheConfiguration> initialCacheConfiguration;
         private final boolean allowInFlightCacheCreation;
         protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
             // 如果调用该方法时 RedisCacheConfiguration 有值则使用定制的, 否则则使用默认的 RedisCacheConfiguration defaultCacheConfig, 即RedisCacheManagerBuilder传递过来的配置
             return new RedisCache(name, cacheWriter, cacheConfig != null ? cacheConfig : defaultCacheConfig);
         }
     }
     ```

   - RedisCache, Redis 缓存, 具体负责将缓存数据序列化的地方, 将 RedisCacheConfiguration 的序列化对 SerializationPair 提取出来并使用其定义的序列化方式分别对 k 和 v 进行序列化操作

     ```java
     public class RedisCache extends AbstractValueAdaptingCache {
         private static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);
         private final String name;
         private final RedisCacheWriter cacheWriter;
         private final RedisCacheConfiguration cacheConfig;
         private final ConversionService conversionService;

         public void put(Object key, @Nullable Object value) {
             Object cacheValue = preProcessCacheValue(value);
             if (!isAllowNullValues() && cacheValue == null) {
                 throw new IllegalArgumentException(String.format(
                         "Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.",
                         name));
             }

             // 在put k-v时使用cacheConfig中的k-v序列化器分别对k-v进行序列化
             cacheWriter.put(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue), cacheConfig.getTtl());
         }

         // 从cacheConfig(即RedisCacheConfiguration)中获取KeySerializationPair并写入key值
         protected byte[] serializeCacheKey(String cacheKey) {
             return ByteUtils.getBytes(cacheConfig.getKeySerializationPair().write(cacheKey));
         }

         // 从cacheConfig(即RedisCacheConfiguration)中获取ValueSerializationPair并写入key值
         protected byte[] serializeCacheValue(Object value) {
             if (isAllowNullValues() && value instanceof NullValue) {
                 return BINARY_NULL_VALUE;
             }
             return ByteUtils.getBytes(cacheConfig.getValueSerializationPair().write(value));
         }
     }
     ```

   - 分析到这也就不难理解, 要使用 json 保存序列化数据时, 需要自定义 RedisCacheConfiguration, 在 RedisCacheConfiguration 中定义序列化转化规则, 并向 RedisCacheManager 传入我们自己定制的 RedisCacheConfiguration 了, 我定制的序列化规则会跟随 RedisCacheConfiguration 一直传递到 RedisCache, 并在序列化时发挥作用.
