## redis[remote dictionary server]

### introduce

1. data struct

   ![avatar](/static/image/db/rredis-data.png)

   ![avatar](/static/image/db/redis-data-struct.png)

   - string: 是二进制安全的, 一个 redis 中字符串 value 最多可以是 512M
   - hash: 存储对象
   - list:底层实际是个链表
   - set
   - zset

2. redis 底层实现 key-value 的存储使用的是 hashtable, 而且会频繁的 re-hash

   - 减少 hash 碰撞

   ![avatar](/static/image/db/rredis-data-store.png)

3. redis 线程问题

   - 运算都是内存级别的运算
   - 单线程避免了多线程的切换性能损耗
   - 非阻塞 IO - IO 多路复用: redis 利用 epoll 来实现 IO 多路复用, 将连接信息和事件放到队列中, 依次放到文件事件分派器, 事件分派器将事件分发给事件处理器

   ![avatar](/static/image/db/rredisthread.png)

4. 持久化

   - AOF: 指令级别的, 设置多久改动一次就执行一次 aof; redis 重启时数据恢复很慢[数据量大的话]
     - aof 文件重写: 将多次操作 key 的指令把柄成一个[为了恢复数据嘛]
   - RDB: 数据级别的, 快照, 设置多久改动一次就执行一次快照, 会丢数据, 数据恢复快
   - 同时打开 AOF 和 RDB, 但是没有打开 混合持久化 时重启会使用 AOF 策略
   - 混合持久化: 需要保证 aof 和 rdb 都打开

     ```js
     aof-use-rdb-preamble no
     ```

     - bgrewriteaof: 会将 此时的 rdb 文件写入 aof[为了快速重启], 重写期间的新命令会在内存中, 直到重写结束后才会 以 aof 文件的方式写入 aof 文件

5. **redis 不适合一个操作占用大量时间, 或者存储大数据块**

6. 缓存淘汰策略

   - 当 Redis 内存超出物理内存限制时, 内存的数据会开始和磁盘产生频繁的交换 (swap)
   - 限制最大使用内存: Redis 提供了配置参数 maxmemory 来限制内存超出期望大小, 超过配置的大小时会淘汰一部分之前的数据 key-value: volatile-xxx 处理带有过期时间的数据
   - volatile-lru: 针对设置了过期时间的 key least recent used
   - allkeys-lru: 针对所有 key
   - volatile-random: 随机淘汰带有过期时间的 key
   - allkeys-random: 是随机的淘汰 key
   - noeviction: 不在提供写服务, 只提供读删除操作
   - volatile-ttl: Remove the key with the nearest expire time (minor TTL)

     ```C
     typedef struct redisObject {
        unsigned type:4;//对象类型（4位=0.5字节）
        unsigned encoding:4;//编码（4位=0.5字节）
        unsigned lru:LRU_BITS;//记录对象最后一次被应用程序访问的时间（24位=3字节）
        int refcount;//引用计数。等于0时表示可以被垃圾回收（32位=4字节）
        void *ptr;//指向底层实际的数据存储结构，如：SDS等(8字节)
     } robj;
     ```

7. Redis 的 LRU

   - redis 的 LRU 算法并不是真实的 LRU 算法
     - **通过抽样的方式进行删除: Redis 随机取出若干 key 在进行最近最少使用**
     - LRU 需要额外的空间进行存储[pre/next]: LRU=DLinkedList + HashMap
     - 可能存在某些 key 值使用很频繁, 但是最近没被使用, 从而被 LRU 算法删除
   - maxmemory-samples: 5
     - 3 最快, 内存少, 但是准确性太差
     - 5 平衡的好
     - 10 接近 LRU 但是耗内存
   - LRU 实现的数据类型的选择

     - DLinkedList: 新来的放入 head 单链表也可以, 但是淘汰最后一个单链表就需要遍历[因此需要使用 DlinkedList]
     - HashMap: 保存和查找都是 hash O(1)
     - 实现思路:
       1. save(key, value): 在 HashMap 找到 Key 对应的节点
          - 如果节点存在, 更新节点的值, 并把这个节点移动队头
          - 如果不存在, 需要构造新的节点, 并且尝试把节点塞到队头
          - 如果 LRU 空间不足, 则通过 tail 淘汰掉队尾的节点, 同时在 HashMap 中移除 Key
       2. get(key): 通过 HashMap 找到 LRU 链表节点
          - 因为根据 LRU 原理, 这个节点是最新访问的, 所以要把节点插入到队头, 然后返回缓存的值

   - Redis LRU
     - 记录对象最后一次被应用程序访问的时间: lru:LRU_BITS[24 位只能存储 194 天]
     - 但是 redis 并不是比较 lru 和当前时间, 而是维护了一个全局属性 lru_clock[定时更新 100ms], 最终比较的是 lru_clock 和 lru, 节约了每次获取当前系统时间

8. Redis 的 LFU: lru 的高 16 位记录访问时间， 低 8 位[0-255]记录访问频率

   - Redis 使用的是一种基于概率的对数器来实现 counter 的递增
   - r 给定一个旧的访问频次，当一个键被访问时，counter 按以下方式递增：

     - 提取 0 和 1 之间的随机数 R。
     - counter - 初始值（默认为 5），得到一个基础差值 baseval，如果这个差值小于 0，则直接取 0
     - 概率 P 计算公式为：`1/(baseval * lfu_log_factor + 1)`: lfu_log_factor 对数因子[10]
     - 如果 R < P 时，频次进行递增（counter++）
     - `random(0, 1) < 1 /((old_counter - 5)*lfu_log_factor + 1) ? counter++ : counter`

   ![avatar](/static/image/db/redis-flu.png)

   - 默认访问 1m 才会到最大值
   - counter 一直会增加, 所以不能反映热度, 需要一段时间不访问了就降下来
   - counter 的减少速度由参数 `lfu-decay-time[1]` 进行控制: N 分钟内没有访问，counter 就要减 N
     - 取出当前的时间戳和对象中的 lru 属性进行对比
     - 计算出当前多久没有被访问到: 比如计算得到的结果是 100 分钟没有被访问
     - 然后再去除配置参数 lfu_decay_time，如果这个配置默认为 1 也即是 100/1=100，代表 100 分钟没访问: 所以 counter 就减少 100。

9. comparison k-v production

   - redis 数据可以持久化, 可以将内存中的数据写入磁盘, 重启的时候可以再次加载进入内存使用[推荐使用 aof[执行级别的, 但是指令多的化重启就会变慢] + rbd[数据级别的, 会丢数据]
   - redis 提供了 string, hash, list, set, zset 的数据结构
   - redis 支持数据备份, master-slave

### install

1. 默认安装目录: `/usr/local/bin`

   ```shell
   root@7d41c0bd290a:/usr/local/bin# ls -la

   ├── redis-benchmark # 性能测试工具
   ├── redis-check-aof # 修复有问题的 AOF 文件
   ├── redis-check-rdb # 修复有问题的 dump.rdb 文件
   ├── redis-cli       # 客户端入口
   ├── redis-sentinel  # 哨兵
   └── redis-server    # 服务端
   ```

2. 启动关闭

   ```shell
   # 搞一份 conf 之后
   # start up
   /usr/local/bin/redis-server /usr/local/etc/redis/redis.conf
   # shut down
   /usr/local/bin/redis-cli shutdown
   /usr/local/bin/redis-cli -p 6379 shutdown
   ```

### common command

1. bluk operation

   - mset/mget, hmset ...

2. 原子操作

   - incr/incrby/hincrby

3. key 命令在数据很多时不建议使用: 消耗资源

   - 使用 scan 替代: cursor + key 的正则模式 + 遍历的 limit hint

4. common

   ```shell
   # 切换数据库
   SELECT 0
   # 查看数据库key的数量
   DBSIZE
   # 清空DB
   FLUSHDB
   FLUSHALL
   ```

5. key

   ```js
   del key
   keys *
   dump key
   exists key
   expire key second
   ttl/pttl  key
   type key
   move key db
   persist ket // 删除过期时间
   rename key newKey
   ```

6. string

   ```js
   // 获取
   get / mget;
   // 添加
   set[mset] / setex / setnx[msetnx] / append;
   // 长度
   strlen;
   // 删除
   del;
   // 原子
   incr / decr / incrby / decrby;
   // setrange设置指定区间范围内的值
   getrange / setrange;
   getset;
   ```

7. hash

   ```js
   // 添加
   hset/hmset/hsetnx
   // 查找
   hget/hmget/hgetall
   hscan key cursor [pattern] [count]
   // 原子
   hincrby/hincrbyfloat
   // 删除
   hdel
   // 长度
   hlen
   // 判断
   hexists key / hkeys / hvals;
   ```

8. list: 链表的操作无论是头和尾效率都极高

   ```js
   // 添加
   lpush/rpush
   // 删除
   lpop/rpop
   // 获取
   lindex key 2
   // 获取所有
   lrange key 0 -1
   // 个数
   llen
   ```

9. set

   ```js
   // 添加
   sadd key member [member ...]
   // 删除
   srem key member [member ...]
   // 获取所有
   smembers key
   // 判断
   sismember key
   // 个数
   scard key
   // 随机找出: 不删除
   srandommember key COUNT
   // 随机找出: 删除
   spop key COUNT
   // 差集
   sdiff key key
   // 交集
   sinter key key
   // 并级
   sunion key key
   ```

10. zset

    ```js
    // 添加
    zadd Z_KEY SCORE KEY [SCORE KEY]
    // 查看
    zrange key i j
    ```

### config

1. units

   - 1k --> 1000 bytes
   - 1kb --> 1024 bytes
   - units are case insensitive so 1GB 1Gb 1gB are all the same.

2. INCLUDES

   - Include one or more other config files here.

3. NETWORK

   - daeminize: run as a daemon, if run in docker, it will need change to `no`
   - pidfile: run as a daemon and write pid in specify file
   - port
   - timeout: Close the connection after a client is idle for N seconds (0 to disable)
   - bind:
   - protected-mode: set auth, then change it to no

4. GENERAL

   - loglevel/logfile: [debug / verbose / notice / warning]
   - tcp-keepalive
   - syslog-enabled / syslog-ident / syslog-facility
   - databases

5. SNAPSHOTTING

   - RDB 是整个内存的压缩过的 Snapshot
   - save <seconds> <change>
   - rdbcompression: 对存储的快照进行压缩, 消耗 CPU
   - rdbchecksum: 存储完成后使用 CRC64 对数据进行校验, 消耗 10% CPU
   - dbfilename
   - dir

6. MEMORY MANAGEMENT

   - maxmemory:
   - maxmemory-policy: 缓存淘汰策略

7. REPLICATION

8. SECURITY

   - requirepass: 设置密码

9. APPEND ONLY MODE

   - appendonly
   - appendfilename
   - appendfsync <[always / everysec/ no]>
   - `no-appendfsync-on-rewrite no`: 重写时是否使用 appendfsync, no 保证数据的安全性
   - auto-aof-rewrite-percentage 100
   - auto-aof-rewrite-min-size 64mb

### durable

#### RDB: 会丢数据, 但是恢复快[只在 Slave 上持久化 RDB 文件]

1. 相关命令

   - 目录配置
     1. 默认是 rdb 文件名为 dump.rdb
     2. dir
     3. dbfilename
   - 触发快照
     1. save <seconds> <change>
     2. `save ""` 标识禁用 rdb
     3. flushall 也会产生 dump.rdb 文件, 但是内容 null

2. 概念

   - 在指定时间隔内将内存中的数据集快照写入磁盘, 恢复时直接将快照文件读到内存
   - redis 会单独创建[fork]一个子线程来进行持久化, 先将数据写到一个临时文件中, 带到持久化结束后替换场次的持久化文件
   - 父进程继续接收并处理客户端发来的命令, 而子进程开始将内存中的数据写入硬盘中的临时文件
   - 持久化过程, 主线程不进行任何 IO[fork 结束之后就可以对外提供服务, 其他的 IO 操作由子进程进行]

3. fork

   - 复制一个与当前进程完全一样的进程[**变量, 环境变量, 程序计数器**]等, 并且作为原进程的子进程`[会造成间断性的暂停服务] + master 不要有rdb操作`
   - fork 进程时 redis 是不对外提供服务的
   - 在执行 fork 的时候操作系统[Unix]会使用写时复制[copy-on-write]策略, 即**fork 函数发生的一刻父子进程共享同一内存数据**, 当父进程要更改其中某片数据时[如执行一个写命令], 操作系统会将该片数据复制一份以保证子进程的数据不受影响, 所以新的 RDB 文件存储的是执行 fork 一刻的内存数据
   - 为此需要确保 Linux 系统允许应用程序申请超过可用内存[物理内存和交换分区]的空间, 方法是在/etc/sysctl.conf 文件加入 vm.overcommit_memory = 1, 然后重启系统或者执行 sysctl vm.overcommit_memory=1 确保设置生效
   - RDB 文件是经过压缩[可以配置 rdbcompression 参数以禁用压缩节省 CPU 占用]的二进制格式, 所以占用的空间会小于内存中的数据大小, 更加利于传输

4. 存储的文件: dbfilename + dir

5. feature

   - 适合大规模的数据恢复
   - 对数据的完整性要求不高
   - fork 时需要 2 倍的内存

6. conclusion

   ![avatar](/static/image/db/redis-rdb.png)

#### AOF

1. 概念

   - 以日志的形式来记录每个`写操作`, 重启时从头到尾执行一遍
   - aof 文件很大的话会很慢

2. 存储的文件: appendonly + appendfilename
3. aof 文件恢复

   - 备份被写坏的 aof 文件
   - redis-check-aof --fix
   - restart

4. rewrite

   - bgrewriteaof
   - aof 文件过大时会 fork 出一个新的进程将文件重写[县写入临时文件]
   - redis 会当 aof 文件大于 64M 且 size 翻倍时重写

5. 触发 aof

   - appendfsync always: 性能差一些
   - appendfsync everysec: 异步每秒一个, 如果一秒内当即会有数据丢失
   - appendfsync no: ~~不同步~~

6. feature

   - 数据丢失概率小
   - aof 文件大于 rdb 时重启恢复慢
   - no 时效率与 rdb 相同

7. conclusion

   ![avatar](/static/image/db/redis-aof.png)

#### 混合持久化

1. 需要保证 aof 和 rdb 都打开

   ![avatar](/static/image/db/redis-durable.png)

   ```js
   aof-use-rdb-preamble no
   ```

2. bgrewriteaof: 会将 此时的 rdb 文件写入 aof[为了快速重启], 重写期间的新命令会在内存中, 直到重写结束后才会 以 aof 文件的方式写入 aof 文件

### transaction

1. 一次执行多个命令, 一个事务中的所有的命令都会序列化, 串行的排他的执行

   - watch: 可以在事务之前也可以之后, 是一个乐观锁, redis 事务提供的 cas 行为, 可以监控一个或多个 key, 一旦其中的值被修改或删除, 之后的事务就不会执行, 监控知道 exec 命令结束
   - multi 开始事务, 会将客户端状态的 flags 属性打开 redis_multi 标识, 接下来的命令都会以 redis_multi 为起点的 queued
   - queued, 入 queue 的语法错误则关闭 redis_multi[事务结束], 并返回错误信息; 否则入 queue
   - exec/discard: 如果客户端的 flags 不包含 redis_multi 或者包含了 redis_dirty_cas 或者 包含了 redis_dity_exec 则取消事务的执行, 否则认为可以执行事务[遍历事务 queue FIFO, 顺序输出结果, 且逻辑的错误不会回滚整个事务]

2. command

   ```js
   discard // 取消事务, 放弃事务内的所有命令
   exec    // 执行是屋内的所有的命令
   multi   // 标记书屋块的开始
   unwatch // 取消watch 命令对所有key的监视
   watch key [key ...] // 监视key, 如果事务执行之前被watch则事务会被打断
   ```

3. practice

   - normal case

   ```shell
   127.0.0.1:6379> MULTI
   OK
   127.0.0.1:6379> set id 12
   QUEUED
   127.0.0.1:6379> get id
   QUEUED
   127.0.0.1:6379> INCR id
   QUEUED
   127.0.0.1:6379> INCR tl
   QUEUED
   127.0.0.1:6379> INCR tl
   QUEUED
   127.0.0.1:6379> get tl
   QUEUED
   127.0.0.1:6379> exec
   1) OK
   2) "12"
   3) (integer) 13
   4) (integer) 1
   5) (integer) 2
   6) "2"
   127.0.0.1:6379>
   ```

   - 放弃事务

   ```shell
   127.0.0.1:6379> MULTI
   OK
   127.0.0.1:6379> set id 12
   QUEUED
   127.0.0.1:6379> get id
   QUEUED
   127.0.0.1:6379> INCR id
   QUEUED
   127.0.0.1:6379> INCR tl
   QUEUED
   127.0.0.1:6379> INCR tl
   QUEUED
   127.0.0.1:6379> get tl
   QUEUED
   127.0.0.1:6379> discard
   OK
   127.0.0.1:6379>
   ```

   - 全体连坐: 语法上的错误

   ```shell
   127.0.0.1:6379> MULTI
   OK
   127.0.0.1:6379> set name zz
   QUEUED
   127.0.0.1:6379> get name
   QUEUED
   127.0.0.1:6379> incr tl
   QUEUED
   127.0.0.1:6379> get tl
   QUEUED
   127.0.0.1:6379> set email
   (error) ERR wrong number of arguments for 'set' command
   127.0.0.1:6379> exec
   (error) EXECABORT Transaction discarded because of previous errors.
   127.0.0.1:6379> get tl
   "2"
   ```

   - 冤有头债有主: 运行时错误[为了性能]

   ```shell
   127.0.0.1:6379> MULTI
   OK
   127.0.0.1:6379> set age 11
   QUEUED
   127.0.0.1:6379> INCR ti
   QUEUED
   127.0.0.1:6379> set emial zack
   QUEUED
   127.0.0.1:6379> INCR emial
   QUEUED
   127.0.0.1:6379> get age
   QUEUED
   127.0.0.1:6379> exec
   1) OK
   2) (integer) 1
   3) OK
   4) (error) ERR value is not an integer or out of range
   5) "11"
   127.0.0.1:6379>
   ```

4. watch

   - watch 类似乐观锁, 事务提交时, 如果 key 的值被别人修改了, 则这个事务放弃
   - 放弃之后会返回 Nullmuti-bulk 应答通知调用者事务执行失败

5. 特点

   - 单独的隔离操作: 事务中的命令会序列化顺序且排他的执行, 不会被打断
   - 没有隔离级别的概念: 提交之前都不会执行
   - 没有原子性: redis 中同一事物如果有一条失败, 其他命令依旧可以执行成功

### MQ

![avatar](/static/image/db/redis-mq.png)

### HA

#### master-slave

1. 概念: `配从不配主`

   - master: 写为主, slave: 读为主
   - 读写分离
   - 容灾恢复

2. `slaveof ip port`: `info replication`
   - 每次断开与 master 的链接都会使得 slave 失效, 或者可以改配置文件 `replicaof <masterip> <masterport>`
   - 中途变更会清除之前的数据并重新开始拷贝
3. 一主二从:
   - 从机是只读的
   - 从机全量复制
   - 主机 shutdown 从机还是原地待命
   - 主机恢复依旧是主机
   - 从机 down 没有关系
4. 薪火相传

   - 上面的把所有 slave 都挂到同一个主机上, 会影响主机的写性能
   - master - salve[salve] -slave

5. 反客为主
6. salveof no one: 恢复为主机

#### 复制原理

1. slave 启动成功连接到 master 后会发送一个 sync 命令
2. master 接到命令启动后台的存盘进程, 同时收集所有接收到的用于修改数据集命令, 在后台进程执行完毕之后, master 将传送整个数据文件到 slave, 以完成一次完全同步: master 新的数据会记录到内存 buffer
3. 全量复制: 而 slave 服务在接收到数据库文件数据后, 将其存盘并加载到内存中, 之后通知 master buffer 可以继续同步
4. 增量复制: master 继续将新的所有收集到的修改命令依次传给 slave, 完成同步
5. 但是只要是重新连接 master, 一次完全同步[全量复制]将被自动执行

#### ~~sentinel~~

1. diagram

   ![avatar](/static/image/db/redis-sentinel.png)

2. 功能

   - 集群监控: 负责监控 redis master and slave
   - 消息通知: 某个 redis 实例有故障, 那么哨兵负责发送消息负责报警发送给管理员
   - 故障转移: 如果 master 挂了, 则自动转移到 slave
   - 配置中心: 通知 client 新的 master 地址

3. 配置: 一组 sentinel 能同时监控多个 master

   - 反客为主的自动版, 能够后台监控主机是否故障, 如果故障了根据投票数自动将从库转换为主库
   - 调整结构, 6379 带着 80、81
   - 自定义的/myredis 目录下新建 sentinel.conf 文件, 名字绝不能错
   - 配置哨兵, 填写内容
     - `sentinel monitor 被监控数据库名字(随意) 127.0.0.1 6379 1`
     - 上面最后一个数字 1, 表示主机挂掉后 salve 投票看让谁接替成为主机, 得票数多少[1]后成为主机
   - 启动哨兵
     - Redis-sentinel /myredis/sentinel.conf
     - 上述目录依照各自的实际情况配置, 可能目录不同
   - 正常主从演示
   - 原有的 master 挂了
   - 投票新选
   - 重新主从继续开工, `info replication` 查查看
   - 问题：如果之前的 master 重启回来, 会变成 slave

4. 问题

   - 由于所有的写操作都是先在 Master 上操作, 然后同步更新到 Slave 上, 所以从 Master 同步到 Slave 机器有一定的延迟, 当系统很繁忙的时候, 延迟问题会更加严重, Slave 机器数量的增加也会使这个问题更加严重
   - 内存: 内存很难搞到很大: 一台 主从 电脑 嘛
   - 并发问题: 理论上 10w+ 就到极限了
   - 瞬断问题: master 挂了, 需要时间取选举出新的 master, 此时 redis 不能对外提供服务

5. 选举原理

   - 哨兵可以同时监视多个主从服务器，并且在被监视的 master 下线时，⾃动将某个 slave 提升为 master，然后由新的 master 继续接收命令。整个过程如下：

     1. 初始化 sentinel，将普通的 redis 代码替换成 sentinel 专⽤代码
     2. 初始化 masters 字典和服务器信息，服务器信息主要保存 ip:port，并记录实例的地址和 ID
     3. 创建和 master 的两个连接，命令连接和订阅连接，并且订阅 sentinel:hello 频道
     4. 每隔 10 秒向 master 发送 info 命令，获取 master 和它下⾯所有 slave 的当前信息
     5. 当发现 master 有新的 slave 之后，sentinel 和新的 slave 同样建⽴两个连接，同时每个 10 秒发送 info
        命令，更新 master 信息
     6. sentinel 每隔 1 秒向所有服务器发送 ping 命令，如果某台服务器在配置的响应时间内连续返回⽆效回
        复，将会被标记为下线状态
     7. 选举出领头 sentinel，领头 sentinel 需要半数以上的 sentinel 同意
     8. 领头 sentinel 从已下线的的 master 所有 slave 中挑选⼀个，将其转换为 master
     9. 让所有的 slave 改为从新的 master 复制数据
     10. 将原来的 master 设置为新的 master 的从服务器，当原来 master 重新回复连接时，就变成了新 master 的从服务器

   - sentinel 会每隔 1 秒向所有实例（包括主从服务器和其他 sentinel）发送 ping 命令，并且根据回复判断是否已经下线，这种⽅式叫做主观下线。当判断为主观下线时，就会向其他监视的 sentinel 询问，如果超过半数的投票认为已经是下线状态，则会标记为客观下线状态，同时触发故障转移。

#### redis 高可用集群

![avatar](/static/image/db/redis-ha.png)

1. 解决的问题:

   - 内存: 每个集群 50 G \* 200 个 = 1T redis 空间
   - 并发: 每个主从 10w+ \* 200 个
   - 瞬断问题: 只有瞬断的哪一个在选举期间不能提供服务, 其他的主从 redis 小集群是可以提供服务的

2. 集群搭建: use redis directly

   - 1. redis 安装:

   ```shell
   # http://redis.io/download 安装步骤
   # 安装gcc
   yum install gcc
   # 把下载好的redis-5.0.8.tar.gz放在/usr/local文件夹下, 并解压
   wget http://download.redis.io/releases/redis-5.0.8.tar.gz tar xzf redis-5.0.8.tar.gz
   cd redis-5.0.8
   # 进入到解压好的 redis-5.0.8 目录下, 进行编译与安装
   make & make install
   # 启动并指定配置文件 src/redis-server redis.conf[注意要使用后台启动, 所以修改 redis.conf 里的 daemonize 改为 yes
   # 验证启动是否成功
   ps -ef | grep redis
   # 进入 redis 客户端
   cd /usr/local/redis/bin/redis-cli
   # 退出客户端 quit
   # 退出redis服务: pkill redis-server; kill 进程号; src/redis-cli shutdown
   ```

   - 2. redis 集群搭建
     - redis 集群需要至少要三个 master 节点, 并且给每个 master 再搭建一个 slave 节点, 每台机器一主一从, 搭建集群的步骤如下:

   ```shell
   # 第一步: 在第一台机器的 `/usr/local` 下创建文件夹 redis-cluster, 然后在其下面分别创建2个文件夾如下
   mkdir -p /usr/local/redis-cluster
   mkdir 8001
   mkdir 8004

   # 第二步: 把之前的 redis.conf 配置文件 copy 到 8001 下, 修改如下内容:
   #  1. daemonize yes
   #  2. port 8001[分别对每个机器的端口号进行设置]
   #  3. 指定数据文件存放位置, 必须要指定不同的目录位置, 不然会丢失数据
   #    dir /usr/local/redis-cluster/8001/
   #  4. 启动集群模式:
   #    cluster-enabled yes
   #  5. 集群节点信息文件, 这里800x最好和port对应上:
   #    cluster-config-file nodes-8001.conf
   #  6. cluster-node-timeout 5000
   #  7. 去掉 bind 绑定访问 ip 信息
   #     # bind 127.0.0.1
   #  8. 关闭保护模式
   #      protected-mode  no
   #  9. appendonly yes
   # 如果要设置密码需要增加如下配置:
   #  10. 设置redis访问密码
   #     requirepass zhuge
   #  11. 设置集群节点间访问密码, 跟上面一致
   #     masterauth zhuge

   # 第三步: 把修改后的配置文件, copy到8002, 修改第 2、3、5 项里的端口号, 可以用批量替换:
   #    :%s/源字符串/目的字符串/g

   # 第四步: 另外两台机器也需要做上面几步操作, 第二台机器用8002和8005, 第三台机器用8003和8006

   # 第五步: 分别启动 6 个 redis 实例, 然后检查是否启动成功
   /usr/local/redis-5.0.8/src/redis-server /usr/local/redis-cluster/800*/redis.conf
   ps -ef | grep redis # 查看是否启动成功

   # 第六步: 用 redis-cli 创建整个 redis 集群[redis5 以前的版本集群是依靠 ruby 脚本 redis-trib.rb 实现]
   /usr/local/redis-5.0.8/src/redis-cli -a zhuge --cluster create --cluster-replicas 1 192.168.0.61:8001 192.168.0.62:8002 192.168.0.63:8003 192.168.0.61:8004 192.168.0.62:8005 192.168.0.63:8006 # 代表为每个创建的主服务器节点创建一个从服务器节点

   # 第七步: 验证集群
   #  1. 连接任意一个客户端即可:
   /usr/local/redis-5.0.8/src/redis-cli -a zhuge -c -h 192.168.0.61 -p 800*  # [-a访问服务端密码, -c 表示集群模式, 指定 ip 地址和端口号
   #  2. 进行验证:
   cluster info # 查看集群信息
   cluster nodes # 查看节点列表
   #  3. 进行数据操作验证
   #  4. 关闭集群则需要逐个进行关闭, 使用命令:
   /usr/local/redis/bin/redis-cli -a zhuge -c -h 192.168.0.60 -p 800* shutdown
   ```

   3. 集群搭建: in docker

   4. 集群下 get key 的流程

      - command arrival redis server cluster
      - redis server cluster do hash with key-value, then redirect to relative server
      - in that server, the server will do hash for key to located value

   5. 集群下使用 jedision get key 的流程

      - jedission will get redis server cluster ip and slot info when create jedission pool
      - jedission do hash for key, then calcuate ip containing this key in client
      - command arrival to specify redis server
      - in that server, the server will do hash for key to located value
      - if redis server cluster 扩容之后, jedission 的 slot 和 ip 实例节点信息解释错误的了
      - 因此会发生一次重定位, 并 server 会给 jedission 一份新的 slot + ip 的数据
      - 重定位之后 jedission 会重新发送一次请求到包含这个 key 的 server
      - in that server, the server will do hash for key to located value

   6. cluster command
      ```shell
      1. create: 创建一个集群环境host1:port1 ... hostN:portN
      2. call: 可以执行redis命令
      3. add-node: 将一个节点添加到集群里, 第一个参数为新节点的ip:port, 第二个参数为集群中任意一个已经存在的节点的ip:port
      4. del-node: 移除一个节点
      5. reshard: 重新分片
      6. check: 检查集群状态
      ```
   7. cluster maintain

      ```shell
      /usr/local/redis-5.0.2/src/redis-cli -a zhuge -c -h 192.168.0.61 -p 8001
      # 查看集群状态
      192.168.0.61:8001> cluster  nodes
      # 增加redis实例
      /usr/local/redis-5.0.2/src/redis-cli --cluster add-node 192.168.0.64:8007 192.168.0.61:8001
      # 当添加节点成功以后, 新增的节点不会有任何数据, 因为它还没有分配任何的slot(hash槽), 我们需要为新节点手工分配hash槽
      /usr/local/redis-5.0.2/src/redis-cli --cluster reshard 192.168.0.61:8001
      - 个数
      - 从哪来
      # 配置8008为8007的从节点
      /usr/local/redis-5.0.2/src/redis-cli --cluster add-node 192.168.0.64:8008 192.168.0.61:8001
      /usr/local/redis-5.0.2/src/redis-cli -c -h 192.168.0.64 -p 8008
      192.168.0.61:8008> cluster replicate eb57a5700ee6f9ff099b3ce0d03b1a50ff247c3c # 先进入 8008 cli

      # 删除8008从节点
      /usr/local/redis-5.0.2/src/redis-cli --cluster del-node 192.168.0.64:8008 1805b6339d91b0e051f46845eebacb9bc43baefe
      # 删除8007主节点
      ## 必须先把8007里的hash槽放入到其他的可用主节点中去, 然后再进行移除节点操作
      /usr/local/redis-5.0.2/src/redis-cli --cluster reshard 192.168.0.64:8007
      /usr/local/redis-5.0.2/src/redis-cli --cluster del-node 192.168.0.64:8007    eb57a5700ee6f9ff099b3ce0d03b1a50ff247c3c
      ```

3. 槽位定位算法:

   - Cluster 默认会对 key 值使用 crc16 算法进行 hash 得到一个整数值
   - 然后用这个整数值对 16384(14) 进行取模来得到具体槽位
   - `HASH_SLOT = CRC16(key) mod 16384`

4. 跳转重定位

   - 当客户端向一个错误的节点发出了指令, 该节点会发现指令的 key 所在的槽位并不归自己管理
   - 这时它会向客户端发送一个特殊的跳转指令携带目标操作的节点地址, 告诉客户端去连这个节点去获取数据
   - 客户端收到指令后除了跳转到正确的节点上去操作, 还会同步更新纠正本地的槽位映射表缓存, 后续所有 key 将使用新的槽位映射表

5. 网络抖动

   - 网络抖动就是非常常见的一种现象, 突然之间部分连接变得不可访问, 然后很快又恢复正常.
   - 为解决这种问题, Redis Cluster 提供了一种选项 cluster-node-timeout: 表示当某个节点持续 timeout 的时间失联时, 才可以认定该节点出现故障, 需要进行主从切换. 如果没有这个选项, 网络抖动会导致主从频繁切换(数据的重新复制)

6. Redis 集群选举原理分析

   - 当 slave 发现自己的 master 变为 FAIL 状态时, 便尝试进行 Failover, 以期成为新的 master
   - 由于挂掉的 master 可能会有多个 slave, 从而存在多个 slave 竞争成为 master 节点的过程, 其过程如下：

     1. slave 发现自己的 master 变为 FAIL
     2. 将自己记录的集群 currentEpoch 加 1, 并广播 FAILOVER_AUTH_REQUEST 信息
     3. 其他节点收到该信息, 只有 master 响应, 判断请求者的合法性, 并发送 FAILOVER_AUTH_ACK, 对每一个 epoch 只发送一次 ack
     4. 尝试 failover 的 slave 收集 FAILOVER_AUTH_ACK
     5. 超过半数后变成新 Master
     6. 广播 Pong 通知其他集群节点.

   - 从节点并不是在主节点一进入 FAIL 状态就马上尝试发起选举, 而是有一定延迟, 一定的延迟确保我们等待 FAIL 状态在集群中传播, slave 如果立即尝试选举, 其它 masters 或许尚未意识到 FAIL 状态, 可能会拒绝投票
     1. 延迟计算公式: `DELAY = 500ms + random(0 ~ 500ms) + SLAVE_RANK * 1000ms`
     2. SLAVE_RANK 表示此 slave 已经从 master 复制数据的总量的 rank
     3. Rank 越小代表已复制的数据越新. 这种方式下, 持有最新数据的 slave 将会首先发起选举[理论上]
