## mybatis

---

## get connection from session

- use privated by mybatis

```java
conn =  sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
```

- use applicationContext
