## agent

1. diagram

   ![avatar](/static/image/java/javase-agent.png)

2. java-agent 最终展示形式是一个 jar

   - 必须 META-INF/MANIFEST.MF 中指定 Premain-Class 设置 agent 启动类
   - 在 启动类中需要编写 `public static void main(String arg)` 方法
   - 不能直接运行, 必须通过 jvm 参数 -javaagent:xx.jat 附着于其他的 jvm 进程运行

3. build

   - Premain-Classs: required, and agent 启动类
   - Can-Redefine-Classes: default false, 是否允许重新定义 class
   - Can-Retransform-Classes: default false, 是否允许重置 class, 下次加载时会经过 `addTransformer`
   - Boot-Class-Path: agent 所依赖的其他 jar

   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-jar-plugin</artifactId>
               <version>3.1.2</version>
               <configuration>
                   <archive>
                       <manifestEntries>
                           <project-name>${project.name}</project-name>
                           <project-version>${project.version}</project-version>
                           <Premain-Class>cn.edu.ntu.javase.agent.Assist</Premain-Class>
                           <Can-Redefine-Classes>true</Can-Redefine-Classes>
                           <Can-Retransform-Classes>true</Can-Retransform-Classes>
                           <Boot-Class-Path>javassist-3.27.0-GA.jar</Boot-Class-Path>
                       </manifestEntries>
                   </archive>
                   <skip>true</skip>
               </configuration>
           </plugin>
       </plugins>
   </build>
   ```

4. core method: 重新加载一个类的方式

   - 在第一次加载时拦截： `addTransformer`[cannot add method]

     ```java
      instrumentation.addTransformer(
         (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
           if (!className.equals("cn/edu/ntu/javase/agent/Server")) {
             return null;
           }
           return IOUtils.readFully(inputStream, -1, false);
         });
     ```

   - 重新加载一个类进 jvm: retransformClasses:[can add method]

   ```java
   instrumentation.addTransformer(
       (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
         if (!className.equals("cn/edu/ntu/javase/agent/Server")) {
           return null;
         }
           return IOUtils.readFully(inputStream, -1, false);
       },
       true);

   instrumentation.retransformClasses(Server.class);
   ```

   - 重新加载一个类进 jvm: redefineClasses[cannot add method]

   ````java
       byte[] bytes = IOUtils.readFully(inputStream, -1, false);
       instrumentation.redefineClasses(new ClassDefinition(Server.class, bytes));
       ```
   ````

## javassist

1. javassist 是一个开源的[jboss]分析, 编辑和创建 java 字节码的类库

2. dependency

   ```xml
   <dependency>
       <groupId>org.javassist</groupId>
       <artifactId>javassist</artifactId>
       <version>3.27.0-GA</version>
   </dependency>
   ```

3. syntax

   |   parameter   |                        explain                         |
   | :-----------: | :----------------------------------------------------: |
   | `$0, $1, $2`  |      $0 表示 this[静态方法没有],$1 表示第一个参数      |
   |    `$args`    |  将参数以 Object 数组的形式封装`new Object[]{$1, $2}`  |
   | `$cflow(...)` |              方法在递归时刻读取其递归层次              |
   |     `$r`      | 用于封装方法结果 `Object result=..; return ($r)result` |
   |     `$w`      |    将基础类型转换为包装类型 `Integer i = ($w) 123;`    |
   |     `$_`      |     设置返回结果 `$_ s=($w) 1; ==> return ($w) 1;`     |
   |    `$sig`     |           获取方法中所有的参数类型[数组形式]           |
   |    `$type`    |                  获取方法结果的 class                  |
   |   `$class`    |               获取当前方法所在类的 class               |

4. 计算某个方法的耗时

   - 由于 javassist 添加的代码时动态代码块, 存在作用域问题
   - step:
     1. 复制原有方法
     2. 更改原有方法名字
     3. 更改被复制的方法的内容
   - demo

     ```java
     private static byte[] buildMonitorClass() throws Exception {
         ClassPool pool = new ClassPool();
         pool.appendSystemPath();
         CtClass ctClass = pool.get("cn.edu.ntu.javase.agent.Server");
         CtMethod sayHello = ctClass.getDeclaredMethod("sayHello");

         CtMethod sayHelloCopy = CtNewMethod.copy(sayHello, ctClass, new ClassMap());
         sayHello.setName("sayHello$agent");
         sayHelloCopy.setBody(
             "{\n"
                 + "    long start = System.nanoTime();\n"
                 + "    try {\n"
                 + "      return sayHello$agent($$);\n"
                 + "    } finally {\n"
                 + "      long end = System.nanoTime();\n"
                 + "      System.out.println(\"time spend: \" + (end - start));\n"
                 + "    }\n"
                 + "  }");

         ctClass.addMethod(sayHelloCopy);
         return ctClass.toBytecode();
     }
     ```

---

## expand

1. 监控系统采集信息的埋点

   - 硬编码
   - aop
   - 公共组件埋点
   - **字节码插桩埋点**

2. 方法采集只能是 pubic 的非 static, 非 abstract, 非 native
3. 采用**通配符**或正则表达式
   - \*: 表示一个或多个任意字符
   - ?:表示单个字符
   - &: 分割多个匹配语句

## 数据采集

1. service

   - 参数: args, spend time, result
   - step:
     1. 编写参数解析方法
     2. 编写监控起始方法
     3. 编写监控结束方法
     4. 基于 javassist 实现插桩

2. web

   - 参数: url, args, cookie, head, spend time, exception
   - 可用性: 项目无关, 架构无关, 容器无关
   - 埋点位置
     | location | 优点 | 缺点 |
     | :------------------------: | :------------------------: | :------------------------------------------------------------------------------------------------- |
     | controller | 简单, 风险因素低 | 判别成本高有局限性:[只能根据 HttpServlet 子类或@RequestMapping 进行判别] |
     | DispatchServlet.doDispatch | 简单, 适用性强 | 只能是 springmvc 项目[springboot 就不行] |
     | HttpServlet.service | 适用性强, 与应用和框架无关 | 不同容器的 class-path 不一样, 存下兼容性问题<br/> 风险高[所有方法都会到这里] <br/> 业务异常无法捕获 |

   - step:

     1. 添加 servlet-api 依赖
     2. 编写 buildWebMonitor()生成插桩节, `HttpServlet.service`
     3. 编写监控起始方法
     4. 编写监控结束方法
     5. 编写 WebTraceInfo 实体类, 用于寸法 http 数据
     6. 测试不同的容器: jetty/ tomcat

   - -javaagent:xx.jar 启动之后报错 HttpServletRequest not found:

     ![avatar](/static/image/java/javaee-tomcat-class-loafer..png)

     1. 是由于 `-javaagent:xx.jar` 是在 `AppClassLoader` 时加载的, servlet-api 是在 `CommonLoader`时加载
     2. solution
        - 将 `xx.jar`放在 `CommonLoader` 加载
        - 将 `servlet-api` 放入 `xx.jar`， 之后由 `AppClassLoader` 加载
        - 不直接调用 `HttpServletRequest` api, 直接使用反射

3. 整合 `Server/Service` + `web`

   - 需求:

     1. 将 Http 请求所发送的事件基于 `traceId` 进行关联
     2. 事件之间要有顺序和层次
     3. 多线程之间不影响采集结果

   - traceSession 机制

     ![avatar](/static/image/java/javaee-trace-session.png)
