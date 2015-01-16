package com.dailysee.ui.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dailysee.R;
import com.dailysee.adapter.OrderAdapter;
import com.dailysee.bean.Order;
import com.dailysee.bean.OrderItem;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.OrderResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;
import com.dailysee.widget.ListViewDialog;
import com.dailysee.widget.OrderFilterPopupWindow;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

public class OrderActivity extends BaseActivity implements OnRefreshListener<ExpandableListView>, OnLastItemVisibleListener, OnGroupClickListener, OnClickListener {

	protected static final String TAG = OrderActivity.class.getSimpleName();
	private PullToRefreshExpandableListView mPullRefreshListView;
	private ExpandableListView mExpandableListView;
	
	private LinearLayout emptyView;
	
    private List<Order> mGroupList = new ArrayList<Order>();
    private List<List<OrderItem>> mChildrenList = new ArrayList<List<OrderItem>>();
	private OrderAdapter mAdapter;
	private int mIndex = 1;
	private String filter = Constants.OrderFilter.ALL;
	
	private boolean mRefreshDataRequired = true;
	private Handler mHandler;
	private ImageView btnFilter;
	private OrderFilterPopupWindow mOrderFilterPopupWindow;
	private ListViewDialog mCommentDialog;

	public OrderActivity() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			filter = intent.getStringExtra("filter");
		}
		
		setTitle("订单");
		setUp();
		
		mHandler = new OrderHandler();
	}

	@Override
	public void onFindViews() {
		btnFilter = (ImageView) findViewById(R.id.iv_action);
		
		mPullRefreshListView = (PullToRefreshExpandableListView) findViewById(R.id.pull_refresh_expandable_list);
		mExpandableListView = mPullRefreshListView.getRefreshableView();
		
		emptyView = (LinearLayout) findViewById(R.id.ll_no_data);
	}

	@Override
	public void onInitViewData() {
		findViewById(R.id.btn_action).setVisibility(View.GONE);
		btnFilter.setVisibility(View.VISIBLE);
		btnFilter.setImageResource(R.drawable.ic_filter_order);
		
		mAdapter = new OrderAdapter(getActivity(), mGroupList, mChildrenList, mHandler);
		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setGroupIndicator(null);
	}

	@Override
	public void onBindListener() {
		btnFilter.setOnClickListener(this);
		
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(this);
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mExpandableListView.setOnGroupClickListener(this);
	}

	public void onLoad() {
		// Tag used to cancel the request
		String tag = "tag_request_order_list";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mRefreshDataRequired = false;
				if (mIndex == 1) {
					SpUtil.getInstance(getActivity()).setOrderRefreshTime();
					
					mGroupList.clear();
					mChildrenList.clear();
				}
				
				OrderResponse orderResponse = (OrderResponse) response.getResponse(new TypeToken<OrderResponse>(){});
				if (orderResponse != null && orderResponse.rows != null && orderResponse.rows.size() > 0) {
					List<Order> orderList = orderResponse.rows;
					for (int i = 0; i < orderList.size(); i++) {
						Order order = orderList.get(i);
						List<OrderItem> items = null;
						if (order != null && order.items != null) {
							items = order.items;
						} else {
							items = new ArrayList<OrderItem>();
						}
						
						if (order != null) {
							OrderItem itemFooter = new OrderItem();
							itemFooter.price = order.amount;
							items.add(itemFooter);
						}
						
						mChildrenList.add(items);
					}
					mGroupList.addAll(orderList);
				} else {
					mExpandableListView.setEmptyView(emptyView);
				}
				mAdapter.notifyDataSetChanged();
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
				params.put("mtd", "tty.order.list.get");
//				params.put("belongObjId", mSpUtil.getBelongObjIdStr());
				params.put("belongObjId", mSpUtil.getMemberIdStr());
				params.put("orderStatus", filter);
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				return params;
			}
		}, tag);
	}

	private String getTime() {
		return SpUtil.getInstance(getActivity()).getOrderRefreshTime();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onRefreshData();
	}

	private void onRefreshData() {
		mHandler.sendEmptyMessageDelayed(OrderHandler.DELAY_AUTO_REFRESH, 200);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
		// Update the LastUpdatedLabel
		String label = getTime();
		if (!TextUtils.isEmpty(label)) {
			mPullRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		}
		
		mIndex = 1;
		onLoad();
	}

	@Override
	public void onLastItemVisible() {
		mIndex++;
		onLoad();
	}
	
	private class OrderHandler extends Handler {
		public static final int DELAY_AUTO_REFRESH = 10001;
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DELAY_AUTO_REFRESH:
				if (mRefreshDataRequired && !mPullRefreshListView.isRefreshing()) {
					mPullRefreshListView.demo();
					mPullRefreshListView.setRefreshing(false);
				}
				break;
			case OrderAdapter.DEAL_ORDER:
				Order order = (Order) msg.obj;
				if (order != null && order.orderId > 0) {
					/**
					 * WAIT_PAY: 等待付款; 
					 * WAIT_ACCEPT_CONFIRM:已支付,待接单确认
					 * WAIT_CONFIRM_GOODS:已接单,待确认消费, 
					 * REFUND_INPROCESS: 退款待处理,
					 * REFUND:退款(退款成功)
					 * SUCCEED: 交易成功    
					 * CLOSE:交易关闭
					 */
					if (Constants.OrderFilter.WAIT_PAY.equals(order.orderStatus)) {
						toPayOrder(order);
					} else if (Constants.OrderFilter.SUCCEED.equals(order.orderStatus)) {
						showCommentDialog(order.orderId);
					}
				}
				break;
			default:
				break;
			}
			
		}
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		Order order = mGroupList.get(groupPosition);
		if (order != null && groupPosition < mChildrenList.size()) {
			List<OrderItem> list = mChildrenList.get(groupPosition);
			if (list == null) {
				requestOrderItems(order, groupPosition);
			}
		}
		return false;
	}

	public void showCommentDialog(long orderId) {
		final List<Object> items = new ArrayList<Object>();
		items.add("非常满意");
		items.add("满意");
		items.add("不满意");
		items.add("非常不满意");
		
		mCommentDialog = new ListViewDialog(getActivity(), "选择服务时长", items, new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
				Utils.clossDialog(mCommentDialog);
				
				showToast(items.get(position).toString());
				toCommitOrderComment(position);
			}
			
		});
		mCommentDialog.show();
	}

	protected void toCommitOrderComment(int position) {
		
	}

	public void toPayOrder(Order order) {
		
	}

	private void requestOrderItems(final Order order, final int groupPosition) {
		// Tag used to cancel the request
		String tag = "tag_request_order_item_list";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Order order = (Order) response.getResponse(new TypeToken<OrderResponse>(){});
				if (order != null && order.items != null && order.items.size() > 0) {
					mChildrenList.set(groupPosition, order.items);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在加载订单详情...");
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
				params.put("mtd", "com.guocui.tty.api.web.OrderController.getOrderDetail");
				params.put("belongObjId", mSpUtil.getBelongObjIdStr());
//				params.put("orderStatus", filter);
				params.put("orderId", Long.toString(order.orderId));
				return params;
			}
		}, tag);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_action:
			showOrderFilterPopupWindow();
			break;
		}
	}

	private void showOrderFilterPopupWindow() {
		if (mOrderFilterPopupWindow == null) {
			mOrderFilterPopupWindow = new OrderFilterPopupWindow(getActivity(), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mOrderFilterPopupWindow.dismiss();
					switch (v.getId()) {
					case R.id.tv_filter_all:
						filter = Constants.OrderFilter.ALL;
						break;
					case R.id.tv_filter_1:
						filter = Constants.OrderFilter.WAIT_PAY;
						break;
					case R.id.tv_filter_2:
						filter = Constants.OrderFilter.WAIT_ACCEPT_CONFIRM + ";" + Constants.OrderFilter.WAIT_CONFIRM_GOODS;
						break;
					case R.id.tv_filter_3:
						filter = Constants.OrderFilter.SUCCEED;
						break;
					}
					mIndex = 1;
					mRefreshDataRequired = true;
					onRefreshData();
				}
			});
			mOrderFilterPopupWindow.init();
		}
		mOrderFilterPopupWindow.showAsDropDown(btnFilter, 0, mOrderFilterPopupWindow.getHeight());
	}
	
	

}
