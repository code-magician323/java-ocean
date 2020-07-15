**Table of Contents** _generated with [DocToc]()_

- [1. architecture](#1-architecture)
  - [1.1 introduce](#11-introduce)
  - [1.2 config file](#12-config-file)
  - [1.3 logic architecture](#13-logic-architecture)
  - [1.4 store engine](#14-store-engine)
- [2. INDEX](#2-index)
  - [2.1 introduce](#21-introduce)
  - [2.2 join](#22-join)
  - [2.3 INDEX introduce](#23-index-introduce)
  - [2.4 perfomance analysis](#24-perfomance-analysis)
  - [2.5 INDEX optimization](#25-index-optimization)
- [3. query analysis](#3-query-analysis)
  - [3.1 slow query optimization](#31-slow-query-optimization)
  - [3.2 slow query log](#32-slow-query-log)
  - [3.3 bulk script](#33-bulk-script)
  - [3.4 show profile](#34-show-profile)
  - [3.5 globel query log](#35-globel-query-log)
- [4. lock](#4-lock)
  - [4.1. table locks[perfer read]](#41-table-locksperfer-read)
    - [4.1.1 read lock](#411-read-lock)
    - [4.1.2 write lock](#412-write-lock)
  - [4.2 row locks[perfer write]](#42-row-locksperfer-write)
  - [4.3 leaf lock[less use]](#43-leaf-lockless-use)
- [5. transaction](#5-transaction)
- [6. master-slave replication](#6-master-slave-replication)
  - [6.1 theory of replication](#61-theory-of-replication)
  - [6.2 principle of replication](#62-principle-of-replication)
  - [6.3 config](#63-config)
- [sample](#sample)
- [issue](#issue)
- [reference](#reference)

## 1. architecture

### 1.1 introduce

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
   # need python2
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
   cp /etc/mysql/mysql.conf.d/mysqld.cnf /etc/mysql/mysql.conf.d/mysqld.cnf.bak
   vim /etc/mysql/mysql.conf.d/mysqld.cnf
   # 0[LOW],1[MEDIUM], 2[STRONG]
   validate_password_policy=0 # validate_password = off
   systemctl restart mysqld # restart

   # 6. set auto start
   systemctl enable mysqld
   systemctl daemon-reload
   # method2: doubt
   chkconfig mysql on

   # 7. remote connect
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Yu**82?' WITH GRANT OPTION;

   # 8. set encoding
   vim /etc/mysql/mysql.conf.d/mysqld.cnf
   # [mysqld]
   # character_set_server=utf8mb4
   # init_connect='SET NAMES utf8mb4'
   # 1 donot; 0 do
   # lower_case_table_names=1

   # 9. file explain
   # config: /etc/mysql/mysql.conf.d/mysqld.cnf
   # log: /var/log/mysqld.log
   # lib: /var/lib/mysql
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
   -- look up slow_query_log
   SHOW VARIABLES LIKE '%slow_query_log%'
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
   service mysqld restart
   ```

   - issue: Garbled still after mofidy mysql.cnf
     > because when create database, it is not utf8 set collection. It can fixed by restart

### 1.2 config file

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

### 1.3 logic architecture

1. diagram
   ![avatar](/static/image/db/mysql-logic.bmp)

- explain

  - Connectors:

  ```txt
  最上层是一些客户端和连接服务包含本地 socket 通信和大多数基于客户端/服务器端工具实现的类似于 TCP/IP 的通信;
  主要完成一些类似于连接处理、授权认证、及相关的安全方案.
  在该层上引入了线程池的概念, 为通过认证安全接入的客户端提供线程.
  同样可以在该层上实现基于 SSL 的安全链接.
  服务器也会为安全连入的每个客户端验证其所具有的的操作权限
  ```

  - Services

  ```txt
  该层主要完成核心服务功能, 如 SQL 的接口, 并完成缓存的查询, SQL 的分析和优化以及部分内置函数的执行.
  所有跨存储引擎的功能都在这一层完成, 如过程/函数
  在该层服务器会解析查询并创建相应的内部解析树, 并对其完成相应的优化: 如确定查询标的顺序、是否利用 INDEX, 最后生成相应的执行操作.
  如果是 SELECT 语句, 服务器会查询内部的缓存: 如果缓存空间足够大,  在有大量的读操作的环境中性能优
  ```

  - Engines

  ```txt
  存储引擎真正的负责 MySQL 中数据的存储和提取, 服务器通过 API 与存储引擎进行通信交互.
  不同的存储引擎具有不同的功能, 主要使用 MyISAM, InnoDB
  ```

  - stores

  ```txt
  主要负责将数据存储到运行与裸设备的文件系统之上, 并完成与存储引擎的交互
  ```

2. Connectors: interaction with different languages

3. Management Serveices & Utilities: System management and control tools

4. Connection Pool:

   ```txt
   管理缓冲用户连接、线程处理等需要缓存的需求.
   负责监听对 MySQL Server 的各种请求, 接收连接请求, 转发所有连接请求到线程管理模块.每一个连接上 MySQL Server 的客户端请求都会被分配(或创建)一个连接线程为其单独服务.而连接线程的主要工作就是负责 MySQL Server 与客户端的通信,
   接受客户端的命令请求, 传递 Server 端的结果信息等.线程管理模块则负责管理维护这些连接线程.包括线程的创建, 线程的 cache 等.
   ```

5. SQL Interface

   ```txt
   接受用户的SQL命令, 并且返回用户需要查询的结果.比如select from就是调用SQL Interface
   ```

6. Parser:

   ```txt
   SQL命令传递到解析器的时候会被解析器验证和解析.解析器是由 Lex 和 YACC 实现的, 是一个很长的脚本.
   在 MySQL 中我们习惯将所有 Client 端发送给 Server 端的命令都称为 query , 在 MySQL Server 里面, 连接线程接收到客户端的一个 Query 后, 会直接将该 query 传递给专门负责将各种 Query 进行分类然后转发给各个对应的处理模块.
   主要功能：
   a . 将SQL语句进行语义和语法的分析, 分解成数据结构, 然后按照不同的操作类型进行分类, 然后做出针对性的转发到后续步骤, 以后SQL语句的传递和处理就是基于这个结构的.
   b.  如果在分解构成中遇到错误, 那么就说明这个sql语句是不合理的
   ```

7. Optimizer: `选取-投影-联接`

   ```txt
   SQL 语句在查询之前会使用查询优化器对查询进行优化.就是优化客户端请求的 query(sql语句),  根据客户端请求的 query 语句, 和数据库中的一些统计信息, 在一系列算法的基础上进行分析, 得出一个最优的策略, 告诉后面的程序如何取得这个 query 语句的结果
   他使用的是“选取-投影-联接”策略进行查询.
       用一个例子就可以理解： select uid,name from user where gender = 1;
       这个 select 查询先根据 where 语句进行选取, 而不是先将表全部查询出来以后再进行 gender 过滤
       这个 select 查询先根据 uid 和 name 进行属性投影, 而不是将属性全部取出以后再进行过滤
       将这两个查询条件联接起来生成最终查询结果

   当客户端向 MYSQL 请求一条 QUERY, Optimizer 完成请求你分类, 区别出 SELECT 并转发给 MYSQL-QUERY-Optimizer 时,
   MYSQL-QUERY-Optimizer 会首先对整条 QUERY 优化, 处理掉一些常量表达式的预算[直接换成常量值].
   并对 QUERY 中的查询条件进行简化和转换, 如去掉一些无用和显而易见的条件、结构调整等.
   然后分析 QUERY 中的 HINT 信息(如果有), 看显示 HINT 信息是否可以完全确定该 QUERY 的执行计划;
   如果没有 HINT 信息或者信息不足时, 则会读取锁涉及的对象的统计信息, 根据 QUERY 进行计算分析, 然后在得出最后的执行计划
   ```

8. Cache and Buffer

   ```txt
   他的主要功能是将客户端提交给 MySQL 的 Select 类 query 请求的返回结果集 cache 到内存中, 与该 query 的一个 hash 值做一个对应.该 Query 所取数据的基表发生任何数据的变化之后,  MySQL 会自动使该 query 的 Cache 失效.在读写比例非常高的应用系统中,  Query Cache 对性能的提高是非常显著的.当然它对内存的消耗也是非常大的.
   如果查询缓存有命中的查询结果, 查询语句就可以直接去查询缓存中取数据.这个缓存机制是由一系列小缓存组成的.比如表缓存, 记录缓存, key 缓存, 权限缓存等
   ```

9. 存储引擎接口

   ```txt
   存储引擎接口模块可以说是 MySQL 数据库中最有特色的一点了.目前各种数据库产品中, 基本上只有 MySQL 可以实现其底层数据存储引擎的插件式管理.这个模块实际上只是 一个抽象类, 但正是因为它成功地将各种数据处理高度抽象化, 才成就了今天 MySQL 可插拔存储引擎的特色.
       从图2还可以看出, MySQL区别于其他数据库的最重要的特点就是其插件式的表存储引擎.MySQL插件式的存储引擎架构提供了一系列标准的管理和服务支持, 这些标准与存储引擎本身无关, 可能是每个数据库系统本身都必需的, 如SQL分析器和优化器等, 而存储引擎是底层物理结构的实现, 每个存储引擎开发者都可以按照自己的意愿来进行开发.
       注意：存储引擎是基于表的, 而不是数据库.
   ```

### 1.4 store engine

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

---

## 2. INDEX

### 2.1 introduce

1. SQL 执行慢的原因: `CPU + IO + CONFIG: TOP + FREE + IOSTAT + VMSTAT`

   - 查询语句写的烂
   - 索引失效: 单值/复合
   - 关联查询太多 join
   - 服务器调优及各个参数设置: 缓冲/线程数等

2. sample

   ```sql
   -- single INDEX: table name should be low case
   CREATE INDEX IDX_TABLENAME_COLUMNNAME ON TABLE_NAME (COLUMN_NAME)
   -- complex INDEX
   CREATE INDEX IDX_TABLENAME_COLUMNNAME ON TABLE_NAME (COLUMN_NAME, COLUMN_NAME)
   ```

### 2.2 join

![avatar](/static/image/db/mysql-join.png)

1. inner join

![avatar](/static/image/db/mysql-inner-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
[INNER] JOIN TBALE B
ON A.KEY = B.KEY

SELECT <select_list>
FROM TABLEA A
FULL OUTER JOIN TBALE B
ON A.KEY = B.KEY
WHERE A.Key IS NOT NULL AND B.Key IS NOT NULL
```

2. left join

![avatar](/static/image/db/mysql-left-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
LEFT JOIN TBALE B
ON A.KEY = B.KEY
```

3. right join

![avatar](/static/image/db/mysql-right-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
RIGHT JOIN TBALE B
ON A.KEY = B.KEY
```

4. left excluding join

![avatar](/static/image/db/mysql-left-excluding-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
LEFT JOIN TBALE B
ON A.KEY = B.KEY AND B.KEY IS NULL
```

5. right excluding join

![avatar](/static/image/db/mysql-right-excluding-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
GIGHT JOIN TBALE B
ON A.KEY = B.KEY AND A.KEY IS NULL
```

6. outer/full join: mysql donot support

![avatar](/static/image/db/mysql-outer-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
FULL OUTER JOIN TBALE B
ON A.KEY = B.KEY

-- in mysql
SELECT <select_list> FROM TABLEA A LEFT JOIN TBALE B ON A.KEY = B.KEY
UNION
SELECT <select_list> FROM TABLEA A RIGHT JOIN TBALE B ON A.KEY = B.KEY
```

7. outer excluding join

![avatar](/static/image/db/mysql-outer-excluding-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
FULL OUTER JOIN TBALE B
ON A.KEY = B.KEY
WHERE A.Key IS NULL OR B.Key IS NULL
```

### 2.3 INDEX introduce

1. definition:

   - INDEX is a **`data structure`** that helps MySQL to obtain data efficiently.
   - **_索引是数据结构; `排好序`的快速`查找` `数据结构`_**
   - 在数据之外, 数据库还维护了维护了满足特定查询算法的数据结构, 这些数据结构以某种方式引用指向数据, 就可以在这些数据结构的基础上实现高级查找算法, 这样的数据结构就是索引
   - 实际上 INDEX 也是一张 TABLE, 保存了主键与索引字段, 并指向实体表的记录
   - 一般来说索引本身也很大, 不可能全部存储在内存中, 因此索引往往以文件形式存储在硬盘上[idb]

2. INDEX structure

   - B+:
     ![avatar](/static/image/db/mysql-b+tree.png)
   - HASH:
   - FULL-TEXT:
   - R-TREE:

3. feature: order + query

   - 类似大学图书馆建书目索引, 提高数据检索效率, 降低数据库 IO 成本
   - 通过索引列对数据进行排序, 降低数据排序成本, 降低了 CPU 的消耗

4. disadvantage

   - 实际上 INDEX 也是一张 TABLE, 保存了主键与索引字段, 并指向实体表的记录, `需要一定的空间`
   - INDEX 虽然提高了 QUERY 的速度, 但是却降低了 UPDATE/INSERT/DELETE 的效率: `当 UPDATE/INSERT/DELETE 时就需要处理 DATA 和 INDEX 两个部分`
     - MySQL 不仅要存数据, 还要保存一下索引文件每次更新添加了索引列的字段, 都会调整因为更新所带来的键值变化后的索引信息
   - 需要花时间去建立优秀的 INDEX

5. INDEX TYPE: 尽量使用 `复合索引`; 每张表的索引尽量不要超过 5 个;

   - 单值索引: 一个表可以有多个单值索引
   - 唯一索引: 索引列的值必须唯一, 但允许有空值
   - 复合索引: 一个索引包含多个列

6. syntax

   ```sql
   -- 1. create INDEX
   CREATE [UNIQUE] INDEX INDEX_NAME ON TABLE_NAME(COLUMN_NAME);
   ALTER TABLE TABLE_NAME ADD [UNIQUE] INDEX [INDEX_NAME] ON TABLE_NAME(COLUMN_NAME);
   ALTER TABLE TABLE_NAME ADD PRIMARY KEY(COLUMN_NAME)                                             -- UNIQUE AND NOT NULL
   ALTER TABLE TABLE_NAME ADD UNIQUE INDEX_NAME(COLUMN_NAME)                                       -- UNIQUE AND CAN NULL, AND NULL CAN MORE TIMES
   ALTER TABLE TABLE_NAME ADD INDEX INDEX_NAME(COLUMN_NAME)                                        -- COMMON INDEX, CAN MORE TIME ONE VALUE
   ALTER TABLE TABLE_NAME ADD FULLTEXT INDEX_NAME(COLUMN_NAME)                                     -- USE IN FULL TEXT SEARCH

   -- 2. delete INDEX
   DROP INDEX [INDEX_NAME] ON TABLE_NAME

   -- 3. show  INDEX
   SHOW INDEX FROM TABLE_NAME
   ```

7. when should to create INDEX

   - PK: 主键自动建立唯一索引
   - `频繁作为查询`的条件的字段应该创建索引
   - 查询中与其他表关联的字段, `外键关系建立索引`
   - 查询中`排序的字段`, 排序字段若通过索引去访问将大大提高排序的速度
   - 查询中`统计`或者`分组`字段
   - 单值/复合索引的选择问题: 在高并发下倾向创建复合索引

8. when should not to create INDEX

   - 表记录`太少`
   - `WHERE` 条件里用不到的字段不创建索引
   - `频繁更新`的字段不适合创建索引: 因为每次更新不单单是更新了记录还会更新索引, 加重 IO 负担
   - 数据`重复`且`分布平均`的表字段: 如果某个数据列包含许多重复的内容, 为它建立索引就没有太大的实际效果

### 2.4 perfomance analysis

1. MYSQL SLOW: `CPU + IO + CONFIG: TOP + FREE + IOSTAT + VMSTAT`

   - [Reference](#1-introduce-1)

2. EXPLAIN: explain machine execute strategy

- function:

  - 表的读取顺序
  - 数据读取操作的操作类型
  - 哪些索引可以使用
  - 哪些索引被实际使用
  - 表之间的引用
  - 每张表有多少行被优化器查询

- syntax

  ```sql
  -- column: id, select_type, table, partitions, type, possible_keys, key, key_len, ref, rows, filtered, Extra
  EXPLAIN SQL EXPRESSION
  ```

- column

  - id: SELECT 查询的序列号, 表示查询中操作表的顺序
    1. ID 相同认为是一组, 从上往下执行
    2. ID 不同则 ID 越大越先执行
    3. DERIVED: 衍生
  - select_type: `区别普通查询、联合查询、子查询等`
    1. SIMPLE: 简单查询[不包含子查询/UNION]
    2. PRIMARY: 查询中若包含任何复杂的子部分, `最外层查询则被标记为 PRIMARY`
    3. SUBQUERY: 在 SELECT 或者 WHERE 列表中包含了子查询
    4. DERIVED: [ALIAS] 在 FROM 列表中包含的子查询被标记为 DERIVED; MySQL 会递归执行这些子查询, 把结果放在临时表里
    5. UNION: 若第二个 SELECT 出现在 UNION 之后, 则被标记为 UNION; 若 UNION 包含在 FROM 子句的子查询中, 外层 SELECT 将被标记为 DERIVED
    6. UNION RESULT: 从 UNION 表获取结果的 SELECT
  - table: 显示这一行的数据是关于哪张表的
  - type:

    ```sql
    1. 显示查询使用了何种类型
    2. `[SYSTEM > CONST > EQ_REF > REF > RANGE > INDEX > ALL]`
    3. 一般来说, 得保证查询只是达到range级别, 最好达到ref
    ```

    1. system: 表只有一行记录(等于系统表), 这是 const 类型的特例, 可以忽略不计
    2. const: 表示通过索引一次就找到了, const 用于比较 primary key 或者 unique 索引
    3. eq_ref: 唯一性索引, 对于每个索引键, 表中只有一条记录与之匹配, `常见于主键或唯一索引扫描`
    4. ref: 非唯一索引扫描, 返回匹配某个单独值的所有行; 本质上也是一种索引访问, 它`返回所有匹配某个单独值的行`, 然而它可能会找到多个符合条件的行, 所以他应该属于`查找和扫描的混合体`
    5. range:

    ```sql
    1. 只检索给定范围的行, 使用一个索引来选择行;
    2. key 列显示使用了哪个索引
    3. 一般就是在你的 where 语句中出现了 between < > in 等的查询
    4. 这种范围扫描索引扫描比全表扫描要好, 因为他只需要开始索引的某一点, 而结束语另一点, 不用扫描全部索引
    ```

    6. INDEX

    ```sql
    FULL INDEX SCAN, INDEX 与 ALL区别:
      INDEX 类型只遍历索引树, 通常比ALL快, 因为索引文件通常比数据文件小
      也就是说虽然 ALL 和 INDEX 都是读全表, 但 INDEX 是从索引中读取的, 而 ALL 是从硬盘中读的
    ```

    7. all: FULLTABLE SCAN, 将遍历全表以找到匹配的行

  - possible_keys
    1. 显示可能应用在这张表中的索引[一个或多个]
    2. 查询涉及的字段上若存在索引, 则该索引将被列出, 但不一定被查询实际使用
  - key:

    1. 实际使用的索引;
    2. 如果为 null 则没有使用索引
    3. 查询中若使用了覆盖索引, 则索引和查询的 SELECT 字段重叠

  - key_len
    1. 表示索引中使用的字节数, 可通过该列计算查询中使用的索引的长度. 在不损失精确性的情况下, 长度越短越好
    2. key_len 显示的值为索引最大可能长度, 并非实际使用长度, 即 key_len 是根据表定义计算而得, 不是通过表内检索出的
  - ref
    1. 显示索引哪一列被使用了, 如果可能的话, 是一个常数.
    2. 哪些列或常量被用于查找索引列上的值
  - rows
    1. 根据表统计信息及索引选用情况, 大致`估算出找到所需的记录所需要读取的行数`
  - Extra

    1. Using filesort

    ```sql
    1. 说明 MYSQL 会对数据使用一个外部的索引排序, 而不是按照表内的索引顺序进行读取.
    2. MySQL 中无法利用索引完成排序操作成为"文件排序"
    ```

    2. Using temporary

    ```sql
    1. 使用了临时表保存中间结果, MySQL 在对查询结果排序时使用临时表
    2. 常见于排序 ORDER BY 和分组查询 GROUP BY
    ```

    3. Using index

    ```sql
    1. 表示相应的 SELECT 操作中使用了覆盖索引(Coveing index), 避免访问了表的数据行, 效率不错
    2. 如果同时出现 USING WHERE, 表明索引被用来执行索引键值的查找
    3. 如果没有同时出现 USING WHERE, 表明索引用来读取数据而非执行查找动作
    ```

    4. Using where: 表明使用了 WHERE 过滤

    5. using join buffer: 使用了连接缓存

    6. impossible where: WHERE 子句的值总是 FALSE, 不能用来获取任何元组

    7. select tables optimized away

    ```sql
    在没有 GROUPBY 子句的情况下:
      基于索引优化 MIN/MAX 操作或者对于 MyISAM 存储引擎优化 COUNT(*) 操作,
      不必等到执行阶段再进行计算, 查询执行计划生成的阶段即完成优化
    ```

    8. distinct: 优化 DISTINCT, 在找到第一匹配的元组后即停止找同样值的工作

### 2.5 INDEX optimization

![avatar](/static/image/db/mysql-index.png)

1. 索引分析

   - 单表:
   - 两表: left join 应该在 right 上建立 INDEX
   - 三表: 小表驱动大表, 除去小表其他 on 条件都应该建立 INDEX

   - 复合 INDEX 是有顺序的, 且 `> <` 之后的 INDEX 会失效
   - 左连接应该加在右表上;
   - 小表做主表
   - 优先优化 nestedloop 的内层循环
   - 保证 JOIN 语句的条件字段有 INDEX

2. 索引失效(应该避免)

   - 1. 全值匹配我最爱
   - 2. 最佳左前缀法则: `索引是有序的, 查询从索引的最左前列开始并且不跳过索引中的列`
   - 3. 不在索引列上做任何操作[计算/函数/自动 or 手动类型转换], 会导致索引失效而转向全表扫描
        `explain select * from staffs where left(NAME, 4) = 'July'`
   - 4. 存储引擎不能使用索引中范围条件右边的列
     - 如果中间断了, 断了之后的索引都会失效
     - `> <` 之后的索引也会失效
     - like 之后索引也会失效
   - 5. [valid]尽量使用覆盖索引, 禁止 SELECT \*
   - 6. 非覆盖索引下, 使用不等于[!= 或者 <>] 的时候无法使用索引会导致全表扫描
     - 覆盖索引时不会索引失效
   - 7. IS NULL 在索引会一直失效; IS NOT NULL 在非覆盖索引下会失效:
     - 应该给定默认值, 系统中少出现 NULL
   - 8. LIKE 非覆盖索引下: 以通配符开头['$abc...'] MYSQL 索引失效会变成全表扫描操作
     - 覆盖索引时不会失效
     - `非覆盖索引时 %%`: invalid
     - `非覆盖索引时 %xx`: invalid
     - `非覆盖索引时 xx%`: valid
     - how to use INDEX when use `%%`
       - 使用覆盖索引
       - 当覆盖索引指向的字段是 varchar(380)及 380 以上的字段时, 覆盖索引会失效!
   - 9. 字符串不加单引号索引失效
   - 10. 少用 or, 用它连接时会索引失效

- 口诀

  ```sql
  全值匹配我最爱, 最左前缀要遵守;
  带头大哥不能死, 中间兄弟不能断;
  索引列上少计算, 范围之后全失效;
  LIKE百分写最右, 覆盖索引不写星;
  不等空值还有or, 索引失效要少用;
  VAR引号不可丢, SQL高级也不难!
  in 后表为小

  -- 如果所以在 where 后全出现则无关顺序, 都会被使用
  -- 索引断层一定导致断层之后的索引失效
  -- >< 没断层之前都是 range; 断层则是断层前的 ref
      - Using index condition
  -- order by 则是 where 和 order 连在一起没断层则使用 ref[orderby 不会被索引使用] + Using index condition
      - 断层则使用 all[非覆盖索引] + Using filesort
      - 覆盖索引 index + Using filesort
  -
  ```

3. **`一般性建议`**
   - 1. 对于单键索引, 尽量选择针对当前 query 过滤性更好的索引
   - 2. 在选择组合索引的时候, 当前 Query 中过滤性最好的字段在索引字段顺序中, 位置越靠前越好
   - 3. 在选择组合索引的时候, 尽量选择可以能包含当前 query 中的 where 子句中更多字段的索引
   - 4. 尽可能通过分析统计信息和调整 query 的写法来达到选择合适索引的目的
   - 5. 定值, 范围还是排序, 一般 order by 是给个范围
   - 6. group by 基本上都需要进行排序, 会有临时表产生

---

## 3. query analysis

- process
  1. 慢查询的开启并捕获
  2. explain + 慢 SQL 分析.
  3. show profile 查询 SQL 在 Mysql 服务器里面的执行细节和生命周期情况
  4. SQL 数据库服务器的参数调优.

### 3.1 slow query optimization

1. 小表驱动大表

   ```sql
   // TODO: why? as i think A is also little table.
   -- B should little: B will load first
   // as i think A is little table.
   SELECT * FROM A WHERE ID IN (SELECT id FROM B)
   -- A should little: A will load first
   // as i think B is little table.
   SELECT * FROM A WHERE EXISTS (SELECT 1 FROM B WHERE A.ID = B.ID)
   ```

2. ORDER BY 子句, 尽量使用 INDEX 方式排序, 避免使用 FileSort 方式排序

   ![avatar](/static/image/db/mysql-order-by.png)

   - type: Using index; Using filesort
   - ORDER BY 符合最佳左前缀法则
   - ORDER BY 后的字段有序
   - ORDER BY 复合升序降序排序会使用 Using filesort
   - ORDER BY 中间不能断, 且可以和 WHERE 之后一起不断也可以
   - 禁止使用 SELECT \*

   ```sql
   -- type: ref, extra: Using index condition
   explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4;
   -- type: ref, extra: Using where; Using index
   explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c2, c3;
   -- type: index, extra: Using where; Using index; Using filesort
   explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4, c5;
   ```

   - 多路复用和单路复用
     - 禁止使用 SELECT \*
     - 增大 sort_buffer_size 参数的设置
     - 增大 max_length_for_sort_data 参数的设置

3. GROUP BY

   - GROUP BY 实质是先排序后进行分组
   - 遵照索引建的最佳左前缀
   - 当无法使用索引列, 增大 max_length_for_sort_data 参数的设置 + 增大 sort_buffer_size 参数的设置
   - WHERE 高于 HAVING, 能写在 WHERE 限定的条件就不要去 HAVING 限定

### 3.2 slow query log

1. MYSQL 默认没有 ENABLE, 且 10S 以上的 QUERY 才算慢查询, long_query_time 进行设置
2. 不建议平时开启, 会有一定的性能影响
3. look up

   ```sql
   -- look up
   SHOW VARIABLES LIKE '%slow_query_log%'
   -- enable slow query log, and if restart mysql will invalid, and it only valid to current database
   SET GLOBAL SLOW_QUERY_LOG=1;

   -- set always enable, then restart
   vim /etc/mysql/mysql.conf.d/mysqld.cnf

   --# Here you can see queries with especially long duration
   slow_query_log = 1
   slow_query_log_file = /var/lib/mysql/mysql-ubuntu.log
   long_query_time = 2
   log_output = FILE

   -- look up long query time definition, it is more than and donot include equals
   show variables like '%long_query_time%';
   -- set long_query_time, and need a new session to be valid
   SET GLOBAL long_query_time = 3;

   -- look up slow query number
   show global status like '%slow_queries%';
   ```

   |    Variable_name    |                      Value                      |
   | :-----------------: | :---------------------------------------------: |
   |   slow_query_log    |                       OFF                       |
   | slow_query_log_file | /var/lib/mysql/iZuf6acp86oa3fwxfnkwr1Z-slow.log |

4. mysqldumpslow --help

- command

  - s: 是表示按何种方式排序
  - c: 访问次数
  - l: 锁定时间
  - r: 返回记录
  - t: 查询时间
  - al: 平均锁定时间
  - ar: 平均返回记录数
  - at: 平均查询时间
  - t: 即为返回前面多少条的数据
  - g: 后边搭配一个正则匹配模式，大小写不敏感的

- sample

  ```shell
  # 得到返回记录集最多的10个SQL
  mysqldumpslow -s r -t 10 Nar/ib/mysq/atguigu-slow.log
  # 得到访问次数最多的10个SQL
  mysqldumpslow -s c -t 10 /var/lib/mysql/atguigu-slow.log
  # 得到按照时间排序的前10条里面含有左连接的查询语句
  mysqldumpslow -s t -t 10 -g "left join" /var/lib/mysq/atguigu-slow.log
  # 另外建议在使用这些命令时结合|和more使用，否则有可能出现爆屏情况
  mysqldumpslow -s r -t 10 /var/ib/mysql/atguigu-slow.log | more
  ```

### 3.3 bulk script

1. error in processor

   ```sql
   -- because we have open slow_query, so it require function must have one parameter.
   show variables like '%log_bin_trust_function_creators%';
   set global log_bin_trust_function_creators= 1 ;
   ```

2. sample

   ```sql
   ## bulk insert
   create database bigData;
   use bigData;

   CREATE TABLE BigData_dept(
     id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
     deptno MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
     dname VARCHAR(20) NOT NULL DEFAULT '',
     loc VARCHAR(13) NOT NULL DEFAULT ''
   ) ENGINE=INNODB;
   select * from BigData_dept;

   CREATE TABLE BigData_emp(
     id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
     empno MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
     ename VARCHAR(20) NOT NULL DEFAULT '',
     job VARCHAR(9) NOT NULL DEFAULT '',
     mgr MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
     hiredate DATE NOT NULL,
     sal DECIMAL(7,2) NOT NULL,
     comm DECIMAL(7 ,2) NOT NULL,
     deptno MEDIUMINT UNSIGNED NOT NULL DEFAULT 0
   )ENGINE=INNODB;
   select * from BigData_emp;

   show variables like '%log_bin_trust_function_creators%';

   -- generate random string: rags: is length
   DELIMITER $$
   CREATE FUNCTION rand_string(n INT) RETURNS VARCHAR (255)
     BEGIN
       DECLARE chars_str VARCHAR(100) DEFAULT ' abcdefghijklmnopqrstuVwxyzABCDEFJHIJKLMNOPQRSTUVWXYZ';
       DECLARE return_str VARCHAR(255) DEFAULT '';
       DECLARE i INT DEFAULT 0;
       WHILE i < n DO
       SET return_str = CONCAT(return_str, SUBSTRING(chars_str, FLOOR(1+RAND()*52),1));
       SET i = i + 1;
     END WHILE;
   RETURN return_str;
   END $$
   -- drop function rand_string;

   -- generate random num: 100 - 110
   DELIMITER $$
   CREATE FUNCTION rand_num( ) RETURNS INT(5)
     BEGIN
       DECLARE i INT DEFAULT 0;
       SET i= FLOOR(100 + RAND() * 10);
       RETURN i;
     END $$
   -- drop function rand_num;

   -- bulk insert emp
   DELIMITER $$
   CREATE PROCEDURE insert_emp(IN start INT(10),IN end INT(10))
     BEGIN
       DECLARE i INT DEFAULT 0;
       # set autocommit = 0把 autocommit 设置成0
       SET autocommit = 0;
       REPEAT
         SET i = i + 1;
         INSERT INTO BigData_emp(empno, ename, job, mgr, hiredate, sal, comm, deptno)
         VALUES((start + i), rand_string(6), 'SALESMAN', 0001, CURDATE(), 2000, 400, rand_num());
       UNTIL i = end
       END REPEAT;
       COMMIT;
     END $$

   -- bulk insert dept
   DELIMITER $$
   CREATE PROCEDURE insert_dept(IN start INT(10),IN max_num INT(10))
     BEGIN
       DECLARE i INT DEFAULT 0;
       SET autocommit = 0;
       REPEAT
         SET i = i + 1;
         INSERT INTO BigData_dept(deptno, dname, loc)
             VALUES((start + i), rand_string(10), rand_string(8));
         UNTIL i = max_num
       END REPEAT;
       COMMIT;
     END $$

   -- call insert_dept(100, 10);
   select * from BigData_dept;

   -- call insert_emp(1000001, 500000);
   select * from BigData_emp;
   ```

3. if exists large data, it will become slow when we create index, even drop it.

### 3.4 show profile

1. 是 MYSQL 提供可以用来分析当前会话中语句执行的资源消耗情况; 可以用于 SQL 的调优测量
2. 默认情况下, 参数处于关闭状态，并保存最近 15 次的运行结果
3. analysis

   - 1. look up enable

   ```sql
   -- look up enable or not, need new seesion
   show variables like '%profiling%';
   set global profiling = 1;
   ```

   - 2. 诊断 SQL 报告

        ```sql
        -- 查看结果
        show profiles;
        show profile cpu, block io for query number_id;
        ```

   - 3. 日常开发需要注意的结论
     - converting HEAP to MyISAM 查询结果太大, 内存都不够用了往磁盘上搬了
     - Creating tmp table 创建临时表
     - Copying to tmp table on disk 把内存中临时表复制到磁盘, 危险!!!
     - locked

### 3.5 globel query log

- 将执行的所有 SQL 全部保存到 mysql.general_log 表中.

- look up

  ```sql
  show variables like '%general_log%';
  set global general_log = 1;
  set global log_output = 'Table'
  ```

---

## 4. lock

### 4.1. table locks[perfer read]

- 偏向 MyISAM 存储引擎, 开销小, 加锁快, 无死锁, 锁定粒度大, 发生锁冲突的概率最高, 并发最低

- analysis

  ```sql
  SHOW STATUS LIKE 'TABLE%';

  +----------------------------+-------+---------------------------------------- +
  | Variable_name              | Value |             description                 |
  +----------------------------+-------+-----------------------------------------+
  | Table_locks_immediate      | 843   | table lock time and immediate execute   |
  | Table_locks_waited         | 0     | occur competition time due to table lock|
  +----------------------------+-------+-------+
  ```

- MyISAM 查询时会自动给表加读锁; 修改时会自动加写锁

- table lock operation

  ```sql
  -- lock table
  LOCK TABLE TBALE_NAME READ/WRITE, TBALE_NAME READ/WRITE ···

  -- look up locked table
  SHOW OPEN TABLES;

  -- unlock table
  UNLOCK TABLES;
  ```

#### 4.1.1 read lock

- env: session01 have read lock, session2 no limit

- session01:

  - [read lock table] session01 just can read lock table
  - [update lock table] cannot update this table
  - [read others] even cannot read other tables
  - [update others] cannot update operation until unlock

- session02:

  - [read lock table] can read session01 locked table: `because read lock is shared`
  - [update lock table] blocked by session01 until session01 unlock table, `then finish update operation`.
  - [read others] can read other tables
  - [update others] can update others table without limit

#### 4.1.2 write lock

- env: session01 have write lock, session2 no limit

- session01:

  - [read lock table] session01 just can read lock table
  - [update lock table] can update this table
  - [read others] even cannot read other tables
  - [update others] cannot update operation until unlock

- session02:

  - [read lock table] blocked by session01 until session01 unlock table: `because write lock is exclusive`
  - [update lock table] blocked by session01 until session01 unlock table, `then finish update operation`.
  - [read others] can read other tables
  - [update others] can update others table without limit

### 4.2 row locks[perfer write]

- 偏向 InnoDB 存储引擎, 开销大, 加锁慢; 会出现死锁; 锁定粒度最小, 发生锁冲突的概率最低, 并发度也最高

  ```sql
  -- disable auto commit
  set autocommit = 0;
  ```

- pre evn

  - InnoDB
  - disable auto commit all
  - 1. update sql, no commit, it can be read by itself session, and cannot be read by other session
    - only when all session commit, data can be read all session shared.
  - 2. if session2 all update this row, it will be blocked until session01 commited;
  - 3. if session2 update other rows, it will be ok without limit

- 无索引行锁升级为表锁

  ```sql
  -- b type is varchar, it will become table lock, other sessions update operation will be blokced
  update test_innodb_lock set a=40001 where b = 4000;
  -- type: all, extra: Using where
  explain update test_innodb_lock set a=40001 where b = 4000; -- index invalid
  -- type: range, extra: Using where
  explain update test_innodb_lock set a=40001 where b = '4000';  -- index valid
  ```

- 间隙锁危害

  - 定义: 当我们用范围条件而不是相等条件检索数据, 并请求共享或排他锁时, InnoDB 会给符合条件的已有数据记录的索引项加锁;
    对于键 值在条件范围内但并不存在的记录, 叫做 "间隙(GAP)". InnoDB 也会对这个 "间隙" 加锁, 这种锁机制就是所谓的间隙锁(Next-Key 锁)

  - sql

  ```sql
  -- no a = 2 data,
  -- session01:
  update test_innodb_lock set b='40001' where a > 1 and a< 6;  -- ok

  --session02:
  insert into test_innodb_lock values(2, '20000');  -- blocked
  update test_innodb_lock b set b='2000' where a=2;  -- ok

  -- if and only if session01 commit, so other session can be un blocked.
  ```

- 常考如何锁定一行

  - sql

  ```sql
  set autocommit = 0;
  -- session01:
  begin;
  select * from TABLE_NAME where id = 1 for update;
  -- commit;

  -- session2: it will blockd until session01 commit
  update TABLE_NAME set COLUMN_NAME = 'xx' where id = 1;  -- blocked
  ```

- analysis

  ```sql
  -- look up
  show status like 'innodb_row_lock%';
  +-------------------------------+-------+
  | Variable_name                 | Value |
  +-------------------------------+-------+
  | Innodb_row_lock_current_waits | 0     |
  | Innodb_row_lock_time          | 56268 |
  | Innodb_row_lock_time_avg      | 28134 |
  | Innodb_row_lock_time_max      | 51008 |
  | Innodb_row_lock_waits         | 2  ☆  |
  +-------------------------------+-------+
  ```

- 行锁优化建议
  - 尽可能让所有数据检索都通过索引来完成, 避免无索引行锁升级为表锁
  - 合理设计索引, 尽量缩小锁的范围
  - 尽可能较少检索条件, 避免间隙锁
  - 尽量控制事务大小, 减少锁定资源量和时间长度
  - 尽可能低级别事务隔离

### 4.3 leaf lock[less use]

- 开销和加锁时间界于表锁和行锁之间: 会出现死锁; 锁定粒度界于表锁和行锁之间, 并发度一般.

## 5. transaction

```sql
-- look up isolation
SHOW VARIABLES LIKE '%tx_isolation%';
```

1. ACID

   - atomic
   - consistency
   - isolation
   - durable

2. PAC

   - partition tolerate
   - atomic + consistence
   - available

3. 3v

   - 海量
   - 多样
   - 实时

4. 3h

   - 高可用
   - 高并发
   - 高性能

5. 读数据

   - 更新丢失: 当两个或多个事务选择同一行, 后一个覆盖前一个
   - 脏读: 一个事务读取到了另外一个事务未提交的数据
     - `[事务A读到事务B已修改但是尚未提交的数据].`
   - 不可重复读: 同一个事务中, 多次读取到的数据不一致
     - [事务 A 读到事务 B 已提交的修改数据, 不符合隔离性]
     - 对于两个事务 T1, T2, T1 读取了一个字段, 然后 T2 更新了该字段. 之后, T1 再次读取同一个字段, 值就不同了
   - 幻读: 一个事务读取数据时, 另外一个事务进行更新, 导致第一个事务读取到了没有更新的数据

     - [事务 A 读到事务 B 已提交的新增数据].
     - 对于两个事务 T1, T2, T1 从一个表中读取了一个字段, 然后 T2 在该表中插入了一些新的行. 之后, 如果 T1 再次读取同一个表, 就会多出几行

   - differ between 脏读 和 幻读
     - 脏读是事务 B 里面修改了数据;
     - 幻读是事务 B 里面新增了数据.

6. isolation

   - READ UNCOMMITED

     ```txt
     公司发工资了，领导把5000元打到singo的账号上，但是该事务并未提交，而singo正好去查看账户，发现工资已经到账，是5000元整，非常高兴。可是不幸的是，领导发现发给singo的工资金额不对，是2000元，于是迅速回滚了事务，修改金额后，将事务提交，最后singo实际的工资只有2000元，singo空欢喜一场。

     出现上述情况，即我们所说的脏读，两个并发的事务，“事务A：领导给singo发工资”、“事务B：singo查询工资账户”，事务B读取了事务A尚未提交的数据
     ```

   - READ COMMINED: avoid `Dirty Read`

     ```txt
     singo拿着工资卡去消费，系统读取到卡里确实有2000元，而此时她的老婆也正好在网上转账，把singo工资卡的2000元转到另一账户，并在singo之前提交了事务，当singo扣款时，系统检查到singo的工资卡已经没有钱，扣款失败，singo十分纳闷，明明卡里有钱，为何......

     出现上述情况，即我们所说的不可重复读，两个并发的事务，“事务A：singo消费”、“事务B：singo的老婆网上转账”，事务A事先读取了数据，事务B紧接了更新了数据，并提交了事务，而事务A再次读取该数据时，数据已经发生了改变。
     ```

   - REPEATABLE READ: avoid `Non-Repeatable` and `Dirty Read` and party `Phantom Reading`

     - 可以通过加间隙锁的方式解决幻读问题

     ```txt
     当隔离级别设置为Repeatable read时，可以避免不可重复读。当singo拿着工资卡去消费时，一旦系统开始读取工资卡信息（即事务开始），singo的老婆就不可能对该记录进行修改，也就是singo的老婆不能在此时转账。
     ```

   - SERIALIZABLE: avoid `Phantom Reading`

   |          隔离吸别          |               读数据一致性               | 脏读 | 不可重复读 | 幻读 |
   | :------------------------: | :--------------------------------------: | :--: | :--------: | ---- |
   | 未提交读(READ UNCOMMITTED) | 最低级别: 只能保证不读取物理上损坏的数据 |  是  |     是     | 是   |
   |  已提交度(READ COMMITTED)  |                  语句级                  |  否  |     是     | 是   |
   | 可重复读(REPEATABLE READ)  |                  事务级                  |  否  |     否     | 是   |
   |   可序列化(SERIALIZABLE)   |             最高级别: 事务级             |  否  |     否     | 否   |

---

## 6. master-slave replication

### 6.1 theory of replication

1. slave 会从 master 读取 binlog 来进行数据同步
2. step
   - master 将改变记录到二进制日志[binary log]. 这些记录过程叫做二进制日志时间, binary log events
   - slave 将 master 的 binary log ebents 拷贝到它的中继日志[relay log]
   - slave 重做中继日志中的时间, 将改变应用到自己的数据库中. MySQL 复制是异步的且串行化的

### 6.2 principle of replication

1. 每个 slave 只有一个 master
2. 每个 slave 只能有一个唯一的服务器 ID
3. 每个 master 可以有多个 salve
4. 主从复制有延迟的问题

### 6.3 config

1. 条件
   - mysql 版本一致且后台以服务运行
2. 要求

   - 主从都配置在[mysqld]结点下, 都是小写

3. 修改 master 的配置文件

   - 1. [必须] 主服务器唯一 ID: `server-id =1`
   - 2. [必须] 启用二进制日志: `log_bin=CUSTOM_PATH`
   - 3. [可选] 启动错误日志: `log_error=CUSTOM_PATH`
   - 4. [可选] 根目录: `basedir= /usr`
   - 5. [可选] 临时目录: `tmpdir= /tmp`
   - 6. [可选] 数据目录: `datadir= /var/lib/mysql`
   - 7. [可选] read-only=0: 表示 master 读写都可以
   - 8. [可选] 设置不要复制的数据库: `binlog_ignore_db= include_database_name`
   - 9. [可选] 设置需要复制的数据: `#binlog_do_db= include_database_name`

4. 修改 slave 的配置文件

   - 1. [必须] 从服务器唯一 ID
   - 2. [可选] 启用二进制文件

5. 因修改过配置文件, 请 master and slave restart

6. master and slave 都关闭防火墙

7. master 建立账户并授权给 slave

   ```sql
   GRANT REPLICATION SLAVE ON *.* TO 'zhangsan'@'SLAVE_IP' IDENTIFIED BY '123456';
   flush privileges;

   -- look up master status
   show master status;  -- File column: which file; Position: where to slave
   ```

8. slave 配置需要复制的主机

   ```sql
   -- we should use show master status; to get new file and position each time
   CHANGE MASTER TO MASTER_HOST = 'MASTER_IP', MASTER_USER = 'zhangsan', MASTER_PASSWORD = '123456', MASTER_LOG_FILE = 'FILENAME', MASTER_LOG_POS=POSITION_NUMBER;

   start slave;

   -- llok up slave status and must Slave_IO_Running:Yes and Slave_SQL_Running:Yes
   show slave status;

   stop slave;
   ```

---

## sample

1. create table

```sql
create table db_test03(
  a int primary key not null auto_increment,
  c1 char(10),
  c2 char(10),
  c3 char(10),
  c4 char(10),
  c5 char(10)
);

insert into db_test03(c1,c2,c3,c4,c5) values('a1','a2', 'a3', 'a4','a5');
insert into db_test03(c1,c2,c3,c4,c5) values('b1','b2', 'b3', 'b4','b5');
insert into db_test03(c1,c2,c3,c4,c5) values('c1','c2', 'c3', 'c4','c5');
insert into db_test03(c1,c2,c3,c4,c5) values('d1','d2', 'd3', 'd4','d5');
insert into db_test03(c1,c2,c3,c4,c5) values('e1','e2', 'e3', 'e4','e5');
```

2. create index

```sql
select * from test03;
create INDEX IDX_C1_C2_C3_C4 on test03(c1, c2, c3, c4) ;
```

3. `=`

```sql
-- SHOW INDEX FROM db_test03;

-- type: ref ref: const,const,const,const extra: null
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c4 = 'a4' ;
-- type: ref ref: const,const,const,const extra: Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c4 = 'a4' ;

-- type: ref ref: const,const,const,const extra: Using where
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c4 = 'a4' and c5 = 'a5' ;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c4 = 'a4' and c5 = 'a5' ;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c5 = 'a5'  and c4 = 'a4' ;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c5 = 'a5'  and c4 = 'a4' ;

-- type: ref ref: const extra: Using index condition; Using where
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c3 = 'a3'  and c5 = 'a5'  and c4 = 'a4' ;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c3 = 'a3'  and c5 = 'a5'  and c4 = 'a4' ;
-- type: ref ref: const,const,const,const extra: Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c4 = 'a4' ;

-- type: ref ref: const,const extra: Using index condition
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and c4 = 'a4' ;
-- type: ref ref: const,const extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' and c4 = 'a4' ;

-- type: ref ref: const,const extra: null
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' ;
-- type: ref ref: const,const extra: Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' ;

-- type:  ALL ref: null extra: Using where
explain select c1, c2, c5 from db_test03 where c2 = 'a3' and c4 = 'a4'
-- type: index ref: null extra: Using where; Using index
explain select c1, c2 from db_test03 where c2 = 'a3' and c4 = 'a4'
```

4. `> <`

```sql
-- type: range ref: null extra: Using index condition: 索引使用了 c1, c2, c3
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and c3 > 'a3' and  c4 = 'a4';
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' and c3 > 'a3' ;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' and c3 > 'a3' and c5 = 'a5';
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' and c3 > 'a3' and c5 > 'a5';

-- type: range ref: null extra: Using where; Using index: 索引使用了 c1, c2, c3
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' and c3 > 'a3' and  c4 = 'a4';
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' and c3 > 'a3';
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' and c3 > 'a3' and c5 > 'a5';

-- type: ref ref: const extra: Using index condition
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c3 > 'a3' and  c4 = 'a4';
-- type: ref ref: const extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c3 > 'a3' and  c4 = 'a4';

-- type: ALL ref: null extra: Using where
explain select c1, c2, c3, c4, c5 from db_test03 where c5 > 'a5';
explain select c1, c2, c3, c4, c5 from db_test03 where c2 > 'a5';
explain select c1, c2, c3, c4, c5 from db_test03 where c1 > 'a5';

-- type: index extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c5 > 'a5';
explain select c1, c2, c3, c4 from db_test03 where c2 > 'a5';
-- type: range extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 > 'a5';
```

5. `order by`

```sql
-- type: ref ref: const,const extra: Using index condition
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' order by c3;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c2;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c2, c3;
-- type: ref ref: const,const extra: Using index condition; Using where
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' and c5 = 'a5' order by c2, c3;
-- type: ref ref: const,const extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' order by c3;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c2;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c2, c3;
-- type: ref ref: const,const extra: Using index condition; Using where
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' and c5 = 'a5' order by c2, c3;

-- type: ref ref: const,const extra: Using index condition
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4;
explain select c1, c2, c3, c4, c5 from db_test03 where c2 = 'a2' and c1 = 'a1' order by c3, c4;
-- type: ref ref: const,const extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4;

-- type: ref ref: const,const extra: Using index condition; Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c4, c3;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c4, c5;
-- type: ref ref: const,const extra: Using where; Using index; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c4, c3;
-- type: ref ref: const,const extra: Using index condition; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c4, c5;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c5;

-- type: ref ref: const,const extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3, c2;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;

-- type: ref ref: const,const extra: Using index condition; Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c5;
-- type: ref ref: const,const extra: Using index condition; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c5;

-- type: ALL extra: Using where; Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 where c2 = 'a2' and  c4 = 'a4' order by c3;
-- type: index extra: Using where; Using index; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c2 = 'a2' and  c4 = 'a4' order by c3;

-- type: ref ref: const,const extra: Using index condition
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;
-- type: ref ref: const,const extra: Using where; Using index
explain select c1, c3, c2, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;

-- type: ALL extra: Using where; Using filesort
explain select c1, c2, c3, c4, c5  from db_test03 where c1 = 'a2' order by c2;
-- type: index extra: Using where; Using index; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a2' order by c2;

-- type: ALL extra: Using where; Using filesort
explain select c1, c2, c3, c4, c5  from db_test03 where c2 = 'a2' order by c3;
-- type: index extra: Using where; Using index; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c2 = 'a2' order by c3;

-- type: ref ref: const,const extra: Using index condition
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;
explain select c1, c2, c3, c4, c5 from db_test03 where c2 = 'a2' and c1 = 'a1' order by c3;
explain select c1, c2, c3, c5, c4 from db_test03 where c2 = 'a2' and c1 = 'a1' order by c3;
-- type: ref ref: const,const extra: Using where; Using index
explain select c1, c3, c2, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c3;
explain select c1, c3, c2, c4 from db_test03 where c2 = 'a2' and c1 = 'a1' order by c3;

-- type: ref ref: const,const extra: Using index condition; Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c4;
-- type: ref ref: const,const extra: Using where; Using index; Using filesort
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' order by c4;

-- type: all extra: Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 order by c4;
explain select c1, c2, c3, c4, c5 from db_test03 order by c1;
explain select c1, c2, c3, c4, c5 from db_test03 order by c5;
-- type: index extra: Using index; Using filesort
explain select c1, c2, c3, c4 from db_test03 order by c4;
explain select c1, c2, c3, c4 from db_test03 order by c1;
explain select c1, c2, c3, c4 from db_test03 order by c5;
```

6. `group by`

```sql
-- type: ref ref: const extra: Using index condition; Using where
explain select c1, c2 c4, c5 from db_test03 where c1 = 'a1' and c4 = 'a4' and c5 ='a5' group by c2;
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c4 = 'a4' and c5 ='a5' group by c2, c3;
-- type: ref ref: const extra: Using index condition; Using where; Using temporary; Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c4 = 'a4' and c5 ='a5' group by c3, c2;

-- type: ref ref: const extra: Using where; Using index
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c4 = 'a4' group by c2, c3;
explain select c1, c3, c2, c4 from db_test03 where c1 = 'a1' and c4 = 'a4' group by c2, c3;
explain select c1, c3, c2, c4 from db_test03 where c4 = 'a4' and c1 = 'a1' group by c2, c3;
-- type: ref ref: const extra: Using where; Using index; Using temporary; Using filesort
explain select c1, c3, c4 from db_test03 where c1 = 'a1' and c4 = 'a4' group by c3;
explain select c1, c2, c4 from db_test03 where c1 = 'a1' and c2 = 'a2' group by c4;
-- type: ref ref: const extra: Using where; Using index
explain select c1, c2, c3 from db_test03 where c1 = 'a1' and c3 = 'a4' group by c2;
-- type: ref ref: const extra: Using where; Using index
explain select c1, c3, c2 from db_test03 where c1 = 'a1' and c2 = 'a2' group by c3;

-- type: ref ref: const extra: Using index condition; Using temporary; Using filesort
explain select c1, c2, c3, c4, c5 from db_test03 where c1 = 'a1' and c4 = 'a4' group by c2, c3, c5;
explain select c1, c2, c3, c4 from db_test03 where c1 = 'a1' and c4 = 'a4' group by c2, c3, c5;

-- type：index extra: Using index
explain select c1 from db_test03 group by c1;
-- type：all extra: Using temporary; Using filesort
explain select c5 from db_test03 group by c5;

```

---

## issue

1. sql load sequence: from first

   ![avatar](/static/image/db/mysql-machine-sequence.png)

   - human code

   ```sql
   SELECT DISTINCT <select_list>
   FROM <left_table>
   <join_type> JOIN <right_table> ON <join_condition>
   WHERE <where_condition>
   GROUP BY <group_list>
   HAVING <having_condition>
   ORDER BY <order_by_condition>
   LIMIT <limit_condition>
   ```

   - machine code

   ```sql
   FROM <left_table>
   ON <join_condition>
   <join_type> JOIN <right_table>
   WHERE <where_condition>
   GROUP BY <group_list>
   HAVING <having_condition>
   SELECT DISTINCT <select_list>
   ORDER BY <order_by_condition>
   LIMIT <limit_condition>
   ```

2. JOIN

   > from a, b: 笛卡尔积
   > from a, b where a.BId = b.AId: inner join

3. UNION: merge result sets and remove duplicates

4. GROUP BY must used with ORDER BY

5. EXPLAIN:

   - id: ID 越大越先执行, ID 相同时从上至下执行
   - table: 使用到的 table
   - select_type[6]: simple, primary, subquery, derived, union, union result
   - type[8]: system > const > eq_ref[唯一性索引扫描] > ref[非唯一性索引扫描] > range > index > all
   - possible_keys: 可能用到的 INDEX
   - key: 实际用到的 INDEX
   - key_len: INDEX 的最大长度
   - ref: 显示 INDEX 的哪一列被使用了
   - rows: 查出来多少条
   - extra

6. 复合 INDEX 是有顺序的, 且 > < 之后的 INDEX 会失效
7. 左连接应该加在右表上;
8. 小表做主表
9. 优先优化 nestedloop 的内层循环
10. 保证 JOIN 语句的条件字段有 INDEX
11. **`VARCHAR 类型必须有单引号`**
12. 少用 OR, 用它连接时会索引失效

13. 覆盖索引下 INDEX 是永远不会失效的
14. INDEX(a, b, c) 如果中间断了之后的索引都会失效; `> <`之后的索引也会失效; `LIKE` 之后索引也会失效
15. ORDER BY 后的字段有序
16. 定值, 范围还是排序, 一般 ORDER BY 是给个范围
17. GROUP BY 基本上都需要进行排序, 会有临时表产生, 实质是先排序后分组
18. EXISTS: 将主查询中的数据放到子查询中验证, 根据验证结果(TRUE/FALSE)来决定主查询数据是否保留.
19. WHERE 高于 HAVING, 能写在 WHERE 限定的条件就不要去 HAVING 限定
20. 表读锁只允许读自己这张表, 其他 session 只阻塞修改这张表
21. 表写锁只允许读写自己这张表, 其他 session 只阻塞读写这张表
22. 表锁: `读锁会阻塞写, 但是不会堵塞读; 而写锁则会把读和写都堵塞`
23. 行锁: **`读己之所写`**
24. 通过范围查找会锁定范围内所有的索引键值, 即使这个键值不存在.

## reference

1. https://blog.csdn.net/weixin_33755554/article/details/93881494
2. https://my.oschina.net/bigdataer/blog/1976010
