package com.dailysee.ui.sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.adapter.SaleAdapter;
import com.dailysee.bean.CityEntity;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.Preferential;
import com.dailysee.db.CityDb;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.PreferentialResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.ui.merchant.MerchantRoomListActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.widget.SelectRegionPopupWindow;
import com.dailysee.widget.SelectRegionPopupWindow.OnSelectListener;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SaleActivity extends BaseActivity implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener,  OnItemClickListener {

	private LinearLayout llFilter;
	private TextView tvFilter;

	private LinearLayout llNearby;
	private TextView tvNearby;
	
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;

	private int mIndex = 1;
	private List<Preferential> saleList = new ArrayList<Preferential>();
	private SaleAdapter mAdatper;
	
	private int filter = Constants.Filter.NEARBY;
	
	private SelectRegionPopupWindow mSelectRegionDialog = null;
	protected String mArea = "";
	protected String mRegion = "";
	
	private int mFrom = Constants.From.GIFT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale);
		
		onLoad(true);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			mFrom = intent.getIntExtra("from", Constants.From.SALE);
		}
		
		setTitle("天天优惠");
		setUp();
	}
	
	@Override
	public void onFindViews() {
		
		llFilter = (LinearLayout) findViewById(R.id.ll_filter);
		tvFilter = (TextView) findViewById(R.id.tv_filter);

		llNearby = (LinearLayout) findViewById(R.id.ll_nearby);
		tvNearby = (TextView) findViewById(R.id.tv_nearby);
		
		LinearLayout emptyView = (LinearLayout) findViewById(R.id.ll_no_data);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(this);
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setEmptyView(emptyView);
	}

	@Override
	public void onInitViewData() {
		tvFilter.setFocusable(true);
		tvFilter.setFocusableInTouchMode(true);
		tvFilter.requestFocus();
		tvFilter.requestFocusFromTouch();
		
		mAdatper = new SaleAdapter(getActivity(), saleList);
		mListView.setAdapter(mAdatper);
	}

	@Override
	public void onBindListener() {
		llFilter.setOnClickListener(this);
		llNearby.setOnClickListener(this);
		
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_filter:
			toSelectRegion();
			break;
		case R.id.ll_nearby:
			mPullRefreshListView.setRefreshing(false);
			break;
		}
	}
	
	private void showRegionPopupWindow() {
		if (mSelectRegionDialog == null) {
			mSelectRegionDialog = new SelectRegionPopupWindow(this, new OnSelectListener() {
				
				@Override
				public void onSelectListener(String title, String area, String region) {
					mSelectRegionDialog.dismiss();
					
					mArea = area;
					mRegion = region;
					
					tvFilter.setText(title);
					mPullRefreshListView.setRefreshing(false);
				}
			});
			mSelectRegionDialog.init();
		}
		mSelectRegionDialog.showAsDropDown(tvFilter, 0, mSelectRegionDialog.getHeight());
	}
	
	private void toSelectRegion() {
		CityDb db = new CityDb(this);
		int cityId = SpUtil.getInstance(this).getCityId();
		if (db.getCount(cityId) <= 0) {
			onLoadRegionInfo();
		} else {
			showRegionPopupWindow();
		}
	}
	
	public void onLoadRegionInfo() {
		final int cityId = SpUtil.getInstance(getActivity()).getCityId();
		
		// Tag used to cancel the request
		String tag = "tag_request_city";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				List<CityEntity> mAreaList = response.getListResponse(new TypeToken<List<CityEntity>>() {});
				if (mAreaList != null && mAreaList.size() > 0) {
					CityDb db = new CityDb(getActivity());
					db.saveCityRegionInfo(cityId, mAreaList);
					
					showRegionPopupWindow();
				} else {
					showToast("该城市暂无区域信息");
				}
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在加载区域");
			}

			@Override
			public void onFinished() {
				toCloseProgressMsg();
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.CityController.getCity");
				params.put("parentId", Integer.toString(cityId));
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		mIndex = 1;
		onLoad(false);
	}

	@Override
	public void onLastItemVisible() {
		mIndex++;
		onLoad(false);
	}

	public void onLoad(final boolean showProgress) {
		// Tag used to cancel the request
		String tag = "tag_request_get_merchant";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				PreferentialResponse preferentialResponse = (PreferentialResponse) response.getResponse(new TypeToken<PreferentialResponse>(){});
				if (mIndex == 1) {
					saleList.clear();
				}
				
				List<Preferential> list = preferentialResponse.rows;
				if (list != null && list.size() > 0) {
					saleList.addAll(list);
				}
				mAdatper.notifyDataSetChanged();
			}

			@Override
			public void onPreExecute() {
				if (showProgress) {
					toShowProgressMsg("正在加载...");
				}
			}

			@Override
			public void onFinished() {
				toCloseProgressMsg();
				mPullRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.PreferentialController.getClosePerferentials");
				params.put("jd", mSpUtil.getLng());
				params.put("wd", mSpUtil.getLat());
				params.put("prov", mSpUtil.getProvince());
				params.put("city", mSpUtil.getCity());
				params.put("area", mArea);
				params.put("landmark", mRegion);
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				params.put("token", mSpUtil.getToken());
				return params;
			}

		}, tag);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Merchant merchant = (Merchant) parent.getAdapter().getItem(position);
		if (merchant != null) {
			toMerchantRoomList(merchant);
		}
	}

	protected void toMerchantRoomList(Merchant merchant) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MerchantRoomListActivity.class);
		intent.putExtra("merchant", merchant);
		intent.putExtra("merchantId", merchant.merchantId);
		intent.putExtra("from", mFrom);
		startActivity(intent);
	}

}
