package com.dailysee.ui.merchant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.dailysee.widget.MyDatePickerDialog;
import com.dailysee.widget.SelectRegionPopupWindow;
import com.dailysee.widget.SelectRegionPopupWindow.OnSelectListener;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MerchantActivity extends BaseActivity implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener, OnTouchListener, OnItemClickListener {

	protected static final String TAG = MerchantActivity.class.getSimpleName();
	
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
	
//	private SelectBookingDateDialog mSelectBookingDateDialog;
	private String mSearch = "";
	private int mFrom = Constants.From.MERCHANT;

	private LinearLayout emptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant);
		
		onLoad(true);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			mFrom = intent.getIntExtra("from", Constants.From.MERCHANT);
		}
	}
	
	public String getFromTitle() {
		String title = "";
		switch (mFrom) {
		case Constants.From.MERCHANT:
			filter = Constants.Filter.RECOMMEND;
			llRecommented.setVisibility(View.VISIBLE);
			tvRecommented.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			tvNearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			title = "天天商家";
			break;
		case Constants.From.GIFT:
			filter = Constants.Filter.NEARBY;
			llRecommented.setVisibility(View.GONE);
			tvRecommented.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
			tvNearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			title = "选择赠送地址";
			break;
		}
		return title;
	}

	@Override
	public void onFindViews() {
		setUp();
		
		llFilter = (LinearLayout) findViewById(R.id.ll_filter);
		tvFilter = (TextView) findViewById(R.id.tv_filter);

		llRecommented = (LinearLayout) findViewById(R.id.ll_recommend);
		tvRecommented = (TextView) findViewById(R.id.tv_recommend);

		llNearby = (LinearLayout) findViewById(R.id.ll_nearby);
		tvNearby = (TextView) findViewById(R.id.tv_nearby);
		
		setTitle(getFromTitle());
		
		etSearch = (EditText) findViewById(R.id.et_search);
		ivSearch = (ImageView) findViewById(R.id.iv_search);
		
		emptyView = (LinearLayout) findViewById(R.id.ll_no_data);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(this);
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		
		mListView = mPullRefreshListView.getRefreshableView();
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
			if (!TextUtils.isEmpty(mSearch)) {
				mIndex = 1;
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
				MerchantResponse merchantResponse = (MerchantResponse) response.getResponse(new TypeToken<MerchantResponse>(){});
				if (mIndex == 1) {
					merchantList.clear();
				}
				
				List<Merchant> list = merchantResponse.rows;
				if (list != null && list.size() > 0) {
					merchantList.addAll(list);
				} else {
					mListView.setEmptyView(emptyView);
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
				if (!TextUtils.isEmpty(mSearch)) {
					params.put("name", mSearch);
				}
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
//			showSelectBookingDateDialog(merchant);
			toMerchantRoomList(merchant);
		}
	}

	private void showSelectBookingDateDialog(final Merchant merchant) {
//		mSelectBookingDateDialog = new SelectBookingDateDialog(getActivity(), "选择预订日期", new OnDateSelectedListener() {
//			
//			@Override
//			public void onDateUnselected(Date date) {
//				
//			}
//			
//			@Override
//			public void onDateSelected(Date date) {
//				Utils.clossDialog(mSelectBookingDateDialog);
//				
//				String dateStr = Utils.formatDate(date, Utils.DATE_FORMAT_YMD);
//				toMerchantRoomList(merchant, dateStr);
//			}
//		});
//		mSelectBookingDateDialog.show();
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		
		MyDatePickerDialog dialog = new MyDatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                    	if (!Utils.isFastDoubleClick()) {
	                    	String dateStr = year + "-" + (month+1) + "-" + dayOfMonth;
	                    	toMerchantRoomList(merchant, dateStr);
                    	}
                    }
                }, 
                year, // 传入年份
                month, // 传入月份
                dayOfMonth // 传入天数
            );
		dialog.show();
	}

	protected void toMerchantRoomList(Merchant merchant, String date) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MerchantRoomListActivity.class);
		intent.putExtra("merchant", merchant);
		intent.putExtra("merchantId", merchant.merchantId);
		intent.putExtra("date", date);
		intent.putExtra("from", mFrom);
		startActivity(intent);
	}

	protected void toMerchantRoomList(Merchant merchant) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MerchantRoomListActivity.class);
		intent.putExtra("merchant", merchant);
		intent.putExtra("merchantId", merchant.merchantId);
		intent.putExtra("from", mFrom);
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		clearSearchFocus();
		return super.onTouchEvent(event);
	}

}
