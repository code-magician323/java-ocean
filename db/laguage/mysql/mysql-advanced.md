## architecture

### introduce

1. feature: 插件式的存储引擎架构将查询处理与其他的系统任务以及数据存储提取相分离
2. install

   - ubuntu

   ```shell
   sudo apt-get install mysql-server

   # remote method1
   mysql -uroot -p
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Yu***2?' WITH GRANT OPTION;
   sudo service mysql start/stop/restart

   #  remote method2
   cp /etc/mysql/mysql.conf.d/mysqld.cnf /etc/mysql/mysql.conf.d/mysqld.cnf.bak
   vim /etc/mysql/mysql.conf.d/mysqld.cnf
   # change 127.0.0.1 to 0.0.0.0
   ```

   - centos

   ```shell
   # 1. config yum
   # http://dev.mysql.com/downloads/repo/yum/
   rpm -qa | grep -i mysql
   wget http://dev.mysql.com/get/mysql57-community-release-el7-8.noarch.rpm
   yum localinstall mysql57-community-release-el7-8.noarch.rpm
   yum repolist enabled | grep "mysql.*-community.*"

   # 2. choose version and install
   vim /etc/yum.repos.d/mysql-community.repo
   yum install mysql-community-server
   systemctl/service start mysqld

   # 3. check install
   cat /etc/passwd | grep mysql
   cat /etc/group | grep mysql
   mysql --version

   # 4. change pwd
   grep 'temporary password' /var/log/mysqld.log # can see pwd
   mysql -uroot -p
   set password for 'root'@'localhost'=password('Yu***2?'); / ALTER USER 'root'@'localhost' IDENTIFIED BY 'Yu***2?';

   # 5. pwd validate policy
   cp /etc/my.cnf /etc/my.cnf.bak
   vim /etc/my.cnf
   # 0[LOW],1[MEDIUM], 2[STRONG]
   validate_password_policy=0 # validate_password = off
   systemctl restart mysqld # restart

   # 6. set auto start
   systemctl enable mysqld
   systemctl daemon-reload
   # method2: doubt
   chkconfig mysql on

   # 7. remote connect
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Yu***2?' WITH GRANT OPTION;

   # 8. set encoding
   vim /etc/my.cnf
   # [mysqld]
   # character_set_server=utf8mb4
   # init_connect='SET NAMES utf8mb4'

   # 9. file explain
   # config: /etc/my.cnf
   # log: /var/log//var/log/mysqld.log
   # start script: /usr/lib/systemd/system/mysqld.service
   # socket: /var/run/mysqld/mysqld.pid
   ```

3. set set collection

   - look up

   ```sql
   -- look up set collection
   SHOW VARIABLES LIKE 'character%'
   -- look up engine
   SHOW VARIABLES LIKE '%storage_engine%';
   ```

   ```shell
   vim /etc/mysql/mysql.conf.d/mysqld.cnf

   [client]
   default-character-set = utf8mb4

   [mysql]
   default-character-set = utf8mb4

   [mysqld]
   character-set-server = utf8mb4
   collation-server = utf8mb4_general_ci
   init_connect='SET NAMES utf8mb4'

   # restart
   service mysql restart
   ```

   - issue: Garbled still after mofidy mysql.cnf
     > because when create database, it is not utf8 set collection. It can fixed by restart

### config file

1. log-bin: Master-slave replication
   - default: disable
2. log-error: error log
   - default: disable
3. low query log
   - default: disable
4. data file
   - [x] frm: table struct
   - [x] idb: data + index
   - [ ] myd: data
   - [ ] myi: index

### logic architecture

1. diagram
   ![avatar](/static/image/db/mysql-logic.bmp)
   // TODO: refine code and remove below image
   ![avatar](/static/image/db//mysql-logic-explain.bmp)

2. Connectors: interaction with different languages

3. Management Serveices & Utilities: System management and control tools

4. Connection Pool:

   ```txt
   管理缓冲用户连接、线程处理等需要缓存的需求。
   负责监听对 MySQL Server 的各种请求，接收连接请求，转发所有连接请求到线程管理模块。每一个连接上 MySQL Server 的客户端请求都会被分配（或创建）一个连接线程为其单独服务。而连接线程的主要工作就是负责 MySQL Server 与客户端的通信，
   接受客户端的命令请求，传递 Server 端的结果信息等。线程管理模块则负责管理维护这些连接线程。包括线程的创建，线程的 cache 等。
   ```

5. SQL Interface

   ```txt
   接受用户的SQL命令，并且返回用户需要查询的结果。比如select from就是调用SQL Interface
   ```

6. Parser:

   ```txt
   SQL命令传递到解析器的时候会被解析器验证和解析。解析器是由 Lex 和 YACC 实现的，是一个很长的脚本。
   在 MySQL 中我们习惯将所有 Client 端发送给 Server 端的命令都称为 query ，在 MySQL Server 里面，连接线程接收到客户端的一个 Query 后，会直接将该 query 传递给专门负责将各种 Query 进行分类然后转发给各个对应的处理模块。
   主要功能：
   a . 将SQL语句进行语义和语法的分析，分解成数据结构，然后按照不同的操作类型进行分类，然后做出针对性的转发到后续步骤，以后SQL语句的传递和处理就是基于这个结构的。
   b.  如果在分解构成中遇到错误，那么就说明这个sql语句是不合理的
   ```

7. Optimizer: `选取-投影-联接`

   ```txt
   SQL 语句在查询之前会使用查询优化器对查询进行优化。就是优化客户端请求的 query（sql语句）， 根据客户端请求的 query 语句，和数据库中的一些统计信息，在一系列算法的基础上进行分析，得出一个最优的策略，告诉后面的程序如何取得这个 query 语句的结果
   他使用的是“选取-投影-联接”策略进行查询。
       用一个例子就可以理解： select uid,name from user where gender = 1;
       这个 select 查询先根据 where 语句进行选取，而不是先将表全部查询出来以后再进行 gender 过滤
       这个 select 查询先根据 uid 和 name 进行属性投影，而不是将属性全部取出以后再进行过滤
       将这两个查询条件联接起来生成最终查询结果
   ```

8. Cache and Buffer

   ```txt
   他的主要功能是将客户端提交给 MySQL 的 Select 类 query 请求的返回结果集 cache 到内存中，与该 query 的一个 hash 值做一个对应。该 Query 所取数据的基表发生任何数据的变化之后， MySQL 会自动使该 query 的 Cache 失效。在读写比例非常高的应用系统中， Query Cache 对性能的提高是非常显著的。当然它对内存的消耗也是非常大的。
   如果查询缓存有命中的查询结果，查询语句就可以直接去查询缓存中取数据。这个缓存机制是由一系列小缓存组成的。比如表缓存，记录缓存，key 缓存，权限缓存等
   ```

9. 存储引擎接口

   ```txt
   存储引擎接口模块可以说是 MySQL 数据库中最有特色的一点了。目前各种数据库产品中，基本上只有 MySQL 可以实现其底层数据存储引擎的插件式管理。这个模块实际上只是 一个抽象类，但正是因为它成功地将各种数据处理高度抽象化，才成就了今天 MySQL 可插拔存储引擎的特色。
       从图2还可以看出，MySQL区别于其他数据库的最重要的特点就是其插件式的表存储引擎。MySQL插件式的存储引擎架构提供了一系列标准的管理和服务支持，这些标准与存储引擎本身无关，可能是每个数据库系统本身都必需的，如SQL分析器和优化器等，而存储引擎是底层物理结构的实现，每个存储引擎开发者都可以按照自己的意愿来进行开发。
       注意：存储引擎是基于表的，而不是数据库。
   ```

### store engine

1. look up

   ```sql
   SHOW VARIABLES LIKE '%storage_engine%';
   ```

2. diff between MyISAM and InnoDB

|      type       |   MyISAM    |                 InnoDB                 |
| :-------------: | :---------: | :------------------------------------: |
|       PK        |     no      |                  yes                   |
|   transaction   |     no      |                  yes                   |
|   table lock    |     yes     |                  yes                   |
|    raw lock     |     no      |                  yes                   |
|      cache      | cache index | cache index and data, need more memory |
|   table space   |     low     |                  high                  |
|   focus point   | performance |              transaction               |
| default install |     yes     |                  yes                   |

## index

### introduce

### join

### index introduce

### perfomance analysis

### index optimization

## query analysis

### slow query optimization

### slow query log

### bulk script

### show profile

### globel query log

## lock

### table locks[perfer read]

#### read lock

- env: session01 have read lock, session2 no limit

- session01:

  - [read lock table] session01 just can read lock table
  - [read others] even cannot read other tables
  - [update lock table] cannot update this table
  - [update others] cannot update operation until unlock

- session02:
  - [read lock table] can read session01 locked table: `because read lock is shared`
  - [read others] can read other tables
  - [update lock table] blocked by session01 until session01 unlock table，`then finish update operation`.
  - [update others] can update others table without limit

#### write lock

### row locks[perfer write]

### leaf lock[less use]

## master-slave replication

### theory of replication

### principle of replication

### problem

### config

---

## issue

1. sql load

   - from first

2. join
   > from a, b: 笛卡尔积
   > from a, b where a.BId = b.AId: inner join
