package cn.zhang.SORM.core;

/**
 * 创建Query对象的工厂类
 * @author tako_
 *
 */
public class QueryFactory {
	@SuppressWarnings("unused")
	private static QueryFactory factory = new QueryFactory();
	private static Query prototypeObj;	//原型对象
	static {
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(DBManager.getConf().getQueryClass());
			prototypeObj = (Query)c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private QueryFactory() {}	//私有构造器
	
	public static Query createQuery() {
		try {
			return (Query) prototypeObj.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
