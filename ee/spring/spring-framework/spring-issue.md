## Issue

### 1. [execute sequence of component](../spring-mvc/mvc-issue.md#1-sequence-of-component)

### 2. datasource.properties error

```xml
<!-- config -->
<!-- jdbc.username=root -->
username=root
<property name="user" value="${username}"></property>

<!-- error -->
cannot get connection for user "administrator"...

<!-- solution -->
<!-- use follow config -->
jdbc.username=root
```

### 3. Test use @Autowire

```java
// add spring-test dependency
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:ApplicationContext.xml")
```
