package com.dailysee.ui.merchant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.adapter.RoomAdapter;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.RoomType;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.UiHelper;
import com.google.gson.reflect.TypeToken;

public class MerchantRoomListActivity extends BaseActivity implements OnClickListener {

	protected static final int REQUEST_SELECT_PRODUCT = 10001;
	
	private ListView mListView;
	private ArrayList<RoomType> items = new ArrayList<RoomType>();
	private RoomAdapter mAdapter;

	private LinearLayout mLlMerchantTitle;
	private ImageView mIvExpand;
	private LinearLayout mLlMerchantInfo;
	private TextView mTvMerchantTitle;
	private ImageView mIvImage;
	private TextView mTvMerchantDesc;
	private LayoutInflater mInflater;
	private Merchant mMerchant;
	private long mMerchantId;
	protected String mDate;

	private int mFrom;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_room_list);

		onLoad();
	}

	@Override
	public void onInit() {
		mInflater = LayoutInflater.from(this);

		Intent intent = getIntent();
		if (intent != null) {
			mMerchant = (Merchant) intent.getSerializableExtra("merchant");
			mMerchantId = intent.getLongExtra("merchantId", 0);
			mDate = intent.getStringExtra("date");
			mFrom = intent.getIntExtra("from", Constants.From.MERCHANT);
		}

		if (mMerchant == null || mMerchantId == 0)
			finish();

		String title = null;
		if (mMerchant != null) {
			title = mMerchant.name;
		}
		if (TextUtils.isEmpty(title)) {
			title = "商家详情";
		}
		setTitle(title);
		setUp();
	}

	@Override
	public void onFindViews() {

		View header = mInflater.inflate(R.layout.item_merchant_header, null);

		mLlMerchantTitle = (LinearLayout) header.findViewById(R.id.ll_merchant_title);
		mTvMerchantTitle = (TextView) header.findViewById(R.id.tv_merchant_title);
		mIvExpand = (ImageView) header.findViewById(R.id.iv_expand);
		mLlMerchantInfo = (LinearLayout) header.findViewById(R.id.ll_merchant_info);
		mIvImage = (ImageView) header.findViewById(R.id.iv_image);
		mTvMerchantDesc = (TextView) header.findViewById(R.id.tv_merchant_desc);

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.addHeaderView(header);
	}

	@Override
	public void onInitViewData() {
		mAdapter = new RoomAdapter(getActivity(), items);
		mListView.setAdapter(mAdapter);

		if (mMerchant != null) {
			if (!TextUtils.isEmpty(mMerchant.logoUrl)) {
				AppController.getInstance().getImageLoader()
						.get(mMerchant.logoUrl, ImageLoader.getImageListener(mIvImage, R.drawable.ic_merchant_avatar, R.drawable.ic_merchant_avatar));
			}

			String desc = mMerchant.introduction;
			if (TextUtils.isEmpty(desc)) {
				desc = "暂无介绍";
			}
			mTvMerchantDesc.setText(desc);
		}
	}

	@Override
	public void onBindListener() {
		mLlMerchantTitle.setOnClickListener(this);
		mIvImage.setOnClickListener(this);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RoomType roomType = (RoomType) arg0.getAdapter().getItem(arg2);
				if (roomType != null) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), MerchantProductListActivity.class);
					intent.putExtra("merchant", mMerchant);
					intent.putExtra("roomType", roomType);
					intent.putExtra("date", mDate);
					intent.putExtra("from", mFrom);
					startActivityForResult(intent, REQUEST_SELECT_PRODUCT);
				}
			}
		});
	}

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
		case R.id.iv_image:
			if (mMerchant != null) {
				UiHelper.toBrowseImage(getActivity(), mMerchant.logoUrl);
			}
			break;
		default:
			break;
		}
	}

	private void onLoad() {
		// Tag used to cancel the request
		String tag = "tag_request_merchant_room_list";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				ArrayList<RoomType> list = response.getListResponse(new TypeToken<ArrayList<RoomType>>() {});
				if (list != null && list.size() > 0) {
					items.clear();
					items.addAll(list);
					mAdapter.notifyDataSetChanged();
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
				params.put("mtd", "com.guocui.tty.api.web.RoomController.getRoomTypes");
				params.put("merchantId", Long.toString(mMerchantId));
				return params;
			}

		}, tag);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (REQUEST_SELECT_PRODUCT == requestCode) {
			setResult(RESULT_OK);
			finish();
		}
	}

}
