## step

1. should create network for mq

    ```shell
    # create network:
    docker network create --driver bridge --subnet 192.168.0.0/16 --gateway 192.168.0.1 rocketmq-net
    # then set this in rocketmq-net
    ```

2. notice broker.conf 
   ```conf
   brokerIP1=101.132.45.28
   ```