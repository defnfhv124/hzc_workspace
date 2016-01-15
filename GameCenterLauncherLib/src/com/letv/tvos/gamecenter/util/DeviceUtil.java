package com.letv.tvos.gamecenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUtil {
	private static final String TAG = "MobileUtils";
	private static final String UPDATA_FILE_CACHE_PATH = "updata";

	/*
	 * 判断网络连接是否已开 2012-08-20true 为已打开 false 未开
	 */
	public static boolean isNetDeviceAvailable(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	/**
	 * 得到MAC地址
	 * 
	 * @return
	 */
	public static String getMacAddress(Context context) {

		String macString = null;

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		if (null != wifiManager) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (null != info) {
				macString = info.getMacAddress();
			}
		}

		return macString;

	}

	/**
	 * 判断com.letv.t2.account Or com.stv.t2.account 返回true请使用国广帐号包名，否则为乐视帐号包名
	 */
	public static boolean LetvAccountORStvAccount(Context context) {
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo("com.stv.t2.account",
							PackageManager.GET_INTENT_FILTERS);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 得到设备名称
	 * 
	 * @return
	 */
	public static String getDeviceName() {
		return android.os.Build.MODEL;
	}

	/**
	 * 得到系统版本
	 * 
	 * @return
	 */
	public static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/***
	 * 
	 * 获取可用的文件存储位�?
	 * 
	 * */
	public static String getStorageDir(Context mContext) {
		String downLoadPath = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			if (null != mContext
					.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)) {
				downLoadPath = mContext.getExternalFilesDir(
						Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			}
			if (downLoadPath == null) {
				if (null != Environment.getExternalStorageDirectory()) {
					downLoadPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
				}
			}
		}

		if (downLoadPath == null) {
			if (null != mContext.getCacheDir()) {
				downLoadPath = mContext.getCacheDir().getAbsolutePath() + "/";// ʹ��androidĬ�ϻ���·��
			}
		}

		return downLoadPath;
	}

	/**
	 * 获取IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager ts = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ts.getDeviceId();
	}

	/**
	 * 获取IMSI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager ts = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ts.getSubscriberId();
	}

	/**
	 * gps是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGPSEnable(Context context) {
		/*
		 * 用Setting.System来读取也可以，只是这是更旧的用法 String str =
		 * Settings.System.getString(getContentResolver(),
		 * Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		 */
		String str = Secure.getString(context.getContentResolver(),
				Secure.LOCATION_PROVIDERS_ALLOWED);
		Log.v("GPS", str);
		if (str != null) {
			return str.contains("gps");
		} else {
			return false;
		}
	}

	/**
	 * 如果gps是开就关�?如果是关闭就打开 private static final int BUTTON_WIFI = 0; private
	 * static final int BUTTON_BRIGHTNESS = 1; private static final int
	 * BUTTON_SYNC = 2; private static final int BUTTON_GPS = 3; private static
	 * final int BUTTON_BLUETOOTH = 4;
	 * 
	 * @param contex
	 */
	public static void toggleGPS(Context contex) {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(contex, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	public boolean isOpenGps(Context contex) {
		LocationManager manager = (LocationManager) contex
				.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public static void closeGps(Context contex) {
		if (isGPSEnable(contex)) {
			toggleGPS(contex);
		}
	}

	/**
	 * android.permission.ACCESS_WIFI_STATE android.permission.CHANGE_WIFI_STATE
	 * android.permission.WAKE_LOCK wifi�?��,使用异步
	 */
	public static void toggleWifi(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		} else {
			wifiManager.setWifiEnabled(true);
		}
	}

	public static void getScanWifiResults(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> wifiResults = wifiManager.getScanResults();
		for (ScanResult wifi : wifiResults) {
		}

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		List<NeighboringCellInfo> cellResults = tm.getNeighboringCellInfo();
		for (NeighboringCellInfo cell : cellResults) {
		}

	}

	public static boolean isNetworkProvider(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isGpsProvider(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 获取本机的Ip地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * 初始化系统目录（用于缓存和下载app的存放路径等�?
	 * 
	 * @param mContext
	 */
	public static String getDownLoadPath(Context mContext) {
		String downLoadPath = null;
		boolean sdcardIsOK = false;
		// 获取sdcard目录
		String s = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			if (null != mContext
					.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)) {
				downLoadPath = mContext.getExternalFilesDir(
						Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			}
			if (downLoadPath == null) {
				if (null != Environment.getExternalStorageDirectory()) {
					downLoadPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
				}
			}
		}
		if (null == downLoadPath) {
			// sdcard 目录为空 获取外接设备
			List<String> deviceList = getDeviceFiles();
			if (deviceList != null && deviceList.size() > 0) {
				for (String path : deviceList) {
					if (!sdcardIsOK) { // 如果没有sdcard 选择第一个设备设备当做外接设�?
						downLoadPath = path;
						sdcardIsOK = true;
					}
				}
			}
		}
		if (null == downLoadPath) {
			// 使用android默认缓存路径
			if (null != mContext.getCacheDir()) {
				downLoadPath = mContext.getCacheDir().getAbsolutePath() + "/";
			}
		}

		// �?�� 并创建更新缓存目�?
		if (null != downLoadPath && new File(downLoadPath).canWrite()) {
			downLoadPath += ("/" + UPDATA_FILE_CACHE_PATH);
			File downLoadFile = new File(downLoadPath);
			if (!downLoadFile.exists()) {
				try {
					downLoadFile.mkdirs();
				} catch (Exception e) {
					Logger.e("DeviceUtil", "mkdirs failed");
					downLoadPath = null;
				}

			}

		}
		return downLoadPath;
	}

	/**
	 * 获取�?��的外接设备目�?
	 * 
	 * @return
	 */
	public static List<String> getDeviceFiles() {
		List<Partitions> partitionList = getPartitions();// 获得挂载点列�?
		List<String> devicePathList = new ArrayList<String>();

		if (partitionList != null && partitionList.size() > 0) { // 有挂载点

			File mountsFile = new File("/proc/mounts");

			if (mountsFile.exists() && mountsFile.isFile()
					&& mountsFile.canRead()) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(mountsFile));
					List<String> devList = new ArrayList<String>();
					String tempString = null;
					while ((tempString = reader.readLine()) != null) {
						if (tempString.startsWith("/dev/block/vold/")) {
							devList.add(tempString);

						}
					}
					for (String strs : devList) {
						String[] args = strs.split(" ");
						if (args != null && args.length > 2) {
							if (isThePartitionPath(partitionList, args[0])) {
								devicePathList.add(args[1]);
							}
						}
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e1) {
						}
					}
				}
			}
		}

		return devicePathList;
	}

	/**
	 * 获取外接设备挂载�?
	 * 
	 * @return
	 */
	public static List<Partitions> getPartitions() {

		File partitonsFile = new File("/proc/partitions");
		List<Partitions> partitionList = new ArrayList<Partitions>();

		if (partitonsFile.exists() && partitonsFile.isFile()) {
			List<String> lineList = new ArrayList<String>();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(partitonsFile));
				String tempString = null;
				while ((tempString = reader.readLine()) != null) {
					lineList.add(tempString);
					// Log.d("--------partitions tempString:"+tempString);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			for (String line : lineList) {
				String[] strs = line.trim().split(" ");
				Partitions partitions = new Partitions();
				for (int i = 0; i < strs.length; i++) {
					if (!"".equals(strs[i])) {
						try {
							if (partitions.getMajor() == null) {
								partitions.setMajor(Integer.valueOf(strs[i]));
								continue;
							}
							if (partitions.getMinior() == null) {
								partitions.setMinior(Integer.valueOf(strs[i]));
								continue;
							}
							if (partitions.getBlocks() == null) {
								partitions.setBlocks(Long.valueOf(strs[i]));
								continue;
							}
							if (partitions.getName() == null) {
								partitions.setName(strs[i]);
								continue;
							}
						} catch (Exception e) {
							// e.printStackTrace();
							continue;
						}

					}
				}
				// 名称不能为空，不能是mtd
				if (partitions.getName() != null
						&& !partitions.getName().trim().equals("")
						&& !partitions.getName().startsWith("mtd")) {
					partitionList.add(partitions);
				}

			}
		}

		return partitionList;
	}

	/**
	 * 验证该挂载点是否是移动设�?
	 * 
	 * @param partitionList
	 *            移动设备挂载点列�?
	 * @param point
	 *            挂载�?
	 * @return
	 */
	private static boolean isThePartitionPath(List<Partitions> partitionList,
			String point) {
		boolean isPartition = false;
		if (partitionList != null && point != null) {
			for (Partitions p : partitionList) {
				if (point.endsWith(p.getMajor() + ":" + p.getMinior())) {
					isPartition = true;
					break;
				}
			}
		}
		return isPartition;
	}

	/**
	 * 得到有线MAC地址
	 * 
	 * @return
	 */
	public static String getWan0MacAddress(Context context) {

		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;

	}

	/**
	 * 得到有线MAC地址
	 * 
	 * @return
	 */
	public static String getEth0MacAddress(Context context) {

		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/eth0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;

	}

	/**
	 * @title 挂载�?
	 * @description 暂未描述
	 * @company 乐视网信息技�?北京)股份有限公司
	 * @author 杨福�? www.yangfuhai.com
	 * @version 1.0
	 * @created 2011-11-17
	 * @changeRecord [修改记录]
	 */
	public static class Partitions {
		private Integer major; // 父节�?
		private Integer minior; // 分区节点
		private Long blocks; // 容量
		private String name; // 名称

		public Integer getMajor() {
			return major;
		}

		public void setMajor(Integer major) {
			this.major = major;
		}

		public Integer getMinior() {
			return minior;
		}

		public void setMinior(Integer minior) {
			this.minior = minior;
		}

		public Long getBlocks() {
			return blocks;
		}

		public void setBlocks(Long blocks) {
			this.blocks = blocks;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
