package cn.zhang.SORM.utils;

/**
 * 封装了字符串常用的操作
 * @author tako_
 *
 */
public class StringUtils {
	/**
	 * 将目标字符串首字母变为大写
	 * @param str 目标字符串
	 * @return 首字母变为大写的字符串
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
