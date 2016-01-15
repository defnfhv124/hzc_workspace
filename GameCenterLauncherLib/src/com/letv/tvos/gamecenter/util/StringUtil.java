package com.letv.tvos.gamecenter.util;


public class StringUtil {
	
	/**
	 * 判断字符串是否为null或�?空字符串
	 * @param str
	 * @return
	 */
	public static boolean isEmptyString(String str) {
		boolean result = false;
		if(null == str || "".equals(str.trim()) || "null".equalsIgnoreCase(str)) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 如果字符串为空则转为""
	 * @param source
	 * @return
	 */
	public static String changeNullToEmpty(String source) {
		if (null == source) {
			return "";
		}
		
		return source;
	}
	
	/**
	 * 如果i小于10，添�?后生成string
	 * @param i
	 * @return
	 */
	public static String addZreoIfLessThanTen(int i) {
		
		String string = "";
		int ballNum = i +1;
		if( ballNum < 10) {
			string = "0"+ballNum;
		}else {
			string = ballNum+"";
		}
		return string;
	}
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotNull(String string){
		if(null != string && !"".equals(string.trim())){
			return true;
		}
		return false;
	}
	/**
	 * 去掉�?��字符串中的所有的单个空格" "
	 * @param string
	 */
	public static String replaceSpaceCharacter(String string){
		if(null == string || "".equals(string)){
			return "";
		}
		return string.replace(" ", "");
	}
	
	
}
