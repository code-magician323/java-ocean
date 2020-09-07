## Jackson

1. version
   - 2.10.1
   - spring boot: 2.2.2.RELEASE
   - spring: 5.2.2.RELEASE

### 1. introduce

1. 优点

   - 高性能且稳定:
   - 流行度高: 很多框架的默认选择, **Spring 生态加持**
   - 容易使用
   - 无需自己手动创建映射
   - 干净的 JSON
   - 无三方依赖
   - 支持多种数据格式: json, Avro, BSON, CBOR, CSV, Smile, Properties, Protobuf, XML or YAML

2. 核心模块

   - Streaming 流处理模块(**jackson-core**):
     - 定义底层处理流的 API: JsonPaser 和 JsonGenerator 等, 并包含特定于 json 的实现
   - Annotations 标准注解模块(**jackson-annotations**)
     - 包含标准的 Jackson 注解
   - **Databind 数据绑定模块(jackson-databind)**:
     - 在 streaming 包上实现数据绑定(和对象序列化)支持;
     - 它依赖于上面的两个模块, 也是 Jackson 的高层 API(如 ObjectMapper)所在的模块

3. 第三方模块: 数据类型模块

   - Jackson 插件模块(通过 ObjectMapper.registerModule()注册,
   - 通过添加序列化器和反序列化器来对各种常用 Java 库数据类型的支持,
   - 以便 Jackson databind 包(ObjectMapper/ObjectReader/ObjectWriter)能够顺利读写/转换这些类型
   - 如: Joda...

4. ~~第三方模块: 数据格式模块~~
   - Data format modules(数据格式模块)提供对 JSON 之外的数据格式的支持
   - 它们中的大多数只是实现 streaming API 抽象, 以便数据绑定组件可以按原样使用
