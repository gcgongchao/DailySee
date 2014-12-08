package com.dailysee.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.CityEntity;

public class CityAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater mInflater;
	private List<CityEntity> items;

	public CityAdapter(Context context, List<CityEntity> items) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public int getCount() {
		return items != null ? items.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_city, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CityEntity city = (CityEntity) getItem(position);
		holder.name.setText(city.name);
		
		return convertView;
	}

	private static class ViewHolder {

		public TextView name;

		public ViewHolder(View convertView) {
			name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(this);
		}

	}

	public void setList(List<CityEntity> list) {
		items.clear();
		if (list != null && list.size() > 0) {
			items.addAll(list);
		}
		notifyDataSetChanged();
	}

}
