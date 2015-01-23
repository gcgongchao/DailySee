package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Ad implements Serializable {
	public long adId;//			广告记录ＩＤ	
	public String subject;//			主题	
	public String status;//			状态	
	public String statusName;//			状态名	
	public String createDate;//			创建时间	
	public String createUser;//			创建人	
	public String remark;//			备注	
	public String logoUrl;//			头像	
	public List<Image> imgs;//（相册类图片）			广告图片	
	public long merchantId;//			广告所属商户ID	
	public String adType;//			广告类型	TIP("讯息"), PREFERENTIAL("优惠")
	public String adTypeName;//			广告类型名称	
	public long belongAdId;//			所属信息ID	如讯息类型，则为讯息记录ID，如果为优惠则为优惠记录ID
	public String url;//			跳转URL	任意url地址
}
