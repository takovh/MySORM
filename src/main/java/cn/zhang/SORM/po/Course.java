package cn.zhang.SORM.po;

import java.sql.*;
import java.util.*;

public class Course {
	private Integer C_No;
	private String C_Name;

	public Integer getC_No(){
		return C_No;
	}
	public String getC_Name(){
		return C_Name;
	}

	public void setC_No(Integer C_No){
		this.C_No=C_No;
	}
	public void setC_Name(String C_Name){
		this.C_Name=C_Name;
	}

}