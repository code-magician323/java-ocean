## replace source

```shell
# bakup source
mv /etc/apt/sources.list /etc/apt/sources.list.bak

vim /etc/apt/sources.list

# add this code
  deb-src http://archive.ubuntu.com/ubuntu xenial main restricted #Added by software-properties
  deb http://mirrors.aliyun.com/ubuntu/ xenial main restricted
  deb-src http://mirrors.aliyun.com/ubuntu/ xenial main restricted multiverse universe #Added by software-properties
  deb http://mirrors.aliyun.com/ubuntu/ xenial-updates main restricted
  deb-src http://mirrors.aliyun.com/ubuntu/ xenial-updates main restricted multiverse universe #Added by software-properties
  deb http://mirrors.aliyun.com/ubuntu/ xenial universe
  deb http://mirrors.aliyun.com/ubuntu/ xenial-updates universe
  deb http://mirrors.aliyun.com/ubuntu/ xenial multiverse
  deb http://mirrors.aliyun.com/ubuntu/ xenial-updates multiverse
  deb http://mirrors.aliyun.com/ubuntu/ xenial-backports main restricted universe multiverse
  deb-src http://mirrors.aliyun.com/ubuntu/ xenial-backports main restricted universe multiverse #Added by software-properties
  deb http://archive.canonical.com/ubuntu xenial partner
  deb-src http://archive.canonical.com/ubuntu xenial partner
  deb http://mirrors.aliyun.com/ubuntu/ xenial-security main restricted
  deb-src http://mirrors.aliyun.com/ubuntu/ xenial-security main restricted multiverse universe #Added by software-properties
  deb http://mirrors.aliyun.com/ubuntu/ xenial-security universe
  deb http://mirrors.aliyun.com/ubuntu/ xenial-security multiverse

apt-get update
```

## software install

### redis

### mongodb

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
cd /usr/bin
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
# db.addUser("root","Yu1252068782?")
db.createUser({ user: 'zack', pwd: 'Yu1252068782?', roles: [ { role: "root", db: "admin" } ] });
# db.removeUser('username')
db.dropUser('username')
db.auth("root","Yu1252068782?")
show collections
# 7.3 mongodb 远程访问配置(ubuntu)
# 修改mongodb的配置文件, 让其监听所有外网ip
vim /etc/mongodb.conf

  bind_ip = 0.0.0.0  或者 #bind_ip 127.0.0.1
  port = 27017
  auth=true (添加帐号,密码认证)
# 使配置生效
/etc/init.d/mongodb restart
# 7.4 robo3t 登录时, 需要在 Auth Mechanism 这一栏选择 SCRAM-SHA-1
```

### rabbitmq

```shell
# 1. 首先必须要有Erlang环境支持
apt-get install erlang-nox

# 2. 添加公钥
sudo wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc | sudo apt-key add -
apt-get update

# 3. 安装 RabbitMQ
apt-get install rabbitmq-server  #安装成功自动启动

# 4. 查看 RabbitMQ 状态
systemctl status rabbitmq-server

# 5. web 端可视化
rabbitmq-plugins enable rabbitmq_management   # 启用插件
service rabbitmq-server restart # 重启

# 6. 添加用户
rabbitmqctl list_users
rabbitmqctl add_user zack yourpassword   # 增加普通用户
rabbitmqctl set_user_tags zack administrator    # 给普通用户分配管理员角色

# 7. 管理
service rabbitmq-server start    # 启动
service rabbitmq-server stop     # 停止
service rabbitmq-server restart  # 重启
```

### docker

#### 1. 安装:

- uname -a ：查看内核版本
- **step 1: 安装必要的一些系统工具:**
  ```shell
  sudo apt-get update
  sudo apt-get -y install apt-transport-https ca-certificates curl software-properties-common
  ```
- **step 2: 安装 GPG 证书，并查看证书:**
  ```shell
  curl -fsSL http://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
  sudo apt-key fingerprint 0EBFCD88
  ```
- **Step 3: 查看 Ubuntu 版本，写入软件源信息:**
  ```shell
  sudo lsb_release -cs
  # 根据CPU类型选择添加哪种源
  amd64: $ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  armhf: $ sudo add-apt-repository "deb [arch=armhf] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  s390x: $ sudo add-apt-repository "deb [arch=s390x] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  ```
- **Step 4: 更新 apt 包索引，并安装 Docker-CE:**
  ```shell
  sudo apt-get -y update
  sudo apt-get -y install docker-ce
  ```
- **Step 5: 检验安装是否成功:**
  ```shell
  sudo docker version
  # auto start
  sudo systemctl enable docker
  # log location
  /var/lib/docker/containers
  ```

#### 2. 配置文件： 例子： mysql [/etc/mysql/mysql.conf.d# vim mysqld.cnf ]

- 1. 配置镜像加速器
     针对 Docker 客户端版本大于 1.10.0 的用户

     您可以通过修改 daemon 配置文件/etc/docker/daemon.json 来使用加速器

     ```shell
     sudo mkdir -p /etc/docker
     sudo vim /etc/docker/daemon.json
     # Add the code
      {
        "registry-mirrors": ["https://wfjvo9ge.mirror.aliyuncs.com"]
      }
     sudo systemctl daemon-reload
     sudo systemctl restart docker

     # 设置开机启动redis
     sudo docker run --restart=always redis
     ```

### jdk1.8

```shell
# extract jdk in /usr/local/
vim /etc/profile
# set java environment
export JAVA_HOME=/usr/java/jdk1.8.0_**
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib

source /etc/profile
```

---

## python

### python version

```shell
# set python3 default
update-alternatives --install /usr/bin/python python /usr/bin/python2.7 1
update-alternatives --install /usr/bin/python python /usr/bin/python3.6 2

# get list alternatives
update-alternatives --list python
# choose version
update-alternatives --config python
```

### install python3 pip

```shell
# download install script
curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
sudo python get-pip.py
# error
sudo apt-get install python3-distutils
sudo python get-pip.py
sudo pip install -U pip
```
