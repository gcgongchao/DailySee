package com.dailysee.bean;

import java.io.Serializable;

import android.text.TextUtils;

public class Merchant implements Serializable {
	public long merchantId;
	public String name;
	public String prov;
	public String city;
	public String area;
	public String landmark;
	public String workType;
	public double jd;
	public double wd;
	public String redu;
	public String addr;
	public double feeRate;
	public String contact;
	public String mobile;
	public String introduction;
	public String status;
	public String adjustRemark;
	public String yyUrl;
	public String sfUrl;
	public String scUrl;
	public String logoUrl;
//	public List<String> imgs;// （证件类图片）
	
	public String getRegion() {
		String region = null;
		if (!TextUtils.isEmpty(prov)) {
			region = prov;
			if (!TextUtils.isEmpty(city)) {
				region += city;
				if (!TextUtils.isEmpty(area)) {
					region += area;
				}
			}
		}
		return region;
	}
}
