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
	private TextView tvFilter1;
	private TextView tvFilter2;
	private TextView tvFilter3;

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
		tvFilter1 = (TextView) contentView.findViewById(R.id.tv_filter_1);
		tvFilter2 = (TextView) contentView.findViewById(R.id.tv_filter_2);
		tvFilter3 = (TextView) contentView.findViewById(R.id.tv_filter_3);

		tvTemp = (TextView) contentView.findViewById(R.id.tv_temp);
	}

	public void initPopViewsValue() {
	}

	public void initPopViewsEvent() {
		tvTemp.setOnClickListener(this);
		
		tvFilterAll.setOnClickListener(mOnClickListener);
		tvFilter1.setOnClickListener(mOnClickListener);
		tvFilter2.setOnClickListener(mOnClickListener);
		tvFilter3.setOnClickListener(mOnClickListener);
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
