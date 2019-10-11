package cn.zhang.SORM.core;

import java.util.List;

import cn.zhang.SORM.po.Reader;

/**
 * 负责针对MySql数据库的查询
 * @author tako_
 *
 */
public class MySqlQuery extends Query {
	public static void main(String[] args) {
		testQueryRows();
	}
	@SuppressWarnings("unchecked")
	public static void testQueryRows() {
		String sql = "select * from reader where age>? and age<?";
		List<Reader> list = new MySqlQuery().queryRows(sql, Reader.class, new Object[] {3, 6});
		System.out.println(list);
	}
	public static void testDML() {
//		Course c = new Course();
//		c.setC_No(4);
//		c.setC_Name("painting");
//		c.setC_Name("music");
		
		Reader r = new Reader();
		r.setAge(15);
		r.setCard_id("002");
		r.setName("li");
		r.setSex("man");
		r.setTel("123123");
		
//		new MySqlQuery().delete(c);
//		new MySqlQuery().insert(r);
//		new MySqlQuery().update(c, new String[] {"C_Name"});
		
//		for(int i=2;i<7;i++) {
//			r.setCard_id("00" + i);
//			r.setAge(i);
//			new MySqlQuery().insert(r);
//		}
	}
	@Override
	public Object queryPagenate(int pageNum, int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
