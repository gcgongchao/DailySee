package com.dailysee.bean;

import java.io.Serializable;

public class ProductOrder implements Serializable {
	
	public long proObjId;//				商品记录ID
    public String proType;
	public String name;//	String	是	商品名称	
	public double price;//	Double	是	实际价格	单位：分
	public int quantity;// 选购数量
	public String remark;
	
	public ProductOrder() {
	}
	
	public ProductOrder(RoomType roomType) {
		name = roomType.name;
		proType = "ROOM";
		proObjId = roomType.roomTypeId;
		price = 0;
		quantity = 1;
	}

	public ProductOrder(Product product) {
		name = product.name;
		proType = "PRODUCT";
		proObjId = product.productId;
		price = product.ttPrice;
		quantity = product.count;
	}
	
}
