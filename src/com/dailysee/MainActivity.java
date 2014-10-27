package com.dailysee;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;

import com.dailysee.ui.CallMeFragment;
import com.dailysee.ui.HomeFragment;
import com.dailysee.ui.MessageFragment;
import com.dailysee.ui.UserFragment;

public class MainActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;

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
		
//		UmengUpdateAgent.update(this);
//		MobclickAgent.updateOnlineConfig(this);
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

}
