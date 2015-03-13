package com.dailysee.util;

import android.content.Intent;

public class Constants {
	
	private static final String PREFIX = "com.dailysee.";
	
	public static final String REFRESH_MEMBER_DETAIL = PREFIX + "REFRESH_MEMBER_DETAIL";

	public static final String CUSTOMER_SERVICES_PHONE = "0755 8613 3999";

	public static final int ADD_PICTURE = 10001;

	public static final int DEFAULT_PROVINCE = 440000;// 深圳
	public static final int DEFAULT_CITY = 440300;// 深圳

	public static final String LOGOUT = "logout";

	public static final String ACTION_PUSH = PREFIX + "PUSH";

	public interface Filter {
		public static final int RECOMMEND = 10001;
		public static final int NEARBY = 10002;
	}

	public interface Product {
		public static final int DRINKS = 10001;
		public static final int SNACK = 20001;
	}
	
	public interface Status {
		public static final String UNCHECK = "UNCHECK";
		public static final String REJECT = "REJECT";
		public static final String PASS = "PASS";
	}
	
	public interface Type {
		public static final int ROOM = 1001;
		public static final int DRINKS = 10001;
		public static final int SNACK = 20001;
		public static final int SMOKE_TEA = 30001;
		public static final int RECOMMEND = 40001;
	}
	
	public interface Sex {
		public static final String ALL = "";
		public static final String MEN = "MAN";
		public static final String WOMEN = "WOMEN";
	}
	
	public interface TipType {
		public static final String MERCHANT = "MERCHANT";//商户类信息, 
		public static final String GOVERNMENT = "GOVERNMENT";//政府公告, 
		public static final String ACTIVITY = "ACKTIVITY";//优惠活动
	}
	
	public interface From {
		public static final int MERCHANT = 1001;
		public static final int SALE = 10002;
		public static final int GIFT = 10003;
		public static final int CONSULTANT = 10004;
	}
	
	public interface OrderFilter {
		String ALL = "";
		String WAIT_PAY = "WAIT_PAY";//等待付款; 
		String WAIT_ACCEPT_CONFIRM = "WAIT_ACCEPT_CONFIRM";//已支付,待接单确认
		String WAIT_CONFIRM_GOODS = "WAIT_CONFIRM_GOODS";//已接单,待确认消费, 
		String WAIT_COMPLETE = "WAIT_COMPLETE";// 服务进行中
		String REFUND_INPROCESS = "REFUND_INPROCESS";// 退款待处理,
		String REFUND = "REFUND";//退款
		String SUCCEED = "SUCCEED";// 交易成功    
		String CLOSE = "CLOSE";//交易关闭
	}
	
	public interface Payment {
		String UP = "100001";//银联: 100001,
		String ALIPAY = "100002";//支付宝:100002
		String WECHAT = "100003";//微信: 100003
	}
	
}
