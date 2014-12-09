package com.dailysee.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Product;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;

public class ProductAdapter extends BaseAdapter {

	public static final int EDIT_PRODUCT = 10001;
	public static final int UPDATE_PRODUCT_STATUS =10002;
	
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<Product> items;
	private Handler mHandler;

	public ProductAdapter(Context context, ArrayList<Product> items, Handler mHandler) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.items = items;
		this.mHandler = mHandler;
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
			convertView = mInflater.inflate(R.layout.item_product_list, null);
			holder = new ViewHolder(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Product product = (Product) getItem(position);

		if (product.imgs != null && product.imgs.size() > 0) {
			AppController.getInstance().getImageLoader().get(product.imgs.get(0).url, ImageLoader.getImageListener(holder.image, R.drawable.ic_image, R.drawable.ic_image));
			holder.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UiHelper.toBrowseImageList(context, product.imgs, 0);
				}
			});
		}

		holder.name.setText(product.name);
		holder.price.setText("原    价: ￥" + Utils.formatTwoFractionDigits(product.price));
		
		String title = "天天价: ￥" + Utils.formatTwoFractionDigits(product.ttPrice);
		SpannableStringBuilder style = new SpannableStringBuilder(title);
		style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 5, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
		holder.salePrice.setText(style);
		
		if (product.isUp()) {// 上架状态
			holder.btnSoldOut.setText("下架");
			holder.btnSoldOut.setBackgroundResource(R.drawable.btn_orange);
		} else {
			holder.btnSoldOut.setText("上架");
			holder.btnSoldOut.setBackgroundResource(R.drawable.btn_green);
		}
		holder.btnSoldOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.what = UPDATE_PRODUCT_STATUS;
				msg.obj = product;
				mHandler.sendMessage(msg);
			}
			
		});
		holder.btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.what = EDIT_PRODUCT;
				msg.obj = product;
				mHandler.sendMessage(msg);
			}
			
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(context, ShopDetailActivity.class);
//				intent.putExtra("shop", room);
//				context.startActivity(intent);
			}
		});

		return convertView;
	}

	private static class ViewHolder {

		public ImageView image;
		public TextView name;
		public TextView salePrice;
		public TextView price;
		public TextView btnSoldOut;
		public TextView btnEdit;

		public ViewHolder(View convertView) {
			image = (ImageView) convertView.findViewById(R.id.iv_image);
			name = (TextView) convertView.findViewById(R.id.tv_name);
			salePrice = (TextView) convertView.findViewById(R.id.tv_sale_price);
			price = (TextView) convertView.findViewById(R.id.tv_price);
			btnSoldOut = (TextView) convertView.findViewById(R.id.tv_sold_out);
			btnEdit = (TextView) convertView.findViewById(R.id.tv_edit);

			convertView.setTag(this);
		}

	}

}
