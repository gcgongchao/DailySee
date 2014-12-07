package com.dailysee.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.Tip;
import com.dailysee.util.TipSpUtil;

public class TipAdapter extends BaseAdapter {

	private Context context;
	private List<Tip> list;
	private LayoutInflater mInflater;
	private TipSpUtil mTipSpUtil;

	public TipAdapter(Context context, List<Tip> tipList) {
		this.context = context;
		this.list = tipList;
		
		mInflater = LayoutInflater.from(context);
		mTipSpUtil = TipSpUtil.getInstance(context);
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
		
		if (!mTipSpUtil.isRead(entity.tipId)) {
			holder.tvName.setTextColor(context.getResources().getColor(R.color.black));
		} else {
			holder.tvName.setTextColor(context.getResources().getColor(R.color.gray));
		}
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
