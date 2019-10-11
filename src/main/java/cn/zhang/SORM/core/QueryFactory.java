package cn.zhang.SORM.core;

/**
 * ����Query����Ĺ�����
 * @author tako_
 *
 */
public class QueryFactory {
	@SuppressWarnings("unused")
	private static QueryFactory factory = new QueryFactory();
	private static Query prototypeObj;	//ԭ�Ͷ���
	static {
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(DBManager.getConf().getQueryClass());
			prototypeObj = (Query)c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private QueryFactory() {}	//˽�й�����
	
	public static Query createQuery() {
		try {
			return (Query) prototypeObj.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
