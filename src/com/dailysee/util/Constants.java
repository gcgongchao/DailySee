package com.dailysee.util;

public class Constants {
	
	private static final String PREFIX = "com.dailysee.merchant.";
	
	public static final String REFRESH_MEMBER_DETAIL = PREFIX + "REFRESH_MEMBER_DETAIL";

	public static final String CUSTOMER_SERVICES_PHONE = "0755 8888 8888";

	public static final int ADD_PICTURE = 10001;
	
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
	
}
