package com.dailysee.ui.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dailysee.R;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;

public class ChangePhoneActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = ChangePhoneActivity.class.getSimpleName();
	private Button btnCommit;
	private EditText etPhone;
	private EditText etCode;
	private TextView btnGetCode;
	protected String mCheckCode;
	protected String mCheckKey;
	private ImageView mIvUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phone);
	}

	@Override
	public void onInit() {
		setTitle("修改手机");
		setUp();
	}

	@Override
	public void onFindViews() {
		mIvUp = (ImageView) findViewById(R.id.iv_up);
		etPhone = (EditText) findViewById(R.id.et_phone);
		etCode = (EditText) findViewById(R.id.et_code);
		btnCommit = (Button) findViewById(R.id.btn_commit);
		btnGetCode = (TextView) findViewById(R.id.btn_get_code);
	}

	@Override
	public void onInitViewData() {
		mIvUp.setVisibility(View.VISIBLE);
	}

	@Override
	public void onBindListener() {
		mIvUp.setOnClickListener(this);
		btnCommit.setOnClickListener(this);
		btnGetCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			if (checkPhone() && checkCode()) {
				requestChangePhone();
			}
			break;
		case R.id.btn_get_code:
			if (checkPhone()) {
				etCode.setText("");
				etCode.requestFocus();
				requestCode();
			}
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
				Toast.makeText(ChangePhoneActivity.this, "验证码已发送到您的手机 " + mCheckCode, Toast.LENGTH_SHORT).show();
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

	private void requestChangePhone() {
		final String phone = getPhone();
		final String code = getCode();

		// Tag used to cancel the request
		String tag = "tag_request_change_phone";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mSpUtil.setLoginId(phone);
				
				showToast("修改成功");

				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在提交...");
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
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.modifyLoginId");
				params.put("memberId", mSpUtil.getMemberIdStr());
				params.put("loginId", phone);
				params.put(mCheckKey, code);
				return params;
			}
		}, tag);
	}

}
