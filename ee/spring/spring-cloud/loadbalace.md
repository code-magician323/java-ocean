## Load Balance[LB]

### Ribbon: _LB + RestTemplate_

1. 提供 **`客户端`** 软件负载均衡算法和服务调用

2. eureka client jar contain ribbon

3. IRule: select service according to specify algorithms

   - struct

     ![avatar](/static/image/spring/cloud-lb-IRule.png)

   - rules
     1. RoundRobinRule: 轮询
     2. RandomRule: 随机
     3. RetryRule: 按照 RoundRobinRule 获取服务, 获取失败在指定时间内重试
     4. WeightedResponseTimeRule: 响应速度越快, 权重越大, 越容易被选
     5. BestAvailableRule: 会先去除由于多次访问故障而处于`断路器`跳闸的服务, 然后选择一个并发量最小的服务
     6. AvailabilityFilteringRule: 去除故障, 选择并发量最小的实例
     7. ZoneAvoidanceRule: 复合判断`SERVER 所在区域的性能和可用性`

4. notice

   - 自定义的 LB 规则不能放在 springboot 的主启动类的 package 里: cannot place in @ComponentScan packge
   - 而且需要告知主启动类使用的 LB 规则: `@RibbonClient(name = "CLOUD-PAYMENT-SERVICE", configuration = CustomLbRule.class)`

5. LB 原理

   - RoundRobinRule 的原理: rest interface request times % cluster namer = server index, and restart server will set zreo to rest request time

     ```java
     List<ServiceInstance> intances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
     var server = instance.get(index);
     ```

   - CAS

6. custom: all this code is in client

   ```java

   // LB Algorithms
   public interface LoadBalancer { ServiceInstance instance(List<ServiceInstance> instances); }

   @Component
   public class CustomLoadBalancer implements LoadBalancer {
     private AtomicInteger atomicInteger = new AtomicInteger(0);

     @Override
     public ServiceInstance instance(List<ServiceInstance> instances) {
         int index = getAndIncrease() % instances.size();
         return instances.get(index);
     }

     /**
     * about AtomicInteger: <br>
     * if (this == expect) { this = update return true; } else { return false; }
     *
     * @return
     */
     private final int getAndIncrease() {
       int current, next;

       do {
         current = atomicInteger.get();
         next = current >= Integer.MAX_VALUE ? 0 : current + 1;
       } while (!this.atomicInteger.compareAndSet(current, next));

       return next;
     }
   }

   // configuration, donot @LoadBalance Annotation
   @Bean
   public RestTemplate getRestTemplate() { return new RestTemplate(); }

   // controller usage
   @Resource private DiscoveryClient discoveryClient;
   @Resource private CustomLoadBalancer customLoadBalancer;

   @GetMapping(value = "/payment/lb")
   public JsonResult getPaymentLB() {

     List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");

     AtomicReference<ServiceInstance> instance = new AtomicReference<>();
     Optional.ofNullable(instances)
         .ifPresent(
             x -> {
               instance.set(customLoadBalancer.instance(x));
             });

     URI uri = instance.get().getUri();
     return restTemplate.getForObject(uri + "/payment/lb/", JsonResult.class);
   }
   ```

## 补充

1. LB

   - 将用户请求平摊到多个服务上, 以达到 HA 的目的: Nginx, LVS

2. comparasion between Nginx and Ribbon

   - [out process]Nginx is BL in server, all client request will coming in and it determine which server handle this request
   - [in process]Ribbon is local[client] LB strategy, it will **`load service info to JVM`** when call microsoft service, then do RPC call in local[client] according to these service info

3. RestTemplate: JsonResult is response model, restTemplate will auto transfer http response to specify model **JsonResult.class**

   - RestTemplate cluster should add this annotation `@LoadBalanced`
   - restTemplate.postForObject(PAYMENT_URL, payment, JsonResult.class)
   - restTemplate.postForEntity(...)
   - restTemplate.getForObject(PAYMENT_URL, JsonResult.class);
   - restTemplate.getForEntity(PAYMENT_URL, JsonResult.class) // this will contain header and request info, and transfer http response body to JsonResult.class model

   ```json
   {
     "headers": {
       "Content-Type": ["application/json"],
       "Transfer-Encoding": ["chunked"],
       "Date": ["Tue, 31 Mar 2020 14:12:37 GMT"],
       "Keep-Alive": ["timeout=60"],
       "Connection": ["keep-alive"]
     },
     "body": {
       // JsonResult Model
       "code": 200,
       "message": "success, and port: 8002",
       "data": {
         "id": 1,
         "serial": "5e908b49-6229-11ea-974c-00163e0a1128"
       }
     },
     "statusCode": "OK",
     "statusCodeValue": 200
   }
   ```
