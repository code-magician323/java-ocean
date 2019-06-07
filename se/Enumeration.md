## Enumberation

### 特性:

- 枚举类: 一个类的对象是 `有限且固定的`: `个数有限[constructor私有化]`, `属性固定[private final]`
- 在类的内部创建对象, 但需在类的外部能够访问对象且不能修改, 所以 `public static final` 修饰

### 遍历枚举类属性:

1. 枚举静态方法 values()
2. 通过反射 getEnumConstants()

```java
/**
 * 1. 枚举类遍历
 * 2. Enumeration对象, 可以使用迭代器遍历；
 *   //对Enumeration对象进行遍历, hasMoreElements()  netElement()
 */
System.out.println("第一种通过枚举静态方法 values()");
for (Sea_Enumeration2 sea :Sea_Enumeration2.values()){
    System.out.println(sea);
}

System.out.println("第二种通过反射");
Class<Sea_Enumeration2> claz = Sea_Enumeration2.class;
for(Sea_Enumeration2 sea : claz.getEnumConstants()){
    System.out.println(sea);
}
```

### 获取枚举类中的某一属性:

- valueOf()

```java
Sea_Enumeration2 s=Sea_Enumeration2.valueOf(Sea_Enumeration2.class,"SPRING");
```

### Notice:

- Enumeration 接口是 Iterator 迭代器的 "古老版本"
- 对 Enumeration 对象进行遍历: hasMoreElements() nextElement()

### 小结:

- 一个类的对象是有限且固定的, 个数有限，属性固定
- 手工定义枚举类,
  - 1. 第一行直接定义枚举常量；等价于创建对象
  - 2. 定义枚举类的变量为 final 类型变量
  - 3. 私有化构造器, 枚举对象是唯一的
  - 4. 书写 getter 方法，暴露枚举对象的成员(只读)
- 枚举类的工具
  - 遍历, 2 中方法, values()、Class.getEnumConstants()
  - 查询某一属性, Define_Enumration.valueOf(Define_Enumration.class, "SPRING");
- 实现接口, 2 种方法
  - 统一在某一个方法中提供各个枚举类对象的实现，可以使用 switch
  - 在声明对象的同时提供对象的实现,
    ```java
    SPRING("春天", "345"){
        // 这里也可以在最后重写getInfo()方法，使用 switch 一个一个判断;
        @Override
        public String getInfo() {
            return "chu";
        }
    }
    ```

## 示例代码:

- 定义 Enumeration 类

  ```java
  public enum Sea_Enumeration2 {

      //1.第一行直接定义枚举常量；等价于创建对象
      SPRING("春天", "345"),SUMMer("夏天", "678"),FALL("秋天", "91011"), WINTER("冬天", "1212");
      //2.定义枚举类的变量为final类型变量
      private  final String desc;
      private final String name;
      //3.私有化构造器：枚举对象是唯一的
      private Sea_Enumeration2(String desc, String name) {
          this.desc = desc;
          this.name = name;
      }
      //4.书写getter方法，暴露枚举对象的成员(只读)
      public String getDesc() {
          return desc;
      }
      public String getName() {
          return name;
      }
      @Override
      public String toString() {
          return "Sea_Enumeration [desc=" + desc + ", name=" + name + "]";
      }
  }
  ```

- Enumeration 实现接口

  ```java
    public interface info {
        String getInfo();
    }

    public enum Sea_Enumeration3 implements info{
        //1.第一行直接定义枚举常量；等价于创建对象
        SPRING("春天", "345"){
            @Override
            public String getInfo() {
                return "chu";

            }
        },SUMMer("夏天", "678"){
            @Override
            public String getInfo() {
                return "xia";

            }
        },FALL("秋天", "91011"){
            @Override
            public String getInfo() {
                return "qiu";

            }
        }, WINTER("冬天", "1212"){
            @Override
            public String getInfo() {
                return "dong";

            }
        };
        //2.定义枚举类的变量为final类型变量
        private  final String desc;
        private final String name;
        //3.私有化构造器：枚举对象是唯一的
        private Sea_Enumeration3(String desc, String name) {
            this.desc = desc;
            this.name = name;
        }
        //4.书写getter方法，暴露枚举对象的成员(只读)
        public String getDesc() {
            return desc;
        }
        public String getName() {
            return name;
        }
        @Override
        public String toString() {
            return "Sea_Enumeration [desc=" + desc + ", name=" + name + "]";
        }
    }
  ```
