package com.dailysee.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dailysee.bean.Member;
import com.google.gson.Gson;

public class SpUtil {
	private static final String NAME = "preferences";

	public static SpUtil instance = null;

	private Context context;
	
	private SpUtil(Context context) {
		this.context = context;
	}

	public static SpUtil getInstance(Context context) {
		Context applicationContext = context.getApplicationContext();
		if (null == instance || instance.context != applicationContext) {
			instance = new SpUtil(context);
		}
		return instance;
	}

	private SharedPreferences sp;

	public SharedPreferences getSp() {
		if (sp == null)
			sp = context.getSharedPreferences(getSpFileName(),
					Context.MODE_PRIVATE);
		return sp;
	}

	public Editor getEdit() {
		return getSp().edit();
	}

	private String getSpFileName() {
		return NAME;
	}

	public void logout(){
		getEdit().clear().commit();
		Utils.setBind(context, false);
	}

	public boolean isLogin() {
		long memberId = getMemberId();
		String token = getToken();
		String loginId = getLoginId();
		return memberId > 0 && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(loginId);
	}

	public void setMemberId(long memberId) {
		getEdit().putLong("memberId", memberId).commit();
	}

	public long getMemberId() {
		return getSp().getLong("memberId", 0);
	}
	
	public String getMemberIdStr() {
		long memberId = getMemberId();
		return Long.toString(memberId);
	}

	public void setBelongObjId(long belongObjId) {
		getEdit().putLong("belongObjId", belongObjId).commit();
	}
	
	public long getBelongObjId() {
		return getSp().getLong("belongObjId", 0);
	}
	
	public String getBelongObjIdStr() {
		long belongObjId = getBelongObjId();
		return Long.toString(belongObjId);
	}

	public void setLoginId(String loginId) {
		getEdit().putString("loginId", loginId).commit();
	}

	public String getLoginId() {
		return getSp().getString("loginId", "");
	}

	public void setMaster(String master) {
		getEdit().putString("master", master).commit();
	}

	public void setStatus(String status) {
		getEdit().putString("status", status).commit();
	}
	
	public String getStatus() {
		return getSp().getString("status", "");
	}

	public void setToken(String token) {
		getEdit().putString("token", token).commit();
	}
	
	public String getToken() {
		return getSp().getString("token", "");
	}
	
	public boolean isFirstLogin() {
		return getSp().getBoolean("isFirstLogin", true);
	}

	public void setFirstLogin(boolean isFirstLogin) {
		getEdit().putBoolean("isFirstLogin", isFirstLogin).commit();
	}

	public boolean isSetAccount() {
		return getSp().getBoolean("isSetAccount", false);
	}

	public void setAccount(boolean isSetAccount) {
		getEdit().putBoolean("isSetAccount", isSetAccount).commit();
	}

	public boolean isMaterialApproved() {
		String status = getStatus();
		return Constants.Status.PASS.equals(status);// || getSp().getBoolean("isMaterialApproved", false);
	}

//	public void setMaterialApproved() {
//		getEdit().putBoolean("isMaterialApproved", true).commit();
//	}

	public void setName(String name) {
		getEdit().putString("name", name).commit();
	}
	
	public String getName() {
		return getSp().getString("name", "");
	}

	public void setAvatar(String avatar) {
		getEdit().putString("avatar", avatar).commit();
	}
	
	public String getAvatar() {
		return getSp().getString("avatar", "");
	}

	public void setMobile(String mobile) {
		getEdit().putString("mobile", mobile).commit();
	}
	
	public String getMobile() {
		return getSp().getString("mobile", "");
	}

	public void setWorkType(String workType) {
		getEdit().putString("workType", workType).commit();
	}
	
	public String getWorkType() {
		return getSp().getString("workType", "");
	}
	
	public boolean isOnline() {
		String workType = getWorkType();
		return "online".equalsIgnoreCase(workType);
	}

	public void setMember(Member member) {
		String json = null;
		if (member == null) {
			json = "{}";
		} else {
			setName(member.name);
			setAvatar(member.logoUrl);
			setMobile(member.mobile);
//			setStatus(member.status);
//			setWorkType(member.workType);
			
			json = new Gson().toJson(member);
		}
		getEdit().putString("member", json).commit();
	}
	
	public Member getMember() {
		String json = getSp().getString("member", "");
		Member member = new Gson().fromJson(json, Member.class);
		return member;
	}

	public String getHomeRefreshTime() {
		return getSp().getString("home_refresh_time", "从未更新");
	}
	
	public void setHomeRefreshTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(date);
		getEdit().putString("home_refresh_time", time).commit();
	}

	public String getMessageRefreshTime() {
		return getSp().getString("message_refresh_time", "从未更新");
	}
	
	public void setMessageRefreshTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(date);
		getEdit().putString("message_refresh_time", time).commit();
	}

	public String getOrderRefreshTime() {
		return getSp().getString("order_refresh_time", "从未更新");
	}
	
	public void setOrderRefreshTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(date);
		getEdit().putString("order_refresh_time", time).commit();
	}

	public void setLat(double lat) {
		getEdit().putString("lat", Double.toString(lat)).commit();
	}
	
	public String getLat() {
		return getSp().getString("lat", "0");
	}
	
	public double getLatD() {
		String lat = getLat();
		return Double.parseDouble(lat);
	}

	public void setLng(double lng) {
		getEdit().putString("lng", Double.toString(lng)).commit();
	}
	
	public String getLng() {
		return getSp().getString("lng", "0");
	}
	
	public double getLngD() {
		String lng = getLng();
		return Double.parseDouble(lng);
	}

	public void setProvince(String province) {
		getEdit().putString("province", province).commit();
	}

	public String getProvince() {
		return getSp().getString("province", "广东省");
	}

	public void setCity(String city) {
		getEdit().putString("city", city).commit();
	}

	public String getCity() {
		return getSp().getString("city", "深圳市");
	}

	public void setArea(String area) {
		getEdit().putString("area", area).commit();
	}

	public String getArea() {
		return getSp().getString("area", "");
	}

	public void setProvinceId(int provinceId) {
		getEdit().putInt("provinceId", provinceId).commit();
	}
	
	public int getProvinceId() {
		return getProvinceId(Constants.DEFAULT_PROVINCE);
	}
	
	public int getProvinceId(int defaultValue) {
		return getSp().getInt("provinceId", defaultValue);
	}

	public void setCityId(int cityId) {
		getEdit().putInt("cityId", cityId).commit();
	}
	
	public int getCityId() {
		return getCityId(Constants.DEFAULT_CITY);
	}
	
	public int getCityId(int defaultValue) {
		return getSp().getInt("cityId", defaultValue);
	}

	public void setBDUserId(String userId) {
		getEdit().putString("bdUserId", userId).commit();
	}
	
	public String getBDUserId() {
		return getSp().getString("bdUserId", "");
	}

	public void setBDChannelId(String channelId) {
		getEdit().putString("bdChannelId", channelId).commit();
	}
	
	public String getBDChannelId() {
		return getSp().getString("bdChannelId", "");
	}

	public void setPurpose(String purpose) {
		getEdit().putString("purpose", purpose).commit();
	}
	
	public String getPurpose() {
		return getSp().getString("purpose", "");
	}

	public void setNewCommentCount(int count) {
		getEdit().putInt("newCommentCount", count).commit();
	}
	
	public int getNewCommentCount() {
		return getSp().getInt("newCommentCount", 0);
	}

	public void setNewConfirmOrderCount(int count) {
		getEdit().putInt("newConfirmOrderCount", count).commit();
	}
	
	public int getNewConfirmOrderCount() {
		return getSp().getInt("newConfirmOrderCount", 0);
	}

	public void setNewMsgCount(int count) {
		getEdit().putInt("newMsgCount", count).commit();
	}
	
	public int getNewMsgCount() {
		return getSp().getInt("newMsgCount", 0);
	}

	public void setNewRefundResultCount(int count) {
		getEdit().putInt("newRefundResultCount", count).commit();
	}
	
	public int getNewRefundResultCount() {
		return getSp().getInt("newRefundResultCount", 0);
	}
	
}
