## ZooKeeper: ZOOKEEPER = 类似 UNIX 文件系统 + 通知机制 + ZNODE 节点

### 1. Introduce: `服务注册 + 分布式系统的一致性通知协调`

1. 定义:

   - 是基于 `观察者模式` 设计的 `分布式` 服务管理框架
   - 负责 **存储和管理** 大家**关心的数据**
   - 接受 `观察者` 的**注册**
   - 有完善的通知机制: 数据的状态变化时, Zookeeper 将通知已注册的**观察者**, 使其做出相应的改变
   - 实现集群中类似 Master/Slave 管理模式<br/>

2. 命名服务: 是将一个名称映射到与该名称有关联的一些信息的服务

3. 特点:

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

## conlusion

1. zookeeper = 类似 unix 文件系统 + 通知机制 + Znode 节点: 服务注册+分布式系统的一致性通知协调
2. 所有向 zookeeper 上注册的地址都是临时节点, 自动感应资源的变化.
