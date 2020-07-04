## Serialize

// TODO: the diffs between serialVersionUID?

1. 实现没有任何内容的 `java.io.Serializable` (标记)接口: 或 `Externalizable` 接口
2. 需要添加类的版本号: 用于对象的序列化, 具体读取对象时比对硬盘上的对象版本号和程序中对象版本号是否一致, 若不一致则失败并抛出异常
3. 当试图对一个对象进行序列化的时候, 如果遇到不支持 Serializable 接口的对象. 在此情况下, 将抛出 `NotSerializableException`

4. 反序列化

   - 创建一个 `ObjectInputStream`
   - 调用 `readObject()` 方法读取六种的对象
   - 如果某个类的字段不是基本数据类型或 String 类型, 而**是另一个引用类型, 那么这个`引用类型(类)必须是可序列化`的, 否则拥有该类型的 field 字段 的类也不能序列化**

5. Serializable 与 Externalizable 的区别

   - Externalizable 继承了 Serializable
   - 当使用 Externalizable 接口来进行序列化与反序列化的时候需要开发人员重写 `writeExternal()` 与 `readExternal()` 方法
   - 实现 Externalizable 接口的类必须要提供一个 public 的无参的构造器: 在使用 Externalizable 进行序列化的时候, 在读取对象时, 会调用被序列化类的无参构造器去创建一个新的对象, 然后再将被保存对象的字段的值分别填充到新对象中.

6. Transient 关键字

   > Transient 关键字可以阻止该变量被序列化到文件中; 在被反序列化后, transient 变量的值被设为初始值

7. 序列化 ID[serialVersionUID]: 反序列化时

   - 虚拟机是否允许反序列化, 不仅取决于类路径和功能代码是否一致, 一个非常重要的一点是两个类的序列化 ID 是否一致
   - 两种生成策略:
     - 一个是固定的 1L(默认用这个)
     - 一个是随机生成一个不重复的 long 类型数据（实际上是使用 JDK 工具生成) [根据包名,类名,变量名, 方法名等产生]

8. 小结:

   - 对象序列化: 使用 `OutputStream writeObject` 写入本地文件;
   - 对象反序列化: 使用 `InputStream readObject` 读取
   - 作用: 对象序列化的目标是将对象保存到磁盘上, 或允许在网络中直接传输对象.
   - 序列化机制是 JavaEE 平台的基础
   - 实现可序列化: 两个接口之一
     > 1. Serializable
     > 2. Externalizable
   - 序列化文件本身就是乱码
   - Transient
   - ID 生成

## demo

```java
import java.io.Serializable;

public class User1 implements Serializable {
    private String name;
    // age will not be serialize, and it's real value.
    private transient int age;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class SerializableTest {
    public static void main(String[] args) {
        // Initializes The Object
        User1 user = new User1();
        user.setName("hollis");
        user.setAge(23);
        System.out.println(user);

        //Write Obj to File
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("tempFile"));
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(oos);
        }

        //Read Obj from File
        File file = new File("tempFile");
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            User1 newUser = (User1) ois.readObject();
            System.out.println(newUser);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(ois);
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//OutPut:
//User{name='hollis', age=23}
//User{name='hollis', age=23}
```
