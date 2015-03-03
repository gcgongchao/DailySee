package com.dailysee.ui.merchant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
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
import com.dailysee.util.Utils;
import com.dailysee.widget.MyDatePickerDialog;
import com.google.gson.reflect.TypeToken;

public class MerchantRoomListActivity extends BaseActivity implements OnClickListener, OnRoomClickListener {

	protected static final int REQUEST_SELECT_PRODUCT = 10001;
	
	private ExpandableListView mListView;
	private ArrayList<RoomType> mGroupList = new ArrayList<RoomType>();
	private Map<Long, List<Room>> mChildrenList = new HashMap<Long, List<Room>>();
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
	protected RoomType mRoomType;
	private int mLastGroupClick = -1;

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
		mListView.setChildDivider(getResources().getDrawable(R.color.gray));
		
		emptyView = findViewById(R.id.ll_no_data);
	}

	@Override
	public void onInitViewData() {
		mAdapter = new RoomAdapter(getActivity(), mFrom, mGroupList, mChildrenList , this);
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
		
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				
//			}
//		});
		mListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View arg1, int groupPositiion, long arg3) {
				if (mLastGroupClick == -1) {
					parent.expandGroup(groupPositiion);
				} else if (mLastGroupClick != groupPositiion) {
					parent.collapseGroup(mLastGroupClick);
					parent.expandGroup(groupPositiion);
				} else if (mLastGroupClick == groupPositiion) {
					if (parent.isGroupExpanded(groupPositiion)) {
						parent.collapseGroup(groupPositiion);
					} else {
						parent.expandGroup(groupPositiion);
					}
				}
				
				mLastGroupClick = groupPositiion;
				
				Object obj = parent.getAdapter().getItem(groupPositiion + 1);
				if (obj != null && obj instanceof RoomType) {
					mRoomType = (RoomType) obj;
					switch (mFrom) {
					case Constants.From.MERCHANT:
//						toMerchantProductList(mRoomType);
						showSelectBookingDateDialog(mRoomType);
						break;
					case Constants.From.GIFT:
						toSelectRoom(mRoomType);
						break;
					default:
						break;
					}
				}
				return true;
			}
		});
	}
	
	private void showSelectBookingDateDialog(final RoomType mRoomType) {
		showSelectBookingDateDialog(mRoomType, null);
	}
	
	private void showSelectBookingDateDialog(final RoomType mRoomType, final Room room) {
//		mSelectBookingDateDialog = new SelectBookingDateDialog(getActivity(), "选择预订日期", new OnDateSelectedListener() {
//			
//			@Override
//			public void onDateUnselected(Date date) {
//				
//			}
//			
//			@Override
//			public void onDateSelected(Date date) {
//				Utils.clossDialog(mSelectBookingDateDialog);
//				
//				String dateStr = Utils.formatDate(date, Utils.DATE_FORMAT_YMD);
//				toMerchantRoomList(merchant, dateStr);
//			}
//		});
//		mSelectBookingDateDialog.show();
		
		Calendar c = Calendar.getInstance();
		final int year = c.get(Calendar.YEAR);
		final int month = c.get(Calendar.MONTH);
		final int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		
		final String curDate = year + "-" + (month+1) + "-" + dayOfMonth;
		MyDatePickerDialog dialog = new MyDatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                    	if (!Utils.isFastDoubleClick()) {
	                    	String dateStr = year + "-" + (month+1) + "-" + dayOfMonth;
	                    	Date cur = Utils.formatDate(curDate, Utils.DATE_FORMAT_YMD);
	                    	Date date = Utils.formatDate(dateStr, Utils.DATE_FORMAT_YMD);
	                    	long offset = (date.getTime() - cur.getTime())/ 1000 / 60 / 60 / 24;
	                    	if (offset >= 0 && offset <= 7) {
		                    	mDate = dateStr;
		                    	toMerchantProductList(mRoomType, room); 
	                    	} else if (offset < 0) {
	                    		showToast("仅可预定七日之内的日期");
	                    	} else if (offset > 7) {
	                    		showToast("仅可预定七日之内的日期");
	                    	}
                    	}
                    }
                }, 
                year, // 传入年份
                month, // 传入月份
                dayOfMonth // 传入天数
            );
		dialog.show();
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
					
					if (!mListView.isGroupExpanded(mLastGroupClick)) {
						mListView.expandGroup(mLastGroupClick);
					}
				} else {
					showToast("该房型暂无房间可赠送");
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在加载房间...");
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

	private void toMerchantProductList(RoomType roomType, Room room) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MerchantProductListActivity.class);
		intent.putExtra("merchant", mMerchant);
		intent.putExtra("roomType", roomType);
		intent.putExtra("room", room);
		intent.putExtra("date", mDate);
		intent.putExtra("from", mFrom);
		startActivityForResult(intent, REQUEST_SELECT_PRODUCT);
	}

	@Override
	public void onRoomClick(Room room) {
		if (mRoomType != null && room != null) {
			showSelectBookingDateDialog(mRoomType, room);
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
				List<RoomType> list = response.getListResponse(new TypeToken<List<RoomType>>() {});
				if (list != null && list.size() > 0) {
					mGroupList.addAll(list);
				}
				mAdapter.notifyDataSetChanged();
				
				if (mGroupList.size() > 0) {
					if (mFrom == Constants.From.GIFT) {
						mLastGroupClick = 0;
						mRoomType = mGroupList.get(0);
						toSelectRoom(mRoomType);
					}
					emptyView.setVisibility(View.GONE);
				} else {
					emptyView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPreExecute() {
				toShowProgressMsg("正在加载房间类型...");
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
