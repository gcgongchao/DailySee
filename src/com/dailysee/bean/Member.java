package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

import com.dailysee.util.Constants;

public class Member implements Serializable {
	public long informationId;
	public String name;
	public int age;
	public String sex;
	public String birthday;
	public String email;
	public String workType;
	public String addr;
	public String mobile;
	public String introduction;
	public String logoUrl;
	public List<Image> imgs;// （证件类图片）
	
	public String getSex() {
		String sexStr = "";
		if (Constants.Sex.MEN.equals(sex)) {
			sexStr = "男";
		} else if (Constants.Sex.WOMEN.equals(sex)) {
			sexStr = "女";
		}
		return sexStr;
	}
}
