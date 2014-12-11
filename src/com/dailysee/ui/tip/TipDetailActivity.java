package com.dailysee.ui.tip;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Tip;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.ui.merchant.MerchantRoomListActivity;
import com.dailysee.util.TipSpUtil;
import com.dailysee.util.UiHelper;
import com.google.gson.reflect.TypeToken;

public class TipDetailActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = TipDetailActivity.class.getSimpleName();

	private Button btnShare;
	
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
		setContentView(R.layout.activity_tip_detail);
		
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
		} else {
			TipSpUtil.getInstance(getActivity()).setRead(mTip.tipId);
		}
		
		setTitle("讯息详情");
		setUp();
	}

	@Override
	public void onFindViews() {
		btnShare = (Button) findViewById(R.id.btn_action);
		
		tvName = (TextView) findViewById(R.id.tv_name);
		tvFrom = (TextView) findViewById(R.id.tv_from);
		tvTime = (TextView) findViewById(R.id.tv_time);
		
		ivImage = (ImageView) findViewById(R.id.iv_image);
		tvContent = (TextView) findViewById(R.id.tv_content);
		
		btnEnterMerchant = (Button) findViewById(R.id.btn_enter_merchant);
	}

	@Override
	public void onInitViewData() {
		btnShare.setText("分享");
		btnShare.setVisibility(View.VISIBLE);
		
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
		btnShare.setOnClickListener(this);
		ivImage.setOnClickListener(this);
		btnEnterMerchant.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_action:
			toShare();
			break;
		case R.id.iv_image:
			UiHelper.toBrowseImage(getActivity(), mTip.logoUrl);
			break;
		case R.id.btn_enter_merchant:
			Intent intent = new Intent();
			intent.setClass(getActivity(), MerchantRoomListActivity.class);
			intent.putExtra("merchantId", mTip.merchantId);
			startActivity(intent);
			break;
		}
		
	}

	private void toShare() {
		Intent intent = new Intent(Intent.ACTION_SEND);  
		if (TextUtils.isEmpty(mTip.logoUrl)) {
			intent.setType("text/plain"); 
		} else {
			intent.setType("image/*");  
	        intent.putExtra(Intent.EXTRA_STREAM, URI.create(mTip.logoUrl));  
		}
        intent.putExtra(Intent.EXTRA_SUBJECT, mTip.title);  
        intent.putExtra(Intent.EXTRA_TEXT, mTip.content);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startActivity(Intent.createChooser(intent, "分享讯息"));
	}

	private void onLoadImage(String imageUrl) {
		if (!TextUtils.isEmpty(imageUrl)) {
			ivImage.setVisibility(View.VISIBLE);
			AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage, R.drawable.ic_image_tip, R.drawable.ic_image_tip));
		} else {
			ivImage.setVisibility(View.GONE);
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
