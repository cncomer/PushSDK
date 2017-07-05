package com.cncom.lib.umeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.onlineconfig.OnlineConfigLog;
import com.umeng.onlineconfig.UmengOnlineConfigureListener;

import org.json.JSONObject;


public class YouMengMessageHelper {
	private static final String TAG = "YouMengMessageHelper";
	public static YouMengMessageHelper INSTANCE = new YouMengMessageHelper();
	private Context mContext;
	private SharedPreferences mPreferManager;
	private PushAgent mPushAgent;

	private boolean debug = false;

	/**保修*/
	private YouMengMessageHelper() {}
	public synchronized void setContext(Context context) {
		mContext = context;
		mPreferManager = PreferenceManager.getDefaultSharedPreferences(mContext);
		mPushAgent = PushAgent.getInstance(mContext);

		setDebug((mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
		mPushAgent.setDebugMode(isInDebug());
		mPushAgent.setPushCheck(isInDebug());

		registerYmengMessageHandler();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void logD(String tag, String message) {
		if (debug && !TextUtils.isEmpty(message)) {
			Log.d(TAG+":" + tag, message);
		}
	}

	public void logE(String tag, String message) {
		if (!TextUtils.isEmpty(message)) {
			Log.d(TAG+":" + tag, message);
		}
	}

	public boolean isInDebug() {
		return this.debug;
	}

	/**
	 * 当使用android studio开发环境时，是基于gradle的配置方式，资源文件的包和应用程序的包是可以分开的，为了正确的找到资源包名，
	 * 为开发者提供了自定义的设置资源包的接口。 当资源包名和应用程序包名不一致时，调用设置资源包名的接口：
	 * @param resourcePackageName
	 */
	public void setResourcePackageName(String resourcePackageName) {
		mPushAgent.setResourcePackageName(resourcePackageName);
	}
	
	public static YouMengMessageHelper getInstance() {
		return INSTANCE;
	}

	public PushAgent getPushAgent() {
		return mPushAgent;
	}
	
	/**
	 * 启动推送功能,一旦设备token注册成功，会回调mRegisterCallback
	 */
	public synchronized void startPushAgent() {
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String deviceToken) {
				//注册成功会返回device token
				logD("IUmengRegisterCallback", "onRegistered() get " + deviceToken);
//				enablePushAgent();
			}

			@Override
			public void onFailure(String s, String s1) {
				logE("IUmengRegisterCallback", "onFailure() s=" + s + ", s1=" + s1);
			}
		});


//		if (!TextUtils.isEmpty(mPushAgent.getRegistrationId())) {
//			enablePushAgent();
//		}
//		enablePushAgent();

	}

	/**
	 * 注销回调：IUmengCallback；当关闭友盟推送时，可调用以下代码(请在Activity内调用)
	 */
//	public void disablePushAgent() {
//		mPushAgent.disable(new IUmengCallback() {
//			@Override
//			public void onSuccess() {
//				logD("disablePushAgent", "onSuccess()");
//			}
//
//			@Override
//			public void onFailure(String s, String s1) {
//				logD("disablePushAgent", "onFailure() " + s + " " + s1);
//			}
//		});
//	}

    /**
     * 若调用关闭推送后，想要再次开启推送，则需要调用以下代码(请在Activity内调用)
     */
//	public void enablePushAgent() {
//		mPushAgent.enable(new IUmengCallback() {
//			@Override
//			public void onSuccess() {
//				logD("enablePushAgent", "onSuccess()");
//			}
//
//			@Override
//			public void onFailure(String s, String s1) {
//				logE("enablePushAgent", "onFailure() " + s + " " + s1);
//			}
//		});
//	}

	/**
	 * MobclickAgent.openActivityDurationTrack(false) 禁止默认的页面统计方式，这样将不会再自动统计Activity。
	 然后需要做两步集成：
	 1. 使用 onResume 和 onPause 方法统计时长, 这和基本统计中的情况一样(针对Activity)
	 2. 使用 onPageStart 和 onPageEnd 方法统计页面(针对页面,页面可能是Activity 也可能是Fragment或View)
	 * @param openActivityDurationTrack 禁止默认的页面统计方式，这样将不会再自动统计Activity
	 */
	public synchronized void startMobclickAgent(boolean openActivityDurationTrack) {
		MobclickAgent.setDebugMode(isInDebug());
		MobclickAgent.openActivityDurationTrack(openActivityDurationTrack);
		MobclickAgent.enableEncrypt(!isInDebug());
		updateOnlineConfig(mContext);
	}

	/**
	 * 统计应用启动数据
	 * 在所有的Activity 的onCreate 函数添加.
	 *
	 * 注意:
	 * 此方法与统计分析sdk中统计日活的方法无关！请务必调用此方法！
	 * 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
	 */
	public void onAppStart() {
		mPushAgent.onAppStart();
	}

	
	 public synchronized String getDeviceTotke() {
		 return mPushAgent.getRegistrationId();
	    }
	public synchronized String getDeviceToken() {
		return mPushAgent.getRegistrationId();
	}

	    public synchronized boolean getDeviceTotkeStatus() {
	    	return mPreferManager.getBoolean("device_token_status", false);
	    }

	public synchronized boolean saveDeviceTokenStatus(boolean deviceTokenHasRegistered) {
		boolean ok =  mPreferManager.edit().putBoolean("device_token_status", deviceTokenHasRegistered).commit();
		logD("saveDeviceTokenStatus", deviceTokenHasRegistered + ", saved " + ok);
		return ok;
	}
	
	public synchronized void registerYmengMessageHandler() {
		mPushAgent.setMessageHandler(new BaseUmengMessageHandler());
		mPushAgent.setNotificationClickHandler(new BaseUmengNotificationClickHandler());
	}

	public void setUmengNotificationClickHandler(UmengNotificationClickHandler umengNotificationClickHandler) {
		mPushAgent.setNotificationClickHandler(umengNotificationClickHandler);
	}

	public void setUmengMessageHandler(UmengMessageHandler umengMessageHandler) {
		mPushAgent.setMessageHandler(umengMessageHandler);
	}


	/**
	 * 当用户使用自有账号登录时，可以这样统计
	 * @param userID
	 */
	public void onProfileSignIn(String userID) {
		MobclickAgent.onProfileSignIn(userID);
	}

	/**
	 * 账号登出时需调用此接口，调用之后不再发送账号相关内容。
	 */
	public void onProfileSignOff() {
		MobclickAgent.onProfileSignOff();
	}

	/**
	 * 发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率。 您需要在程序的入口 Activity 中添加。
	 * 在没有取到在线配置的发送策略的情况下，会使用默认的发送策略：启动时发送。
	 * @param context
	 */
	public void updateOnlineConfig(Context context) {
		OnlineConfigAgent.getInstance().setDebugMode(isInDebug());
		OnlineConfigAgent.getInstance().updateOnlineConfig(context);
	}

	public String getOnlineConfigParams(String key) {
		String value = OnlineConfigAgent.getInstance().getConfigParams(mContext, key);
		logD("getOnlineConfigParams", "key=" + key + ", value=" + value);
		return value;
	}


	/**
	 * 设置在线参数的监听器
	 * @param listener
	 */
	public void setUmengOnlineConfigureListener(UmengOnlineConfigureListener listener) {
		if (listener == null) {
			listener = configureListener;
		}
		OnlineConfigAgent.getInstance().setOnlineConfigListener(configureListener);
	}
	/**
	 * 移除在线参数的监听器
	 */
	public void removeOnlineConfigListener() {
		OnlineConfigAgent.getInstance().removeOnlineConfigListener();
	}
	private UmengOnlineConfigureListener configureListener = new UmengOnlineConfigureListener() {
		@Override
		public void onDataReceived(JSONObject json) {
			// TODO Auto-generated method stub
			OnlineConfigLog.d("OnlineConfig", "json=" + json);
		}
	};

}
