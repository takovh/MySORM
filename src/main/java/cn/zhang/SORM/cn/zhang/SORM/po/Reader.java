package cn.zhang.SORM.cn.zhang.SORM.po;

public class Reader {
	private Double balance;
	private String sex;
	private String name;
	private String tel;
	private String card_id;
	private Integer age;

	public Double getBalance(){
		return balance;
	}
	public String getSex(){
		return sex;
	}
	public String getName(){
		return name;
	}
	public String getTel(){
		return tel;
	}
	public String getCard_id(){
		return card_id;
	}
	public Integer getAge(){
		return age;
	}

	public void setBalance(Double balance){
		this.balance=balance;
	}
	public void setSex(String sex){
		this.sex=sex;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setTel(String tel){
		this.tel=tel;
	}
	public void setCard_id(String card_id){
		this.card_id=card_id;
	}
	public void setAge(Integer age){
		this.age=age;
	}

}