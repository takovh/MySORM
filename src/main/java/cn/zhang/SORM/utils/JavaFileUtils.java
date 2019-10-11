package cn.zhang.SORM.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zhang.SORM.bean.ColumnInfo;
import cn.zhang.SORM.bean.JavaFieldGetSet;
import cn.zhang.SORM.bean.TableInfo;
import cn.zhang.SORM.core.DBManager;
import cn.zhang.SORM.core.TypeConvertor;

/**
 * 封装了生成java源代码的操作
 * @author tako_
 *
 */
public class JavaFileUtils {
	/**
	 * 根据字段信息生成java属性信息
	 * 如：var username --> private String username;以及相应的set和get方法源码
	 * @param column 字段信息
	 * @param convertor 类型转化器
	 * @return java属性和set、get方法源码
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		
		/**
		 * 生成定义字段源码
		 */
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + column.getName() + ";\n");
		
		/**
		 * 生成get方法源码
		 */
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic " + javaFieldType + " get" + StringUtils.firstchar2UpperCase(column.getName()) + "(){\n");
		getSrc.append("\t\treturn " + column.getName() + ";\n");
		getSrc.append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());
		
		/**
		 * 生成set方法源码
		 */
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set" + StringUtils.firstchar2UpperCase(column.getName()) + "(");
		setSrc.append(javaFieldType + " " + column.getName() + "){\n");
		setSrc.append("\t\tthis." + column.getName() + "=" + column.getName() + ";\n");
		setSrc.append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());
		
		return jfgs;
	}
	
	/**
	 * 根据表信息生成java类的源代码
	 * @param tableInfo 表信息
	 * @param convertor 类型转化器
	 * @return java类的源代码
	 */
	public static String createJavaSrc(TableInfo tableInfo, TypeConvertor convertor) {
		
		Map<String, ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<JavaFieldGetSet>();
		for(ColumnInfo c:columns.values()) {
			javaFields.add(createFieldGetSetSRC(c, convertor));
		}
		
		StringBuilder src = new StringBuilder();
		//生成package语句
		src.append("package cn.zhang.SORM." + DBManager.getConf().getPoPackage() + ";\n\n");
		//生成import语句
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		//生成类声明语句
		src.append("public class " + StringUtils.firstchar2UpperCase(tableInfo.getTname()) + " {\n");
		//生成属性列表
		for(JavaFieldGetSet f:javaFields) {
			src.append(f.getFieldInfo());
		}
		src.append("\n");
		//生成get方法列表
		for(JavaFieldGetSet f:javaFields) {
			src.append(f.getGetInfo());
		}
		src.append("\n");
		//生成set方法列表
		for(JavaFieldGetSet f:javaFields) {
			src.append(f.getSetInfo());
		}
		src.append("\n");
		//生成类结束符号
		src.append("}");
//		System.out.println(src);
		return src.toString();
	}
	
	
	public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor convertor) {
		String src = createJavaSrc(tableInfo, convertor);
		
		String srcPath = DBManager.getConf().getSrcPath() + "\\";
		String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "\\\\");
		File f = new File(srcPath + packagePath);
//		System.out.println(f.getAbsolutePath());
		
		if(!f.exists()) {
			f.mkdirs();
		}
		
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(f.getAbsolutePath() + 
					"/" + StringUtils.firstchar2UpperCase(tableInfo.getTname()) + ".java"));
			bw.write(src);
			System.out.println("建立表" + tableInfo.getTname() + "对应的java类：" + 
					StringUtils.firstchar2UpperCase(tableInfo.getTname()) + ".java");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(bw!=null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
//		ColumnInfo ci = new ColumnInfo("username", "varchar", 0);
//		System.out.println(createFieldGetSetSRC(ci, new MySqlTypeConvertor()));
		
//		Map<String, TableInfo> map = TableContext.tables;
//		for(TableInfo tableInfo:map.values()) {
//			createJavaPOFile(tableInfo, new MySqlTypeConvertor());
//		}
	}
}
