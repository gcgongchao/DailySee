package com.dailysee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dailysee.bean.CityEntity;
import com.dailysee.bean.Member;
import com.dailysee.bean.Push;
import com.dailysee.db.CityDb;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.CallMeFragment;
import com.dailysee.ui.HomeFragment;
import com.dailysee.ui.MessageFragment;
import com.dailysee.ui.UserFragment;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;
import com.dailysee.widget.BadgeView;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author Administrator
 * 
 */
public class MainActivity extends BaseActivity implements OnTabChangeListener {
	
	public static final String TAB_HOME = "home";
	public static final String TAB_MESSAGE = "message";
	public static final String TAB_CALLME = "callme";
	public static final String TAB_USER = "user";
	
	public static final String TAB_HOME_TITLE = "首页";
	public static final String TAB_MESSAGE_TITLE = "天天讯息";
	public static final String TAB_CALLME_TITLE = "呼叫天天";
	public static final String TAB_USER_TITLE = "个人中心";

	private static final String TAG = MainActivity.class.getSimpleName();

	private FragmentTabHost mTabHost;

	private boolean mLoadDataRequired = true;
	private boolean tabChanged = false;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private DelayHandler mHander;

	protected List<CityEntity> mProvinceList;

	public String mProvince;
	public String mCity;
	private BadgeView mMessageBadge;
	private BadgeView mUserBadge;
	
	private PushReceiver mPushReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);
		
		enterTab(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		enterTab(intent);
	}

	private void enterTab(Intent intent) {
		if (intent != null) {
			int curTab = 0;
			String tab = intent.getStringExtra("tab");
			if (TAB_HOME.equals(tab)) {
				curTab = 0;
			} else if (TAB_MESSAGE.equals(tab)) {
				curTab = 1;
			} else if (TAB_CALLME.equals(tab)) {
				curTab = 2;
			} else if (TAB_USER.equals(tab)) {
				curTab = 2;
			} else {
				curTab = -1;
			}
			
			if (curTab >= 0) {
				mTabHost.setCurrentTab(curTab);
				if (curTab == 1) {
					MessageFragment fragment = getMessageFragment();
					if (fragment != null) {// 主动刷新tab
						fragment.onTabDoubleClick();
					}
				}
			}
		}
	}

	@Override
	public void onInit() {
		mHander = new DelayHandler();

		initLocation();
		onLoadCity();
	}

	@Override
	public void onFindViews() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		// mTabHost.getTabWidget().setDividerDrawable(R.color.white);
	}

	@Override
	public void onInitViewData() {
		addTab(TAB_HOME, TAB_HOME_TITLE, HomeFragment.class, R.drawable.item_tab_1_selector);
		addTab(TAB_MESSAGE, TAB_MESSAGE_TITLE, MessageFragment.class, R.drawable.item_tab_2_selector);
		addTab(TAB_CALLME, TAB_CALLME_TITLE, CallMeFragment.class, R.drawable.item_tab_3_selector);
		addTab(TAB_USER, TAB_USER_TITLE, UserFragment.class, R.drawable.item_tab_4_selector);

		mMessageBadge = new BadgeView(this, mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.iv_tab));
		mUserBadge = new BadgeView(this, mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.iv_tab));
		
		mPushReceiver = new PushReceiver();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PUSH);
		registerReceiver(mPushReceiver, filter);
	}

	@Override
	public void onBindListener() {
		mTabHost.setOnTabChangedListener(this);
		View tab1 = mTabHost.getTabWidget().getChildAt(1);
		if (tab1 != null) {
			tab1.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent e) {
					switch (e.getAction()) {
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						if (tabChanged || mSpUtil.getNewMsgCount() > 0) {
							Object tag = v.getTag();
							if (tag != null) {
								String tab = tag.toString();
								Fragment fragment = getSupportFragmentManager().findFragmentByTag(tab);
								if (fragment != null && fragment instanceof OnTabDoubleClickListener) {
									((OnTabDoubleClickListener)fragment).onTabDoubleClick();
								}
							}
						}
						break;
					}
					return false;
				}
			});
		}
	}

	private void addTab(String key, String title, Class fragment, int resId) {
		Bundle b = new Bundle();
		b.putString(key, title);
		mTabHost.addTab(setIndicator(mTabHost.newTabSpec(key), key, resId), fragment, b);
	}

	public TabSpec setIndicator(TabSpec spec, String tab, int resId) {
		View v = LayoutInflater.from(this).inflate(R.layout.item_tab, null);

		ImageView ivTab = (ImageView) v.findViewById(R.id.iv_tab);
		ivTab.setImageResource(resId);
		 
		v.setTag(tab);

		return spec.setIndicator(v);
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		} else {
			Log.d("LocSDK4", "locClient is null or not started");
		}

		mHander.sendEmptyMessageDelayed(DelayHandler.DELAY_STOP_LOCATION, 15 * 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onLoadData();
		onLoadNewMessage();
		toBindPush();
		onRefreshNewMsgCount();
	}
	 
	private void toBindPush() {
		if (!Utils.hasBind(this)) {
			// Tag used to cancel the request
			String tag = "tag_request_bind_user";
			NetRequest.getInstance(this).post(new Callback() {

				@Override
				public void onSuccess(BaseResponse response) {
					Utils.setBind(getActivity(), true);
				}

				@Override
				public Map<String, String> getParams() {
					Map<String, String> params = new HashMap<String, String>();
					params.put("mtd", "tty.member.bind.user");
					params.put("memberId", mSpUtil.getMemberIdStr());
					params.put("userId", Utils.getClientId(getActivity()));
					params.put("channelId", "0");
					params.put("token", mSpUtil.getToken());
					return params;
				}
			}, tag, true);
		}
	}

	private void onRefreshNewMsgCount() {
		int newMsgCount = mSpUtil.getNewMsgCount();
		mMessageBadge.setText(newMsgCount + "");
		if (newMsgCount > 0) {
			mMessageBadge.show();
		} else {
			mMessageBadge.hide();
		}
		
		int newUserMsgCount = mSpUtil.getNewCommentCount() + mSpUtil.getNewConfirmOrderCount() + mSpUtil.getNewRefundResultCount();
		mUserBadge.setText(newUserMsgCount + "");
		if (newUserMsgCount > 0) {
			mUserBadge.show();
		} else {
			mUserBadge.hide();
		}
		if (TAB_USER.equals(mTabHost.getCurrentTabTag())) {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAB_USER);
			if (fragment != null && fragment instanceof UserFragment) {
				((UserFragment)fragment).onRefreshNewMsgCount();
			}
		}
	}

	private void onLoadNewMessage() {
		if (!mSpUtil.isLogin()) {
			return;
		}

		// Tag used to cancel the request
		String tag = "tag_request_get_new_message";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				List<Push> list = response.getListResponse(new TypeToken<List<Push>>() {});
				if (list != null && list.size() > 0) {
//					int count = mSpUtil.getNewMsgCount();
					for (Push push : list) {
						if ("03".equals(push.msgType)) {
							int count = mSpUtil.getNewMsgCount();
							count += push.cnt;
							mSpUtil.setNewMsgCount(count);
						} else if ("05".equals(push.msgType)) {
							int count = mSpUtil.getNewCommentCount();
							count += push.cnt;
							mSpUtil.setNewCommentCount(count);
						} else if ("07".equals(push.msgType)) {
							int count = mSpUtil.getNewConfirmOrderCount();
							count += push.cnt;
							mSpUtil.setNewConfirmOrderCount(count);
						} else if ("10".equals(push.msgType)) {
							int count = mSpUtil.getNewRefundResultCount();
							count += push.cnt;
							mSpUtil.setNewRefundResultCount(count);
						}
					}
					
					onRefreshNewMsgCount();
				}
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "tty.message.list.get");
				params.put("memberId", mSpUtil.getMemberIdStr());
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag, true);
	}

	private void onLoadData() {
		if (mLoadDataRequired) {
			onLoadMyInfo();
		}
	}

	private void onLoadMyInfo() {
		if (!mSpUtil.isLogin()) {
			return;
		}

		// Tag used to cancel the request
		String tag = "tag_request_get_member_detail";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Member member = (Member) response.getResponse(new TypeToken<Member>() {
				});
				if (member != null) {
					mLoadDataRequired = false;
					mSpUtil.setMember(member);

					Intent intent = new Intent(Constants.REFRESH_MEMBER_DETAIL);
					sendBroadcast(intent);
				}
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getMemberDetail");
				params.put("belongObjId", mSpUtil.getBelongObjIdStr());
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		UserFragment userFragment = getUserFragment();
		if (userFragment != null) {
			userFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	private UserFragment getUserFragment() {
		Fragment userFragment = getSupportFragmentManager().findFragmentByTag(TAB_USER);
		if (userFragment != null && userFragment instanceof UserFragment) {
			return (UserFragment) userFragment;
		}
		return null;
	}

	private MessageFragment getMessageFragment() {
		Fragment messageFragment = getSupportFragmentManager().findFragmentByTag(TAB_MESSAGE);
		if (messageFragment != null && messageFragment instanceof MessageFragment) {
			return (MessageFragment) messageFragment;
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()");
		UserFragment userFragment = getUserFragment();
		if (userFragment != null) {
			Log.d(TAG, "onDestroy() on calling UserFragment.onDestroy()");
			userFragment.onDestroy();
		}
		if (mPushReceiver != null) {
			Log.d(TAG, "onDestroy() unregisterReceiver - UserReceiver");
			unregisterReceiver(mPushReceiver);
			mPushReceiver = null;
		}
		super.onDestroy();
		stopLocation();
	}

	public class MyLocationListener implements BDLocationListener {
		private final String TAG = MyLocationListener.class.getSimpleName();

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nlocation type : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			mProvince = location.getProvince();
			mCity = location.getCity();
			if (!TextUtils.isEmpty(mProvince) && !TextUtils.isEmpty(mCity)) {
				getCityId();
				mSpUtil.setProvince(mProvince);
				mSpUtil.setCity(mCity);
				mSpUtil.setArea(location.getDistrict());
			}
			mSpUtil.setLat(location.getLatitude());
			mSpUtil.setLng(location.getLongitude());

			Log.d(TAG, sb.toString());
		}
	}

	public void getCityId() {
		if (mProvinceList != null && mProvinceList.size() > 0 && !TextUtils.isEmpty(mProvince)) {
			for (CityEntity province : mProvinceList) {
				if (province != null && mProvince.equals(province.name)) {
					int provinceId = mSpUtil.getProvinceId(0);
					int cityId = mSpUtil.getCityId(0);
					if (provinceId != province.cityId || cityId == 0) {
						mSpUtil.setProvinceId(province.cityId);
						mSpUtil.setCityId(0);
						onLoadCity(province.cityId);
					}
					stopLocation();
				}
			}
		}
	}

	private void stopLocation() {
		mHander.removeMessages(DelayHandler.DELAY_STOP_LOCATION);
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
	}

	private void onLoadCity() {
		CityDb db = new CityDb(getActivity());

		mProvinceList = db.findAll();
		if (mProvinceList != null && mProvinceList.size() > 0) {
			getCityId();
		} else {
			requestProvince();
		}
	}

	private void requestProvince() {
		// Tag used to cancel the request
		String tag = "tag_request_city";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				mProvinceList = response.getListResponse(new TypeToken<List<CityEntity>>() {
				});
				if (mProvinceList != null && mProvinceList.size() > 0) {
					CityDb db = new CityDb(getActivity());
					db.saveAll(mProvinceList);

					getCityId();
				}
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.CityController.getCity");
				params.put("parentId", "0");
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag, true);
	}

	public void onLoadCity(final int provinceId) {
		// Tag used to cancel the request
		String tag = "tag_request_city";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				List<CityEntity> mCityList = response.getListResponse(new TypeToken<List<CityEntity>>() {
				});
				if (mCityList != null && mCityList.size() > 0) {
					if (!TextUtils.isEmpty(mCity)) {
						for (CityEntity city : mCityList) {
							if (city != null && mCity.equals(city.name)) {
								int cityId = mSpUtil.getCityId(0);
								if (cityId != city.cityId) {
									mSpUtil.setCityId(city.cityId);
									onLoadCityRegionInfo(city.cityId);
								}
							}
						}
					}

					CityDb db = new CityDb(getActivity());
					db.saveCityInfo(provinceId, mCityList);
				}
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.CityController.getCity");
				params.put("parentId", Integer.toString(provinceId));
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag, true);
	}

	public void onLoadCityRegionInfo(final int cityId) {
		// Tag used to cancel the request
		String tag = "tag_request_city_region";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				List<CityEntity> mCityList = response.getListResponse(new TypeToken<List<CityEntity>>() {
				});
				if (mCityList != null && mCityList.size() > 0) {
					CityDb db = new CityDb(getActivity());
					db.saveCityRegionInfo(cityId, mCityList);
				}
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.CityController.getCity");
				params.put("parentId", Integer.toString(cityId));
				params.put("token", mSpUtil.getToken());
				return params;
			}
		}, tag, true);
	}

	private class DelayHandler extends Handler {
		public static final int DELAY_STOP_LOCATION = 10001;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DELAY_STOP_LOCATION:
				stopLocation();
				break;
			default:
				break;
			}

		}
	}
	
	private long firstClickTime = 0;

	@Override
	public void onBackPressed() {
		long secondTime = System.currentTimeMillis();
		if (secondTime - firstClickTime > 2000) {
			showToast("再按一次退出程序...");
			firstClickTime = secondTime;
			return;
		} else {
			finish();
		}
	}
	
	private class PushReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (Constants.ACTION_PUSH.equals(action)) {
					Push push = (Push) intent.getSerializableExtra("push");
					if (push != null) {
						if ("03".equals(push.msgType)) {
							int count = mSpUtil.getNewMsgCount();
							count += push.cnt;
							mSpUtil.setNewMsgCount(count);
						} else if ("05".equals(push.msgType)) {
							int count = mSpUtil.getNewCommentCount();
							count += push.cnt;
							mSpUtil.setNewCommentCount(count);
						} else if ("07".equals(push.msgType)) {
							int count = mSpUtil.getNewConfirmOrderCount();
							count += push.cnt;
							mSpUtil.setNewConfirmOrderCount(count);
						} else if ("10".equals(push.msgType)) {
							int count = mSpUtil.getNewRefundResultCount();
							count += push.cnt;
							mSpUtil.setNewRefundResultCount(count);
						}
						
						onRefreshNewMsgCount();
					}
				}
			}
		}
	}
	
	public interface OnTabDoubleClickListener {
		void onTabDoubleClick();
	}

	@Override
	public void onTabChanged(String tab) {
		tabChanged = false;
		if (tab != null) {
			if (tab.equals(TAB_MESSAGE)) {
				tabChanged = true;
			}
		}
	}

}
