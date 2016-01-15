package com.letv.tvos.gamecenter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件工具类
 * 
 * @author devlei
 * @date 2014-2-27 上午10:24:57
 */
public class FileUtil {

	/**
	 * @Description: 下载的文件是否是apk
	 * @param path
	 *            文件本地路径
	 * @return
	 */
	public static boolean downlaodFileTypeIsApk(String path) {
		if (!StringUtil.isEmptyString(path)) {
			int lastPointPosition = path.lastIndexOf(".");
			String suffixName = path.substring(lastPointPosition + 1, path.length());
			Logger.i("GC_Info", "download file suffix name:" + suffixName);
			if ("apk".equalsIgnoreCase(suffixName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 下载的文件是否是zip
	 * @param path
	 *            文件本地路径
	 * @return
	 */
	public static boolean downlaodFileTypeIsZip(String path) {
		if (!StringUtil.isEmptyString(path)) {
			int lastPointPosition = path.lastIndexOf(".");
			String suffixName = path.substring(lastPointPosition + 1, path.length());
			Logger.i("GC_Info", "download file suffix name:" + suffixName);
			if ("zip".equalsIgnoreCase(suffixName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean downlaodFileTypeIsTxt(String path) {
		if (!StringUtil.isEmptyString(path)) {
			int lastPointPosition = path.lastIndexOf(".");
			String suffixName = path.substring(lastPointPosition + 1, path.length());
			Logger.i("GC_Info", "download file suffix name:" + suffixName);
			if ("txt".equalsIgnoreCase(suffixName)) {
				return true;
			}
		}
		return false;
	}
	public static void copyFile(InputStream is, File to) {
		boolean result = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(to);
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			result = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public static void copyFile(File from, File to) {
		boolean result = false;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(from);
			fos = new FileOutputStream(to);
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = fis.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			result = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
