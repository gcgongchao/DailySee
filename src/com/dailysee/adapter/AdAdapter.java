package com.dailysee.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.bean.Ad;

public class AdAdapter extends PagerAdapter {

	private List<View> mListViews;
	private List<Ad> adList;

	public AdAdapter(List<Ad> adList, List<View> mListViews) {
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
		
		String url = adList.get(position).logoUrl;
		if (!TextUtils.isEmpty(url)) {
			AppController.getInstance().getImageLoader().get(adList.get(position).logoUrl, ImageLoader.getImageListener(imageView, R.drawable.ic_ad, R.drawable.ic_ad));
		}
		return view;
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