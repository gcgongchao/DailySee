package com.dailysee.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.Product;
import com.dailysee.util.Utils;

public class ConfirmOrderAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater mInflater;
	private List<Product> items;

	public ConfirmOrderAdapter(Context context, List<Product> items) {
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
			convertView = mInflater.inflate(R.layout.item_confirm_order, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Product product = (Product) getItem(position);
		holder.name.setText(product.name);
		holder.count.setText("X" + product.count);
		holder.price.setText("￥" + Utils.formatTwoFractionDigits(product.ttPrice));

		return convertView;
	}

	private static class ViewHolder {

		public TextView name;
		public TextView count;
		public TextView price;

		public ViewHolder(View convertView) {
			name = (TextView) convertView.findViewById(R.id.tv_name);
			count = (TextView) convertView.findViewById(R.id.tv_count);
			price = (TextView) convertView.findViewById(R.id.tv_price);
			convertView.setTag(this);
		}
	}

}
