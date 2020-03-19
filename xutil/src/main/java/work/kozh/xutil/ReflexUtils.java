package work.kozh.xutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class ReflexUtils {


    //    ---------------------------    获取对应构造函数的 类的实例方法        ----------------------------//

    /**
     * 获取无参的 构造方法   类的实例
     * 重载方法 传入class名字
     *
     * @param className
     * @return
     */
    public static Object createObject(String className) {
        //参数类型 class
        Class[] paraTypes = {};
        //参数数值
        Object[] paraValues = {};

        try {
            Class<?> clazz = Class.forName(className);
            return createObject(clazz, paraTypes, paraValues);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取无参的 构造方法  类的实例
     * 重载方法 传入class
     *
     * @param clazz
     * @return
     */
    public static Object createObject(Class clazz) {
        //参数类型 class
        Class[] paraTypes = {};
        //参数数值
        Object[] paraValues = {};

        return createObject(clazz, paraTypes, paraValues);

    }


    /**
     * 获取一个参数的 构造方法  类的实例
     * * 重载方法 传入class名字
     *
     * @param className
     * @param paraType
     * @param paraValue
     * @return
     */
    public static Object createObject(String className, Class paraType, Object paraValue) {
        //参数类型 class
        Class[] paraTypes = {paraType};
        //参数数值
        Object[] paraValues = {paraValue};

        try {
            Class<?> clazz = Class.forName(className);
            return createObject(clazz, paraTypes, paraValues);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取一个参数的 构造方法  类的实例
     * 重载方法 传入class
     *
     * @param clazz
     * @param paraType
     * @param paraValue
     * @return
     */
    public static Object createObject(Class clazz, Class paraType, Object paraValue) {
        //参数类型 class
        Class[] paraTypes = {paraType};
        //参数数值
        Object[] paraValues = {paraValue};

        return createObject(clazz, paraTypes, paraValues);

    }


    /**
     * 获取 多参数的构造方法  （兼容型）   类的实例
     * *已知class类名
     *
     * @param className
     * @param paraTypes
     * @param paraValues
     * @return
     */
    public static Object createObject(String className, Class[] paraTypes, Object[] paraValues) {

        try {
            Class<?> clazz = Class.forName(className);
            return createObject(clazz, paraTypes, paraValues);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * 获取 多参数的 构造方法  （兼容型）   类的实例
     * 已知class类
     * 最终内部调用的方法
     *
     * @param clazz
     * @param paraTypes
     * @param paraValues
     * @return
     */
    public static Object createObject(Class<?> clazz, Class[] paraTypes, Object[] paraValues) {

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(paraTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(paraValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //    ---------------------------    获取内部函数方法        ----------------------------//


    /**
     * 获取 无参数的 内部函数方法
     *
     * @param object
     * @param methodName
     * @return
     */
    public static Object invokeInstanceMethod(Object object, String methodName) {

        Class[] paraTypes = new Class[]{};
        Object[] paraValues = new Object[]{};

        return invokeInstanceMethod(object, methodName, paraTypes, paraValues);

    }


    /**
     * 获取 单个参数的 内部函数方法 （兼容型）
     *
     * @param object
     * @param methodName
     * @param paraType
     * @param paraValue
     * @return
     */
    public static Object invokeInstanceMethod(Object object, String methodName, Class paraType, Object paraValue) {

        Class[] paraTypes = {paraType};
        Object[] paraValues = {paraValue};

        return invokeInstanceMethod(object, methodName, paraTypes, paraValues);

    }

    /**
     * 获取 多个参数的 内部函数方法 （兼容型）
     * 最终调用的方法
     *
     * @param object
     * @param methodName
     * @param paraTypes
     * @param paraValues
     * @return
     */
    public static Object invokeInstanceMethod(Object object, String methodName, Class[] paraTypes, Object[] paraValues) {

        if (object == null) {
            return null;
        }

        try {
            //调用一个private方法  在指定类中获取指定的方法
            Method method = object.getClass().getDeclaredMethod(methodName, paraTypes);
            method.setAccessible(true);
            return method.invoke(object, paraValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    //    ---------------------------    获取内部静态函数方法        ----------------------------//

    /**
     * 获取 无参数的 内部静态函数方法
     * 已知class类名
     *
     * @param className
     * @param methodName
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName) {

        Class[] paraTypes = new Class[]{};
        Object[] paraValues = new Object[]{};

        return invokeStaticMethod(className, methodName, paraTypes, paraValues);

    }


    /**
     * 获取 无参数的 内部静态函数方法
     * 已知 class类
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String methodName) {

        Class[] paraTypes = new Class[]{};
        Object[] paraValues = new Object[]{};

        return invokeInstanceMethod(clazz, methodName, paraTypes, paraValues);

    }

    /**
     * 获取 单个参数的 内部静态函数方法
     * 已知类名
     *
     * @param className
     * @param methodName
     * @param paraType
     * @param paraValue
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName, Class paraType, Object paraValue) {

        Class[] paraTypes = {paraType};
        Object[] paraValues = {paraValue};

        return invokeStaticMethod(className, methodName, paraTypes, paraValues);

    }


    /**
     * 获取 单个参数的 内部静态函数方法
     * 已知class类
     *
     * @param clazz
     * @param methodName
     * @param paraType
     * @param paraValue
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String methodName, Class paraType, Object paraValue) {

        Class[] paraTypes = {paraType};
        Object[] paraValues = {paraValue};

        return invokeStaticMethod(clazz, methodName, paraTypes, paraValues);

    }

    /**
     * 获取 多个参数的 内部函数方法 （兼容型）
     * 已知class类
     * 最终调用的方法
     *
     * @param clazz
     * @param methodName
     * @param paraTypes
     * @param paraValues
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String methodName, Class[] paraTypes, Object[] paraValues) {

        try {
            //调用一个private方法  在指定类中获取指定的方法
            Method method = clazz.getDeclaredMethod(methodName, paraTypes);
            method.setAccessible(true);
            return method.invoke(null, paraValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取 多个参数的 内部函数方法 （兼容型）
     * 已知类名
     *
     * @param className
     * @param methodName
     * @param paraTypes
     * @param paraValues
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName, Class[] paraTypes, Object[] paraValues) {

        try {
            //调用一个private方法  在指定类中获取指定的方法
            Class clazz = Class.forName(className);
            return invokeStaticMethod(clazz, methodName, paraTypes, paraValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    //    ---------------------------    获取内部变量 方法        ----------------------------//


    /**
     * 获取类内部的变量
     * 已知class类
     *
     * @param clazz
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getFieldObject(Class clazz, Object object, String fieldName) {

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * * 获取类内部的变量
     * 已知class类名
     *
     * @param className
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getFieldObject(String className, Object object, String fieldName) {

        try {
            Class<?> clazz = Class.forName(className);
            return getFieldObject(clazz, object, fieldName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    //    ---------------------------    设置内部变量 方法        ----------------------------//


    /**
     * 设置内部变量值
     * 已知class类名
     *
     * @param className
     * @param object
     * @param fieldName
     * @param fieldValue
     */
    public static void setFieldObject(String className, Object object, String fieldName, Object fieldValue) {

        try {
            Class<?> clazz = Class.forName(className);
            setFieldObject(clazz, object, fieldName, fieldValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 设置内部变量值
     * 已知class类
     *
     * @param clazz
     * @param object
     * @param fieldName
     * @param fieldValue
     */
    public static void setFieldObject(Class clazz, Object object, String fieldName, Object fieldValue) {

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //    ---------------------------    获取内部静态变量 方法        ----------------------------//

    /**
     * 获取类内部的静态变量
     * 已知类名
     *
     * @param className
     * @param fieldName
     * @return
     */
    public static Object getStaticFieldObject(String className, String fieldName) {

        return getFieldObject(className, null, fieldName);

    }


    /**
     * 获取类内部的静态变量
     * 已知类
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Object getStaticFieldObject(Class clazz, String fieldName) {

        return getFieldObject(clazz, null, fieldName);

    }


    //    ---------------------------   设置内部静态变量 方法        ----------------------------//

    /**
     * 设置类的内部静态变量值
     * 已知类名
     *
     * @param className
     * @param fieldName
     * @param fieldValue
     */
    public static void setStaticFieldObject(String className, String fieldName, Object fieldValue) {

        setFieldObject(className, null, fieldName, fieldValue);

    }

    /**
     * 设置类的内部静态变量值
     * 已知类
     *
     * @param clazz
     * @param fieldName
     * @param fieldValue
     */
    public static void setStaticFieldObject(Class clazz, String fieldName, Object fieldValue) {

        setFieldObject(clazz, null, fieldName, fieldValue);

    }


}
