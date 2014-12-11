package com.dailysee.widget;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dailysee.R;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

public class SelectBookingDateDialog extends BaseDialog {

	protected static final String TAG = SelectBookingDateDialog.class.getSimpleName();

	private TextView tvTitle;
	private CalendarPickerView dialogView;

	private String title;
	private OnDateSelectedListener listener;

	public SelectBookingDateDialog(Context context, String title, OnDateSelectedListener listener) {
		super(context);
		this.title = title;
		this.listener = listener;
	}

	protected void init(Context context) {
		View view = getLayoutInflater().inflate(getLayoutId(), null);
		setContentView(view);
		getWindow().setGravity(Gravity.CENTER);
		setCanceledOnTouchOutside(true);
		initDialogViews();
		afterDialogViews();
		Window win = getWindow();
		WindowManager m = win.getWindowManager();
		DisplayMetrics dm = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(dm);
		// Display d = m.getDefaultDisplay(); //
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = (int) (dm.widthPixels * 0.9);
		p.height = (int) (dm.heightPixels * 0.8);
		win.setAttributes(p);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.dialog_select_booking_date;
	}

	@Override
	protected void initDialogViews() {
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		final Calendar today = Calendar.getInstance();
		
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);

		dialogView = (CalendarPickerView) findViewById(R.id.calendar_view);
		dialogView.init(today.getTime(), nextYear.getTime()).withSelectedDate(new Date());
		dialogView.setOnDateSelectedListener(listener);
	}

	@Override
	protected void afterDialogViews() {
		setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialogInterface) {
				Log.d(TAG, "onShow: fix the dimens!");
				dialogView.fixDialogDimens();
			}
		});
	}
}
