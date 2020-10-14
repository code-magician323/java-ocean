1. static load sequence

    - code 
    
    ```java
    public class StaticTest {
        public static void main(String[] args) {
            staticFunction();
        }

        // 静态变量[有实例化的过程,这就是本题的重点]
        static StaticTest st = new StaticTest();
        static {
            // System.out.println(b); // 编译报错
            System.out.println(st.b); // 0
            System.out.println("1");
        }

        {
            System.out.println(b); // 0
            System.out.println(a); // 编译报错, 如果 a 定义再前面既不会报错
            System.out.println("2");
        }

        // 执行构造函数之前, 必须初始化实例属性
        StaticTest() {
            System.out.println("3");
            System.out.println("a=" + a + ",b=" + b);
        }

        public static void staticFunction() {
            System.out.println("4");
        }

        int a = 110;
        static int b = 112;
    }

    0
    2
    3
    a=110,b=0
    0
    1
    4
    ```

    - explain
        1. 静态变量会再类加载时被加载到内存中, 且赋值初始值, 非静态变量不会
        2. 动态代码块会在 `构造函数` 之前执行, 和赋值语句代码顺序执行
        3. static 相关的会在load 进内存时执行, 之后 new 的逻辑不会触发
        4. 静态变量从上到下初始化
        5. 顺序: 静态变量, 静态代码块 是顺序执行的; 但是父子类是[静态成员变量, 静态代码块]
        6. 执行顺序:
           - 一个类中: 静态变量, 静态代码块是顺序执行的[代码顺序]; 非静态变量赋值, 动态代码块是顺序执行的
           - 父子类中: 所有的静态变量先执行, 静态代码块执行, 动态代码块, 成员变量, 构造函数
