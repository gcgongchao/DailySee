package com.dailysee.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.dailysee.R;

public class SelectPicDialog extends BaseDialog {
	private Button btnSelectPic;
	private Button btnCameraPic;
	private Button btnCancel;
	private View.OnClickListener mListener;

	public SelectPicDialog(Context context, View.OnClickListener clickListener) {
		super(context);
		this.mListener = clickListener;
	}

	@Override
	protected void afterDialogViews() {

	}

	@Override
	protected int getLayoutId() {
		return R.layout.dialog_add_pic;
	}

	@Override
	protected void initDialogViews() {
		btnCancel = (Button) findViewById(R.id.btn_cancel_pic);
		btnCameraPic = (Button) findViewById(R.id.btn_camera_pic);
		btnSelectPic = (Button) findViewById(R.id.btn_select_pic);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		btnCameraPic.setOnClickListener(mListener);
		btnSelectPic.setOnClickListener(mListener);
	}

}
