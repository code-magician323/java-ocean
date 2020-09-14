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
   - JsonToken: 每一部分都是一个独立的 Token[有不同类型的 Token], 最终被 `拼凑` 起来就是一个 JSON`[这是流式API里很重要的一个抽象概念]`
   - **纵观整个 Jackson, 它更多的是使用抽象类而非接口**

### JsonGenerator: 负责向目的地写数据

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

4. 输出漂亮的 JSON 格式: `jsonGenerator.useDefaultPrettyPrinter();`

   ```java
   // 自己指定漂亮格式打印器
   public JsonGenerator setPrettyPrinter(PrettyPrinter pp) { ... }

   // 应用默认的漂亮格式打印器
   public abstract JsonGenerator useDefaultPrettyPrinter();
   ```

5. 序列化 POJO 对象

   ```java
   // ObjectMapper 是一个解码器, 实现了序列化和反序列化、树模型等
   public abstract JsonGenerator setCodec(ObjectCodec oc);
   ```

### JsonGenerator#Feature:

1. 控制 Jackson 的读/写行为, 类似于 Spring 使用 Environment/PropertySource 管理配置

   ```java
   // 枚举值均为bool类型, 括号内为默认值
   public enum Feature {
      // 底层I/O流相关
      AUTO_CLOSE_TARGET(true), // 自动关闭流
      AUTO_CLOSE_JSON_CONTENT(true), // 自动补齐[闭合]JsonToken#START_ARRAY和JsonToken#START_OBJECT, 但是请务必自己写
      FLUSH_PASSED_TO_STREAM(true), // 调用JG close()/flush() 方法时, 自动强刷I/O流里面的数据

      // 双引号""引用相关
      @Deprecated // JsonWriteFeature#QUOTE_FIELD_NAMES
      QUOTE_FIELD_NAMES(true), // 字段名使用""括起来[遵循JSON规范]
      @Deprecated // JsonWriteFeature#WRITE_NAN_AS_STRINGS
      QUOTE_NON_NUMERIC_NUMBERS(true), // Number 中不是数字的加""
      @Deprecated // JsonWriteFeature#ESCAPE_NON_ASCII
      ESCAPE_NON_ASCII(false), // true 会将字符转换为 ASCII
      @Deprecated // JsonWriteFeature#WRITE_NUMBERS_AS_STRINGS
      WRITE_NUMBERS_AS_STRINGS(false), // true 所有数字「强制」写为字符串

      // 约束/规范/校验相关
      WRITE_BIGDECIMAL_AS_PLAIN(false), // true 使用 BigDecimal#toPlainString() 方法输出
      STRICT_DUPLICATE_DETECTION(false),  // true 时有相同的key则抛出JsonParseException异常; false: 相同字段名都要解析
      IGNORE_UNKNOWN(false);
            // 如果「底层数据格式」需要输出所有属性, 以及如果「找不到」调用者试图写入的属性的定义, 则该特性确定是否要执行的操作
            // true: 可以预先调用[在写数据之前]这个API设定好模式信息: JsonGenerator#setSchema
            // false: 禁用该功能, 如果底层数据格式需要所有属性的知识才能输出, 那就抛出 JsonProcessingException 异常
      ...
   }
   ```

2. 不同的 JsonGenerator 设置不同的 Feature 配置

   ```java
   // 开启
   public abstract JsonGenerator enable(Feature f);
   // 关闭
   public abstract JsonGenerator disable(Feature f);
   // 开启/关闭
   public final JsonGenerator configure(Feature f, boolean state) { ... };
   public abstract boolean isEnabled(Feature f);
   public boolean isEnabled(StreamWriteFeature f) { ... };
   ```

3. trending: 使用 JsonFactory#StreamWriteFeature 替换 JsonGenerator#Feature
   - 因为 JsonGenerator 并不局限于写 JSON, 因此把 Feature 放在 JsonGenerator 作为内部类是不太合适的

### JsonParser: convert json to bean[负责从一个 JSON 字符串中提取出值]

- JsonParser 针对不同的 value 类型, 提供了非常多的方法用于实际值的获取
- 最终工作的是: `UTF8StreamJsonParser/ReaderBasedJsonParser`
- 无需指定编码: 本身自带的 `ByteSourceJsonBootstrapper#detectEncoding()`

1. 「直接」值获取

   ```java
   // 获取字符串类型
   public abstract String getText() throws IOException;

   // 数字 Number 类型值 标量值[支持的Number类型参照NumberType枚举]
   public abstract Number getNumberValue() throws IOException;
   public enum NumberType {
      INT, LONG, BIG_INTEGER, FLOAT, DOUBLE, BIG_DECIMAL
   };

   // 如果value值是null, 像 getIntValue()/getBooleanValue() 会抛出异常的, 但getText()不会
   public abstract int getIntValue() throws IOException;
   public abstract long getLongValue() throws IOException;
   ...
   public abstract byte[] getBinaryValue(Base64Variant bv) throws IOException;
   ```

2. 「带默认值」的值获取: 具有更好安全性

   ```java
   // 此类方法若碰到数据的转换失败时,「不会抛出异常」, 把def作为默认值返回
   public String getValueAsString() throws IOException {
      return getValueAsString(null);
   }
   public abstract String getValueAsString(String def) throws IOException;
   ...
   public long getValueAsLong() throws IOException {
      return getValueAsLong(0);
   }
   public abstract long getValueAsLong(long def) throws IOException;
   ...
   ```

3. 组合方法

   ```java
   JsonToken nextToken() throws IOException;
   JsonToken nextValue() throws IOException;
   boolean nextFieldName(SerializableString str) throws IOException;
   String nextFieldName() throws IOException;
   String nextTextValue() throws IOException;
   int nextIntValue(int defaultValue) throws IOException;
   long nextLongValue(long defaultValue) throws IOException;
   Boolean nextBooleanValue() throws IOException;
   ```

4. 自动绑定: **必须依赖于 ObjectCodec 去实现**

   ```java
   <T> T readValueAs(Class<T> valueType) throws IOException;
   <T> T readValueAs(TypeReference<?> valueTypeRef) throws IOException;
   <T> Iterator<T> readValuesAs(Class<T> valueType) throws IOException ;
   <T> Iterator<T> readValuesAs(TypeReference<T> valueTypeRef) throws IOException;
   public <T extends TreeNode> T readValueAsTree() throws IOException {
        return (T) _codec().readTree(this);
    }
   ```

5. sample: read json as bean

   ```java
   public class UserObjectCodec extends ObjectCodec {
      @SneakyThrows
      @Override
      public <T> T readValue(JsonParser jsonParser, Class<T> aClass) throws IOException {
         User user = (User) aClass.newInstance();
         while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            if ("name".equals(fieldName)) {
            jsonParser.nextToken();
            user.setName(jsonParser.getText());
            } else if ("age".equals(fieldName)) {
            jsonParser.nextToken();
            user.setAge(jsonParser.getIntValue());
            }
         }

         return (T) user;
      }
      // other method
   }

   @Test
   public void testObjectCodec() throws IOException {
      String jsonStr = "{\"name\":\"zack\",\"age\":18}";
      JsonFactory factory = JsonFactory.builder().build();
      try (JsonParser parser = factory.createParser(jsonStr)) {
         parser.setCodec(new UserObjectCodec());
         User user = parser.readValueAs(User.class);
         System.out.println(user);
      }
   }
   ```

### JsonParser#Feature

1. source code

   ```java
   public enum Feature {
      AUTO_CLOSE_SOURCE(true), // 自动关闭流

      ALLOW_COMMENTS(false), // 是否允许/* */或者 // 这种类型的注释出现
      ALLOW_YAML_COMMENTS(false), // 开启后将支持 Yaml 格式的的注释, 也就是#形式的注释语法
      ALLOW_UNQUOTED_FIELD_NAMES(false), // 是否允许属性名「不带双引号""」
      ALLOW_SINGLE_QUOTES(false), // 是否允许属性名支持单引号, 也就是使用''包裹
      @Deprecated // JsonReadFeature#ALLOW_UNESCAPED_CONTROL_CHARS
      ALLOW_UNQUOTED_CONTROL_CHARS(false), // 是否允许JSON字符串包含非引号「控制字符」: 不可打印字符[ASCII 0~32号]
      @Deprecated // JsonReadFeature#ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER
      ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER(false), // 是否允许 反斜杠 转义任何字符
      @Deprecated // JsonReadFeature#ALLOW_LEADING_ZEROS_FOR_NUMBERS
      ALLOW_NUMERIC_LEADING_ZEROS(false), // 是否允许像000016这样的"数字"出现, true: 16
      @Deprecated // JsonReadFeature#ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS
      ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS(false), // 是否允许小数点.打头, 也就是说 .1 这种小数格式是否合法
      @Deprecated // JsonReadFeature#ALLOW_NON_NUMERIC_NUMBERS
      ALLOW_NON_NUMERIC_NUMBERS(false), // 是否允许一些解析器识别一组**"非数字"(如NaN)**作为合法的浮点数值
      @Deprecated // JsonReadFeature#ALLOW_MISSING_VALUES
      ALLOW_MISSING_VALUES(false), // 是否允许支持「JSON数组中」元素值: 如 [value1, , value3]
      @Deprecated // JsonReadFeature#ALLOW_TRAILING_COMMA
      // [true,true,] 等价于 [true, true]
      // {"a": true,} 等价于 {"a": true}
      ALLOW_TRAILING_COMMA(false), // 是否允许最后一个多余的逗号[一定是最后一个] // 优先级高

      STRICT_DUPLICATE_DETECTION(false), // 是否允许JSON串有两个相同的属性key, 默认是「允许的」
      IGNORE_UNDEFINED(false), // 是否忽略「没有定义」的属性key
      // JsonParser:
            // public void setSchema(FormatSchema schema) {
            //    ...
            // }
      INCLUDE_SOURCE_IN_LOCATION(true);// 是否构建 JsonLocation 对象来表示每个 part 的来源, 你可以通过 JsonParser#getCurrentLocation() 来访问
   }
   ```

2. trending
   - replaced by `JsonReadFeature`

### JsonToken: 解析 JSON 内容时, 用于返回结果的基本「标记类型」的枚举

1. source code

   ```java
   public enum JsonToken {
      NOT_AVAILABLE(null, JsonTokenId.ID_NOT_AVAILABLE),

      START_OBJECT("{", JsonTokenId.ID_START_OBJECT),
      END_OBJECT("}", JsonTokenId.ID_END_OBJECT),
      START_ARRAY("[", JsonTokenId.ID_START_ARRAY),
      END_ARRAY("]", JsonTokenId.ID_END_ARRAY),

      // 属性名（key）
      FIELD_NAME(null, JsonTokenId.ID_FIELD_NAME),

      // 值（value）
      VALUE_EMBEDDED_OBJECT(null, JsonTokenId.ID_EMBEDDED_OBJECT),
      VALUE_STRING(null, JsonTokenId.ID_STRING),
      VALUE_NUMBER_INT(null, JsonTokenId.ID_NUMBER_INT),
      VALUE_NUMBER_FLOAT(null, JsonTokenId.ID_NUMBER_FLOAT),
      VALUE_TRUE("true", JsonTokenId.ID_TRUE),
      VALUE_FALSE("false", JsonTokenId.ID_FALSE),
      VALUE_NULL("null", JsonTokenId.ID_NULL),
   }
   ```

### JsonFactory

1. JsonFactory is thread safe and UTF-8 is default encoding

2. source code

   ```java
    @Override
    public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);

        if (enc == JsonEncoding.UTF8) {
            return _createUTF8Generator(_decorate(out, ctxt), ctxt);
        }
        // 使用指定的编码把OutputStream包装为一个writer
        Writer w = _createWriter(out, enc, ctxt);
        return _createGenerator(_decorate(w, ctxt), ctxt);
    }
   ```

3. feature:
   - create json-generate
   - create json-parser
   - 可以定制化创建

### JsonFactory#Feature

1. code

   ```java
   public enum Feature {
      // 对JSON的「字段名」是否调用String#intern方法, 放进字符串常量池里,
      INTERN_FIELD_NAMES(true), // InternCache extends ConcurrentHashMap
      // 是否需要规范化属性名, 简而言之会根据Hash值来计算每个属性名存放的位置
      CANONICALIZE_FIELD_NAMES(true),
      // 当ByteQuadsCanonicalizer处理hash碰撞达到一个阈值时,  是否快速失败
      FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
      // 是否使用 BufferRecycler/ThreadLocal/SoftReference 来有效的「重用」底层的输入/输出缓冲区
      USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true)
   }
   ```

2. SPI 创建

   ```java
   com\fasterxml\jackson\core\jackson-core\2.10.1\jackson-core-2.10.1.jar!\META-INF\services
   ```

### ObjectMapper

1. function

   - 读取和写入 JSON 的功能: 普通 POJO 的序列化/反序列化 + JSON 树模型的读/写
   - 定制处理不同风格的 JSON: Feature+ com.fasterxml.jackson.databind.Module
   - 支持多态泛型、对象标识
   - 工厂: 创建 ObjectReader 和 ObjectWriter 的

2. ObjectMapper usage

   - writeValue(): 序列化
     1. writeValue(File resultFile, Object value): 写到目标文件里
     2. writeValue(OutputStream out, Object value): 写到输出流
     3. String writeValueAsString(Object value): 写成字符串形式，此方法最为常用」
     4. writeValueAsBytes(Object value): 写成字节数组 byte[]
   - readValue(): 反序列 + `不能反序列化一个 "单纯的" 字符串·
     1. readValue(String content, Class<T> valueType): 读为指定 class 类型的对象, 此方法最常用
     2. readValue(String content, TypeReference<T> valueTypeRef): T 表示泛型类型, 如 List<T>这种类型, 一般用于集合/Map 的反序列化
     3. readValue(String content, JavaType valueType): Jackson 内置的 JavaType 类型, 后再详解（使用并不多）

3. 泛型擦除问题

   - 定义
     - Java 在编译时会在字节码里指令集之外的地方保留「部分」泛型信息
     - **泛型接口**、`类`、**方法**定义上的所有泛型、`成员变量声明处`的泛型「都会」被保留类型信息,「其它地方」的泛型信息都会被擦除
   - solution
     - 利用成员变量保留泛型
     - TypeReference<T>

4. tree
   - 序列化
     1. valueToTree(Object)
     2. writeTree(JsonGenerator, JsonNode)
     3. writeTree(JsonGenerator,TreeNode)
   - 反序列化
     1. readTree(String)

### Jackson 用树模型

1. function

   - JSON 串中我只想要某些属性的值
   - 临时使用没有 POJO 与之对应
   - 数据结构高度「动态化」

2. 树模型是 JSON 数据内存树的表示形式

   - JsonNodeFactory: 用来构造各种 JsonNode 节点的工厂
   - JsonNode: 表示 json 节点
   - ObjectMapper: 实现 JsonNode 和 JSON 字符串的互转

3. JsonNode

   ![avatar](/static/image/common/json/jackson-jsonNode.png)

   - 绝大多数的 get 方法均放在了此抽象类里
   - 大多数的修改方法都必须通过特定的子类类型去调用
   - code

   ```java
   public abstract class JsonNode extends JsonSerializable.Base implements TreeNode, Iterable<JsonNode> {
      ...
   }
   ```

4. JsonNodeFactory

   - ValueNode: 一个节点表示一个值

   - ContainerNode: 本节点代表一个容器, 里面可以装任何其它节点
     1. ObjectNode: 类比 Map, 采用 K-V 结构存储
     2. ArrayNode: 类比 Collection、数组

---

## conlusion

1. 若工程中遇到 objectMapper.readValue(xxx, List.class)这种代码, 那肯定是有安全隐患的

---

## reference

1. https://github.com/yourbatman/jackson-learning
