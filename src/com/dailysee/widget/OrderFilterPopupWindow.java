package com.dailysee.widget;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dailysee.R;

public class OrderFilterPopupWindow extends BasePopupWindow implements OnClickListener {

	public static final String TAG = OrderFilterPopupWindow.class.getSimpleName();

	private TextView tvTemp;
	private TextView tvFilterAll;
	private TextView tvFilterUnprocessed;
	private TextView tvFilterProcessed;

	private OnClickListener mOnClickListener;

	public OrderFilterPopupWindow(Context context) {
		super(context, R.layout.dialog_order_filter);
	}

	public OrderFilterPopupWindow(Context context, OnClickListener onClickListener) {
		super(context, onClickListener, R.layout.dialog_order_filter);
		this.mOnClickListener = onClickListener;
	}

	public void init() {
		initPopFindViews();
		initPopViewsValue();
		initPopViewsEvent();
	}

	public void initPopFindViews() {
		tvFilterAll = (TextView) contentView.findViewById(R.id.tv_filter_all);
		tvFilterUnprocessed = (TextView) contentView.findViewById(R.id.tv_filter_unprocessed);
		tvFilterProcessed = (TextView) contentView.findViewById(R.id.tv_filter_processed);

		tvTemp = (TextView) contentView.findViewById(R.id.tv_temp);
	}

	public void initPopViewsValue() {
	}

	public void initPopViewsEvent() {
		tvTemp.setOnClickListener(this);
		
		tvFilterAll.setOnClickListener(mOnClickListener);
		tvFilterUnprocessed.setOnClickListener(mOnClickListener);
		tvFilterProcessed.setOnClickListener(mOnClickListener);
	}

	public void show(View v, int x, int y) {
		this.showAsDropDown(v, x, y);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_temp:
			dismiss();
			break;

		default:
			break;
		}
	}

}
