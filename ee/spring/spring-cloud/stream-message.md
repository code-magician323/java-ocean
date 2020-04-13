## Spring Cloud Stream Message

1. 定义: 是一个构建**`消息驱动微服务`**的框架

   - [binding]app 通过 inputs 和 outputs 与 cloud stream 的 binder 对象交互
   - cloud stream 的 binder 对象负责与消息中间件交互
   - 通过 spring integration 来连接消息中间件, 进而实现消息事件驱动
   - cloud stream 为消息中间件提供了个性化的自动化配置实现: `发布-订阅`, `消费组`, `分区`
   - supported mq: rabbitmq, kafka

2. 功能: 屏蔽底层消息中间件的差异, 降低切换成本, 统一消息的编程模型

3. Binder
   ![avatar](/static/image/spring/cloud-stream.png)

   ![avatar](/static/image/spring/cloud-stream-explian.png)

   - cloud stream 使用 binder 作为中间层实现了`应用程序`和`消息中间件细节`的解耦
   - 通过向应用程序暴露统一的 Channel 通道, 是的应用程序不需要知道中间件的具体实现
   - inputs: consumer
   - outputs: producer
   - cloud stream 的消息通信方式遵循`发布-订阅`模式: **`topic广播`**
     - rabbitmq: exchange
     - kafka: topic
   - Binder: 链接中间件, 屏蔽差异
   - Channel: Queue 的抽象, 实现存储和转发的媒介, 通过 Channel 对 queue 进行配置
   - source and sink

     - source: inputs
     - sink: outputs

   - annotation

   | annotation     | explian                                      |
   | -------------- | :------------------------------------------- |
   | @Input         | mark input channel, accept message to stream |
   | @Output        | mark output channel, message leave stream    |
   | @StreamListner | listen queue, used by consumer               |
   | @EnableBinding | channel bind with exchange                   |

4. config producer

   - yml

   ```yml
   server:
     port: 8801

   spring:
     application:
       name: cloud-stream-provider-service
     cloud:
       stream:
         binders: # define config mq info
           tutorial-rabbitmq: # for integration binding
             type: rabbit # mq type
             environment: # config mq
               spring:
                 rabbitmq:
                   address: 101.132.45.28
                   host: 101.132.45.28
                   port: 5672
                   username: guest
                   password: guest
         bindings: # service integration
           output: #  mark as producer
             destination: tutorial-exchange
             content-type: application/json
             binder: tutorial-rabbitmq

   eureka:
     client:
       register-with-eureka: true
       fetch-registry: true
       service-url:
         defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
     instance:
       lease-renewal-interval-in-seconds: 2 # heartbeat time, default 30s
       lease-expiration-duration-in-seconds: 5 # 如果现在超过了5秒的间隔（默认是90秒）
       instance-id: stream-provider # to be registered with eureka and show as host name
       prefer-ip-address: true # make the access path becomes an IP address

   # disable rabbit mq health check
   management:
     health:
       rabbit:
         enabled: false
   ```

   - code

   ```java
   /**
   * this service is work with mq, so donot need @Service annotation.<br>
   * @EnableBinding(Source.class) is to define message push Channel<br>
   */
   @Slf4j
   @EnableBinding(Source.class)
   public class MessageProviderImpl implements IMessageProvider {
     @Resource private MessageChannel output;

     @Override
     public Object send() {
       String serial = IdUtil.simpleUUID();
       output.send(MessageBuilder.withPayload(serial).build());
       return serial;
     }
   }
   ```

5. config consumer

   - yml

   ```yml
   server:
     port: 8811

   spring:
     application:
       name: cloud-stream-consumer-service
     cloud:
       stream:
         binders: # define config mq info
           tutorial-rabbitmq: # for integration binding
             type: rabbit # mq type
             environment: # config mq
               spring:
                 rabbitmq:
                   address: 101.132.45.28
                   host: 101.132.45.28
                   port: 5672
                   username: guest
                   password: guest
         bindings: # service integration
           input: #  mark as producer
             destination: tutorial-exchange
             content-type: application/json
             binder: tutorial-rabbitmq

   eureka:
     client:
       register-with-eureka: true
       fetch-registry: true
       service-url:
         defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
     instance:
       lease-renewal-interval-in-seconds: 2 # heartbeat time, default 30s
       lease-expiration-duration-in-seconds: 5 # 如果现在超过了5秒的间隔（默认是90秒）
       instance-id: stream-consumer # to be registered with eureka and show as host name
       prefer-ip-address: true # make the access path becomes an IP address

   # disable rabbit mq health check
   management:
     health:
       rabbit:
         enabled: false
   ```

   - code

   ```java
   @EnableBinding(Sink.class)
   @Slf4j
   public class MessageListener {
     @Value("${server.port}") private String port;

     @StreamListener(Sink.INPUT)
     public void receive(Message<?> message) {
       log.info("consumer01 receive message: {}, and port: {}", message.getPayload(), port);
     }
   }
   ```

6. 分组消费: 针对于 consumer 的概念

- 以上的 code 在多消费者模式下会存在消息的重复消费问题: 每个 consumer 都会消费一遍 producer 产生的消息
- group: cloud stream 中处于同一个 group 的消费者彼此之间是竞争关系, 保证消息纸杯消费一次; 不同的组可以重复消费同一条消息
- 解决重复消费问题:

  - 把 consumer 放入一个 group, cloud stream 默认会为每个 consumer 分配一个新的 group
  - config

  ```yml
  spring:
    cloud:
      stream:
        bindings: # service integration
          output:
            group: tutorial-exchange-rabbitmq-consumer
  ```

7. 持久化

   - if the consumer has specify group, it will can consumer message persisted by cloud stream
   - if no specify, fisrt start provider and pulish message; then start consumerA; the message will no durable, and cannot consume by consumerA

## 补充

1. MQ

   - Producer
   - Consumer
   - Message: 生产者和消费者之间靠`消息媒介传`递消息
   - MessageChannel: 消息必须走特定的通道
   - MessageHandler: 消息通道*MessageChannel* 的子接口 **SubscribableChannel** 被 MessageHandler 所订阅处理

   ![avatar](/static/image/spring/cloud-stram-mq.png)

2. topic message consume

   - [rabbitmq](/mq/rabbitmq.md)
   - first start producer and do message publish, then start consumer; the message will not be consumed by consumer in rabbit mq defaultly.

3. `RabbitHealthIndicator: Rabbit health check failed`

   - can disable health check

   ```yml
   # disable rabbit mq health check
   management:
     health:
       rabbit:
         enabled: false
   ```

---

## reference

1. cloud stream: https://spring.io/projects/spring-cloud-stream

2. stream rabbit: https://cloud.spring.io/spring-cloud-static/spring-cloud-stream-binder-rabbit/3.0.1.RELEASE/reference/html/spring-cloud-stream-binder-rabbit.html

3. [rabbitmq](/mq/rabbitmq.md)
