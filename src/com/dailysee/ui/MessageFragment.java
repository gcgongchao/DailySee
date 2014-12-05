package com.dailysee.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseFragment;
import com.dailysee.util.SpUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MessageFragment extends BaseFragment implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener {

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	
	private int mIndex = 1;
	private boolean mRefreshDataRequired = true;
	private DelayHandler mHander;

	public MessageFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_message, null);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onInit() {
		mHander = new DelayHandler();
		
	}

	@Override
	public void onFindViews() {
		View v = getView();
		setTitle("天天讯息");
		
		LinearLayout emptyView = (LinearLayout) v.findViewById(R.id.ll_no_data);

		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(this);
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setEmptyView(emptyView);
	}

	@Override
	public void onInitViewData() {
		
	}

	@Override
	public void onBindListener() {
		
	}

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mHander.sendEmptyMessageDelayed(DelayHandler.DELAY_AUTO_REFRESH, 1000);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// Update the LastUpdatedLabel
		String label = getTime();
		if (!TextUtils.isEmpty(label)) {
			mPullRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		}
		
		mIndex = 1;
		onLoad();
	}

	private String getTime() {
		return mSpUtil.getHomeRefreshTime();
	}

	@Override
	public void onLastItemVisible() {
		mIndex++;
		onLoad();
	}

	public void onLoad() {
		// Tag used to cancel the request
		String tag = "tag_request_home";
		NetRequest.getInstance(getActivity()).get(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mRefreshDataRequired = false;
				
				JSONObject data = response.getParent();
				Gson gson = new Gson();
				
				if (mIndex == 1) {
					SpUtil.getInstance(mContext).setHomeRefreshTime();
				}
				
			}

			@Override
			public void onPreExecute() {
//				toShowProgressMsg("正在加载...");
			}

			@Override
			public void onFinished() {
//				toCloseProgressMsg();
				mPullRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", "HomeServlet");
				params.put("code", "1");
				params.put("page", Integer.toString(mIndex));
				params.put("hardware", "1");
//				params.put("cityId", mCityCode);
				params.put("cityId", "137");
				return params;
			}

		}, tag);
	}
	
	private class DelayHandler extends Handler {
		
		public static final int DELAY_AUTO_REFRESH = 10001;
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DELAY_AUTO_REFRESH:
				if (mRefreshDataRequired && !mPullRefreshListView.isRefreshing()) {
//					mPullRefreshListView.demo();
					mPullRefreshListView.setRefreshing(false);
				}
				break;
			}
			
		}
	}

}
