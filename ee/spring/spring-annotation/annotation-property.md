## Property Value

1. @Value

- xml configuration

  ```xml
  <context:property-placeholder location="cat.properties"/>
  <bean id="cat" class="cn.edu.ntu.javaee.annotation.model.Cat">
      <property name="age" value="18"/>
      <property name="name" value="zack"/>
      <property name="color" value="${color}"/>
      <property name="owner" value="${owner:mars}"/>
  </bean>
  ```

- annotation

  - model

    ```java
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public class Cat {

        @Value("#{20-15}")
        private Integer age;

        @Value("zack")
        private String name;

        @Value("${color:blue}")
        private String color;

        @Value("${os.name}")
        private String os;

        private String owner;

        private static String weight;

        public static String getWeight() {
            return weight;
        }

        @Value("${weight}")
        public void setWeight(String weight) {
            Cat.weight = weight;
        }
    }
    ```

  - configuration

  ```java
    // <code>@PropertySource</code> will get property value and put it into env vars. <br>
    @Configuration
    @PropertySource(value = "classpath:cat.properties")
    public class PropertyConfig {

        @Bean
        public Cat cat() {
            return new Cat();
        }
    }
  ```

- @Value
  - String value: `@Value("zack")`
  - SPEL: `@Value("#{20-15}")`
  - `${}`: get config file or env value: `@Value("${color:red}")`
