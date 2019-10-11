package cn.zhang.SORM.utils;

import java.lang.reflect.Method;

/**
 * 封装了反射常用的操作
 * @author tako_
 *
 */
public class ReflectUtils {

	/**
	 * 调用obj对象对应属性fieldName的get方法
	 * @param fieldName
	 * @param obj
	 * @return get方法返回的值
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object invokeGet(String fieldName, Object obj) {
		Class c = obj.getClass();
		//通过反射机制，调用属性对应的get、set方法
		try {
			Method m = c.getDeclaredMethod("get"+StringUtils.firstchar2UpperCase(fieldName), null);
			return m.invoke(obj, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * 调用obj对象对应属性columnName的set方法,设置为columnValue
	 * @param obj 
	 * @param columnName
	 * @param columnValue
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void invokeSet(Object obj, String columnName, Object columnValue) {
		Class c = obj.getClass();
		//通过反射机制，调用属性对应的get、set方法
		try {
			//columnValue.getClass()参数有bug
			Method m = c.getDeclaredMethod("set" + StringUtils.firstchar2UpperCase(columnName), columnValue.getClass());
			m.invoke(obj, columnValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
