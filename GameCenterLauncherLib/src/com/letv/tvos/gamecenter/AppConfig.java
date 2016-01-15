package com.letv.tvos.gamecenter;

/**
 * @author linan1 2013年10月8日 上午11:31:10
 */
public class AppConfig {
	/*
	 * ######################################打包注意事项#######################################
	 * 
	 * 1.香港版打包：更换splash页面、更换服务器地址、删除英文、修改客服电话，更换DataEye APP Id
	 * 
	 * 2.海外版打包：更换推送服务器地址、更换服务器地址、更换统计SDK服务器地址、更换DataEye APP Id
	 * 
	 * 3.更换DEBUG_MODE和RELEASE_BUILD_ENVIRONMENT_DEFAULT的值即可
	 * 
	 * ######################################打包注意事项#######################################
	 */

	/**
	 * 测试模式,在打包时需要修改为false
	 */
	public static boolean DEBUG_MODE = true;
	/**
	 * 以独立apk模式运行是这个参数要改成true
	 * 以插件模式运行时这个参数要改成false
	 * */
	public static boolean DEBUG_IN_SELF = false;
	public static String IMAGE_CACHE_DIR = "/gamecenter/";

	/**
	 * @Fields RELEASE_BUILD_ENVIRONMENT_ZH : 版本发布中国内地环境
	 */
	public static String RELEASE_BUILD_ENVIRONMENT_ZH = "environment_zh";

	/**
	 * @Fields RELEASE_BUILD_ENVIRONMENT_HK : 版本发布中国香港版
	 */
	public static String RELEASE_BUILD_ENVIRONMENT_HK = "environment_hk";

	/**
	 * @Fields RELEASE_BUILD_ENVIRONMENT_HW : 版本发布海外环境
	 */
	public static String RELEASE_BUILD_ENVIRONMENT_HW = "environment_hw";

	/**
	 * @Fields RELEASE_BUILD_ENVIRONMENT_DEFAULT : 版本发布默认环境
	 */
	public static String RELEASE_BUILD_ENVIRONMENT_DEFAULT = RELEASE_BUILD_ENVIRONMENT_ZH;

	/**
	 * @Fields DATAEYE_APP_ID_ZH : DataEye中国内地统计id
	 */
	public static String DATAEYE_APP_ID_ZH = "CE0FB6481F8FC54BF57B3C515C62F02C2";

	/**
	 * @Fields DATAEYE_APP_ID_HK : DataEye中国香港统计id
	 */
	public static String DATAEYE_APP_ID_HK = "CE0FB6481F8FC54BF57B3C515C62F02C2";

	/**
	 * @Fields DATAEYE_APP_ID_HW : DataEye海外统计id
	 */
	public static String DATAEYE_APP_ID_HW = "CE0FB6481F8FC54BF57B3C515C62F02C2";

	/**
	 * @Fields DATAEYE_APP_ID_TEST : DataEye测试统计id
	 */
	public static String DATAEYE_APP_ID_TEST = "CD663256BF70FF843D27D636F2145EAC7";

	/**
	 * 页面数据的过期时间,超过此时间后,需要更新页面
	 */
	public static long PAGE_DATA_OVERDUE_TIME = 1000 * 60 * 5;

	/** 3D游戏中心配置文件检测间隔 */
	public static long THREE_DIMENSIONAL_GAME_PROFILE_CHECKING_DELAY_TIME = 1000 * 60 * 30;

	public static class BuildConfig {
		public static boolean DEBUG = DEBUG_MODE;
	}

	/**
	 * @Description: 由不同环境获取DataEye APP Id
	 * @return
	 */
	public static String getDataEyeAppId() {
		if (AppConfig.DEBUG_MODE) {
			return DATAEYE_APP_ID_TEST;
		} else {
			if (AppConfig.RELEASE_BUILD_ENVIRONMENT_DEFAULT.equals(AppConfig.RELEASE_BUILD_ENVIRONMENT_ZH)) {
				return DATAEYE_APP_ID_ZH;
			} else if (AppConfig.RELEASE_BUILD_ENVIRONMENT_DEFAULT.equals(AppConfig.RELEASE_BUILD_ENVIRONMENT_HK)) {
				return DATAEYE_APP_ID_HK;
			} else if (AppConfig.RELEASE_BUILD_ENVIRONMENT_DEFAULT.equals(AppConfig.RELEASE_BUILD_ENVIRONMENT_HW)) {
				return DATAEYE_APP_ID_HW;
			} else {
				return DATAEYE_APP_ID_ZH;
			}
		}
	}

}
