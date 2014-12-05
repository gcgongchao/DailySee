package com.dailysee.ui.base;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.dailysee.R;
import com.dailysee.util.SpUtil;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity {

	protected boolean mSlideFinish = false;

	protected int mDownX = 0;

	protected int finishAnimId = 0;

	protected Intent mIntent;

	protected boolean isStartActivity = false;

	protected Toast mShortToast = null;

	protected Toast mLongToast = null;
	
	public SpUtil mSpUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mSpUtil = SpUtil.getInstance(this);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// 调用顺序
		onInit();
		onFindViews();
		onInitViewData();
		onBindListener();
	}

	public OnClickListener mBackClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	public void setUp() {
		View up = findViewById(R.id.iv_up);
		if (up != null) {
			up.setVisibility(View.VISIBLE);
			up.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}
	
	public void setTitle(String title) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		if (tvTitle != null) {
			tvTitle.setText(title);
		}
	}

	/**
	 * 初始化 优先顺序：<br/>
	 * <font color=red>onInit();</font><br/>
	 * onFindViews();<br/>
	 * onInitViewData();<br/>
	 * onBindListener();<br/>
	 */
	public abstract void onInit();

	/**
	 * 查找控件 <br/>
	 * 优先顺序：<br/>
	 * onInit();<br/>
	 * <font color=red>onFindViews();</font><br/>
	 * onInitViewData();<br/>
	 * onBindListener();<br/>
	 */
	public abstract void onFindViews();

	/**
	 * 初始化控件内容 优先顺序：<br/>
	 * onInit();<br/>
	 * onFindViews();<br/>
	 * <font color=red>onInitViewData();</font><br/>
	 * onBindListener();<br/>
	 */
	public abstract void onInitViewData();

	/**
	 * 注册控件事件 优先顺序：<br/>
	 * onInit();<br/>
	 * onFindViews();<br/>
	 * onInitViewData();<br/>
	 * <font color=red>onBindListener();</font><br/>
	 */
	public abstract void onBindListener();

	/**
	 * 打开滑动退出此Activity的功能 <功能详细描述>
	 * 
	 * @param isOpen
	 * @see [类、类#方法、类#成员]
	 */
	public void setSlide2Finish(boolean isOpen) {
		mSlideFinish = isOpen;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean res = super.dispatchTouchEvent(ev);
		// YLog.i(this, "dispatchTouchEvent="+ev.getAction()+" "+res);
		if (mSlideFinish) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mDownX = (int) ev.getRawX();
				break;
			case MotionEvent.ACTION_UP:
				Window window = this.getWindow();
				DisplayMetrics dm = new DisplayMetrics();
				window.getWindowManager().getDefaultDisplay().getMetrics(dm);
				if (Math.abs((ev.getRawX() - mDownX)) > (dm.widthPixels / 3)) {
					this.finish();
				}
				break;
			}
		}
		return res;
	}

	/**
	 * 结束此activity时动画 从左边移到右边 <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void finishWithLeftAnim() {
		finishAnimId = R.anim.from_left_out;
	}

	/**
	 * 结束此activity时动画 从上往下移动 <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void finishWithDownAnim() {
		finishAnimId = R.anim.from_up_out;
	}

	/**
	 * Toast 短时间显示 <功能详细描述>
	 * 
	 * @param message
	 * @see [类、类#方法、类#成员]
	 */
	public void showToastShort(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void showToastShort(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast 长时间显示 <功能详细描述>
	 * 
	 * @param message
	 * @see [类、类#方法、类#成员]
	 */
	public void showToastLong(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Toast <功能详细描述>
	 * 
	 * @param str资源ID
	 * @see [类、类#方法、类#成员]
	 */
	public void showToast(int strResID) {
		String msgStr = this.getResources().getString(strResID);
		showToastLong(msgStr);
	}

	/**
	 * 显示提示
	 * 
	 * @param message
	 */
	public void showToast(String message) {
		showToastLong(message);
	}

	// jiang add
	protected Dialog progressDialog;

	protected Activity getActivity() {
		return this;
	}

	protected void runOnUiThreadSafety(final Runnable runnable) {
		Activity activity = getActivity();
		if (activity == null) {
			return;
		}
		if (runnable == null) {
			return;
		}
		try {
			activity.runOnUiThread(runnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void toCloseProgressMsg() {
		runOnUiThreadSafety(new Runnable() {
			@Override
			public void run() {
				closeProgressDialog();
			}
		});
	}

	protected Dialog getProgressDialog(String msg) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(msg);
		return dialog;
	}

	private void closeProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing() && progressDialog.getWindow() != null) {
			progressDialog.dismiss();
		}
	}

	protected void toShowProgressMsg(final String msg) {
		runOnUiThreadSafety(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					if (progressDialog instanceof ProgressDialog) {
						((ProgressDialog) progressDialog).setMessage(msg);
					} else {
						try {
							progressDialog.getClass().getMethod("setMessage", String.class).invoke(progressDialog, msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					progressDialog = getProgressDialog(msg);
					progressDialog.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		isStartActivity = false;
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void finish() {
		super.finish();
		if (finishAnimId != 0) {
			overridePendingTransition(R.anim.none, finishAnimId);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
