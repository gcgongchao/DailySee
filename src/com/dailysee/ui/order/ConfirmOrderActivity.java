package com.dailysee.ui.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.adapter.ConfirmOrderAdapter;
import com.dailysee.bean.Consultant;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.Product;
import com.dailysee.bean.ProductOrder;
import com.dailysee.bean.Room;
import com.dailysee.bean.RoomType;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;
import com.dailysee.widget.SelectPaymentDialog;
import com.google.gson.Gson;

public class ConfirmOrderActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = ConfirmOrderActivity.class.getSimpleName();

	private static final int REQUEST_WRITE_DESC = 10000;
	
	private ListView mListView;
	private List<Product> items = new ArrayList<Product>();
	private ConfirmOrderAdapter mAdapter;
	
	private LayoutInflater mInflater;

	private LinearLayout mEmptyView;

	private LinearLayout llOrderInfo;
	private TextView tvRoom;
	private TextView tvTime;

	private TextView tvTotalPrice;
	private TextView etPhone;
	private LinearLayout llRemark;
	private TextView tvRemark;
	private Button mBtnCommit;

	private RoomType mRoomType;
	private Merchant mMerchant;
	
	private int mShoppingCount;
	private double mTotalPrice;
	private String mDate;

	private int mFrom;
	
	private SelectPaymentDialog mSelectPaymentDialog;

	private long mOrderId;

	private String mDesc;

	private Room mRoom;

	private Consultant mConsultant;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_order);

		initDialog();
	}

	@Override
	public void onInit() {
		mInflater = LayoutInflater.from(this);
		
		Intent intent = getIntent();
		if (intent != null) {
			mRoomType = (RoomType) intent.getSerializableExtra("roomType");
			mRoom = (Room) intent.getSerializableExtra("room");
			mMerchant = (Merchant) intent.getSerializableExtra("merchant");
			mTotalPrice = intent.getDoubleExtra("totalPrice", 0);
			mDate = intent.getStringExtra("date");
			mFrom = intent.getIntExtra("from", Constants.From.MERCHANT);
			mConsultant = (Consultant) intent.getSerializableExtra("consultant");
		}
		
		if (((mFrom == Constants.From.GIFT || mFrom == Constants.From.MERCHANT) && (mRoomType == null || mMerchant == null))
				|| (mFrom == Constants.From.CONSULTANT && mConsultant == null)) {
			finish();
		}
		
		setTitle("确认订单");
		setUp();
	}

	@Override
	public void onFindViews() {
		mEmptyView = (LinearLayout) findViewById(R.id.ll_no_data);
		
		View header = mInflater.inflate(R.layout.item_confirm_order_header, null);
		llOrderInfo = (LinearLayout) header.findViewById(R.id.ll_order_info);
		tvRoom = (TextView) header.findViewById(R.id.tv_room);
		tvTime = (TextView) header.findViewById(R.id.tv_time);
		
		View footer = mInflater.inflate(R.layout.item_confirm_order_footer, null);
		tvTotalPrice = (TextView) footer.findViewById(R.id.tv_total_price);
		etPhone = (TextView) footer.findViewById(R.id.et_phone);
		llRemark = (LinearLayout) footer.findViewById(R.id.ll_remark);
		tvRemark = (TextView) footer.findViewById(R.id.tv_remark);
		mBtnCommit = (Button) footer.findViewById(R.id.btn_commit);

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.addHeaderView(header);
		mListView.addFooterView(footer);
//		mListView.setEmptyView(emptyView);
	}

	@Override
	public void onInitViewData() {
		switch (mFrom) {
		case Constants.From.CONSULTANT:
			llOrderInfo.setBackgroundColor(getResources().getColor(R.color.white));
			
			String name = mConsultant.getName();
			String orderInfo = "商务顾问" + name + "服务一次";
			SpannableStringBuilder builder = new SpannableStringBuilder(orderInfo);
			builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 4, 4 + name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
			
			tvRoom.setText(builder);
			tvRoom.setTextColor(getResources().getColor(R.color.deep_gray));
			tvTime.setText("时间: " + mDate);
			tvTime.setTextColor(getResources().getColor(R.color.gray));
			
			mAdapter = new ConfirmOrderAdapter(getActivity(), items);
			mListView.setAdapter(mAdapter);
			
			tvTotalPrice.setText("￥" + Utils.formatTwoFractionDigits(mTotalPrice));
			llRemark.setVisibility(View.GONE);
			break;
		case Constants.From.GIFT:
		case Constants.From.MERCHANT:
			tvRoom.setText(mRoomType.name);
			tvTime.setText("时间: " + mDate);
			
			items = getShoppingCartItems();
			mAdapter = new ConfirmOrderAdapter(getActivity(), items);
			mListView.setAdapter(mAdapter);
			
			tvTotalPrice.setText("￥" + Utils.formatTwoFractionDigits(mTotalPrice));
			etPhone.setText(mSpUtil.getLoginId());
			
			showRemarkIfFromGift();
			break;
		}
	}

	private List<Product> getShoppingCartItems() {
		Collection<Product> collection = AppController.getInstance().getShoppingCart().values();
		
		if (collection != null && collection.size() > 0) {
			List<Product> list = new ArrayList<Product>();
			for (Product product : collection) {
				list.add(product);
			}
			items.addAll(list);
		}
		
		return items;
	}

	private void showRemarkIfFromGift() {
		if (mFrom == Constants.From.GIFT) {
			llRemark.setVisibility(View.VISIBLE);
		} else {
			llRemark.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBindListener() {
		llRemark.setOnClickListener(this);
		mBtnCommit.setOnClickListener(this);
	}

	private void initDialog() {
		mSelectPaymentDialog = new SelectPaymentDialog(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.clossDialog(mSelectPaymentDialog);
				if (v.getId() == R.id.btn_wechat_payment) {
					showToast("微信支付成功");
				} else if (v.getId() == R.id.btn_alipay_payment) {
					showToast("支付宝支付成功");
				}
				AppController.getInstance().clearShoppingCart();
				
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	private void requestCreateOrder() {		
		// Tag used to cancel the request
		String tag = "tag_request_create_order";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mOrderId = Long.parseLong(response.getDataStr());
				showSelectPaymentDialog();
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在提交订单...");
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
				params.put("mtd", "com.guocui.tty.api.web.OrderController.saveConsumeOrder");
				params.put("memberId", mSpUtil.getMemberIdStr());
				params.put("buyerName", mSpUtil.getName());
				params.put("bookDate", mDate);
				params.put("mobile", getPhone());
				params.put("remark", getRemark());

				switch (mFrom) {
				case Constants.From.GIFT:
				case Constants.From.MERCHANT:
					params.put("merchantId", Long.toString(mMerchant.merchantId));
					params.put("sellerName", mMerchant.name);
					if (mFrom == Constants.From.GIFT && mRoom != null) {
						params.put("roomId", Long.toString(mRoom.roomId));
					} 
					List<ProductOrder> orderList = new ArrayList<ProductOrder>();
					
					ProductOrder roomOrder = new ProductOrder(mRoomType);
					orderList.add(roomOrder);
					
					for (Product product : items) {
						ProductOrder productOrder = new ProductOrder(product);
						orderList.add(productOrder);
					}
					params.put("items", new Gson().toJson(orderList));
					break;
				case Constants.From.CONSULTANT:
					params.put("sellerName", mConsultant.getName());
					params.put("merchantId", Long.toString(mConsultant.counselorId));
					params.put("amount", Double.toString(mConsultant.price));
					break;
				}
				return params;
			}

		}, tag);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			if (checkPhone()) {
				if (mOrderId > 0) {
					showSelectPaymentDialog();
				} else {
					requestCreateOrder();
				}
			}
			break;
		case R.id.ll_remark:
			toWriteDesc();
			break;
		default:
			break;
		}
	}

	private void toWriteDesc() {
		Intent intent = new Intent(this, WriteDescActivity.class);
		intent.putExtra("desc", mDesc);
		startActivityForResult(intent, REQUEST_WRITE_DESC);
	}
	
	private boolean checkPhone() {
		String phone = getPhone();

		boolean check = false;
		if (TextUtils.isEmpty(phone)) {
			showToast("请输入手机号码");
		} else {
			check = true;
		}

		return check;
	}

	private String getPhone() {
		return etPhone.getText().toString();
	}

	private String getRemark() {
		return tvRemark.getText().toString();
	}

	private void showSelectPaymentDialog() {
		if (mSelectPaymentDialog != null) {
			mSelectPaymentDialog.show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_WRITE_DESC && data != null) {
				mDesc = data.getStringExtra("desc");
				tvRemark.setText(mDesc);
			}
		}
	}
	
}
