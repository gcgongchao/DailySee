package com.dailysee.service;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dailysee.MainActivity;
import com.dailysee.R;
import com.dailysee.bean.Push;
import com.dailysee.net.BaseResponse;
import com.dailysee.net.Callback;
import com.dailysee.net.NetRequest;
import com.dailysee.ui.order.OrderActivity;
import com.dailysee.ui.sale.SaleActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 0 - Success 10001 - Network Problem 30600 - Internal
 * Server Error 30601 - Method Not Allowed 30602 - Request Params Not Valid
 * 30603 - Authentication Failed 30604 - Quota Use Up Payment Required 30605 -
 * Data Required Not Found 30606 - Request Time Expires Timeout 30607 - Channel
 * Token Timeout 30608 - Bind Relation Not Found 30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class MyPushMessageReceiver extends BroadcastReceiver {
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("PushService", "onReceive() action=" + bundle.getInt("action") + ", bundle: " + bundle.toString());
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
			
			if (payload != null) {
				String data = new String(payload);

				updateContent(context, data);
				Log.d("GetuiSdkDemo", "Got Payload:" + data);
			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			Utils.setClientId(context, cid);
			requestBindPush(context, cid);
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "taskid = " + taskid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "result = " + result);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);*/
			break;
		default:
			break;
		}
	}

	private void requestBindPush(final Context context, final String clientId) {
		if (!SpUtil.getInstance(context).isLogin()) {
			return;
		}

		// Tag used to cancel the request
		String tag = "tag_request_bind_user";
		NetRequest.getInstance(context).post(new Callback() {

			@Override
			public void onSuccess(BaseResponse response) {
				Utils.setBind(context, true);
			}

			@Override
			public Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mtd", "tty.member.bind.user");
				params.put("memberId", SpUtil.getInstance(context).getMemberIdStr());
				params.put("userId", clientId);
				params.put("channelId", "0");
				params.put("token", SpUtil.getInstance(context).getToken());
				return params;
			}
		}, tag, true);
	}

	private void updateContent(Context context, String msg) {
		Log.d(TAG, "updateContent");
		
		if (TextUtils.isEmpty(msg) || !msg.startsWith("{")) {
			return ;
		}
		
		Push push = null;
		try {
			push = new Gson().fromJson(msg, Push.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		if (push != null) {
			Intent intent = new Intent();
			String content = null;
			if ("01".equals(push.msgType)) {
				content = "天天有发布了一条新优惠，赶紧看看吧！";
				intent.setClass(context, SaleActivity.class);
			} else if ("02".equals(push.msgType)) {
				content = "天天有发布了一条新活动，赶紧看看吧！";
				intent.setClass(context, SaleActivity.class);
			} else if ("03".equals(push.msgType)) {
				content = "天天有发布了一条新讯息，赶紧看看吧！";
				intent.setClass(context, MainActivity.class);
				intent.putExtra("tab", MainActivity.TAB_MESSAGE);
			} else if ("04".equals(push.msgType)) {
				content = "您的预约订单快到时间了，赶快去消费吧！";
				intent.setClass(context, OrderActivity.class);
			} else if ("05".equals(push.msgType)) {
				content = "您的订单已完成，赶快去评价吧！";
				intent.setClass(context, OrderActivity.class);
//			} else if ("06".equals(push.msgType)) {
//				content = "您的订单已接单";
			} else if ("07".equals(push.msgType)) {
				content = "您的订单已接单";
				intent.setClass(context, OrderActivity.class);
			} else {
				content = push.msgType;
			}
	//		String currentPackageName = AppUtil.getTopAppPackage(context);
	//		if (currentPackageName.equals(context.getPackageName())) {
	//			return;
	//		}
			
			if (push.cnt > 0) {
				Intent pushIntent = new Intent(Constants.ACTION_PUSH);
				pushIntent.putExtra("push", push);
				context.sendBroadcast(pushIntent);
			}
			
			Notification notification = new Notification();
            notification.icon = R.drawable.ic_launcher;
            notification.defaults = Notification.DEFAULT_LIGHTS;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = content;
			
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationManager.cancel(0);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), content, pendingIntent);
			notificationManager.notify(0, notification);
		}
	}

}
