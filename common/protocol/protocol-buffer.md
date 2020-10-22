## Protocol Buffer 详解

### protobuf 简介

1. Protocol Buffer 是一种支持多平台、多语言、可扩展的`的数据序列化机制`
2. protobuf 更小更快更简单, 支持自定义的数据结构
3. 用 protobu 编译器生成特定语言的源代码

### Message 定义

1. quick-start

   ```js
   syntax = "proto3"; // 指定proto版本，默认为proto2，而且改行必须是第一行

   // message包含多个种类的fields
   message SearchRequest {
       string query = 1;
       int32 page_number = 2;
       int32 result_per_page = 3;
   }
   ```

2. 分配 field 编号: 1-2^29

   - 这个编号是这个 field 的唯一标识
   - 标识 1-15 在编码的时候只占用一个字节, 16-2047 占用两个字节
   - `经常出现的element, 建议使用1-15来作为他们的唯一标识`; 不经常出现的使用 16-2047 标识
   - `19000-19999是系统预留的`, 不要使用

3. field 类型

   - singular: 0-1 次
   - repeated: `可以在message中重复使用[类似于数组][有序]`, 数值类型的 repeated 默认使用 packed 编码方式

4. 多 message 结构

   - 在一个 proto 文件中可以定义多个 protobuf

5. reserved field 类型

   - 确保你要删除的 field 的标识[或是名字]是 reserved
   - 具体 protobuf 的编译器会决定未来这个 field 表示能否被使用

   ```js
   // 数字标识和命名不能在同一条语句中混合声明
   message Foo {
       reserved 2, 15, 9 to 11, 200 to max;
       reserved "foo", "bar";
   }
   ```

6. 编译

   - https://developers.google.com/protocol-buffers/docs/proto3#whats_generated_from_your_proto

### 数据类型

1. [reference](https://developers.google.com/protocol-buffers/docs/proto3#scalar)

2. list

   | proto      | C++    | Java       | Python      | Go      | Ruby            | C#         | PHP     | Dart   |
   | ---------- | ------ | ---------- | ----------- | ------- | --------------- | ---------- | ------- | ------ |
   | double     | double | double     | float       | float64 | Float           | double     | float   | double |
   | float      | float  | float      | float       | float32 | Float           | float      | float   | double |
   | int32      | int32  | int        | int         | int32   | Fixnum/Bignum   | int        | integer | int    |
   | int64      | int64  | long       | int/long    | int64   | Bignum          | long       | int/str | Int64  |
   | uint32     | uint32 | int        | int/long    | uint32  | Fixnum/Bignum   | uint       | integer | int    |
   | uint64     | uint64 | long       | int/long    | uint64  | Bignum          | ulong      | int/str | Int64  |
   | sint32     | int32  | int        | int         | int32   | Fixnum/Bignum   | int        | integer | int    |
   | sint64     | int64  | long       | int/long    | int64   | Bignum          | long       | int/str | Int64  |
   | fixed32    | uint32 | int        | int/long    | uint32  | Fixnum/Bignum   | uint       | integer | int    |
   | fixed64    | uint64 | long       | int/long    | uint64  | Bignum          | ulong      | int/str | Int64  |
   | sfixed32   | int32  | int        | int         | int32   | Fixnum/Bignum   | int        | integer | int    |
   | sfixed64   | int64  | long       | int/long    | int64   | Bignum          | long       | int/str | Int64  |
   | bool       | bool   | boolean    | bool        | bool    | True/FalseClass | bool       | boolean | bool   |
   | string2^32 | string | String     | str/unicode | string  | String[UTF8]    | string     | string  | String |
   | bytes2^32  | string | ByteString | str         | []byte  | String[ASCII8]  | ByteString | string  | List   |

### 默认值

1. string/byte: null
2. bool: false
3. numberic: 0
4. enum: fisrt value, default 0
5. message: tbd
6. repeated field: null

### 枚举类型

1. quick-start

   ```js
   message SearchRequest {
       string query = 1;
       int32 page_number = 2;
       int32 result_per_page = 3;
       enum Corpus {
           UNIVERSAL = 0;
           WEB = 1;
           IMAGES = 2;
           LOCAL = 3;
           NEWS = 4;
           PRODUCTS = 5;
           VIDEO = 6;
       }
       Corpus corpus = 4;
   }
   ```

2. 枚举类型的第一个值初始化为 0
3. 也可以给不同的元素以相同的 alias, 但是需要指定`option allow_alias = true`

   ```js
   enum EnumGender {
       option allow_alias = true;
       NOT_SPECIFIED = 0;
       FEMALE = 1;
       WOMAN = 1;
       MALE = 2;
       MAN = 2;
   }
   enum EnumNotAllowingAlias {
       UNKNOWN = 0;
       STARTED = 1;
       // cause a compile error inside Google and a warning message outside.
       // RUNNING = 1;
   }
   ```

4. 在更改枚举类型 field 时, 为保证系统运行正常, 同样可以指定 reserved 数字标识和命名

### 使用其他 message 类型

1. 同文件引用

   ```js
   message SearchResponse {
       repeated Result results = 1;
   }

   message Result {
       string url = 1;
       string title = 2;
       repeated string snippets = 3;
   }
   ```

2. 不同文件引用


    ```js
    // contain Result
    import "myproject/other_protos.proto";

    message SearchResponse {
        repeated Result results = 1;
    }
    ```

### 嵌套类型

1. quick-start

   ```js
   message SearchResponse {
       message Result {
           string url = 1;
           string title = 2;
           repeated string snippets = 3;
       }
       repeated Result results = 1;
   }

   message Outer {                     // Level 0
       message MiddleAA {              // Level 1
           message Inner {             // Level 2
               int64 ival = 1;
               bool  booly = 2;
           }
       }
       message MiddleBB {              // Level 1
           message Inner {             // Level 2
               int32 ival = 1;
               bool  booly = 2;
           }
       }
   }
   ```

### 更新 message 类型

- 当现有的 message 已经无法满足现有业务需要, 你需要更新你的 message 类型以支持更复杂的业务, 这就涉及到`向后兼容`的问题了, 为保证已有服务不受影响, 需要遵守以下的一些规定

1. 不要更改已经存在的 fields 的数字标识
2. 如果添加新的 field

   - 利用旧代码序列化得到的 message 可以使用新的代码进行解析, 你需要记住各个元素的默认值
   - 新代码创建的 field 同样可以由旧代码进行加解析

3. field 可以被删除

   - 但是需要保证其对应的数字标识不再被使用
   - 可以通过加前缀的方式来重新使用这个 field name
   - 或者指定数字标识为 reserved 来避免这种情况

4. int32/int64/uint32/uint64/bool 这些类型都是互相兼容的, 并不会影响前向/后向兼容性
5. sint32 和 sint64 之间是互相兼容的, 但是和其他数字类型是不兼容的
6. string 和 bytes 是互相兼容的, 只要使用的是 UTF-8 编码
7. 如果 byte 包含 message 的编码版本, 则嵌套的 message 和 bytes 兼容
8. flexed32 兼容 sfixed32/fixed64/sfixed64
9. enum 兼容 int32/uint32/int64/uint64
   - 对于这个值在转化时, 不同语言的客户端处理方式会有所不同

---

### tutorials

1. [reference](https://developers.google.com/protocol-buffers/docs/tutorials)
