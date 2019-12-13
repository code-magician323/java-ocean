### docker directory structure

1. log: `/var/lib/docker/container/CONTAINERID/CONTAINERID-json.log`
2. docker install software
   - step 1: install demo with no mounted point, and cp conf to `host machine`
   - step 2: install with mounted point and conf
3. docker will mapping directory in `/var/lib/docker/container`
   - path: `/var/lib/docker/containers/035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f`
   - filename: `config.v2.json`
   - file explain: this file will explain the mounted path for this docker container
   - specify node: `MountPoints`
   ```json
   {
     "StreamConfig": {},
     "State": {
       "Running": true,
       "Paused": false,
       "Restarting": false,
       "OOMKilled": false,
       "RemovalInProgress": false,
       "Dead": false,
       "Pid": 2526,
       "ExitCode": 0,
       "Error": "",
       "StartedAt": "2019-10-03T03:58:58.51394376Z",
       "FinishedAt": "2019-10-03T03:58:29.939636928Z",
       "Health": null
     },
     "ID": "035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f",
     "Created": "2019-08-31T15:32:43.041565523Z",
     "Managed": false,
     "Path": "docker-entrypoint.sh",
     "Args": ["rabbitmq-server"],
     "Config": {
       "Hostname": "rabbit",
       "Domainname": "",
       "User": "",
       "AttachStdin": false,
       "AttachStdout": false,
       "AttachStderr": false,
       "ExposedPorts": {
         "15671/tcp": {},
         "15672/tcp": {},
         "25672/tcp": {},
         "4369/tcp": {},
         "5671/tcp": {},
         "5672/tcp": {}
       },
       "Tty": false,
       "OpenStdin": false,
       "StdinOnce": false,
       "Env": [
         "RABBITMQ_DEFAULT_USER=guest",
         "RABBITMQ_DEFAULT_PASS=guest",
         "RABBITMQ_DEFAULT_VHOST=/",
         "PATH=/opt/rabbitmq/sbin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
         "OPENSSL_VERSION=1.1.1c",
         "OPENSSL_SOURCE_SHA256=f6fb3079ad15076154eda9413fed42877d668e7069d9b87396d0804fdb3f4c90",
         "OPENSSL_PGP_KEY_IDS=0x8657ABB260F056B1E5190839D9C4D26D0E604491 0x5B2545DAB21995F4088CEFAA36CEE4DEB00CFE33 0xED230BEC4D4F2518B9D7DF41F0DB4D21C1D35231 0xC1F33DD8CE1D4CC613AF14DA9195C48241FBF7DD 0x7953AC1FBC3DC8B3B292393ED5E9E43F7DF9EE8C 0xE5E52560DD91C556DDBDA5D02064C53641C25E5D",
         "OTP_VERSION=22.0.7",
         "OTP_SOURCE_SHA256=04c090b55ec4a01778e7e1a5b7fdf54012548ca72737965b7aa8c4d7878c92bc",
         "RABBITMQ_DATA_DIR=/var/lib/rabbitmq",
         "RABBITMQ_VERSION=3.7.16",
         "RABBITMQ_PGP_KEY_ID=0x0A9AF2115F4687BD29803A206B73A36E6026DFCA",
         "RABBITMQ_HOME=/opt/rabbitmq",
         "RABBITMQ_LOGS=-",
         "RABBITMQ_SASL_LOGS=-",
         "HOME=/var/lib/rabbitmq",
         "LANG=C.UTF-8",
         "LANGUAGE=C.UTF-8",
         "LC_ALL=C.UTF-8"
       ],
       "Cmd": ["rabbitmq-server"],
       "Image": "3f92e6354d11",
       "Volumes": { "/var/lib/rabbitmq": {} },
       "WorkingDir": "",
       "Entrypoint": ["docker-entrypoint.sh"],
       "OnBuild": null,
       "Labels": {}
     },
     "Image": "sha256:3f92e6354d117ecfad6ab83c62d299db863da799294f0c62f7d66269bceb9a6b",
     "NetworkSettings": {
       "Bridge": "",
       "SandboxID": "a87bc6b3b6a268ef5e688cafdec13051288adca1b4513e1b568779910bf4e604",
       "HairpinMode": false,
       "LinkLocalIPv6Address": "",
       "LinkLocalIPv6PrefixLen": 0,
       "Networks": {
         "bridge": {
           "IPAMConfig": null,
           "Links": null,
           "Aliases": null,
           "NetworkID": "7c2504e13889d053ac484fa46d9d3cbf8489de503243a7ad7e8f2e0e6d442030",
           "EndpointID": "d81a6fb5e4df99e417319849a38a8061225ea90ceda5fe7efeae629baf5aa254",
           "Gateway": "172.17.0.1",
           "IPAddress": "172.17.0.3",
           "IPPrefixLen": 16,
           "IPv6Gateway": "",
           "GlobalIPv6Address": "",
           "GlobalIPv6PrefixLen": 0,
           "MacAddress": "02:42:ac:11:00:03",
           "DriverOpts": null,
           "IPAMOperational": false
         }
       },
       "Service": null,
       "Ports": {
         "15671/tcp": null,
         "15672/tcp": [{ "HostIp": "0.0.0.0", "HostPort": "15672" }],
         "25672/tcp": null,
         "4369/tcp": null,
         "5671/tcp": null,
         "5672/tcp": [{ "HostIp": "0.0.0.0", "HostPort": "5672" }]
       },
       "SandboxKey": "/var/run/docker/netns/a87bc6b3b6a2",
       "SecondaryIPAddresses": null,
       "SecondaryIPv6Addresses": null,
       "IsAnonymousEndpoint": false,
       "HasSwarmEndpoint": false
     },
     "LogPath": "/var/lib/docker/containers/035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f/035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f-json.log",
     "Name": "/rabbitmq",
     "Driver": "overlay2",
     "OS": "linux",
     "MountLabel": "",
     "ProcessLabel": "",
     "RestartCount": 0,
     "HasBeenStartedBefore": true,
     "HasBeenManuallyStopped": false,
     "MountPoints": {
       "/var/lib/rabbitmq": {
         "Source": "/root/rabbitmq/data",
         "Destination": "/var/lib/rabbitmq",
         "RW": true,
         "Name": "",
         "Driver": "",
         "Type": "bind",
         "Propagation": "rprivate",
         "Spec": {
           "Type": "bind",
           "Source": "/root/rabbitmq/data",
           "Target": "/var/lib/rabbitmq"
         },
         "SkipMountpointCreation": false
       },
       "/var/log/rabbitmq": {
         "Source": "/root/rabbitmq/logs",
         "Destination": "/var/log/rabbitmq",
         "RW": true,
         "Name": "",
         "Driver": "",
         "Type": "bind",
         "Propagation": "rprivate",
         "Spec": {
           "Type": "bind",
           "Source": "/root/rabbitmq/logs",
           "Target": "/var/log/rabbitmq"
         },
         "SkipMountpointCreation": false
       }
     },
     "SecretReferences": null,
     "ConfigReferences": null,
     "AppArmorProfile": "",
     "HostnamePath": "/var/lib/docker/containers/035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f/hostname",
     "HostsPath": "/var/lib/docker/containers/035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f/hosts",
     "ShmPath": "",
     "ResolvConfPath": "/var/lib/docker/containers/035a9c97bf291e463ce6d45a5fa9343f6f9bb989726ecfde496da5db04438a9f/resolv.conf",
     "SeccompProfile": "",
     "NoNewPrivileges": false
   }
   ```

### common comand

    ```shell
    sudo docker version
    # auto start
    sudo systemctl enable docker
    # log location
    /var/lib/docker/containers

    # docker 开机自启动容器
    docker update --restart=always 镜像ID
    # docker log 查看
    sudo docker logs -f -t --tail 行数 容器名
    ```

### docker comand

    ```shell
    service docker start/stop
    docker version
    docker info
    docker --help
    # 查看所有本地镜像
    docker images
    # 删除指定的本地镜像
    docker rmi image-id
    # 我们经常去docker hub上检索镜像的详细信息，如镜像的TAG.
    # eg：docker search redis
    docker search 关键字
    # :tag是可选的，tag表示标签，多为软件的版本，默认是latest
    docker pull 镜像名:tag
    # [‐d：后台运行  ‐p: 将主机的端口映射到容器的一个端口    主机端口:容器内部的端口]
    docker run ‐d ‐p 8888:8080 tomcat
    # [进入 tomcat]
    docker exec -it 85b87053ec8c /bin/bash
    # [复制 war 到 tomcat]
    docker cp example.war 85b87053ec8c:/usr/local/tomcat/webapps
    # [查看容器的日志]
    docker logs container‐name/container‐id
    # 查看防火墙状态
    service firewalld status 
    # 关闭防火墙
    service firewalld stop
    # [查看运行中的容器]
    docker ps
    # [查看所有的容器]
    docker ps ‐a
    # [启动容器]
    docker container start 容器id
    # [停止运行中的容器]
    docker container stop 容器的id
    # [删除一个容器]
    docker container rm 容器id
    ```

### install mysql

    ```shell
    sudo docker pull mysql:5.7

    docker run -p 3306:3306 --name mysql -v /root/mysql/conf.d:/etc/mysql/conf.d -v /root/mysql/logs:/var/log/mysql -v /root/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD='Yu1252068782?' -d mysql:5.7
    sudo docker exec -it mysql /bin/bash
    mysql -u root -p
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Yu1252068782?' WITH GRANT OPTION;
    FLUSH  PRIVILEGES;
    docker logs --tail=200 -f mysql
    ```

### install redis

    ```shell
    docker run -d --name redis -p 6379:6379 -v /root/redis/data:/data -v /root/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf  -v /root/redis/log:/logs redis:5.0 redis-server /usr/local/etc/redis/redis.conf --appendonly yes
    ```

### install rabbitmq

    ```shell
    docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq 3f92e6354d11

    docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -v /root/rabbitmq/data:/var/lib/rabbitmq -v /root/rabbitmq/logs:/var/log/rabbitmq  --hostname rabbit -e RABBITMQ_DEFAULT_VHOST=/ -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest 3f92e6354d11
    ```

### install activemq

    ```shell
    docker pull webcenter/activemq
    # -v /root/activemq/lib/custom:/opt/activemq/lib/custom
    #  the jar must be in /lib
    docker run -d --name activemq -p 8161:8161 -p 61613:61613 -p 61616:61616 -v /root/activemq/conf:/opt/activemq/conf -v /root/activemq/data:/data/activemq -v /root/activemq/logs:/var/log/activemq webcenter/activemq
    ```

### install mongodb

    #where to log
    logpath=/var/log/mongodb/mongodb.log

    ```shell
    docker run -d --name mongo -p 27017:27017 -v /root/mongo/data/db:/data/db cdc6740b66a7

    docker run -d --name mongodb -p 27017:27017 -v /root/mongodb/configdb:/data/configdb/ -v /root/mongodb/logs:/var/log/mongodb -v /root/mongodb/data/db/:/var/lib/mongodb cdc6740b66a7
    docker exec -it CONTAINER_ID /bin/bash

    use admin
    db.createUser({
        user: "admin",
        pwd: "Yu1252068782?",
        roles: [ { role: "root", db: "admin" } ]
    });

    # test auth

    mongo --port 27017 -u admin -p Yu1252068782? --authenticationDatabase admin
    ```

### docker-tomcat

    ```shell
    sudo docker pull tomcat:8.5.40
    mkdir tomcat # /root
    docker run -d -p 8001:8080 --name tomcat8 -v /root/tomcat/conf/:/usr/local/tomcat/conf -v /root/tomcat/logs:/usr/local/tomcat/logs -v /root/tomcat/webapps/:/usr/local/tomcat/webapps tomcat

    sudo docker exec -it tomcat8 /bin/bash
    # look up log
    docker logs --tail=200 -f tomcat8
    ```

### mssql-server

### nginx

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
    ```
