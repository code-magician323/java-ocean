**Table of Contents**

- [mongodb install](#mongodb-install)
  - [introducation](#introducation)
  - [window os](#window-os)
    - [安装 mongodb](#%E5%AE%89%E8%A3%85-mongodb)
  - [linux os](#linux-os)
    - [安装 mongodb](#%E5%AE%89%E8%A3%85-mongodb-1)

# mongodb install

## introducation

1.  基本概念

    - MongoDB 是一个 NoSQL 的数据库
    - MongoDB 是一款文档型数据库
    - MongoDB 数据库使用的是 JavaScript 进行操作的，在 MongoDB 含有一个对 ES 标准实现的引擎，在 MongoDB 中所有 ES 中的语法中都可以使用
    - 数据库（database）
    - 集合（collection）
    - 文档（document）
      在 MongoDB 中，数据库和集合都不需要手动创建, 当我们创建文档时，如果文档所在的集合或数据库不存在会自动创建数据库和集合

2.  基本指令

    ```shell
    # 显示当前的所有数据库
    show dbs
    show databases
    # 进入到指定的数据库中
    use 数据库名
    # 表示的是当前所处的数据库
    db
    # 显示数据库中所有的集合
    show collections
    # 插入文档
    db.<collection>.insert(doc)
    db.collection.insertOne(doc)
    db.collection.insertMany(doc)
    # 查询文档
    db.<collection>.find(doc)
    db.collection.findOne()
    db.collection.find().count()
    # 更新文档
    db.<collection>.update(doc)
    db.collection.update()
    db.collection.updateOne()
    db.collection.updateMany()
    db.collection.replaceOne()
    # 删除文档
    db.<collection>.remove(doc)
    db.collection.deleteOne()
    db.collection.deleteMany()
    db.collection.drop()
    db.dropDatabase()
    ```

3.  常见函数与投影: limit skip sort

    ```js
    //limit skip sort 可以以任意的顺序进行调用
    //sort() 默认情况是按照 _id 升序排列, 可以用来指定文档的排序的规则, sort()需要传递一个对象来指定排序规则 1表示升序 -1表示降序
    db.emp.find({}).sort({ a: 1, b: -1 });
    // 分页  skip((页码-1) * 每页显示的条数).limit(每页显示的条数);
    db.emp
      .find()
      .skip(10)
      .limit(10);

    //在查询时，可以在第二个参数的位置来设置查询结果的投影: 1 表示要; 0 表示不要
    db.emp.find({}, { ename: 1, _id: 0, sal: 1 });
    ```

## window os

### 安装 mongodb

1.  傻瓜式安装
2.  [配置环境变量]: mongo: C:\Program Files\MongoDB\Server\3.2\bin
3.  配置 data 存放位置:

- 在安装盘根目录, 创建一个文件夹 data, 在 data 中创建一个文件夹 db

4.  将 mongodb 设置为系统服务，可以自动在后台启动，不需要每次都手动启动

- 在安装盘根目录创建 data
  - 在 data 下创建 db 和 log 文件夹
- 在安装目录创建配置文件 mongod.cfg

  - 在目录 `C:\Program Files\MongoDB\Server\3.2` 下添加一个配置文件 mongod.cfg
  - 内容

    ```js
    systemLog:
        destination: file
        path: e:\data\log\mongod.log
    storage:
        dbPath: e:\data\db
    ```

- 以管理员的身份打开命令行窗口
- 执行如下的命令
  ```shell
  sc.exe create MongoDB binPath= "\"mongod的bin目录\mongod.exe\" --service --config=\"mongo的安装目录\mongod.cfg\"" DisplayName= "MongoDB" start= "auto"
  ```
  ```shell
  sc.exe create MongoDB binPath= "\"C:\Program Files\MongoDB\Server\3.2\bin\mongod.exe\" --service --config=\"C:\Program Files\MongoDB\Server\3.2\mongod.cfg\"" DisplayName= "MongoDB" start= "auto"
  ```
- 在计算机管理中心找到服务, 启动 mongodb 服务
- 如果启动失败，证明上边的操作有误, 删除之前的配置, 然后从第一步再来一次

  ```shell
  # 在控制台删除之前配置的服务
  sc delete MongoDB
  ```

## linux os

### 安装 mongodb

```shell
# 1. 安装
sudo apt-get install mongodb
# 2. 查看是否运行
pgrep mongo -l  # 8083 mongod

# 3. 查看 mongo 安装位置
# mongo server -- mongod -- /usr/bin/mongod
# mongo clinet -- mongo -- /usr/bin/mongo
# mongo log -- mongodb.log -- /var/log/mongodb/mongodb.log
locate mongo

# 4. 进入 mongod, 指定 data 与 log 的位置
cd /usr/bin/mongod
./mongod --dbpath /var/lib/mongodb/ --logpath /var/log/mongodb/mongodb.log --logappend &
# --dbpath：指定mongo的数据库文件在哪个文件夹
# --logpath：指定mongo的log日志是哪个，这里log一定要指定到具体的文件名
# --logappend：表示log的写入是采用附加的方式，默认的是覆盖之前的文件

# 5. 删除系统非正常关闭时, mongodb 产生的 lock
cd /var/lib/mongodb/
rm mongodb.lock

# 6. 启动关闭 mongo 服务
sudo service mongodb stop 　　
sudo service mongodb start

# 7. 设置数据库连接密码: 这里的密码是单独的数据库(即 use 之后的)
# 7.1 重启服务
sudo service mongodb stop
sudo service mongodb start
# 7.2 进入 mongo
mongo
use admin
db.addUser("root","1983")
db.removeUser('username')
db.auth("root","1983")
show collections
# 7.3 mongodb 远程访问配置(ubuntu)
# 修改mongodb的配置文件, 让其监听所有外网ip
vi /etc/mongodb.conf

  bind_ip = 0.0.0.0  或者 #bind_ip 127.0.0.1
  port = 27017
  auth=true (添加帐号,密码认证)
# 使配置生效
/etc/init.d/mongodb restart
# 7.4 robo3t 登录时, 需要在 Auth Mechanism 这一栏选择 MONGODN-CR
```
