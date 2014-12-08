package com.dailysee;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.content.Intent;
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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author Administrator
 * 
 */
public class MainActivity extends BaseActivity {

	private FragmentTabHost mTabHost;
	
	private boolean mLoadDataRequired = true;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private DelayHandler mHander;

	protected List<CityEntity> mCityList;

	public String mProvince;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);
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
//		mTabHost.getTabWidget().setDividerDrawable(R.color.white);
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
			return ;
		}
		
		// Tag used to cancel the request
		String tag = "tag_request_get_member_detail";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Member member = (Member) response.getResponse(new TypeToken<Member>() {});
				if (member != null) {
					mLoadDataRequired = false;
					mSpUtil.setMember(member);
					
					Intent intent = new Intent(Constants.REFRESH_MEMBER_DETAIL);
					sendBroadcast(intent);
				}
			}

			@Override
			public void onPreExecute() {
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
		UserFragment userFragment = getUserFragment();
		if (userFragment != null) {
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
			String city = location.getCity();
			if (!TextUtils.isEmpty(mProvince) && !TextUtils.isEmpty(city)) {
				mSpUtil.setProvince(mProvince);
				mSpUtil.setCity(city);
				mSpUtil.setArea(location.getDistrict());
			}
			mSpUtil.setLat(location.getLatitude());
			mSpUtil.setLng(location.getLongitude());

			Log.d(TAG, sb.toString());
		}
	}

	public void getCityId() {
		if (mCityList != null && mCityList.size() > 0 && !TextUtils.isEmpty(mProvince)) {
			for (CityEntity city : mCityList) {
				if (city != null && mProvince.equals(city.name)) {
					mSpUtil.setCityId(city.cityId);
					onLoadCityData(city.cityId);
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
		
		mCityList = db.findAll();
		if (mCityList != null && mCityList.size() > 0) {
			getCityId();
		} else {
			requestCity(0);
		}
	}

	private void requestCity(final int parentId) {
		// Tag used to cancel the request
		String tag = "tag_request_city";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				if (parentId == 0) {
					mCityList = response.getListResponse(new TypeToken<List<CityEntity>>() {});
					if (mCityList != null && mCityList.size() > 0) {
						CityDb db = new CityDb(getActivity());
						db.saveAll(mCityList);
						
						getCityId();
					}
				}
			}

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onFailed(String msg) {

			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.CityController.getCity");
				params.put("parentId", Integer.toString(parentId));
				return params;
			}
		}, tag);
	}
	
	public void onLoadCityData(final int cityId) {
		// Tag used to cancel the request
		String tag = "tag_request_city";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				List<CityEntity> mAreaList = response.getListResponse(new TypeToken<List<CityEntity>>() {});
				if (mAreaList != null && mAreaList.size() > 0) {
					CityDb db = new CityDb(getActivity());
					db.saveCityRegionInfo(cityId, mAreaList);
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
				params.put("mtd", "com.guocui.tty.api.web.CityController.getCity");
				params.put("parentId", Integer.toString(cityId));
				return params;
			}
		}, tag);
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

}
