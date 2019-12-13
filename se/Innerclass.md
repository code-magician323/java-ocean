## java 内部类

1. 在类的内部定义类称为内部类
2. `内部类相当于类的一个成员`, 可以修饰成员的 `public static final abstract` 都可以修饰内部类
3. 声明和使用内部类的实例:

   - 非静态成员内部类的创建: 先创建外部类的实例, 再通过 "外部类名.new" 创建内部类的实例
     ```java
     OuterClass oc = new OuterClass();
     OuterClass.InnerClass in = oc.new InnerClass();
     ```
   - 静态成员内部类的创建: 不再需要外部内的实例
     ```java
     OuterClass.StaticInnerClass sic = new OuterClass.StaticInnerClass();
     ```

4. 内部类引用外部类的成员:
   ```java
   System.out.println(OuterClass.this.a);
   ```
5. 匿名内部类对象: `使用某一接口通常是先创建接口的实现类, 在创建其实现类的对象`; 还可以`直接创建其实现类对象`

   ```java
   InvocationHandler invocationHandler=new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
   };
   ```

## demo

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
