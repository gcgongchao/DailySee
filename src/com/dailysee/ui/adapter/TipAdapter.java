package com.dailysee.ui.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.Tip;

public class TipAdapter extends BaseAdapter {

	private Context context;
	private List<Tip> list;
	private LayoutInflater mInflater;

	public TipAdapter(Context context, List<Tip> tipList) {
		this.context = context;
		this.list = tipList;
		
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_tip, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Tip entity = list.get(position);
		
		holder.tvName.setText(entity.title);
		holder.tvFrom.setText("来自" + entity.companyName);
		holder.tvTime.setText(entity.createDate);
		
		return convertView;
	}
	
	private static class ViewHolder {
		
		private TextView tvName;
		private TextView tvTime;
		private TextView tvFrom;

		public ViewHolder(View convertView) {
			tvName = (TextView) convertView.findViewById(R.id.tv_name);
			tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			tvFrom = (TextView) convertView.findViewById(R.id.tv_from);
			convertView.setTag(this);
		}
		
	}

}
