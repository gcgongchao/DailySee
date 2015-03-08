package com.dailysee.ui.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.os.Bundle;
import android.widget.TextView;

import com.dailysee.R;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.base.BaseActivity;

public class ServiceAimActivity extends BaseActivity {

	protected static final String TAG = ServiceAimActivity.class.getSimpleName();
	private TextView tvServiceAim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_aim);
		
//		requestServiceAim();
	}

	@Override
	public void onInit() {
		setTitle("服务宗旨");
		setUp();
	}

	@Override
	public void onFindViews() {
		tvServiceAim = (TextView) findViewById(R.id.tv_content);
		tvServiceAim.setText(mSpUtil.getPurpose());
	}

	@Override
	public void onInitViewData() {
	}

	@Override
	public void onBindListener() {
	}
	
	private void requestServiceAim() {
		// Tag used to cancel the request
		String tag = "tag_request_service_aim";
		NetRequest.getInstance(this).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				try {
					String serviceAim = response.getData().getString("serviceAim");
					tvServiceAim.setText("serviceAim");
				} catch (JSONException e) {
					e.printStackTrace();
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
				return params;
			}
		}, tag);
	}

}
