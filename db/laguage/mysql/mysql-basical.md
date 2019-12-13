## 一、 相关概念

1. 数据库[database]: 数据库是一个以某种有组织的方式存储的数据集合.
2. DBMS: 数据库管理系统
3. 模式[schema]: 关于数据库和表的布局及特性的信息.
4. 主键[primarykey]:一列(或一组列), 其值能够唯一区分表中每个行[不同且非空].
5. SQL: 结构化查询语言.
6. SP: 存储过程

## 二、 基础指令

```sql
-- 0. 帮助指令
HELP SHOW;
-- 1. change database
USE DATABASE_NAME;
-- 2. 查询所有数据名称
SHOW databases;
-- 3. 查看一个数据库内的所有表
SHOW TABLES;
-- 4. 查看一张表的所有字段
SHOW COLUMNS FROM TABLE_NAME; -- DESCRIBE TABLE_NAME;
-- 5. 查看建库/表 SQL
SHOW CREATE DATABASE/TBALE DATABASE_NAME/TABLE_NAME;
-- 6. 查看授权用户
SHOW GRANTS;
-- 7. 查看数据库服务器的错误
SHOW WARNINGS/ERRORS;
-- 8. View the database engine
SHOW ENGINES;
```

## 1. SELECT

### syntax

```sql
SELECT DISTINCT CONCAT(RTRIM/LTRIM())*  AS ... -- 连接函数/去空格
FROM ...
[LEFT/RIGHT/FULL] JOIN ... ON ...
WHERE ... REGEXP BINARY BETWEEN ... AND ...  AND/OR xx NOT IN (..., ...) -- 闭区间 <> 不等于
HAVING ....
GROUP BY ... ASC/DESC
ORDER BY ..
LIMIT START_POSITION, LENGTH
LIMIT LENGTH OFFSET START_POSITION
```

### wildcard

1. LIKE 操作符: 区分大小写

```txt
1.1 `%:` 表示任何字符出现任意次数
1.2 `_:` 表示只匹配单个字符
```

2. `WHERE ... BINARY REGEXP`

### JOIN

#### 等值连接、非等值连接、自连接

```sql
SELECT COLUMN_NAME, ...
FROM TABLE_NAME
INNER|LEFT OUTER|RIGHT OUTER|CROSS JOIN TABLE_NAME ON  CONDITION
INNER|LEFT OUTER|RIGHT OUTER|CROSS JOIN TABLE_NAME ON  CONDITION
WHERE ...
GROUP BY ...
HAVING ...
ORDER BY ...
```

## 2. UPDATE

```sql
UPDATE TABLE_NAME
SET COLUMN = VALUE , COLUMN = VALUE
WHERE ...
```

## 3. INSETR

```sql
INSERT INTO TABLE_NAME VALUES (VALUE1, VALUE2,....)
INSERT INTO TABLE_NAME (COLUMN1, COLUMN1,...)VALUES (VALUE1, VALUE2,....)
```

## 4. DELETE

```sql
DELETE FROM TABLE_NAME WHERE ...
-- truncate 删除带自增长的列的表后, 如果再插入数据, 数据从 1 开始
TRUNCATE table TABLE_NAME

DELETE ALIAS, ALIAS
FROM TABLE_NAME ALIAS, TABLE_NAME ALIAS
WHERE ...
```

## 5. DATABASE

```sql
-- create database
CREATE DATABASE DATABASE_NAME
-- drop database
DROP DATABASE DATABASE_NAME
```

## 6. TABLE

```sql
-- 1. create table
CREATE TABLE IF NOT EXISTS test_create(
  id BIGINT(20)UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  gmt_create DATETIME NOT NULL COMMENT 'create time',
  user_name NVARCHAR(256)NOT NULL COMMENT 'user name',
  user_value VARCHAR(256)NOT NULL COMMENT 'user value',
  gender TINYINT(1)DEFAULT true COMMENT 'user gender default 1',
  transaction_id VARCHAR(36)NOT NULL COMMENT 'transaction id',
  descripttion BLOB NOT NULL COMMENT 'decription about user',
  is_deleted TINYINT(1)DEFAULT false COMMENT 'user gender default false',
  create_time DATETIME NOT NULL comment 'create time',
  jpaentity_id INT comment 'foregin key',
  PRIMARY KEY (id),
  INDEX (create_time, user_name),
  UNIQUE KEY uk_name_value (user_name, user_value), -- can be null
  KEY transaction_id (transaction_id),
  FOREIGN KEY (jpaentity_id)REFERENCES JPAEntity(id)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

-- 2. get table info
DESC TABLE_NAME;

-- 3. alter table
ALTER TABLE TBALENAME ADD|MODIFY|DROP|CHANGE COLUMN COLUMNNAME DATETYPE;
-- 3.1. alter column
ALTER TABLE TBALENAME CHANGE COLUMN sex gender CHAR;
-- 3.2 alter table name
ALTER TABLE TBALENAME RENAME [TO] NEW_TBALENAME;
-- 3.3 alter column type and row constraint
ALTER TABLE TBALENAME MODIFY COLUMN COLMUNNAME DATE ;
-- 3.4 add column
ALTER TABLE TBALENAME ADD COLUMN NEW_COLUMNNAME VARCHAR(20)FIRST;
-- 3.5 minus column
ALTER TABLE TBALENAME DROP COLUMN COLMUNNAME;

-- 4. delete table
DROP TABLE [IF EXISTS] TBALENAME;
```

## 7. TRANSACTION

- 7.1 definition:
  ```txt
  通过一组逻辑操作单元(一组 DML-sql 语句), 将数据从一种状态切换到另外一种状态
  ```
- 7.2 feature: ACID

  - 原子性: 要么都执行, 要么都回滚
  - 一致性: 保证数据的状态操作前和操作后保持一致
  - 隔离性: 多个事务同时操作相同数据库的同一个数据时, 一个事务的执行不受另外一个事务的干扰
  - 持久性: 一个事务一旦提交, 则数据将持久化到本地, 除非其他事务对其进行修改

- 7.3 coding step

  - start transaction[disable auto commit]
  - coding transaction unit[sql]
  - commit transaction or rollback:

  ```sql
  SET autocommit=0;
  START TRANSACTION;
  COMMIT [to BREAKPOINT];
  ROLLBACK [to BREAKPOINT];
  ```

- 7.4 category

  - Implicit transactions: with no obvious sign of starting and ending the transaction[insert/update/delete]
  - Explicit transaction: with an obvious sign of starting and ending the transaction

- 7.5 avoid transaction concurrency

  - set transaction isolation level

  ```sql
  -- 脏读: 一个事务读取到了另外一个事务未提交的数据.
  -- 不可重复读: 同一个事务中, 多次读取到的数据不一致.
  -- 幻读: 一个事务读取数据时, 另外一个事务进行更新, 导致第一个事务读取到了没有更新的数据.

  READ UNCOMMITTED
  READ COMMITTED  -- 可以避免脏读
  REPEATABLE READ -- 可以避免脏读、不可重复读和一部分幻读
  SERIALIZABLE -- 可以避免脏读、不可重复读和幻读
  ```

  - set isolation level:

  ```sql
  -- 设置隔离级别：
  SET SESSION|GLOBAL TRANSACTION ISOLATION LEVEL 隔离级别名;
  -- 查看隔离级别:
  SELECT @@tx_isolation;
  ```

## 8. view

- 8.1 definition: a virtual table

- 8.2 diffence

  |      | 使用方式 |         占用物理空间          |
  | :--: | :------: | :---------------------------: |
  | 视图 | 完全相同 | 不占用, 仅仅保存的是 sql 逻辑 |
  |  表  | 完全相同 |             占用              |

- 8.3 feature

  - SQL 语句提高重用性, 效率高
  - 和表实现了分离, 提高了安全性

- 8.4 syntax

  ```sql
  -- 1. create view
  CREATE VIEW  VIEWNAME AS
  SELECT * FROM ...
  -- 2. select: as table
  -- 3. update view
  CREATE OR REPLACE VIEW VIEWNAME AS SELECT ... FROM ... WHERE ...;
  ALTER VIEW VIEWNAME AS SELECT ... FROM ...;
  -- 4. delete view
  DROP VIEW VIEWNAME, VIEWNAME2, VIEWNAME3;
  -- 5. description view
  DESC VIEWNAME;
  SHOW CREATE VIEW VIEWNAME;
  ```

- 8.5 note

1.  包含以下关键字的 VIEW 不能更新: 分组函数, DISTINCT, GROUP BY, HAVING, UNION [ALL], 常量视图, **`SELECT 中包含子查询 JOIN ... FROM 一个不能更新的视图 WHERE 子句的子查询引用了 FROM 子句中的表`**

## 9. SP

- 9.1 definition: a collection of pre-compiled sql statements
- 9.2 feature
  - reuse
  - effective
  - reduce transfer and connection
- 9.3 syntax
  ```sql
  CREATE PROCEDURE SP_NAME (IN|OUT|INOUT ARGUS  DATATYPE, ...)
  BEGIN
    SP_BODY
  END
  ```
- 9.4 use
  ```sql
  call SP_NAME(SP_PARAMETERS)
  ```

### variable

### Process control

- if function
  ```sql
  IF (condition, value1, value2)
  ```
- if elseif
  ```sql
  IF 情况1 THEN 语句1;
  ELSEIF 情况2 THEN 语句2;
  ...
  ELSE 语句N;
  END IF;
  ```
- case

  ```sql
  -- one
  CASE 表达式
    WHEN 值1 THEN 结果1或语句1(如果是语句, 需要加分号)
    WHEN 值2 THEN 结果2或语句2(如果是语句, 需要加分号)
    ...
    ELSE 结果N或语句N(如果是语句, 需要加分号)
  END AS COLUMN_NAME

  -- other
  CASE
    WHEN 条件1 THEN 结果1或语句1(如果是语句, 需要加分号)
    WHEN 条件2 THEN 结果2或语句2(如果是语句, 需要加分号)
    ...
    ELSE 结果N或语句N(如果是语句, 需要加分号)
    END AS COLUMN_NAME

  -- more case in select
  SELECT CASE WHEN condition THEN VALUE1 ELSE VALUE2 END AS totalCashDrop,
  COALESCE(SUM(CASE WHEN ftt.TransactionType = 'TABLE_FILL' THEN ftt.Amount ELSE 0 END), 0) AS totalTableFill
  FROM ...
  WHERE...
  ```

- while
  ```sql
  LABEL: WHILE 循环条件  DO
      循环体
    END WHILE LABEL;
  ```
- loop
  ```sql
  LABEL: LOOP
    循环体;
  END LOOP LABEL;
  ```
- repeat
  ```sql
  LABEL: REPEAT
    循环体;
  UNTIL 结束循环的条件
  END REPEAT LABEL;
  ```
- sample

  ```sql
  TRUNCATE TABLE admin$
  DROP PROCEDURE test_while1$
  CREATE PROCEDURE test_while1(IN insertCount INT)
  BEGIN
    DECLARE i INT DEFAULT 1;
    a:WHILE i<=insertCount DO
      INSERT INTO admin(username,`password`) VALUES(CONCAT('xiaohua',i),'0000');
      IF i>=20 THEN LEAVE a;
      -- IF MOD(i,2)!=0 THEN ITERATE a;
      END IF;
      SET i=i+1;
    END WHILE a;
  END $

  -- two
  DROP TABLE IF EXISTS stringcontent;
  CREATE TABLE stringcontent(
    id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(20)

  );
  DELIMITER $
  CREATE PROCEDURE test_randstr_insert(IN insertCount INT)
  BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE str VARCHAR(26) DEFAULT 'abcdefghijklmnopqrstuvwxyz';
    DECLARE startIndex INT;#代表初始索引
    DECLARE len INT;#代表截取的字符长度
    WHILE i<=insertcount DO
      SET startIndex=FLOOR(RAND()*26+1);#代表初始索引，随机范围1-26
      SET len=FLOOR(RAND()*(20-startIndex+1)+1);#代表截取长度，随机范围1-（20-startIndex+1）
      INSERT INTO stringcontent(content) VALUES(SUBSTR(str,startIndex,len));
      SET i=i+1;
    END WHILE;
  END $

  CALL test_randstr_insert(10)$
  ```

## 10. function

```sql
CREATE FUNCTION FUNCTION_NAME(PARAMETER PARAMETER_TYPE, ...)RETURNS DATATYPE
  BEGIN
    FUNCTION_BODY
  END

SELECT FUNCTION_NAME(PARAMETERS)
```

## diff

- 函数和存储过程的区别

  |   type   |         关键字         |    调用语法     |                          返回值                          |   应用场景   |
  | :------: | :--------------------: | :-------------: | :------------------------------------------------------: | :----------: |
  |   函数   | FUNCTION SELECT 函数() |   只能是一个    | 一般用于查询结果为一个值并返回时, 当有返回值而且仅仅一个 |
  | 存储过程 |       PROCEDURE        | CALL 存储过程() |                    可以有 0 个或多个                     | 一般用于更新 |

- 用户变量和局部变量

  |  作用域  |       定义位置        |                     语法                      |
  | :------: | :-------------------: | :-------------------------------------------: |
  | 用户变量 |       当前会话        |     会话的任何地方, 加@符号, 不用指定类型     |
  | 局部变量 | 定义它的 BEGIN END 中 | BEGIN END 的第一句话 一般不用加@,需要指定类型 |

- 流程控制

  |   type    |     应用场合     |
  | :-------: | :--------------: |
  |  if 函数  |    简单双分支    |
  | case 结构 | 等值判断的多分支 |
  |  if 结构  | 区间判断的多分支 |

---

## 函数

1. 字符

```sql
Concat() -- 连接 select
Left()-- 返回串左边的字符
Length() -- 返回串的长度
Locate()-- 找出串的一个子串
Lower()-- 将串转换为小写
Trim()
LTrim()-- 去掉串左边的空格
RTrim()-- 去掉串右边的空格
Right()-- 返回串右边的字符
Soundex()-- 返回串的SOUNDEX值
SubString()-- 返回子串的字符
Upper()-- 将串转换为大写
```

2. 时间

```sql
Now()
AddDate()-- 增加一个日期（天、周等）
AddTime()-- 增加一个时间（时、分等）
CurDate()-- 返回当前日期
CurTime()-- 返回当前时间
Date()-- 返回日期时间的日期部分
DateDiff()-- 计算两个日期之差
Date_Add()-- 高度灵活的日期运算函数
str_to_date()
Date_Format()-- 返回一个格式化的日期或时间串
Day()-- 返回一个日期的天数部分
DayOfWeek()-- 对于一个日期, 返回对应的星期几
Hour()-- 返回一个时间的小时部分
Minute()-- 返回一个时间的分钟部分
Month()-- 返回一个日期的月份部分
Now()-- 返回当前日期和时间
Second()-- 返回一个时间的秒部分
Time()-- 返回一个日期时间的时间部分
Year()-- 返回一个日期的年份部分
```

- 时间 FORMAT

  | 格式符 |    功能    |
  | :----: | :--------: |
  |   %Y   | 四位的年份 |
  |   %y   | 两位的年份 |
  |   %m   |   0 始月   |
  |   %c   |   1 始月   |
  |   %d   |   1 始日   |
  |   %H   |  24 制时   |
  |   %h   |  12 制时   |
  |   %i   |     分     |
  |   %s   |     秒     |

3. 数学函数

```sql
round  -- 四舍五入
rand -- 随机数
floor -- 向下取整
ceil -- 向上取整
mod -- 取余
truncate -- 截断
```

4. 流程控制函数

```sql
if 处理双分支
case语句 处理多分支
    情况1：处理等值判断
    情况2：处理条件判断
```

5. 其他函数

```sql
version版本
database当前库
user当前连接用户
```

---

## NOTICE

1. NULL 无值(no value), 它与字段包含 0, 空字符串或仅仅包含空格不同
2. WHERE 后的 AND 优先级高于 OR.

---

## Coding

1.  SQL 关键字使用大写, 而对所有列和表名使用小写
