## Spring-Annotation

- **common:**

```java
// 进行 JrS303 校验 @Email：表示为email格式
@Validated
// 将方法返回值直接写给浏览器
@ResponseBody
// 请求参数映射
@RequestMapping("/sayHello")
@Controller + @ResponseBody
@RestController
// 将主配置类的所在包以及下面的所有字包里面的所有组件扫描注册到SpringIOC中
@SpringBootApplication
// 自动装配
@Autowired
// spring-boot 测试类
@RunWith(SpringRunner.class) + @SpringBootTest
```
