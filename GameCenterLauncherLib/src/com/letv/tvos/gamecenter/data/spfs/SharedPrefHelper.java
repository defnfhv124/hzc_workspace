package com.letv.tvos.gamecenter.data.spfs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
	/**
	 * SharedPreferences的名字
	 */
	private static final String SP_FILE_NAME = "GAMECENTER_LAUNCHER_SP";
	private static SharedPrefHelper sharedPrefHelper = null;
	private static SharedPreferences sharedPreferences;

	public static synchronized SharedPrefHelper getInstance(Context context) {
		if (null == sharedPrefHelper) {
			sharedPrefHelper = new SharedPrefHelper(context);
		}
		return sharedPrefHelper;
	}

	private SharedPrefHelper(Context context) {
		sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
	}

	public void setDataCache(String cache) {
		sharedPreferences.edit().putString("cache", cache).commit();
	}

	public String getDataCache() {
		return sharedPreferences.getString("cache", "");
	}
	
	public void setLastUpdateTime(long time) {
		sharedPreferences.edit().putLong("lastupdatetime", time).commit();
	}

	public long getLastUpdateTime() {
		return sharedPreferences.getLong("lastupdatetime", 0);
	}

}
