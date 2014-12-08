package com.dailysee.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.dailysee.R;

public abstract class BasePopupWindow extends PopupWindow {

	protected Context context;
	View contentView;
	View.OnClickListener clickListener;

	public BasePopupWindow(Context context, int layoutId) {
	    this(context, null, layoutId);
	}

	public BasePopupWindow(Context context, OnClickListener onclickListener, int layoutId) {
		super(context);
		this.context = context;
		setContentView(layoutId);
		clickListener = onclickListener;
	}

	private void setContentView(int layoutId) {
		contentView = LayoutInflater.from(context).inflate(layoutId, null);
		setAnimationStyle(R.style.PopupWindowAnimation);
		setContentView(contentView);
		setWidth(android.view.ViewGroup.LayoutParams.FILL_PARENT);
		setHeight(android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		// 点击外部可消失
		setFocusable(true);

		// 背景透明
//		 ColorDrawable dw = new ColorDrawable(0x70000000);
//		setBackgroundDrawable(dw);
		setBackgroundDrawable(new BitmapDrawable());
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		super.showAsDropDown(anchor, xoff, yoff);
	}

}
