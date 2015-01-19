package com.dailysee.widget;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.Room;
import com.dailysee.bean.ServiceHour;

public class ListViewDialog extends Dialog implements android.view.View.OnClickListener {
	
	private String title;
	private List<Object> mItems;
	private OnItemClickListener mListener;

	private ListView mListView;
	private TextView tvTitle;
	
	protected final Context context;

	public ListViewDialog(Context context) {
		super(context, R.style.BaseDialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(context);
	}

	public ListViewDialog(Context context, String title, List<Object> items, OnItemClickListener listener) {
		this(context);
		this.title = title;
		this.mItems = items;
		this.mListener = listener;
	}

	protected void afterDialogViews() {
		tvTitle.setText(title);
		
		ListViewAdapter adapter = new ListViewAdapter();
		mListView.setAdapter(adapter);
	}

	protected int getLayoutId() {
		return R.layout.dialog_list_view;
	}

	protected void init(Context context) {
		View view = getLayoutInflater().inflate(getLayoutId(), null);
		setContentView(view);
		getWindow().setGravity(Gravity.CENTER);
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		initDialogViews();
		afterDialogViews();
//		Window win = getWindow();
//		WindowManager m = win.getWindowManager();
//		DisplayMetrics dm = new DisplayMetrics();
//		m.getDefaultDisplay().getMetrics(dm);
//		// Display d = m.getDefaultDisplay(); //
//		WindowManager.LayoutParams p = getWindow().getAttributes();
//		p.width = (int) (dm.widthPixels * 0.9);
//		p.height = (int) (dm.heightPixels * 0.6);
//		win.setAttributes(p);
		
		// 设置对话框透明度和宽高
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        Window window = getWindow();
        LayoutParams attributes = window.getAttributes();
        attributes.width = (int) (displayMetrics.widthPixels * 0.8);
        window.setAttributes(attributes);
	}

	protected void initDialogViews() {
//		View vTop = findViewById(R.id.v_top);
//		vTop.setOnClickListener(this);
//		View vBottom = findViewById(R.id.v_bottom);
//		vBottom.setOnClickListener(this);
		findViewById(R.id.fl_content).setOnClickListener(this);
		
		tvTitle = (TextView) findViewById(R.id.tv_title);
		
		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setOnItemClickListener(mListener);
	}
	
	private class ListViewAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;

		public ListViewAdapter() {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mItems != null ? mItems.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_dialog_list_view, parent, false);
				holder = new ViewHolder(convertView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String text = null;
			String content = null;
			
			Object obj = (Object) getItem(position);
			if (obj != null) {
				if (obj instanceof Room) {
					Room room = (Room) obj;
					text = room.name;
				} else if (obj instanceof ServiceHour) {
					ServiceHour hour = (ServiceHour) obj;
					text = hour.hour + "小时";
					content = "¥" + hour.price;
				} else if (obj instanceof String) {
					text = (String) obj;
				}
			}
			holder.text.setText(text);
			holder.content.setText(content);
			
			return convertView;
		}
		
	}
	
	private class ViewHolder {

		public TextView text;
		public TextView content;

		public ViewHolder(View convertView) {
			text = (TextView) convertView.findViewById(R.id.tv_name);
			content = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(this);
		}
		
	}

	@Override
	public void onClick(View arg0) {
		dismiss();
	}

}
