package com.letv.tvos.gamecenter.model;


public class BaseModel {
	
	/**
	 * 此对象的创建时间.
	 */
	private long createTime;
	
	public BaseModel() {
		createTime = System.currentTimeMillis();
	}
	
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	
	
}
