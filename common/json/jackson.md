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

   ![avatar](/static/image/json/json-denpendency.png)

3. 第三方模块: 数据类型模块

   - Jackson 插件模块(通过 ObjectMapper.registerModule()注册,
   - 通过添加序列化器和反序列化器来对各种常用 Java 库数据类型的支持,
   - 以便 Jackson databind 包(ObjectMapper/ObjectReader/ObjectWriter)能够顺利读写/转换这些类型
   - 如: Joda...

4. ~~第三方模块: 数据格式模块~~

   - Data format modules(数据格式模块)提供对 JSON 之外的数据格式的支持
   - 它们中的大多数只是实现 streaming API 抽象, 以便数据绑定组件可以按原样使用

5. 相关的概念

   - 流式[Streaming]: 指的是「IO 流」, 因此具有最低的开销和最快的读/写操作
   - 增量模式[incremental mode]: 它表示每个部分一个一个地往上增加, 类似于垒砖. 使用此流式 API 读写 JSON 的方式使用的「均是增量模式」
   - JsonToken: 每一部分都是一个独立的 Token[有不同类型的 Token]，最终被 `拼凑` 起来就是一个 JSON`[这是流式API里很重要的一个抽象概念]`
   - **纵观整个 Jackson, 它更多的是使用抽象类而非接口**

### JsonGenerator

![avatar](/static/image/json/jackson-impl.png)

1. sample

```java
@Test
public void testHello() throws IOException {
   JsonFactory factory = new JsonFactory();

   try (JsonGenerator jsonGenerator = factory.createGenerator(System.out, JsonEncoding.UTF8)) {
      jsonGenerator.writeStartObject(); // }
      jsonGenerator.writeStringField("name", "zack");
      jsonGenerator.writeNumberField("age", 18);
      jsonGenerator.writeEndObject(); // }
   }
}
```

2. 只负责 JSON 的生成, 至于把生成好的 JSON 写到哪里去它并不关心

3. api

![avatar](/static/image/json/jackson-type-mapping.png)

- key: key 可以独立存在[无需 value]

  1. writeFieldName(String):void
  2. void writeFieldName(SerializableString):void
  3. writeFieldId(long):void

- value: 不能单独存在/JSON 的顺序, 和你 write 的顺序保持一致/写任何类型的 Value 之前请记得先 write 写 key, 否则可能无效

  1. type: String, Number, Json, Array, boolean, null
  2. String

     - writeString(String):void
     - writeString(Reader, int):void
     - writeString(char[], int, int):void
     - writeString(SerializableString):void
     - writeRawUTF8String(byte[], int, int):void
     - writeUTF8String(byte[], int, int):void

  3. Number

     - writeNumber(short):void
     - writeNumber(int):void
     - writeNumber(long):void
     - writeNumber(BigInteger):void
     - writeNumber(double):void
     - writeNumber(float):void
     - writeNumber(BigDecimal):void
     - writeNumber(String):void
     - writeNumber(char[], int, int):void

  4. JSON

     - writeStartObject():void
     - writeStartObject(Object):void
     - writeStartObject(Object, int):void
     - writeEndObject():void

  5. Array

     - writeStartArray():void
     - writeStartArray(int):void
     - writeStartArray(Object):void
     - writeStartArray(Object, int):void
     - writeEndArray():void
     - writeArray(int[], int, int):void
     - writeArray(long[], int, int):void
     - writeArray(double[], int, int):void
     - writeArray(String[], int, int):void

  6. boolean

     - writeBoolean(boolean):void

  7. null

     - writeNull(boolean):void

- k-v: KV 一起写

  1. writeBooleanField(String, boolean):void
  2. writeNulleanField(String):void
  3. writeStringField(String, String):void
  4. writeBooleanField(String, boolean):void
  5. writeBooleanField(String, boolean):void
  6. writeNumberField(String, short/int/long/BigInteger/BigDecimal/float/double):void
  7. writeArrayFieldStart(String):void
  8. writeObjectFieldStart(String):void

- others

  1.  writeRaw/writeRawValue: 不做任何修改地逐字复制输入文本[包括不进行转义, 也不添加分隔符,即使上下文[array,object]]
  2.  writeBinary: 使用 Base64 编码把数据写进去
  3.  **writeObject**: 写 POJO, 但前提是你必须给 JsonGenerator 指定一个 ObjectCodec 解码器才能正常 work, 否则抛出异常

      - 依赖 ObjectCodec

      ```log
      java.lang.IllegalStateException: No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed cn.yourbatman.jackson.core.beans.User)

         at com.fasterxml.jackson.core.JsonGenerator._writeSimpleObject(JsonGenerator.java:2238)
         at com.fasterxml.jackson.core.base.GeneratorBase.writeObject(GeneratorBase.java:391)
         ...
      ```

  4.  writeTree:
      - 依赖 ObjectCodec
      - JsonNode: 通常只调用给定节点的 writeObject, 但添加它是为了方便起见, 并使代码在专门处理树的情况下更显式
      - 快速写/读数据
      - 可以模糊掉类型的概念: **AnnotationAttributes**
