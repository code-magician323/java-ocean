## mybatis

---

## get connection from session

- use privated by mybatis

```java
conn =  sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
```

- use applicationContext

## question

1. diff between `#{}` and `${}`
