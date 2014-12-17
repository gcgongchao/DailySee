package com.dailysee.bean;

import java.util.List;

public class Preferential {
	public long preferentialId;//			优惠ID	
	public String title;//			优惠标题	
	public String content;//			优惠内容	
	public double totalAmt;//			优惠总价	元
	public long merchantId;//			所属商户ID	
	public String companyName;//			商户名称	
	public String status;//			优惠状态	
	public String statusName;//			状态名称	
	public long startDate;//			开始时间	
	public long endDate;//			结束时间	
	public String createDate;//			创建时间	
	public String logoUrl;//			头像	
	public List<Image> imgs;//（相册类图片）			优惠图片	
}
