package com.dailysee.ui.base;

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
import com.dailysee.bean.Member;
import com.dailysee.util.UiHelper;
import com.dailysee.widget.ConfirmDialog;

public class ProfileActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = ProfileActivity.class.getSimpleName();
	
	private static final int REQUEST_EDIT_PROFILE = 10001;
	private static final int REQUEST_CHANGE_PHONE = 10002;

	private ImageView ivImage;
	
	private TextView tvName;
	private TextView tvSex;
	private TextView tvBirthday;
	private TextView tvEmail;

	private Button btnEdit;
	private Button btnChangePhone;
	private Button btnLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
	}

	@Override
	public void onInit() {
		setTitle("个人资料");
		setUp();
	}

	@Override
	public void onFindViews() {
		ivImage = (ImageView) findViewById(R.id.iv_image);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvSex = (TextView) findViewById(R.id.tv_sex);
		tvBirthday = (TextView) findViewById(R.id.tv_birthday);
		tvEmail = (TextView) findViewById(R.id.tv_email);
		
		btnEdit = (Button) findViewById(R.id.btn_edit);
		btnChangePhone = (Button) findViewById(R.id.btn_change_phone);
		btnLogout = (Button) findViewById(R.id.btn_logout);
	}

	@Override
	public void onInitViewData() {
	}
	
	@Override
	public void onBindListener() {
		btnEdit.setOnClickListener(this);
		btnChangePhone.setOnClickListener(this);
		btnLogout.setOnClickListener(this);

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
		Member member = mSpUtil.getMember();
		if (member != null) {
			if (!TextUtils.isEmpty(member.logoUrl)) {
				AppController.getInstance().getImageLoader().get(member.logoUrl, ImageLoader.getImageListener(ivImage, R.drawable.ic_avatar, R.drawable.ic_avatar));
			}

			tvName.setText(member.name);
			tvSex.setText(member.sex);
			tvBirthday.setText(member.birthday);
			tvEmail.setText(member.email);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_edit:
			toEditProfile();
			break;
		case R.id.btn_change_phone:
			toChangePhone();
			break;
		case R.id.btn_logout:
			showLogoutDialog();
			break;
		}
	}

	private void toChangePhone() {
		Intent intent = new Intent(this, ChangePhoneActivity.class);
		startActivityForResult(intent, REQUEST_CHANGE_PHONE);
	}

	private void toEditProfile() {
		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivityForResult(intent, REQUEST_EDIT_PROFILE);
	}
	
	private void showLogoutDialog() {
		ConfirmDialog dialog = new ConfirmDialog(getActivity(), "确定退出登录？", "取消", "确定", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSpUtil.logout();
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				startActivity(intent);
				
				Intent data = new Intent();
				data.putExtra("logout", true);
				setResult(RESULT_OK, data);
				finish();
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_EDIT_PROFILE && resultCode == RESULT_OK && data != null) {
			onInitViewData();
		} else if (requestCode == REQUEST_CHANGE_PHONE && resultCode == RESULT_OK && data != null) {
		}
	}

}
