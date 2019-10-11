package cn.zhang.SORM.test;

import java.util.List;

import cn.zhang.SORM.core.Query;
import cn.zhang.SORM.core.QueryFactory;
import cn.zhang.SORM.po.Reader;

public class Test01 {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Query q = QueryFactory.createQuery();
		String sql = "select * from reader where age>? and age<?";
		List<Reader> list = q.queryRows(sql, Reader.class, new Object[] {3, 6});
		System.out.println(list);
	}
}
