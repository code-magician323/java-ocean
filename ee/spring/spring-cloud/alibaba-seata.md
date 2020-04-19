## seata

### 分布式事务的由来

1. 分布式事务的举例: 一起成功一起失败

   - 用户购买商品的业务逻辑. 整个业务逻辑由 3 个微服务提供支持:

     - 仓储服务: 对给定的商品扣除仓储数量
     - 订单服务: 根据术购需求创建订单
     - 帐户服务: 从用户帐户中扣除余额

   ![avatar](/static/image/spring/alibab-seata-logic.png)

### seata: 一款开源的分布式事务解决方案

1. seata terminology

   - XID: Transaction ID: 全局唯一的事务 ID
   - TC: Transaction Coordinator [事务协调者]: 维护全局和分支事务的状态, 驱动全局事务提交或回滚.
   - TM: Transaction Manager[事务管理器]: 定义全局事务的范围：开始全局事务、提交或回滚全局事务
   - RM: Resource Manager[资源管理器]: 管理分支事务处理的资源, 与 TC 交谈以注册分支事务和报告分支事务的状态, 并驱动分支事务提交或回滚.

2. processor

   ![avatar](/static/image/spring/alibab-seata-processor.png)

   - TM 向 TC 申请开启一个事物: 全局事务创建成功, 并生成一个全局唯一的 XID
   - XID 在微服务调用的链路上传播
   - RM 向 TC 注册分支事务, 将其纳入 XID 对应的事务内管理
   - TM 向 TC 发起针对 XID 的事务的提交或者回滚
   - TC 调度 XID 下管辖的全部分支事务提交或者回滚

   ![avatar](/static/image/spring/alibab-seata.png)

3. istall

   - docker

   ```yml
   seata-server:
     image: seataio/seata-server:1.0.0
     # hostname: seata-server
     restart: on-failure
     container_name: dev-seata
     ports:
       - '8091:8091'
     volumes:
       # file.conf modify db
       - /root/seata-server/resources/file.conf:/seata-server/resources/file.conf
       # registry.conf modify register center
       - /root/seata-server/resources/registry.conf:/seata-server/resources/registry.conf
     environment:
       - SEATA_PORT=8091
   ```

4. usage sample

   - function: `下订单 --> 减库存 --> 扣余额 --> 改状态[订单]`
   - modules

     - order:
     - storage:
     - account:

   - core

     - config server and client:

     ```json
     vgroup_mapping.cloud_seata_tx_group = "default"

     # should be match with seata server
     tx-service-group: cloud_seata_tx_group
     ```

   - code

   ```java
   @GlobalTransactional(name = "cloud_seata_tx_group")
   ```

## issue

1. java.net.SocketTimeoutException: Read timed out:
   ```log
   ### Cause: java.sql.SQLException: io.seata.core.exception.RmTransactionException: Response[ TransactionException[Could not register branch into global session xid = 192.168.43.143:8091:2040948595 status = Rollbacking whi ]
   ```
