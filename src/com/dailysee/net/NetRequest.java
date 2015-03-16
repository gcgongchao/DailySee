package com.dailysee.net;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dailysee.AppController;
import com.dailysee.util.Md5Utils;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;

public class NetRequest {

	public static final String SERVER_URL = "http://120.24.77.231/ebp/cgi/gateway.json";
	public static final String UPLOAD_SERVER_URL = "http://120.24.77.231/ebp/filemanager/upload.htm";
	public static final String SECRET_KEY = "fdsa2324323d";
	public static final String APP = "MEMBER";
	
	public static final int PAGE_SIZE = 20;
	public static final int PAGE_LARGE_SIZE = 100;

	private static final String TAG = NetRequest.class.getSimpleName();

	private static NetRequest instance_;
	private Context mContext;

	public NetRequest(Context context) {
		this.mContext = context;
	}

	public synchronized static NetRequest getInstance(Context context) {
		Context applicationContext = context.getApplicationContext();
		if (instance_ == null || instance_.mContext != applicationContext) {
			instance_ = new NetRequest(applicationContext);
		}

		return instance_;
	}

	public void get(final Callback callback) {
		get(callback, null);
	}

	public void get(final Callback callback, String tag) {
		executeNetworkInvoke(Method.GET, callback, tag, false);
	}

	public void get(final Callback callback, String tag, boolean silent) {
		executeNetworkInvoke(Method.GET, callback, tag, silent);
	}

	public void post(final Callback callback) {
		post(callback, null);
	}

	public void post(final Callback callback, String tag) {
		executeNetworkInvoke(Method.POST, callback, tag, false);
	}

	public void post(final Callback callback, String tag, boolean silent) {
		executeNetworkInvoke(Method.POST, callback, tag, silent);
	}

	public void executeNetworkInvoke(int method, final Callback callback, String tag, boolean silent) {

		if (!Utils.isNetworkValid(mContext)) {
			onFailed(callback, "无网络，请检查您的网络设置", silent);
			return;
		}

		String url = SERVER_URL;// 添加灰度的HTTP HOST和PORT
		Log.d(TAG, "request url: " + url);

		if (!TextUtils.isEmpty(tag)) {// true 取消上一次的请求
			cancelRequest(tag);
		}

		onPreExecute(callback);

		StringRequest strReq = null;
		if (method == Method.GET) {
			url = convertParams(url, callback.getParams());
			strReq = new StringRequest(method, url, new SuccessResponse(callback, silent), new ErrorResponse(callback, silent));
		} else if (method == Method.POST) {
			strReq = new StringRequest(method, url, new SuccessResponse(callback, silent), new ErrorResponse(callback, silent)) {
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> params = callback.getParams();
					params.put("app", APP);
					
					String loginId = SpUtil.getInstance(mContext).getLoginId();
					params.put("loginName", loginId);

					String newSign = genSign(params);
					params.put("sign", newSign);
					
					Log.d(TAG, "request params: " + params.toString());
					return params;
				}
			};
		}

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag);
	}

	private String convertParams(String url, Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return url;
		}
		
		params.put("app", APP);
		
		String loginId = SpUtil.getInstance(mContext).getLoginId();
		params.put("loginName", loginId);
		
		String newSign = genSign(params);
		params.put("sign", newSign);
		
		StringBuffer sb = new StringBuffer();
		sb.append(url).append("?");
		
		Object[] array = params.keySet().toArray();
		for (Object key : array) {
			String k = String.valueOf(key);
			String value = params.get(k);
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(k).append("=").append(value);
		}
		return sb.toString();
	}

	public static String genSign(Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return null;
		}
		
		Object[] array = params.keySet().toArray();
		if (array != null) {
			Arrays.sort(array);// 默认升序排列，array为参数组成的数组
		}
		StringBuffer sb = new StringBuffer();
		for (Object k : array) {
			String k_ = String.valueOf(k);
			if (!k_.equals("file") && !k_.equals("sign")) {
				String value = params.get(k);
//				if (!TextUtils.isEmpty(value)) {
					sb.append(k_).append("=").append(value);
//				}
			}
		}
		
		String sign = sb.toString();
//		Log.d(TAG, "sign data: " + sign);
		String newSign = Md5Utils.encryptMD5(sign, SECRET_KEY);// key为客户端对应私钥
//		Log.d(TAG, "sign result: " + newSign);
		
		return newSign;
	}

	private class SuccessResponse implements Response.Listener<String> {

		private Callback callback;
		private boolean silent;

		public SuccessResponse(Callback callback, boolean silent) {
			this.callback = callback;
			this.silent = silent;
		}

		@Override
		public void onResponse(String json) {
			Log.d(TAG, "response: " + json.toString());
			onFinished(callback);

			try {
				JSONObject jsonObj = new JSONObject(json);
				String code = jsonObj.optString("code");
				String message = jsonObj.optString("message");
				
				if ("0000".equals(code)) {
					BaseResponse response = new BaseResponse(json, code, message, jsonObj);
					onSuccess(callback, response);
				} else {
					onFailed(callback, "(" + code + ") " + message, silent);
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailed(callback, "数据格式错误", silent);
			}
		}
	}

	private class ErrorResponse implements Response.ErrorListener {

		private Callback callback;
		private boolean silent;

		public ErrorResponse(Callback callback, boolean silent) {
			this.callback = callback;
			this.silent = silent;
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
			onFinished(callback);
			
			String msg = error.getMessage();
			if (error instanceof ServerError) {
				msg = "系统走神了，请稍后重试";
			} else if (error instanceof TimeoutError) {
				msg = "请求超时，请稍后重试";
			} else if (error instanceof NetworkError) {
				msg = "网络异常，请稍后重试";
			} else if (error instanceof NoConnectionError) {
				msg = "网络状态不好，请稍后重试";
			} else if (error instanceof ParseError) {
				msg = "系统错误，请稍后重试";
			} else if (TextUtils.isEmpty(msg)) {
				msg = "网络异常，请稍后重试";// 默认错误提示
			}
			
			onFailed(callback, msg, silent);
		}
	}

	public void onPreExecute(Callback callback) {
		if (callback != null)
			callback.onPreExecute();
	}

	public void onSuccess(Callback callback, BaseResponse response) {
		if (callback != null)
			callback.onSuccess(response);
	}

	public void onFinished(Callback callback) {
		if (callback != null)
			callback.onFinished();
	}

	public void onFailed(Callback callback, String msg, boolean silent) {
		if (!TextUtils.isEmpty(msg) && !silent) {
			toShowToast(msg);
		}
		if (callback != null)
			callback.onFailed(msg);
	}

	protected void toShowToast(final Exception e) {
		if (e != null) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (!TextUtils.isEmpty(msg)) {
				toShowToast(msg);
				return;
			}
		}
	}

	protected void toShowToast(final String msg) {
		Utils.showMsg(mContext, msg);
	}

	/**
	 * 取消请求TAG
	 * 
	 * @param tag
	 */
	public void cancelRequest(final String tag) {
		AppController.getInstance().cancelPendingRequests(tag);
	}

}
