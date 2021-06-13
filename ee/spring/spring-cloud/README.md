## Spring Cloud

- Spring Cloud 分布式微服务架构设计的一站式解决方案, 是多种微服务架构落地的集合体

### knowledge list

![avatar](/static/image/spring/cloud-knowledge-list.png)

1. [服务注册中心](./02.服务注册中心.md):

   - ~~Eureka~~
   - Zookeeper
   - Consul
   - Nacos

2. [服务调用](./03.服务调用.md):

   - ~~Feign~~
   - OpenFeign
   - Ribbon
   - LoadBalance

3. [服务降级](./04.服务降级.md):

   - ~~Hystrix~~
   - resilience4j
   - sentinel

4. [服务网关](./05.服务网关.md)

   - ~~Zuul~~
   - Zuul2
   - gateway

5. [服务配置](./06.服务配置.md)

   - ~~Config~~
   - Nacos

6. [服务总线](./07.服务总线.md)

   - ~~Bus~~
   - Nacos

7. others
   - 服务熔断:
   - 负载均衡
   - 服务消息队列
   - 配置中心管理
   - 服务监控
   - 全链路追踪
   - 自动化构建部署
   - 服务定时任务调度操作
   -

## content

1. 微服务架构概述

   - small service
   - own process
   - lightweight mechanisms[轻量级机制]
   - independency deployable

2. Spring Cloud 简介

   - 主流的微服务架构
     ![avatar](/static/image/spring/cloud-usage.png)

3. Spring Cloud 技术栈

## pre-environment

1. version

   - cloud: H.SR1
   - boot: 2.2.2.RELEASE
   - alibaba: 2.1.0.RELEASE
   - java: JDK8 or later
   - mysql: 5.7 or later
   - maven: 3.5 or later
   - JDK 11

---

## micro service

1. 是什么

   - 区别于系统, 是一个或者一组相对较小且独立的功能单元
   - 根据业务换分 + 微服务的职责单一原则
   - 可以独立运行部署迭代的
   - 不同的服务可以语言不限 + 使用轻量级机制通信, 通常是 HTTP API
   - 约定大于配置大于编码

2. 有什么

   - 服务治理: 服务注册[注册到注册中心] + 服务发现[从注册中心获取服务信息] + 服务治理[管理服务之间的关系:lb+call+fallback]

     - nacos/Consul/Eureka/zookeeper

       |   type    | language |   CAP   | health check | expose protocol | integration cloud | UI  |
       | :-------: | :------: | :-----: | :----------: | :-------------: | :---------------: | :-: |
       |  eureka   |   java   |   AP    |   configed   |      HTTP       |        yes        | yes |
       |  consul   |    go    |   CP    |     yes      |    HTTP/DNS     |        yes        | yes |
       | zookeeper |   java   |   CP    |     yes      |     Client      |        yes        | no  |
       |   nacos   |   java   | AP + CP |     yes      |       --        |        yes        | yes |

       |         type         |          nacos           | eureka |      consul       | coreDNS | zookeeper |
       | :------------------: | :----------------------: | :----: | :---------------: | :-----: | :-------: |
       |         CAP          |         CP + AP          |   AP   |        CP         |   --    |    CP     |
       |     health check     |   TCP/HTTP/MYSQL/BEAT    |  BEAT  | TCP/HTTP/gRPC/Cmd |   --    |   BEAT    |
       |          LB          | weight/DSL/metadata/CMDB | Ribbon |       Fabio       |   RR    |    --     |
       | Avalanche protection |           yes            |  yes   |        no         |   no    |    no     |
       | auto logout instance |           yes            |  yes   |        no         |   no    |    yes    |
       |       protocol       |       HTTP/DNS/UDP       |  HTTP  |     HTTP/DNS      |   DNS   |    TCP    |
       |       monitor        |           yes            |  yes   |        yes        |   no    |    yes    |
       |  multi data center   |           yes            |  yes   |        yes        |   no    |    no     |
       |    Cross-registry    |           yes            |   no   |        yes        |   no    |    no     |
       |  cloud integration   |           yes            |  yes   |        yes        |   no    |    no     |
       |        dubbo         |           yes            |   no   |        no         |   no    |    no     |
       |   k8s integration    |           yes            |   no   |        yes        |   yes   |    no     |

   - 服务负载均衡 Ribbon[`将用户请求平摊到多个服务上, 以达到 HA 的目的, 防止某个实例被打死`]: lb[ngixn/lvs]

     1. 客户端负载均衡
     2. RoundRobinRule: 轮询
     3. RandomRule: 随机
     4. WeightedResponseTimeRule: 响应速度越快, 权重越大, 越容易被选
     5. RetryRule: 按照 RoundRobinRule 获取服务, 获取失败在指定时间内重试
     6. BestAvailableRule: 会先去除由于多次访问故障而处于`断路器`跳闸的服务, 然后选择一个并发量最小的服务
     7. AvailabilityFilteringRule: 去除故障, 选择并发量最小的实例
     8. ZoneAvoidanceRule: 复合判断`SERVER 所在区域的性能和可用性`

   - 服务调用: openfeign[resttamplate]+grpc
     - 从服务注册中心获取服务提供者, 之后调用[不需要经过服务注册中心]
     - client 本地会维护一份服务提供者[with timeout]
   - 服务网管: 所有流量的入口[一定要高性能], 做路由分发

     - gateway: reactor-netty 性能非常高 + filter 的模式
     - ~~Zuul~~

   - 服务配置: 为微服务提供集中式的的中心化的外部配置, 方便管理, 运行期间动态调整
     - Config
     - nacos
   - ~~服务总线~~
   - 服务降级: 容错 + 避免级联故障/服务雪崩 + up 系统弹性
     - 断路器：故障监控 + 服务熔断
   - 服务限流: `QPS`

     - 流量太多会导致本来可以快速响应的接口也变慢了
     - 因为很多资源拿去应对并发了

   - 服务监控
     - 在微服务模块中, 一个由客户端发起的请求会经过不同的服务节点调用来协同产生最后的请求结果
     - 每个请求都会形成一个复杂的分布式调用链路
     - 链路的任何一环出现高延迟或者错误都会引起整个请求的失败

3. 为什么

   - 为什么: 优点+缺点

4. 优点是什么?

   - 管理: 网警[code rush] + 360 两个例子: `我们就是一批人5+4负责一个微服务的开发`
     - 单体应用的管理迭代很困难的`[很多人协作,任意一个问题都会导致整个应用的 block]`, 复杂度不好控制[`模块分层+核心代码沉淀+接口抽象`], 耦合严重, 对新人十分的不友好[互联网的人员流动很频繁]
     - 团队太大不好管理, 如果一个单体应用大几十个人一起开发会有很大的交流成本: `信息的衰减性 + 一个人正常可以 hold 的住的有效交流人群大概就 20 左右`
     - 微服务每个服务人员不多, 且不同的服务之间完全黑盒: `无论时压力还是性能都被分割了`
     - 服务的代码质量被隔离[复杂度较单体应用有很大的下降, 实在看不下去了就可以找人完全重构]
     - **微服务每个项目的复杂度很小, 对新人十分的友好**
   - 平滑的升级[不停机]: 多实例一台一台升级
   - 服务的稳定性的到保证: 其中一个实例挂了, 服务还能继续运行
   - 不同的业务可以实现解耦: 部分服务的可用[发帖挂了, 完全不会影响我们的活动模块]
   - 动态增加实例可以应对流量: 之后在去掉, 节约成本
   - 减少每个人在本地都能 build 过, 但是合并后就不可以了情况
   - 开发方面: 可以多个微服务同时进行加快开发迭代周期, **快速迭代应对市场**

5. 缺点
   - docker 内存消耗: 每个实例的 jvm 就会占用很多内存
   - k8s 这一套也会消耗资源
   - 微服务的划分时很难界定的: `我们根据业务去划分微服务的, 都是自己写的 feign`
   - 分布式事物问题
   - 重试问题带来的幂等问题
   - **分布式任务: 一个单独的服务 quartz + feign**
   - 增加了这个系统的复杂度 & 学习成本
   - 数据链路会超长: frontend -- nginx -- gateway -- auth -- service
   - 网络开销: 专属网络[还好]
   - 错误追踪: 以上链路任意一个错了都会导致请求的失败
