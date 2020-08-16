## Enumberation

### feature:

- 枚举类: 一个类的对象是 `有限且固定的`: `个数有限[constructor私有化]`, `属性固定[private final]`
- 在类的内部创建对象, 但需在类的外部能够访问对象且不能修改, 所以 `public static final` 修饰
- will extands `java.lang.Enum`

### Enum Prop:

1. values() / getEnumConstants()

   ```java
   /**
   * 1. 枚举类遍历
   * 2. Enumeration对象, 可以使用迭代器遍历;
   *   //对Enumeration对象进行遍历, hasMoreElements()  netElement()
   */
   System.out.println("第一种通过枚举静态方法 values()");
   for (UserStatus userStatus :UserStatus.values()){
       System.out.println(userStatus);
   }

   System.out.println("第二种通过反射");
   Class<UserStatus> claz = UserStatus.class;
   for(UserStatus userStatus : claz.getEnumConstants()){
       System.out.println(userStatus);
   }
   ```

2. name

   ```java
   String name = userStatus.name();
   UserStatus userStatus1 = UserStatus.valueOf(name);
   ```

3. ordrial

   ```java
   int ordinal = userStatus.ordinal();
   UserStatus userStatus1 = UserStatus.values()[ordinal];
   ```

4. valueOf()

   ```java
   // 将给定的字符串装换为枚举类型
   Enum.valueOf(SPECIFY_ENUM.class, enumName)
   SPECIFY_ENUM.valueOf(enumName)
   ```

### Singleton

1. 枚举值具有单例性, 及枚举中的每个值都是一个单例对象, 可以直接使用 == 进行等值判断.
2. 枚举是定义单例对象最简单的方法

### Notice:

- Enumeration 接口是 Iterator 迭代器的 "古老版本"
- 对 Enumeration 对象进行遍历: hasMoreElements() nextElement()

### Nature of Enum

1. enum source code

   ```java
   /**
   * This is the common base class of all Java language enumeration types.
   */
   public abstract class Enum<E extends Enum<E>>
           implements Comparable<E>, Serializable {

       // The name of this enum constant, as declared in the enum declaration.
       private final String name;
       public final String name() {
           return name;
       }

       // The ordinal of this enumeration constant (its position
       // in the enum declaration, where the initial constant is assigned
       // an ordinal of zero).
       private final int ordinal;
       public final int ordinal() {
           return ordinal;
       }

       // Sole constructor.  Programmers cannot invoke this constructor.
       // Used by compiler.
       protected Enum(String name, int ordinal) {
           this.name = name;
           this.ordinal = ordinal;
       }

       // Returns the name of this enum constant, as contained in the declaration.
       // This method may be overridden, though it typically isn't necessary or desirable.
       // An enum type should override this method when a more "programmer-friendly" string form exists.
       public String toString() {
           return name;
       }

       // Returns true if the specified object is equal to this enum constant.
       public final boolean equals(Object other) {
           return this == other;
       }

       // Returns a hash code for this enum constant.
       public final int hashCode() {
           return super.hashCode();
       }

       // Throws CloneNotSupportedException.
       // This guarantees that enums are never cloned,
       // which is necessary to preserve their "singleton" status.
       protected final Object clone() throws CloneNotSupportedException {
           throw new CloneNotSupportedException();
       }

       // Compares this enum with the specified object for order.
       // Returns a negative integer, zero,
       //    or a positive integer as this object is less than, equal to,
       //    or greater than the specified object.
       //
       // Enum constants are only comparable to other enum constants of the same enum type.
       // The natural order implemented by this method is the order in which the constants are declared.
       public final int compareTo(E o) {
           Enum<?> other = (Enum<?>)o;
           Enum<E> self = this;
           if (self.getClass() != other.getClass() && // optimization
               self.getDeclaringClass() != other.getDeclaringClass())
               throw new ClassCastException();
           return self.ordinal - other.ordinal;
       }

       // Returns the Class object corresponding to this enum constant's
       // enum type.  Two enum constants e1 and  e2 are of the
       // same enum type if and only if
       //   e1.getDeclaringClass() == e2.getDeclaringClass().
       @SuppressWarnings("unchecked")
       public final Class<E> getDeclaringClass() {
           Class<?> clazz = getClass();
           Class<?> zuper = clazz.getSuperclass();
           return (zuper == Enum.class) ? (Class<E>)clazz : (Class<E>)zuper;
       }

       // Returns the enum constant of the specified enum type with the
       // specified name.  The name must match exactly an identifier used
       // to declare an enum constant in this type.
       public static <T extends Enum<T>> T valueOf(Class<T> enumType,
                                                   String name) {
           T result = enumType.enumConstantDirectory().get(name);
           if (result != null)
               return result;
           if (name == null)
               throw new NullPointerException("Name is null");
           throw new IllegalArgumentException(
               "No enum constant " + enumType.getCanonicalName() + "." + name);
       }

       // enum classes cannot have finalize methods.
       protected final void finalize() { }

       // prevent default deserialization
       private void readObject(ObjectInputStream in) throws IOException,
           ClassNotFoundException {
           throw new InvalidObjectException("can't deserialize enum");
       }

       private void readObjectNoData() throws ObjectStreamException {
           throw new InvalidObjectException("can't deserialize enum");
       }
   }
   ```

2. conlusion

   - enum prop is final, and init in Sole constructor
   - overwrite equals, hashCode, toString: return name
   - implements Comparable, overwrite compareTo and use declared ordinal to compare
   - implements Serializable, overwrite clone, readObject to guarantee singleton
   - valueOf is use reflect to get enum constants by name
   - **`no values() method and enum constants enum named as declared`**

3. each enum constants
   - add public static final prop for each enum instance, name is same as declared,
   - add private static final \$VALUES to record enums
   - add public static values method to get all enums
   - add public static valueOf method to get enum by name

### Enum Collection

- the java provides support for the collection of enums(mainly optimized around the ordinal feature).

1. EnumSet

   ```java
   public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E>
   implements Cloneable, java.io.Serializable{}
   ```

   - The EnumSet introduced from Java5, which uses long values as bit vectors internally to maximize the performance of the Set
   - implements:
     - RegularEnumSet: enum number is less than 64
     - JumboEnumSet: enum number is less than 64
   - method:
     - copyOf: create EnumSet use provided Set
     - noneOf: null Set
     - allOf: use provided Set to create EnumSet
     - of: use enum to create EnumSet
     - range: according to ordinal to define ranged EnumSet

2. EnumMap

   ```java
   private transient Object[] vals;

   public V get(Object key) {
       return (isValidKey(key) ?
               unmaskNull(vals[((Enum<?>)key).ordinal()]) : null);
   }

   public V put(K key, V value) {
       typeCheck(key);
       int index = key.ordinal();
       Object oldValue = vals[index];
       vals[index] = maskNull(value);
       if (oldValue == null)
           size++;
       return unmaskNull(oldValue);
   }
   ```

   - EnumMap is special Map, and it's key should be enum object.
   - EnumMap implements by array[ordinal] internally, to improve operation speed

### Usage

1. Switch

   - case expression donot have guarantee data value in ranged.

   ```java
   // convert data to enum
   // then use it in switch
   FruitEnum fruit;
   switch (fruit) {
     case APPLE:
       return "apple";
     case BANANA:
       return "banana";
     case PEAR:
       return "pear";
     default:
       return "unknown";
   }
   ```

2. Singleton

   - syntax

   ```java
   public enum Singleton{
       INSTANCE;

       public void doSomething(){
       }
   }
   ```

   - feature
     - thread safe
     - no recommend
     - it takes more than twice memory compared to the static constant usage

3. **State Machine**

   - State machines are an effective way to solve business processes,
     and enumerated singletons provide convenience for building state machines.
   - The state of the order process, and the states involved include `Created, Canceled, Confirmed, Overtime and Paied`;
     the actions involved include `cancel, confirm, timeout, and pay`.

   ![avatar](/static/image/java/enum.png)

   - sample

   ```java
   package cn.edu.ntu.javase.usage;

   import org.junit.Test;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   /**
   * @author zack <br>
   * @create 2020-01-31 19:03 <br>
   */
   public class StateMachineUsageTest {
     private static final Logger LOG = LoggerFactory.getLogger(StateMachineUsageTest.class);

     @Test
     public void testOrderState() {
       Order order = new Order();
       LOG.info(order.getState().name());

       order.confirm();
       LOG.info(order.getState().name());

       order.timeout();
       LOG.info(order.getState().name());

       Order order2 = new Order();
       LOG.info(order2.getState().name());

       order2.confirm();
       LOG.info(order2.getState().name());

       order2.pay();
       LOG.info(order2.getState().name());

       order2.cancel();
       LOG.info(order2.getState().name());
     }
   }

   interface IOrderState {
     /** @param context */
     void cancel(OrderStateContext context);

     /** @param context */
     void confirm(OrderStateContext context);

     /** @param context */
     void timeout(OrderStateContext context);

     /** @param context */
     void pay(OrderStateContext context);
   }

   /** implements state machine based on enum */
   enum OrderState implements IOrderState {
     /** CREATED state when order is created */
     CREATED {
       @Override
       public void cancel(OrderStateContext context) {
         context.setState(CANCELED);
       }

       /** allow confirm operation and set state to CONFIRMED */
       @Override
       public void confirm(OrderStateContext context) {
         context.setState(CONFIRMED);
       }
     },
     /** CONFIRMED state when order is confirm */
     CONFIRMED {

       /** allow cancel operation and set state to CANCELED */
       @Override
       public void cancel(OrderStateContext context) {
         context.setState(CANCELED);
       }

       /** allow timeout operation and set state to OVERTIME */
       @Override
       public void timeout(OrderStateContext context) {
         context.setState(OVERTIME);
       }

       /** allow pay operation and set state to PAYED */
       @Override
       public void pay(OrderStateContext context) {
         context.setState(PAYED);
       }
     },
     /** Canceled is freeze state when order is cancel() */
     CANCELED {},

     /** Canceled is freeze state when order is overtime() */
     OVERTIME {},

     /** CREATED state when order is pay() */
     PAYED {
       @Override
       public void cancel(OrderStateContext context) {
         context.setState(CANCELED);
       }
     };

     @Override
     public void cancel(OrderStateContext context) {
       throw new UnsupportedOperationException();
     }

     @Override
     public void confirm(OrderStateContext context) {
       throw new UnsupportedOperationException();
     }

     @Override
     public void timeout(OrderStateContext context) {
       throw new UnsupportedOperationException();
     }

     @Override
     public void pay(OrderStateContext context) {
       throw new UnsupportedOperationException();
     }
   }

   /** State Context: for change order state */
   interface OrderStateContext {
     /**
     * this method is to change order state.
     *
     * @param state
     */
     void setState(OrderState state);
   }

   /** order implements */
   class Order {
     private OrderState state;

     /** this is subClass and use it to change order state */
     private StateContext stateContext = new StateContext();

     public Order() {
       this.state = OrderState.CREATED;
     }

     public OrderState getState() {
       return state;
     }

     private void setState(OrderState state) {
       this.state = state;
     }

     /** transfer request operation to enum state to handle */
     public void cancel() {
       this.state.cancel(stateContext);
     }

     /** transfer request operation to enum state to handle */
     public void confirm() {
       this.state.confirm(stateContext);
     }

     /** transfer request operation to enum state to handle */
     public void timeout() {
       this.state.timeout(stateContext);
     }

     /** transfer request operation to enum state to handle */
     public void pay() {
       this.state.pay(stateContext);
     }

     /** subClass: implements OrderStateContext to change Order state */
     private class StateContext implements OrderStateContext {

       @Override
       public void setState(OrderState state) {
         Order.this.setState(state);
       }
     }
   }
   ```

4. Chain of responsibility

   - In the chain of responsibility model, a program can use multiple ways to deal with a problem, and then link them together.
   - When a request comes in, it will traverse the entire chain to find a processor that can handle the request and process the request.

   - sample

   ```java
   package cn.edu.ntu.javase.usage;

   import org.junit.Test;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   /**
   * @author zack <br>
   * @create 2020-01-31 19:03 <br>
   */
   public class ResponsibilityChainUsageTest {

     @Test
     public void testMessageHandlerChain() {
       MessageHandlerChain messageHandlerChain = new MessageHandlerChain();
       messageHandlerChain.handle(new Message(MessageType.JSON));
       messageHandlerChain.handle(new Message(MessageType.XML));
     }
   }

   /** Message Type */
   enum MessageType {
     TEXT,
     BIN,
     XML,
     JSON
   }

   /** define message */
   class Message {
     private final MessageType type;

     public MessageType getType() {
       return type;
     }

     public Message(MessageType type) {
       this.type = type;
     }
   }

   /** define message handler */
   interface MessageHandler {
     /**
     * handle difference type message.
     *
     * @param message
     * @return
     */
     boolean handle(Message message);
   }

   /** implements MessageHandlers based on enum */
   enum MessageHandlers implements MessageHandler {
     /** text handler */
     TEXT_HANDLER(MessageType.TEXT) {
       @Override
       boolean doHandle(Message message) {
         LOG.info("text");
         return true;
       }
     },
     /** bin handler */
     BIN_HANDLER(MessageType.BIN) {
       @Override
       boolean doHandle(Message message) {
         LOG.info("bin");
         return true;
       }
     },
     /** xml handler */
     XML_HANDLER(MessageType.XML) {
       @Override
       boolean doHandle(Message message) {
         LOG.info("xml");
         return true;
       }
     },
     /** json handler */
     JSON_HANDLER(MessageType.JSON) {
       @Override
       boolean doHandle(Message message) {
         LOG.info("json");
         return true;
       }
     };

     private static final Logger LOG = LoggerFactory.getLogger(MessageHandlers.class);
     private final MessageType acceptType;

     MessageHandlers(MessageType acceptType) {
       this.acceptType = acceptType;
     }

     abstract boolean doHandle(Message message);

     /**
     * if message type is accept type, then call doHandle to handle message.
     *
     * @param message
     * @return handle response: 1 is success; 0 is failed
     */
     @Override
     public boolean handle(Message message) {
       return message.getType() == this.acceptType && doHandle(message);
     }
   }

   /** message handle chain */
   class MessageHandlerChain {
     public boolean handle(Message message) {
       for (MessageHandler handler : MessageHandlers.values()) {
         if (handler.handle(message)) {
           return true;
         }
       }
       return false;
     }
   }
   ```

5. Distributor

   - The distributor finds the corresponding processor according to the input data,
     and forwards the request to the processor for processing.
   - Because of its excellent performance, EnumMap is particularly suitable for scenarios
     based on specific types as a distribution strategy.

   - sample

   ```java
   package cn.edu.ntu.javase.usage;

   import org.junit.Test;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import java.util.EnumMap;
   import java.util.Map;

   /**
   * @author zack <br>
   * @create 2020-01-31 19:03 <br>
   */
   public class DistributorUsageTest {
     private static final Logger LOG = LoggerFactory.getLogger(DistributorUsageTest.class);

     @Test
     public void testOperationDispatcher(){
         OperationDispatcher dispatcher = new OperationDispatcher();
         dispatcher.dispatch(new Operation(OperationType.LOGIN));
     }
   }

   /** message type */
   enum OperationType {
     LOGIN,
     ENTER_ROOM,
     EXIT_ROOM,
     LOGOUT
   }

   /** message */
   class Operation {
     private final OperationType type;

     public OperationType getType() {
       return type;
     }

     public Operation(OperationType type) {
       this.type = type;
     }
   }

   /** Operation handler */
   interface OperationHandler {
     /**
     * handle difference operation.
     *
     * @param operation
     */
     void handle(Operation operation);
   }

   /** Implements Operation Dispatcher based on EnumMap */
   class OperationDispatcher {

     private static final Logger LOG = LoggerFactory.getLogger(OperationDispatcher.class);

     private final Map<OperationType, OperationHandler> dispatcherMap =
         new EnumMap<>(OperationType.class);

     public OperationDispatcher() {
       dispatcherMap.put(OperationType.LOGIN, message -> LOG.info("Login"));
       dispatcherMap.put(OperationType.ENTER_ROOM, message -> LOG.info("Enter Room"));
       dispatcherMap.put(OperationType.EXIT_ROOM, message -> LOG.info("Exit Room"));
       dispatcherMap.put(OperationType.LOGOUT, message -> LOG.info("Logout"));
     }

     public void dispatch(Operation operation) {
       OperationHandler handler = this.dispatcherMap.get(operation.getType());
       if (handler != null) {
         handler.handle(operation);
       }
     }
   }
   ```

### Conlusion:

1. enum prop

   - values() / EnumErrorCode.class.getEnumConstants()
   - xx.values()[ordinal];
   - valueOf(clz, name)
   - name() / ordinal()
   - convert enum to Optional

2. 一个类的对象是有限且固定的, 个数有限, 属性固定, 且继承自 `java.lang.Enum`

3. 手工定义枚举类,

   - 1. 第一行直接定义枚举常量; 等价于创建对象
   - 2. 定义枚举类的变量为 final 类型变量
   - 3. 私有化构造器, 枚举对象是唯一的
   - 4. 书写 getter 方法，暴露枚举对象的成员(只读)
   - 5. 提供相关获取值的方法

- 实现接口, 2 种方法

  - 统一在某一个方法中提供各个枚举类对象的实现，可以使用 switch
  - 在声明对象的同时提供对象的实现,

    ```java
    SPRING("春天", "345"){
        // 这里也可以在最后重写 getInfo() 方法，使用 switch 一个一个判断;
        @Override
        public String getInfo() {
            return "春暖花开";
        }
    }
    ```

## Sample:

- Enumeration and implements interface

  ```java
    public interface info {
        String getInfo();
    }

    public enum Sea_Enumeration3 implements info{
        //1.第一行直接定义枚举常量；等价于创建对象
        SPRING("春天", "345"){
            @Override
            public String getInfo() {
                return "chu";

            }
        },SUMMer("夏天", "678"){
            @Override
            public String getInfo() {
                return "xia";

            }
        },FALL("秋天", "91011"){
            @Override
            public String getInfo() {
                return "qiu";

            }
        }, WINTER("冬天", "1212"){
            @Override
            public String getInfo() {
                return "dong";

            }
        };
        //2.定义枚举类的变量为final类型变量
        private  final String desc;
        private final String name;
        //3.私有化构造器：枚举对象是唯一的
        private Sea_Enumeration3(String desc, String name) {
            this.desc = desc;
            this.name = name;
        }
        //4.书写getter方法，暴露枚举对象的成员(只读)
        public String getDesc() {
            return desc;
        }
        public String getName() {
            return name;
        }
        @Override
        public String toString() {
            return "Sea_Enumeration [desc=" + desc + ", name=" + name + "]";
        }
    }
  ```

## Reference

1. https://mp.weixin.qq.com/s/-tx2e6GXUpV3eFljMQF_vw
2. [tutorials-sample](https://github.com/Alice52/tutorials-sample/tree/feat-zack/java/javase/javase.enumeration)
