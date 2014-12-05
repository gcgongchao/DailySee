package com.dailysee.widget;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.dailysee.R;

public class InputTextDialog extends ConfirmDialog {
	
	private EditText etInputText;
	private String inputHint;
	private int inputType = InputType.TYPE_CLASS_TEXT;

	public InputTextDialog(Context context, String confirmMsg, String cancelText, String okText, String inputHint,
            View.OnClickListener onOKListener) {
		super(context, confirmMsg, cancelText, okText, onOKListener);
		this.inputHint = inputHint;
	}
	
	public InputTextDialog(Context context, String confirmMsg, String cancelText, String okText, String inputHint, int inputType,
            View.OnClickListener onOKListener) {
		super(context, confirmMsg, cancelText, okText, onOKListener);
		this.inputHint = inputHint;
		this.inputType  = inputType;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.dialog_input_text;
	}

	@Override
	protected void initViews() {
		super.initViews();
		etInputText = (EditText) findViewById(R.id.et_input_text);
		etInputText.setHint(inputHint);
		etInputText.setInputType(inputType);
		this.setCanceledOnTouchOutside(false);
	}
	
	private String getInputText() {
		return etInputText.getText().toString();
	}

	@Override
	protected void btnOK(View v) {
		v.setTag(getInputText());
		super.btnOK(v);
	}
	
}
