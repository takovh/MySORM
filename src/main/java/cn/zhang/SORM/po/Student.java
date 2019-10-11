package cn.zhang.SORM.po;

import java.sql.*;
import java.util.*;

public class Student {
	private String uname;
	private Integer id;
	private String pwd;

	public String getUname(){
		return uname;
	}
	public Integer getId(){
		return id;
	}
	public String getPwd(){
		return pwd;
	}

	public void setUname(String uname){
		this.uname=uname;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public void setPwd(String pwd){
		this.pwd=pwd;
	}

}