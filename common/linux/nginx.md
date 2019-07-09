## nginx 的使用

### 安装 nginx

```shell
# install
sudo apt-get install nginx
# check the version and test
nginx -v
nginx -t
```

### nginx 的目录结构

**安装好的文件位置:**

- /usr/sbin/nginx：主程序
- /etc/nginx：存放配置文件
- /usr/share/nginx：存放静态文件
- /var/log/nginx：存放日志

### nginx 的默认端口为 80，该改一下

```shell
# copy the default conf to recovery, such as port in this file.
# location: /etc/nginx/sites-enabled
cp -r sites-enabled/ sites-enabled-backup/
cd sites-enabled
vim default
```

```json
server {
    listen 81 default_server;
    listen [::]:81 default_server;
     ...
}
```

```shell
# copy the conf file to recovery.
cp nginx.conf nginx.conf.backup
vim nginx.conf
```

```json
// do some changes
user root;
server {
    listen    81;
    server_name 101.132.45.28;
    location /image/ {
        root  /usr/local/myImage/;
        autoindex on;
    }
}
// 说明: 这里的访问路径为 101.132.45.28:81/image/xxxx.jpg
// 文件存储路径为: /usr/local/myImage/image/xxxx.jpg.
```

```shell
# enforce the conf
/etc/init.d/nginx -s reload
```

### 启动与停止服务

```shell
# enforce the conf
/etc/init.d/nginx -s reload
# start the service
/etc/init.d/nginx start
# stop the service
/etc/init.d/nginx stop
# restart the service
/etc/init.d/nginx restart
```
