package com.dailysee;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
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
import com.dailysee.bean.Member;
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

	private FragmentTabHost mTabHost;
	
	private boolean mLoadDataRequired = true;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private DelayHandler mHander;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);

		Utils.logStringCache = Utils.getLogText(getApplicationContext());

//		Resources resource = this.getResources();
//		String pkgName = this.getPackageName();
//
//		PushSettings.enableDebugMode(this, true);
//		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
//		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
//		// PushManager.enableLbs(getApplicationContext());
//
//		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
//		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
//		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
//		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(getApplicationContext(),
//				resource.getIdentifier("notification_custom_builder", "layout", pkgName), resource.getIdentifier("notification_icon", "id", pkgName), resource.getIdentifier(
//						"notification_title", "id", pkgName), resource.getIdentifier("notification_text", "id", pkgName));
//		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//		cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//		cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
//		PushManager.setNotificationBuilder(this, 1, cBuilder);
	}

	@Override
	public void onInit() {
		mHander = new DelayHandler();
		
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
		
		final long belongObjId = mSpUtil.getBelongObjId();

		// Tag used to cancel the request
		String tag = "tag_request_get_member_detail";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Member member = (Member) response.getResponse(new TypeToken<Member>() {});
				if (member != null) {
//					SpUtil sp = SpUtil.getInstance(MainActivity.this);
//					sp.setMember(member);
					
					Intent intent = new Intent(Constants.REFRESH_MEMBER_DETAIL);
					sendBroadcast(intent);
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
				toCloseProgressMsg();
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "com.guocui.tty.api.web.MemberControllor.getMemberDetail");
				params.put("belongObjId", Long.toString(belongObjId));
				return params;
			}
		}, tag);
	}
	
	@Override 
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data); 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
	}

	public class MyLocationListener implements BDLocationListener {
		private final String TAG = MyLocationListener.class.getSimpleName();

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if (mLocationClient != null) {
				mLocationClient.stop();
			}
			mHander.removeMessages(DelayHandler.DELAY_STOP_LOCATION);

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

			Log.d(TAG, sb.toString());
		}
	}
	
	private class DelayHandler extends Handler {
		public static final int DELAY_AUTO_REFRESH = 10001;
		public static final int DELAY_STOP_LOCATION = 10002;
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DELAY_STOP_LOCATION:
				if (mLocationClient != null) {
					mLocationClient.stop();
				}
				break;
			default:
				break;
			}
			
		}
	}

}
