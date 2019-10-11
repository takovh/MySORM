package cn.zhang.SORM.core;

/**
 * ����java�������ͺ����ݿ����͵Ļ���ת��
 * @author tako_
 *
 */
public interface TypeConvertor {
	
	/**
	 * �����ݿ���������ת����java����������
	 * @param columnType ���ݿ��ֶε���������
	 * @return java����������
	 */
	public String databaseType2JavaType(String columnType);
	
	/**
	 * ��java����������ת�������ݿ���������
	 * @param columnType java��������
	 * @return ���ݿ���������
	 */
	public String javaType2DatabaseType(String javaDataType);
	
}
