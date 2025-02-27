package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class ProductType implements Serializable {
	public int productTypeId;//			房间类型记录id	
	public long merchantId;//			商户详情记录ID	
	public String companyName;//			所属商户名称	
	public String name;//			房间类型名	
	public String status;//			房间类型状态	ENABLE：使用, UNABLE：停用
	public String statusName;//			房间类型状态名称	
	public String useDesc;//			房间类型描述	
	public String logoUrl;//			头像	
	public List<Image> imgs;//（相册类图片）		
	public int parentId;
}
