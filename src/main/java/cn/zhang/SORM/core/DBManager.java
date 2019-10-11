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
 * ����������Ϣ��ά�����Ӷ���Ĺ����������ӳع��ܣ�
 * @author tako_
 *
 */
public class DBManager {
	/**
	 * ������Ϣ
	 */
	private static Configuration conf;
	/**
	 * ���ӳض���
	 */
	private static DBConnectionPool pool;
	
	
	static {	//��̬����飬ֻ�������ʼ��ʱִ��һ��
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
	 * �����µ�Connection����
	 * @return
	 */
	public static Connection createConn() {
		//����������
		try {
			Class.forName(conf.getDriver());
			//Ŀǰֱ�ӽ������ӣ������������ӳش������Ч��
			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * ���Connection����
	 * @return
	 */
	public static Connection getConn() {
		//���������࣬ʹ�����ӳ�
		if(pool==null) {
			pool = new DBConnectionPool();
		}
		return pool.getConnection();
		
//		//���������࣬δʹ�����ӳ�
//		try {
//			Class.forName(conf.getDriver());
//			//Ŀǰֱ�ӽ������ӣ������������ӳش������Ч��
//			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} 
	}
	
	/**
	 * �ر�����
	 * �ر�˳����ѭ��resultSet-->statement-->connection��������һ��Ҫ�ֿ�д
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
		//ʹ�����ӳ�
		pool.close(conn);
		
//		//δʹ�����ӳأ���Ĺر�
//		try {
//			if(conn!=null) conn.close();	
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * �ر�����
	 * @param ps
	 * @param conn
	 */
	public static void close(Statement ps, Connection conn) {
		try {
			if(ps!=null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//ʹ�����ӳ�
		pool.close(conn);
		
//		//δʹ�����ӳأ���Ĺر�
//		try {
//			if(conn!=null) conn.close();	
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * �ر�����
	 * @param ps
	 * @param conn
	 */
	public static void close(Connection conn) {
		//ʹ�����ӳ�
		pool.close(conn);
		
//		//δʹ�����ӳأ���Ĺر�
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
