package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

import android.text.TextUtils;

public class Consultant implements Serializable {
	public long counselorId;
	public String name;
	public String nick;
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
	public String signature;
	public String introduction;
	public String status;
	public String adjustRemark;
	public String sfUrl;
	public String scUrl;
	public String age;//	String	否	50	年龄
	public String sex;//	String	否	10	性别（BOY，GRIL）
	public String height;//	String	否	10	身高（厘米）
	public String three;//	String	否	30	三维（逗号隔开）
	public String email;//	String	否	100	邮件
	public String logoUrl;
	public List<Image> imgs;// （证件类图片）
	public double worth;
	
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
	
	public String getName() {
		if (TextUtils.isEmpty(nick)) {
			return name;
		}
		return nick;
	}
	
	public String getSignature() {
		String sign = signature;
		if (TextUtils.isEmpty(sign)) {
			sign = "这个人很懒，什么都没有留下。";
		}
		return sign;
	}
}
