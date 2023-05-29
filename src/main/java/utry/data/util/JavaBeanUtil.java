package utry.data.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 针对JavaBean的工具类,该工具类只适用于标准的JavaBean<br>
 * 对于标准的JavaBean属性名称,该类只处理private,非static,非final的属性<br>
 * 若在使用该类的时候抛出{@link NoSuchMethodException},请修改对应的属性名称,保证该名称可以用标准的<br>
 * JDK的方法生成对应的getter和setter方法名称<br>
 *
 * @see PropertyDescriptor#getWriteMethod()
 * @see PropertyDescriptor#getReadMethod()
 * @see #isJavaBeanField(Field)
 *
 * @author YEFEI
 *
 * @date 2017年1月17日 上午10:24:28
 */
public final class JavaBeanUtil {

    /**
     * 私有化构造器
     */
    private JavaBeanUtil() {
        throw new AssertionError("There is no " + JavaBeanUtil.class.getName() + " instances for you!");
    }

    /**
     * 获取某个JavaBean对象的所有的属性名称列表
     *
     * @param clazz 对象的类型
     *
     * @return 某个类型对应的所有的属性名称列表
     */
    public static List<String> getJavaBeanFieldNames(Class<?> clazz) {
        Set<String> fieldNameSet = new HashSet<String>();
        while (clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (isJavaBeanField(field)) {
                    fieldNameSet.add(field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return Collections.unmodifiableList(new ArrayList<String>(fieldNameSet));
    }

    /**
     * 判断属性是否是标准的JavaBean属性
     *
     * @param field
     *            属性对象
     *
     * @return 若该属性是private, 非static, 非final的, 返回true;反之,返回false
     */
    private static boolean isJavaBeanField(Field field) {
        int modifier = field.getModifiers();
        return Modifier.isPrivate(modifier) && !Modifier.isStatic(modifier) && !Modifier.isFinal(modifier);
    }

    /**
     * JavaBean对象中是否存在指定的属性名称
     *
     * @param propertyName
     *            熟悉名称
     *
     * @param clazz
     *            检查的类型
     *
     * @return 在类中存在指定的名称, 则返回true, 若不存在, 返回false
     */
    private static boolean hasField(String propertyName, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (isJavaBeanField(field)) {
                if (field.getName().equals(propertyName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 调用某属性对应的setter方法,若参数中有一个为<code>null</code>,则跳过不执行方法
     *
     * @param target
     *            需要调用的对象
     *
     * @param propertyName
     *            属性名称
     *
     * @param val
     *            需要设置的值
     */
    public static void invokeSetter(Object target, String propertyName, Object val) {
        if (target != null && propertyName != null && val != null) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(propertyName, target.getClass());
                pd.getWriteMethod().invoke(target, val);
            } catch (Exception e) {
                String errorMsg = "在" + target + "上调用setter方法出错,属性名称为:" + propertyName + ",设置的值为:" + val;
                throw new IllegalArgumentException(errorMsg);
            }
        }
    }

    /**
     * 调用某属性对应的getter方法,若参数中有一个为<code>null</code>,跳过不执行方法,并返回<code>null</code>
     *
     * @param target
     *            需要调用的对象
     *
     * @param propertyName
     *            属性名称
     *
     * @param classOfT
     *            返回对象的类型
     *
     * @return 在JavaBean对象中某属性对应的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeGetter(Object target, String propertyName, Class<T> classOfT) {
        if (target != null && propertyName != null && classOfT != null) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(propertyName, target.getClass());
                return (T) pd.getReadMethod().invoke(target);
            } catch (Exception e) {
                String errorMsg = "在" + target + "上调用getter方法出错,属性名称为:" + propertyName + ",返回的class为:" + classOfT;
                throw new IllegalArgumentException(errorMsg);
            }
        }
        return null;
    }

    /**
     * 调用某属性对应的getter方法,若参数中有一个为<code>null</code>,跳过不执行方法,并返回<code>null</code>
     * ,该方法返回的对象为Object类型
     *
     * @param target
     *            需要调用的对象
     *
     * @param propertyName
     *            返回对象的类型
     *
     * @return 在JavaBean对象中某属性对应的值, 这里为Object类型
     */
    public static Object invokeGetter(Object target, String propertyName) {
        return invokeGetter(target, propertyName, Object.class);
    }

    /**
     * 在一个集合中获取其中对象的某属性,并组成一个列表
     *
     * @param dataList
     *            数据集合
     *
     * @param propertyName
     *            需要提取的属性
     *
     * @param classOfT
     *            返回集合中持有的对象的类型
     *
     * @return 集合列表，包含数据集合中对象对应属性的值
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getPropertyValues(Iterable<?> dataList, String propertyName, Class<T> classOfT) {
        List<Object> results = new LinkedList<Object>();
        for (Object obj : dataList) {
            if (hasField(propertyName, obj.getClass())) {
                Object val = invokeGetter(obj, propertyName);
                results.add(val);
            }
        }
        return (List<T>) results;
    }

    /**
     * 复制属性到指定对象,复制的属性在目标对象中存在并且也存在与源对象,但是不复制在指定排除列表的属性
     *
     * @param srcObj
     *            源数据对象
     *
     * @param target
     *            目标数据对象
     *
     * @param propertyNames
     *            不需要复制的属性名称列表,若为<code>null</code>,则会复制所有的属性
     *
     * @return 复制晚属性值的对象, 也就是target
     */
    public static <T> T copyPropertyExclude(Object srcObj, T target, List<String> propertyNames) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            String propertyName = field.getName();
            if (hasField(propertyName, srcObj.getClass())) {
                Boolean hasProperty = (propertyNames != null && !propertyNames.contains(propertyName)) || propertyNames == null;
                if (hasProperty) {
                    Object val = invokeGetter(srcObj, propertyName);
                    invokeSetter(target, propertyName, val);
                }
            }
        }
        return target;
    }

    /**
     * 复制属性到指定对象,复制的属性在目标对象中存在并且也存在与源对象,并且还要存在于指定的propertyNames中
     *
     * @param srcObj
     *            源数据对象
     *
     * @param target
     *            目标数据对象
     *
     * @param propertyNames
     *            需要复制的属性名称列表,若为<code>null</code>,则不会复制任何属性
     *
     * @return 复制好属性值的对象, 也就是target
     */
    public static <T> T copyPropertyInclude(Object srcObj, T target, List<String> propertyNames) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            String propertyName = field.getName();
            if (hasField(propertyName, srcObj.getClass())) {
                if (propertyNames != null && propertyNames.contains(propertyName)) {
                    Object val = invokeGetter(srcObj, propertyName);
                    invokeSetter(target, propertyName, val);
                }
            }
        }
        return target;
    }

    /**
     * 复制所有的属性,这些熟悉必须存在与target中也必须出现在srcObj中，否则不会复制
     *
     * @param srcObj
     *            源数据对象
     *
     * @param target
     *            目标数据对象
     *
     * @return 复制好属性值的对象, 也就是target
     */
    public static <T> T copyAllProperties(Object srcObj, T target) {
        return copyPropertyExclude(srcObj, target, null);
    }

}