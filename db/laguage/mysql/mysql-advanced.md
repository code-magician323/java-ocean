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
  如果是 SELECT 语句, 服务器会查询内部的缓存: 如果缓存空间足够大， 在有大量的读操作的环境中性能优
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

### 5. index optimization

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
  - [update lock table] blocked by session01 until session01 unlock table，`then finish update operation`.
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
