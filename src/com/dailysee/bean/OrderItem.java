package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class OrderItem implements Serializable {
	public long itemId;//			订单商品项纪录ID	
	public long orderId;//			订单ID	
	public String name;//			商品名称	
	public String proType;//			商品类型	ROOM(“房间”), PRODUCT(“商品”)
	public String proTypeName;//			商品类型名称	
	public long proObjId;//			在商品表中ID	
	public int quantity;//			数量	
	public double price;//			商品单价	元
	public String remark;//			商品备注	
	public List<Image> Imgs;//（相册类图片）				
}
