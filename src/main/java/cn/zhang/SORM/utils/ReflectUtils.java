package cn.zhang.SORM.utils;

import java.lang.reflect.Method;

/**
 * ��װ�˷��䳣�õĲ���
 * @author tako_
 *
 */
public class ReflectUtils {

	/**
	 * ����obj�����Ӧ����fieldName��get����
	 * @param fieldName
	 * @param obj
	 * @return get�������ص�ֵ
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object invokeGet(String fieldName, Object obj) {
		Class c = obj.getClass();
		//ͨ��������ƣ��������Զ�Ӧ��get��set����
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
	 * ����obj�����Ӧ����columnName��set����,����ΪcolumnValue
	 * @param obj 
	 * @param columnName
	 * @param columnValue
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void invokeSet(Object obj, String columnName, Object columnValue) {
		Class c = obj.getClass();
		//ͨ��������ƣ��������Զ�Ӧ��get��set����
		try {
			//columnValue.getClass()������bug
			Method m = c.getDeclaredMethod("set" + StringUtils.firstchar2UpperCase(columnName), columnValue.getClass());
			m.invoke(obj, columnValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
