package com.letv.tvos.gamecenter.application.network;


/**
 * 网络请求完成的回调
 * @author linan1
 * 2013年10月8日 上午11:41:28
 * @param <T>
 */
public interface OnNetworkCompleteListener<T> {
	/**
	 * 成功的回调
	 * @param result 包含请求的信息和服务区返回的结果
	 * @param resultString 服务器返回的字符串,用于持久化等操作
	 */
	public void onNetworkCompleteSuccess(IRequest<T> result , String resultString);
	
	/**
	 * 失败的回调
	 * @param result 包含请求的信息和服务区返回的结果
	 * @param resultString 服务器返回的字符串,用于持久化等操作
	 */
	public void onNetworkCompleteFailed(IRequest<T> result , String resultString);

}
