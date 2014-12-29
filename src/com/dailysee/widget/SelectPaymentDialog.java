package com.dailysee.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.dailysee.R;

public class SelectPaymentDialog extends BaseDialog {
	private Button btnWechatPayment;
	private Button btnAlipayPayment;
	private Button btnUPPayment;
	private Button btnCancel;
	private View.OnClickListener mListener;

	public SelectPaymentDialog(Context context, View.OnClickListener clickListener) {
		super(context);
		this.mListener = clickListener;
	}

	@Override
	protected void afterDialogViews() {

	}

	@Override
	protected int getLayoutId() {
		return R.layout.dialog_select_payment;
	}

	@Override
	protected void initDialogViews() {
		btnWechatPayment = (Button) findViewById(R.id.btn_wechat_payment);
		btnAlipayPayment = (Button) findViewById(R.id.btn_alipay_payment);
		btnUPPayment = (Button) findViewById(R.id.btn_up_payment);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		
		btnAlipayPayment.setOnClickListener(mListener);
		btnWechatPayment.setOnClickListener(mListener);
		btnUPPayment.setOnClickListener(mListener);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
	}

}
