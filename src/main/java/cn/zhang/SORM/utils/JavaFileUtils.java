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
 * ��װ������javaԴ����Ĳ���
 * @author tako_
 *
 */
public class JavaFileUtils {
	/**
	 * �����ֶ���Ϣ����java������Ϣ
	 * �磺var username --> private String username;�Լ���Ӧ��set��get����Դ��
	 * @param column �ֶ���Ϣ
	 * @param convertor ����ת����
	 * @return java���Ժ�set��get����Դ��
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		
		/**
		 * ���ɶ����ֶ�Դ��
		 */
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + column.getName() + ";\n");
		
		/**
		 * ����get����Դ��
		 */
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic " + javaFieldType + " get" + StringUtils.firstchar2UpperCase(column.getName()) + "(){\n");
		getSrc.append("\t\treturn " + column.getName() + ";\n");
		getSrc.append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());
		
		/**
		 * ����set����Դ��
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
	 * ���ݱ���Ϣ����java���Դ����
	 * @param tableInfo ����Ϣ
	 * @param convertor ����ת����
	 * @return java���Դ����
	 */
	public static String createJavaSrc(TableInfo tableInfo, TypeConvertor convertor) {
		
		Map<String, ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<JavaFieldGetSet>();
		for(ColumnInfo c:columns.values()) {
			javaFields.add(createFieldGetSetSRC(c, convertor));
		}
		
		StringBuilder src = new StringBuilder();
		//����package���
		src.append("package cn.zhang.SORM." + DBManager.getConf().getPoPackage() + ";\n\n");
		//����import���
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		//�������������
		src.append("public class " + StringUtils.firstchar2UpperCase(tableInfo.getTname()) + " {\n");
		//���������б�
		for(JavaFieldGetSet f:javaFields) {
			src.append(f.getFieldInfo());
		}
		src.append("\n");
		//����get�����б�
		for(JavaFieldGetSet f:javaFields) {
			src.append(f.getGetInfo());
		}
		src.append("\n");
		//����set�����б�
		for(JavaFieldGetSet f:javaFields) {
			src.append(f.getSetInfo());
		}
		src.append("\n");
		//�������������
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
			System.out.println("������" + tableInfo.getTname() + "��Ӧ��java�ࣺ" + 
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
	 * ����
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
