package com.dailysee.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.dailysee.MainActivity;
import com.dailysee.R;
import com.dailysee.bean.Push;
import com.dailysee.ui.order.OrderActivity;
import com.dailysee.ui.sale.SaleActivity;
import com.dailysee.util.Constants;
import com.dailysee.util.SpUtil;
import com.dailysee.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();

	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId + " channelId=" + channelId + " requestId=" + requestId;
		Log.d(TAG, responseString);

		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		if (errorCode == 0) {
			SpUtil.getInstance(context).setBDUserId(userId);
			SpUtil.getInstance(context).setBDChannelId(channelId);
			Utils.setBind(context, userId, channelId, true);
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
		Log.d(TAG, messageString);

		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, message);
	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + title + "\" description=\"" + description + "\" customContent=" + customContentString;
		Log.d(TAG, notifyString);

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, notifyString);
	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;
		Log.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			Utils.setBind(context, null, null, false);
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
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
