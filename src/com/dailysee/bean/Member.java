package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Member implements Serializable {
	public long informationId;
	public String name;
	public int age;
	public int sex;
	public String birthday;
	public String email;
	public String workType;
	public String addr;
	public String mobile;
	public String introduction;
	public List<Image> imgs;// （证件类图片）
}
