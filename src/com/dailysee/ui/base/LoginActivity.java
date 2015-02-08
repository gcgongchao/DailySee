package com.dailysee.ui.base;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dailysee.MainActivity;
import com.dailysee.R;
import com.dailysee.bean.Member;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.user.EditProfileActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;
import com.google.gson.reflect.TypeToken;

public class LoginActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = LoginActivity.class.getSimpleName();
	private Button btnCommit;
	private EditText etPhone;
	private EditText etCode;
	private TextView btnToRegister;
	private TextView btnGetCode;
	protected String mCheckCode;
	protected String mCheckKey;
	private String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			from = intent.getStringExtra("from");
		}
		
		setTitle("登录");
		setUp();
	}

	@Override
	public void onFindViews() {
		etPhone = (EditText) findViewById(R.id.et_phone);
		etCode = (EditText) findViewById(R.id.et_code);
		btnCommit = (Button) findViewById(R.id.btn_commit);
		btnGetCode = (TextView) findViewById(R.id.btn_get_code);
		btnToRegister = (TextView) findViewById(R.id.btn_to_register);
	}

	@Override
	public void onInitViewData() {
	}

	@Override
	public void onBindListener() {
		btnCommit.setOnClickListener(this);
		btnGetCode.setOnClickListener(this);
		btnToRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			if (checkPhone() && checkCode()) {
				requestLogin();
			}
			break;
		case R.id.btn_get_code:
			if (checkPhone()) {
				etCode.setText("");
				etCode.requestFocus();
				requestCode();
			}
			break;
		case R.id.btn_to_register:
			// Intent intent = new Intent(this, RegisterActivity.class);
			// startActivity(intent);
			break;
		}
	}

	private void requestCode() {
		// 获取验证码
		final String phone = getPhone();

		// Tag used to cancel the request
		String tag = "tag_request_code";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				JSONObject data = response.getData();
				mCheckKey = data.optString("key");
				mCheckCode = data.optString("value");
				Toast.makeText(LoginActivity.this, "验证码已发送到您的手机 " + mCheckCode, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在获取验证码...");
			}

			@Override
			public void onFinished() {
				toCloseProgressMsg();
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.PhoneChkNumControllor.createPhoneChkNum");
				params.put("loginId", phone);
				return params;
			}
		}, tag);
	}

	private boolean checkPhone() {
		String phone = getPhone();

		boolean check = false;
		if (TextUtils.isEmpty(phone)) {
			showToast("请输入手机号码");
		} else if (phone.contains(" ")) {
			showToast("手机号码不能包含空格");
		} else if (phone.length() != 11) {
			showToast("手机号码长度不对");
		} else {
			check = true;
		}

		return check;
	}

	public String getPhone() {
		return etPhone.getText().toString();
	}

	private boolean checkCode() {
		String code = getCode();

		boolean check = false;
		if (TextUtils.isEmpty(code)) {
			showToast("请输入验证码");
		} else if (code.contains(" ")) {
			showToast("验证码不能包含空格");
		} else if (code.length() != 6) {
			showToast("验证码由6位数字组成");
		} else if (!code.equals(mCheckCode)) {
			showToast("输入的验证码不正确");
		} else {
			check = true;
		}

		return check;
	}

	public String getCode() {
		return etCode.getText().toString();
	}

	private void requestLogin() {
		final String phone = getPhone();
		final String code = getCode();

		// Tag used to cancel the request
		String tag = "tag_request_login";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				JSONObject data = response.getData();
				try {
					boolean isFirstLogin = "Y".equalsIgnoreCase(data.getString("isFirstLogin"));
					
					SpUtil sp = SpUtil.getInstance(getActivity());
					sp.setMemberId(data.getLong("memberId"));
					sp.setBelongObjId(data.getLong("belongObjId"));
					sp.setLoginId(data.getString("loginId"));
					sp.setMaster(data.getString("master"));
					sp.setStatus(data.getString("status"));
					sp.setToken(data.getString("token"));
					sp.setFirstLogin(isFirstLogin);
					
					showToast("登录成功");
					
					onLoadMyInfo();
				} catch (JSONException e) {
					e.printStackTrace();
					showToast("服务器错误");
				}
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在登录...");
			}

			@Override
			public void onFinished() {
				toCloseProgressMsg();
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.customerLogin");
				params.put("loginId", phone);
				params.put(mCheckKey, code);
//				params.put("userId", mSpUtil.getBDUserId());
//				params.put("channelId", mSpUtil.getBDChannelId());
				params.put("userId", Utils.getBindUserId(getActivity()));
				params.put("channelId", Utils.getBindChannelId(getActivity()));
				return params;
			}
		}, tag);
	}
	
	private void onLoadMyInfo() {
		// Tag used to cancel the request
		String tag = "tag_request_get_member_detail";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Member member = (Member) response.getResponse(new TypeToken<Member>() {});
				if (member != null) {
					mSpUtil.setMember(member);
					
					sendBroadcast(new Intent(Constants.REFRESH_MEMBER_DETAIL));
					
					dispatchResult();
				}
			}

			private void dispatchResult() {
				if ("confirmOrder".equals(from)) {
					setResult(RESULT_OK);
				} if (TextUtils.isEmpty(mSpUtil.getName()) && TextUtils.isEmpty(mSpUtil.getAvatar())) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), EditProfileActivity.class);
					startActivity(intent);
				} else if (Constants.LOGOUT.equals(from)) {
					Intent mainIntent = new Intent(getActivity(), MainActivity.class);
					mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(mainIntent);
				} else {
					setResult(RESULT_OK);
				}
				finish();
			}

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onFailed(String msg) {
				dispatchResult();
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getMemberDetail");
				params.put("belongObjId", mSpUtil.getBelongObjIdStr());
				return params;
			}
		}, tag);
	}

}
