## 动态代理

### 知识点

1. 横切关注点: `跨越应用程序多个模块的功能`.

2. 代理设计模式的原理:
   > 使用一个代理将对象包装起来, 然后用该代理对象取代原始对象. `任何对原始对象的调用都要通过代理`. **代理对象决定是否以及何时将方法调用转到原始对象上**.
3. 动态代理关系:
4. Proxy 中的代码中:
   > 提供具体实现类: ServiceImpl(必须要在方法中自定义异常), 如果在方法中不自定义异常, 代理调用方法时永远都不会出异常;
5. 理解图:
   ![avatar](https://img-blog.csdnimg.cn/20190513214146334.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

### demo

```java
@Test
/**
    * 功能：使用代理调用方法，并加上了一些限制(Annotation)
    *   1.创建一个被代理对象final类型:为执行方法的提供对象;因为被代理对象在代理对象proxy还在使用的时候，就会被当做垃圾回收了.
    *   2.创建代理对象proxy：3个参数 Proxy.newProxyInstance(ClassLoader,Class<?>[],InvocationHandler)
    *     2.1类加载器：一般就是被代理对象的类加载器(这里由于存在多态，所以最好用被代理对象实现的接口的类加载器)
    *     2.2获取被代理对象实现的接口的Class数组:
    *       2.2.1new Class[]{ ArithmeticCaculator.class}
    *       2.2.2若代理对象不需要实现被代理对象实现的意外的接口方法【不需要代理对象去实现接口的方法】，可以使用：target.getClass().getInterfaces()
    *     2.3创建InvocationHandler对象(通常使用匿名内部类的方式):1个方法有3个参数    invoke(Object proxy, Method method,Object[] args){method.invoke(obj,args)}
    *      2.3.1proxy：//正在被返回的代理对象，一般不会使用  class com.sun.proxy.$Proxy4
    *      2.3.2method：正在被调用的方法
    *      2.3.3args:调用方法时传入的参数
    *  3.调用代理对象执行方法
    *    proxy.methdName(Objrct...args);
    *
    *  Notice:提供具体实现类：ServiceImpl  (必须要在方法中自定义异常)
    *     //如果在方法中不自定义异常，，代理调用方法时永远都不会出异常；必须要在方法中自定义异常
    */
public void testProxy2() {
    final ArithmeticCaculator target=new ArithmeticCaculatorImpl();
    ArithmeticCaculator proxy=(ArithmeticCaculator) Proxy.newProxyInstance(ArithmeticCaculator.class.getClassLoader(),
            target.getClass().getInterfaces(),new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    //在这里调用方法
                    Object obj=method.invoke(target, args);
                    return obj;
                }
            });
    proxy.add(1, 2);
}

@Test
/**
    * 功能：实现了可以日志.
    *   1.创建代理对象的返回类型一般是被代理对象实现的接口类型：这里就一般是ArithmeticCaculator类型
    *
    *   2.说明3各参数：
    *     2.1.ClassLoader :动态代理产生的对象(arithmeticCaculator)是由那个类加载器加载的：通常是和被代理对象使用一样的类加载器
    *     2.2.Class<T> :动态代理产生的对象(arithmeticCaculator)需要实现的接口的Class数组:被代理对象实现的接口
    *     2.3.InvocationHandler:当具体调用代理方法时，将产生的行为
    */
public void testProxy() {
    final ArithmeticCaculator arithmeticCaculator =new ArithmeticCaculatorImpl();
    //1.创建代理对象：返回类型一般是被代理对象实现的接口类型；但是必须是接口
    ArithmeticCaculator proxy=  (ArithmeticCaculator) Proxy.newProxyInstance(
        //2.获取类加载器：一般是被代理对象的类加载器(这里由于存在多态，所以最好用被代理对象实现的接口的类加载器)
        ArithmeticCaculator.class.getClassLoader(),
        //3.获取被代理对象实现的接口的Class数组:target.getClass().getInterfaces()
        new Class<?>[] { ArithmeticCaculator.class },
        //4.创建InvocationHandler对象(通常使用匿名内部类的方式)，在其中实现行为invoke(Object proxy, Method method,Object[] args){method.invoke(obj,args)}
        new InvocationHandler() {
            @Override
            /**
                * 说明3个参数：
                *   1.proxy
                *   2.method：正在被调用的方法
                *   3.args:调用方法时传入的参数
                */
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //打印method
                System.out.println(method);
                //打印args:
                System.out.println(Arrays.asList(args));
                //打印proxy
                System.out.println(proxy.getClass());    //class com.sun.proxy.$Proxy4

                //写日志
                System.out.println("The method "+method.getName()+" start with "+Arrays.asList(args));

                //调用被代理类的方法
                Object obj=method.invoke(arithmeticCaculator, args);

                //写日志
                System.out.println("The method "+method.getName()+" ends with "+obj );
                return obj;
            }
        });
    //5.调用代理对象执行方法
    System.out.println(proxy.add(1, 2));
    proxy.mul(1, 2);
}
```
