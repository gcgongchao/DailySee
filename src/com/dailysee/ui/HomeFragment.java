package com.dailysee.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

import com.dailysee.R;
import com.dailysee.adapter.AdAdapter;
import com.dailysee.bean.Ad;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.AdResponse;
import com.dailysee.ui.base.BaseFragment;
import com.dailysee.ui.merchant.MerchantActivity;
import com.google.gson.reflect.TypeToken;

public class HomeFragment extends BaseFragment implements OnClickListener, OnPageChangeListener {

	protected static final String TAG = HomeFragment.class.getSimpleName();
	
	private ViewPager mViewPager;
	private ImageView[] mAdDots;
	
	private ImageView ivMerchant;
	private ImageView ivSale;
	private ImageView ivGift;
	private ImageView ivConsultant;
	
	private boolean mLoadAdRequired = true;

	public HomeFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void request() {
	}

	@Override
	public void onInit() {
		
	}

	@Override
	public void onFindViews() {
		View v = getView();
		
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		
		mAdDots = new ImageView[10];
		mAdDots[0] = (ImageView) v.findViewById(R.id.iv_dot1);
		mAdDots[1] = (ImageView) v.findViewById(R.id.iv_dot2);
		mAdDots[2] = (ImageView) v.findViewById(R.id.iv_dot3);
		mAdDots[3] = (ImageView) v.findViewById(R.id.iv_dot4);
		mAdDots[4] = (ImageView) v.findViewById(R.id.iv_dot5);
		mAdDots[5] = (ImageView) v.findViewById(R.id.iv_dot6);
		mAdDots[6] = (ImageView) v.findViewById(R.id.iv_dot7);
		mAdDots[7] = (ImageView) v.findViewById(R.id.iv_dot8);
		mAdDots[8] = (ImageView) v.findViewById(R.id.iv_dot9);
		mAdDots[9] = (ImageView) v.findViewById(R.id.iv_dot10);
		
		ivMerchant = (ImageView) v.findViewById(R.id.iv_merchant);
		ivSale = (ImageView) v.findViewById(R.id.iv_sale);
		ivGift = (ImageView) v.findViewById(R.id.iv_gift);
		ivConsultant = (ImageView) v.findViewById(R.id.iv_consultant);
	}

	@Override
	public void onInitViewData() {
		setAdDotVisibile(0);
	}

	@Override
	public void onBindListener() {
		ivMerchant.setOnClickListener(this);
		ivSale.setOnClickListener(this);
		ivGift.setOnClickListener(this);
		ivConsultant.setOnClickListener(this);
	}

	private void setAdDotVisibile(int pos) {
		for (int i = 0; i < mAdDots.length; i++) {
			if (i < pos) {
				mAdDots[i].setVisibility(View.VISIBLE);
			} else {
				mAdDots[i].setVisibility(View.GONE);
			}
		}
	}
	
	private void setAdDotSelected(int pos) {
		for (int i = 0; i < mAdDots.length; i++) {
			mAdDots[i].setSelected(false);
		}
		
		mAdDots[pos].setSelected(true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_merchant:
			toMerchant();
			break;
		case R.id.iv_sale:
			toSale();
			break;
		case R.id.iv_gift:
			toGift();
			break;
		case R.id.iv_consultant:
			toConsultant();
			break;
		default:
			break;
		}
	}

	private void toMerchant() {
		Intent intent = new Intent();
		intent.setClass(mContext, MerchantActivity.class);
		startActivity(intent);
	}

	private void toSale() {
		
	}

	private void toGift() {
		
	}

	private void toConsultant() {
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onLoadAd();
	}
	
	private void onLoadAd() {
		if (!mLoadAdRequired) {
			return;
		}

		// Tag used to cancel the request
		String tag = "tag_request_get_member_detail";
		NetRequest.getInstance(mContext).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mLoadAdRequired = false;
				
				AdResponse adResponse = (AdResponse) response.getResponse(new TypeToken<AdResponse>() {});
				if (adResponse != null && adResponse.rows != null && adResponse.rows.size() > 0) {
					onRefreshAd(adResponse.rows);
				}
			}
			
			@Override
			public void onPreExecute() {
				super.onPreExecute();
				toShowProgressMsg("正在加载");
			}
			
			@Override
			public void onFinished() {
				super.onFinished();
				toCloseProgressMsg();
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.AdController.getAds");
				params.put("pageNo", "1");
				params.put("pageSize", "10");
				return params;
			}
		}, tag);
	}
	
	private void onRefreshAd(List<Ad> adList) {
		ArrayList<View> imageViewList = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		for (int i = 0; i < adList.size(); i++) {
			LinearLayout view = (LinearLayout) inflater.inflate(R.layout.item_ad, null);
			ImageView imageView = (ImageView) view.getChildAt(0);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViewList.add(view);
		}
		mViewPager.setAdapter(new AdAdapter(adList, imageViewList));
		mViewPager.setOnPageChangeListener(this);
		
		setAdDotVisibile(adList.size());
		setAdDotSelected(0);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int pos) {
		setAdDotSelected(pos);
	}

}
