package com.dailysee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dailysee.R;
import com.dailysee.ui.base.BaseFragment;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;
import com.dailysee.widget.ConfirmDialog;

public class CallMeFragment extends BaseFragment implements OnClickListener {

	protected static final String TAG = CallMeFragment.class.getSimpleName();

	private ImageView ivImage;

	public CallMeFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_call_me, null);
		return v;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onFindViews() {
		View v = getView();
		setTitle("呼叫天天");
		
		ivImage = (ImageView) v.findViewById(R.id.iv_image);
	}

	@Override
	public void onInitViewData() {
	}

	@Override
	public void onBindListener() {
		ivImage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image:
			showCallDialog();
			break;
		}
		
	}

	private void showCallDialog() {
		ConfirmDialog dialog = new ConfirmDialog(getActivity(), "拨打天天客服电话", "取消", "拨打", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.call(mContext, Constants.CUSTOMER_SERVICES_PHONE);
			}
		});
		dialog.show();
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
