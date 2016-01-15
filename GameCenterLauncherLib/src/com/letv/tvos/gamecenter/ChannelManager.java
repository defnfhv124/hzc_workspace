package com.letv.tvos.gamecenter;

import com.android.letvmanager.LetvManager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ChannelManager {

	private static String CHANNELMANAGER_CPID = "tvos_cpid"; // 渠道
	private static String CHANNELMANAGER_MODEL = "tvos_devicename"; // 机型
	private static String CHANNELMANAGER_MODEL_EXACT = "tvos_devicename_exact"; // 机型

	/**
	 * 渠道号
	 */
	public static String CHANNEL_LETV = "20"; // 乐视自有渠道号
	public static String CHANNEL_LETV_CNTV = "40"; // CNTV送检

	/**
	 * 设备渠道名
	 */
	public static String DEVICE_CHANNEL_X60 = "x60"; // S60
	public static String DEVICE_CHANNEL_S50 = "S50"; // S50
	public static String DEVICE_CHANNEL_XX60 = "XX60"; // CNTV送检机型
	public static String DEVICE_CHANNEL_S40UI15 = "S40UI15"; // S40 UI1.5渠道
	public static String DEVICE_CHANNEL_S40UI20 = "S40UI20"; // S40 UI2.0渠道
	public static String DEVICE_CHANNEL_S40 = "S40"; // S40 UI2.0渠道
	public static String DEVICE_CHANNEL_S40Air = "S40AIR";// S40Air
	public static String DEVICE_CHANNEL_CC1 = "CC1"; // C1/C1s UI2.0渠道
	public static String DEVICE_CHANNEL_CC1A = "CC1A"; // C1/C1s UI1.5渠道
	public static String DEVICE_CHANNEL_CC1B = "CC1B"; // C1/C1s UI1.5运营商渠道
	public static String DEVICE_CHANNEL_S50Air = "S50AIR"; // S2_50
	public static String DEVICE_CHANNEL_X50Air = "X50AIR"; // S2_50
	public static String DEVICE_CHANNEL_MAX_70 = "Max70"; // Max 70
	public static String DEVICE_CHANNEL_C2 = "C2"; // C2
	public static String DEVICE_CHANNEL_C1 = "C1"; // C2
	public static String DEVICE_CHANNEL_NEWC1S = "NewC1S"; // NewC1S
	public static String DEVICE_CHANNEL_G1 = "G1"; // G1
	public static String DEVICE_CHANNEL_X55Air = "X55AIR";

	/**
	 * 设备渠道名,LetvManager定义的渠道名称
	 */
	public static String LETV_MANAGER_CHANNEL_X60 = "X60";
	public static String LETV_MANAGER_CHANNEL_S50 = "S50";
	public static String LETV_MANAGER_CHANNEL_S40 = "S40";
	public static String LETV_MANAGER_CHANNEL_C1 = "C1";
	public static String LETV_MANAGER_CHANNEL_C1S = "C1S";
	public static String LETV_MANAGER_CHANNEL_C1A = "C1A";
	public static String LETV_MANAGER_CHANNEL_C1B = "C1B";
	public static String LETV_MANAGER_CHANNEL_T1S = "T1S";
	public static String LETV_MANAGER_CHANNEL_NEWC1S = "NewC1S";
	public static String LETV_MANAGER_CHANNEL_S50Air = "S250F";
	public static String LETV_MANAGER_CHANNEL_X50Air = "S250U";
	public static String LETV_MANAGER_CHANNEL_S40Air = "S240F";
	public static String LETV_MANAGER_CHANNEL_MAX_70 = "MAX70";
	public static String LETV_MANAGER_CHANNEL_C2 = "C2";
	public static String LETV_MANAGER_CHANNEL_X55Air = "S255U";

	/**
	 * 设备真实名称
	 */
	public static String DEVICE_EXACT_X60 = "x60"; // x60真实设备名称
	public static String DEVICE_EXACT_S50 = "S50"; // S50真实设备名称
	public static String DEVICE_EXACT_S40UI15 = "s40"; // S40真实设备名称
	public static String DEVICE_EXACT_CC1 = "c1"; // C1/C1s真实设备名称

	private static String CPID = null;
	private static String DEVICE_CHANNEL_NAME = null;
	private static String DEVICE_EXACT_NAME = null;

	/**
	 * 是否是同样的设备
	 * 
	 * @param context
	 * @param deviceExactNameString
	 * @return
	 */
	public static boolean isSameDevice(Context context, String deviceExactNameString) {

		boolean isSameDevice = false;

		if (null != deviceExactNameString && !"".equalsIgnoreCase(deviceExactNameString)) {
			isSameDevice = deviceExactNameString.equalsIgnoreCase(getDeviceExactName(context));
		}
		return isSameDevice;
	}

	/**
	 * 是否是同样的设备渠道名
	 * 
	 * @param context
	 * @param deviceChannelNameString
	 * @return
	 */
	public static boolean isSameDeviceChannelName(Context context, String deviceChannelNameString) {

		boolean isSameDeviceChannel = false;

		if (null != deviceChannelNameString && !"".equalsIgnoreCase(deviceChannelNameString)) {
			isSameDeviceChannel = deviceChannelNameString.equalsIgnoreCase(getDeviceChannelNameWithLetvManager(context));
		}

		return isSameDeviceChannel;
	}

	/***
	 * 是否是乐视渠道
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isLetvChannel(Context context) {

		boolean isLetvChannel = false;

		if (CHANNEL_LETV.equalsIgnoreCase(getCpid(context))) {
			isLetvChannel = true;
		}

		return isLetvChannel;

	}

	/***
	 * 获取ApplicationInfo
	 * 
	 * @param context
	 * @return
	 */
	private static ApplicationInfo getApplicationInfo(Context context) {

		ApplicationInfo applicationInfo = null;
		try {
			applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return applicationInfo;
	}

	/***
	 * 获取渠道号
	 * 
	 * @param context
	 * @return
	 */
	public static String getCpid(Context context) {

		return "20";
	}

	/***
	 * 获取包名,渠道号,设备渠道名 的拼接字符串, 示例:com.letv.tvos.appstore.20.x60
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageNameAndCpidAndDeviceChannel(Context context) {

		StringBuilder builder = new StringBuilder(context.getPackageName());
		builder.append(".");
		builder.append(getCpid(context));
		builder.append(".");
		builder.append(getDeviceChannelNameWithLetvManager(context));

		return builder.toString();
	}

	// /**
	// * 获取设备渠道名,不是渠道号,不要搞混. 示例:x60,S40UI20.
	// *
	// * @param context
	// * @return
	// */
	// public static String getDeviceChannelName(Context context) {
	// String model = "";
	// if (null == DEVICE_CHANNEL_NAME || "".equals(DEVICE_CHANNEL_NAME)) {
	// final ApplicationInfo info = getApplicationInfo(context);
	//
	// if (null != info) {
	// model = info.metaData.get(CHANNELMANAGER_MODEL) + "";
	// DEVICE_CHANNEL_NAME = model;
	// }
	// } else {
	// model = DEVICE_CHANNEL_NAME;
	// }
	//
	// return model;
	// }

	/**
	 * 经过LetvManager转化 获取设备渠道名,不是渠道号,不要搞混. 示例:x60,S40UI20.
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceChannelNameWithLetvManager(Context context) {
		String model = "";
		if (null == DEVICE_CHANNEL_NAME || "".equals(DEVICE_CHANNEL_NAME)) {
			final ApplicationInfo info = getApplicationInfo(context);

			if (null != info) {
				model = LETV_MANAGER_CHANNEL_X60;

				// 通过LetvManager获取的设备信息, 和store的渠道进行对应.
				String letvManagerModel = getLetvManagerModel();
				if (letvManagerModel != null) {
					if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_C1) || letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_C1A) || letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_C1B) || letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_C1S)) {
						// TODO C1系列暂时不处理,C1A和C1B的含义未知
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_NEWC1S)) {
						model = LETV_MANAGER_CHANNEL_NEWC1S;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_X60)) {
						model = DEVICE_CHANNEL_X60;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_MAX_70)) {
						model = DEVICE_CHANNEL_MAX_70;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_S50Air)) {
						model = DEVICE_CHANNEL_S50Air;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_X50Air)) {
						model = DEVICE_CHANNEL_X50Air;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_S50)) {
						model = DEVICE_CHANNEL_S50;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_S40)) {
						// 如果能获取到40, 一定是UI20版本
						model = DEVICE_CHANNEL_S40UI20;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_S40Air)) {
						model = DEVICE_CHANNEL_S40Air;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_C2)) {
						model = DEVICE_CHANNEL_C2;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_T1S)) {
						// T1S 渠道转置NewC1S渠道
						model = LETV_MANAGER_CHANNEL_NEWC1S;
					} else if (letvManagerModel.equalsIgnoreCase(LETV_MANAGER_CHANNEL_X55Air)) {
						model = DEVICE_CHANNEL_X55Air;
					} else {
						model = letvManagerModel;
					}

				}

				DEVICE_CHANNEL_NAME = model;
			}
		} else {
			model = DEVICE_CHANNEL_NAME;
		}

		return model;
	}

	/**
	 * 获取设备真实名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceExactName(Context context) {
		String model = "";
		if (null == DEVICE_EXACT_NAME || "".equals(DEVICE_EXACT_NAME)) {
			final ApplicationInfo info = getApplicationInfo(context);

			if (null != info) {
				model = info.metaData.get(CHANNELMANAGER_MODEL_EXACT) + "";
				DEVICE_EXACT_NAME = model;
			}
		} else {
			model = DEVICE_EXACT_NAME;
		}

		return model;

	}

	/**
	 * 获取LetvManager中的设备信息
	 * 
	 * @return
	 */
	public static String getLetvManagerModel() {
		String modelString = null;
		try {
			modelString = LetvManager.getLetvModel();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return modelString;
	}

}
