package com.dailysee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 3000; // �ӳ�����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
	}

}
