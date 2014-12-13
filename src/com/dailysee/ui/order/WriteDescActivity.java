package com.dailysee.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.ui.base.BaseActivity;

public class WriteDescActivity extends BaseActivity implements OnClickListener {

	private EditText etDesc;
	private TextView tvSize;
	private Button btnCommit;

	private String desc;
	private String descBackup;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_desc);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			desc = intent.getStringExtra("desc");
			descBackup = desc;
		}
		
		setTitle("订单备注");
		setUp();
	}

	@Override
	public void onFindViews() {
		etDesc = (EditText) findViewById(R.id.et_desc);
		tvSize = (TextView) findViewById(R.id.tv_size);
		btnCommit = (Button) findViewById(R.id.btn_commit);
	}

	@Override
	public void onInitViewData() {
		etDesc.setText(desc);
		Selection.setSelection(etDesc.getEditableText(), etDesc.getEditableText().length());// 光标移动到最后
		showTextSize();
	}

	@Override
	public void onBindListener() {
		etDesc.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				desc = s.toString();
				showTextSize();
			}
		});
		btnCommit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			finish(desc);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish(descBackup);
	}

	public void showTextSize() {
		if (desc != null)
			tvSize.setText((800 - desc.length()) + "");
		else
			tvSize.setText(800 + "");
	}

	public void finish(String result) {
		Intent intent = new Intent();
		intent.putExtra("desc", result);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
