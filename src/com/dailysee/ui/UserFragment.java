package com.dailysee.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.dailysee.ui.consultant.ConsultantPriceActivity;
import com.dailysee.ui.order.OrderActivity;
import com.dailysee.ui.user.AboutActivity;
import com.dailysee.ui.user.ProfileActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.UiHelper;
import com.dailysee.widget.BadgeView;

public class UserFragment extends BaseFragment implements OnClickListener {

	protected static final String TAG = UserFragment.class.getSimpleName();

	private static final int REQUEST_LOGIN = 1000;
	private static final int REQUEST_PROFILE = 1001;

	private LinearLayout llUserInfo;
	private ImageView ivImage;
	private TextView tvName;
	
	private LinearLayout llUnlogin;

	private LinearLayout llAllOrder;
	private TextView tvAllOrder;
	private LinearLayout llBookOrder;
	private TextView tvBookOrder;
	private LinearLayout llUncommentOrder;
	private TextView tvUncommentOrder;

	private LinearLayout llConsultantPrice;
	private LinearLayout llReceipt;
	private LinearLayout llAbout;
	
	private UserReceiver mUserReceiver;

	private BadgeView allOrderBadge;
	private BadgeView bookOrderBadge;
	private BadgeView uncommentOrderBadge;

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

		llAllOrder = (LinearLayout) v.findViewById(R.id.ll_all_order);
		tvAllOrder = (TextView) v.findViewById(R.id.tv_all_order);
		llBookOrder = (LinearLayout) v.findViewById(R.id.ll_book_order);
		tvBookOrder = (TextView) v.findViewById(R.id.tv_book_order);
		llUncommentOrder = (LinearLayout) v.findViewById(R.id.ll_uncomment_order);
		tvUncommentOrder = (TextView) v.findViewById(R.id.tv_uncomment_order);
		
		llConsultantPrice = (LinearLayout) v.findViewById(R.id.ll_consultant_price);
		llReceipt = (LinearLayout) v.findViewById(R.id.ll_receipt);
		llAbout = (LinearLayout) v.findViewById(R.id.ll_about);
	}

	@Override
	public void onInitViewData() {
		allOrderBadge = new BadgeView(mContext, tvAllOrder);
		bookOrderBadge = new BadgeView(mContext, tvBookOrder);
		uncommentOrderBadge = new BadgeView(mContext, tvUncommentOrder);
		
		mUserReceiver = new UserReceiver();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.REFRESH_MEMBER_DETAIL);
		mContext.registerReceiver(mUserReceiver, filter);
	}

	@Override
	public void onBindListener() {
		llUserInfo.setOnClickListener(this);
		ivImage.setOnClickListener(this);
		
		llUnlogin.setOnClickListener(this);
		
		llAllOrder.setOnClickListener(this);
		llBookOrder.setOnClickListener(this);
		llUncommentOrder.setOnClickListener(this);
		
		llConsultantPrice.setOnClickListener(this);
		llReceipt.setOnClickListener(this);
		llAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_unlogin:
			toLogin();
			break;
		case R.id.ll_user_info:
			toProfile();
			break;
		case R.id.iv_image:
			String avatar = mSpUtil.getAvatar();
			UiHelper.toBrowseImage(mContext, avatar);
			break;
		case R.id.ll_all_order:
			if (!mSpUtil.isLogin()) {
				toLogin();
			} else {
				toOrder(Constants.OrderFilter.ALL);
			}
			break;
		case R.id.ll_book_order:
			if (!mSpUtil.isLogin()) {
				toLogin();
			} else {
				toOrder(Constants.OrderFilter.WAIT_ACCEPT_CONFIRM + ";" + Constants.OrderFilter.WAIT_CONFIRM_GOODS);
			}
			break;
		case R.id.ll_uncomment_order:
			if (!mSpUtil.isLogin()) {
				toLogin();
			} else {
				toOrder(Constants.OrderFilter.SUCCEED);
			}
			break;
		case R.id.ll_consultant_price:
			toConsultantPrice();
			break;
		case R.id.ll_receipt:
			if (!mSpUtil.isLogin()) {
				toLogin();
			} else {
				toOrder(Constants.OrderFilter.SUCCEED, "receipt");
			}
			break;
		case R.id.ll_about:
			startActivity(new Intent(mContext, AboutActivity.class));
			break;
		}
	}

	private void toConsultantPrice() {
		startActivity(new Intent(mContext, ConsultantPriceActivity.class));
	}

	private void toOrder(String filter) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), OrderActivity.class);
		intent.putExtra("filter", filter);
		startActivity(intent);
	}
	
	private void toOrder(String filter, String from) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), OrderActivity.class);
		intent.putExtra("filter", filter);
		intent.putExtra("from", from);
		startActivity(intent);
	}

	private void toProfile() {
//		if (TextUtils.isEmpty(mSpUtil.getName()) && TextUtils.isEmpty(mSpUtil.getAvatar())) {
//			Intent intent = new Intent();
//			intent.setClass(getActivity(), EditProfileActivity.class);
//			startActivity(intent);
//		} else {
			Intent intent = new Intent();
			intent.setClass(mContext, ProfileActivity.class);
			startActivityForResult(intent, REQUEST_PROFILE);
//		}
	}

	private void toLogin() {
		Intent intent = new Intent();
		intent.setClass(mContext, LoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	@Override
	public void onResume() {
		super.onResume();
		onRefreshUserInfo();
		onRefreshNewMsgCount();
	}
	
	public void onRefreshUserInfo() {
		if (!mSpUtil.isLogin()) {
			llUnlogin.setVisibility(View.VISIBLE);
			llUserInfo.setVisibility(View.GONE);
		} else {
			llUnlogin.setVisibility(View.GONE);
			llUserInfo.setVisibility(View.VISIBLE);
			
			Member member = mSpUtil.getMember();
			if (member != null) {
				String name = member.name;
				if (TextUtils.isEmpty(name)) {
					name = mSpUtil.getLoginId();
				}
				tvName.setText(name);
				
				String avatar = mSpUtil.getAvatar();
				AppController.getInstance().getImageLoader().get(avatar, ImageLoader.getImageListener(ivImage, R.drawable.ic_avatar, R.drawable.ic_avatar));
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_PROFILE && data != null) {
				if (data.getBooleanExtra("logout", false)) {
					getActivity().finish();
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		if (mUserReceiver != null) {
			Log.d(TAG, "onDestroy() unregisterReceiver - UserReceiver");
			mContext.unregisterReceiver(mUserReceiver);
			mUserReceiver = null;
		}
		super.onDestroy();
	}
	
	private class UserReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (Constants.REFRESH_MEMBER_DETAIL.equals(action)) {
					onRefreshUserInfo();
				}
			}
		}
	}

	public void onRefreshNewMsgCount() {
		int newCommentCount = mSpUtil.getNewCommentCount();
		int newConfirmOrderCount = mSpUtil.getNewConfirmOrderCount();
		
		int newUserMsgCount = newCommentCount + newConfirmOrderCount + mSpUtil.getNewRefundResultCount();
		allOrderBadge.setText(newUserMsgCount + "");
		if (newUserMsgCount > 0) {
			allOrderBadge.show();
		} else {
			allOrderBadge.hide();
		}
		
		bookOrderBadge.setText(newConfirmOrderCount + "");
		if (newConfirmOrderCount > 0) {
			bookOrderBadge.show();
		} else {
			bookOrderBadge.hide();
		}
		
		uncommentOrderBadge.setText(newCommentCount + "");
		if (newCommentCount > 0) {
			uncommentOrderBadge.show();
		} else {
			uncommentOrderBadge.hide();
		}
	}

}
