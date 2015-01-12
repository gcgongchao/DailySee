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

	public static final int ADD_PRODUCT = 10001;
	public static final int REMOVE_PRODUCT =10002;

	private Context context;
	private ArrayList<Product> mList;
	private LayoutInflater mInflater;
	private Handler mHandler;

	public ProductAdapter(Context context, ArrayList<Product> mChildrenList, Handler mHandler) {
		this.context = context;
		this.mList = mChildrenList;
		mInflater = LayoutInflater.from(context);
		this.mHandler = mHandler;
	}


	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
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
			AppController.getInstance().getImageLoader().get(product.imgs.get(0).url, ImageLoader.getImageListener(holder.image, R.drawable.ic_noimage, R.drawable.ic_noimage));
			holder.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UiHelper.toBrowseImageList(context, product.imgs, 0);
				}
			});
		}
		
//		product.count= AppController.getInstance().findCountInShoppingCart(product.productId);
		int count = AppController.getInstance().findCountInShoppingCart(product.productId);
//		int count = product.count;
		setBtnEnable(holder, product, count);

		holder.name.setText(product.name);
		holder.price.setText("原    价:¥" + Utils.formatTwoFractionDigits(product.price));
//		holder.price.setText("¥" + Utils.formatTwoFractionDigits(product.price));
//		holder.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰 
		
		String title = "天天价:¥" + Utils.formatTwoFractionDigits(product.ttPrice);
		SpannableStringBuilder style = new SpannableStringBuilder(title);
		style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 4, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
		holder.salePrice.setText(style);
//		String title = "¥" + Utils.formatTwoFractionDigits(product.ttPrice);
//		holder.salePrice.setText(title);
//		holder.salePrice.setTextColor(context.getResources().getColor(R.color.orange));
		
		holder.btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int count = getProductCount(holder.count);
				if (count > 0) {
					count --;
					setBtnEnable(holder, product, count);
				
					Message msg = new Message();
					msg.what = REMOVE_PRODUCT;
					msg.obj = product;
					mHandler.sendMessage(msg);
				}
			}
			
		});
		holder.btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int count = getProductCount(holder.count);
				count ++;
//				if (count <= product.validCnt) {
					setBtnEnable(holder, product, count);
					
					Message msg = new Message();
					msg.what = ADD_PRODUCT;
					msg.obj = product;
					mHandler.sendMessage(msg);
//				}
			}
			
		});
		
        return convertView;
	}


	private void setBtnEnable(final ViewHolder holder, final Product product, int count) {
		holder.count.setText(Integer.toString(count));
		if (count > 0) {
			holder.btnRemove.setEnabled(true);
			holder.btnRemove.setImageResource(R.drawable.ic_remove_pressed);
//			if (count >= product.validCnt) {
//				holder.btnAdd.setEnabled(false);
//				holder.btnAdd.setImageResource(R.drawable.ic_add);
//			} else {
				holder.btnAdd.setEnabled(true);
				holder.btnAdd.setImageResource(R.drawable.ic_add_pressed);
//			}
		} else {
			holder.btnRemove.setEnabled(false);
			holder.btnRemove.setImageResource(R.drawable.ic_remove);
			holder.btnAdd.setEnabled(true);
			holder.btnAdd.setImageResource(R.drawable.ic_add_pressed);
		}
	}
	
	private int getProductCount(final TextView tvCount) {
		String countStr = tvCount.getText().toString();
		int count = Integer.parseInt(countStr);
		return count;
	}

	private static class ViewHolder {

		public ImageView image;
		public TextView name;
		public TextView salePrice;
		public TextView price;
		public ImageView btnRemove;
		public TextView count;
		public ImageView btnAdd;

		public ViewHolder(View convertView) {
			image = (ImageView) convertView.findViewById(R.id.iv_image);
			name = (TextView) convertView.findViewById(R.id.tv_name);
			salePrice = (TextView) convertView.findViewById(R.id.tv_sale_price);
			price = (TextView) convertView.findViewById(R.id.tv_price);
			btnRemove = (ImageView) convertView.findViewById(R.id.iv_remove);
			count = (TextView) convertView.findViewById(R.id.tv_count);
			btnAdd = (ImageView) convertView.findViewById(R.id.iv_add);

			convertView.setTag(this);
		}

	}

}
