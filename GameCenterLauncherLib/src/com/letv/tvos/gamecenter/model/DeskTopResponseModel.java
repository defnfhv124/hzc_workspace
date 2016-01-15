package com.letv.tvos.gamecenter.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeskTopResponseModel {
	public List<DeskTopRowItem> focusPics;
	public DeskTopRowItem myGame;
	public List<DeskTopRowItem> operateItems;
	
	public static class DeskTopRowItem{
		public int cellType;
		public int orderType;
		public String type;
		public String name;
		public String title;
		public String background;
		public String desktopPic;
		public String parameter;
		public String itemType;
		public String picture;
		public List<DeskTopItem> items;
	}
	
	public static class DeskTopItem{
		public String desktopPic;
		public String icon;
		public int id = -1;
		public String name = "";
		public String packageName;
		public String subType;
		public String type;
		public String accessoriesZoneCid;
		public String zoneName;
		public String itemType;
		public String accessoriesId;
		public String accessoriesZoneBackgroundUrl;
		public List<Map<Object, Object>> items;
	}
}
