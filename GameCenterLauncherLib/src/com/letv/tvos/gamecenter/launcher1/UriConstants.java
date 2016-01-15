package com.letv.tvos.gamecenter.launcher1;

import android.net.Uri;

/**
 * 用户常量
 * 
 * @author QIYI
 * 
 */
public class UriConstants {
	public static final String IQY_AUTHORITY = "com.letv.tvapp.gamecenter";
	/**
	 * The scheme part for this provider's URI
	 */
	private static final String SCHEME = "content://";
	/**
	 * Path part for the Notes URI
	 */
	public static final String PATH_LETV = "/letv";

	/**路径
	 */
	public static final String PATH = SCHEME + IQY_AUTHORITY + PATH_LETV;
	/**
	 * The content:// style URL for this table
	 */
	public static final Uri CONTENT_URI = Uri.parse(PATH);
	public static class Project {
		public static final String ID = "_id";
		public static final String APP_PAG_NAGE = "_app_package_name";
		public static final String APP_LABEL_NAME = "_app_label_name";
		public static final String APP_INSTALL_TIME = "_app_app_install_time";
		public static final String APP_LATEST_START_TIME = "_app_latest_start_time";
		public static final String APP_LATEST_OPERATE_TIME = "_app_latest_operate_time";
		public static final String APP_ID = "_app_id";
		public static final String APP_DESKTOP_PIC = "_app_desktop_pic";
		public static final String APP_ICON = "_app_icon";
		public static final String APP_OTHER = "_app_other";
		
	}
}
