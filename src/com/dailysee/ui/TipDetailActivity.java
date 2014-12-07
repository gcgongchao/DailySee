package com.dailysee.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.alexbbb.uploadservice.ContentType;
import com.alexbbb.uploadservice.UploadRequest;
import com.alexbbb.uploadservice.UploadService;
import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Member;
import com.dailysee.bean.Tip;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.ui.base.LoginActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Md5Utils;
import com.dailysee.util.SpUtil;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;
import com.dailysee.widget.ConfirmDialog;
import com.dailysee.widget.InputTextDialog;
import com.dailysee.widget.SelectPicDialog;
import com.google.gson.reflect.TypeToken;

public class TipDetailActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = TipDetailActivity.class.getSimpleName();
	
	private TextView tvName;
	private TextView tvFrom;
	private TextView tvTime;
	
	private ImageView ivImage;
	private TextView tvContent;

	private Button btnEnterMerchant;

	private Tip mTip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestTipDetail();
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			mTip = (Tip) intent.getSerializableExtra("tip");
		}
		
		if (mTip == null) {
			finish();
		}
		
		setTitle("讯息详情");
		setUp();
	}

	@Override
	public void onFindViews() {
		tvName = (TextView) findViewById(R.id.tv_name);
		tvFrom = (TextView) findViewById(R.id.tv_from);
		tvTime = (TextView) findViewById(R.id.tv_time);
		
		ivImage = (ImageView) findViewById(R.id.iv_image);
		tvContent = (TextView) findViewById(R.id.tv_content);
		
		btnEnterMerchant = (Button) findViewById(R.id.btn_enter_merchant);
	}

	@Override
	public void onInitViewData() {
		tvName.setText(mTip.title);
		tvFrom.setText("来自" + mTip.companyName);
		tvTime.setText(mTip.createDate);
		
		onLoadImage(mTip.logoUrl);
		tvContent.setText(mTip.content);
		
		if (mTip.merchantId > 0) {
			btnEnterMerchant.setVisibility(View.VISIBLE);
		} else {
			btnEnterMerchant.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBindListener() {
		ivImage.setOnClickListener(this);
		btnEnterMerchant.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image:
			UiHelper.toBrowseImage(getActivity(), mTip.logoUrl);
			break;
		case R.id.btn_enter_merchant:
			Intent intent = new Intent();
//			intent.setClass(getActivity(), MerchantDetailActivity.class);
			intent.putExtra("merchantId", mTip.merchantId);
			startActivity(intent);
			break;
		}
		
	}

	private void onLoadImage(String imageUrl) {
		if (!TextUtils.isEmpty(imageUrl)) {
			AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage, R.drawable.ic_merchant, R.drawable.ic_merchant));
		}
	}
	
	private void requestTipDetail() {
		// Tag used to cancel the request
		String tag = "tag_request_tip_detail";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Tip tip = (Tip) response.getResponse(new TypeToken<Tip>(){});
				if (tip != null) {
					mTip = tip;
					onInitViewData();
				}
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在加载...");
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
				params.put("mtd", "com.guocui.tty.api.web.TipController.getTipDetail");
				params.put("tipId", Long.toString(mTip.tipId));
				return params;
			}
		}, tag);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
