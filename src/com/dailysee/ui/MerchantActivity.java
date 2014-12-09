package com.dailysee.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.adapter.MerchantAdapter;
import com.dailysee.bean.CityEntity;
import com.dailysee.bean.Merchant;
import com.dailysee.db.CityDb;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.MerchantResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;
import com.dailysee.widget.SelectRegionPopupWindow;
import com.dailysee.widget.SelectRegionPopupWindow.OnSelectListener;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

public class MerchantActivity extends BaseActivity implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener, OnTouchListener, OnItemClickListener {

	private LinearLayout llFilter;
	private TextView tvFilter;

	private LinearLayout llRecommented;
	private TextView tvRecommented;

	private LinearLayout llNearby;
	private TextView tvNearby;
	
	private EditText etSearch;
	private ImageView ivSearch;

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;

	private int mIndex = 1;
	private List<Merchant> merchantList = new ArrayList<Merchant>();
	private MerchantAdapter mAdatper;
	
	private int filter = Constants.Filter.RECOMMEND;
	
	private SelectRegionPopupWindow mSelectRegionDialog = null;
	protected String mArea = "";
	protected String mRegion = "";
	
	private SelectBookingDateDialog mSelectBookingDateDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant);
		
		onLoad(true);
	}

	@Override
	public void onInit() {
		setTitle("天天商家");
		setUp();
	}

	@Override
	public void onFindViews() {
		llFilter = (LinearLayout) findViewById(R.id.ll_filter);
		tvFilter = (TextView) findViewById(R.id.tv_filter);

		llRecommented = (LinearLayout) findViewById(R.id.ll_recommend);
		tvRecommented = (TextView) findViewById(R.id.tv_recommend);

		llNearby = (LinearLayout) findViewById(R.id.ll_nearby);
		tvNearby = (TextView) findViewById(R.id.tv_nearby);
		
		etSearch = (EditText) findViewById(R.id.et_search);
		ivSearch = (ImageView) findViewById(R.id.iv_search);
		
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
		
		mAdatper = new MerchantAdapter(getActivity(), merchantList);
		mListView.setAdapter(mAdatper);
	}

	@Override
	public void onBindListener() {
		llFilter.setOnClickListener(this);
		llRecommented.setOnClickListener(this);
		llNearby.setOnClickListener(this);
		
		ivSearch.setOnClickListener(this);
		
		mListView.setOnTouchListener(this);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_filter:
			toSelectRegion();
			break;
		case R.id.ll_recommend:
			tvRecommented.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			tvNearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			if (filter != Constants.Filter.RECOMMEND) {
				filter = Constants.Filter.RECOMMEND;
				mPullRefreshListView.setRefreshing(false);
			}
			break;
		case R.id.ll_nearby:
			tvRecommented.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			tvNearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			if (filter != Constants.Filter.NEARBY) {
				filter = Constants.Filter.NEARBY;
				mPullRefreshListView.setRefreshing(false);
			}
			break;
		case R.id.iv_search:
			clearSearchFocus();
			String search = etSearch.getText().toString();
			if (!TextUtils.isEmpty(search)) {
				onLoad(true);
			}
			break;
		}
	}

	private void clearSearchFocus() {
		etSearch.clearFocus();
		Utils.hideSoft(getActivity(), etSearch);
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
				MerchantResponse merchantResponse = (MerchantResponse) response.getResponse(new TypeToken<MerchantResponse>(){});
				if (mIndex == 1) {
					merchantList.clear();
				}
				
				List<Merchant> list = merchantResponse.rows;
				if (list != null && list.size() > 0) {
					merchantList.addAll(list);
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
				if (filter == Constants.Filter.RECOMMEND) {
					params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getRecMerchant");
				} else {
					params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getMerchant");
					params.put("jd", mSpUtil.getLng());
					params.put("wd", mSpUtil.getLat());
				}
				params.put("prov", mSpUtil.getProvince());
				params.put("city", mSpUtil.getCity());
				params.put("area", mArea);
				params.put("landmark", mRegion);
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				return params;
			}

		}, tag);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Merchant merchant = (Merchant) parent.getAdapter().getItem(position);
		if (merchant != null) {
			showSelectBookingDateDialog(merchant);
		}
	}

	private void showSelectBookingDateDialog(final Merchant merchant) {
		if (mSelectBookingDateDialog == null) {
			mSelectBookingDateDialog = new SelectBookingDateDialog(getActivity(), "选择预订日期", new OnDateSelectedListener() {
				
				@Override
				public void onDateUnselected(Date date) {
					
				}
				
				@Override
				public void onDateSelected(Date date) {
					Utils.clossDialog(mSelectBookingDateDialog);
					
					String dateStr = Utils.formatDate(date, Utils.DATE_FORMAT_YMD);
					showToast(dateStr);
					toMerchantDetail(merchant, dateStr);
				}
			});
		}
		mSelectBookingDateDialog.show();
	}

	protected void toMerchantDetail(Merchant merchant, String date) {
//		Intent intent = new Intent();
//		intent.setClass(getActivity(), MerchantDetailActivity.class);
//		intent.putExtra("merchantId", merchant.merchantId);
//		intent.putExtra("date", date);
//		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		clearSearchFocus();
		return super.onTouchEvent(event);
	}

}
