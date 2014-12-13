package com.dailysee.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.dailysee.R;

public class SelectPaymentDialog extends BaseDialog {
	private Button btnWechatPayment;
	private Button btnAlipayPayment;
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
		btnCancel = (Button) findViewById(R.id.btn_cancel_pic);
		btnAlipayPayment = (Button) findViewById(R.id.btn_alipay_payment);
		btnWechatPayment = (Button) findViewById(R.id.btn_wechat_payment);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		btnAlipayPayment.setOnClickListener(mListener);
		btnWechatPayment.setOnClickListener(mListener);
	}

}
