package com.dailysee.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.bean.Order;
import com.dailysee.bean.OrderItem;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;

public class OrderAdapter extends BaseExpandableListAdapter {

	public static final int DEAL_ORDER =10002;

	private Context context;
	private List<Order> mGroupList;
	private List<List<OrderItem>> mChildrenList;
	private LayoutInflater mInflater;
	private Handler mHandler;

	public OrderAdapter(Context context, List<Order> mGroupList, List<List<OrderItem>> mChildrenList, Handler mHandler) {
		this.context = context;
		this.mGroupList = mGroupList;
		this.mChildrenList = mChildrenList;
		mInflater = LayoutInflater.from(context);
		this.mHandler = mHandler;
	}

	@Override
	public int getGroupCount() {
		return mGroupList != null ? mGroupList.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<OrderItem> list = null;
		if (mChildrenList != null && mChildrenList.size() > groupPosition) {
			list = mChildrenList.get(groupPosition);
		}
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<OrderItem> list = mChildrenList.get(groupPosition);
		if (list != null) {
			return list.get(childPosition);
		} else {
			return null;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder holder = null;
    	if(null == convertView){
    		convertView = mInflater.inflate(R.layout.item_order, parent, false);
    		holder = new GroupViewHolder(convertView);
    	} else{
    		holder = (GroupViewHolder) convertView.getTag();
    	}
    	
    	Order order = mGroupList.get(groupPosition);

    	holder.llOrderInfo.setBackgroundColor(isExpanded ? context.getResources().getColor(R.color.orange) : context.getResources().getColor(R.color.app_gray));
    	
    	holder.tvName.setText(order.buyerName);
    	holder.tvName.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black));
    	
    	holder.tvOrderStatus.setText(order.orderSelStatusName);
    	holder.tvOrderStatus.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.orange));
    	
    	holder.tvOrderId.setText("NO." + order.orderId);
    	holder.tvOrderId.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.deep_gray));
    	
    	holder.tvTime.setText(Utils.formatTime(order.bookDate, Utils.DATE_FORMAT_YMD));
    	holder.tvTime.setTextColor(isExpanded ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.deep_gray));
    	
    	holder.ivExpand.setImageResource(isExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
    	
        return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final ChildrenViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_order_item, null);
			holder = new ChildrenViewHolder(convertView);
		} else {
			holder = (ChildrenViewHolder) convertView.getTag();
		}

		final Order order = (Order) getGroup(groupPosition);
		final OrderItem orderItem = (OrderItem) getChild(groupPosition, childPosition);

		if (orderItem.itemId > 0) { 
			holder.llOrderItemInfo.setVisibility(View.VISIBLE);
			holder.llOrderItemFooter.setVisibility(View.GONE);
			holder.divider.setVisibility(View.GONE);
			holder.count.setText("X" + orderItem.quantity);
			holder.name.setText(orderItem.name);
			holder.price.setText("¥" + Utils.formatTwoFractionDigits(orderItem.price));
		} else {
			holder.llOrderItemInfo.setVisibility(View.GONE);
			holder.llOrderItemFooter.setVisibility(View.VISIBLE);
			holder.divider.setVisibility(View.VISIBLE);
			holder.totalPrice.setText("¥" + Utils.formatTwoFractionDigits(orderItem.price));
			
			/**
			 * WAIT_PAY: 等待付款; 
			 * WAIT_ACCEPT_CONFIRM:已支付,待接单确认
			 * WAIT_CONFIRM_GOODS:已接单,待确认消费, 
			 * REFUND_INPROCESS: 退款待处理,
			 * REFUND:退款(退款成功)
			 * SUCCEED: 交易成功    
			 * CLOSE:交易关闭
			 */
			holder.btnDeal.setVisibility(View.GONE);
			if (Constants.OrderFilter.WAIT_PAY.equals(order.orderStatus)) {
				holder.btnDeal.setText("去付款");
				holder.btnDeal.setVisibility(View.VISIBLE);
			} else if (Constants.OrderFilter.SUCCEED.equals(order.orderStatus)) {
				holder.btnDeal.setText("去评价");
				holder.btnDeal.setVisibility(View.VISIBLE);
			}
			holder.btnDeal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Message msg = new Message();
					msg.what = DEAL_ORDER;
					msg.obj = orderItem;
					mHandler.sendMessage(msg);
				}
				
			});
		}
		
        return convertView;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private static class GroupViewHolder {

		private LinearLayout llOrderInfo;
		private TextView tvName;
		private TextView tvOrderStatus;
		private TextView tvOrderId;
		private TextView tvTime;
		private ImageView ivExpand;

		public GroupViewHolder(View convertView) {
			llOrderInfo = (LinearLayout) convertView.findViewById(R.id.ll_order_info);
			tvName = (TextView) convertView.findViewById(R.id.tv_name);
			tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_status);
			tvOrderId = (TextView) convertView.findViewById(R.id.tv_order_id);
			tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			ivExpand = (ImageView) convertView.findViewById(R.id.iv_expand);

			convertView.setTag(this);
		}

	}
	
	private static class ChildrenViewHolder {

		public LinearLayout llOrderItemInfo;
		public TextView name;
		public TextView count;
		public TextView price;
		
		public LinearLayout llOrderItemFooter;
		public TextView totalPrice;
		public Button btnDeal;
		
		public View divider;

		public ChildrenViewHolder(View convertView) {
			llOrderItemInfo = (LinearLayout) convertView.findViewById(R.id.ll_order_item_info);
			name = (TextView) convertView.findViewById(R.id.tv_name);
			count = (TextView) convertView.findViewById(R.id.tv_count);
			price = (TextView) convertView.findViewById(R.id.tv_price);

			llOrderItemFooter = (LinearLayout) convertView.findViewById(R.id.ll_order_item_footer);
			totalPrice = (TextView) convertView.findViewById(R.id.tv_total_price);
			btnDeal = (Button) convertView.findViewById(R.id.btn_deal);
			
			divider = convertView.findViewById(R.id.divider);

			convertView.setTag(this);
		}

	}

}
