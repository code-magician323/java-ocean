## practice

1. 测试连通行

```java
public class Demo01 {
  public static void main(String[] args) {
    //连接本地的 Redis 服务
    Jedis jedis = new Jedis("127.0.0.1",6379);
    //查看服务是否运行, 打出pong表示OK
    System.out.println("connection is OK==========>: "+jedis.ping());
  }
}
```

2. common usage

```java
package com.atguigu.redis.test;

import java.util.*;
import redis.clients.jedis.Jedis;

public class Test02
{
  public static void main(String[] args)
  {
     Jedis jedis = new Jedis("127.0.0.1",6379);

     //key
     Set<String> keys = jedis.keys("*");
     for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
       String key = (String) iterator.next();
       System.out.println(key);
     }
     System.out.println("jedis.exists====>"+jedis.exists("k2"));
     System.out.println(jedis.ttl("k1"));

     //String
     //jedis.append("k1","myreids");
     System.out.println(jedis.get("k1"));
     jedis.set("k4","k4_redis");
     System.out.println("----------------------------------------");
     jedis.mset("str1","v1","str2","v2","str3","v3");
     System.out.println(jedis.mget("str1","str2","str3"));

     //list
     System.out.println("----------------------------------------");
     //jedis.lpush("mylist","v1","v2","v3","v4","v5");
     List<String> list = jedis.lrange("mylist",0,-1);
     for (String element : list) {
       System.out.println(element);
     }

     //set
     jedis.sadd("orders","jd001");
     jedis.sadd("orders","jd002");
     jedis.sadd("orders","jd003");

     Set<String> set1 = jedis.smembers("orders");
     for (Iterator iterator = set1.iterator(); iterator.hasNext();) {
       String string = (String) iterator.next();
       System.out.println(string);
     }

     jedis.srem("orders","jd002");
     System.out.println(jedis.smembers("orders").size());

     //hash
     jedis.hset("hash1","userName","lisi");
     System.out.println(jedis.hget("hash1","userName"));

     Map<String,String> map = new HashMap<String,String>();
     map.put("telphone","13811814763");
     map.put("address","atguigu");
     map.put("email","abc@163.com");
     jedis.hmset("hash2",map);

     List<String> result = jedis.hmget("hash2", "telphone","email");
     for (String element : result) {
       System.out.println(element);
     }

     //zset
     jedis.zadd("zset01",60d,"v1");
     jedis.zadd("zset01",70d,"v2");
     jedis.zadd("zset01",80d,"v3");
     jedis.zadd("zset01",90d,"v4");

     Set<String> s1 = jedis.zrange("zset01",0,-1);
     for (Iterator iterator = s1.iterator(); iterator.hasNext();) {
       String string = (String) iterator.next();
       System.out.println(string);
     }
  }
}
```

3. 事务

```java
package com.atguigu.redis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class Test03
{
  public static void main(String[] args)
  {
     Jedis jedis = new Jedis("127.0.0.1",6379);

     //监控key, 如果该动了事务就被放弃
     /*3
     jedis.watch("serialNum");
     jedis.set("serialNum","s#####################");
     jedis.unwatch();
     */

     Transaction transaction = jedis.multi();//被当作一个命令进行执行
     Response<String> response = transaction.get("serialNum");
     transaction.set("serialNum","s002");
     response = transaction.get("serialNum");
     transaction.lpush("list3","a");
     transaction.lpush("list3","b");
     transaction.lpush("list3","c");
     transaction.exec();
     //2 transaction.discard();

     System.out.println("serialNum***********"+response.get());
  }
}
```

4. lock

```java
public class TestTransaction {
  public boolean transMethod() {
     Jedis jedis = new Jedis("127.0.0.1", 6379);

     int balance;// 可用余额
     int debt;// 欠额
     int amtToSubtract = 10;// 实刷额度
     jedis.watch("balance");
     //jedis.set("balance","5");//此句不该出现, 讲课方便. 模拟其他程序已经修改了该条目
     balance = Integer.parseInt(jedis.get("balance"));

     if (balance < amtToSubtract) {
       jedis.unwatch();
       System.out.println("modify");
       return false;
     } else {
       System.out.println("***********transaction");
       Transaction transaction = jedis.multi();
       transaction.decrBy("balance", amtToSubtract);
       transaction.incrBy("debt", amtToSubtract);
       transaction.exec();
       balance = Integer.parseInt(jedis.get("balance"));
       debt = Integer.parseInt(jedis.get("debt"));

       System.out.println("*******" + balance);
       System.out.println("*******" + debt);

       return true;
     }
  }

  /**
   * 通俗点讲, watch命令就是标记一个键, 如果标记了一个键,  在提交事务前如果该键被别人修改过, 那事务就会失败, 这种情况通常可以在程序中重新再尝试一次.
   * 首先标记了键balance, 然后检查余额是否足够, 不足就取消标记, 并不做扣减;  足够的话, 就启动事务进行更新操作,
   * 如果在此期间键balance被其它人修改,  那在提交事务（执行exec）时就会报错,  程序中通常可以捕获这类错误再重新执行一次, 直到成功.
   */
  public static void main(String[] args) {

     TestTransaction test = new TestTransaction();
     boolean retValue = test.transMethod();
     System.out.println("main retValue-------: " + retValue);
  }
}
```

5. pool: JedisPoolUtil

   - maxActive: 控制一个 pool 可分配多少个 jedis 实例, 通过 `pool.getResource()` 来获取
     - 如果赋值为-1, 则表示不限制;
     - 如果 pool 已经分配了 maxActive 个 jedis 实例, 则此时 pool 的状态为 exhausted.
   - maxIdle: 控制一个 pool 最多有多少个状态为 idle(空闲)的 jedis 实例;
   - whenExhaustedAction: 表示当 pool 中的 jedis 实例都被 allocated 完时, pool 要采取的操作; 默认有三种.
     - WHEN_EXHAUSTED_FAIL: 表示无 jedis 实例时, 直接抛出 NoSuchElementException;
     - WHEN_EXHAUSTED_BLOCK: 则表示阻塞住, 或者达到 maxWait 时抛出 JedisConnectionException;
     - WHEN_EXHAUSTED_GROW: 则表示新建一个 jedis 实例, 也就说设置的 maxActive 无用;
   - maxWait：表示当 borrow 一个 jedis 实例时, 最大的等待时间, 如果超过等待时间, 则直接抛 JedisConnectionException;
   - testOnBorrow：获得一个 jedis 实例的时候是否检查连接可用性（ping()）; 如果为 true, 则得到的 jedis 实例均是可用的;
   - testOnReturn：return 一个 jedis 实例给 pool 时, 是否检查连接可用性（ping()）;
   - testWhileIdle：如果为 true, 表示有一个 idle object evitor 线程对 idle object 进行扫描, 如果 validate 失败, 此 object 会被从 pool 中 drop 掉; 这一项只有在 timeBetweenEvictionRunsMillis 大于 0 时才有意义;
   - timeBetweenEvictionRunsMillis：表示 idle object evitor 两次扫描之间要 sleep 的毫秒数;
   - numTestsPerEvictionRun：表示 idle object evitor 每次扫描的最多的对象数;
   - minEvictableIdleTimeMillis：表示一个对象至少停留在 idle 状态的最短时间, 然后才能被 idle object evitor 扫描并驱逐; 这一项只有在 timeBetweenEvictionRunsMillis 大于 0 时才有意义;
   - softMinEvictableIdleTimeMillis：在 minEvictableIdleTimeMillis 基础上, 加入了至少 minIdle 个对象已经在 pool 里面了. 如果为-1, evicted 不会根据 idle time 驱逐任何对象. 如果 minEvictableIdleTimeMillis>0, 则此项设置无意义, 且只有在 timeBetweenEvictionRunsMillis 大于 0 时才有意义;
   - lifo：borrowObject 返回对象时, 是采用 DEFAULT_LIFO（last in first out, 即类似 cache 的最频繁使用队列）, 如果为 False, 则表示 FIFO 队列;

```java
package com.atguigu.redis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {

    //被volatile修饰的变量不会被本地线程缓存, 对该变量的读写都是直接操作共享内存.
    private static volatile JedisPool jedisPool = null;

    private JedisPoolUtil() {}

    public static JedisPool getJedisPoolInstance()
    {
        if(null == jedisPool)
        {
            synchronized (JedisPoolUtil.class)
            {
                if(null == jedisPool)
                {
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    // default value
                    // testWhileIdle=true
                    // minEvictableIdleTimeMills=60000
                    // timeBetweenEvictionRunsMillis=30000
                    // numTestsPerEvictionRun=-1

                    poolConfig.setMaxActive(1000);
                    poolConfig.setMaxIdle(32);
                    poolConfig.setMaxWait(100*1000);
                    poolConfig.setTestOnBorrow(true);

                    jedisPool = new JedisPool(poolConfig,"127.0.0.1");
                }
            }
        }
        return jedisPool;
    }

    public static void release(JedisPool jedisPool,Jedis jedis)
    {
        if(null != jedis)
        {
        jedisPool.returnResourceObject(jedis);
        }
    }
}
```

## impliment

1. RESP:redis 序列化协议

   - For Simple Strings the first byte of the reply is "+"
   - For Errors the first byte of the reply is "-"
   - For Integers the first byte of the reply is ":"
   - For Bulk Strings the first byte of the reply is "\$"
   - For Arrays the first byte of the reply is "\*"

2. socket[ip+port] + io stream + protocol

- 可以假装自己是 jedis 处理的 socket, 看看客户端发到 server 的是什么东西

3. digram

![avatar](/static/image/db/redis-jedis.png)
