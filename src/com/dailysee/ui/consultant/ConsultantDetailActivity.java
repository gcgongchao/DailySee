package com.dailysee.ui.consultant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Consultant;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.ui.order.ConfirmOrderActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;

public class ConsultantDetailActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = ConsultantDetailActivity.class.getSimpleName();

	private static final int REQUEST_CONFIRM_ORDER = 1000;
	
	private ImageView ivImage;
	
	private TextView tvName;
	
	private ImageView ivSex;
	private TextView tvAge;
	
	private TextView tvBwh;
	private TextView tvHeight;

	private TextView tvSignature;
	private TextView tvIntroduction;

	private LinearLayout llAlbum;
	private ImageView ivImage1;
	private ImageView ivImage2;
	private ImageView ivImage3;

	private Button btnCommit;

	private int mFrom;
	private Consultant consultant;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consultant_detail);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			consultant = (Consultant) intent.getSerializableExtra("consultant");
			mFrom = intent.getIntExtra("from", Constants.From.CONSULTANT);
		}
		
		setTitle("个人资料");
		setUp();
	}

	@Override
	public void onFindViews() {
		ivImage = (ImageView) findViewById(R.id.iv_image);
		tvName = (TextView) findViewById(R.id.tv_name);
		ivSex = (ImageView) findViewById(R.id.iv_sex);
		tvAge = (TextView) findViewById(R.id.tv_age);
		tvBwh = (TextView) findViewById(R.id.tv_bwh);
		tvHeight = (TextView) findViewById(R.id.tv_height);
		tvSignature = (TextView) findViewById(R.id.tv_signature);
		tvIntroduction = (TextView) findViewById(R.id.tv_introduction);
		
		llAlbum = (LinearLayout) findViewById(R.id.ll_album);
		ivImage1 = (ImageView) findViewById(R.id.iv_image_1);
		ivImage2 = (ImageView) findViewById(R.id.iv_image_2);
		ivImage3 = (ImageView) findViewById(R.id.iv_image_3);
		
		btnCommit = (Button) findViewById(R.id.btn_commit);
	}

	@Override
	public void onInitViewData() {
		if (consultant != null) {
			onRefreshMemberDetail(consultant);
		} else {
			finish();
		}
	}

	private void onRefreshMemberDetail(Consultant member) {
		if (!TextUtils.isEmpty(member.logoUrl)) {
			AppController.getInstance().getImageLoader().get(member.logoUrl, ImageLoader.getImageListener(ivImage, R.drawable.ic_avatar, R.drawable.ic_avatar));
		}
		
		tvName.setText(member.nick);
		ivSex.setImageResource(Constants.Sex.MEN.equals(member.sex) ? R.drawable.ic_boy : (Constants.Sex.WOMEN.equals(member.sex) ? R.drawable.ic_girl : 0));
		tvBwh.setText("三围: " + member.three);
		if (!TextUtils.isEmpty(member.age)) {
			tvAge.setText(Utils.parseAge(member.age) + "岁");
		} else {
			tvAge.setText("");
		}
		if (!TextUtils.isEmpty(member.three)) {
			tvBwh.setText("三围: " + member.three);
		} else {
			tvBwh.setText("");
		}
		if (!TextUtils.isEmpty(member.height)) {
			tvHeight.setText("身高: " + member.height);
		} else {
			tvHeight.setText("");
		}
		tvSignature.setText(member.signature);
		tvIntroduction.setText(member.introduction);
		
		if (member.imgs != null) {
			int size = member.imgs.size();
			if (size > 0) {
				String imageUrl = member.imgs.get(0).url;
				ivImage1.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(imageUrl)) {
					AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage1, R.drawable.ic_image, R.drawable.ic_image));
				}
			}
			if (size > 1) {
				String imageUrl = member.imgs.get(1).url;
				ivImage2.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(imageUrl)) {
					AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage2, R.drawable.ic_image, R.drawable.ic_image));
				}
			}
			if (size > 2) {
				String imageUrl = member.imgs.get(2).url;
				ivImage3.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(imageUrl)) {
					AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage3, R.drawable.ic_image, R.drawable.ic_image));
				}
			}
					
		}
	}

	
	@Override
	public void onBindListener() {
		llAlbum.setOnClickListener(this);
		btnCommit.setOnClickListener(this);

		ivImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mSpUtil.getAvatar())) {
					UiHelper.toBrowseImage(getActivity(), mSpUtil.getAvatar());
				}
			}
		});
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
		case R.id.ll_album:
			UiHelper.toBrowseImageList(getActivity(), consultant.imgs, 0);
			break;
		case R.id.btn_commit:
			toConfirmOrder();
			break;
		}
	}

	private void toConfirmOrder() {
		Intent intent = new Intent();
		intent.setClass(this, ConfirmOrderActivity.class);
		intent.putExtra("consultant", consultant);
		intent.putExtra("from", mFrom);
		startActivityForResult(intent, REQUEST_CONFIRM_ORDER);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (REQUEST_CONFIRM_ORDER == requestCode && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

}
