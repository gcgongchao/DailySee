package com.dailysee.ui.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.alipay.sdk.app.PayTask;
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
import com.dailysee.ui.base.LoginActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Constants.Payment;
import com.dailysee.util.Result;
import com.dailysee.util.Utils;
import com.dailysee.widget.SelectPaymentDialog;
import com.google.gson.Gson;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

public class ConfirmOrderActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = ConfirmOrderActivity.class.getSimpleName();

	private static final int REQUEST_WRITE_DESC = 10000;
	private static final int REQUEST_LOGIN = 10001;

	private static final int SDK_PAY_FLAG = 20001;
	
	private ListView mListView;
	private List<Product> items = new ArrayList<Product>();
	private ConfirmOrderAdapter mAdapter;
	
	private LayoutInflater mInflater;

	private LinearLayout mEmptyView;

	private LinearLayout llOrderInfo;
	private TextView tvRoom;
	private TextView tvTime;

	private View divider;
	private TextView tvTotalPrice;
	private TextView etPhone;

	private View dividerRemarkTop;
	private LinearLayout llRemark;
	private TextView tvRemarkTitle;
	private TextView tvRemark;
	private View dividerRemarkBottom;
	
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
			mOrderId = intent.getLongExtra("orderId", 0);
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
		divider = footer.findViewById(R.id.divider);
		tvTotalPrice = (TextView) footer.findViewById(R.id.tv_total_price);
		etPhone = (TextView) footer.findViewById(R.id.et_phone);
		dividerRemarkTop = footer.findViewById(R.id.divider_remark_top);
		llRemark = (LinearLayout) footer.findViewById(R.id.ll_remark);
		tvRemarkTitle = (TextView) footer.findViewById(R.id.tv_remark_title);
		tvRemark = (TextView) footer.findViewById(R.id.tv_remark);
		dividerRemarkBottom = footer.findViewById(R.id.divider_remark_bottom);
		mBtnCommit = (Button) footer.findViewById(R.id.btn_commit);

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.addHeaderView(header);
		mListView.addFooterView(footer);
//		mListView.setEmptyView(emptyView);
	}

	@Override
	public void onInitViewData() {
		etPhone.setText(mSpUtil.getLoginId());
		
		switch (mFrom) {
		case Constants.From.CONSULTANT:
			llOrderInfo.setBackgroundColor(getResources().getColor(R.color.white));
			
			String name = mConsultant.getName();
			String orderInfo = "商务公关" + name + "服务一次";
			SpannableStringBuilder builder = new SpannableStringBuilder(orderInfo);
			builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 4, 4 + name.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
			
			tvRoom.setText(builder);
			tvRoom.setTextColor(getResources().getColor(R.color.deep_gray));
			tvTime.setText("服务时长: " + mDate + "小时");
			tvTime.setTextColor(getResources().getColor(R.color.gray));
			
			mAdapter = new ConfirmOrderAdapter(getActivity(), items);
			mListView.setAdapter(mAdapter);
			
			tvTotalPrice.setText("¥" + Utils.formatTwoFractionDigits(mTotalPrice));
			hideRemark();
			tvRemarkTitle.setText("留言");
			break;
		case Constants.From.GIFT:
		case Constants.From.MERCHANT:
			tvRoom.setText(mRoomType.name);
			tvTime.setText("时间: " + mDate);
			
			items = getShoppingCartItems();
			mAdapter = new ConfirmOrderAdapter(getActivity(), items);
			mListView.setAdapter(mAdapter);
			
			tvTotalPrice.setText("¥" + Utils.formatTwoFractionDigits(mTotalPrice));
			etPhone.setText(mSpUtil.getLoginId());
			
			showRemark();
			if (mFrom == Constants.From.GIFT) {
				tvRemarkTitle.setText("留言");
			} else {
				tvRemarkTitle.setText("叮嘱商家");
			}
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

	private void showRemark() {
		llRemark.setVisibility(View.VISIBLE);
		divider.setVisibility(View.VISIBLE);
		dividerRemarkTop.setVisibility(View.VISIBLE);
		dividerRemarkBottom.setVisibility(View.VISIBLE);
	}

	private void hideRemark() {
		llRemark.setVisibility(View.GONE);
		divider.setVisibility(View.GONE);
		dividerRemarkTop.setVisibility(View.GONE);
		dividerRemarkBottom.setVisibility(View.GONE);
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
				String payment = null;
				if (v.getId() == R.id.btn_wechat_payment) {
					payment = Payment.WECHAT;
//					showToast("微信支付成功");
//					onPaySuccess();
				} else if (v.getId() == R.id.btn_alipay_payment) {
					payment = Payment.ALIPAY;
//					toAlipayPayment();
				} else if (v.getId() == R.id.btn_up_payment) {
					payment = Payment.UP;
//					toUPPayment();
				}
				requestPayParams(payment);
			}
		});
	}
	
	protected void requestPayParams(final String payment) {
		// Tag used to cancel the request
		String tag = "tag_request_pay_params";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				String url = response.getData().optString("url");
				String params = response.getData().optString("params");
//				String payInfo = url + params;
				String payInfo = params;
				if (Payment.ALIPAY.equals(payment)) {
					toAlipayPayment(payInfo);
				} else if (Payment.UP.endsWith(payment)) {
					toUPPayment(payInfo);
				}
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
				params.put("mtd", "tty.mobile.securitypay.pay");
				params.put("memberId", mSpUtil.getMemberIdStr());
				params.put("orderId", Long.toString(mOrderId));
				params.put("thirdpayId", payment);

				return params;
			}

		}, tag);
	}

	public void onPaySuccess() {
		AppController.getInstance().clearShoppingCart();
		
		setResult(RESULT_OK);
		finish();
	}

	private void requestCreateOrder() {		
		// Tag used to cancel the request
		String tag = "tag_request_create_order";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mOrderId = Long.parseLong(response.getSimpleDataStr());
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
				String name = mSpUtil.getName();
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("memberId", mSpUtil.getMemberIdStr());
				params.put("buyerName", name);
				params.put("mobile", getPhone());

				switch (mFrom) {
				case Constants.From.GIFT:
				case Constants.From.MERCHANT:
					params.put("mtd", "com.guocui.tty.api.web.OrderController.saveConsumeOrder");
					params.put("merchantId", Long.toString(mMerchant.merchantId));
					params.put("sellerName", mMerchant.name);
					params.put("bookDate", mDate);
					params.put("remark", getRemark());
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
					params.put("mtd", "com.guocui.tty.api.web.OrderController.saveServiceOrder");
					params.put("sellerName", mConsultant.getName());
					params.put("merchantId", Long.toString(mConsultant.counselorId));
					params.put("amount", Double.toString(mTotalPrice));
					params.put("buyHours", mDate);
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
//			if (checkPhone()) {
				if (!mSpUtil.isLogin()) {
					toLogin();
				} else {
					toCommitOrder();
				}
//			}
			break;
		case R.id.ll_remark:
			toWriteDesc();
			break;
		default:
			break;
		}
	}

	private void toCommitOrder() {
		if (mOrderId > 0) {
			showSelectPaymentDialog();
		} else {
			requestCreateOrder();
		}
	}

	private void toLogin() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		intent.putExtra("from", "confirmOrder");
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void toWriteDesc() {
		Intent intent = new Intent(this, WriteDescActivity.class);
		intent.putExtra("title", tvRemarkTitle.getText().toString());
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
			} else if (requestCode == REQUEST_LOGIN) {
				etPhone.setText(mSpUtil.getLoginId());
				toCommitOrder();
			}
		}
		
		/*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (!TextUtils.isEmpty(str)) {
	        if (str.equalsIgnoreCase("success")) {
	            msg = "支付成功！";
				onPaySuccess();
	        } else if (str.equalsIgnoreCase("fail")) {
	            msg = "支付失败！";
	        } else if (str.equalsIgnoreCase("cancel")) {
	            msg = "用户取消了支付";
	        }
	
	        showToast(msg);
        }
	}

	private void toUPPayment(String params) {
		if (!TextUtils.isEmpty(params) && params.startsWith("tn=")) {
			String tn = params.substring(3);
			// mode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
			String mode = "01";
			UPPayAssistEx.startPayByJAR(getActivity(), PayActivity.class, null, null, tn, mode);
		} else {
			showToast("银联交易流水号不正确");
		}
	}

	private void toAlipayPayment(final String params) {
//		showToast("支付宝支付成功");
//		String orderInfo = PayUtils.getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
//		String sign = PayUtils.sign(orderInfo);
//		try {
//			// 仅需对sign 做URL编码
//			sign = URLEncoder.encode(sign, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
//				+ PayUtils.getSignType();
//		
//		Log.d(TAG, payInfo);
		
		final String payInfo = params;

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(ConfirmOrderActivity.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					showToastShort("支付成功");
					onPaySuccess();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						showToastShort("支付结果确认中");
					} else {
						showToastShort("支付失败");
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};
	
}
