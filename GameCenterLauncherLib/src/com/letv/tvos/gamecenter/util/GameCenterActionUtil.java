package com.letv.tvos.gamecenter.util;

import com.letv.tvos.gamecenter.model.DesktopItem;

public class GameCenterActionUtil {

	public static final int TYPE_APP = 11;
	public static final int TYPE_3RD_ZONE = 10;
	public static final int TYPE_CLASS = 2;
	public static final int TYPE_INPUT_DEVICE_CLASS = 1;
	public static final int TYPE_SUBJECT = 3;
	public static final int TYPE_USER_CENTER = 9;
	public static final int TYPE_SETTING = 8;
	public static final int TYPE_SEARCH = 7;
	public static final int TYPE_EVENT = 5;
	public static final int TYPE_RANK = 4;
	public static final int TYPE_MAIN = 6;
	public static final int TYPE_OTHER = 0;

	// ACC("体感"),
	// ACCSET("体感集合"),
	// CATG("分类"),
	// CATGSET("分类集合"),
	// SUBJECT("专题"),
	// SBJSET("专题集合"),
	// ACTIVITY("活动"),
	// APP("游戏");
	// SHOUYE("首页"),
	// TIGANYE("体感页"),
	// PAIHANGYE("排行页"),
	// ZHUANTIYE("专题页"),
	// FENLEIYE("分类页"),
	// HUODONGYE("活动页"),
	// SOUSUO("搜索"),
	// SHEZHI("设置"),
	// GRZX("个人中心"),
	// WDYX("我的游戏"),


	public static int getGameCenterActionCode(String actionName) {
		if ("ACC".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("ACCSET".equals(actionName)) {
			return TYPE_INPUT_DEVICE_CLASS;
		} else if ("CATG".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("CATGSET".equals(actionName)) {
			return TYPE_CLASS;
		} else if ("SUBJECT".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("SBJSET".equals(actionName)) {
			return TYPE_SUBJECT;
		} else if ("ACTIVITY".equals(actionName)) {
			return TYPE_EVENT;
		} else if ("APP".equals(actionName)) {
			return TYPE_APP;
		} else if ("SHOUYE".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("TIGANYE".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("PAIHANGYE".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("ZHUANTIYE".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("HUODONGYE".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("SOUSUO".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("SHEZHI".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("GRZX".equals(actionName)) {
			return TYPE_MAIN;
		} else if ("WDYX".equals(actionName)) {
			return TYPE_MAIN;
		}
		return 0;

	}
	
	public String getGameCenterActionParamters(DesktopItem desktopItem){
		if(desktopItem != null){
			if ("ACC".equals(desktopItem.type)) {
				return "{tab = 0}";
			} else if ("ACCSET".equals(desktopItem.type)) {
				return desktopItem.parameter;
			} else if ("CATG".equals(desktopItem.type)) {
				return "{tab = 3}";
			} else if ("CATGSET".equals(desktopItem.type)) {
				return desktopItem.parameter;
			} else if ("SUBJECT".equals(desktopItem.type)) {
				return "{tab = 4}";
			} else if ("SBJSET".equals(desktopItem.type)) {
				return desktopItem.parameter;
			} else if ("ACTIVITY".equals(desktopItem.type)) {
				return desktopItem.parameter;
			} else if ("APP".equals(desktopItem.type)) {
				return desktopItem.parameter;
			} else if ("SHOUYE".equals(desktopItem.type)) {
				return "{tab = 1}";
			} else if ("TIGANYE".equals(desktopItem.type)) {
				return "{tab = 0}";
			} else if ("PAIHANGYE".equals(desktopItem.type)) {
				return "{tab = 2}";
			} else if ("ZHUANTIYE".equals(desktopItem.type)) {
				return "{tab = 4}";
			} else if ("HUODONGYE".equals(desktopItem.type)) {
				return "{tab = 5}";
			} else if ("SOUSUO".equals(desktopItem.type)) {
				return "{tab = 6}";
			} else if ("SHEZHI".equals(desktopItem.type)) {
				return "{tab = 7}";
			} else if ("GRZX".equals(desktopItem.type)) {
				return "{tab = 8}";
			} else if ("WDYX".equals(desktopItem.type)) {
				return "{}";
			}
		}
		return "";
		
	}
}
