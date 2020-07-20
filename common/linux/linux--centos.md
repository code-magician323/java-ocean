## CentOS

### common command

```shell
# 1. reboot
shutdown -r now
# 2. 查看版本
uname -r
cat /etc/redhat-release
lsb_release -a
# 3. 更新 yum 源
yum makecache fast
# 4. start dcoker
systemctl start docker
# 5. install gcc
yum -y install gcc


# 6. stop fallwalld
systemctl stop fallwalld
```

---

## replace yum source

```shell
# 1. 备份原来的yum源
sudo cp /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.bak
# 2. 设置aliyun的yum源
sudo wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
# 3. 添加EPEL源
sudo wget -P /etc/yum.repos.d/ http://mirrors.aliyun.com/repo/epel-7.repo
# 4. 清理缓存并生成新的缓存
sudo yum clean all
sudo yum makecache
```

## install software

### install docker

```shell
# 1. install required packages
# yum install -y epel-release
yum install -y yum-utils device-mapper-persistent-data lvm2
# 2. 配置阿里云加速
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
cat /etc/yum.repos.d/docker-ce.repo
# 3. install docker
sudo yum install docker-ce
# 4. start dcoker
systemctl start docker
# 配置镜像加速
vim /etc/docker/daemon.json
########################################
{
  "registry-mirrors": ["https://wfjvo9ge.mirror.aliyuncs.com"]
}
########################################
docker sudo systemctl daemon-reload
docker sudo systemctl restart docker

# set docker auto start
systemctl is-enabled docker.service
systemctl enable docker.service
systemctl start docker.service

systemctl disable docker.service
systemctl stop docker.service
systemctl restart docker.service

# docker 开机自启动容器
docker update --restart=always 镜像ID
# docker log 查看
sudo docker logs -f -t --tail 行数 容器名
```

### [install mysql](../../db/laguage/mysql/mysql-advanced.md#introduce)

## remove software

### remove docker

```shell
systemctl stop docker
yum -y remove docker-ce
yum remove docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine
rm -rf /var/lib/docker
```

## install tool

- tree

  ```shell
  # 1. install
  yum -y install tree
  # 2. use
  man tree
  tree -L 2
  ```

- zsh

  ```shell
  # 1. install zsh
  echo $SHELL
  yum -y install zsh
  cat /etc/shells
  chsh -s /bin/zsh
  # 2. install oh-my-zsh
  sh -c "$(curl -fsSL https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
  ```

- nc

  ```shell
  # http://vault.centos.org/6.6/os/x86_64/Packages/nc-1.84-22.el6.x86_64.rpm
  # i: install; v: verbose; h: progress bar
  rpm -ivh nc-1.84-22.el6.x86_64.rpm
  ```

---

## python

### python version

```shell
# 1. install python3 dependency: maybe not requried
yum -y install zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel
# 2. download python3
mkdir /usr/local/python3
cd /usr/local/python3
# download by vpn
wget https://www.python.org/ftp/python/3.6.8/Python-3.6.8.tar.xz
tar -xvf Python-3.6.8.tar.xz
# 3. install python3
cd /usr/local/python3/Python-3.6.8
./configure --prefix=/usr/local/python3 # complie
make
make install
# 4. create new softlink
mv /usr/bin/python /usr/bin/python_bak
mv /usr/bin/pip /usr/bin/pip_bak
ln -s /usr/local/python3/bin/python3 /usr/bin/python
ln -s /usr/local/python3/bin/pip3 /usr/bin/pip
# 5. enable pip3
vim ~/.bash_profile

PATH=$PATH:$HOME/bin:/usr/local/python3/bin

source ~/.bash_profile
# 6. modify yum: Change the python of the first line of the following file to python3
vim /usr/bin/yum
vim /usr/libexec/urlgrabber-ext-down
```

### install goaccess

```shell
# 1. install dependy
yum -y install glib2 glib2-devel ncurses ncurses-devel GeoIP GeoIP-devel
# 2. get source code
wget http://tar.goaccess.io/goaccess-1.3.tar.gz
tar -zxvf goaccess-1.3.tar.gz && cd goaccess
# 3. compile
./configure --prefix=/usr/local/goaccess --enable-utf8 --enable-geoip
make
make install
# 4. add goaccess to PATH
vim ~/.bash_profile

PATH=$PATH:$HOME/bin:/usr/local/goaccess/bin

source ~/.bash_profile
```

## question

- 1. docker 安装映射出来的 log 文件; 但是 没有 log 对比之前的非 docker 安装
