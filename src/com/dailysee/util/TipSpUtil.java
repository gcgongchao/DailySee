package com.dailysee.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TipSpUtil {
	private static final String NAME = "tip_preferences";

	public static TipSpUtil instance = null;

	private Context context;
	
	private TipSpUtil(Context context) {
		this.context = context;
	}

	public static TipSpUtil getInstance(Context context) {
		Context applicationContext = context.getApplicationContext();
		if (null == instance || instance.context != applicationContext) {
			instance = new TipSpUtil(context);
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
	}

	public boolean isRead(long tipId) {
		return getSp().getBoolean("t" + tipId, false);
	}
	
	public void setRead(long tipId) {
		getEdit().putBoolean("t" + tipId, true).commit();
	}
	
}
