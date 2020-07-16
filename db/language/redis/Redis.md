## redis[remote dictionary server]

### introduce

1.  data struct

    ![avatar](/static/image/db/rredis-data.png)

    ![avatar](/static/image/db/redis-data-struct.png)

    - string: 是二进制安全的, 一个 redis 中字符串 value 最多可以是 512M
    - hash: 存储对象
    - list： 底层实际是个链表
    - set
    - zset

2.  redis 底层实现 key-value 的存储使用的是 hashtable, 而且会频繁的 re-hash

    - 减少 hash 碰撞

    ![avatar](/static/image/db/rredis-data-store.png)

3.  redis 线程问题

    - 运算都是内存级别的运算
    - 单线程避免了多线程的切换性能损耗
    - 非阻塞 IO - IO 多路复用: redis 利用 epoll 来实现 IO 多路复用, 将连接信息和事件放到队列中, 依次放到文件事件分派器, 事件分派器将事件分发给事件处理器

    ![avatar](/static/image/db/rredisthread.png)

4.  持久化

    - AOF: 指令级别的, 设置多久改动一次就执行一次 aof; redis 重启时数据恢复很慢[数据量大的话]
      - aof 文件重写: 将多次操作 key 的指令把柄成一个[为了恢复数据嘛]
    - RDB: 数据级别的, 快照, 设置多久改动一次就执行一次快照, 会丢数据, 数据恢复快
    - 同时打开 AOF 和 RDB, 但是没有打开 混合持久化 时重启会使用 AOF 策略
    - 混合持久化: 需要保证 aof 和 rdb 都打开

      ```js
      aof-use-rdb-preamble no
      ```

      - bgrewriteaof: 会将 此时的 rdb 文件写入 aof[为了快速重启], 重写期间的新命令会在内存中, 直到重写结束后才会 以 aof 文件的方式写入 aof 文件

5.  **redis 不适合一个操作占用大量时间, 或者存储大数据块**

6.  缓存淘汰策略

    - 当 Redis 内存超出物理内存限制时, 内存的数据会开始和磁盘产生频繁的交换 (swap)
    - 限制最大使用内存: Redis 提供了配置参数 maxmemory 来限制内存超出期望大小, 超过配置的大小时会淘汰一部分之前的数据 key-value: volatile-xxx 处理带有过期时间的数据
    - noeviction: 不在提供写服务, 只提供读删除操作
    - volatile-lru: 针对设置了过期时间的 key least recent used
    - allkeys-lru: 针对所有 key
    - volatile-ttl: Remove the key with the nearest expire time (minor TTL)
    - volatile-random: 随机淘汰带有过期时间的 key
    - allkeys-random: 是随机的淘汰 key

7.  comparison k-v production

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

   - incr/incrby

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
   ttl key
   type key
   move key db
   persist ket // 删除过期时间
   rename key newKey
   ```

6. string

   ```js
   set / get / del / append / strlen;
   incr / desr / incrby / decrby;
   // setrange设置指定区间范围内的值
   getrange / setrange;
   setex / setnx;
   mset / mget / msetnx;
   getset;
   ```

7. hash

   ```js
   hset / hget / hdel / hgetall / hlen
   hmset / hmget;
   hincrby / hincrbyfloat;
   hexists key / hkeys / hvals;
   hsetnx;
   hscan key cursor [pattern] [count]
   ```

8. list: 链表的操作无论是头和尾效率都极高

   ```js
   lpush/rpush/lrange
   lpop/rpop
   lindex key 2
   llen
   // 从left往right删除2个值等于v1的元素，返回的值为实际删除的数量
   lrem key 2 v1
   // ltrim：截取指定索引区间的元素，格式是ltrim list的key 起始索引 结束索引
   ltrim key start_index emd_index
   lset key index value
   linsert key before/after value1 value2
   ```

9. set / zset

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

#### RDB: 会丢数据， 但是恢复快[只在 Slave 上持久化 RDB 文件]

1. 概念

   - 在指定时间隔内将内存中的数据集快照写入磁盘, 恢复时直接将快照文件读到内存
   - redis 会单独创建[fork]一个子线程来进行持久化, 先将数据写到一个临时文件中, 带到持久化结束后替换场次的持久化文件
   - 持久化过程, 主线程不进行任何 IO

2. fork

   - 复制一个与当前进程完全一样的进程[变量, 环境变量, 程序计数器]等, 并且作为原进程的子进程

3. 存储的文件: dbfilename + dir
4. 触发快照

   - save <seconds> <change>
   - `save ""` 标识禁用 rdb
   - flushall 也会产生 dump.rdb 文件, 但是内容 null

5. feature

   - 适合大规模的数据恢复
   - 对数据的完整性要求不高
   - 数据丢失
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
   - appendfsync no: 不同步

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

   - multi 开始事务
   - queued
   - exec/ discard

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

   - 冤有头债有主: 运行时错误

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

// TODO: vedio

1. 概念
   - master: 写为主, slave: 读为主
   - 读写分离
   - 容灾恢复

#### ~~sentinel~~

1. diagram

   ![avatar](/static/image/db/redis-sentinel.png)

2. 问题
   - 内存: 内存很难搞到很大: 一台 主从 电脑 嘛
   - 并发问题: 理论上 10w+ 就到极限了
   - 瞬断问题: master 挂了, 需要时间取选举出新的 master, 此时 redis 不能对外提供服务

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
   #  3. 指定数据文件存放位置，必须要指定不同的目录位置，不然会丢失数据
   #    dir /usr/local/redis-cluster/8001/
   #  4. 启动集群模式:
   #    cluster-enabled yes
   #  5. 集群节点信息文件，这里800x最好和port对应上:
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
   #  11. 设置集群节点间访问密码，跟上面一致
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
