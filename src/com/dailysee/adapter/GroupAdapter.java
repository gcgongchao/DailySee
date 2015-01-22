package com.dailysee.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.ProductType;
import com.dailysee.bean.RoomType;
import com.dailysee.util.UiHelper;

public class GroupAdapter extends BaseAdapter {

	private Context context;
	private int selectedItem = 0;// 默认不限
	private ArrayList<Object> list;
	private LayoutInflater inflater;
	private int colorTabNormal;
	private int colorTabPressed;
	private AbsListView.LayoutParams params;
	private int width;

	public GroupAdapter(Context context, ArrayList<Object> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		width = UiHelper.getDisplayMetrics(context).widthPixels / 6;

		params = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);

		colorTabNormal = context.getResources().getColor(R.color.light_gray);
		colorTabPressed = context.getResources().getColor(R.color.white);
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setData(ArrayList<Object> data) {
		this.list = data;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {

		ViewHolder holder = null;
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.item_group, arg2, false);
			arg1.setLayoutParams(params);
			holder = new ViewHolder(arg1);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}

		String name = null;
		
		Object obj = getItem(position);
		if (obj instanceof RoomType) {
			RoomType roomType = (RoomType) obj;
			name = roomType.name;
		} else {
			ProductType productType = (ProductType) obj;
			name = productType.name;
		}
			
		holder.tabTv.setText(name);
		if (selectedItem == position) {
			holder.tabTv.setTextColor(colorTabPressed);
		} else {
			holder.tabTv.setTextColor(colorTabNormal);
		}
		// holder.dividerIv.setVisibility((position == list.size() - 1) ?
		// View.GONE : View.VISIBLE);
		// holder.tabTv.setSelected(selectedItem == position);

		return arg1;
	}

	public void setSelectedItem(int selectedItem) {
		this.selectedItem = selectedItem;
	}

	private static class ViewHolder {

		public TextView tabTv;

		// public ImageView dividerIv;

		public ViewHolder(View arg1) {
			tabTv = (TextView) arg1.findViewById(R.id.tv_group);
			// dividerIv = (ImageView) arg1.findViewById(R.id.iv_divider);

			arg1.setTag(this);
		}

	}

}
