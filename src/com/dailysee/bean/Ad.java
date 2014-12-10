package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Ad implements Serializable {
	public long adId;//			广告记录ＩＤ	
	public String ubject;//			主题	
	public String status;//			状态	
	public String statusName;//			状态名	
	public String createDate;//			创建时间	
	public long createUser;//			创建人	
	public String remark;//			备注	
	public String logoUrl;//			头像	
	public List<Image> imgs;//（相册类图片）			广告图片	
}
