package com.letv.tvos.gamecenter.model;

public class DesktopItem implements Comparable{
	public int cellType;
	public int orderValue;
	public String type;
	public String name;
	public String title;
	public String picture;
	public String parameter;
	@Override
	public int compareTo(Object another) {
		DesktopItem anotherDesktopItem = (DesktopItem) another;
		if(this.orderValue < anotherDesktopItem.orderValue){
			return -1;
		}else if(this.orderValue == anotherDesktopItem.orderValue){
			return 0;
		}else{
			return 1;
		}
	}
}
