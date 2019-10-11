package cn.zhang.SORM.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.zhang.SORM.core.DBManager;

/**
 * 连接池的类
 * @author tako_
 *
 */
public class DBConnectionPool {
	/**
	 * 连接池对象
	 */
	private List<Connection> pool;
	/**
	 * 最大连接数
	 */
	private static final int POOL_MAX_SIZE = 100;
	/**
	 * 最小连接数
	 */
	private static final int POOL_MIN_SIZE = 10;
	
	/**
	 * 初始化连接池，使池中的连接数增至最小值
	 */
	public void initPool() {
		if(pool==null) {
			pool = new ArrayList<Connection>();
		}
		while(pool.size()<POOL_MIN_SIZE) {
			pool.add(DBManager.createConn());
			System.out.println("初始化池，池中连接数：" + pool.size());
		}
	}
	
	/**
	 * 从连接池中取一个连接
	 * @return
	 */
	public synchronized Connection getConnection() {
		int last_index = pool.size()-1;
		Connection conn = pool.get(last_index);
		pool.remove(last_index);
		return conn;
	}
	
	/**
	 * 将连接放回连接池
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
