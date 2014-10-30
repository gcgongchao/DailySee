package com.dailysee;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dailysee.ui.CallMeFragment;
import com.dailysee.ui.HomeFragment;
import com.dailysee.ui.MessageFragment;
import com.dailysee.ui.UserFragment;
import com.dailysee.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author Administrator
 * 
 */
public class MainActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		addTab("home", "首页", HomeFragment.class);
		addTab("message", "天天讯息", MessageFragment.class);
		addTab("user", "个人中心", UserFragment.class);
		addTab("callme", "呼叫天天", CallMeFragment.class);

		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK4", "locClient is null or not started");

		Utils.logStringCache = Utils.getLogText(getApplicationContext());

		Resources resource = this.getResources();
		String pkgName = this.getPackageName();

		PushSettings.enableDebugMode(this, true);
		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(getApplicationContext());

		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(getApplicationContext(),
				resource.getIdentifier("notification_custom_builder", "layout", pkgName), resource.getIdentifier("notification_icon", "id", pkgName), resource.getIdentifier(
						"notification_title", "id", pkgName), resource.getIdentifier("notification_text", "id", pkgName));
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(this, 1, cBuilder);
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	private void addTab(String key, String title, Class fragment) {
		Bundle b = new Bundle();
		b.putString("key", "callme");
		mTabHost.addTab(mTabHost.newTabSpec(key).setIndicator(title), fragment, b);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// public TabSpec setIndicator(TabSpec spec, int resid) {
	// View v = LayoutInflater.from(this).inflate(R.layout.tabs_text, null);
	// v.setBackgroundResource(resid);
	// TextView text = (TextView) v.findViewById(R.id.tab_title);
	//
	// text.setText(spec.getTag());
	// return spec.setIndicator(v);
	// }

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

			Log.d(TAG, sb.toString());
			// Toast.makeText(this, sb, )
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
	}

}
