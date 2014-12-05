package com.dailysee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Member;
import com.dailysee.ui.base.BaseFragment;
import com.dailysee.ui.base.LoginActivity;
import com.dailysee.util.UiHelper;

public class UserFragment extends BaseFragment implements OnClickListener {

	protected static final String TAG = UserFragment.class.getSimpleName();

	private static final int REQUEST_LOGIN = 1000;

	private LinearLayout llUserInfo;
	private ImageView ivImage;
	
	private LinearLayout llUnlogin;

	private TextView tvName;

	public UserFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_user, null);
		return v;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onFindViews() {
		View v = getView();
		setTitle("个人中心");
		
		llUserInfo = (LinearLayout) v.findViewById(R.id.ll_user_info);
		ivImage = (ImageView) v.findViewById(R.id.iv_image);
		tvName = (TextView) v.findViewById(R.id.tv_name);

		llUnlogin = (LinearLayout) v.findViewById(R.id.ll_unlogin);
	}

	@Override
	public void onInitViewData() {
		if (!mSpUtil.isLogin()) {
			llUnlogin.setVisibility(View.VISIBLE);
			llUserInfo.setVisibility(View.GONE);
		} else {
			llUnlogin.setVisibility(View.GONE);
			llUserInfo.setVisibility(View.VISIBLE);
			
			Member member = mSpUtil.getMember();
			if (member != null) {
				tvName.setText(member.name);
				
				String avatar = mSpUtil.getAvatar();
				AppController.getInstance().getImageLoader().get(avatar, ImageLoader.getImageListener(ivImage, R.drawable.ic_image, R.drawable.ic_image));
			}
		}
	}

	@Override
	public void onBindListener() {
		llUserInfo.setOnClickListener(this);
		ivImage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_unlogin:
			toLogin();
			break;
		case R.id.ll_user_info:
			break;
		case R.id.iv_image:
			String avatar = mSpUtil.getAvatar();
			UiHelper.toBrowseImage(mContext, avatar);
			break;
		}
		
	}

	private void toLogin() {
		Intent intent = new Intent();
		intent.setClass(mContext, LoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
