package cn.zhang.SORM.utils;

/**
 * ��װ���ַ������õĲ���
 * @author tako_
 *
 */
public class StringUtils {
	/**
	 * ��Ŀ���ַ�������ĸ��Ϊ��д
	 * @param str Ŀ���ַ���
	 * @return ����ĸ��Ϊ��д���ַ���
	 */
	public static String firstchar2UpperCase(String str) {
		/**
		 * abcd --> ABCD --> A
		 * +
		 * abcd --> bcd
		 * =Abcd
		 */
		return str.toUpperCase().substring(0, 1) + str.substring(1);
	}
}
