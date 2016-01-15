package com.letv.tvos.gamecenter.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class InstalledApkRecordUtil {
	private static InstalledApkRecordUtil instance;
	public ArrayList<String> installedPackages = new ArrayList<String>(100);
	public static synchronized InstalledApkRecordUtil getInstance(){
		if(instance == null){
			instance = new InstalledApkRecordUtil();
		}
		return instance;
	}
	public InstalledApkRecordUtil() {
		super();
	}
	
	public void init(Context context){
		installedPackages.clear();
		List<PackageInfo> installedPackageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
		for (PackageInfo packageInfo : installedPackageInfos) {
			installedPackages.add(packageInfo.packageName);
		}
		Collections.sort(installedPackages);
	}
	
	public boolean isInstalled(String s){
		return Collections.binarySearch(installedPackages, s) >= 0;
	}
	
	public void removeInstalledPackage(String s){
		installedPackages.remove(s);
	}
	
	public void addInstalledPackage(String s){
		if(Collections.binarySearch(installedPackages, s) < 0){
			installedPackages.add(s);
			Collections.sort(installedPackages);
		}
	}
	
	public synchronized void destory(){
		instance = null;
	}
	
}
