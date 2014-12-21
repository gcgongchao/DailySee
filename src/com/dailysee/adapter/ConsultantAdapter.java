package com.dailysee.adapter;

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
import com.dailysee.bean.Consultant;
import com.dailysee.bean.Merchant;

public class ConsultantAdapter extends BaseAdapter {

	private Context context;
	private List<Consultant> items;
	private LayoutInflater mInflater;

	public ConsultantAdapter(Context context, List<Consultant> items) {
		this.context = context;
		this.items = items;
		
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size();
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_merchant, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Consultant merchant = items.get(position);
		
		if (!TextUtils.isEmpty(merchant.logoUrl)) {
			AppController.getInstance().getImageLoader().get(merchant.logoUrl, ImageLoader.getImageListener(holder.ivImage, R.drawable.ic_image_merchant, R.drawable.ic_image_merchant));
		}
//		holder.rbHot.setRating(merchant.getHot());
		holder.tvName.setText(merchant.name);
		holder.tvAddress.setText(merchant.addr);
		holder.tvDesc.setText(merchant.introduction);
		
		return convertView;
	}
	
	private static class ViewHolder {
		
		private ImageView ivImage;
		private RatingBar rbHot;
		private TextView tvName;
		private TextView tvAddress;
		private TextView tvDesc;

		public ViewHolder(View convertView) {
			ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
			rbHot = (RatingBar) convertView.findViewById(R.id.rb_hot);
			tvName = (TextView) convertView.findViewById(R.id.tv_name);
			tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
			tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
			convertView.setTag(this);
		}
		
	}

}
