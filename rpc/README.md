## rpc: 远程过程调用

1. type

   | rpc          | Dubbo | mptan | thrift | gRpc     | Brpc |
   | ------------ | ----- | ----- | ------ | -------- | ---- |
   | 开发语言     | java  | java  | 跨语言 | 跨语言   | --   |
   | 服务治理     | Y     | Y     | X      | X        | --   |
   | 多种序列化   | Y     | Y     | thrift | protobuf | --   |
   | 多种注册中心 | Y     | Y     | X      | X        | --   |
   | 管理中心     | Y     | Y     | X      | X        | --   |
   | 跨语言通讯   | X     | X     | Y      | Y        | --   |

2. 目的: `是一种编程模型, 初衷就是不在乎底层的网络技术协议而实现远程调用`

   ![avatar](/static/image/rpc/rpc-feign.png)
