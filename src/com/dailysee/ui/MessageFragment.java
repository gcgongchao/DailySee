package com.dailysee.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.adapter.TipAdapter;
import com.dailysee.bean.Tip;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.TipResponse;
import com.dailysee.ui.base.BaseFragment;
import com.dailysee.ui.tip.TipDetailActivity;
import com.dailysee.util.SpUtil;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MessageFragment extends BaseFragment implements OnClickListener, OnRefreshListener<ListView>, OnLastItemVisibleListener, OnItemClickListener {

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private TipAdapter mAdapter;
	
	private LinearLayout emptyView;
	
	private int mIndex = 1;
	private ArrayList<Tip> tipList = new ArrayList<Tip>();

	private LinearLayout llUnread;
	private TextView tvUnread;

	private LinearLayout llRead;
	private TextView tvRead;

	private LinearLayout llAll;
	private TextView tvAll;
	
	private boolean mRefreshDataRequired = true;
	private DelayHandler mHander;
	private int filter;

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
		
		llUnread = (LinearLayout) v.findViewById(R.id.ll_unread);
		tvUnread = (TextView) v.findViewById(R.id.tv_unread);

		llRead = (LinearLayout) v.findViewById(R.id.ll_read);
		tvRead = (TextView) v.findViewById(R.id.tv_read);

		llAll = (LinearLayout) v.findViewById(R.id.ll_all);
		tvAll = (TextView) v.findViewById(R.id.tv_all);
		
		emptyView = (LinearLayout) v.findViewById(R.id.ll_no_data);

		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(this);
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		
		mListView = mPullRefreshListView.getRefreshableView();
//		mListView.setEmptyView(emptyView);
	}

	@Override
	public void onInitViewData() {
		mAdapter = new TipAdapter(mContext, tipList);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onBindListener() {
		llUnread.setOnClickListener(this);
		llRead.setOnClickListener(this);
		llAll.setOnClickListener(this);
		
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		tvUnread.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
		tvRead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
		tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_off, 0, 0, 0);
		switch (v.getId()) {
		case R.id.ll_unread:
			tvUnread.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			filter = 0;
			break;
		case R.id.ll_read:
			tvUnread.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			filter = 1;
			break;
		case R.id.ll_all:
			tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_on, 0, 0, 0);
			filter = 2;
			break;
		}
		mPullRefreshListView.setRefreshing(false);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		mHander.sendEmptyMessageDelayed(DelayHandler.DELAY_AUTO_REFRESH, 0);
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// Update the LastUpdatedLabel
		String label = getTime();
		if (!TextUtils.isEmpty(label)) {
			mPullRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		}
		
		mIndex = 1;
		onLoad(false);
	}

	private String getTime() {
		return mSpUtil.getMessageRefreshTime();
	}

	@Override
	public void onLastItemVisible() {
		mIndex++;
		onLoad(false);
	}

	public void onLoad(final boolean showProgress) {
		// Tag used to cancel the request
		String tag = "tag_request_message";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mRefreshDataRequired = false;
				if (mIndex == 1) {
					SpUtil.getInstance(mContext).setMessageRefreshTime();
					tipList.clear();
				}
				
				TipResponse tipResponse =(TipResponse) response.getResponse(new TypeToken<TipResponse>(){});
				if (tipResponse != null && tipResponse.rows != null && tipResponse.rows.size() > 0) {
					tipList.addAll(tipResponse.rows);
				} else {
					mListView.setEmptyView(emptyView);
				}
				mAdapter.notifyDataSetChanged();
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
				params.put("mtd", "com.guocui.tty.api.web.TipController.getTips");
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
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
				if (mRefreshDataRequired) {
					onLoad(true);
				} else if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
//				if (mRefreshDataRequired && !mPullRefreshListView.isRefreshing()) {
////					mPullRefreshListView.demo();
//					mPullRefreshListView.setRefreshing(false);
//				}
				break;
			}
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		Tip tip = (Tip) parent.getAdapter().getItem(position);
		if (tip != null) {
			Intent intent = new Intent();
			intent.setClass(mContext, TipDetailActivity.class);
			intent.putExtra("tip", tip);
			startActivity(intent);
		}
	}

}
