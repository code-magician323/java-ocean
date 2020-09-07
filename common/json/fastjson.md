## 优点

1. API 简单[static 方法直接使用], 上手快, 对开发者友好

   ```java
   String text = JSON.toJSONString(obj); //序列化
   VO vo = JSON.parseObject("{...}", VO.class); //反序列化
   ```

2. 阿里巴巴出品, 背靠大厂值得信赖.
3. 社区相对活跃, 维护升级有保障
4. 速度快 + 性能高: [测试](https://zhuanlan.zhihu.com/p/99123002)
5. 测试完备
6. 功能完备

## test performance

1. dimensions

   - 字符串解析成 JSON 性能
   - 字符串解析成 JavaBean 性能
   - JavaBean 构造 JSON 性能
   - 集合构造 JSON 性能
   - 易用性

2. lib

   - **Gson**
   - FastJson
   - Jackson

3. result

- 样本

  ```java
  public class Person {
     private String name;
     private FullName fullName;
     private int age;
     private Date birthday;
     private List<String> hobbies;
     private Map<String, String> clothes;
     private List<Person> friends;
  }
  public class FullName {
     private String firstName;
     private String middleName;
     private String lastName;
  }
  ```

- 序列化

  ```js
  Benchmark                            (count)  Mode  Cnt  Score   Error  Units
  JsonSerializeBenchmarkTest.FastJson     1000    ss       0.264           s/op
  JsonSerializeBenchmarkTest.FastJson    10000    ss       0.604           s/op
  JsonSerializeBenchmarkTest.FastJson   100000    ss       1.212           s/op
  JsonSerializeBenchmarkTest.Gson         1000    ss       0.148           s/op
  JsonSerializeBenchmarkTest.Gson        10000    ss       0.339           s/op
  JsonSerializeBenchmarkTest.Gson       100000    ss       1.639           s/op
  JsonSerializeBenchmarkTest.Jackson      1000    ss       0.347           s/op
  JsonSerializeBenchmarkTest.Jackson     10000    ss       0.404           s/op
  JsonSerializeBenchmarkTest.Jackson    100000    ss       0.768           s/op
  ```

- 反序列化

  ```js
  Benchmark                              (count)  Mode  Cnt  Score   Error  Units
  JsonDeserializeBenchmarkTest.FastJson     1000    ss       0.315           s/op
  JsonDeserializeBenchmarkTest.FastJson    10000    ss       0.386           s/op
  JsonDeserializeBenchmarkTest.FastJson   100000    ss       1.323           s/op
  JsonDeserializeBenchmarkTest.Gson         1000    ss       0.156           s/op
  JsonDeserializeBenchmarkTest.Gson        10000    ss       0.282           s/op
  JsonDeserializeBenchmarkTest.Gson       100000    ss       0.995           s/op
  JsonDeserializeBenchmarkTest.Jackson      1000    ss       0.423           s/op
  JsonDeserializeBenchmarkTest.Jackson     10000    ss       0.474           s/op
  JsonDeserializeBenchmarkTest.Jackson    100000    ss       1.070           s/op
  ```
