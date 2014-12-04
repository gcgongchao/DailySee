package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
	public long productId;//				商品记录ID
	public long merchantId;//	Long	是	商户信息ID	登录成功返回信息中的belongObjId
    public String companyName;
	public String name;//	String	是	商品名称	
	public long productType;//	Long	是	商品所属类型ID	选择所属商品类型记录的productTypeId
	public String productTypeName;// 商品类型名称	
    public String status;
    public String statusName;
	public double price;//	Double	是	实际价格	单位：分
	public double ttPrice;//	Double	是	天天价格	单位：分
	public long totalCnt;//	Long	是	商品总数	整数
	public long validCnt;//	Long	是	商品可用数	整数
	public String proDesc;//	String	否	商品描述	
	public long creator;//	Long	是	创建者ID	登录成功的memberId
	public String logoUrl;//			头像	
	public List<Image> imgs;//（相册类图片）	
	
	public boolean isUp() {
		return "UP".equalsIgnoreCase(status);
	}
	
}
