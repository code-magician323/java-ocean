## build in cmd and in /redis

docker build -f redis.dockerfile -t dev-redis:5.0 .

## tag image

docker images

docker tag dev-redis:5.0 registry.cn-shanghai.aliyuncs.com/alice52/dev-redis:5.0

## push to aliyun

docker push registry.cn-shanghai.aliyuncs.com/alice52/dev-redis:5.0

## usage

```yml
version: '2.1'
services:
  redis:
    image: registry.cn-shanghai.aliyuncs.com/alice52/dev-redis:5.0
    restart: always
    container_name: dev-redis
    ports:
      - 6379:6379
    volumes:
      - /root/redis/data:/data
    environment:
      TZ: Asia/Shanghai
```

## issue

1. Can't open the log file: Permission denied: this should be specify log file, not a path

   - issue: start redis container failed, because host machine donot have relative file and relative permisssion
   - solution: create relative file and provide relevant permissions

2. should create /redis.log file, then to mount to container

   - logs folder will create by container in host machine
   - logs folder is empty, so will mount to container, then container will has no file named `redis.log`

3. also can do as below:

   - firstly, donot mount log folder
   - copy container logs folder to host machine

   ```shell
   mkdir -p /root/redis/conf
   docker cp dev-redis:/usr/local/etc/redis/redis.conf /root/redis/conf/redis.conf

   docker cp dev-redis:/logs /root/redis
   chmod 666 -R /root/redis/logs
   ```

   - then mofidy docker-compose config to mount logs folder

   ```yml
   version: '2.1'
   services:
     redis:
       image: registry.cn-shanghai.aliyuncs.com/alice52/dev-redis:5.0
       restart: always
       container_name: dev-redis
       ports:
         - 6379:6379
       volumes:
         - /root/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf
         - /root/redis/logs/redis.log:/logs/redis.log
         - /root/redis/data:/data
       environment:
         TZ: Asia/Shanghai
   ```

4. mount a specify file to container, so must be absolute path, and have already exist, otherwise will throw exception

5. why data folder can be mounted to host machine

   - in this fisrt time, container and host machine has all no file in this folder
   - then container is run to generate, it will generate file in container folder
   - then will mount to host machine

// TODO: 1

6. how to create redis.log file after container start when mount empty log folder

// TODO: 2

7. docker is first startup container, or first mount volume then start up?

   - guest: first mount volume, then startup

// TODO: 3

8. docker: is not find

   - issue: if jenkins in install in docker, which is isolated to other container and host machine, so it cannot get access to
     docker in host machine
   - solution:

     1. install jenkins in host machine directly, so it can get acccess to docker service
     2. [make jenkins run in docker has access to docker service](https://forums.docker.com/t/docker-not-found-in-jenkins-pipeline/31683)

## reference

1. https://www.fatalerrors.org/a/can-t-open-the-log-file-permission-denied.html
2. https://cloud.tencent.com/developer/article/1432962
3. https://forums.docker.com/t/docker-not-found-in-jenkins-pipeline/31683
