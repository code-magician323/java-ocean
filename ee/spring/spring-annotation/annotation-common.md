## Spring-Annotation

### Validate

```java
// JRS303 validate @Email
@Validated
@Email
```

### MVC

```java
// return resposne to brower directly
@ResponseBody
// mapping URL, and can get config value use ${}
@RequestMapping("/sayHello")
@RestController
@Controller
@PathVariable // Get path value eg. user/{id}
@RequestParam // Get paramters eg. user?name=zack
@CookieValue
@Resource // javax.annotation.Resource
@Autowired
@ModelAttribute // Add to Method or Parameter
@SessionAttributes 
```

### Functional

```java
@Bean // add retrun value to container; and id is method name
@Autowired // auto inject from IOC container

// @SpringBootApplication
@SpringBootApplication // labeled spring boot main class
@Configuration // marked as config class, but donot inject to container
@SpringBootConfiguration // [@Configuration] labeled this is config class
@EnableAutoConfiguration // enable autoconfig, replace brfore xml config
@AutoConfigurationPackage // auto config package
// import component to container, use AutoConfigurationPackages.Registrar.class to import all the components in the package and subpackages, which contains class marked by @SpringBootApplication
@Import(EnableAutoConfigurationImportSelector.class)

// import xml to do config: marked in spring boot main class
@ImportResource(locations = { "classpath:testService.xml" })
@ConfigurationProperties(prefix = "person") // mapping config file value to class and add to container
@Value("${user.api.host}")  // get value from config file or env var
@PropertySource(value = { "classpath:person.properties" }) // import specify property file to config

// enable specify properties class to implement config
@EnableConfigurationProperties(HttpEncodingProperties.class)
// judge wether conditional: web application
@ConditionalOnWebApplication
// judge wether contains specify class: CharacterEncodingFilter
@ConditionalOnClass(CharacterEncodingFilter.class)
// judge wether exist specify propety, matchIfMissing is also set[OK]
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)


@ApplicationListener
```

### @Conditional: expected true

| @Conditional Derived            | fucntion                                                     |
| ------------------------------- | ------------------------------------------------------------ |
| @ConditionalOnJava              | java version                                                 |
| @ConditionalOnBean              | container exist specify Bean                                 |
| @ConditionalOnMissingBean       | container not exist specify Bean                             |
| @ConditionalOnExpression        | meet SpEL expression specification                           |
| @ConditionalOnClass             | container exist specify class                                |
| @ConditionalOnMissingClass      | container not exist specify class                            |
| @ConditionalOnSingleCandidate   | container just have one specify Bean, or it's preferred Bean |
| @ConditionalOnProperty          | system exist specify propety                                 |
| @ConditionalOnResource          | classpath exist specify source                               |
| @ConditionalOnWebApplication    | web application                                              |
| @ConditionalOnNotWebApplication | not web application                                          |
| @ConditionalOnJndi              | JNDI exist specify column                                    |

### Test

```java
// spring boot test class
@RunWith(SpringRunner.class) + @SpringBootTest

// spring
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {LoggingAspect4Anno.class, ArithmeticCalculatorImpl.class})
```

## issue

1. @ControllerAdvice

---

## common annotation

1. @Component

2. @Service

3. @Repository

4. @Controller

5. @Autowired

   - byType

6. @Resource

   - byName

7. @Inject
