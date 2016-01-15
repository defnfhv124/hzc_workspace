package com.letv.tvos.gamecenter.application.network;

import com.letv.tvos.gamecenter.AppConfig;

/**
 * @author linan1 2013年10月8日 下午6:32:17
 */
public class UrlSet {

	public static final String URL_919_EVENT_SERVER = "http://ms.letvstore.com";

	public static final String URL_919_EVENT_SERVER_TEST = "http://117.121.2.139:9090";
	/**
	 * @Fields URL_BASIC_SERVICE_ZH : 内地服务器地址
	 */
	public static final String URL_BASIC_SERVICE_ZH = "http://gc.letvstore.com";
	// public static final String URL_BASIC_SERVICE_ZH = "http://106.38.226.10:9080";//灰度地址

	/**
	 * @Fields URL_BASIC_SERVICE_HK : 香港服务器地址
	 */
	public static final String URL_BASIC_SERVICE_HK = "http://hk.gc.letvstore.com";

	/**
	 * @Fields URL_BASIC_SERVICE_HW : 海外服务器地址
	 */
	public static final String URL_BASIC_SERVICE_HW = "http://gc.vmoters.com";

	/**
	 * @Fields URL_BASIC_SERVICE_TEST : 测试服务器地址
	 */
	public static final String URL_BASIC_SERVICE_TEST = "http://106.120.179.40:8080";

	/**
	 * @Fields PUSH_SERVER_URL_ZH : 内地推送服务器地址
	 */
	public static final String PUSH_SERVER_URL_ZH = "athena.letvstore.com";

	/**
	 * @Fields PUSH_SERVER_URL_HW : 海外推送服务器地址
	 */
	public static final String PUSH_SERVER_URL_HW = "117.121.2.245";

	/** 桌面接口1 */
	public static final String URL_DESKTOP_ITEMS = getBasicServiceUrl() + "/api/desktop/list";

	/** 桌面接口2 */
	public static final String URL_DESKTOP_ITEMS1 = getBasicServiceUrl() + "/api/desktop/list2";
	/**
	 * @Description: 由不同环境获取服务器地址
	 * @return
	 */
	public static String getBasicServiceUrl() {
		if (AppConfig.DEBUG_MODE) {
			return URL_BASIC_SERVICE_TEST;
		} else {
//			if (AppConfig.RELEASE_BUILD_ENVIRONMENT_DEFAULT.equals(AppConfig.RELEASE_BUILD_ENVIRONMENT_ZH)) {
				return URL_BASIC_SERVICE_ZH;
//			} else if (AppConfig.RELEASE_BUILD_ENVIRONMENT_DEFAULT.equals(AppConfig.RELEASE_BUILD_ENVIRONMENT_HK)) {
//				return URL_BASIC_SERVICE_HK;
//			} else if (AppConfig.RELEASE_BUILD_ENVIRONMENT_DEFAULT.equals(AppConfig.RELEASE_BUILD_ENVIRONMENT_HW)) {
//				return URL_BASIC_SERVICE_HW;
//			} else {
//				return URL_BASIC_SERVICE_ZH;
//			}
		}
	}


}
