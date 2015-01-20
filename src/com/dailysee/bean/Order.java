package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
	public long orderId;//			订单ID	
	public long parentOrderId; // 父订单ID
	public String businessType;//			业务类型	CONSUME(“消费”), GIFT(“赠送”),  PREFER(“优惠”), SERVICE(“顾问服务”);
	public String businessTypeName;//			业务类型名称
	public String subBusinessType;
	public String subBusinessTypeName;
	public long merchantId;//			商户详情ID	
	public String sellerName;//			商户名称	
	public long memberId	;//		会员登录信息ID	
	public String buyerName;//			会员名称	
	public String orderStatus;//			订单状态	
	public String orderSelStatusName;//
	public String orderBuyStatusName;//			订单状态名称	会员取orderBuyStatusName的值 商户和顾问取orderSelStatusName的值
	public double amount;//			订单金额	元
	public double fee;//			小费	元
	public String roomNo;//			房号	赠送单时候才有
	public String mobile;//			接收短信手机	
	public long createDate;
	public long bookDate;//			预定日期	
	public String remark;//			下单备注/赠送备注	
	public int buyHours;
	public String startDate;
	public String paymentId;
	
	public List<OrderItem> items;
}
