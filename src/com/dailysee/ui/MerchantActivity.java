package com.dailysee.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.Merchant;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.MerchantResponse;
import com.dailysee.ui.adapter.MerchantAdapter;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MerchantActivity extends BaseActivity implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener {

	private LinearLayout llFilter;
	private TextView tvFilter;

	private LinearLayout llRecommented;
	private TextView tvRecommented;

	private LinearLayout llNearby;
	private TextView tvNearby;

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;

	private int mIndex = 1;
	private List<Merchant> merchantList = new ArrayList<Merchant>();
	private MerchantAdapter mAdatper;
	
	private int filter = Constants.Filter.RECOMMEND;

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
		mAdatper = new MerchantAdapter(getActivity(), merchantList);
		mListView.setAdapter(mAdatper);
	}

	@Override
	public void onBindListener() {
		llFilter.setOnClickListener(this);
		llRecommented.setOnClickListener(this);
		llNearby.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_filter:
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
		}
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
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				return params;
			}

		}, tag);
	}

}
