package com.dailysee.ui.consultant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.dailysee.adapter.ConsultantAdapter;
import com.dailysee.bean.CityEntity;
import com.dailysee.bean.Consultant;
import com.dailysee.db.CityDb;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.ConsultantResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;
import com.dailysee.widget.SelectRegionPopupWindow;
import com.dailysee.widget.SelectRegionPopupWindow.OnSelectListener;
import com.dailysee.widget.SexFilterPopupWindow;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ConsultantActivity extends BaseActivity implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener, OnTouchListener, OnItemClickListener {

	private LinearLayout llFilter;
	private TextView tvFilter;

	private LinearLayout llRecommented;
	private TextView tvRecommented;

	private LinearLayout llNearby;
	private TextView tvNearby;
	
	private LinearLayout llSexBoy;
	private TextView tvSexBoy;
	
	private LinearLayout llSexGirl;
	private TextView tvSexGirl;

	private LinearLayout llMore;
	private TextView tvMore;
	
	private EditText etSearch;
	private ImageView ivSearch;

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;

	private int mIndex = 1;
	private List<Consultant> consultantList = new ArrayList<Consultant>();
	private ConsultantAdapter mAdatper;
	
	private int filter = Constants.Filter.RECOMMEND;
	private String sexFilter = Constants.Sex.ALL;
	
	private SelectRegionPopupWindow mSelectRegionDialog = null;
	protected String mArea = "";
	protected String mRegion = "";
	
//	private SelectBookingDateDialog mSelectBookingDateDialog;
	private String mSearch = "";
	private int mFrom = Constants.From.CONSULTANT;
	
	private SexFilterPopupWindow mSexFilterPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consultant);
		
		onLoad(true);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			mFrom = intent.getIntExtra("from", Constants.From.MERCHANT);
		}
		
		setTitle("商务公关");
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

		llSexBoy = (LinearLayout) findViewById(R.id.ll_sex_boy);
		tvSexBoy = (TextView) findViewById(R.id.tv_sex_boy);

		llSexGirl = (LinearLayout) findViewById(R.id.ll_sex_girl);
		tvSexGirl = (TextView) findViewById(R.id.tv_sex_girl);

		llMore = (LinearLayout) findViewById(R.id.ll_more);
		tvMore = (TextView) findViewById(R.id.tv_more);
		
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
		
		mAdatper = new ConsultantAdapter(getActivity(), consultantList);
		mListView.setAdapter(mAdatper);
	}

	@Override
	public void onBindListener() {
		llFilter.setOnClickListener(this);
		llRecommented.setOnClickListener(this);
		llNearby.setOnClickListener(this);
		llSexBoy.setOnClickListener(this);
		llSexGirl.setOnClickListener(this);
		llMore.setOnClickListener(this);
		
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mSearch = s.toString();
			}
		});
		ivSearch.setOnClickListener(this);

		llFilter.setOnTouchListener(this);
		llRecommented.setOnTouchListener(this);
		llNearby.setOnTouchListener(this);
		llMore.setOnTouchListener(this);
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
				mIndex = 1;
				startRefresh();
			}
			break;
		case R.id.ll_nearby:
			tvRecommented.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			tvNearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			if (filter != Constants.Filter.NEARBY) {
				filter = Constants.Filter.NEARBY;
				mIndex = 1;
				startRefresh();
			}
			break;
		case R.id.ll_sex_boy:
			tvSexBoy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			tvSexGirl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			if (!Constants.Sex.MEN.equals(sexFilter)) {
				sexFilter = Constants.Sex.MEN;
				mIndex = 1;
				startRefresh();
			}
			break;
		case R.id.ll_sex_girl:
			tvSexBoy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			tvSexGirl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			if (!Constants.Sex.WOMEN.equals(sexFilter)) {
				sexFilter = Constants.Sex.WOMEN;
				mIndex = 1;
				startRefresh();
			}
			break;
		case R.id.ll_more:
			showMoreFilterDialog();
			break;
		case R.id.iv_search:
			clearSearchFocus();
			if (!TextUtils.isEmpty(mSearch)) {
				mIndex = 1;
				startRefresh();
			}
			break;
		}
	}

	private void showMoreFilterDialog() {
		if (mSexFilterPopupWindow == null) {
			mSexFilterPopupWindow = new SexFilterPopupWindow(getActivity(), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mSexFilterPopupWindow.dismiss();
					switch (v.getId()) {
					case R.id.tv_filter_all:
						sexFilter = Constants.Sex.ALL;
						break;
					case R.id.tv_filter_women:
						sexFilter = Constants.Sex.WOMEN;
						break;
					case R.id.tv_filter_men:
						sexFilter = Constants.Sex.MEN;
						break;
					}
					mIndex = 1;
					startRefresh();
				}
			});
			mSexFilterPopupWindow.init();
		}
		mSexFilterPopupWindow.showAsDropDown(tvMore, 0, mSexFilterPopupWindow.getHeight());
	}

	private void startRefresh() {
		mPullRefreshListView.demo();
		mPullRefreshListView.setRefreshing(false);
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
					startRefresh();
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
				ConsultantResponse consultantResponse = (ConsultantResponse) response.getResponse(new TypeToken<ConsultantResponse>(){});
				if (mIndex == 1) {
					consultantList.clear();
				}
				
				List<Consultant> list = consultantResponse.rows;
				if (list != null && list.size() > 0) {
					consultantList.addAll(list);
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
					params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getRecAdvisor");
				} else {
					params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getAdvisor");
					params.put("jd", mSpUtil.getLng());
					params.put("wd", mSpUtil.getLat());
				}
				params.put("prov", mSpUtil.getProvince());
				params.put("city", mSpUtil.getCity());
				params.put("area", mArea);
				params.put("landmark", mRegion);
				if (!TextUtils.isEmpty(mSearch)) {
					params.put("name", mSearch);
				}
				if (!TextUtils.isEmpty(sexFilter)) {
					params.put("sex", sexFilter);
				}
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				return params;
			}

		}, tag);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Consultant consultant = (Consultant) parent.getAdapter().getItem(position);
		if (consultant != null) {
			toConsultantDetail(consultant);
		}
	}

	protected void toConsultantDetail(Consultant consultant) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ConsultantDetailActivity.class);
		intent.putExtra("consultant", consultant);
		intent.putExtra("from", mFrom);
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		clearSearchFocus();
		return super.onTouchEvent(event);
	}

}
