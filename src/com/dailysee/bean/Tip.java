package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Tip implements Serializable {
	public long tipId;//			讯息记录ID	
	public String title;//			主题	
	public String content;//			内容	
	public long merchantId;//			商户ID	当时商户类信息时候可通过此ID调用商户详情接口进入该商户的界面
	public String companyName;
	public String statusName;//			状态名	转义后的状态名称：已过期，未生效，已生效
	public String status;//			状态	
	public String tipType;
	public String tipTypeName;
	public String beginDate;//			生效日期	
	public int validDays;//			生效天数	
	public long createUser	;//		创建者	
	public long createDate;//			创建时间	
	public String logoUrl;//			头像	
	public List<Image> imgs;//（相册类图片）			讯息图片	
}
