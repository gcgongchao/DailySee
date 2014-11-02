package com.dailysee.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.util.Md5Utils;

public class HomeFragment extends Fragment {

	protected static final String TAG = HomeFragment.class.getSimpleName();
	private TextView text;

	public HomeFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout, null);
		text = (TextView) v.findViewById(R.id.text);
		if (getArguments() != null) {
			//
			try {
				String value = getArguments().getString("key");
				text.setText("Current Tab is: " + value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				request();
			}
		});
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void request() {
		// Tag used to cancel the request
		String tag_str_req = "json_str_req";

		String url = "http://120.24.77.231/ebp/cgi/gateway.htm";

		final ProgressDialog pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading...");
		pDialog.show();
		
		StringRequest strReq = new StringRequest(Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, response);
				pDialog.hide();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				pDialog.hide();
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("app", "MEMBER");
				params.put("mtd", "com.guocui.tty.api.web.PhoneChkNumControllor.createPhoneChkNum");
				params.put("loginId", "13424269212");
				
				Object[] array = params.keySet().toArray();
				Arrays.sort(array);// 默认升序排列，array为参数组成的数组
		        StringBuffer sb = new StringBuffer();
		        for (Object k : array) {
		            String k_ = String.valueOf(k);
		            if (!k_.equals("file") && !k_.equals("sign")) {
		                String value = params.get(k);
		                sb.append(k_).append("=").append(value);
		            }
		        }
		        String signString = sb.toString();
		        String newSign = Md5Utils.encryptMD5(signString, "fdsa2324323d");//key为客户端对应私钥

				params.put("sign", newSign);
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_str_req);
	}

}
