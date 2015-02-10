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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.adapter.GroupAdapter;
import com.dailysee.adapter.ProductAdapter;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.Product;
import com.dailysee.bean.ProductType;
import com.dailysee.bean.Room;
import com.dailysee.bean.RoomType;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.ProductResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.ui.order.ConfirmOrderActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;
import com.dailysee.widget.HorizontalListView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MerchantProductListActivity extends BaseActivity implements OnClickListener, OnLastItemVisibleListener, OnItemClickListener, OnRefreshListener<ListView> {

	protected static final String TAG = MerchantProductListActivity.class.getSimpleName();

	private static final int REQUEST_CONFIRM_ORDER = 1000;

	private LinearLayout mLlRoomTitle;
	private TextView mTvRoomTitle;
	private ImageView mIvRoomExpand;
	private LinearLayout mLlRoomContent;
	private TextView mTvRoomDesc;

	private FrameLayout mLlDrinkTitle;
	private TextView mTvDrinkTitle;
	private ImageView mIvDrinkExpand;
	private LinearLayout mLlDrinkContent;
	private HorizontalListView mHlvDrinkTab;
	private PullToRefreshListView mPullRefreshDrinkListView;
	private ListView mDrinkListView;
	private LinearLayout mDrinkEmptyView;

	private ArrayList<Object> mDrinkTypeList = new ArrayList<Object>();
	private GroupAdapter mDrinkTypeAdapter;
	
	private ArrayList<Product> mDrinkList = new ArrayList<Product>();
	private ProductAdapter mDrinkAdapter;
	
	protected ProductType mDrinkType;
	
	private FrameLayout mLlSnackTitle;
	private TextView mTvSnackTitle;
	private ImageView mIvSnackExpand;
	private LinearLayout mLlSnackContent;
	private HorizontalListView mHlvSnackTab;
	private PullToRefreshListView mPullRefreshSnackListView;
	private ListView mSnackListView;
	private LinearLayout mSnackEmptyView;

	private ArrayList<Object> mSnackTypeList = new ArrayList<Object>();
	private GroupAdapter mSnackTypeAdapter;
	
	private ArrayList<Product> mSnackList = new ArrayList<Product>();
	private ProductAdapter mSnackAdapter;
	
	protected ProductType mSnackType;
	
	private FrameLayout mLlSmokeTeaTitle;
	private TextView mTvSmokeTeaTitle;
	private ImageView mIvSmokeTeaExpand;
	private LinearLayout mLlSmokeTeaContent;
	private HorizontalListView mHlvSmokeTeaTab;
	private PullToRefreshListView mPullRefreshSmokeTeaListView;
	private ListView mSmokeTeaListView;
	private LinearLayout mSmokeTeaEmptyView;

	private ArrayList<Object> mSmokeTeaTypeList = new ArrayList<Object>();
	private GroupAdapter mSmokeTeaTypeAdapter;
	
	private ArrayList<Product> mSmokeTeaList = new ArrayList<Product>();
	private ProductAdapter mSmokeTeaAdapter;
	
	protected ProductType mSmokeTeaType;
	
	private FrameLayout mLlRecommendTitle;
	private TextView mTvRecommendTitle;
	private ImageView mIvRecommendExpand;
	private LinearLayout mLlRecommendContent;
	private PullToRefreshListView mPullRefreshRecommendListView;
	private ListView mRecommendListView;
	private LinearLayout mRecommendEmptyView;

	private ArrayList<Product> mRecommendList = new ArrayList<Product>();
	private ProductAdapter mRecommendAdapter;
	
	protected ProductType mRecommendType;

	private View mEmptyView;
	
	private LinearLayout mLlBottomBar;
//	private TextView mTvShoppingCount;
	private TextView mTvTotalPrice;
	private Button mBtnToPayment;
	
	private LayoutInflater mInflater;

	private int mIndex = 1;
	
	private Merchant mMerchant;
	private RoomType mRoomType;
	private Room mRoom;
	
	private int mShoppingCount;
	private double mTotalPrice;

	private String mDate;
	private int mFrom;
	
	private ChooseProductHandler mHandler;

	protected int productTopType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_product_list);
		
		AppController.getInstance().clearShoppingCart();
//		onLoadProductType();
	}

	@Override
	public void onInit() {
		mInflater = LayoutInflater.from(this);
		
		Intent intent = getIntent();
		if (intent != null) {
			mRoomType = (RoomType) intent.getSerializableExtra("roomType");
			mRoom = (Room) intent.getSerializableExtra("room");
			mMerchant = (Merchant) intent.getSerializableExtra("merchant");
			mDate = intent.getStringExtra("date");
			mFrom = intent.getIntExtra("from", Constants.From.MERCHANT);
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
		mLlRoomTitle = (LinearLayout) findViewById(R.id.ll_room_title);
		mTvRoomTitle = (TextView) findViewById(R.id.tv_room_title);
		mIvRoomExpand = (ImageView) findViewById(R.id.iv_room_expand);
		mLlRoomContent = (LinearLayout) findViewById(R.id.ll_room_content);
		mTvRoomDesc = (TextView) findViewById(R.id.tv_room_desc);
		
		mLlDrinkTitle = (FrameLayout) findViewById(R.id.ll_drink_title);
		mTvDrinkTitle = (TextView) findViewById(R.id.tv_drink_title);
		mIvDrinkExpand = (ImageView) findViewById(R.id.iv_drink_expand);
		mLlDrinkContent = (LinearLayout) findViewById(R.id.ll_drink_content);
		mHlvDrinkTab = (HorizontalListView) findViewById(R.id.hlv_drink_tab);
		mPullRefreshDrinkListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_drink_list);
//		mPullRefreshDrinkListView.setMode(Mode.PULL_FROM_END);
		mDrinkListView = mPullRefreshDrinkListView.getRefreshableView();
		mDrinkEmptyView = (LinearLayout) findViewById(R.id.ll_drink_no_data);
		
		mLlSnackTitle = (FrameLayout) findViewById(R.id.ll_snack_title);
		mTvSnackTitle = (TextView) findViewById(R.id.tv_snack_title);
		mIvSnackExpand = (ImageView) findViewById(R.id.iv_snack_expand);
		mLlSnackContent = (LinearLayout) findViewById(R.id.ll_snack_content);
		mHlvSnackTab = (HorizontalListView) findViewById(R.id.hlv_snack_tab);
		mPullRefreshSnackListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_snack_list);
//		mPullRefreshSnackListView.setMode(Mode.PULL_FROM_END);
		mSnackListView = mPullRefreshSnackListView.getRefreshableView();
		mSnackEmptyView = (LinearLayout) findViewById(R.id.ll_snack_no_data);
		
		mLlSmokeTeaTitle = (FrameLayout) findViewById(R.id.ll_smoke_tea_title);
		mTvSmokeTeaTitle = (TextView) findViewById(R.id.tv_smoke_tea_title);
		mIvSmokeTeaExpand = (ImageView) findViewById(R.id.iv_smoke_tea_expand);
		mLlSmokeTeaContent = (LinearLayout) findViewById(R.id.ll_smoke_tea_content);
		mHlvSmokeTeaTab = (HorizontalListView) findViewById(R.id.hlv_smoke_tea_tab);
		mPullRefreshSmokeTeaListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_smoke_tea_list);
//		mPullRefreshSmokeTeaListView.setMode(Mode.PULL_FROM_END);
		mSmokeTeaListView = mPullRefreshSmokeTeaListView.getRefreshableView();
		mSmokeTeaEmptyView = (LinearLayout) findViewById(R.id.ll_smoke_tea_no_data);
		
		mLlRecommendTitle = (FrameLayout) findViewById(R.id.ll_recommend_title);
		mTvRecommendTitle = (TextView) findViewById(R.id.tv_recommend_title);
		mIvRecommendExpand = (ImageView) findViewById(R.id.iv_recommend_expand);
		mLlRecommendContent = (LinearLayout) findViewById(R.id.ll_recommend_content);
		mPullRefreshRecommendListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_recommend_list);
//		mPullRefreshRecommendListView.setMode(Mode.PULL_FROM_END);
		mRecommendListView = mPullRefreshRecommendListView.getRefreshableView();
		mRecommendEmptyView = (LinearLayout) findViewById(R.id.ll_recommend_no_data);
		
		mEmptyView = findViewById(R.id.empty_view);
		
		mLlBottomBar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
//		mTvShoppingCount = (TextView) findViewById(R.id.tv_shopping_count);
		mTvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
		mBtnToPayment = (Button) findViewById(R.id.btn_to_payment);
	}

	@Override
	public void onInitViewData() {
		mTvRoomTitle.setText("您预定的房间信息");
		
		if (mRoomType != null) {
			if (mRoomType.startAmt > 0 && mFrom == Constants.From.MERCHANT) {
				mTotalPrice = mRoomType.startAmt;
				mShoppingCount = 1;
				mTvTotalPrice.setText("¥" + Utils.formatTwoFractionDigits(mTotalPrice));
				mEmptyView.setVisibility(View.VISIBLE);
				showBottomBar();
			} else {
				hideBottomBar();
			}
			String desc = mRoomType.useDesc;
			if (TextUtils.isEmpty(desc)) {
				desc = "暂无介绍";
			}
			mTvRoomDesc.setText(desc);
		} else {
			hideBottomBar();
		}
		
		onGroupItemClick(Constants.Type.ROOM);
		
		mDrinkTypeAdapter = new GroupAdapter(this, mDrinkTypeList);
		mHlvDrinkTab.setAdapter(mDrinkTypeAdapter);

		mDrinkAdapter = new ProductAdapter(this, mDrinkList, mHandler);
		mDrinkListView.setAdapter(mDrinkAdapter);
		
		mSnackTypeAdapter = new GroupAdapter(this, mSnackTypeList);
		mHlvSnackTab.setAdapter(mSnackTypeAdapter);

		mSnackAdapter = new ProductAdapter(this, mSnackList, mHandler);
		mSnackListView.setAdapter(mSnackAdapter);
		
		mSmokeTeaTypeAdapter = new GroupAdapter(this, mSmokeTeaTypeList);
		mHlvSmokeTeaTab.setAdapter(mSmokeTeaTypeAdapter);

		mSmokeTeaAdapter = new ProductAdapter(this, mSmokeTeaList, mHandler);
		mSmokeTeaListView.setAdapter(mSmokeTeaAdapter);
		
		mRecommendAdapter = new ProductAdapter(this, mRecommendList, mHandler);
		mRecommendListView.setAdapter(mRecommendAdapter);
		
		mRecommendType = new ProductType();
		mRecommendType.productTypeId = Constants.Type.RECOMMEND;
	}

	private void hideBottomBar() {
		mLlBottomBar.setVisibility(View.GONE);
	}
	
	private void showBottomBar() {
		mLlBottomBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onBindListener() {
		mLlRoomTitle.setOnClickListener(this);
		
		mLlDrinkTitle.setOnClickListener(this);
		mHlvDrinkTab.setOnItemClickListener(this);
		mPullRefreshDrinkListView.setOnRefreshListener(this);
		mPullRefreshDrinkListView.setOnLastItemVisibleListener(this);
//		mDrinkListView.setOnItemClickListener(this);
		
		mLlSnackTitle.setOnClickListener(this);
		mHlvSnackTab.setOnItemClickListener(this);
		mPullRefreshSnackListView.setOnRefreshListener(this);
		mPullRefreshSnackListView.setOnLastItemVisibleListener(this);
//		mSnackListView.setOnItemClickListener(this);
		
		mLlSmokeTeaTitle.setOnClickListener(this);
		mHlvSmokeTeaTab.setOnItemClickListener(this);
		mPullRefreshSmokeTeaListView.setOnRefreshListener(this);
		mPullRefreshSmokeTeaListView.setOnLastItemVisibleListener(this);
//		mSmokeTeaListView.setOnItemClickListener(this);
		
		mLlRecommendTitle.setOnClickListener(this);
		mPullRefreshRecommendListView.setOnRefreshListener(this);
		mPullRefreshRecommendListView.setOnLastItemVisibleListener(this);
//		mRecommendListView.setOnItemClickListener(this);
		
		mBtnToPayment.setOnClickListener(this);
	}

//	public void onLoad(final boolean showProgress) {
//		// Tag used to cancel the request
//		String tag = "tag_request_get_products";
//		NetRequest.getInstance(getActivity()).post(new Callback() {
//
//			@Override
//			public void onSuccess(BaseResponse response) {
//				ProductResponse productResponse = (ProductResponse)response.getResponse(new TypeToken<ProductResponse>(){});
//				if (productResponse != null && productResponse.rows != null && productResponse.rows.size() > 0) {
//					List<Product> list = productResponse.rows;
//					for (Product product : list) {
//						if (Constants.Product.DRINKS == productTopType) {
//							mChildrenList.get(0).add(product);
//						} else if (Constants.Product.SNACK == productTopType) {
//							mChildrenList.get(1).add(product);
//						}
//					}
//					mAdapter.notifyDataSetChanged();
//				}
//			}
//
//			@Override
//			public void onPreExecute() {
//				if (showProgress) {
//					toShowProgressMsg("正在加载...");
//				}
//			}
//
//			@Override
//			public void onFinished() {
//				toCloseProgressMsg();
//			}
//
//			@Override
//			public void onFailed(String msg) {
//
//			}
//
//			@Override
//			public Map<String, String> getParams() {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("mtd", "com.guocui.tty.api.web.ProductController.getProducts");
//				params.put("merchantId", Long.toString(mMerchant.merchantId));
//				params.put("pageNo", Integer.toString(mIndex));
//				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
//				return params;
//			}
//
//		}, tag);
//	}
//	
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
//		mTvTotalPrice.setText("¥" + mTotalPrice);
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_room_title:
			onGroupItemClick(Constants.Type.ROOM);
			break;
		case R.id.ll_drink_title:
			onGroupItemClick(Constants.Type.DRINKS);
			if (mDrinkType == null) {
				onLoadProductType();
			} else if (mDrinkList == null || mDrinkList.size() == 0) {
				onLoadProductListByType(mDrinkType);
			}
			break;
		case R.id.ll_snack_title:
			onGroupItemClick(Constants.Type.SNACK);
			if (mSnackType == null) {
				onLoadProductType();
			} else if (mSnackList == null || mSnackList.size() == 0) {
				onLoadProductListByType(mSnackType);
			}
			break;
		case R.id.ll_smoke_tea_title:
			onGroupItemClick(Constants.Type.SMOKE_TEA);
			if (mSmokeTeaType == null) {
				onLoadProductType();
			} else if (mSmokeTeaList == null || mSmokeTeaList.size() == 0) {
				onLoadProductListByType(mSmokeTeaType);
			}
			break;
		case R.id.ll_recommend_title:
			onGroupItemClick(Constants.Type.RECOMMEND);
			if (mRecommendType == null) {
				onLoadProductType();
			} else if (mRecommendList == null || mRecommendList.size() == 0) {
				onLoadProductListByType(mRecommendType);
			}
			break;
		case R.id.btn_to_payment:
			if (mRoomType != null && mMerchant != null) {
				if (mFrom == Constants.From.GIFT || mTotalPrice >= mRoomType.ttAmt) {
					toConfirmOrder();
				} else {
					showToast("您选购的商品没有达到该商家的最低消费: ¥" + Utils.formatTwoFractionDigits(mRoomType.ttAmt));
				}
			}
			break;
		default:
			break;
		}
	}

	private void onGroupItemClick(int item) {
		productTopType = item;
		
		boolean isRoomExpanded = item == Constants.Type.ROOM;
		mLlRoomContent.setVisibility(isRoomExpanded ? View.VISIBLE : View.GONE);
		mTvRoomTitle.setTextColor(isRoomExpanded ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
		mLlRoomTitle.setBackgroundColor(isRoomExpanded ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.app_gray));
		mIvRoomExpand.setImageResource(isRoomExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
		
		boolean isDrinkExpanded = item == Constants.Type.DRINKS;
		mLlDrinkContent.setVisibility(isDrinkExpanded ? View.VISIBLE : View.GONE);
		mTvDrinkTitle.setTextColor(isDrinkExpanded ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
		mLlDrinkTitle.setBackgroundColor(isDrinkExpanded ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.app_gray));
		mIvDrinkExpand.setImageResource(isDrinkExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
		
		boolean isSnackExpanded = item == Constants.Type.SNACK;
		mLlSnackContent.setVisibility(isSnackExpanded ? View.VISIBLE : View.GONE);
		mTvSnackTitle.setTextColor(isSnackExpanded ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
		mLlSnackTitle.setBackgroundColor(isSnackExpanded ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.app_gray));
		mIvSnackExpand.setImageResource(isSnackExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
		
		boolean isSmokeTeaExpanded = item == Constants.Type.SMOKE_TEA;
		mLlSmokeTeaContent.setVisibility(isSmokeTeaExpanded ? View.VISIBLE : View.GONE);
		mTvSmokeTeaTitle.setTextColor(isSmokeTeaExpanded ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
		mLlSmokeTeaTitle.setBackgroundColor(isSmokeTeaExpanded ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.app_gray));
		mIvSmokeTeaExpand.setImageResource(isSmokeTeaExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
		
		boolean isRecommendExpanded = item == Constants.Type.RECOMMEND;
		mLlRecommendContent.setVisibility(isRecommendExpanded ? View.VISIBLE : View.GONE);
		mTvRecommendTitle.setTextColor(isRecommendExpanded ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
		mLlRecommendTitle.setBackgroundColor(isRecommendExpanded ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.app_gray));
		mIvRecommendExpand.setImageResource(isRecommendExpanded ? R.drawable.ic_expand_on : R.drawable.ic_expand_off);
		
		mEmptyView.setVisibility(isRoomExpanded ? View.VISIBLE : View.GONE);
	}
	
	private void onLoadProductType() {
		// 加载分类，可以第一分类的数据
		// Tag used to cancel the request
		String tag = "tag_request_get_product_types";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				List<ProductType> list = response.getListResponse(new TypeToken<List<ProductType>>() {});
				if (list != null && list.size() > 0) {
					if (productTopType == Constants.Type.DRINKS) {
						mDrinkTypeList.clear();
						mDrinkTypeList.addAll(list);
						mDrinkTypeAdapter.notifyDataSetChanged();
						
						mHlvDrinkTab.setSelection(mDrinkTypeList.size() - 1);
						mDrinkType = list.get(0);
						onLoadProductListByType(mDrinkType);
					} else if (productTopType == Constants.Type.SNACK) {
						mSnackTypeList.clear();
						mSnackTypeList.addAll(list);
						mSnackTypeAdapter.notifyDataSetChanged();
						
						mHlvSnackTab.setSelection(mSnackTypeList.size() - 1);
						mSnackType = list.get(0);
						onLoadProductListByType(mSnackType);
					} else if (productTopType == Constants.Type.SMOKE_TEA) {
						mSmokeTeaTypeList.clear();
						mSmokeTeaTypeList.addAll(list);
						mSmokeTeaTypeAdapter.notifyDataSetChanged();
						
						mHlvSmokeTeaTab.setSelection(mSmokeTeaTypeList.size() - 1);
						mSmokeTeaType = list.get(0);
						onLoadProductListByType(mSmokeTeaType);
					} else if (productTopType == Constants.Type.RECOMMEND) {
						// 无子分类
					}
				}
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在加载...");
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
				params.put("mtd", "com.guocui.tty.api.web.ProductController.getProductTypes");
//				params.put("merchantId", mSpUtil.getBelongObjIdStr());
				params.put("merchantId", Long.toString(mMerchant.merchantId));
				params.put("useStatus", "ENABLE");
				params.put("parentId", Integer.toString(productTopType));
				return params;
			}
		}, tag);
	}
	
	protected void onLoadProductListByType(final ProductType productType) {
		onLoadProductListByType(productType, false);
	}

	protected void onLoadProductListByType(final ProductType productType, final boolean silent) {
		if (productType == null) {
			return ;
		}
		
		// Tag used to cancel the request
		String tag = "tag_request_get_products";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				if (productTopType == Constants.Type.DRINKS) {
					if (mIndex == 1) {
						mDrinkList.clear();
					}
					ProductResponse productResponse = (ProductResponse) response.getResponse(new TypeToken<ProductResponse>() {});
					if (productResponse != null && productResponse.rows != null && productResponse.rows.size() > 0) {
						mDrinkList.addAll(productResponse.rows);
					}
					mDrinkAdapter.notifyDataSetChanged();
					
					if (mDrinkList.size() > 0) {
						mPullRefreshDrinkListView.setVisibility(View.VISIBLE);
						mDrinkEmptyView.setVisibility(View.GONE);
					} else {
						mPullRefreshDrinkListView.setVisibility(View.GONE);
						mDrinkEmptyView.setVisibility(View.VISIBLE);
					}
				} else if (productTopType == Constants.Type.SNACK) {
					if (mIndex == 1) {
						mSnackList.clear();
					}
					ProductResponse productResponse = (ProductResponse) response.getResponse(new TypeToken<ProductResponse>() {});
					if (productResponse != null && productResponse.rows != null && productResponse.rows.size() > 0) {
						mSnackList.addAll(productResponse.rows);
					}
					mSnackAdapter.notifyDataSetChanged();
					
					if (mSnackList.size() > 0) {
						mPullRefreshSnackListView.setVisibility(View.VISIBLE);
						mSnackEmptyView.setVisibility(View.GONE);
					} else {
						mPullRefreshSnackListView.setVisibility(View.GONE);
						mSnackEmptyView.setVisibility(View.VISIBLE);
					}
				} else if (productTopType == Constants.Type.SMOKE_TEA) {
					if (mIndex == 1) {
						mSmokeTeaList.clear();
					}
					ProductResponse productResponse = (ProductResponse) response.getResponse(new TypeToken<ProductResponse>() {});
					if (productResponse != null && productResponse.rows != null && productResponse.rows.size() > 0) {
						mSmokeTeaList.addAll(productResponse.rows);
					}
					mSmokeTeaAdapter.notifyDataSetChanged();
					
					if (mSmokeTeaList.size() > 0) {
						mPullRefreshSmokeTeaListView.setVisibility(View.VISIBLE);
						mSmokeTeaEmptyView.setVisibility(View.GONE);
					} else {
						mPullRefreshSmokeTeaListView.setVisibility(View.GONE);
						mSmokeTeaEmptyView.setVisibility(View.VISIBLE);
					}
				} else if (productTopType == Constants.Type.RECOMMEND) {
					if (mIndex == 1) {
						mRecommendList.clear();
					}
					ProductResponse productResponse = (ProductResponse) response.getResponse(new TypeToken<ProductResponse>() {});
					if (productResponse != null && productResponse.rows != null && productResponse.rows.size() > 0) {
						mRecommendList.addAll(productResponse.rows);
					}
					mRecommendAdapter.notifyDataSetChanged();
					
					if (mRecommendList.size() > 0) {
						mPullRefreshRecommendListView.setVisibility(View.VISIBLE);
						mRecommendEmptyView.setVisibility(View.GONE);
					} else {
						mPullRefreshRecommendListView.setVisibility(View.GONE);
						mRecommendEmptyView.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void onPreExecute() {
				if (!silent) {
					toShowProgressMsg("正在加载...");
				}
			}

			@Override
			public void onFinished() {
				toCloseProgressMsg();
				if (productTopType == Constants.Type.DRINKS) {
					mPullRefreshDrinkListView.onRefreshComplete();
				} else if (productTopType == Constants.Type.SNACK) {
					mPullRefreshSnackListView.onRefreshComplete();
				} else if (productTopType == Constants.Type.SMOKE_TEA) {
					mPullRefreshSmokeTeaListView.onRefreshComplete();
				} else if (productTopType == Constants.Type.RECOMMEND) {
					mPullRefreshRecommendListView.onRefreshComplete();
				}
			}

			@Override
			public void onFailed(String msg) {
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.ProductController.getProducts");
//				params.put("merchantId", mSpUtil.getBelongObjIdStr());
				params.put("merchantId", Long.toString(mMerchant.merchantId));
				params.put("productType", Integer.toString(productType.productTypeId));
				params.put("status", "UP");
				params.put("pageNo", Integer.toString(mIndex));
				params.put("pageSize", Integer.toString(NetRequest.PAGE_SIZE));
				return params;
			}
		}, tag);
	}

	private void toConfirmOrder() {
		Intent intent = new Intent();
		intent.setClass(this, ConfirmOrderActivity.class);
		intent.putExtra("roomType", mRoomType);
		intent.putExtra("room", mRoom);
		intent.putExtra("merchant", mMerchant);
		intent.putExtra("totalPrice", mTotalPrice);
		intent.putExtra("date", mDate);
		intent.putExtra("from", mFrom);
		startActivityForResult(intent, REQUEST_CONFIRM_ORDER);
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
				mTotalPrice = mTotalPrice + product.ttPrice;
				mTvTotalPrice.setText("¥" + Utils.formatTwoFractionDigits(mTotalPrice));
				
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
				mTotalPrice = mTotalPrice - product.ttPrice;
				if (mTotalPrice < 0) {
					mTotalPrice = 0;
				}
				mTvTotalPrice.setText("¥" + Utils.formatTwoFractionDigits(mTotalPrice));
				
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
		if (REQUEST_CONFIRM_ORDER == requestCode && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		mIndex = 1;
		ProductType productType = null;
		if (productTopType == Constants.Type.DRINKS) {
			productType = mDrinkType;
		} else if (productTopType == Constants.Type.SNACK) {
			productType = mSnackType;
		} else if (productTopType == Constants.Type.SMOKE_TEA) {
			productType = mSmokeTeaType;
		} else if (productTopType == Constants.Type.RECOMMEND) {
			productType = mRecommendType;
		}
		onLoadProductListByType(productType, true);
	}

	@Override
	public void onLastItemVisible() {
		mIndex ++;
		ProductType productType = null;
		if (productTopType == Constants.Type.DRINKS) {
			productType = mDrinkType;
		} else if (productTopType == Constants.Type.SNACK) {
			productType = mSnackType;
		} else if (productTopType == Constants.Type.SMOKE_TEA) {
			productType = mSmokeTeaType;
		} else if (productTopType == Constants.Type.RECOMMEND) {
			productType = mRecommendType;
		}
		onLoadProductListByType(productType, true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		ProductType productType = (ProductType) parent.getAdapter().getItem(position);
		if (productType != null) {
			if (productTopType == Constants.Type.DRINKS) {
				mDrinkTypeAdapter.setSelectedItem(position);
				mDrinkTypeAdapter.notifyDataSetChanged();
				
				mDrinkType = productType;
			} else if (productTopType == Constants.Type.SNACK) {
				mSnackTypeAdapter.setSelectedItem(position);
				mSnackTypeAdapter.notifyDataSetChanged();
				
				mSnackType = productType;
			} else if (productTopType == Constants.Type.SMOKE_TEA) {
				mSmokeTeaTypeAdapter.setSelectedItem(position);
				mSmokeTeaTypeAdapter.notifyDataSetChanged();
				
				mSmokeTeaType = productType;
			} else if (productTopType == Constants.Type.RECOMMEND) {
				// 无子分类
			}
			mIndex = 1;
			onLoadProductListByType(productType);
		}
	}

}
