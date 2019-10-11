package cn.zhang.SORM.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.zhang.SORM.bean.ColumnInfo;
import cn.zhang.SORM.bean.TableInfo;
import cn.zhang.SORM.utils.JavaFileUtils;
import cn.zhang.SORM.utils.StringUtils;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构
 * @author tako_
 *
 */
public class TableContext {
	/**
	 * Map中，key为表名，value为表信息对象
	 */
	public static Map<String, TableInfo> tables = new HashMap<String, TableInfo>();
	
	/**
	 * 将po的class对象和表信息对象关联起来，便于重用
	 */
	@SuppressWarnings("rawtypes")
	public static Map<Class, TableInfo> poClassTableMap = new HashMap<Class, TableInfo>();
	
	private TableContext() {
	}
	
	static {
		try {
			//初始化获得表的信息
			Connection con = DBManager.getConn();
			DatabaseMetaData dbmd = con.getMetaData();
			
			ResultSet tableRet = dbmd.getTables(null, "%", "%", new String[] {"Table"});
			while(tableRet.next()) {
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>(), new HashMap<String, ColumnInfo>());
				tables.put(tableName, ti);
				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");
				while(set.next()) {
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
				while(set2.next()) {
					ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);
					ti.getPriKeys().add(ci2);
				}
				if(ti.getPriKeys().size()>0) {
					ti.setOnlyPriKey(ti.getPriKeys().get(0));
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		//更新类结构
		updateJavaPOFile();
		//加载PO包下的所有的类，便于重用，提高效率
		loadPOTables();
	}
	
	/**
	 * 根据表结构，更新配置的PO包下面的java类
	 */
	public static void updateJavaPOFile() {
		Map<String, TableInfo> map = TableContext.tables;
		for(TableInfo tableInfo:map.values()) {
			JavaFileUtils.createJavaPOFile(tableInfo, new MySqlTypeConvertor());
		}
	}

	/**
	 * 加载po包下的类
	 */
	@SuppressWarnings("rawtypes")
	public static void loadPOTables() {
		for(TableInfo tableInfo:tables.values()) {
			try {
				Class c = Class.forName("cn.zhang.SORM." + 
						DBManager.getConf().getPoPackage() + "." + 
						StringUtils.firstchar2UpperCase(tableInfo.getTname()));
				poClassTableMap.put(c, tableInfo);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Map<String, TableInfo> tables = TableContext.tables;
		System.out.println(tables);
	}
}
