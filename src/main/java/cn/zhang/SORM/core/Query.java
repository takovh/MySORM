package cn.zhang.SORM.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.zhang.SORM.bean.ColumnInfo;
import cn.zhang.SORM.bean.TableInfo;
import cn.zhang.SORM.po.Reader;
import cn.zhang.SORM.utils.JDBCUtils;
import cn.zhang.SORM.utils.ReflectUtils;

/**
 * 负责查询（对外提供服务的核心类）
 * @author tako_
 *
 */
@SuppressWarnings("all")

public abstract class Query implements Cloneable {

	/**
	 * 采用模板方法模式将JDBC操作封装成模板，便于重用
	 * @param sql sql语句
	 * @param params sql参数
	 * @param clazz 记录要封装到的java类
	 * @param back CallBack的实现类，实现回调
	 * @return
	 */
	public Object executeQueryTemplate(String sql, Object[] params, Class clazz, CallBack back) {
		Connection conn = DBManager.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			//给sql设置参数
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			
			return back.doExecute(conn, ps, rs);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			DBManager.close(ps, conn);
		}
	}
	/**
	 * 直接执行一个DML语句
	 * @param sql sql语句
	 * @param params 参数
	 * @return 执行sql语句后影响的记录行数
	 */
	public int executeDML(String sql, Object[] params) {
		Connection conn = DBManager.getConn();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			//给sql设置参数
			JDBCUtils.handleParams(ps, params);
			count = ps.executeUpdate();
			System.out.println(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBManager.close(ps, conn);
		}
		return count;
	}
	
	/**
	 * 将一个对象存储到数据库中
	 * 把对象中不为null的属性存入数据库
	 * 如果数字为null则放0
	 * @param obj 要存储的对象
	 */
	public void insert(Object obj){
		//obj --> 表	insert into 表名 (id, uname, pwd) values(?, ?, ?)
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();	//存储sql参数的对象
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("insert into " + tableInfo.getTname() + " (");
		int countNotNullField = 0;//计算不为空的属性值
		Field[] fs = c.getDeclaredFields();
		for(Field f:fs) {
			String fieldName = f.getName();
			Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
			if(fieldValue!=null) {
				countNotNullField++;
				sql.append(fieldName + ",");
				params.add(fieldValue);
			}
		}
		sql.setCharAt(sql.length()-1, ')');
		sql.append(" values(");
		for(int i=0;i<countNotNullField;i++) {
			sql.append("?,");
		}
		sql.setCharAt(sql.length()-1, ')');
		
		executeDML(sql.toString(), params.toArray());
	}
	
	/**
	 * 删除clazz类对应的表中的记录（指定主键id）
	 * @param clazz 跟表对应的类
	 * @param id 主键的值
	 * @return 执行sql语句后影响的记录行数
	 */
	public int delete(Class clazz, Object id) {
		//通过class对象找到TableInfo
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		//获得主键
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		//拼接sql语句
		String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + "=? ";
		//执行sql语句
		return executeDML(sql, new Object[] {id});
	}
	/**
	 * 删除对象在数据库中的记录
	 * @param obj
	 * @return 执行sql语句后影响的记录行数
	 */
	public int delete(Object obj){
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		
		//通过反射机制，调用属性对应的get、set方法
		Object prikeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
		delete (c, prikeyValue);
		return 0;
	}
	
	/**
	 * 更新对象对应的记录，并且只更新指定字段的值
	 * @param obj 要更新的对象
	 * @param fieldNames 要更新的属性列表
	 * @return 执行sql语句后影响的记录行数
	 */
	public int update(Object obj, String[] fieldNames) {
		//obj{} --> update 表名 set uname=?, pwd=? where id=?
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();	//存储sql参数的对象
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo priKey = tableInfo.getOnlyPriKey();
		
		StringBuilder sql = new StringBuilder("update " + tableInfo.getTname() + " set ");
		for(String fname:fieldNames) {
			Object fvalue = ReflectUtils.invokeGet(fname, obj);
			params.add(fvalue);
			sql.append(fname + "=?,");
		}
		sql.setCharAt(sql.length()-1, ' ');//将最后一个逗号变空格
		sql.append("where ");
		sql.append(priKey.getName() + "=? ");
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));//参数中主键的值
		return executeDML(sql.toString(), params.toArray());
	}
	
	/**
	 * 查询返回多行记录，并将每行记录封装到clazz指定类的对象中
	 * @param sql 查询语句
	 * @param clazz 封装数据的javabean类的clazz对象
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public List queryRows(final String sql, final Class clazz, final Object[] params){
		
		return (List)executeQueryTemplate(sql, params, clazz, new CallBack() {
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData metaData = rs.getMetaData();
					//多行
					while(rs.next()) {
						if(list==null) {
							list = new ArrayList();	//存放查询结果的容器
						}
						Object rowObj = clazz.newInstance();	//调用javabean的无参构造器
						//多列
						for(int i=0;i<metaData.getColumnCount();i++) {
							String columnName = metaData.getColumnLabel(i+1);
							Object columnValue = rs.getObject(i+1);
							//调用rowObj对象的set方法，将columnValue的值设置进去
							ReflectUtils.invokeSet(rowObj, columnName, columnValue);
						}
						list.add(rowObj);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return list;
			}
		});
	}
	
	/**
	 * 查询返回一行记录，并将记录封装到clazz指定类的对象中
	 * @param sql 查询语句
	 * @param clazz 封装数据的javabean类的clazz对象
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public Object queryUniqueRow(String sql, Class clazz, Object[] params){
		List list = queryRows(sql, clazz, params);
		return (list!=null&&list.size()>0)?list.get(0):null;
	}
	
	/**
	 * 查询返回一个值，并将该值返回
	 * @param sql 查询语句
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public Object queryValue(String sql, Object[] params) {
		return executeQueryTemplate(sql, params, null, new CallBack() {
			
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object value = null;
				try {
					while(rs.next()) {
						value = rs.getObject(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return value;
			}
		});
	}
	
	/**
	 * 查询返回一个数，并将该值返回
	 * @param sql 查询语句
	 * @param params sql的参数
	 * @return 查询到的结果
	 */
	public Number queryNumber(String sql, Object[] params) {
		return (Number)queryValue(sql, params);
	}
	
	/**
	 * 分页查询
	 * @param pageNum 第几页数据
	 * @param size 每一页显示多少记录
	 * @return
	 */
	public abstract Object queryPagenate(int pageNum, int size);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
