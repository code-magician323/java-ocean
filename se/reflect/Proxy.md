## [动态代理](../../ee/spring/spring-framework/3.aop.md)

### 知识点

1. 横切关注点: `跨越应用程序多个模块的功能`.

2. 代理设计模式的原理:
   - 使用一个代理将对象包装起来, 然后用该代理对象取代原始对象. `任何对原始对象的调用都要通过代理`. **代理对象决定是否以及何时将方法调用转到原始对象上**.
3. 动态代理关系:
4. Proxy 中的代码中:
   - 提供具体实现类: ServiceImpl(必须要在方法中自定义异常), 如果在方法中不自定义异常, 代理调用方法时永远都不会出异常;
5. 理解图:
   ![avatar](/static/image/java/proxy.png)

### InvocationHandler

1. InvocationHandler 是由动态代理处理器实现的接口, 对代理对象的方法调用, 会路由到该处理器上进行统一处理.

   ```java
   public interface InvocationHandler {
       /**
        * proxy: 正在被返回的代理对象, 一般不会使用 class com.sun.proxy.$Proxy4
        * method: 调用方法
        * args: 调用方法参数
        **/
       // Processes a method invocation on a proxy instance and returns
       // the result.  This method will be invoked on an invocation handler
       // when a method is invoked on a proxy instance that it is associated with.
       public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
   }
   ```

2. Proxy 用于生成代理对象

   ```java
   public class Proxy implements java.io.Serializable {
       // 获取代理类 <br/>
       // loader: 类加载器
       // interfaces: 类实现的接口
       Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces);

       // 生成代理对象 <br/>
       // loader: 类加载器
       // interfaces: 类实现的接口
       // h: 动态代理回调
       Object newProxyInstance(ClassLoader loader,
                                               Class<?>[] interfaces,
                                               InvocationHandler h);

       // 判断是否为代理类 <br/>
       // cl: 待判断类
       public static boolean isProxyClass(Class<?> cl);

       // 获取代理对象的InvocationHandler <br/>
       // proxy : 代理对象
       InvocationHandler getInvocationHandler(Object proxy);
   }
   ```

### sample1

```java
@Test
/**
    * 功能: 使用代理调用方法，并加上了一些限制(Annotation)
    *   1. 创建一个被代理对象final类型:为执行方法的提供对象;因为被代理对象在代理对象proxy还在使用的时候，就会被当做垃圾回收了.
    *   2. 创建代理对象proxy: 3个参数 Proxy.newProxyInstance(ClassLoader, Class<?>[],InvocationHandler)
    *     2.1 类加载器: 一般就是被代理对象的类加载器(这里由于存在多态，所以最好用被代理对象实现的接口的类加载器)
    *     2.2 获取被代理对象实现的接口的Class数组:
    *       2.2.1 new Class[]{ ArithmeticCaculator.class}
    *       2.2.2 若代理对象不需要实现被代理对象实现的意外的接口方法【不需要代理对象去实现接口的方法】，可以使用: target.getClass().getInterfaces()
    *     2.3 创建InvocationHandler对象(通常使用匿名内部类的方式):1个方法有3个参数    invoke(Object proxy, Method method,Object[] args){method.invoke(obj,args)}
    *      2.3.1 proxy: //正在被返回的代理对象，一般不会使用  class com.sun.proxy.$Proxy4
    *      2.3.2 method: 正在被调用的方法
    *      2.3.3 args:调用方法时传入的参数
    *  3. 调用代理对象执行方法
    *    proxy.methdName(Objrct...args);
    *
    *  Notice:提供具体实现类: ServiceImpl  (必须要在方法中自定义异常)
    *     //如果在方法中不自定义异常，，代理调用方法时永远都不会出异常；必须要在方法中自定义异常
    */
public void testProxy2() {
    final ArithmeticCaculator target = new ArithmeticCaculatorImpl();
    ArithmeticCaculator proxy = (ArithmeticCaculator) Proxy.newProxyInstance(ArithmeticCaculator.class.getClassLoader(),
            target.getClass().getInterfaces(),new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    //在这里调用方法
                    Object obj = method.invoke(target, args);
                    return obj;
                }
            });
    proxy.add(1, 2);
}

@Test
/**
    * 功能: 实现了可以日志.
    *   1. 创建代理对象的返回类型一般是被代理对象实现的接口类型: 这里就一般是ArithmeticCaculator类型
    *
    *   2. 说明3各参数:
    *     2.1 ClassLoader :动态代理产生的对象(arithmeticCaculator)是由那个类加载器加载的: 通常是和被代理对象使用一样的类加载器
    *     2.2 Class<T> :动态代理产生的对象(arithmeticCaculator)需要实现的接口的Class数组:被代理对象实现的接口
    *     2.3 InvocationHandler:当具体调用代理方法时，将产生的行为
    */
public void testProxy() {
    final ArithmeticCaculator arithmeticCaculator =new ArithmeticCaculatorImpl();
    // 1. 创建代理对象: 返回类型一般是被代理对象实现的接口类型；但是必须是接口
    ArithmeticCaculator proxy=  (ArithmeticCaculator) Proxy.newProxyInstance(
        // 2. 获取类加载器: 一般是被代理对象的类加载器(这里由于存在多态，所以最好用被代理对象实现的接口的类加载器)
        ArithmeticCaculator.class.getClassLoader(),
        // 3. 获取被代理对象实现的接口的Class数组:target.getClass().getInterfaces()
        new Class<?>[] { ArithmeticCaculator.class },
        // 4. 创建InvocationHandler对象(通常使用匿名内部类的方式)，在其中实现行为invoke(Object proxy, Method method,Object[] args){method.invoke(obj,args)}
        new InvocationHandler() {
            @Override
            /**
                * 说明3个参数:
                *   1. proxy
                *   2. method: 正在被调用的方法
                *   3. args:调用方法时传入的参数
                */
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                System.out.println(Arrays.asList(args));
                System.out.println(proxy.getClass());    //class com.sun.proxy.$Proxy4
                System.out.println("The method " + method.getName() + " start with "+Arrays.asList(args));

                Object obj=method.invoke(arithmeticCaculator, args);

                System.out.println("The method "+method.getName() + " ends with "+obj );
                return obj;
            }
        });
    // 5.调用代理对象执行方法
    System.out.println(proxy.add(1, 2));
    proxy.mul(1, 2);
}
```

### sample2

1. 已有一个 Handler 的接口

   ```java
   public interface Handler {
       void handle(String data);
   }
   ```

2. Handler 接口的实现类

   ```java
   public class HandlerImpl implements Handler {
       private static final Logger LOG = LoggerFactory.getLogger(HandlerImpl.class);

       @Override
       public void handle(String data) {
           try {
                TimeUnit.MILLISECONDS.sleep(100);
                LOG.info(data);
           } catch (InterruptedException e) {
                e.printStackTrace();
           }
       }
   }
   ```

3. **want to analysis Handler performance**

   - inject customization Handler to HandlerProxy and implements Handler interface method
   - and call customization Handler handle method in overwrite methods

   ```java
   public class HandlerProxy implements Handler {
       private static final Logger LOG = LoggerFactory.getLogger(HandlerProxy.class);
       private final Handler handler;

       public HandlerProxy(Handler handler) {
           this.handler = handler;
       }

       @Override
       public void handle(String data) {
           long start = System.currentTimeMillis();
           this.handler.handle(data);
           long end = System.currentTimeMillis();
           LOG.info("cost " + (end - start) + " ms");
       }
   }
   ```

4. use dynamic proxy

   - implements InvocationHandler

   ```java
   public class HandlerInvocation implements InvocationHandler {
       private static final Logger LOG = LoggerFactory.getLogger(HandlerInvocation.class);
       private final Object target;

       public HandlerInvocation(Object target) {
           this.target = target;
       }

       @Override
       public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
           LOG.info("call method " + method + " ,args " + args);
           long start = System.currentTimeMillis();
           try {
               return method.invoke(this.target, args);
           } finally {
               long end = System.currentTimeMillis();
               System.out.println("cost " + (end - start) + "ms");
           }
       }
   }
   ```

5. test usage

   ```java
   /**
   * This is one of strategy, custom HandlerImpl handler <br>
   * then set it to HandlerProxy and use it. <br>
   *
   * <p>Others, in this case, use HandlerProxy to analysis performance; <br>
   * In HandlerProxy, we aggressive it use HandlerImpl and execute. <br>
   *
   * <p>And can use Dynamic proxy to optimize this code, <br>
   * because it can dynamically create proxies and dynamically handle calls to proxied methods. <br>
   *
   * @author zack <br>
   * @create 2020-02-10 22:02 <br>
   */
   public class ProxyStrategyTest {
       private static final Logger LOG = LoggerFactory.getLogger(ProxyStrategyTest.class);

       @Test
       public void testHandlerProxy() {
           Handler handler = new HandlerImpl();
           Handler proxy = new HandlerProxy(handler);
           proxy.handle("Test");
       }

       @Test
       public void testHandlerInvocation() {
           Handler handler = new HandlerImpl();
           HandlerInvocation invocationHandler = new HandlerInvocation(handler);

           Handler proxy =
               (Handler)
                   Proxy.newProxyInstance(
                       // Get ClassLoader: common target object and always interface
                       // [The class loader of the proxied object]
                       ProxyStrategyTest.class.getClassLoader(),
                       // Get an array of Classes of interfaces implemented by the proxied object:
                       // target.getClass().getInterfaces()
                       new Class<?>[] {Handler.class},
                       // Create an InvocationHandler object(usually using an anonymous inner class)
                       invocationHandler);

           LOG.info("invoke method");
           proxy.handle("Test");

           Class cls = Proxy.getProxyClass(ProxyStrategyTest.class.getClassLoader(), Handler.class);
           LOG.info("isProxyClass: " + Proxy.isProxyClass(cls));
           LOG.info("getInvocationHandler: " + (invocationHandler == Proxy.getInvocationHandler(proxy)));
       }
   }
   ```
