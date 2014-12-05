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

public class HomeFragment extends BaseFragment {

	protected static final String TAG = HomeFragment.class.getSimpleName();
	private ImageView ivMerchant;

	public HomeFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void request() {
	}

	@Override
	public void onInit() {
		
	}

	@Override
	public void onFindViews() {
		View v = getView();
		
		ivMerchant = (ImageView) v.findViewById(R.id.iv_merchant);
		ivMerchant.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(mContext, MerchantActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onInitViewData() {
		
	}

	@Override
	public void onBindListener() {
		
	}

}
