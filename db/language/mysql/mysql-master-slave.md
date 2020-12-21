## master and slave

1. Mysq主从复制的类型

    - 基于语句的复制: 时间上可能不完全同步造成偏差, 执行语句的用户也可能是不同一个用户
    - **基于行的复制**: 一条语句可以修改很多行
    - 混合类型的复制: 先语句的复制, 有问题这回使用基于行的复制

2. Mysql主从复制的工作原理

    ![avatar](/static/image/db/mysql-master-slave1.png)

    - 主服务器上的任何修改都会保存在二进制日志`Binary log`里面
    - 从服务器上面启动一个`I/O thread`[实际上就是一个主服务器的客户端进程], 连接到主服务器上面请求读取二进制日志
    - 然后把读取到的二进制日志写到本地的一个`Realy log`里面
    - 从服务器上面开启一个`SQL thread`定时检查`Realy log`, 如果发现有更改立即把更改的内容在本机上面执行一遍

    ![avatar](/static/image/db/mysql-master-slave.png)

    - 如果一主多从的话, 这时主库既要负责写又要负责为几个从库提供二进制日志, `比较不好`
    - 此时可以稍做调整, 将二进制日志只给某一从, 这一从再开启二进制日志并将自己的二进制日志再发给其它从
    - 或者是干脆这个从不记录只负责将二进制日志转发给其它从
    - 这样架构起来性能可能要好得多，而且数据之间的延时应该也稍微要好一些

3. MySQL的复制过滤[Replication Filters]
    - 复制过滤可以让你只复制服务器中的一部分数据

    ![avatar](/static/image/db/mysql-replication-filters.png)

    - 有两种复制过滤:
        - 在Master上过滤二进制日志中的事件
        - 在Slave上过滤中继日志中的事件

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

4. Mysql主从复制的过程

    - MySQL主从复制的两种情况：同步复制和异步复制，实际复制架构中大部分为异步复制。
    - 复制的基本过程如下：
        1. Slave上面的IO进程连接上Master, 并请求从指定日志文件的指定位置[或者从最开始的日志]之后的日志内容
        2. Master 接收到来自 Slave 的 IO 进程的请求后, 负责复制的 IO 进程会根据请求信息读取日志指定位置之后的日志信息, 返回给Slave的IO进程. 返回信息中除了日志所包含的信息之外, 还包括本次返回的信息已经到Master端的bin-log文件的名称以及bin-log的位置.
        3. Slave 的 IO 进程接收到信息后, 将接收到的日志内容依次添加到 Slave 端的 relay-log 文件的最末端, 并将读取到的 Master 端的 bin-log 的文件名和位置记录到 master-info 文件中, 以便在下一次读取的时候能够清楚的告诉 Master 从某个bin-log的哪个位置开始往后的日志内容
        4. Slave 的 Sql 进程检测到 relay-log 中新增加了内容后, 会马上解析 relay-log 的内容成为在 Master 端真实执行时候的那些可执行的内容, 并在自身执行


### mysql master and slave config

1. step
   - 建立一个主节点, 开启binlog, 设置服务器id, 并授权 salve 的账户密码: 局域网内这个id必须唯一

    ```sql
    grant replication slave on *.* to 'backup'@'%' identified by '123456';
    FLUSH PRIVILEGES;
    ```

   - 建立一个从节点, 设置服务器id
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

1. master: `/etc/my.cnf`

    ```conf
    [mysqld]
    server-id = 1
    log_bin = /var/log/mysql/mysql-bin.log
    binlog_do_db = test # 这个是你要同步的数据库
    binlog_ignore_db = mysql
    ```
2. slave: `/etc/my.cnf`

    ```conf
    [mysqld]
    server_id = 2
    # 开启二进制日志功能, 以备 Slave 作为其它 Slave 的 Master 时使用
    log_bin=mysql-slave-bin
    # relay_log 配置中继日志
    relay_log=edu-mysql-relay-bi
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

## Reference

1. https://segmentfault.com/a/1190000008942618
2. https://ejin66.github.io/2019/08/21/mysql-master-slave.html
3. https://www.jianshu.com/p/b0cf461451fb
4. http://www.r9it.com/20190727/mysql-master-slave-in-docker.html
