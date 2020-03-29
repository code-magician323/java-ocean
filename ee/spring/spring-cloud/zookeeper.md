## ZooKeeper: ZOOKEEPER = 类似 UNIX 文件系统 + 通知机制 + ZNODE 节点

### 1. Introduce: `服务注册 + 分布式系统的一致性通知协调`

1. definition:

   - 是基于 `观察者模式` 设计的 `分布式` 服务管理框架
   - 负责 **存储和管理** 大家**关心的数据**
   - 接受 `观察者` 的**注册**
   - 有完善的通知机制: 数据的状态变化时, Zookeeper 将通知已注册的**观察者**, 使其做出相应的改变
   - 实现集群中类似 Master/Slave 管理模式<br/>

2. 命名服务`[Name Service]`: 是将一个名称映射到与该名称有关联的一些信息的服务

3. feature:

   - 配置维护

     - **存储在 zookeeper 集群中的配置, 如果发生变更会主动推送到连接配置中心的应用节点, 实现一处更新处处更新的效果**

       ![avatar](/static/image/spring/cloud-zookeeper.png)

   - 集群管理
   - 分布式消息同步和协调机制
   - 对 Dubbo 的支持: Dubbo 是一个 `致力于高性能和透明化的 RPC 方案` 的分布式服务框架
     - Service Provider UP, 向 ZK 上的指定节点 `/dubbo/${serviceName}/providers` 目录下写入自己的 URL 地址, 就完成了服务的发布
     - Service Consumer UP, 订阅 `/dubbo/${serviceName}/providers` 目录下的 Provider URL 地址, 并向 `/dubbo/${serviceName}/consumers` 目录下写入自己的 URL 地址
     - Consumer and Provider 可以自动感应资源的变化: 所有向 ZK 上注册的地址都是临时节点
     - Dubbo 还有针对服务粒度的监控

4. Zoopkeeper 提供 `基于层次型的目录树的数据结构` 的分布式集群管理的机制:

   - 可以对树中的节点进行有效管理, 从而设计出多样的分布式的数管理模型作为分布式系统的沟通调度桥梁

5. 所有向 zookeeper 上注册的地址都是临时节点, 自动感应资源的变化.

---

### Install

1. Linux

- [docker](/common/docker/docker.md#6-install-container)

- Native

  ```shell
  # 1. download zookeeper-3.4.9.tar.gz

  # 2. mkdir and remove
  tar -zxvf /opt/zookeeper-3.4.9.tar.gz
  mkdir /opt/zookeeper
  mv /opt/zookeeper-3.4.10 /opt/zookeeper

  # 3. rename zoo_sample.cfg
  cp /opt/zookeeper/zookeeper-3.4.10/conf/zoo_sample.cfg /opt/zookeeper/zookeeper-3.4.10/conf/zoo.cfg

  # 4. install java

  # 5. 开启服务+客户端连接
  cd /opt/zookeeper/zookeeper-3.4.10/conf/bin
  ls -l *.sh
  ```

2. windows

3. explain

- zoo_sample.cfg

  - tickTime: 通信心跳数, zookeeper 服务器心跳时间, 单位毫秒;
    - 服务器之间或客户端与服务器之间维持心跳的时间间隔, 也就是每个 tickTime 时间
    - `session 的最小超时时间 2 * tickTime`
  - **[LF]**initLimit: 配置 Zookeeper 接收 Follower 客户端初始化连接时最长能忍受多少个心跳的时间间隔数
    - Follower 在启动过程中, 会从 Leader 同步所有最新数据, 然后确定自己能够对外服务的起始状态
    - **[Leader 允许 Follower 在 initLimit 时间内完成这个工作]**
  - **[LF]**syncLimit: LF 同步通信时限
    - 如果 L 发出心跳包在 syncLimit 之后，还没有从 F 那收到响应, 从服务器列表中删除 Follwer
  - dataDir: 数据文件目录 + 数据持久化路径
    - 保存内存数据库快照信息的位置
    - 默认更新的事务日志也保存到数据库
  - clientPort: 客户端连接端口
    - 监听客户端连接的端口

## usage

### common command

1. basic

   ```shell
   help
   ls PATH
   ls2 PATH: ls + stat
   stat PATH
   set PATH DATA
   get PATH
   create PATH DATA
   delete PATH
   rmr PATH
   ```

2. [custom](https://www.jianshu.com/p/c96c9f8c2433)

```shell
# install nc

# test server is in the correct state. If it does, then returns 'imok', otherwise it does nothing
echo ruok | nc 127.0.0.1 2181

# 输出关于性能和连接的客户端的列表
echo stat | nc 127.0.0.1 2181

# 输出相关服务配置的详细信息
echo conf | nc 127.0.0.1 2181

# 列出所有连接到服务器的客户端的完全的连接/会话的详细信息
# 包括 '接受/发送' 的包数量, 会话id , 操作延迟, 最后的操作执行等等信息
echo cons | nc 127.0.0.1 2181

# 列出未经处理的会话和临时节点
echo dump | nc 127.0.0.1 2181

# 输出关于服务环境的详细信息[区别于conf命令]
echo envi | nc 127.0.0.1 2181

# 列出未经处理的请求
echo reqs | nc 127.0.0.1 2181

# 列出服务器watch的详细信息
echo wchs | nc 127.0.0.1 2181

# 通过 session 列出服务器 watch 的详细信息
# 它的输出是一个与 watch 相关的会话的列表
# https://blog.csdn.net/x763795151/article/details/80599498
echo wchc | nc 127.0.0.1 2181

# 通过路径列出服务器 watch的详细信息。它输出一个与 session相关的路径
echo wchp | nc 127.0.0.1 2181
```

2. CRUD: like mysql

   ```sql
   -- create : create table + insert
   -- create [-s] [-e] path data acl
   create /ntu IOT
   create /ntu iot -- Node already exists: /ntu
   create -s /ntu iot -- Created /ntu0000000001
   create /ntu1 iot
   create -s /ntu iot -- Created /ntu0000000003
   create -e /temp iot
   create -e -s /temp iot

   -- get : select
   get /ntu -- IOT + stat

   - set : update
   set /ntu iot

   -- delete : delete
   delete /ntu
   ```

### file system

1. linux-like tree file structure
2. has root node
3. name + tree node: K-V

![avatar](/static/image/spring/cloud-zookeeper-file-system.png)

### notification mechanism

1. 客户端注册监听它关心的目录节点, 当目录节点发生变化[数据改变, 被删除,子目录节点增加删除]时, zookeeper 会通知客户端

   - 客户端可以在每个 znode 结点上设置一个观察, 节点不存在会报错
   - **`异步回调的触发机制`**
   - 在看到 watch 事件之前绝不会看到变化, `这样不同客户端看到的是一致性的顺序`
   - Watches 是在 Client 连接到 Zookeeper 服务端的`本地维护`, 这可让 watches 成为轻量的, 可维护的和派发的
   - 当一个 Client 连接到新 Server, watch 将会触发任何 session 事件, 断开连接后不能接收到
   - 当客户端重连, 先前注册的 watches 将会被重新注册并触发
   - `有且仅有`对还没有创建的节点设置存在观察, 而在断开连接期间创建节点并随后删除, 会导致观察事件将丢失

2. sequence: 关于 watches, Zookeeper 维护这些保证

   - Watches 和其他事件, watches 和异步恢复都是有序的. Zookeeper 客户端保证每件事都是有序派发
   - 客户端在看到新数据之前先看到 watch 事件
   - 对应更新顺序的 watches 事件顺序由 Zookeeper 服务所见

3. JAVA API

   ```java
   getData()
   getChildren()
   exists()
   ```

4. type

   - once: 一次触发
   - more
   - sample
     - https://github.com/Alice52/tutorials-sample/tree/master/java/javaee/spring-cloud/relevant

### znode: path + data + stat

- this is same as redis, mapping path to key and data to value

1. path: is uniquely identifies
2. data default size is 1M, and store configuration info
3. stat:

   - data version: node change will update this value
   - ctime + mtime + data version will maintain zookeeper's cache and cor-update
   - czxid: 引起这个 znode 创建的 zxid, 创建节点的事务的 zxid(ZooKeeper Transaction Id)
   - mzxid: znode 最后更新的 zxid
   - pZxid: znode 最后更新的子节点 zxid
   - cversion: znode 子节点变化号, znode 子节点修改次数
   - aclVersion: znode 访问控制列表的变化号
   - ephemeralOwner: 如果是临时节点,这个是 znode 拥有者的 session id; 如果不是临时节点则是 0
   - numChildren: znode 子节点数量

   ```yaml
   get /ntu         # path

   iot              # data

                    # stat
   cZxid = 0x2
   ctime = Sat Mar 28 16:30:30 CST 2020
   mZxid = 0x4
   mtime = Sat Mar 28 16:34:10 CST 2020
   pZxid = 0x2
   cversion = 0
   dataVersion = 1
   aclVersion = 0
   ephemeralOwner = 0x0
   dataLength = 3
   numChildren = 0
   ```

4. znode type: temp + persistent
   - persistent
   - persistent_sequential
   - ephemeral
   - ephemeral_sequential

## cluster

- config

  ```yaml
  version: '2'
    networks:
      zk:
    services:
      zk1:
        image: zookeeper:3.4.10
        restart: always
        hostname: zoo1
        container_name: dev-zookeeper01
        networks:
            - zk
        ports:
            - "21811:2181"
        volumes:
            - /root/zookeeper/zoo1/data:/data
            - /root/zookeeper/zoo1/datalog:/datalog
        environment:
          ZOO_MY_ID: 1
          ZOO_SERVERS: server.1=0.0.0.0:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888
      zk2:
       image: zookeeper:3.4.10
        restart: always
        hostname: zoo2
        container_name: dev-zookeeper02
        volumes:
            - /root/zookeeper/zoo2/data:/data
            - /root/zookeeper/zoo2/datalog:/datalog
        networks:
            - zk
        ports:
            - "21812:2181"
        environment:
          ZOO_MY_ID: 2
          ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=0.0.0.0:2888:3888 server.3=zoo3:2888:3888
      zk3:
        image: zookeeper:3.4.10
        restart: always
        hostname: zoo3
        container_name: dev-zookeeper03
        volumes:
            - /root/zookeeper/zoo3/data:/data
            - /root/zookeeper/zoo3/datalog:/datalog
        networks:
            - zk
        ports:
            - "21813:2181"
        environment:
          ZOO_MY_ID: 3
          ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=0.0.0.0:2888:3888
  ```

## conlusion

1. zookeeper = 类似 unix 文件系统 + 通知机制 + Znode 节点: 服务注册+分布式系统的一致性通知协调
2. 所有向 zookeeper 上注册的地址都是临时节点, 自动感应资源的变化.
