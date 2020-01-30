## Java Inner Class

1. 在类的内部定义类称为内部类
2. `内部类相当于类的一个成员`, 可以修饰成员的 `public static final abstract` 都可以修饰内部类

### 1. Common Inner Class:

1. introduce

- 1. exist depending on outer class objects: if want to create inner class, we should create outer class.
- 2. inner class have access to all member of outer class.
- 3. outer class have access to all member of inner class.
- 4. common inner class do not have static member.

2. syntax

   ```java
   public class InnerClassTest {
       public class InnerClassA {
       }
   }
   // usage
   InnerClassTest innerClassTest = new InnerClassTest();
   // TODO: what is the different between the two class?
   CommonInner commonInner = innerClassTest.new CommonInner();
   CommonInner commonInner2 = new CommonInner();
   ```

### 2. Static Inner Class

1. introduce:

- 1. exist not depend on outer class.
- 2. static inner class have no access to outer class, in addition to static member.
- 3. outer class have access to all member of static inner class.
- 4. static inner class can also used to new instance.
- 5. static inner class can define un-static and static member.

2. syntax

   ```java
   public class InnerClassTest {
       static class InnerClassA {
       }
   }

   // usage
   // There are no difference between the two class.
   StaticInner staticInner = new InnerClassTest.StaticInner();
   StaticInner staticInner2 = new StaticInner();
   ```

### 3. Anonymous Inner Class

1. introduce

- 1. anonymous in known as implements interface as parameter.
- 2. anonymous inner class have all access to outer class.
- 3. outer class have a no access to anonymous inner class:
  - anonymous inner class can define property，and can only used in local,
  - and cannot used in outer class due to no class name.
- 4. even cannot create or get anonymous instance.
- 5. anonymous just define implements and it will not execute besides call interface method.
- 6. create anonymous for each interface method call.

2. syntax

   ```java
   interface AnonymousInterface {
      void accept(String tag, Consumer consumer);
   }

   AnonymousInterface anonymousInterface = new AnonymousInterface() {
       // can define property in anonymous class，and can only used in local.
       int field = 1;

       @Override
       public void accept(String tag, Consumer consumer) {
           consumer.accept(tag);
           LOG.info("Outer class field2: " + outerFiled);
       }
   };

   // create anonymous for each interface method call.
   anonymousInterface.accept("obj1", (Object a) -> LOG.info(a.toString()));
   ```

### 4. Local Inner Class

1. introduce

- 1. local inner class have all access to outer class
- 2. outer class have no access to local inner class.

2. syntax

   ```java
   public class InnerClassTest {
      public void anonymousClassTest() {
          class LocalInner {
          }
      }
   }
   ```

### 5. Nesting Inner Class

1. common inner class: cannot define static inner class
2. static inner class: all type is fine
3. anonymous inner class: cannot define static inner class
4. local inner class: cannot define static inner class

### 6. Deep Understanding Inner Class

1. test sample

```java
public class InnerClassTest {

    int field1 = 1;
    private int field2 = 2;

    public InnerClassTest() {
        InnerClassA inner = new InnerClassA();
        int v = inner.x2;
    }

    public class InnerClassA {
        int x1 = field1;
        private int x2 = field2;
    }
}
```

2. run `javac InnerClassTest.java` command in cmd, then can get two file: `InnerClassTest.class`, `InnerClassTest$InnerClassA.class`

3. run `javap -c InnerClassTest` to decompile file, can get follow

   ```java
   Compiled from "InnerClassTest.java"
   public class InnerClassTest {
       int field1;

       public InnerClassTest();
           Code:
           0: aload_0
           1: invokespecial #2                  // Method java/lang/Object."<init>":()V
           4: aload_0
           5: iconst_1
           6: putfield      #3                  // Field field1:I
           9: aload_0
           10: iconst_2
           11: putfield      #1                  // Field field2:I
           14: new           #4                  // class InnerClassTest$InnerClassA
           17: dup
           18: aload_0
           19: invokespecial #5                  // Method InnerClassTest$InnerClassA."<init>":(LInnerClassTest;)V
           22: astore_1
           23: aload_1
           24: invokestatic  #6                  // Method InnerClassTest$InnerClassA.access$000:(LInnerClassTest$InnerClassA;)I
           27: istore_2
           28: return

       // access$100 method accept InnerClassTest as parameter to get InnerClassTest field
       // it will be called by inner class
       // expose own property
       static int access$100(InnerClassTest);
           Code:
           0: aload_0
           1: getfield      #1                  // Field field2:I
           4: ireturn
   }
   ```

- `getfield` make sense for InnerClassTest to get outer class field
- `24: invokestatic`: it calls inner class `access$000` method to get inner class field

4. run `javap -c InnerClassTest$InnerClassA.class` to decompile file, can get follow content

   ```java
   Compiled from "InnerClassTest.java"
   public class InnerClassTest$InnerClassA {
       int x1;
       // common inner class will have reference of outer class.
       final InnerClassTest this$0;

       // constructor will accept a outer class paramter
       public InnerClassTest$InnerClassA(InnerClassTest);
           Code:
           0: aload_0
           1: aload_1
           2: putfield      #2                  // Field this$0:LInnerClassTest;
           5: aload_0
           6: invokespecial #3                  // Method java/lang/Object."<init>":()V
           9: aload_0
           10: aload_0
           11: getfield      #2                  // Field this$0:LInnerClassTest;
           14: getfield      #4                  // Field InnerClassTest.field1:I
           17: putfield      #5                  // Field x1:I
           20: aload_0
           21: aload_0
           22: getfield      #2                  // Field this$0:LInnerClassTest;
           25: invokestatic  #6                  // Method InnerClassTest.access$100:(LInnerClassTest;)I
           28: putfield      #1                  // Field x2:I
           31: return

       // expose own property
       static int access$000(InnerClassTest$InnerClassA);
           Code:
           0: aload_0
           1: getfield      #1                  // Field x2:I
           4: ireturn
   }
   ```

- `getfield` in `access$000` method make sense for `InnerClassTest$InnerClassA` to get inner class field
- `25: invokestatic`: it is call outer class `access$000` method to get outer class field

5. static inner class content

   ```java
   Compiled from "InnerClassTest.java"
   public class InnerClassTest$InnerClassA {
       // no call outer class access method, so it has no access to outer class property
       public InnerClassTest$InnerClassA();
           Code:
           0: aload_0
           1: invokespecial #2                  // Method java/lang/Object."<init>":()V
           4: aload_0
           5: iconst_0
           6: putfield      #1                  // Field x2:I
           9: return

       // this makes sense to expose inner class property
       static int access$000(InnerClassTest$InnerClassA);
           Code:
           0: aload_0
           1: getfield      #1                  // Field x2:I
           4: ireturn
   }
   ```

### 7. Inner Class and Multiple Inheritance

1. not allow multiple inheritance
2. use class D to generate A, B, C: I think it more like factory rather than multiple inheritance

   ```java
   class A {}
   class B {}
   class C {}

   public class D extends A {
       class InnerClassB extends B {}
       class InnerClassC extends C {}

       public B makeB() {
           return new InnerClassB();
       }

       public C makeC() {
           return new InnerClassC();
       }

       public static void testA(A a) {}

       public static void testB(B b) {}

       public static void testC(C c) {}

       public static void main(String[] args) {
           D d = new D();
           testA(d);
           testB(d.makeB());
           testC(d.makeC());
       }
   }
   ```

3. feature
   - broken class structure
   - Unless there is a very clear dependency between the two classes (such as a certain car and its special model of wheels), or one class exists to assist another class (such as the HashMap and its internal HashIterator class used to traverse its elements), then using the inner class at this time will have a better code structure and implementation effect.
   - In other cases, separate classes will have better code readability and code maintainability.

### 8. Internal Class and Memory Leaks

1. Memory leak: there are some objects that can be recycled but not recycled for some reason

2. how to avoid memory leak

   - Use static inner classes whenever possible
   - For some custom class objects, use the static keyword with caution

3. code

   ```java
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import java.util.concurrent.TimeUnit;

   /**
   * function: this class is created for test memory leak.<br>
   *      1. MyComponent, OnClickListener, MyWindow will be not recycled<br>
   *      2. due to OnClickListener is static, so it will not be recycled;<br>
   *           and OnClickListener has a final member to point to MyComponent outer class,
   *           so MyComponent will not also be recycled;<br>
   *       and MyComponent has MyWindow member, so MyWindow will also not recycled
   * // TODO: why not recycle memory? for static or lambda
   */
   public class MemoryLeakTest {
       private static final Logger LOG = LoggerFactory.getLogger(MemoryLeakTest.class);

       abstract static class Component {

           final void create() {
           onCreate();
           }

           final void destroy() {
           onDestroy();
           }

           /** This is for subClass overwrite. */
           abstract void onCreate();

           /** This is for subClass overwrite. */
           abstract void onDestroy();
       }

       static class MyComponent extends Component {
           static OnClickListener clickListener;
           MyWindow myWindow;

           @Override
           void onCreate() {
               clickListener = obj -> LOG.info("Object " + obj + " onclick.");
               myWindow = new MyWindow();
               myWindow.setClickListener(clickListener);
           }

           @Override
           void onDestroy() {
               myWindow.removeClickListener();
           }
       }

       static class MyWindow {
           OnClickListener clickListener;

           void setClickListener(OnClickListener clickListener) {
               this.clickListener = clickListener;
           }

           void removeClickListener() {
               this.clickListener = null;
           }
       }

       public interface OnClickListener {
           void onClick(Object obj);
       }

       public static void main(String[] args) throws InterruptedException {
           MyComponent myComponent = new MyComponent();
           myComponent.create();
           myComponent.myWindow.clickListener.onClick(new Object());
           myComponent.destroy();
           // this operation will donot recycled memory
           myComponent = null;
           System.gc();
           System.out.println("");
           TimeUnit.HOURS.sleep(5);
       }
   }
   ```

---

## sample

1. refenrence

2. code

```java
package basical;

public class OuterClass {
    private int a = 1;

    // class 内部类可以使用修饰符修饰：Inner class可以声明为 private 或 protected

    /**
     * 说明：非静态内部类
     */
    public class InnerClass {
        private int a;
        public void method() {
            int a = 3;
            // 访问方法内部变量
            System.out.println(a);
            // 访问内部类的成员变量
            System.out.println(this.a);
            // 访问外部内的成员变量
            System.out.println(OuterClass.this.a);
            System.out.println("HelloWorld!I am InnerClass!");
        }
        public int getB() {
            return a;
        }
        public void setB(int a) {
            this.a = a;
        }
    }

    /**
     * 说明：静态内部类
     */
    public static class InnerClass2 {
        private int a = 2; // 与外部内的成员变量同名
        public void method() {
            int a = 3;
            // 访问方法内部变量
            System.out.println(a);
            // 访问内部类的成员变量
            System.out.println(this.a);
            // 访问外部内的成员变量
            /* System.out.println(OuterClass.this.a); */

            System.out.println("HelloWorld!I am static InnerClass!");
        }
        public int getB() {
            return a;
        }
        public void setB(int a) {
            this.a = a;
        }
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}

public class InnerClassTest {
    public static main(String []args) {
        OuterClass oc = new OuterClass();
        //  静态内部类
        InnerClass ic = oc.new InnerClass();
        ic.method();//方法调用
        // 静态内部类: 可以直接创建; 静态内部类不可以使用非静态成员
        InnerClass2 ic2 = new InnerClass2();
        ic2.method();//方法调用

        // 內部類舉例：在类的内部直接创建一个Comparable接口的实现类对象[匿名内部类]
        Comparable<Integer> comparable = new Comparable<Integer>() {
            @Override
            public int compareTo(Integer o) {
                // TODO Auto-generated method stub
                return 0;
            }
        };
    }
}
```

---

## conlusion

1. inner class type: `-> means to get`

|   type    | o -> i | i -> o | create instance | nest static inner class |
| :-------: | :----: | :----: | :-------------: | :---------------------: |
|  common   |   Y    |   Y    |        Y        |            N            |
|  static   |   Y    |   N    |        Y        |            Y            |
| anonymous |   N    |   Y    |        N        |            N            |
|   local   |   N    |   Y    |  just in scope  |            N            |

2. understand about inner class:

   - 在外部类访问非静态内部类私有成员的时候, 会持有一个指向外部类引用的成员变量, 对应的内部类会生成一个静态方法, 用来返回对应私有成员的值,而外部类对象通过调用其内部类提供的静态方法来获取对应的私有成员的值.
   - 在非静态内部类访问外部类私有成员的时候, 对应的外部类会生成一个静态方法, 用来返回对应私有成员的值, 而内部类对象通过调用其外部类提供的静态方法来获取对应的私有成员的值.

   - 静态内部类访问外部类的成员时, 对应的外部类会生成一个静态方法, 用来返回对应私有成员的值, 而内部类对象不没有调用其外部类提供的静态方法来获取对应的成员的值.

---

## Reference

1. https://blog.csdn.net/hacker_zhidian/article/details/82193100
