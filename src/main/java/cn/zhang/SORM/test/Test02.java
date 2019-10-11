package cn.zhang.SORM.test;

import java.util.List;

import cn.zhang.SORM.core.Query;
import cn.zhang.SORM.core.QueryFactory;
import cn.zhang.SORM.po.Reader;

/**
 * 测试连接池的调用
 * @author tako_
 *
 */
public class Test02 {
	public static void main(String[] args) {
		test();
	}
	@SuppressWarnings("unchecked")
	public static void test() {
		Query q = QueryFactory.createQuery();
		String sql = "select * from reader where age>? and age<?";
		List<Reader> list = q.queryRows(sql, Reader.class, new Object[] {3, 6});
		System.out.println(list);
	}
}
