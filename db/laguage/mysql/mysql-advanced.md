## 1. architecture

### 1. introduce

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
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Yu1252068782?' WITH GRANT OPTION;

   # 8. set encoding
   vim /etc/my.cnf
   # [mysqld]
   # character_set_server=utf8mb4
   # init_connect='SET NAMES utf8mb4'

   # 9. file explain
   # config: /etc/my.cnf
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

### 2. config file

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

### 3. logic architecture

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
  在该层服务器会解析查询并创建相应的内部解析树, 并对其完成相应的优化: 如确定查询标的顺序、是否利用 Index, 最后生成相应的执行操作.
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

### 4. store engine

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

## 2. index

### 1. introduce

1. SQL 执行慢的原因: `CPU + IO + CONFIG: TOP + FREE + IOSTAT + VMSTAT`

   - 查询语句写的烂
   - 索引失效: 单值/复合
   - 关联查询太多 join
   - 服务器调优及各个参数设置: 缓冲/线程数等

2. sample

   ```sql
   -- single index: table name should be low case
   CREATE INDEX IDX_TABLENAME_COLUMNNAME ON TABLE_NAME (COLUMN_NAME)
   -- complex index
   CREATE INDEX IDX_TABLENAME_COLUMNNAME ON TABLE_NAME (COLUMN_NAME, COLUMN_NAME)
   ```

### 2. join

![avatar](/static/image/db/join.png)

1. inner join

![avatar](/static/image/db/inner-join.png)

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

![avatar](/static/image/db/left-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
LEFT JOIN TBALE B
ON A.KEY = B.KEY
```

3. right join

![avatar](/static/image/db/right-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
RIGHT JOIN TBALE B
ON A.KEY = B.KEY
```

4. left excluding join

![avatar](/static/image/db/left-excluding-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
LEFT JOIN TBALE B
ON A.KEY = B.KEY AND B.KEY IS NULL
```

5. right excluding join

![avatar](/static/image/db/right-excluding-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
GIGHT JOIN TBALE B
ON A.KEY = B.KEY AND A.KEY IS NULL
```

6. outer/full join: mysql donot support

![avatar](/static/image/db/outer-join.png)

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

![avatar](/static/image/db/outer-excluding-join.png)

```sql
SELECT <select_list>
FROM TABLEA A
FULL OUTER JOIN TBALE B
ON A.KEY = B.KEY
WHERE A.Key IS NULL OR B.Key IS NULL
```

### 3. index introduce

1. definition:

   - Index is a **`data structure`** that helps MySQL to obtain data efficiently.
   - **_索引是数据结构; `排好序`的快速`查找` `数据结构`_**
   - 在数据之外, 数据库还维护了维护了满足特定查询算法的数据结构, 这些数据结构以某种方式引用指向数据, 就可以在这些数据结构的基础上实现高级查找算法, 这样的数据结构就是索引
   - 实际上 INDEX 也是一张 TABLE, 保存了主键与索引字段, 并指向实体表的记录
   - 一般来说索引本身也很大, 不可能全部存储在内存中, 因此索引往往以文件形式存储在硬盘上[idb]

2. INDEX structure

   - B+:
     ![avatar](/static/image/db/b+tree.png)
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
   -- 1. create index
   CREATE [UNIQUE] INDEX INDEX_NAME ON TABLE_NAME(COLUMN_NAME);
   ALTER TABLE TABLE_NAME ADD [UNIQUE] INDEX [INDEX_NAME] ON TABLE_NAME(COLUMN_NAME);
   ALTER TABLE TABLE_NAME ADD PRIMARY KEY(COLUMN_NAME)                                             -- UNIQUE AND NOT NULL
   ALTER TABLE TABLE_NAME ADD UNIQUE INDEX_NAME(COLUMN_NAME)                                       -- UNIQUE AND CAN NULL, AND NULL CAN MORE TIMES
   ALTER TABLE TABLE_NAME ADD INDEX INDEX_NAME(COLUMN_NAME)                                        -- COMMON INDEX, CAN MORE TIME ONE VALUE
   ALTER TABLE TABLE_NAME ADD FULLTEXT INDEX_NAME(COLUMN_NAME)                                     -- USE IN FULL TEXT SEARCH

   -- 2. delete index
   DROP INDEX [INDEX_NAME] ON TABLE_NAME

   -- 3. show  index
   SHOW INDEX FROM TABLE_NAME
   ```

7. when should to create index

   - PK: 主键自动建立唯一索引
   - `频繁作为查询`的条件的字段应该创建索引
   - 查询中与其他表关联的字段, `外键关系建立索引`
   - 查询中`排序的字段`, 排序字段若通过索引去访问将大大提高排序的速度
   - 查询中`统计`或者`分组`字段
   - 单值/复合索引的选择问题: 在高并发下倾向创建复合索引

8. when should not to create index

   - 表记录`太少`
   - `WHERE` 条件里用不到的字段不创建索引
   - `频繁更新`的字段不适合创建索引: 因为每次更新不单单是更新了记录还会更新索引, 加重 IO 负担
   - 数据`重复`且`分布平均`的表字段: 如果某个数据列包含许多重复的内容, 为它建立索引就没有太大的实际效果

### 4. perfomance analysis

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

    6. index

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
    1. 表示索引中使用的字节数, 可通过该列计算查询中使用的索引的长度。在不损失精确性的情况下, 长度越短越好
    2. key_len 显示的值为索引最大可能长度, 并非实际使用长度, 即 key_len 是根据表定义计算而得, 不是通过表内检索出的
  - ref
    1. 显示索引那一列被使用了, 如果可能的话, 是一个常数.
    2. 那些列或常量被用于查找索引列上的值
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

    3. USING index

    ```sql
    1. 表示相应的 SELECT 操作中使用了覆盖索引(Coveing Index), 避免访问了表的数据行, 效率不错
    2. 如果同时出现 USING WHERE, 表明索引被用来执行索引键值的查找
    3. 如果没有同时出现 USING WHERE, 表面索引用来读取数据而非执行查找动作
    ```

    4. Using where: 表面使用了 WHERE 过滤

    5. using join buffer: 使用了连接缓存

    6. impossible where: WHERE 子句的值总是 FALSE, 不能用来获取任何元组

    7. select tables optimized away

    ```sql
    在没有 GROUPBY 子句的情况下:
      基于索引优化 MIN/MAX 操作或者对于 MyISAM 存储引擎优化 COUNT(*) 操作,
      不必等到执行阶段再进行计算, 查询执行计划生成的阶段即完成优化
    ```

    8. distinct: 优化 DISTINCT, 在找到第一匹配的元组后即停止找同样值的工作

### 5. index optimization

![avatar](/static/image/db/index.png)

1. 索引分析

   - 单表:
   - 两表: left join 应该在 right 上建立 Index
   - 三表: 小表驱动大表, 除去小表其他 on 条件都应该建立 Index

   - 复合 Index 是有顺序的, 且 > < 之后的 index 会失效
   - 左连接应该加在右表上;
   - 小表做主表
   - 优先优化 nestedloop 的内层循环
   - 保证 JOIN 语句的条件字段有 Index

2. 索引失效(应该避免)

   - 1. 全值匹配我最爱
   - 2. 最佳左前缀法则: `索引是有序的, 查询从索引的最左前列开始并且不跳过索引中的列`
   - 3. 不在索引列上做任何操作[计算/函数/自动 or 手动类型转换], 会导致索引失效而转向全表扫描
        `explain select * from staffs where left(NAME, 4) = 'July'`
   - 4. 存储引擎不能使用索引中范围条件右边的列
     - 如果中间断了, 断了之后的索引都会失效
     - > < 之后的索引也会失效
     - like 之后索引也会失效
   - 5. [valid]尽量使用覆盖索引, 禁止 SELECT \*
   - 6. 非覆盖索引下, 使用不等于[!= 或者 <>] 的时候无法使用索引会导致全表扫描
     - 覆盖索引时不会索引失效
   - 7. IS NULL 在索引会一直失效; IS NOT NULL 在非覆盖索引下会失效:
     - 应该给定默认值, 系统中少出现 NULL
   - 8. LIKE 非覆盖索引下: 以通配符开头['$abc...'] MYSQL 索引失效会变成全表扫描操作
     - 覆盖索引时不会失效
     - `非覆盖索引时 %%`: invalid
     - `非覆盖索引时 %`: invalid
     - `非覆盖索引时 %`: valid
     - how to use index when use `%%`
       - 使用覆盖索引
       - 当覆盖索引指向的字段是 varchar(380)及 380 以上的字段时, 覆盖索引会失效!
   - 9. 字符串不加单引号索引失效
   - 10. 少用 or, 用它连接时会索引失效

- 口诀

  ```js
  全值匹配我最爱, 最左前缀要遵守;
  带头大哥不能死, 中间兄弟不能断;
  索引列上少计算, 范围之后全失效;
  LIKE百分写最右, 覆盖索引不写星;
  不等空值还有or, 索引失效要少用;
  VAR引号不可丢, SQL高级也不难!
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

### 1. slow query optimization

### 2. slow query log

### 3. bulk script

### 4. show profile

### 5. globel query log

---

## 4. lock

### 1. table locks[perfer read]

#### 2. read lock

- env: session01 have read lock, session2 no limit

- session01:

  - [read lock table] session01 just can read lock table
  - [read others] even cannot read other tables
  - [update lock table] cannot update this table
  - [update others] cannot update operation until unlock

- session02:
  - [read lock table] can read session01 locked table: `because read lock is shared`
  - [read others] can read other tables
  - [update lock table] blocked by session01 until session01 unlock table, `then finish update operation`.
  - [update others] can update others table without limit

#### 3. write lock

### 4. row locks[perfer write]

### 5. leaf lock[less use]

---

## 5. master-slave replication

### 1. theory of replication

### 2. principle of replication

### 3. problem

### 4. config

---

## sample

```sql
create table test03(
  a int primary key not null auto_increment,
  c1 char(10),
  c2 char(10),
  c3 char(10),
  c4 char(10),
  c5 char(10)
);

insert into test03(c1,c2,c3,c4,c5) values('a1','a2', 'a3', 'a4','a5');
insert into test03(c1,c2,c3,c4,c5) values('b1','b2', 'b3', 'b4','b5');
insert into test03(c1,c2,c3,c4,c5) values('c1','c2', 'c3', 'c4','c5');
insert into test03(c1,c2,c3,c4,c5) values('d1','d2', 'd3', 'd4','d5');
insert into test03(c1,c2,c3,c4,c5) values('e1','e2', 'e3', 'e4','e5');

select * from test03;
create index IDX_C1_C2_C3_C4 on test03(c1, c2, c3, c4) ;

## ref
-- all valid
-- type: ref, extra: using where
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' and c3 = 'a3' and c4 = 'a4' and c5 = 'a5';
explain select c1, c2, c3, c4, c5 from test03 where c3 = 'a3' and c4 = 'a4' and c5 = 'a5'; -- invalid
-- same the follow 3 sql:
-- type: ref, extra: null
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' and c3 = 'a3' and c4 = 'a4';
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and c4 = 'a4';
explain select c1, c2, c3, c4, c5 from test03 where c3 = 'a3' and c1 = 'a1' and c2 = 'a2' and c4 = 'a4';
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' and c3 = 'a3' ;
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' ;
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' ;
-- type: ref, extra: using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and c3 = 'a3' and  c4 = 'a4' and c5 = 'a5';
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and c3 = 'a3' and  c4 = 'a4';
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c3 = 'a3' and c2 = 'a2' and  c4 = 'a4';
explain select c1, c2, c3, c4 from test03 where c3 = 'a3' and c1 = 'a1' and c2 = 'a2' and  c4 = 'a4';
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and c3 = 'a3' ;
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2';
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' ;

## Range
-- valid, and type: range, extra: Using where; Using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and c3 > 'a3' and  c4 = 'a4';
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' and c3 > 'a3';
-- valid, and type: range, extra: Using index condition
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' and c3 > 'a3' and  c4 = 'a4';
-- valid, and type: range, extra: Using where; Using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and  c4 > 'a4' and c3 = 'a3' ;
-- valid, and type: range, extra: Using index condition
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' and  c4 > 'a4' and c3 = 'a3' ;

## order by
-- type: ref, ref: 2, because c3 break, and c3 used to order by
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' order by c3;
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and  c4 = 'a4' order by c3;
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c3;
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' order by c3;
-- type: ref, extra: Using index condition; Using filesort
-- this is because c3 break
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' order by c4;
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c4;
-- type: ref, extra: Using index condition
explain select c1, c2, c3, c4, c5 from test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4;
-- type: ref, extra: Using where; Using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c3, c4;
-- type: ref, extra: Using where; Using index; Using filesort
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c4, c3;
-- type: ref, extra: Using where; Using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c3, c2;
-- type: ref, extra: Using where; Using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' order by c2, c3;
-- type: ref, extra: Using index condition; Using where
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c2 = 'a2' and c5 = 'a5' order by c2, c3;

## group by
-- type: ref, extra: Using where; Using index
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c4 = 'a4' group by c2, c3;
explain select c1, c2, c3 from test03 where c1 = 'a1' group by c2, c3;
-- type: ref, extra: Using where; Using index; Using temporary; Using filesort
explain select c1, c2, c3, c4 from test03 where c1 = 'a1' and c4 = 'a4' group by c3, c2;
```

---

## issue

1. sql load: from first

   ![avatar](/static/image/db/machine-sequence.png)

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
   - possible_keys: 可能用到的 Index
   - key: 实际用到的 Index
   - key_len: Index 的最大长度
   - ref: 显示 Index 的哪一列被使用了
   - rows: 查出来多少条
   - extra

6. 复合 Index 是有顺序的, 且 > < 之后的 index 会失效
7. 左连接应该加在右表上;
8. 小表做主表
9. 优先优化 nestedloop 的内层循环
10. 保证 JOIN 语句的条件字段有 Index
11. varchar 类型必须有单引号
12. 少用 or, 用它连接时会索引失效
13. 覆盖索引下 Index 是永远不会失效的
14. index(a, b, c) 如果中间断了之后的索引都会失效; `> <`之后的索引也会失效; `LIKE` 之后索引也会失效
15. order by 后的字段有序

16. 定值, 范围还是排序, 一般 order by 是给个范围
17. group by 基本上都需要进行排序, 会有临时表产生
