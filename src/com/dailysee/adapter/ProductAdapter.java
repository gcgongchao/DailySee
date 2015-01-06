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
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Product;
import com.dailysee.util.UiHelper;
import com.dailysee.util.Utils;

public class ProductAdapter extends BaseExpandableListAdapter {

	public static final int ADD_PRODUCT = 10001;
	public static final int REMOVE_PRODUCT =10002;

	private Context context;
	private ArrayList<String> mGroupList;
	private ArrayList<ArrayList<Product>> mChildrenList;
	private LayoutInflater mInflater;
	private Handler mHandler;

	public ProductAdapter(Context context, ArrayList<String> mGroupList, ArrayList<ArrayList<Product>> mChildrenList, Handler mHandler) {
		this.context = context;
		this.mGroupList = mGroupList;
		this.mChildrenList = mChildrenList;
		mInflater = LayoutInflater.from(context);
		this.mHandler = mHandler;
	}

	@Override
	public int getGroupCount() {
		return mGroupList != null ? mGroupList.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mChildrenList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mChildrenList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder holder = null;
    	if(null == convertView){
    		convertView = mInflater.inflate(R.layout.item_merchant_product_type, parent, false);
    		holder = new GroupViewHolder(convertView);
    	} else{
    		holder = (GroupViewHolder) convertView.getTag();
    	}
    	holder.mTvMerchantTitle.setText(mGroupList.get(groupPosition));

    	holder.mTvMerchantTitle.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black));
    	holder.mLlMerchantTitle.setBackgroundColor(isExpanded ? context.getResources().getColor(R.color.orange) : context.getResources().getColor(R.color.app_gray));
    	holder.mIvExpand.setImageResource(isExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
    	
        return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final ChildrenViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_product_list, null);
			holder = new ChildrenViewHolder(convertView);
		} else {
			holder = (ChildrenViewHolder) convertView.getTag();
		}

		final Product product = (Product) getChild(groupPosition, childPosition);

		if (product.imgs != null && product.imgs.size() > 0) {
			AppController.getInstance().getImageLoader().get(product.imgs.get(0).url, ImageLoader.getImageListener(holder.image, R.drawable.ic_image, R.drawable.ic_image));
			holder.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UiHelper.toBrowseImageList(context, product.imgs, 0);
				}
			});
		}
		
		if (product != null && product.count > 0) {
			holder.count.setText(Integer.toString(product.count));
			holder.btnRemove.setBackgroundResource(R.drawable.ic_remove_pressed);
			if (product.count > product.validCnt) {
				holder.btnAdd.setBackgroundResource(R.drawable.ic_add);
			} else {
				holder.btnAdd.setBackgroundResource(R.drawable.ic_add_pressed);
			}
		} else {
			holder.btnRemove.setBackgroundResource(R.drawable.ic_remove);
			holder.btnAdd.setBackgroundResource(R.drawable.ic_add_pressed);
			holder.count.setText("0");
		}

		holder.name.setText(product.name);
		holder.price.setText("原    价:￥" + Utils.formatTwoFractionDigits(product.price));
//		holder.price.setText("￥" + Utils.formatTwoFractionDigits(product.price));
//		holder.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰 
		
		String title = "天天价:￥" + Utils.formatTwoFractionDigits(product.ttPrice);
		SpannableStringBuilder style = new SpannableStringBuilder(title);
		style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 5, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
		holder.salePrice.setText(style);
//		String title = "￥" + Utils.formatTwoFractionDigits(product.ttPrice);
//		holder.salePrice.setText(title);
//		holder.salePrice.setTextColor(context.getResources().getColor(R.color.orange));
		
		holder.btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int count = getProductCount(holder.count);
				if (count > 0) {
					count --;
					holder.count.setText(Integer.toString(count));
				
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
					holder.count.setText(Integer.toString(count));
					
					Message msg = new Message();
					msg.what = ADD_PRODUCT;
					msg.obj = product;
					mHandler.sendMessage(msg);
//				}
			}
			
		});
		
        return convertView;
	}
	
	private int getProductCount(final TextView tvCount) {
		String countStr = tvCount.getText().toString();
		int count = Integer.parseInt(countStr);
		return count;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private static class GroupViewHolder {

		private LinearLayout mLlMerchantTitle;
		private TextView mTvMerchantTitle;
		private ImageView mIvExpand;

		public GroupViewHolder(View convertView) {
			mLlMerchantTitle = (LinearLayout) convertView.findViewById(R.id.ll_merchant_title);
			mTvMerchantTitle = (TextView) convertView.findViewById(R.id.tv_merchant_title);
			mIvExpand = (ImageView) convertView.findViewById(R.id.iv_expand);

			convertView.setTag(this);
		}

	}
	
	private static class ChildrenViewHolder {

		public ImageView image;
		public TextView name;
		public TextView salePrice;
		public TextView price;
		public ImageView btnRemove;
		public TextView count;
		public ImageView btnAdd;

		public ChildrenViewHolder(View convertView) {
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
