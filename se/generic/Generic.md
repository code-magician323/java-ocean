## Generic

### 知识点

1. 不使用泛型原因:

   - `集合中的类型并不安全`, 可以向集合中放入任何引用类型的变量
   - 从集合中取出的对象都是 Object 类型, 在具体操作时可能需要进行类型的强制转换; 就有可能发生 `ClassCastException` 异常

2. 声明泛型: 在类中凡是可以使用类型的地方都可以使用泛型

3. 泛型方法: 在方法声明时, 同时声明泛型, 在方法的返回值, 参数列表以及方法体中都可以使用泛型

4. 定义泛型:

   - 定义泛型类: 和别人一样(standstart)
     > 声明(接口)时, 在类名的后面, 大括号的前面利用<>来声明泛型类, 在方法(不一定是泛型类)的返回值使用<E>E 来声明泛型类型, 则在方法的参数和方法体中都可以使用. public class Dao<T>
   - 定义泛型方法:
     > 在类(不一定是泛型类)中使用泛型方法
     > 在方法的返回值前面使用<E>E 声明泛型类型, 则是在方法的返回值、参数、方法体中都可以使用该类型. `public <E> E getProperty(Integer id)`

5. 泛型与子类继承:

   ```java
   // List<Object> objlist 不是 List<String> strlist的父类
   List<Object> objlist = strlist; //ERROR:

   public class List_General {
       public static void main(String[] args) {
           List<String> strlist=Arrays.asList("AA","BB");
           System.out.println(strlist);
           // List<Object> objlist=strlist;// ERROR: List<Object> objlist不是List<String> strlist的父类

           //test List<PersonZ>
           List<PersonZ>pers=new ArrayList<>();
           pers.add(new PersonZ(20, "zhang"));
           pers.add(new PersonZ(22, "lug"));
           pers.add(new PersonZ(40, "1465"));
           printPersonZInfo(pers);
           printPersonZInfo2(pers);

           //test List<Student>
           List<Student>stus=new ArrayList<>();
           stus.add(new Student(20, "zhang", "hazgu"));
           stus.add(new Student(22, "lug", "sefrg"));
           stus.add(new Student(40, "1465", "dwefgr"));
           //printPersonZInfo(stus);  //ERROR
           printPersonZInfo2(stus);
       }

       public static void printPersonZInfo(List<PersonZ>PersonZs){
           for(PersonZ PersonZ:PersonZs){
               System.out.println(PersonZ);
           }
       }

       public  static void printPersonZInfo2(List<? extends PersonZ>PersonZs) {
           for(PersonZ PersonZ:PersonZs){
               System.out.println(PersonZ);
           }
       }
   }
   ```

6. 通配符: (这里讲的是面向过程, 把对象当做参数传入进来进行操作)
   - 如果 Student 是 Person 的一个子类(子类或接口), 而 G 是某种泛型声明如: List<Person>, 那么`G<Student>是G<Person>的子类对型并不成立`:
   - **`Student 是 Person 的子类, 但是 ListStudent> 并不是 List<Person> 的子类`**
   - 如: `PrintInfoPersons(List<Person>persons)` 该方法的参数只能是 Person 类型的 List; 而不能是 Person 任何子类对型: 如 PrintInfo(List<Student>stus)会编译失败:
     > 这里问题的解决: `printPersonInfo2(List<? extends Person>Persons)` // 待上限的通配符, 该类型可以只想 Person 类型和 Person 的子类类型的集合; 同样不能向其中放入除 null 意外的任何类型元素
     > printCollection(Collection<?>collection)// 在這個方法中, 传入任何数据都是错误的, 除了 null

### create generic object

1. generic array

   ```java
   // T[] array = new T[]; // compile error
   public static <T> T[] getArray(Class<T> componentType, int length)
   {
       return (T[]) Array.newInstance(componentType, length);
   }
   ```

2. generic list

   ```java
   public static <T> List<T> getList(Class<T> componentType) {
       Field[] declaredFields = componentType.getDeclaredFields();
       Arrays.stream(declaredFields).forEach(System.out::println);
       List<T> list = new ArrayList<>();
       T t = componentType.newInstance();
       list.add(t);

       return list;
   }
   ```

3. object

   ```java
   public class GenericsArrayT<T> {
       private Object[] array;

       public GenericsArrayT(int size) {
           array = new Object[size];
       }

       public void put(int index, T item) {
           array[index] = item;
       }

       public T get(int index) {
           return (T) array[index];
       }

       public T[] rap() {
           return (T[]) array;
       }
   }
   ```

### demo

- 泛型

  ```java
  import java.util.Collection;

  public class General<T> {
      /**
      * 方法的返回值可以使用前面声明的泛型类型
      */
      public T get(Integer id) {
          T result = null;
          return result;
      }

      /**
      * 方法参数也可以使用声明类时声明的泛型类型
      */
      public void save(T entity) {

      }

      /**
      * 泛型方法1
      *  1.这里的<T>声明只是为了后面的使用
      */
      public static <T> void fromArrayToCollection(T[] a, Collection<T> c){
          for (T o : a) {
            c.add(o); // correct
          }
      }

      /**
      * 泛型方法2
      * 在方法(不一定是泛型类)的返回值使用<E>E来声明泛型类型, 则在方法的参数和方法体中都可以使用
      */
      public <E> E getProperty(Integer id) {
          E e=null;
          return e;  //从这句话确定的E类型：因为要赋值给int型, 所以这里的return返回类型要是int, 所以E为int型
      }

      /**
      * 功能：这里解释上面的E类型的确定
      * 测试代码：pers.test("");  //此时的E为String类型
      *          pers.test(new Person("123",12));	//此时的E为Person类型
      */

      public <E> void test(E entries){
      }
  }

  public class TestGeneral {
    public static void main(String[] args) {
        General<Person>pers=new General<>();
        Person person=pers.get(2);
        int age=person.getAge();

        int age1=pers.getProperty(1); // public <E> E getProperty(Integer id)
        String name=pers.getProperty(1);
    }
  }
  ```

- 测试 List 父类问题

  ```java
  import java.util.ArrayList;
  import java.util.Arrays;
  import java.util.Collection;
  import java.util.List;

  /**
  *  1. List<Object> objlist不是List<String> strlist的父类
  *  //这里是面向过程编程, 不是面向对象编程
  *  2. public  static void printPersonZInfo2(List<? extends PersonZ>PersonZs) {
  *           for(PersonZ PersonZ:PersonZs){
  *               System.out.println(PersonZ);
  *           }
  *     }
  *  // 这里要是转换成面向对象：就等价于子类重写父类的方法, 并且可以通过父类.方法名来调用
  */
  public class List_General {
      public static void main(String[] args) {
          List<String> strlist=Arrays.asList("AA","BB");
          System.out.println(strlist);
          // List<Object> objlist=strlist; //ERROR: List<Object> objlist不是List<String> strlist的父类

          //test List<PersonZ>
          List<PersonZ>pers=new ArrayList<>();
          pers.add(new PersonZ(20,"zhang"));
          pers.add(new PersonZ(22,"lug"));
          pers.add(new PersonZ(40,"1465"));
          printPersonZInfo(pers);

          //test List<Student>
          List<Student>stus=new ArrayList<>();
          stus.add(new Student(20,"zhang","hazgu"));
          stus.add(new Student(22,"lug","sefrg"));
          stus.add(new Student(40,"1465","dwefgr"));
          //printPersonZInfo(stus);  //ERROR
          printPersonZInfo2(stus);

      }

      public  static void printPersonZInfo(List<PersonZ>PersonZs) {
          for(PersonZ PersonZ:PersonZs){
              System.out.println(PersonZ);
          }
      }
      /**
      *List<? extends PersonZ>PersonZs：存在通配符時, 寫入是非法的
      * @param PersonZs
      */
      public  static void printPersonZInfo2(List<? extends PersonZ>PersonZs) {
      //  PersonZs.add(new Student(12, "s", "23"));//  error
          for(PersonZ PersonZ:PersonZs){
              System.out.println(PersonZ);
          }
      }
      /**
      * 在這個方法中, 传入任何数据都是错误的, 除了null
      * @param collection
      */
      public void printCollection(Collection<?>collection) {
          collection.add(null);//在這個方法中, 传入任何数据都是错误的, 除了null

      }
  }

  class Student extends PersonZ{
      private String school;
      public Student(int age,String name,String school) {
          super(age,name);
          this.school = school;
      }
      public String getSchool() {
          return school;
      }
      public void setSchool(String school) {
          this.school = school;
      }
      @Override
      public String toString() {
          return "Student	 [age=" + super.getAge() + ", nanme=" + super.getNanme() + " [school=" + school + "]";
      }
  }

  class PersonZ{
      private int age;
      private String nanme;
      public int getAge() {
          return age;
      }
      public void setAge(int age) {
          this.age = age;
      }
      public String getNanme() {
          return nanme;
      }
      public void setNanme(String nanme) {
          this.nanme = nanme;
      }
      @Override
      public int hashCode() {
          final int prime = 31;
          int result = 1;
          result = prime * result + age;
          result = prime * result + ((nanme == null) ? 0 : nanme.hashCode());
          return result;
      }
      @Override
      public boolean equals(Object obj) {
          if (this == obj)
              return true;
          if (obj == null)
              return false;
          if (getClass() != obj.getClass())
              return false;
          PersonZ other = (PersonZ) obj;
          if (age != other.age)
              return false;
          if (nanme == null) {
              if (other.nanme != null)
                  return false;
          } else if (!nanme.equals(other.nanme))
              return false;
          return true;
      }
      public PersonZ() {
          // TODO Auto-generated constructor stub
      }
      public PersonZ(int age, String nanme) {
          this.age = age;
          this.nanme = nanme;
      }
      @Override
      public String toString() {
          return "PersonZ [age=" + age + ", nanme=" + nanme + "]";
      }
  }
  ```

---

## [demo-code](https://github.com/Alice52/DemoCode/tree/master/java/javase/java-Generic)

## Question1: 如何在泛型中创建对象
