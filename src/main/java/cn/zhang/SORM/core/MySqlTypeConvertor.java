package cn.zhang.SORM.core;

/**
 * Mysql数据类型和java数据类型的转换
 * @author tako_
 *
 */
public class MySqlTypeConvertor implements TypeConvertor {

	@Override
	public String databaseType2JavaType(String columnType) {
		//varchar-->String
		if("varchar".equalsIgnoreCase(columnType)
				||"char".equalsIgnoreCase(columnType)
				||"enum".equalsIgnoreCase(columnType)) {
			return "String";
		}else if("int".equalsIgnoreCase(columnType)
				||"tinyint".equalsIgnoreCase(columnType)
				||"smallint".equalsIgnoreCase(columnType)
				||"integer".equalsIgnoreCase(columnType)) {
			return "Integer";
		}else if("bigint".equalsIgnoreCase(columnType)) {
			return "Long";
		}else if("double".equalsIgnoreCase(columnType)
				||"float".equalsIgnoreCase(columnType)
				||"decimal".equalsIgnoreCase(columnType)) {
			return "Double";
		}else if("clob".equalsIgnoreCase(columnType)) {
			return "java.sql.Clob";
		}else if("blob".equalsIgnoreCase(columnType)) {
			return "java.sql.Blob";
		}else if("date".equalsIgnoreCase(columnType)) {
			return "java.sql.Date";
		}else if("time".equalsIgnoreCase(columnType)) {
			return "java.sql.Time";
		}else if("timestamp".equalsIgnoreCase(columnType)) {
			return "java.sql.Timestamp";
		}
		return null;
	}

	@Override
	public String javaType2DatabaseType(String javaDataType) {
		// TODO Auto-generated method stub
		return null;
	}

}
