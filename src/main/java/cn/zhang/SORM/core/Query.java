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
 * �����ѯ�������ṩ����ĺ����ࣩ
 * @author tako_
 *
 */
@SuppressWarnings("all")

public abstract class Query implements Cloneable {

	/**
	 * ����ģ�巽��ģʽ��JDBC������װ��ģ�壬��������
	 * @param sql sql���
	 * @param params sql����
	 * @param clazz ��¼Ҫ��װ����java��
	 * @param back CallBack��ʵ���࣬ʵ�ֻص�
	 * @return
	 */
	public Object executeQueryTemplate(String sql, Object[] params, Class clazz, CallBack back) {
		Connection conn = DBManager.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			//��sql���ò���
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
	 * ֱ��ִ��һ��DML���
	 * @param sql sql���
	 * @param params ����
	 * @return ִ��sql����Ӱ��ļ�¼����
	 */
	public int executeDML(String sql, Object[] params) {
		Connection conn = DBManager.getConn();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			//��sql���ò���
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
	 * ��һ������洢�����ݿ���
	 * �Ѷ����в�Ϊnull�����Դ������ݿ�
	 * �������Ϊnull���0
	 * @param obj Ҫ�洢�Ķ���
	 */
	public void insert(Object obj){
		//obj --> ��	insert into ���� (id, uname, pwd) values(?, ?, ?)
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();	//�洢sql�����Ķ���
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("insert into " + tableInfo.getTname() + " (");
		int countNotNullField = 0;//���㲻Ϊ�յ�����ֵ
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
	 * ɾ��clazz���Ӧ�ı��еļ�¼��ָ������id��
	 * @param clazz �����Ӧ����
	 * @param id ������ֵ
	 * @return ִ��sql����Ӱ��ļ�¼����
	 */
	public int delete(Class clazz, Object id) {
		//ͨ��class�����ҵ�TableInfo
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		//�������
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		//ƴ��sql���
		String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + "=? ";
		//ִ��sql���
		return executeDML(sql, new Object[] {id});
	}
	/**
	 * ɾ�����������ݿ��еļ�¼
	 * @param obj
	 * @return ִ��sql����Ӱ��ļ�¼����
	 */
	public int delete(Object obj){
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		
		//ͨ��������ƣ��������Զ�Ӧ��get��set����
		Object prikeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
		delete (c, prikeyValue);
		return 0;
	}
	
	/**
	 * ���¶����Ӧ�ļ�¼������ֻ����ָ���ֶε�ֵ
	 * @param obj Ҫ���µĶ���
	 * @param fieldNames Ҫ���µ������б�
	 * @return ִ��sql����Ӱ��ļ�¼����
	 */
	public int update(Object obj, String[] fieldNames) {
		//obj{} --> update ���� set uname=?, pwd=? where id=?
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();	//�洢sql�����Ķ���
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo priKey = tableInfo.getOnlyPriKey();
		
		StringBuilder sql = new StringBuilder("update " + tableInfo.getTname() + " set ");
		for(String fname:fieldNames) {
			Object fvalue = ReflectUtils.invokeGet(fname, obj);
			params.add(fvalue);
			sql.append(fname + "=?,");
		}
		sql.setCharAt(sql.length()-1, ' ');//�����һ�����ű�ո�
		sql.append("where ");
		sql.append(priKey.getName() + "=? ");
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));//������������ֵ
		return executeDML(sql.toString(), params.toArray());
	}
	
	/**
	 * ��ѯ���ض��м�¼������ÿ�м�¼��װ��clazzָ����Ķ�����
	 * @param sql ��ѯ���
	 * @param clazz ��װ���ݵ�javabean���clazz����
	 * @param params sql�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public List queryRows(final String sql, final Class clazz, final Object[] params){
		
		return (List)executeQueryTemplate(sql, params, clazz, new CallBack() {
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData metaData = rs.getMetaData();
					//����
					while(rs.next()) {
						if(list==null) {
							list = new ArrayList();	//��Ų�ѯ���������
						}
						Object rowObj = clazz.newInstance();	//����javabean���޲ι�����
						//����
						for(int i=0;i<metaData.getColumnCount();i++) {
							String columnName = metaData.getColumnLabel(i+1);
							Object columnValue = rs.getObject(i+1);
							//����rowObj�����set��������columnValue��ֵ���ý�ȥ
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
	 * ��ѯ����һ�м�¼��������¼��װ��clazzָ����Ķ�����
	 * @param sql ��ѯ���
	 * @param clazz ��װ���ݵ�javabean���clazz����
	 * @param params sql�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Object queryUniqueRow(String sql, Class clazz, Object[] params){
		List list = queryRows(sql, clazz, params);
		return (list!=null&&list.size()>0)?list.get(0):null;
	}
	
	/**
	 * ��ѯ����һ��ֵ��������ֵ����
	 * @param sql ��ѯ���
	 * @param params sql�Ĳ���
	 * @return ��ѯ���Ľ��
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
	 * ��ѯ����һ������������ֵ����
	 * @param sql ��ѯ���
	 * @param params sql�Ĳ���
	 * @return ��ѯ���Ľ��
	 */
	public Number queryNumber(String sql, Object[] params) {
		return (Number)queryValue(sql, params);
	}
	
	/**
	 * ��ҳ��ѯ
	 * @param pageNum �ڼ�ҳ����
	 * @param size ÿһҳ��ʾ���ټ�¼
	 * @return
	 */
	public abstract Object queryPagenate(int pageNum, int size);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
