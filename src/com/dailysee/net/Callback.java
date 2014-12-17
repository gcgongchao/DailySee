package com.dailysee.net;

import java.util.Map;

public abstract class Callback {
	public void onPreExecute() {

	}

	public Map<String, String> getParams() {
		return null;
	}

	public void onSuccess(BaseResponse data) {

	}

	public void onFinished() {

	}

	public void onFailed(String msg) {

	}
}