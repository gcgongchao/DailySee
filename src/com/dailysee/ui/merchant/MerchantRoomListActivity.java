package com.dailysee.ui.merchant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dailysee.AppController;
import com.dailysee.R;
import com.dailysee.adapter.RoomAdapter;
import com.dailysee.adapter.RoomAdapter.OnRoomClickListener;
import com.dailysee.bean.Member;
import com.dailysee.bean.Merchant;
import com.dailysee.bean.Room;
import com.dailysee.bean.RoomType;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.net.response.RoomResponse;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.UiHelper;
import com.google.gson.reflect.TypeToken;

public class MerchantRoomListActivity extends BaseActivity implements OnClickListener, OnRoomClickListener {

	protected static final int REQUEST_SELECT_PRODUCT = 10001;
	
	private ExpandableListView mListView;
	private ArrayList<RoomType> mGroupList = new ArrayList<RoomType>();
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

	private Map<Long, List<Room>> mChildrenList = new HashMap<Long, List<Room>>();

	protected RoomType mRoomType;

	private View emptyView;

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

		if (mMerchant == null && mMerchantId == 0) {// 商家信息不存在，直接退出
			finish();
		}

		onRefreshTitle();
		setUp();
	}

	private void onRefreshTitle() {
		String title = null;
		if (mMerchant != null) {
			title = mMerchant.name;
		}
		if (TextUtils.isEmpty(title)) {
			title = "商家详情";
		}
		setTitle(title);
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

		mListView = (ExpandableListView) findViewById(R.id.list_view);
		mListView.addHeaderView(header);
		mListView.setGroupIndicator(null);
		
		emptyView = findViewById(R.id.ll_no_data);
	}

	@Override
	public void onInitViewData() {
		mAdapter = new RoomAdapter(getActivity(), mGroupList, mChildrenList , this);
		mListView.setAdapter(mAdapter);

		onRefreshMerchant();
	}

	private void onRefreshMerchant() {
		if (mMerchant != null) {
			if (!TextUtils.isEmpty(mMerchant.logoUrl)) {
				AppController.getInstance().getImageLoader()
						.get(mMerchant.logoUrl, ImageLoader.getImageListener(mIvImage, R.drawable.ic_noimage, R.drawable.ic_noimage));
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
		
		mListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View arg1, int groupPositiion, long arg3) {
				RoomType roomType = (RoomType) parent.getAdapter().getItem(groupPositiion + 1);
				if (roomType != null) {
					mRoomType = roomType;
					switch (mFrom) {
					case Constants.From.MERCHANT:
						toMerchantProductList(roomType);
						break;
					case Constants.From.GIFT:
						toSelectRoom(roomType);
						break;
					default:
						break;
					}
				}
				return false;
			}
		});
	}

	protected void toSelectRoom(RoomType roomType) {
		List<Room> roomList = mChildrenList.get(roomType.roomTypeId);
		if (roomList == null) {
			requestRoomListByType(roomType);
		} else {
			
		}
	}

	private void requestRoomListByType(final RoomType roomType) {
		if (roomType == null) {
			return ;
		}
		
		// Tag used to cancel the request
		String tag = "tag_request_get_rooms";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				RoomResponse roomResponse = (RoomResponse) response.getResponse(new TypeToken<RoomResponse>() {});
				if (roomResponse != null && roomResponse.rows != null && roomResponse.rows.size() > 0) {
					List<Room> roomList = roomResponse.rows;
					mChildrenList.put(roomType.roomTypeId, roomList);
				} else {
					showToast("该房型暂无房间可赠送");
				}
				mAdapter.notifyDataSetChanged();
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
				params.put("mtd", "com.guocui.tty.api.web.RoomController.getRooms");
				params.put("merchantId", Long.toString(mMerchant.merchantId));
				params.put("roomType", Long.toString(roomType.roomTypeId));
				params.put("pageNo", "1");
				params.put("pageSize", Integer.toString(NetRequest.PAGE_LARGE_SIZE));
				return params;
			}
		}, tag);
	}

	private void toMerchantProductList(RoomType roomType) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MerchantProductListActivity.class);
		intent.putExtra("merchant", mMerchant);
		intent.putExtra("roomType", roomType);
		intent.putExtra("date", mDate);
		intent.putExtra("from", mFrom);
		startActivityForResult(intent, REQUEST_SELECT_PRODUCT);
	}

	@Override
	public void onRoomClick(Room room) {
		if (mRoomType != null && room != null) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), MerchantProductListActivity.class);
			intent.putExtra("merchant", mMerchant);
			intent.putExtra("roomType", mRoomType);
			intent.putExtra("room", room);
			intent.putExtra("date", mDate);
			intent.putExtra("from", mFrom);
			startActivityForResult(intent, REQUEST_SELECT_PRODUCT);
		}
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
		if (mMerchant == null) {
			onLoadMerchantInfo();
		} else {
			onLoadMerchantRoomList();
		}
	}
	
	private void onLoadMerchantInfo() {
		// Tag used to cancel the request
		String tag = "tag_request_get_member_detail";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mMerchant = (Merchant) response.getResponse(new TypeToken<Merchant>() {});
				if (mMerchant != null) {
					onRefreshTitle();
					onRefreshMerchant();
					onLoadMerchantRoomList();
				} else {
					showToast("商家信息不存在");
					finish();
				}
			}
			
			@Override
			public void onFailed(String msg) {
				super.onFailed(msg);
				finish();
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getMemberDetail");
				params.put("belongObjId", Long.toString(mMerchantId));
				params.put("memberType", "MERCHANT");
				return params;
			}
		}, tag);
	}

	private void onLoadMerchantRoomList() {
		// Tag used to cancel the request
		String tag = "tag_request_merchant_room_list";
		NetRequest.getInstance(getActivity()).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mGroupList.clear();
				ArrayList<RoomType> list = response.getListResponse(new TypeToken<ArrayList<RoomType>>() {});
				if (list != null && list.size() > 0) {
					mGroupList.addAll(list);
				} else {
					mListView.setEmptyView(emptyView);
				}
				mAdapter.notifyDataSetChanged();
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
				params.put("mtd", "tty.roomtype.list.get");
				params.put("merchantId", Long.toString(mMerchantId));
				return params;
			}

		}, tag);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (REQUEST_SELECT_PRODUCT == requestCode && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

}
