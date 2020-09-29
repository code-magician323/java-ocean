## introduce

1. this code is 3 redis instances in one server, repeat same code in different server
2. notice to replace ip
3. notice the fallwall: `${port} + 1${port}`

## common

```shell
# 1. create cluster
./redis-cli -a AUTH --cluster create 101.37.174.197:8001 101.37.174.197:8002 101.37.174.197:8003 101.132.45.28:8001 101.132.45.28:8002 101.132.45.28:8003 115.159.57.187:6379 --cluster-replicas 1

# 2. connect to cluster server
./redis-cli -a AUTH -c -h IP -p PORT

# 3. look up cluster info
cluster info
cluster nodes

# 4. loop up slots
cluster slots

# 5. readonly for slave
readonly

# 6. add or reduce node dynamic
# 7. reshared slots
```
