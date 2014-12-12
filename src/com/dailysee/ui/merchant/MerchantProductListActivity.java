package com.dailysee.ui.merchant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.adapter.ProductAdapter;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.Product;
import com.dailysee.bean.RoomType;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.ProductResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

public class MerchantProductListActivity extends BaseActivity implements OnClickListener, OnChildClickListener, OnLastItemVisibleListener {

	protected static final String TAG = MerchantProductListActivity.class.getSimpleName();

	private static final int REQUEST_CONFIRM_ORDER = 1000;

	private PullToRefreshExpandableListView mPullRefreshListView;
	private ExpandableListView mExpandableListView;
	private ArrayList<String> mGroupList = new ArrayList<String>();
	private ArrayList<ArrayList<Product>> mChildrenList = new ArrayList<ArrayList<Product>>();
	private ProductAdapter mAdapter;

	private LinearLayout mLlMerchantTitle;
	private TextView mTvMerchantTitle;
	private ImageView mIvExpand;
	private LinearLayout mLlMerchantInfo;
	private TextView mTvMerchantDesc;
	
	private LinearLayout mLlBottomBar;
//	private TextView mTvShoppingCount;
	private TextView mTvTotalPrice;
	private Button mBtnToPayment;
	
	private LinearLayout mEmptyView;
	
	private LayoutInflater mInflater;

	private Merchant mMerchant;
	private RoomType mRoomType;
	
	private int mShoppingCount;
	private double mTotalPrice;

	private int mIndex;
	
	private ChooseProductHandler mHandler;

	private TextView mTvFooter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_product_list);
		
		onLoad(true);
	}

	@Override
	public void onInit() {
		mInflater = LayoutInflater.from(this);
		
		Intent intent = getIntent();
		if (intent != null) {
			mRoomType = (RoomType) intent.getSerializableExtra("roomType");
			mMerchant = (Merchant) intent.getSerializableExtra("merchant");
		}
		
		if (mRoomType == null) finish();
		
		mHandler = new ChooseProductHandler();

		String title = null;
		if (mRoomType != null) {
			title = mRoomType.name;
		}
		if (TextUtils.isEmpty(title)) {
			title = "房间详情";
		}
		setTitle(title);
		setUp();
	}

	@Override
	public void onFindViews() {
		View header = mInflater.inflate(R.layout.item_merchant_product_header, null);

		mLlMerchantTitle = (LinearLayout) header.findViewById(R.id.ll_merchant_title);
		mTvMerchantTitle = (TextView) header.findViewById(R.id.tv_merchant_title);
		mIvExpand = (ImageView) header.findViewById(R.id.iv_expand);
		mLlMerchantInfo = (LinearLayout) header.findViewById(R.id.ll_merchant_info);
		mTvMerchantDesc = (TextView) header.findViewById(R.id.tv_merchant_desc);
		
		mEmptyView = (LinearLayout) findViewById(R.id.ll_no_data);
		
		View footer = mInflater.inflate(R.layout.item_merchant_footer, null);
		mTvFooter = (TextView) footer.findViewById(R.id.tv_footer);
		
		mPullRefreshListView = (PullToRefreshExpandableListView) findViewById(R.id.pull_refresh_expandable_list);
		
		mExpandableListView = mPullRefreshListView.getRefreshableView();
		mExpandableListView.addHeaderView(header);
		mExpandableListView.addFooterView(footer);

		mLlBottomBar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
//		mTvShoppingCount = (TextView) findViewById(R.id.tv_shopping_count);
		mTvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
		mBtnToPayment = (Button) findViewById(R.id.btn_to_payment);
	}

	@Override
	public void onInitViewData() {
		hideBottomBar();
		
		mTvMerchantTitle.setText("房间信息");
		
		mGroupList.add("酒水");
		mChildrenList.add(new ArrayList<Product>());
		
		mGroupList.add("特色小吃");
		mChildrenList.add(new ArrayList<Product>());
		
		mExpandableListView.setGroupIndicator(null);
		mAdapter = new ProductAdapter(getActivity(), mGroupList, mChildrenList, mHandler);
		mExpandableListView.setAdapter(mAdapter);
		
		if (mRoomType != null) {
			String desc = mRoomType.useDesc;
			if (TextUtils.isEmpty(desc)) {
				desc = "暂无介绍";
			}
			mTvMerchantDesc.setText(desc);
		}
	}

	private void hideBottomBar() {
		mTvFooter.setVisibility(View.GONE);
		mLlBottomBar.setVisibility(View.GONE);
	}
	
	private void showBottomBar() {
		mTvFooter.setVisibility(View.VISIBLE);
		mLlBottomBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onBindListener() {
		mLlMerchantTitle.setOnClickListener(this);
		mBtnToPayment.setOnClickListener(this);
		
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mExpandableListView.setOnChildClickListener(this);
	}

	public void onLoad(final boolean showProgress) {
		// Tag used to cancel the request
		String tag = "tag_request_get_products";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				ProductResponse productResponse = (ProductResponse)response.getResponse(new TypeToken<ProductResponse>(){});
				if (productResponse != null && productResponse.rows != null && productResponse.rows.size() > 0) {
					List<Product> list = productResponse.rows;
					for (Product product : list) {
						if (Constants.Product.DRINKS == product.productTopType) {
							mChildrenList.get(0).add(product);
						} else if (Constants.Product.SNACK == product.productTopType) {
							mChildrenList.get(1).add(product);
						}
					}
					mAdapter.notifyDataSetChanged();
				}
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
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.ProductController.getProducts");
				params.put("merchantId", Long.toString(mMerchant.merchantId));
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				return params;
			}

		}, tag);
	}
	
//	private void onUpdateBottomBar() {
//		if (mShopDetail == null) {
//			return ;
//		}
//
//		mShoppingCount = 0;
//		mTotalPrice = 0;
//		List<ShoppingCart> list = AppController.getInstance().findFoodsInShoppingCart(mShopDetail.shop_id);
//		if (list != null && list.size() > 0) {
//			for (ShoppingCart shoppingCart : list) {
//				mShoppingCount += shoppingCart.count;
//				mTotalPrice += shoppingCart.count * shoppingCart.food.price;
//			}
//			
//			showBottomBar();
//		} else {			
//			hideBottomBar();
//		}
//		mTvShoppingCount.setText(Integer.toString(mShoppingCount));
//		mTvTotalPrice.setText("￥" + mTotalPrice);
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_merchant_title:
			boolean isExpanded = !(mLlMerchantInfo.getVisibility() == View.VISIBLE);
			mLlMerchantInfo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
			mTvMerchantTitle.setTextColor(isExpanded ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
	    	mLlMerchantTitle.setBackgroundColor(isExpanded ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.app_gray));
	    	mIvExpand.setImageResource(isExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
			break;
		case R.id.btn_to_payment:
//			AppController.getInstance().clearShoppingCart();
//			
//			ShoppingCartDb mDb = new ShoppingCartDb(getActivity());
//			mDb.clearAllData();
//			if (mMerchant != null) {
//				float upPrice = Float.parseFloat(mMerchant.up_price);
//				if (mTotalPrice >= upPrice) {
//					Intent intent = new Intent();
//					intent.setClass(this, ConfirmFoodActivity.class);
//					intent.putExtra("shop", mShopDetail);
//					startActivityForResult(intent, REQUEST_CONFIRM_ORDER);
//				} else {
//					showToast("您选购的美食没有达到该商家的起送价格: ￥" + mShopDetail.up_price);
//				}
//			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private class ChooseProductHandler extends Handler {
		
		public ChooseProductHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ProductAdapter.ADD_PRODUCT:{
				showBottomBar();
				
				mShoppingCount ++;
//				mTvShoppingCount.setText(Integer.toString(mShoppingCount));
				
				Product product = (Product) msg.obj;
				mTotalPrice = mTotalPrice + product.price;
				mTvTotalPrice.setText("￥" + Utils.formatTwoFractionDigits(mTotalPrice));
				
				product.count ++;
				
				AppController.getInstance().addToShoppingCart(product);
				break;
			}
			case ProductAdapter.REMOVE_PRODUCT: {
				mShoppingCount --;
//				mTvShoppingCount.setText(Integer.toString(mShoppingCount));
				if (mShoppingCount <= 0) {
					hideBottomBar();
				}
				
				Product product = (Product) msg.obj;
				mTotalPrice = mTotalPrice - product.price;
				if (mTotalPrice < 0) {
					mTotalPrice = 0;
				}
				mTvTotalPrice.setText("￥" + Utils.formatTwoFractionDigits(mTotalPrice));
				
				product.count --;
				AppController.getInstance().removeFromShoppingCart(product);
				break;
			}
			default:
				break;
			}
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (REQUEST_CONFIRM_ORDER == requestCode) {
//			onUpdateBottomBar();
//			if (mAdapter != null) {
//				mAdapter.notifyDataSetChanged();
//			}
//		}
	}

	@Override
	public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4) {
		return false;
	}

	@Override
	public void onLastItemVisible() {
		mIndex ++;
		onLoad(false);
	}

}
