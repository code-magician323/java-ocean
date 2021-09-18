## master and slave

1. 从库设置成 readonly

   - 防止误操作
   - 防止切换逻辑有 bug: 比如切换过程中出现双写
   - 可以用 readonly 状态, 来判断节点的角色
   - readonly 设置对超级(super)权限用户是无效的: 用于同步更新的线程, 就拥有超级权限

2. 主从同步大体过程:

   - 单主从

     ![avatar](/static/image/db/mysql-sm-flow.png)

   - 双 Master

     ![avatar](/static/image/db/mysql-sm-2m.png)

3. 双 Master 循环复制

   - 节点 A 和 B 之间总是互为主备关系
   - log_slave_updates: 从机在执行 relay log 后也会生成 binlog
   - 循环复制: 如果节点 A 同时是节点 B 的备库, 相当于又把节点 B 新生成的 binlog 拿过来执行了一次, 然后节点 A 和 B 间, 会不断地循环执行这个更新语句
   - 解决方法:
     1. 规定两个库的 server id 必须不同, 如果相同, 则它们之间不能设定为主备关系
     2. 一个备库接到 binlog 并在重放的过程中, 生成与原 binlog 的 server id 相同的新的 binlog
     3. 每个库在收到从自己的主库发过来的日志后, 先判断 server id, 如果跟自己的相同, 表示这个日志是自己生成的, 就直接丢弃这个日志
     4. 同步的过程中修改了 server id 还是会导致死循环的
   - 双 M 日志的执行流程
     1. 主节点节点 A 更新的事务, binlog 里面记的都是 A 的 server id
     2. 传到节点 B 执行一次以后, 节点 B 生成的 binlog 的 server id 也是 A 的 server id
     3. 再传回给节点 A, A 判断到这个 server id 与自己的相同, 就不会再处理这个日志

4. Mysq 主从复制的类型

   - 基于语句的复制: 时间上可能不完全同步造成偏差, 执行语句的用户也可能是不同一个用户
     1. delete 带 limit 可能会导致删除的数据不一致: 索引选取不一样导致的
     2. 记录的是: `执行的 SQL 本体`
   - **基于行的复制{恢复数据}**: 一条语句可以修改很多行: binlog_row_image=FULL|MINIMAL
     1. Table_map event 记录 test 库的表 t;
     2. Delete_rows 等 event: 详细的记录了被删除数据的每一列
     3. Xid 表示事务被正确地提交
     4. `mysqlbinlog -vv data/master.000001 --start-position=8900;`
     5. row 格式的缺点是, 很占空间: 比如 deleted10w+耗费 IO 资源, 影响执行速度
     6. update 语句的话, binlog 里面会记录修改前整行的数据和修改后的整行数据
   - 混合类型的复制: 先语句的复制, 有问题这回使用基于行的复制

5. Mysql 主从复制的工作原理

   ![avatar](/static/image/db/mysql-master-slave1.png)

   - 主服务器上的任何修改都会保存在二进制日志`Binary log`里面
   - 从服务器上面启动一个`I/O thread`[实际上就是一个主服务器的客户端进程], 连接到主服务器上面请求读取二进制日志
   - 然后把读取到的二进制日志写到本地的一个`Realy log`里面
   - 从服务器上面开启一个`SQL thread`定时检查`Realy log`, 如果发现有更改立即把更改的内容在本机上面执行一遍

   ![avatar](/static/image/db/mysql-master-slave.png)

   - 如果一主多从的话, 这时主库既要负责写又要负责为几个从库提供二进制日志, `比较不好`
   - 此时可以稍做调整, 将二进制日志只给某一从, 这一从再开启二进制日志并将自己的二进制日志再发给其它从
   - 或者是干脆这个从不记录只负责将二进制日志转发给其它从
   - 这样架构起来性能可能要好得多, 而且数据之间的延时应该也稍微要好一些

6. MySQL 的复制过滤[Replication Filters]

   - 复制过滤可以让你只复制服务器中的一部分数据

   ![avatar](/static/image/db/mysql-replication-filters.png)

   - 有两种复制过滤:

     - 在 Master 上过滤二进制日志中的事件
     - 在 Slave 上过滤中继日志中的事件

   - master config: my.cnf

     ```conf
     log_bin=mysql-bin
     server_id = 1
     binlog_do_db=DB1
     binlog_do_db=DB2 // 如果备份多个数据库, 重复设置这个选项即可
     binlog_do_db=DB3 // 需要同步的数据库, 如果没有本行, 即表示同步所有的数据库 binlog-ignore-db=mysql // 被忽略的数据库
     ```

   - slave config: my.cnf

     ```conf
     log_bin=mysql-bin
     server_id=2
     #replicate-do-db=DB1
     #replicate-do-db=DB2
     #replicate-do-db=DB3 //需要同步的数据库, 如果没有本行, 即表示同步所有的数据库
     #replicate-ignore-db=mysql // 被忽略的数据库
     ```

   - 不管是黑名单[binlog-ignore-db/replicate-ignore-db], 还是白名单[binlog-do-db/replicate-do-db]只写一个就行了, 如果同时使用那么只有白名单生效

7. Mysql 主从复制的过程

   - MySQL 主从复制的两种情况：同步复制和异步复制, 实际复制架构中大部分为异步复制。
   - 复制的基本过程如下：
     1. Slave 上面的 IO 进程连接上 Master, 并请求从指定日志文件的指定位置[或者从最开始的日志]之后的日志内容
     2. Master 接收到来自 Slave 的 IO 进程的请求后, 负责复制的 IO 进程会根据请求信息读取日志指定位置之后的日志信息, 返回给 Slave 的 IO 进程. 返回信息中除了日志所包含的信息之外, 还包括本次返回的信息已经到 Master 端的 bin-log 文件的名称以及 bin-log 的位置.
     3. Slave 的 IO 进程接收到信息后, 将接收到的日志内容依次添加到 Slave 端的 relay-log 文件的最末端, 并将读取到的 Master 端的 bin-log 的文件名和位置记录到 relay-info.log 文件中, 以便在下一次读取的时候能够清楚的告诉 Master 从某个 bin-log 的哪个位置开始往后的日志内容
     4. Slave 的 Sql 进程检测到 relay-log 中新增加了内容后,** 会马上解析 relay-log 的内容成为在 Master 端真实执行时候的那些可执行的内容**[慢的根源(随机写)], 并在自身执行

### master and slave 延时的原因

1. ~~slave 备份一般性能比 master 差~~
2. 备库充当读库: 写的压力在 master, 读的压力在 slave 读多会导致消耗大量资源, 使得 slave 同步速度减慢
3. 大事务: 比如一个事务 10min 那么要等到执行完成之后才会写 bin-log, 之后才会同步, 但是事务已经开始 10min 了, 从库也就有了 10 min 的延时
4. 主库可以是多线程的顺序写 binlog, 但是 slave 是单线程的顺序读随机写, 会变慢有延迟
5. 主库的 TPS 非常高, 产生的 DDL 的数量远超过一个线程的处理能力是会导致延迟
6. 在 bin-log 的读取写入和网络传输的过程中都可能带来延迟
7. 从库在于其他查询线程一起可能会有锁抢占的情况, 会造成延时

### master and slave delayed

1. master 写 bin-log 是顺序写, 所以很快
2. master 到 slave 的传输: 局域网或者专线, 所以很快
3. io 线程 到 relay-log 是顺序写, 所以很快
4. thread 线程的读取是顺序的[快], 但是写时随机的[慢](要不停的寻址修改)
5. 且 master 上是可以并发的, 但是 thread 线程只有一个, 会造成 relay-log 的堆积, 也会造成延迟
   - MTS(multi-thread slave): 要考虑多线程的顺序问题
6. MTS

   - 5.6 只能库级别的备份
   - 5.7 表
   - 5.7 行

   ```sql
   show variables like '%para%';
   +------------------------+----------+
   | slave_parallel_type    | DATABASE |
   | slave_parallel_workers | 0        |
   +------------------------+----------+
   ```

### mysql master and slave 延时问题的解决: `Seeconds_Behind_Master`

0. 适用场景

   - 读多写少的应用
   - 读的实时性要求不那么高

1. 架构方面

   - 业务分库层采用分库的架构, 分散单台机器的压力
   - 对于不经常修改的数据可以在 mysql 和 业务之间加入缓存层, 减小读的压力: 靠考虑命中率问题
   - 使用更好的硬件设备: CPU, SSD

2. slave 的配置问题

   - 设置合理的 sync_binlog 的参数值: 每个线程会有自己的 binlog cache, 公用一份 binlog, 事务提交时可以先写入文件系统的 page cache, 之后才调用 fsync
     1. 0 则每次事务提交只是 write 到 page cache, 没有立即调用 fsync
     2. 1[默认值]每次事务提交都调用 fysnc
     3. N 表示第 N 个事务时才调用 fsync
   - 禁用 slave 的 bin-log
   - 设置 innodb_flush_log_at_trx_commit

3. 使用 5.7 之后的 MTS[组提交的并行复制]

   ![avatar](/static/image/db/mysql-sm-delay.png)

   - binlog 的写操作被分为了两个阶段: prepare 阶段 + commit 阶段
   - binlog 和 redo-log 是同时写的
   - 先写 redo-log 在写 bin-log 会导致 redo-log 成功 bin-log 没写成功时的备份数据不一致问题
   - 先写 bin-log 在写 redo-log 会导致 bin-log 改了, 但是 redo-log 没写[事务失败], 也导致数据的不一致
   - 因此需要二阶段提交

   ![avatar](/static/image/db/mysql-ms-redo-log.png)

   - 组提交[undo-log/bin-log](https://mp.weixin.qq.com/s/_LK8bdHPw9bZ9W1b3i5UZA):

     1. 所有需要写到磁盘的数据都需要在当前进程的内存空间 --write-> 系统的内存空间 --fsync[尽可能多的一次性写更多的数据(组提交)]-> 刷盘
     2. 主要解决写日志时频繁 fsync 的问题

   - 5.7 的 MTS
     1. 设置 slave_parallel_type 值: DATABASE 按库进行并行策略, logical_clock 表示[https://blog.csdn.net/michaelyang_yz/article/details/79077588]
     2. 不是所有同时处于执行的事务都可以并行的[会有锁等待的问题(在 prepare 阶段之后就没有诉问题了)]
     3. 所以同时处于 prepare 的事务在 slave 是可以并行的
     4. prepare 和 commit 之间的事务也是可以并行的
     5. binlog_group_commit_sync_delay: 表示延迟多少微妙才调用 fsync
     6. binlog_group_commit_sync_no_delay_count: 表示累计多少次才调用 fsync

### mysql master and slave config

1.  step

    - 建立一个主节点, 开启 binlog, 设置服务器 id, 并授权 salve 的账户密码: 局域网内这个 id 必须唯一

    ```sql
    grant replication slave on *.* to 'backup'@'%' identified by '123456';
    FLUSH PRIVILEGES;
    ```

    - 建立一个从节点, 设置服务器 id
    - 将从节点连接到主节点上

          ```conf
          # version 1: sync all data[bin-log]
          change master to master_host='10.1.6.159', master_port=3306, master_user='rep', master_password='123456';
          start slave;
          show slave status;
          stop slave;

          # version 2: sync from specific position
          mysql> show binlog events\G # look up first bin-log file

          *************************** 1. row ***************************
          Log_name:      mysql-bin.000001
          Pos:           4
          Event_type:    Format_desc
          Server_id:     1
          End_log_pos:   107
          Info:          Server ver: 5.5.28-0ubuntu0.12.10.2-log, Binlog ver: 4

          *************************** 2. row ***************************
          Log_name:      mysql-bin.000001
          Pos:           107
          Event_type:    Query
          Server_id:     1
          End_log_pos:   181
          Info:          create user rep

          *************************** 3. row ***************************
          Log_name:      mysql-bin.000001
          Pos:           181
          Event_type:    Query
          Server_id:     1
          End_log_pos:   316
          Info:          grant replication slave on *.* to rep identified by '123456'

          3 rows in set (0.00 sec)

          # 为了防止在操作过程中数据更新, 导致数据不一致, 所以需要先刷新数据并锁定数据库:
          flush tables with read lock;

          # 检查当前的binlog文件及其位置
          show master status

          # 通过 mysqldump 命令创建数据库的逻辑备分
          mysqldump --all-databases -hlocalhost -p >back.sql

          # 有了 master 的逻辑备份后, 对数据库进行解锁
          unlock tables;

          # 把 back.sql 复制到新的 slave上执行
          mysql -hlocalhost -p # 把 master 的逻辑备份插入 slave 的数据库中

          # link slave to matser
          change master to master_host='10.1.6.159', master_port=3306, master_user='rep', master_password='123456',master_log_file='mysql-bin.000003', master_log_pos='107';
          # master_host: Master的地址
          # master_port: Master的端口号
          # master_user: 用于数据同步的用户
          # master_password: 用于同步的用户的密码
          # master_log_file: 指定 Slave 从哪个日志文件开始复制数据, 即上文中提到的 File 字段的值
          # master_log_pos: 从哪个 Position 开始读, 即上文中提到的 Position 字段的值
          # master_connect_retry: 如果连接失败, 重试的时间间隔, 单位是秒, 默认是60秒

          start slave;

          # 需要停止slave的同步
          stop slave;
          ```

2.  master: `/etc/my.cnf`

    ```conf
    [mysqld]
    server-id = 1
    log_bin = /var/log/mysql/mysql-bin.log
    binlog_do_db = test # 这个是你要同步的数据库
    binlog_ignore_db = mysql
    ```

3.  slave: `/etc/my.cnf`

    ```conf
    [mysqld]
    server_id = 2
    # 开启二进制日志功能, 以备 Slave 作为其它 Slave 的 Master 时使用
    log_bin=mysql-slave-bin
    # relay_log 配置中继日志
    relay_log=edu-mysql-relay-bi
    ```

4.  修改 slave 为 MTS

    ```sql
    -- 设置 worker 数量
    set global slave_parallel_workers=4
    set global slave_parallel_type=logical_clock
    show full processlist;
    ```

5.  可以适当的修改 master 的一下参数

    ```sql
    -- binlog_group_commit_sync_delay: 等多久之后才 commit
    -- binlog_group_commit_sync_no_delay_count
    ```

### docker mysql master and slave config

1. login master and grant

   ```sql
   flush logs;
   grant replication slave on *.* to 'backup'@'%' identified by '123456';
   FLUSH PRIVILEGES;
   ```

2. ~~login slave to create connectin without legacy data~~

   ```sql
   change master to master_host='dev-mysql-master', master_port=3306, master_user='root', master_password='Y***?';
   start slave;
   show slave status\G;
   stop slave;
   ```

3. handle legacy data

   ```sql
   -- master
   -- 1. frozen master data
   mysql> flush tables with read lock;
   -- 2. new terminal and backup data
   mysqldump -h 127.0.0.1 -uroot -proot --skip-comments --databases --compact -C -q -f db1 db2  db3 >> back.sql
   -- 3. obtain master bin-log postion
   mysql> show master status;
   -- 4. unfrozen master data
   mysql> unlock tables;


   -- slave
   -- 1. import data from backup
   $ mysql -uroot -proot <back.sql
   -- 2. set connection to master with bin-log position
   CHANGE MASTER TO MASTER_HOST='172.18.135.185',MASTER_PORT=3306,MASTER_USER='repl',
   MASTER_PASSWORD='repl',MASTER_LOG_FILE='mysql-bin.000028',MASTER_LOG_POS=4032;
   -- 3. enable slave
   mysql> start slave;
   ```

4. set slave read only

   ```sql
   -- set read only
   SET GLOBAL READ_ONLY=1;
   set global super_read_only=ON;
   ```

---

## issue list

1. 他还问主库挂了怎么办？
   - mysql 主从+keepalived/heartbeat: 有脑裂, 还是有前面丢数据问题
   - 用 [MMM](https://www.cnblogs.com/panwenbin-logs/p/8284593.html) 或 [HMA](https://www.cnblogs.com/JevonWei/p/7525924.html) 之类
   - 用 [ZK](https://www.cnblogs.com/robbinluobo/p/8294740.html) 之类

## Reference

1. https://segmentfault.com/a/1190000008942618
2. https://ejin66.github.io/2019/08/21/mysql-master-slave.html
3. https://www.jianshu.com/p/b0cf461451fb
4. http://www.r9it.com/20190727/mysql-master-slave-in-docker.html
