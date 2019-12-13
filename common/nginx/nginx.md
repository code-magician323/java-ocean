- [nginx](#nginx)
  - [introduce](#introduce)
  - [nginx 安装](#nginx-%E5%AE%89%E8%A3%85)
  - [nginx conf](#nginx-conf)
    - [server](#server)
    - [location](#location)
  - [common comand](#common-comand)
  - [nginx 配置 LOG](#nginx-%E9%85%8D%E7%BD%AE-log)
  - [nginx 配置反向代理](#nginx-%E9%85%8D%E7%BD%AE%E5%8F%8D%E5%90%91%E4%BB%A3%E7%90%86)
  - [nginx 配置负载均衡](#nginx-%E9%85%8D%E7%BD%AE%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1)
  - [nginx 配置动静分离](#nginx-%E9%85%8D%E7%BD%AE%E5%8A%A8%E9%9D%99%E5%88%86%E7%A6%BB)
  - [Keepalived/Nginx 高可用集群](#keepalivednginx-%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4)
    - [sample 主从模式](#sample-%E4%B8%BB%E4%BB%8E%E6%A8%A1%E5%BC%8F)
    - [sample 双主模式](#sample-%E5%8F%8C%E4%B8%BB%E6%A8%A1%E5%BC%8F)
  - [nginx 原理](#nginx-%E5%8E%9F%E7%90%86)
    - [master-workers 的机制的好处](#master-workers-%E7%9A%84%E6%9C%BA%E5%88%B6%E7%9A%84%E5%A5%BD%E5%A4%84)
    - [需要设置多少个 worker](#%E9%9C%80%E8%A6%81%E8%AE%BE%E7%BD%AE%E5%A4%9A%E5%B0%91%E4%B8%AA-worker)
    - [设置 worker 数量](#%E8%AE%BE%E7%BD%AE-worker-%E6%95%B0%E9%87%8F)
- [question](#question)

## nginx

### introduce

- definition:
  nginx 是一个高性能的 `HTTP 和 反向代理服务器`. Nginx `专为性能优化而开发`, 负载能力强[50 000 并发连接数]
- feature
  - 高并发/高性能
  - 高可靠性
  - 可扩展性
  - 热部署
  - BSD Lisence
- apply
  - static resource
  - reverse proxy[accelerate/cache]
  - API server

### nginx 安装

- ubuntu

  ```shell
  # install
  sudo apt-get install nginx
  # check the version and test
  nginx -v
  nginx -t

  # change default port
  just change config file `/etc/nginx/sites-enabled/default`
  ```

- centos

  ```shell
  # 1. install pcre
  wget http://downloads.sourceforge.net/project/pcre/pcre/8.37/pcre-8.37.tar.gz
  tar -zxvf pcre-8.36.tar.gz
  cd pcre-8.37
  ./configure
  make && make install

  # 2. install openssl/ zlib
  yum -y install make zlib zlib-devel gcc-c++ libtool openssl openssl-devel

  # 3. install nginx
  tar -zxvf nginx-xx.tar.gz
  ./configure
  make && make install
  ```

- docker

  ```shell
  docker pull nginx
  # get default conf
  docker run --name nginx-test -p 80:80 -d nginx
  docker cp nginx-test:/etc/nginx/nginx.conf /root/nginx/conf/nginx.conf
  docker cp nginx-test:/etc/nginx/conf.d /root/nginx/conf/conf.d

  # delete container
  docker container stop CONTAINER_ID
  docker rm CONTAINER_ID

  # start new container
  docker run -d -p 80:80 --name nginx -v /root/nginx/www:/usr/share/nginx/html -v /root/nginx/conf/nginx.conf/nginx.conf:/etc/nginx/nginx.conf -v /root/nginx/conf/conf.d:/etc/nginx/conf.d -v /root/nginx/logs:/var/log/nginx nginx

  # set aoto start
  docker update --restart=always 镜像ID

  # change default port
  # in this case, we should change the nginx config and reflect port.
  ```

### nginx conf

- diagram
  ![avatar](/static/image/nginx/nginx-conf.png)
- /usr/sbin/nginx：主程序
- /etc/nginx：存放配置文件
- /usr/share/nginx：存放静态文件
- /var/log/nginx：存放日志

- global var: 影响 nginx 服务器整体运行的配置指令
  - nginx 服务器的用户(组)
  - worker process 数: worker_processes 值越大, 并发处理量也越多, 但是
    会受到硬件、软件等设备的制约
  - 进程 PID 存放路径
  - 日志存放路径和类型
  - 配置文件的引入
- event: 影响 Nginx 服务器与用户的网络连接
  - 否开启对多 work process 下的网络连接进行序列化
  - 是否允许同时接收多个网络连接
  - 选取哪种事件驱动模型来处理连接请求
  - 每个 work process 可以同时支持的最大连接数等
  -
- http
  - golbal var
    - http 全局块配置的指令包括文件引入
    - MIME-TYPE 定义
    - 日志自定义
    - 连接超时时间
    - 单链接请求数上限等
  - server: 和虚拟主机有密切关系
    - 全局 server 块: 每个 server 块就相当于一个虚拟主机
      - 监听配置
      - 本虚拟主机的名称或 IP 配置[server_name]
    - location 块:
      - 一个 server 块可以配置多个 location 块
      - 这块的主要作用是基于 Nginx 服务器接收到的请求字符串(例如 server_name/uri-string), 对虚拟主机名称(也可以是 IP 别名)之外的字符串（例如 前面的 /uri-string）进行匹配, 对特定的请求进行处理.
      - 地址定向、数据缓存和应答控制等功能, 还有许多第三方模块的配置也在这里进行.

#### server

- syntax
  ```conf
  server {
    listen       80;
    server_name  localhost; # IP
    LOCATION_MODULE;
  }
  ```

#### location

- syntax
  ```conf
  location [= | ~ | ~* | ^~] {
    root   html;
    index  index.html index.htm;
    autoindex on;
  }
  ```
- explain
  1. =: 用于不含正则表达式的 uri 前, 要求请求字符串与 uri 严格匹配,如果匹配成功, 就停止继续向下搜索并立即处理该请求.
  2. ~: 用于表示 uri 包含正则表达式, 并且区分大小写.
  3. ~\*: 用于表示 uri 包含正则表达式, 并且不区分大小写.
  4. ^~: 用于不含正则表达式的 uri 前, 要求 Nginx 服务器找到标识 uri 和请求字符串匹配度最高的 location 后, 立即使用此 location 处理请求, 而不再使用 location 块中的正则 uri 和请求字符串做匹配.
- 注意: 如果 uri 包含正则表达式, 则必须要有 ~ 或者 ~\* 标识.

- demo
  ```conf
  cp nginx.conf nginx.conf.backup
  vim nginx.conf
  // do some changes
  user root;
  server {
      listen    81; #  listen [::]:81 default_server;
      server_name 101.132.45.28;
      location /image/ {
          root  /usr/local/myImage/;
          autoindex on;
      }
  }
  // 说明: 这里的访问路径为 101.132.45.28:81/image/xxxx.jpg
  // 文件存储路径为: /usr/local/myImage/image/xxxx.jpg.
  ```

### common comand

- ubuntu
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
- centos

  ```shell

  ```

### nginx 配置 LOG

#### web

- 1. config /conf.d/default

  ```conf
  location /logs {
    # nginx logs content
    alias NGINX_LOG_PATH;
    # list content
    autoindex on;
    # default show exact size
    autoindex_exact_size off;
    # default show file time, change it to file system time
    autoindex_localtime on;
    # no cache
    add_header Cache-Control no-store;

    # nginx auth
    auth_basic "Restricted";
    # ngxin log user file
    auth_basic_user_file NGINX_CONFIG_PATH/loguser;
  }
  ```

- 2. config mime.types

  ```types
  text/log log;
  ```

- 3. config auth

  ```shell
  yum -y install httpd-tools

  htpasswd -c NGINX_CONFIG_PATH/loguser loguser
  # get log auth config from 1.
  ```

#### goaccess

```shell
# install goacess
yum -y install glib2 glib2-devel ncurses ncurses-devel GeoIP GeoIP-devel
wget http://tar.goaccess.io/goaccess-1.3.tar.gz
tar -zxvf goaccess-1.3.tar.gz && cd goaccess-1.3
./configure --prefix=/usr/local/goaccess --enable-utf8 --enable-geoip
# add path to env

# start new socket to listen log change
/usr/local/goaccess/bin/goaccess access.log -o /root/nginx/www/report/report.html --real-time-html --time-format='%H:%M:%S' --date-format='%d/%b/%Y' --log-format=COMBINED
```

### nginx 配置 gzip

- conf

  ```conf
  gzip on;
  gzip_buffers 32 4K;
  gzip_comp_level 6;
  gzip_min_length 100;
  gzip_types application/javascript text/css text/xml text/log;
  #配置禁用gzip条件，支持正则。此处表示ie6及以下不启用gzip（因为ie低版本不支持）
  gzip_disable "MSIE [1-6]\.";
  gzip_vary on;
  ```

- notice
  1. 比较小的文件不必压缩
  2. 图片/mp3 这样的二进制文件, 不必压缩: 因为压缩率比较小, 比如 100->80 字节,而且压缩也是耗费 CPU 资源的.

### nginx 配置反向代理

- 定义:
  客户端对代理是无感知的, 将请求发送到反向代理服务器, 由反向代理服务器去选择目标服务器获取数据后返回给客户端, 此时反向代理服务器和目标服务器对外就是一个服务器, 暴露的是代理服务器地址, 隐藏了真实服务器 IP 地址.

- demo diagram

  ![avatar](/static/image/nginx/nginx-reverse-proxy.png)

- sample: 将请求代理到 101.132.45.28 server 上的 8001 端口

  ```conf
  # 1. config 101.37.174.197 conf file
  location /tomcat {
    proxy_pass   http://101.132.45.28:8001;
  }
  # notice
  #   1. request: http://101.37.174.197/tomcat will proxy to http://101.132.45.28:8001/tomcat.

  location /image/ {
    root  html;
    autoindex on;
  }
  # notice
  #   2. request: http://101.37.174.197/image/xx.jpg will find file in /html/image/xx.jpg.
  ```

### nginx 配置负载均衡

- 定义:
  将请求分发到各个服务器上, 将原先请求集中到单个服务器上的情况改为将请求分发到多个服务器上, 将负载分发到不同的服务器

- demo diagram

  ![avatar](/static/image/nginx/nginx-load-balance.png)

- 负载均衡分配策略:

  1. 轮询(默认): 每个请求按时间顺序逐一分配到不同的后端服务器, 如果后端服务器 down 掉, 能自动剔除.
  2. weight: weight 代表权, 重默认为 1, 权重越高被分配的客户端越多指定轮询几率, weight 和访问比率成正比, 用于后端服务器性能不均的情况.
  3. ip_hash: 每个请求按访问 ip 的 hash 结果分配, 这样每个访客固定访问一个后端服务器, 可以解决 session 的问题.
  4. fair(第三方): 按后端服务器的响应时间来分配请求, 响应时间短的优先分配

- sample: 访问两台 server 上的 tomcat

  ```conf
  {
    # ...
    http: {
      upstream LOAD_BALANCE_SERVER_NAME {
        [ip_hash/fair;]
        server 101.132.45.28:8080 weight=10;
        server 101.37.174.197:8080 weight=1;
      }
      # ...

      server: {
        location / {
          # 需要转发请求的服务器--负载均衡也是如此配置
          proxy_pass   LOAD_BALANCE_SERVER_NAME;

          #Proxy Settings
          # 是否跳转
          proxy_redirect     off;
          # 请求要转发的host
          proxy_set_header   Host             $host;
          # 请求的远程地址: 这些在浏览器的header都可看, 不一一解释
          proxy_set_header   X-Real-IP        $remote_addr;
          proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
          proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
          proxy_max_temp_file_size 0;
          # 连接前面的服务器超时时间
          proxy_connect_timeout      90;
          # 请求转发数据报文的超时时间
          proxy_send_timeout         90;
          # 读取超时时间
          proxy_read_timeout         90;
          # 缓冲区的大小
          proxy_buffer_size          4k;
          proxy_buffers              4 32k;
          # proxy_buffers缓冲区, 网页平均在32k以下的
          proxy_busy_buffers_size    64k;
          # 高负荷下缓冲大小(proxy_buffers*2)
          proxy_temp_file_write_size 64k;
        }
        # ...
      }
    }
  }
  ```

### nginx 配置动静分离

- 定义:
  为了加快网站的解析速度, 可以把动态页面和静态页面由不同的服务器来解析, 加快解析速度. 降低原来单个服务器的压力.

- demo diagram

  ![avatar](/static/image/nginx/nginx-static-dynamic.png)

- sample

  ```conf
  {
    # ...
    location / {
      root html;
      index index.html index.htm;
    }

    # proxy to other server, such as oss
    location /static/ {
      root html;
      autoindex on;
    }
  }
  ```

### Keepalived/Nginx 高可用集群

#### sample 主从模式

#### sample 双主模式

### nginx 原理

- master/worker diagram
  ![avatar](/static/image/nginx/master-worker.png)
- worker: 采用的是争抢机制
  ![avatar](/static/image/nginx/worker.png)

#### master-workers 的机制的好处

```txt
1. 对于每个 worker 进程来说, 独立的进程不需要加锁, 所以省掉了锁带来的开销
2. 采用独立的进程, 可以让互相之间不会影响, 一个进程退出后, 其它进程还在工作, 服务不会中断, master 进程则很快启动新的
worker 进程.
3. 当然, worker 进程的异常退出, 肯定是程序有 bug 了, 异常退出, 会导致当前 worker 上的所有请求失败, 不过不会影响到所有请求, 所以降低了风险.
```

#### 需要设置多少个 worker

```txt
1. Nginx 同 redis 类似都采用了 io 多路复用机制
2. 每个 worker 都是一个独立的进程, 但每个进程里只有一个主线程, 通过异步非阻塞的方式来处理请求, 即使是千上万个请求也不在话下.
3. 每个 worker 的线程可以把一个 cpu 的性能发挥到极致.
4. 所以 worker 数和服务器的 cpu数相等是最为适宜的. 设少了会浪费 cpu, 设多了会造成 cpu 频繁切换上下文带来的损耗.
```

#### 设置 worker 数量

```conf
worker_processes 4
#work 绑定 cpu(4 work 绑定 4cpu).
worker_cpu_affinity 0001 0010 0100 1000
#work 绑定 cpu (4 work 绑定 8cpu 中的 4 个) .
worker_cpu_affinity 0000001 00000010 00000100 00001000
```

---

## question

- 1. 403 forbbiden

  - maybe it shoud be config `autoindex on`.

- 2. log in web

  - solution in [nginx 配置 LOG](#nginx-%E9%85%8D%E7%BD%AE-log)
