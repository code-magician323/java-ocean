<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

**Table of Contents** _generated with [DocToc](https://github.com/thlorenz/doctoc)_

- [一、linux](#%E4%B8%80linux)
- [二、用户](#%E4%BA%8C%E7%94%A8%E6%88%B7)
- [三、网络](#%E4%B8%89%E7%BD%91%E7%BB%9C)
- [四、软件相关](#%E5%9B%9B%E8%BD%AF%E4%BB%B6%E7%9B%B8%E5%85%B3)
  - [4.1 安装](#41-%E5%AE%89%E8%A3%85)
  - [4.2 卸载](#42-%E5%8D%B8%E8%BD%BD)
- [五、文件](#%E4%BA%94%E6%96%87%E4%BB%B6)
- [六、磁盘](#%E5%85%AD%E7%A3%81%E7%9B%98)
  - [6.1 参数说明](#61-%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E)
- [七、服务](#%E4%B8%83%E6%9C%8D%E5%8A%A1)
- [八、Vim](#%E5%85%ABvim)
- [Ubuntu](#ubuntu)
  - [一、翻墙 VPN[SS]](#%E4%B8%80%E7%BF%BB%E5%A2%99-vpnss)
  - [二、优化 Ubuntu 的桌面](#%E4%BA%8C%E4%BC%98%E5%8C%96-ubuntu-%E7%9A%84%E6%A1%8C%E9%9D%A2)
  - [三、zsh 使用 alias 快速启动](#%E4%B8%89zsh-%E4%BD%BF%E7%94%A8-alias-%E5%BF%AB%E9%80%9F%E5%90%AF%E5%8A%A8)
- [注 1: 搜狗输入法的安装:](#%E6%B3%A8-1-%E6%90%9C%E7%8B%97%E8%BE%93%E5%85%A5%E6%B3%95%E7%9A%84%E5%AE%89%E8%A3%85)
- [注 2: 通过命令打开文件夹 opendir:](#%E6%B3%A8-2-%E9%80%9A%E8%BF%87%E5%91%BD%E4%BB%A4%E6%89%93%E5%BC%80%E6%96%87%E4%BB%B6%E5%A4%B9-opendir)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 一、linux

```shell
# 1.1 查看 Linux 的内核
uname -a
# 1.2 查看 linux 系统版本
lsb_release -a

# 2.1 查看进程
ps -ef | grep tomact | grep -v grep # 根据进程名查看
lsof -i:8080 #根据端口号查看
netstat -anp | grep 8080 #根据端口号查看
# 2.2 查看进程服务状态
systemctl status mssql-server

# 3. 查看历史命令
history |grep xxx

# 4. 给文件权限
sudo chmod 755 filename

# 5. 常看当前所在的目录
pwd

# 6.1查看可执行文件位置
which nginx
# 6.2 查找文件位置
locate filename

# 7. 中文乱码
iconv -f gbk -t utf8 一念永恒.txt > 一念永恒.txt.utf8

# 8.1  cat 浏览文件
cat FILENAME | more

# more 的解释
## Enter 代表向下翻『一行』；
## q 代表立刻离开more ，不再显示该文件内容。
## Ctrl+F 向下滚动一屏
## Ctrl+B 返回上一屏
## = 输出当前行的行号
## :f 输出文件名和当前行的行号

# 8.2 tail 浏览文件
tail -f 文件 # 实时追踪该文档的所有更新
tail -n number 文件 # 查看文件头 number 行内容
# 8.3 less 浏览文件
less FILENAME
# 8.4 head 浏览文件
head -number FILENAME

# 9. echo
echo "内容" >> FILENAME # 向 FILENAME 文件追加内容

# 10. ln  指令软链接也成为符号链接
ln -s [原文件或目录] [软链接名] # 功能描述：给原文件创建一个软链接
```

## 二、用户

```shell
# 1.1 创建用户[指定家目录]
useradd [-d /home/目录] USERNAME
# 1.2 创建用户并分配到指定组
useradd -g 用户组 用户名

# 2.1 创建组
groupadd GPROUPNAME
# 2.2 删除组
groupdel GROUPNAME
# 2.3 修改用户到指定组
usermod -g 用户组 用户名

# 3. 设置用户密码
passwd  USERNAME

# 4.1 删除用户，保留home
userdel USERNAME
# 4.2 删除用户，不保留home
userdel -r USERNAME

# 5.1 常看当前用户
whoami # zack
# 5.2 查询用户
id zack # uid=1000(zack) gid=1000(zack) groups=1000(zack)

# 6.1 切换用户
su -- USERNAME：
# 6.2 回到原来的用户
exit()
```

## 三、网络

```shell
# 执行telnet指令开启终端机阶段作业，并登入远端主机
# 测试远程主机端口是否打开
telnet 192.192.193.211 22
# 下载 URL 内容
wget [选项]... [URL]...
```

## 四、软件相关

```shell
# 1. 查看安装的软件
dpkg --list | grep mysql
# 2. 安装依赖
apt-get -f install
```

### 4.1 安装

```shell
# 1.1 apt 安装
apt-get install git
# 1.2 deb 安装
sudo dpkg -i git.deb
# 1.3 AppImage 安装
chmod a+x git.AppImage
./git.AppImage
```

### 4.2 卸载

```shell
# 1.1 apt 卸载软件和配置
sudo apt-get --purge remove <programname>
# 1.2 apt 只卸载软件保留配置文件
sudo apt-get remove <programname>
# 1.3 dpkg 卸载
dpkg --get-selections |grep firefox
sudo apt-get purge firefox firefox-locale-en firefox-locale-zh-hans
```

## 五、文件

```shell
# 1.1 创建文件目录
mkdir CONTENT_NAME
# 1.2 p 若路径中的某些目录尚不存在，加上此选项后，系统将自动建立好那些尚不存在的目录，即一次可以建立多个目录
mkdir -p[--parents] CONTENT_NAME # mkdir -p BBB/Test # BBB 不存在则创建

# 2. 显示树形结构:
man tree / tree -L 2

# 3. 复制文件夹
cp -r 目录 目录

# 4.1 重命名文件
mv OLDNAME NEWNAME
# 4.2 移动文件夹
mv -i/f 目录 目录

# 5. 查看文件[夹]个数
ls | wc -w
```

## 六、磁盘

```shell
# 1. 查看磁盘使用情况
df -h
# 2.1 看到当前目录下的所有文件占用磁盘大小和总大小
du -ach *
# 2.2 查看文件夹大小
du sh
# 2.3 查看当前目录下所有文件/文件夹的大小
du -sh ./*
# 2.4 查看当前目录下所有一级子目录文件夹大小
du -h --max-depth=1
# 2.5 查看子目录大小
du -sh *
```

### 6.1 参数说明

```js
1. -a||-all  为每个指定文件显示磁盘使用情况, 或者为目录中每个文件显示各自磁盘使用情况.
2. -c||–total 除了显示目录或文件的大小外, 同时也显示所有目录或文件的总和.
3. -H||–si 与-h参数相同, 但是K, M, G是以1000为换算单位, 而不是以1024为换算单位.
4. -h|| –human-readable 以K, M, G为单位, 提高信息的可读性.
5. -b||-bytes 显示目录或文件大小时, 以byte为单位.
6. -k||–kilobytes 以1024 bytes为单位[默认].
7. -m||–megabytes 以1MB为单位.
8. -g 以1GB为单位.
9. -s||–summarize 仅显示总计, 即当前目录的大小.
10. –exclude=目录或文件 略过指定的目录或文件.
11. max-depth=N 只列出深度小于max-depth的目录和文件的信息 –max-depth=0 的时候效果跟–s是 一样
12. –help 显示帮助.
13. –version 显示版本信息.


14. -l||–count-links 重复计算硬件连接的文件.
15. -D||–dereference-args 显示指定符号连接的源文件大小.
16. -L<符号连接>或–dereference<符号连接> 显示选项中所指定符号连接的源文件大小.
17. -S||–separate-dirs 显示每个目录的大小时, 并不含其子目录的大小.
18. -x||–one-file-xystem 以一开始处理时的文件系统为准, 若遇上其它不同的文件系统目录则略过.
19. -X<文件>||–exclude-from=<文件> 在<文件>指定目录或文件。
```

## 七、服务

```shell
# 1.1 重启服务
sudo service apache2 restart
# 1.2 停止服务
sudo service apache2 stop

# 2.1 查看防火墙状态
service firewalld status
systemctl status firewalld
# 2.2 关闭防火墙
sudo service firewalld/iptables stop
sudo ufw disable
# 2.2 开启防火墙
sudo service firewalld/iptables start
sudo ufw enable
# 2.3 重启防火墙
sudo ufw reload

# 3.1 放行端口
sudo ufw allow 27017

# 4.1 查看服务状态
systemctl status mssql-server

## Apache
# 1. 查看 Apache 的 version
apachectl -v
# 2. 重启服务
sudo service apache2 restart
```

## 八、Vim

```shell
# 1. VIM 显示行号
set number/nonumber
  # 进入当前行
  ：20
# 2. VIM 跳转一行的最后一个字符
SHIFT + A # 要在非插入模式
# 3. VIM 删除行
2, 3, 4d
```

---

## Ubuntu

### 一、翻墙 VPN[SS]

> **_天坑 :_** **apt-get install 安装的 `shadowsocks`需要使用到 `distribute`, 但是这个 Python3.3 之后就不在维护, 因此不能使用之上的 python 版本作为默认的 python 环境**

```shell
# 1. 安装shadowsocks
sudo apt-get update
sudo apt-get install shadowsocks
# 2. 在/etc/shadowsocks/目录下, 创建 config.json:
{
  "server":"****",
  "server_port":18889,
  "local_address": "127.0.0.1",
  "local_port":1080,
  "password":"057510",
  "timeout":300,
  "method":"aes-256-cfb",
  "fast_open": false,
  "workers": 1,
  "prefer_ipv6": false
}
# 3. 创建 ss.sh 处理文件
#!/bin/sh
#sr.sh
sslocal -c /etc/shadowsocks/config.json
# 4. 使创建的 ss.sh 文件开机自动执行
# 4.1 建立 rc-local.service 文件
sudo vim /etc/systemd/system/rc-local.service
# 4.2 复制下列内容到 rc-local.service 文件中
[Unit]
Description=/etc/rc.local Compatibility
ConditionPathExists=/etc/rc.local

[Service]
Type=forking
ExecStart=/etc/rc.local start
TimeoutSec=0
StandardOutput=tty
RemainAfterExit=yes
SysVStartPriority=99

[Install]
WantedBy=multi-user.target
# 4.3 创建文件 rc.local
sudo vim /etc/rc.local
# 4.4 将下列内容复制进rc.local文件
#!/bin/sh -e
#
# rc.local
#
# This script is executed at the end of each multiuser runlevel.
# Make sure that the script will "exit 0" on success or any other
# value on error.
#
# In order to enable or disable this script just change the execution
# bits.
#
# By default this script does nothing.
nohup bash /etc/shadowsocks/ss.sh &
# 4.5 给rc.local加上权限
sudo chmod +x /etc/rc.local
# 4.6 将下列内容复制进rc.local文件
sudo systemctl enable rc-local
# 4.7 启动服务并检查状态
sudo systemctl start rc-local.service
sudo systemctl status rc-local.service

# 5. 最重要的一步是设置本地电脑的网络代理
# 在 socks host 中配置代理: 127.0.0.1 1080, 如图:
# 6. 不使用代理时可以选择 disabled; 使用是选择 manual, 为翻墙模式
```

![avatar](https://img-blog.csdnimg.cn/20190525121523221.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

### 二、优化 Ubuntu 的桌面

1. 安装 GNOME Tweaks

   ```shell
   sudo apt-get install gnome-tweak-tool
   sudo apt-get install gnome-shell-extensions
   sudo apt-get install  gnome-shell-extension-dashtodock
   ```

2. 打开 tweak, 选择扩展, 打开 User themes 选项: **然后选择外观，如果 shell 上有感叹号，关闭 tweak, 按 Alt+F2, 输入 r, 执行后重新打开 tweak 就没有感叹号了**
3. 配置主题和图标, [商店网址](https://www.gnome-look.org/s/Gnome/browse/cat/135/)<br>, 完成下载， 移动到 _/usr/share/themes/_ 目录下 **reboot**.
   [具体的应用、图标、shell 资源链接](./gnome-resource)
   ![avatar](https://img-blog.csdnimg.cn/20190526144526575.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)
4. 安装 _Dash to dock_，并进行设置 dock, 具体如图:
   ![avatar](https://img-blog.csdnimg.cn/20190526150622341.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)
5. 更换锁屏的壁纸
6. 设置终端

   ```shell
   # Zsh是替代Bash的终端，还可以设置多种主题，在终端中安装：
   sudo apt-get install zsh
   sh -c "$(curl -fsSL https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"
   # 最后用Zsh替换Bash：
   chsh -s `which zsh`
   ```

### 三、zsh 使用 alias 快速启动

- 在 ~/.bash_aliases 文件下添加
  ```shell
  alias ...="cd ../.."
  alias ....="cd ../../.."
  alias download="cd ~/Downloads"
  ```
- 在 ~/.zshrc 文件最后添加
  ```shell
  if [ -f ~/.bash_aliases ]; then
    . ~/.bash_aliases
  fi
  ```
- 使配置的 alias 生效
  ```shell
  source ~/.zshrc
  ```

---

## 注 1: 搜狗输入法的安装:

- [referece](https://blog.csdn.net/neuroc/article/details/82992524)
- **sudo apt-get install fcitx-bin**
- **sudo dpkg -i 搜狗安装包的文件名**
- **sudo apt install -f # 如果出现依赖错误输入这行处理，执行完后，继续输入上面的命令 sudo apt --fix-broken install**
- **安装成功过后，进入设置 根据红色箭头进入语言安装界面，安装语言（会自动安装中文语言）**
- **注销或者重启**
- **Configure Current Input Method 调出搜狗，并置顶**

## 注 2: 通过命令打开文件夹 opendir:

- 1. 在 _/usr/bin_ 下创建 opendir 文件
- 2. 给权限 755
  ```shell
  touch opendir
  sudo chmod 755 opendir
  ```
- 3. 填写 opendir 文件内容
  ```bash
  if [ -n "$1" ]; then
        nautilus "$1" > /dev/null 2>&1
  else
        nautilus "$(pwd)" > /dev/null 2>&1
  fi
  ```
