package com.dailysee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dailysee.bean.CityEntity;
import com.dailysee.bean.Member;
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
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author Administrator
 * 
 */
public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private FragmentTabHost mTabHost;

	private boolean mLoadDataRequired = true;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private DelayHandler mHander;

	protected List<CityEntity> mProvinceList;

	public String mProvince;
	public String mCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initPush();
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);
	}

	private void initPush() {
		Resources resource = this.getResources();
        String pkgName = this.getPackageName();		
        
        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")！！ 请将AndroidManifest.xml 104行处 api_key 字段值修改为自己的 api_key 方可使用 ！！！！ ATTENTION：You need to modify the value of api_key to your own at row 104 in AndroidManifest.xml to use this Demo !!
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // PushManager.enableLbs(getApplicationContext());

        // Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                getApplicationContext(), resource.getIdentifier("notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);
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
		addTab("home", "首页", HomeFragment.class, R.drawable.item_tab_1_selector);
		addTab("message", "天天讯息", MessageFragment.class, R.drawable.item_tab_2_selector);
		addTab("callme", "呼叫天天", CallMeFragment.class, R.drawable.item_tab_3_selector);
		addTab("user", "个人中心", UserFragment.class, R.drawable.item_tab_4_selector);
	}

	@Override
	public void onBindListener() {

	}

	private void addTab(String key, String title, Class fragment, int resId) {
		Bundle b = new Bundle();
		b.putString(key, title);
		mTabHost.addTab(setIndicator(mTabHost.newTabSpec(key), resId), fragment, b);
	}

	public TabSpec setIndicator(TabSpec spec, int resId) {
		View v = LayoutInflater.from(this).inflate(R.layout.item_tab, null);

		ImageView ivTab = (ImageView) v.findViewById(R.id.iv_tab);
		ivTab.setImageResource(resId);

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
		Bundle b = new Bundle();
		b.putString("user", "个人中心");
		Fragment userFragment = getSupportFragmentManager().findFragmentByTag("user");
		if (userFragment != null && userFragment instanceof UserFragment) {
			return (UserFragment) userFragment;
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

}
