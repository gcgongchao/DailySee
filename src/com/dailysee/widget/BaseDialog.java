package com.dailysee.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dailysee.R;

public abstract class BaseDialog extends Dialog {

	protected final Context context;

	public BaseDialog(Context context) {
		super(context, R.style.BaseDialog);
		this.context = context;
	}

	protected BaseDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public BaseDialog(Context context, int theme) {
		super(context, R.style.BaseDialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(context);
	}

	protected abstract void afterDialogViews();

	protected abstract int getLayoutId();

	protected void init(Context context) {
		View view=getLayoutInflater().inflate(getLayoutId(), null);
		setContentView(view);
		getWindow().setGravity(Gravity.BOTTOM);
		setCanceledOnTouchOutside(true);
		initDialogViews();
		afterDialogViews();
		 Window win = getWindow();
	    WindowManager m = win.getWindowManager();
		DisplayMetrics  dm = new DisplayMetrics();    
	    m.getDefaultDisplay().getMetrics(dm);    
		//Display d = m.getDefaultDisplay(); //
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = (int) dm.widthPixels;
	    win.setAttributes(p);
	}

	protected abstract void initDialogViews();
}
