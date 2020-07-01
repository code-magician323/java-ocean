## Introduce

![avatar](/static/image/java/javase.png)

// TODO: reorgnization

## special content

### [static](./static.md)

### [DataType](./DataType.md)

### [Integer-String](./Integer-String.md)

### [this](./this.md)

### [final](./final.md)

### [Serialize](./Serialize.md)

### [Annotation](./Annotation.md)

### [Socket](./Socket.md)

### [Exception](./Exception.md)

### [ClassLoader](./ClassLoader.md)

### [Enumeration](./Enumeration.md)

### [General](./General.md)

### [HashMap](./HashMap.md)

### [Collection](./Collection.md)

### [Proxy](./Proxy.md)

### [IO](./IO.md)

1. 磁盘 IO: 由于 SSD 的普及, 这里的优化空间再收缩
   - async
2. 内存 IO
3. 网络 IO: 异步处理
4. 
   - 发送数据: 同步发送就可以了, 没有必要异步: 想将数据缓存, 通过网卡将缓存中的数据发送
   - 接受数据: 需要有一个线程一直阻塞, 直到有数据时, 写入缓存, 然后给接收数据的线程发一个通知， 线程收到通知后结束等待， 开始读取数据: `周而复始`; 大量线程时就 频繁切换 CPU ...
   - 

### [Reflect](./Reflect.md)

### [JUC](./JUC.md)

### [JVM](./JVM.md)

### [GC](./GC.md)

### [Java8](./java8/README.md)
