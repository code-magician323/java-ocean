## build in cmd and in /redis

docker build -f redis.dockerfile -t dev_redis:5.0 .

## tag image

docker images

docker tag dev_redis:5.0 registry.cn-shanghai.aliyuncs.com/alice52/dev_redis:5.0

## push to aliyun

docker push registry.cn-shanghai.aliyuncs.com/alice52/dev_redis:5.0

## usage

```yml
version: '2.1'
services:
  redis:
    image: registry.cn-shanghai.aliyuncs.com/alice52/dev_redis:5.0
    restart: always
    container_name: dev-redis
    ports:
      - 6379:6379
    volumes:
      - /root/redis/log:/logs
      - /root/redis/data:/data
    environment:
      TZ: Asia/Shanghai
```
