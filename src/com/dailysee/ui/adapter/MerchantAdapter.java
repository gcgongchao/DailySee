package com.dailysee.ui.adapter;

import java.util.List;

import android.content.Context;
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

public class MerchantAdapter extends BaseAdapter {

	private Context context;
	private List<Merchant> merchantList;
	private LayoutInflater mInflater;

	public MerchantAdapter(Context context, List<Merchant> merchantList) {
		this.context = context;
		this.merchantList = merchantList;
		
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return merchantList.size();
	}

	@Override
	public Object getItem(int position) {
		return merchantList.get(position);
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
		
		Merchant merchant = merchantList.get(position);
		
		AppController.getInstance().getImageLoader().get(merchant.logoUrl, ImageLoader.getImageListener(holder.ivImage, R.drawable.ic_image_merchant, R.drawable.ic_image_merchant));
		holder.rbHot.setRating(Float.parseFloat(merchant.redu));
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
