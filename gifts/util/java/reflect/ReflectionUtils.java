import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
* 反射中封装的函数:
* 1.获取 methodName 方法, 该方法可能是私有的, 也可能是父类的(私有)方法:
* getMethod(Class<?> clazz, String methodName, Class<?>...paramerTypes)
* 2.执行方法:
* *invoke2(String className,String methodName, Object...args)
* *invoke(Object obj,String methodName, Object...args)
* 3.获取定义Class时声明的父类的泛型参数的类型:
* *getSupperClassGenericType(Class<?> clazz, int index)
*/
public class ReflectionUtils {
    /**
    * 功能: 通过反射, 获取定义 Class 时声明的父类的泛型参数的类型
    * 如: public EmployeeDao extends BaseDao<Employee,String>
    *
    * @param clazz:子类对应的           class 对象
    * @param index:子类继承父类时传入的泛型索引, 从0开始
    */
    public static Class<?> getSupperClassGenericType(Class<?> clazz, int index) {
        // 1.获取带泛型父类的Class对象
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // 2.强制转换, 获取实际参数数组
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] args = parameterizedType.getActualTypeArguments();
            if (args != null & args.length > 0) {
                return (Class<?>) args[index];
            }
        }
        return null;
    }

    /**
    * 功能: 获取 methodName 方法, 该方法可能是私有的, 也可能是父类的(私有)方法
    *
    * @param clazz
    * @param methodName
    * @param paramerTypes
    */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramerTypes) {
        // 1.这里通过循环,遍历查找方法, 包括父类中的方法
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(methodName, paramerTypes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
    * @param clazz
    * @param fieldName
    *
    * @return Field
    *
    * @funtion: get the specific field.
    */
    public static Field getField(Class<?> clazz, String fieldName) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }

    /**
    * 功能: 获取 methodName 方法, 该方法可能是私有的, 也可能是父类的(私有)方法
    *
    * @param clazz
    * @param methodName
    * @param paramerTypes
    */
    public static Method getField(Class<?> clazz, String methodName, Class<?>... paramerTypes) {
        // 1.这里通过循环,遍历查找方法, 包括父类中的方法
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(methodName, paramerTypes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
    * 自己定义的执行对象方法的方法1:invoke2(String className,String methodName, Object...args)
    *
    * @param className:  调用方法所在的对象的全类名
    * @param methodName: 方法名, 该方法可能是私有方法, 也可能是父类的(私有)方法
    * @param args:       方法所需的参数
    */
    public static Object invoke(String className, String methodName, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Class<?>[] paramerTypes = (Class<?>[]) new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramerTypes[i] = args[i].getClass();
            }
            Method method = getMethod(clazz, methodName, paramerTypes);
            Object obj = clazz.newInstance();
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
    * 自己定义的执行对象方法的方法1:invoke(Object obj,String methodName, Object...args)
    *
    * @param obj:        调用方法所在的对象实例
    * @param methodName: 方法名, 该方法可能是私有方法；
    * @param args:       方法所需的参数
    */
    public static Object invoke(Object obj, String methodName, Object... args) throws Exception {
        //1.找到这个方法
        Class<?> clazz = obj.getClass();
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        Method method = getMethod(clazz, methodName, parameterTypes);
        //2.执行方法
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /**
    * 自己定义的执行对象方法的方法1:invoke(Object obj,String methodName, Object...args)
    *
    * @param clazz:
    * @param methodName: 方法名, 该方法可能是私有方法；
    * @param args: 方法所需的参数
    */
    public static Object invoke(Class<?> clazz, String methodName, Object... args) throws Exception {
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        Method method = getMethod(clazz, methodName, parameterTypes);
        method.setAccessible(true);
        Object obj = clazz.newInstance();
        return method.invoke(obj, args);
    }
}
