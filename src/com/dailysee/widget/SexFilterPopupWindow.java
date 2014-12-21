package com.dailysee.widget;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dailysee.R;

public class SexFilterPopupWindow extends BasePopupWindow implements OnClickListener {

	public static final String TAG = SexFilterPopupWindow.class.getSimpleName();

	private TextView tvTemp;
	private TextView tvFilterAll;
	private TextView tvFilterWomen;
	private TextView tvFilterMen;

	private OnClickListener mOnClickListener;

	public SexFilterPopupWindow(Context context) {
		super(context, R.layout.dialog_sex_filter);
	}

	public SexFilterPopupWindow(Context context, OnClickListener onClickListener) {
		super(context, onClickListener, R.layout.dialog_sex_filter);
		this.mOnClickListener = onClickListener;
	}

	public void init() {
		initPopFindViews();
		initPopViewsValue();
		initPopViewsEvent();
	}

	public void initPopFindViews() {
		tvFilterAll = (TextView) contentView.findViewById(R.id.tv_filter_all);
		tvFilterWomen = (TextView) contentView.findViewById(R.id.tv_filter_women);
		tvFilterMen = (TextView) contentView.findViewById(R.id.tv_filter_men);

		tvTemp = (TextView) contentView.findViewById(R.id.tv_temp);
	}

	public void initPopViewsValue() {
	}

	public void initPopViewsEvent() {
		tvTemp.setOnClickListener(this);
		
		tvFilterAll.setOnClickListener(mOnClickListener);
		tvFilterWomen.setOnClickListener(mOnClickListener);
		tvFilterMen.setOnClickListener(mOnClickListener);
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
