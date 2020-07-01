## 取参数

1. 单参数 mybatis 不会做特殊处理，

   - `#{参数名/任意名}`: 取出参数值

2. 多个参数: mybatis 会做特殊处理

   - 多个参数会被封装成 一个 map
     - key: param1...paramN, 或者参数的索引也可以
     - value: 传入的参数值
   - `#{}就是从map中获取指定的key的值`

   - 异常:
     ```log
     org.apache.ibatis.binding.BindingException:
         Parameter 'id' not found.
         Available parameters are [1, 0, param1, param2]
     ```
   - sample:
     - 方法: public Employee getEmpByIdAndLastName(Integer id,String lastName);
     - 取值：#{id},#{lastName}

3. 命名参数: 明确指定封装参数时 map 的 key `@Param("id")`
   - 多个参数会被封装成 一个 map
     - key: 使用@Param 注解指定的值
     - value: 参数值
   - `#{指定的key}取出对应的参数值`

## 对象参数

1. POJO:

   - 如果多个参数正好是我们业务逻辑的数据模型, 我们就可以直接传入 pojo;
   - `#{属性名}: 取出传入的pojo的属性值`

2. Map:

   - 如果多个参数不是业务模型中的数据, 没有对应的 pojo, 不经常使用, 为了方便我们也可以传入 map
   - `#{key}: 取出map中对应的值`

3. TO:
   - 如果多个参数不是业务模型中的数据, 但是经常要使用, 推荐来编写一个 TO[Transfer Object]数据传输对象
   - code
     ```js
     Page {
         int index;
         int size;
     }
     ```

---

## practice

1. public Employee getEmp(@Param("id") Integer id, String lastName)

   `取值: id ==> #{id/param1} lastName ==> #{param2}`

2. public Employee getEmp(Integer id, @Param("e") Employee emp)

   `取值: id ==> #{param1} lastName ==> #{param2.lastName/e.lastName}`

3. public Employee getEmpById(List<Integer> ids)

   `取值: 取出第一个id的值: #{list[0]}`

   - 特别注意: 如果是 Collection[List/Set] 类型或者是数组也会特殊处理. 也是把传入的 list 或者数组封装在 map 中
     - key: Collection, 如果是 List 还可以使用这个 key(list)
     - 数组(array)

## 总结

1. 参数多时会封装 map, 为了不混乱, 我们可以使用 @Param 来指定封装时使用的 key

   - #{key}就可以取出 map 中的值

   - (@Param("id") Integer id, @Param("lastName") String lastName)

     - ParamNameResolver 解析参数封装 map 的；
     - names: {0=id, 1=lastName}; 构造器的时候就确定好了

   - 流程:

     1. 获取每个标了 param 注解的参数的 @Param 的值: id, lastName 赋值给 name;
     2. 每次解析一个参数给 map 中保存信息: [key: 参数索引, value: name 的值]
     3. name 的值: `{0=id, 1=lastName, 2=2}`

        - 标注了 param 注解: 注解的值
        - 没有标注:
          1. 全局配置: useActualParamName[jdk1.8]: name=参数名
          2. name=map.size() 相当于当前元素的索引

     4. args[1, "Tom", "hello"]:

     ```java
     public Object getNamedParams(Object[] args) {
         final int paramCount = names.size();
         // 1. 参数为null直接返回
         if (args == null || paramCount == 0) {
             return null;
             // 2. 如果只有一个元素, 并且没有Param注解; args[0]: 单个参数直接返回
         } else if (!hasParamAnnotation && paramCount == 1) {
             return args[names.firstKey()];
             // 3. 多个元素或者有Param标注
         } else {
             final Map<String, Object> param = new ParamMap<Object>();
             int i = 0;

             // 4. 遍历names集合: {0=id, 1=lastName, 2=2}
             for (Map.Entry<Integer, String> entry : names.entrySet()) {
                 // names集合的value作为key; names集合的key又作为取值的参考args[0]:args[1, "Tom"]:
                 // eg:{id=args[0]:1, lastName=args[1]:Tom, 2=args[2]:2}
                 param.put(entry.getValue(), args[entry.getKey()]);
                 // add generic param names (param1, param2, ...)param
                 // 额外的将每一个参数也保存到map中, 使用新的key: param1...paramN
                 // 效果: 有Param注解可以#{指定的key}, 或者#{param1}
                 final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
                 // ensure not to overwrite parameter named with @Param
                 if (!names.containsValue(genericParamName)) {
                     param.put(genericParamName, args[entry.getKey()]);
                 }
                 i++;
             }

             return param;
             }
         }
     }
     ```
