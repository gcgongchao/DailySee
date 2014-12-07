package com.dailysee.util;

public class Constants {
	
	private static final String PREFIX = "com.dailysee.merchant.";
	
	public static final String REFRESH_MEMBER_DETAIL = PREFIX + "REFRESH_MEMBER_DETAIL";

	public static final String CUSTOMER_SERVICES_PHONE = "0755 8888 8888";

	public static final int ADD_PICTURE = 10001;

	public static final int DEFAULT_CITY = 440300;// 深圳

	public static final String LOGOUT = "logout";

	public interface Filter {
		public static final int RECOMMEND = 10001;
		public static final int NEARBY = 10002;
	}
	
	public interface Status {
		public static final String UNCHECK = "UNCHECK";
		public static final String REJECT = "REJECT";
		public static final String PASS = "PASS";
	}
	
	public interface TYPE {
		public static final int ROOM = 1001;
		public static final int DRINKS = 10001;
		public static final int SNACK = 20001;
	}
	
	public interface Sex {
		public static final String MEN = "MAN";
		public static final String WOMEN = "WOMEN";
	}
	
	public interface TipType {
		public static final String MERCHANT = "MERCHANT";//商户类信息, 
		public static final String GOVERNMENT = "GOVERNMENT";//政府公告, 
		public static final String ACTIVITY = "ACKTIVITY";//优惠活动
	}
	
}
