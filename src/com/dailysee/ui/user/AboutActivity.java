package com.dailysee.ui.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.ui.base.BaseActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.Utils;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class AboutActivity extends BaseActivity implements OnClickListener{

    private LinearLayout llFeedback;
    private LinearLayout llCheckUpdate;
	private LinearLayout llCallUs;
	
	private TextView tvCheckUpdate;
    private TextView tvCallUs;
	private FeedbackAgent mFeedbackAgent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}
	
	@Override
	public void onInit() {
		setTitle("关于");
		setUp();
	}

	@Override
	public void onFindViews() {
		llFeedback = (LinearLayout) findViewById(R.id.ll_feed_back);
		llCheckUpdate = (LinearLayout) findViewById(R.id.ll_check_update);
		llCallUs = (LinearLayout) findViewById(R.id.ll_call_us);
		
		tvCheckUpdate = (TextView) findViewById(R.id.tv_check_update);
		tvCallUs = (TextView) findViewById(R.id.tv_call_us);
	}

	@Override
	public void onInitViewData() {
		String ver = Utils.getCurrentAppVersionName(this);
		String version = getResources().getString(R.string.app_version, ver);
		tvCheckUpdate.setText(version);
		
		tvCallUs.setText(Constants.CUSTOMER_SERVICES_PHONE);

        mFeedbackAgent = new FeedbackAgent(this);
        mFeedbackAgent.sync();
	}

	@Override
	public void onBindListener() {
	    llFeedback.setOnClickListener(this);
	    llCheckUpdate.setOnClickListener(this);
	    llCallUs.setOnClickListener(this);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_feed_back: // 帮助与反馈
            	mFeedbackAgent.startFeedbackActivity();
                break;
            case R.id.ll_check_update:
                checkUpdateVersion();
                break;
            case R.id.ll_call_us:
                break;
        }
    }

    private void checkUpdateVersion() {
        // 检查更新
    	UmengUpdateAgent.forceUpdate(this);
    }
    
}
