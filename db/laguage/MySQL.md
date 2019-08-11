## 一、 相关概念

1. 数据库[database]: 数据库是一个以某种有组织的方式存储的数据集合.
2. [DBMS]: 数据库管理系统
3. 模式[schema]: 关于数据库和表的布局及特性的信息.
4. 主键[primarykey]:一列(或一组列), 其值能够唯一区分表中每个行.[不同且非空]
5. SQL: 结构化查询语言.

## 二、 基础指令

```sql
-- 0. 帮助指令
HELP SHOW;

-- 1. change database
use DATABASE_NAME;

-- 2. 查询所有数据名称
show databases;

-- 3. 查看一个数据库内的所有表
SHOW TABLES;

-- 4. 查看一张表的所有字段
SHOW COLUMNS from TABLE_NAME; -- DESCRIBE TABLE_NAME;

-- 5. 查看建库/表 SQL
SHOW CREATE DATABASE/TBALE DATABASE_NAME/TABLE_NAME;

-- 6. 查看授权用户
SHOW GRANTS;

-- 7. 查看数据库服务器的错误
SHOW WARNINGS/ERRORS;
```

## 1. SELECT

### 语法

```sql
select  DISTINCT Concat(RTrim/LTrim()) *  AS ... -- 连接函数/去空格
from ...
join ... on ...
where ... REGEXP BINARY between ... and ...  and/or xx not in (..., ...)  -- 闭区间 <> 不等于
having ....
group by ... asc/desc
order by ..
limit START_POSITION, LENGTH
limit LENGTH OFFSET START_POSITION
```

### 通配符

1. LIKE 操作符: 区分大小写
   1.1 `%:` 表示任何字符出现任意次数
   1.2 `_:` 表示只匹配单个字符
2. `where ... BINARY REGEXP`

## 函数

- 1. 字符

  ```sql
  Concat()  -- 连接 select
  Left() -- 返回串左边的字符
  Length()  -- 返回串的长度
  Locate() -- 找出串的一个子串
  Lower() -- 将串转换为小写
  Trim()
  LTrim() -- 去掉串左边的空格
  RTrim() -- 去掉串右边的空格
  Right() -- 返回串右边的字符
  Soundex() -- 返回串的SOUNDEX值
  SubString() -- 返回子串的字符
  Upper() -- 将串转换为大写
  ```

- 2. 时间

  ```sql
  Now()
  AddDate() -- 增加一个日期（天、周等）
  AddTime() -- 增加一个时间（时、分等）
  CurDate() -- 返回当前日期
  CurTime() -- 返回当前时间
  Date() -- 返回日期时间的日期部分
  DateDiff() -- 计算两个日期之差
  Date_Add() -- 高度灵活的日期运算函数
  str_to_date()
  Date_Format() -- 返回一个格式化的日期或时间串
  Day() -- 返回一个日期的天数部分
  DayOfWeek() -- 对于一个日期，返回对应的星期几
  Hour() -- 返回一个时间的小时部分
  Minute() -- 返回一个时间的分钟部分
  Month() -- 返回一个日期的月份部分
  Now() -- 返回当前日期和时间
  Second() -- 返回一个时间的秒部分
  Time() -- 返回一个日期时间的时间部分
  Year() -- 返回一个日期的年份部分
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

- 3. 数学函数

  ```sql
  round  -- 四舍五入
  rand -- 随机数
  floor -- 向下取整
  ceil -- 向上取整
  mod -- 取余
  truncate -- 截断
  ```

- 4. 流程控制函数

  ```sql
  if 处理双分支
  case语句 处理多分支
      情况1：处理等值判断
      情况2：处理条件判断
  ```

- 5. 其他函数
  ```sql
  version版本
  database当前库
  user当前连接用户
  ```

---

## Introduce

1. NULL 无值(no value), 它与字段包含 0、空字符串或仅仅包含空格不同
2. WHERE 后的 AND 优先级高于 OR.

## Coding

1.  SQL 关键字使用大写, 而对所有列和表名使用小写
