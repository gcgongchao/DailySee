package com.dailysee.ui.consultant;

import android.os.Bundle;

import com.dailysee.R;
import com.dailysee.ui.base.BaseActivity;

public class ConsultantPriceActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consultant_price);
	}

	@Override
	public void onInit() {
		setTitle("商务公关服务价目表");
		setUp();
	}

	@Override
	public void onFindViews() {
		
	}

	@Override
	public void onInitViewData() {
		
	}

	@Override
	public void onBindListener() {
		
	}

}
