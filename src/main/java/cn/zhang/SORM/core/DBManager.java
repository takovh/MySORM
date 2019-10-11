package cn.zhang.SORM.core;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import cn.zhang.SORM.bean.Configuration;
import cn.zhang.SORM.pool.DBConnectionPool;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池功能）
 * @author tako_
 *
 */
public class DBManager {
	/**
	 * 配置信息
	 */
	private static Configuration conf;
	/**
	 * 连接池对象
	 */
	private static DBConnectionPool pool;
	
	
	static {	//静态代码块，只会在类初始化时执行一次
		Properties pros = new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("cn/zhang/SORM/db.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conf = new Configuration();
		conf.setDriver(pros.getProperty("driver"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setUsingDB(pros.getProperty("usingDB"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setPoPackage(pros.getProperty("poPackage"));
		conf.setQueryClass(pros.getProperty("queryClass"));
		

	}
	
	/**
	 * 创建新的Connection对象
	 * @return
	 */
	public static Connection createConn() {
		//加载驱动类
		try {
			Class.forName(conf.getDriver());
			//目前直接建立连接，后期增加连接池处理，提高效率
			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * 获得Connection对象
	 * @return
	 */
	public static Connection getConn() {
		//加载驱动类，使用连接池
		if(pool==null) {
			pool = new DBConnectionPool();
		}
		return pool.getConnection();
		
//		//加载驱动类，未使用连接池
//		try {
//			Class.forName(conf.getDriver());
//			//目前直接建立连接，后期增加连接池处理，提高效率
//			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} 
	}
	
	/**
	 * 关闭连接
	 * 关闭顺序遵循：resultSet-->statement-->connection，三个块一定要分开写
	 * @param rs
	 * @param ps
	 * @param conn
	 */
	public static void close(ResultSet rs, Statement ps, Connection conn) {
		try {
			if(rs!=null) rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if(ps!=null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//使用连接池
		pool.close(conn);
		
//		//未使用连接池，真的关闭
//		try {
//			if(conn!=null) conn.close();	
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 关闭连接
	 * @param ps
	 * @param conn
	 */
	public static void close(Statement ps, Connection conn) {
		try {
			if(ps!=null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//使用连接池
		pool.close(conn);
		
//		//未使用连接池，真的关闭
//		try {
//			if(conn!=null) conn.close();	
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 关闭连接
	 * @param ps
	 * @param conn
	 */
	public static void close(Connection conn) {
		//使用连接池
		pool.close(conn);
		
//		//未使用连接池，真的关闭
//		try {
//			if(conn!=null) conn.close();	
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	public static Configuration getConf() {
		return conf;
	}
}
