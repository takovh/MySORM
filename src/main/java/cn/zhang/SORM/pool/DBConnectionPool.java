package cn.zhang.SORM.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.zhang.SORM.core.DBManager;

/**
 * ���ӳص���
 * @author tako_
 *
 */
public class DBConnectionPool {
	/**
	 * ���ӳض���
	 */
	private List<Connection> pool;
	/**
	 * ���������
	 */
	private static final int POOL_MAX_SIZE = 100;
	/**
	 * ��С������
	 */
	private static final int POOL_MIN_SIZE = 10;
	
	/**
	 * ��ʼ�����ӳأ�ʹ���е�������������Сֵ
	 */
	public void initPool() {
		if(pool==null) {
			pool = new ArrayList<Connection>();
		}
		while(pool.size()<POOL_MIN_SIZE) {
			pool.add(DBManager.createConn());
			System.out.println("��ʼ���أ�������������" + pool.size());
		}
	}
	
	/**
	 * �����ӳ���ȡһ������
	 * @return
	 */
	public synchronized Connection getConnection() {
		int last_index = pool.size()-1;
		Connection conn = pool.get(last_index);
		pool.remove(last_index);
		return conn;
	}
	
	/**
	 * �����ӷŻ����ӳ�
	 * @param conn
	 */
	public synchronized void close(Connection conn) {
		if(pool.size()>=POOL_MAX_SIZE) {
			try {
				if(conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			pool.add(conn);
		}
	}
	
	public DBConnectionPool() {
		initPool();
	}
}
