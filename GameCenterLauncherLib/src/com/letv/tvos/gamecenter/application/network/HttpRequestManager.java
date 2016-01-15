package com.letv.tvos.gamecenter.application.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.letv.tvos.gamecenter.ChannelManager;
import com.letv.tvos.gamecenter.util.AppUtil;
import com.letv.tvos.gamecenter.util.DeviceUtil;
import com.letv.tvos.gamecenter.util.StringUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;




public class HttpRequestManager {
	public static String macString;
	public static String sessionIdString;
	public static String channelNoString;
	public static String deviceChannelNameString;
	public static String deviceExactNameString ;
	public static String appVersionString ;
	public static String deviceInfoString;
	public static String osVersionString;
	public static String timeStampString;
	public static String dataInfoString;
	public static String imeiString;
	private static HttpRequestManager httpRequestManager = null;
	
	private ExecutorService executorService;
	
	public void init(Context context) {
		Context applicationContext = context.getApplicationContext();
		executorService = (ExecutorService) Executors.newFixedThreadPool(6); 
		macString = DeviceUtil.getMacAddress(applicationContext);
		imeiString = DeviceUtil.getIMEI(applicationContext);
		sessionIdString = null;
		channelNoString = ChannelManager.getCpid(applicationContext);
		deviceChannelNameString = ChannelManager.getDeviceChannelNameWithLetvManager(applicationContext);
		deviceExactNameString = "default";
		appVersionString = ""+AppUtil.getVersionCode(applicationContext);
		deviceInfoString = DeviceUtil.getDeviceName();
		osVersionString = DeviceUtil.getOSVersion();
		timeStampString = ""+System.currentTimeMillis();
		dataInfoString = null;
	}
	
	public void start(IRequest<?> request,OnNetworkCompleteListener<?> onNetworkCompleteListener) {
		request.setOnNetworkCompleteListener(onNetworkCompleteListener);
		HttpRequestAsyncTask httpRequestAsyncTask = new HttpRequestAsyncTask();
		httpRequestAsyncTask.executeOnExecutor(executorService, request);
	}
	
	public static synchronized HttpRequestManager getInstance(Context context) {
		if (null == httpRequestManager) {
			httpRequestManager = new HttpRequestManager(context);
		}
	
		return httpRequestManager;
	}
	
	private HttpRequestManager(Context context) {
		init(context);
	}

}
