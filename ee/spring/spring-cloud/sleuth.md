## sleuth

1. why

   - 在微服务模块中, 一个由客户端发起的请求会经过不同的服务节点调用来协同产生最后的请求结果
   - 每个请求都会形成一个复杂的分布式调用链路
   - 链路的任何一环出现高延迟或者错误都会引起整个请求的失败

2. what

   - cloud sleuth[collect-data] 提供了一套完整的服务跟踪的解决方案
   - 并且支持 zipkin[show-ui]

3. work flow

   ![avatar](/static/image/spring/cloud-sleuth.png)
   ![avatar](/static/image/spring/cloud-sleuth-zipkin.jpg)
   ![avatar](/static/image/spring/cloud-zipkin.png)

   - traceId 是 request 的唯一标识
   - spanId 标识发起的请求信息
   - 各个 spanId 通过 parent id 关联

4. install zipkin or run in docker

   ```shel
   # download
   http://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/
   # start
   nohup java -jar zipkin-server-2.12.9-exec.jar &
   ```

5. code: in need monitor module

   - pom

   ```xml
   <!-- sleuth + zipkin -->
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-zipkin</artifactId>
   </dependency>
   ```

   - yml

   ```yml
   spring:
     zipkin:
       base-url: http://101.132.45.28:9411
     sleuth:
       sampler:
         # Probability of requests that should be sampled
         probability: 1
   ```

## reference

1. [sleuth docs](https://cloud.spring.io/spring-cloud-sleuth/reference/html/)
