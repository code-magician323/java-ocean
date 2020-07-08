## list

### data struct

![avatar](/static/image/db/rredis-data.png)

![avatar](/static/image/db/redis-data-struct.png)

1. string
2. list
3. hash
4. set
5. zset

### introduce

1.  redis 底层实现 key-value 的存储使用的是 hashtable, 而且会频繁的 re-hash

    - 减少 hash 碰撞

    ![avatar](/static/image/db/rredis-data-store.png)

2.  redis 线程问题

    - 运算都是内存级别的运算
    - 单线程避免了多线程的切换性能损耗
    - 非阻塞 IO - IO 多路复用: redis 利用 epoll 来实现 IO 多路复用, 将连接信息和事件放到队列中, 依次放到文件事件分派器, 事件分派器将事件分发给事件处理器

    ![avatar](/static/image/db/rredisthread.png)

3.  持久化

    - AOF: 指令级别的, 设置多久改动一次就执行一次 aof; redis 重启时数据恢复很慢[数据量大的话]
      - aof 文件重写: 将多次操作 key 的指令把柄成一个[为了恢复数据嘛]
    - RDB: 数据级别的, 快照, 设置多久改动一次就执行一次快照, 会丢数据, 数据恢复快
    - 同时打开 AOF 和 RDB, 但是没有打开 混合持久化 时重启会使用 AOF 策略
    - 混合持久化: 需要保证 aof 和 rdb 都打开

    ![avatar](/static/image/db/redis-durable.png)

         ```js
         aof-use-rdb-preamble no
         ```

         - bgrewriteaof: 会将 此时的 rdb 文件写入 aof[为了快速重启], 重写期间的新命令会在内存中, 直到重写结束后才会 以 aof 文件的方式写入 aof 文件

4.  redis 不适合一个操作占用大量时间, 或者存储大数据块

5.  缓存淘汰策略
    - 当 Redis 内存超出物理内存限制时, 内存的数据会开始和磁盘产生频繁的交换 (swap)
    - 限制最大使用内存: Redis 提供了配置参数 maxmemory 来限制内存超出期望大小, 超过配置的大小时会淘汰一部分之前的数据 key-value: volatile-xxx 处理带有过期时间的数据
    - noeviction: 不在提供写服务, 只提供读删除操作
    - volatile-lru: 针对设置了过期时间的 key least recent used
    - allkeys-lru: 针对所有 key
    - volatile-ttl: Remove the key with the nearest expire time (minor TTL)
    - volatile-random: 随机淘汰带有过期时间的 key
    - allkeys-random: 是随机的淘汰 key

### common command

1. bluk operation

   - mset/mget, hmset ...

2. 原子操作

   - incr/incrby

3. key 命令在数据很多时不建议使用: 消耗资源
   - 使用 scan 替代: cursor + key 的正则模式 + 遍历的 limit hint

### ~~sentinel~~

1. diagram

   ![avatar](/static/image/db/redis-sentinel.png)

2. 问题
   - 内存: 内存很难搞到很大: 一台 主从 电脑 嘛
   - 并发问题: 理论上 10w+ 就到极限了
   - 瞬断问题: master 挂了, 需要时间取选举出新的 master, 此时 redis 不能对外提供服务

### redis 高可用集群

![avatar](/static/image/db/redis-ha.png)

1. 解决的问题:

   - 内存: 每个集群 50 G \* 200 个 = 1T redis 空间
   - 并发: 每个主从 10w+ \* 200 个
   - 瞬断问题: 只有瞬断的哪一个在选举期间不能提供服务, 其他的主从 redis 小集群是可以提供服务的

2. 集群搭建

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
