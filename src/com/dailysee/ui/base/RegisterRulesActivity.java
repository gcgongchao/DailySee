package com.dailysee.ui.base;

import android.os.Bundle;
import android.widget.TextView;

import com.dailysee.R;

public class RegisterRulesActivity extends BaseActivity {

	protected static final String TAG = RegisterRulesActivity.class.getSimpleName();
	private TextView tvRegisterRules;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_rules);
	}

	@Override
	public void onInit() {
		setTitle("用户须知");
		setUp();
	}

	@Override
	public void onFindViews() {
		tvRegisterRules = (TextView) findViewById(R.id.tv_register_rules);
		tvRegisterRules.setText(R.string.service_protocol);
	}

	@Override
	public void onInitViewData() {
	}

	@Override
	public void onBindListener() {
	}

}
