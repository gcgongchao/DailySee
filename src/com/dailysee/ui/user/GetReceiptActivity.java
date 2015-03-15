package com.dailysee.ui.user;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.alexbbb.uploadservice.ContentType;
import com.alexbbb.uploadservice.UploadRequest;
import com.alexbbb.uploadservice.UploadService;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.CityEntity;
import com.dailysee.bean.Member;
import com.dailysee.bean.Order;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.SpUtil;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;
import com.dailysee.widget.SelectPicDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GetReceiptActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {

	protected static final String TAG = GetReceiptActivity.class.getSimpleName();
	
	private EditText etReceiptTitle;
	
	private EditText etName;
	private EditText etIdCard;
	private EditText etPhone;
	private EditText etAddress;
	private EditText etEmail;
	
	private Button btnCommit;

	private long mOrderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_receipt);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			mOrderId = intent.getLongExtra("orderId", 0);
		}
		
		if (mOrderId <= 0) {
			finish();
		}
		
		setTitle("索取发票");
		setUp();
	}

	@Override
	public void onFindViews() {
		etReceiptTitle = (EditText) findViewById(R.id.et_receipt_title);
		
		etName = (EditText) findViewById(R.id.et_name);
		etIdCard = (EditText) findViewById(R.id.et_id_card);
		etPhone = (EditText) findViewById(R.id.et_phone);
		etAddress = (EditText) findViewById(R.id.et_address);
		etEmail = (EditText) findViewById(R.id.et_email);
		
		btnCommit = (Button) findViewById(R.id.btn_commit);
	}

	@Override
	public void onInitViewData() {
	}

	@Override
	public void onBindListener() {
		btnCommit.setOnClickListener(this);

		etReceiptTitle.setOnFocusChangeListener(this);
		etName.setOnFocusChangeListener(this);
		etIdCard.setOnFocusChangeListener(this);
		etPhone.setOnFocusChangeListener(this);
		etAddress.setOnFocusChangeListener(this);
		etEmail.setOnFocusChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			if (checkReceiptTitle() && checkName() && checkIdCard() && checkPhone() && checkAddress() && checkEmail()) {
				requestGetReceipt();
			}
			break;
		}
	}

	private boolean checkEmail() {
		String email = getEmail();
		
		boolean check = false;
		if (TextUtils.isEmpty(email)) {
			showToast("请输入电子邮件");
		} else if (!Utils.checkEmail(email)) {
			showToast("邮件格式不正确，请重新输入");
		} else {
			check = true;
		}

		return check;
	}

	public String getEmail() {
		return etEmail.getText().toString();
	}

	private boolean checkName() {
		String name = getName();

		boolean check = false;
		if (TextUtils.isEmpty(name)) {
			showToast("请输入收件人");
		} else {
			check = true;
		}

		return check;
	}

	public String getName() {
		return etName.getText().toString();
	}
	
	private boolean checkReceiptTitle() {
		String title = getReceiptTitle();
		
		boolean check = false;
		if (TextUtils.isEmpty(title)) {
			showToast("请输入发票抬头");
		} else {
			check = true;
		}

		return check;
	}

	public String getReceiptTitle() {
		return etReceiptTitle.getText().toString();
	}

	private boolean checkIdCard() {
		String idCard = getIdCard();

		boolean check = false;
		if (TextUtils.isEmpty(idCard)) {
			showToast("请输入收件人");
		} else {
			check = true;
		}

		return check;
	}

	public String getIdCard() {
		return etIdCard.getText().toString();
	}

	private boolean checkAddress() {
		String address = getAddress();

		boolean check = false;
		if (TextUtils.isEmpty(address)) {
			showToast("请输入住址");
		} else {
			check = true;
		}

		return check;
	}

	public String getAddress() {
		return etAddress.getText().toString();
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

	private void requestGetReceipt() {
		final String receiptTitle = getReceiptTitle();
		final String name = getName();
		final String idCard = getIdCard();
		final String phone = getPhone();
		final String address = getAddress();
		final String email = getEmail();

		// Tag used to cancel the request
		String tag = "tag_request_get_receipt";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Toast.makeText(GetReceiptActivity.this, "提交成功", Toast.LENGTH_SHORT).show();

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
				params.put("mtd", "tty.order.e-invoice.req");
				params.put("counselorId", mSpUtil.getBelongObjIdStr());
				params.put("title", receiptTitle);
				params.put("orderNo", mOrderId + "");
				params.put("idCard", idCard);
				params.put("tel", phone);
				params.put("addr", name + "@" + address);
				params.put("email", email);
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag);
	}

	@Override
	public void onFocusChange(View view, boolean focus) {
		if (focus) {
			if (view instanceof EditText) {
				EditText et = (EditText) view;
				Selection.setSelection(et.getEditableText(), et.getEditableText().length());
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
