package com.dailysee.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Ad;
import com.dailysee.ui.tip.TipDetailActivity;

public class AdAdapter extends PagerAdapter {

	private List<View> mListViews;
	private List<Ad> adList;
	private Context context;

	public AdAdapter(Context context, List<Ad> adList, List<View> mListViews) {
		this.context = context;
		this.adList = adList;
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		View view = mListViews.get(position);
		container.addView(view, 0);// 添加页卡
		ImageView imageView = (ImageView) ((LinearLayout) view).getChildAt(0);
		
		final Ad ad = adList.get(position);
		String url = ad.logoUrl;
		if (TextUtils.isEmpty(url) && ad.imgs != null && ad.imgs.size() > 0 && ad.imgs.get(0) != null) {
			url = ad.imgs.get(0).url;
		}
		if (!TextUtils.isEmpty(url)) {
			AppController.getInstance().getImageLoader().get(url, ImageLoader.getImageListener(imageView, R.drawable.ic_noimage_l, R.drawable.ic_noimage_l));
		}
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dispatchForward(ad);
			}
		});
		return view;
	}

	protected void dispatchForward(Ad ad) {
		if ("TIP".equals(ad.adType)) {
			toTipDetail(ad);
		} else if ("PREFERENTIAL".equals(ad.adType)) {
			toSaleDetail(ad);
		}
	}

	private void toSaleDetail(Ad ad) {
		Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
	}

	private void toTipDetail(Ad ad) {
		Intent intent = new Intent();
		intent.setClass(context, TipDetailActivity.class);
		intent.putExtra("tipId", ad.belongAdId);
		context.startActivity(intent);
	}

	@Override
	public int getCount() {
		return mListViews.size();// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}
}