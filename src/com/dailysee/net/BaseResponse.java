package com.dailysee.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BaseResponse<T> {

	public String code;
	
	public String message;
	
	public String response;
	
	public JSONObject parent;
	
	public BaseResponse() {
		
	}
	
	public BaseResponse(String response, String code, String message, JSONObject parent) {
		this.response = response;
		this.code = code;
		this.message = message;
		this.parent = parent;
	}
	
	public JSONObject getParent() {
		return parent;
	}
	
	public JSONObject getData() {
		return parent.optJSONObject("data");
	}
	
	public String getDataStr() {
		JSONObject jsonObj = getData();
		
		String json = "";
		if (jsonObj != null) {
			json = jsonObj.toString();
		}
		return json;
	}
	
	public T getResponse(TypeToken<T> token) {
		Gson gson = new Gson();
		String json = getDataStr();
		return gson.fromJson(json, token.getType());
	}
	
	public ArrayList<T> getListResponse(TypeToken<ArrayList<T>> token) {
		Gson gson = new Gson();
		String json = getDataArrStr();
		return gson.fromJson(json, token.getType());
	}
	
	public JSONArray getDataArr() {
		return parent.optJSONArray("data");
	}
	
	public String getDataArrStr() {
		JSONArray jsonArr = getDataArr();
		
		String json = "";
		if (jsonArr != null) {
			json = jsonArr.toString();
		}
		return json;
	}
	
}
