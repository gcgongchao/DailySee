package com.dailysee.ui.consultant;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
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

	private void onRefreshMemberDetail(Consultant consultant) {
		if (!TextUtils.isEmpty(consultant.logoUrl)) {
			AppController.getInstance().getImageLoader().get(consultant.logoUrl, ImageLoader.getImageListener(ivImage, R.drawable.ic_avatar, R.drawable.ic_avatar));
		}
		
		tvName.setText(consultant.getName());
		ivSex.setImageResource(Constants.Sex.MEN.equals(consultant.sex) ? R.drawable.ic_boy : (Constants.Sex.WOMEN.equals(consultant.sex) ? R.drawable.ic_girl : 0));
		tvBwh.setText("三围: " + consultant.three);
		if (!TextUtils.isEmpty(consultant.age)) {
			tvAge.setText(Utils.parseAge(consultant.age) + "岁");
		} else {
			tvAge.setText("");
		}
		if (!TextUtils.isEmpty(consultant.three)) {
			tvBwh.setText("三围: " + consultant.three);
		} else {
			tvBwh.setText("");
		}
		if (!TextUtils.isEmpty(consultant.height)) {
			tvHeight.setText("身高: " + consultant.height);
		} else {
			tvHeight.setText("");
		}
		tvSignature.setText(consultant.getSignature());
		tvIntroduction.setText(consultant.introduction);
		
		if (consultant.imgs != null) {
			int size = consultant.imgs.size();
			if (size > 0) {
				String imageUrl = consultant.imgs.get(0).url;
				ivImage1.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(imageUrl)) {
					AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage1, R.drawable.ic_image, R.drawable.ic_image));
				}
			}
			if (size > 1) {
				String imageUrl = consultant.imgs.get(1).url;
				ivImage2.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(imageUrl)) {
					AppController.getInstance().getImageLoader().get(imageUrl, ImageLoader.getImageListener(ivImage2, R.drawable.ic_image, R.drawable.ic_image));
				}
			}
			if (size > 2) {
				String imageUrl = consultant.imgs.get(2).url;
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
				if (consultant != null && !TextUtils.isEmpty(consultant.logoUrl)) {
					UiHelper.toBrowseImage(getActivity(), consultant.logoUrl);
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
			if (consultant != null) {
				showSelectBookingDateDialog();
			}
			break;
		}
	}

	private void showSelectBookingDateDialog() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                    	if (!Utils.isFastDoubleClick()) {
	                    	String dateStr = year + "-" + (month+1) + "-" + dayOfMonth;
	                    	toConfirmOrder(dateStr);
                    	}
                    }
                }, 
                year, // 传入年份
                month, // 传入月份
                dayOfMonth // 传入天数
            );
		dialog.show();
	}

	private void toConfirmOrder(String date) {
		Intent intent = new Intent();
		intent.setClass(this, ConfirmOrderActivity.class);
		intent.putExtra("consultant", consultant);
		intent.putExtra("from", mFrom);
		intent.putExtra("date", date);
		intent.putExtra("totalPrice", consultant.worth);
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
