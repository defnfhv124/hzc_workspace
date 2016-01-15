package com.letv.tvos.gamecenter.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author linan1 2013年10月8日 下午5:16:00
 */
public class AppUtil {

	/**
	 * 获取应用版本
	 * 
	 * @param context
	 *            上下文引用
	 * @return 应用版本
	 * @throws NameNotFoundException
	 *             未设置应用版本
	 */
	public static int getVersionCode(Context context) {

		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Logger.i("GC_Info", "gamecenter current version code:" + versionCode);
		return versionCode;
	}

	/**
	 * 获取应用版本名
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Logger.i("GC_Info", "gamecenter current version name:" + versionName);
		return versionName;
	}

	public static PackageInfo getPackageInfo(Context context, String packageName) {
		if(!StringUtil.isEmptyString(packageName)){
			PackageManager pm = context.getPackageManager();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
				if (packageInfo != null) {
					return packageInfo;
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// List<ApplicationInfo> list = getAllInstalledAppInfo(context);
		// if (list == null || list.size() == 0 || TextUtils.isEmpty(packageName)) {
		// return false;
		// }
		// boolean isExists = false;
		// for (ApplicationInfo info : list) {
		// if (info.packageName.equals(packageName)) {
		// isExists = true;
		// break;
		// }
		// }
		// return isExists;
		return null;
	}

	/**
	 * 判断应用是否已经安装
	 * 
	 * @param packageName
	 * @return
	 */

	/**
	 * 获取应用的versionCode
	 */
	public static long getAppVersionCode(Context context, String packageName) {
		long versionCode = -1;
		PackageManager pm = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (null != info) {
			versionCode = info.versionCode;
		}
		return versionCode;
	}

	/**
	 * 获取应用的versionCode
	 */
	public static String getAppVersionName(Context context, String packageName) {
		String versionName = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (null != info) {
			versionName = info.versionName;
		}
		return versionName;
	}
	
	/**
	 * 打开应用
	 * 
	 * @return boolean true 成功打开 false 打开失败
	 */
	public static boolean launchApp(final Context context, String packageName) {
		boolean isLaunched = false;
		if (packageName != null) {
			Intent intentOpen = context.getPackageManager().getLaunchIntentForPackage(packageName);
			if (null != intentOpen) {
				intentOpen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					context.startActivity(intentOpen);
					isLaunched = true;
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return isLaunched;
	}

}
