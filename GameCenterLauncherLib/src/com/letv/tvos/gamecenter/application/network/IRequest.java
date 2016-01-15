package com.letv.tvos.gamecenter.application.network;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.google.gson.Gson;
import com.letv.tvos.gamecenter.application.parser.BaseParser;
import com.letv.tvos.gamecenter.util.AppUtil;
import com.letv.tvos.gamecenter.util.DeviceUtil;
import com.letv.tvos.gamecenter.util.StringUtil;
/**
 * 发起一个网络任务的任务实体
 * @author linan1
 * 2013年10月8日 上午11:07:25
 * @param <T>
 */
public class IRequest<T>{
	
	/**
	 * 请求方法的枚举
	 */
	public enum RequestMethod {
		POST("POST"), GET("GET");
		private String requestMethodName;
		RequestMethod(String requestMethodName) {
			this.requestMethodName = requestMethodName;
		}

		public String getRequestMethodName() {
			return requestMethodName;
		}
	}
	/**
	 * 请求协议的枚举
	 */
	public enum RequestProtocol {
		HTTP("HTTP"), HTTPS("HTTPS");
		private String requestProtocol;
		RequestProtocol(String requestProtocol) {
			this.requestProtocol = requestProtocol;
		}

		public String requestProtocolName() {
			return requestProtocol;
		}
	}
	
	public void start(Context context) {
		HttpRequestManager.getInstance(context).start(this, onNetworkCompleteListener);
	}
	
	
	/**
	 * 重试次数,默认为1次
	 */
	private int retryNumber = 1;
	
	/**
	 * 请求方式
	 */
	private RequestMethod requestMethod;
	
	/**
	 * 请求所用协议
	 */
	private RequestProtocol requestProtocol;
	
	/**
	 * 接口参数列表
	 */
	private Map<String, String> paramsMap;
	
	/**
	 * POST
	 */
	private Object postObject;
	
	/**
	 * 默认解析器解析出的类型
	 */
	private Type modelType;
	
	/**
	 * 请求完成之后的回调
	 */
	@SuppressWarnings("rawtypes")
	private OnNetworkCompleteListener onNetworkCompleteListener;
	
	/**
	 * 请求的URL地址
	 */
	private String urlString;
	
	/**
	 * 如果不使用默认解析,传入的特定解析器
	 */
	private BaseParser<IResponse<T>> parser;
	
	/**
	 * 请求得到的结果
	 */
	private IResponse<T> responseObject;
	
	/**
	 * 一个IRequest可以附带一个tag
	 */
	private Object tag;
	
	/**
	 * 是否使用和服务器端定义的通信协议,true为使用.
	 * 使用时,会将{@link #paramsMap}生成定义的通信格式
	 * 如不使用,则将{@link #paramsMap}的内容,依次作为请求的Key,Value传递
	 */
	private boolean isUseCustomRules;
	
	
	private int httpCode;





	private IRequest(Builder<T> builder) {
		this.retryNumber = builder.retryNumber;
		this.requestMethod = builder.requestMethod;
		this.requestProtocol = builder.requestProtocol;
		this.paramsMap = builder.paramsMap;
		this.postObject = builder.postObject;
		this.modelType = builder.modelType;
		this.onNetworkCompleteListener = builder.onNetworkCompleteListener;
		this.urlString = builder.urlString;
		this.parser = builder.parser;
		this.responseObject = builder.responseObject;
		this.tag = builder.tag;
		this.isUseCustomRules = builder.isUseCustomRules;
	}
	
	public boolean isUseCustomRules() {
		return isUseCustomRules;
	}


	public void setUseCustomRules(boolean isUseCustomRules) {
		this.isUseCustomRules = isUseCustomRules;
	}

	
	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	public int getRetryNumber() {
		return retryNumber;
	}


	public void setRetryNumber(int retryNumber) {
		this.retryNumber = retryNumber;
	}


	public RequestMethod getRequestMethod() {
		return requestMethod;
	}


	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}


	public RequestProtocol getRequestProtocol() {
		return requestProtocol;
	}


	public void setRequestProtocol(RequestProtocol requestProtocol) {
		this.requestProtocol = requestProtocol;
	}


	public Map<String, String> getParamsMap() {
		return paramsMap;
	}


	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}


	public Type getModelType() {
		return modelType;
	}


	public void setModelType(Type modelType) {
		this.modelType = modelType;
	}

	@SuppressWarnings("rawtypes")
	public OnNetworkCompleteListener getOnNetworkCompleteListener() {
		return onNetworkCompleteListener;
	}

	@SuppressWarnings("rawtypes")
	public IRequest setOnNetworkCompleteListener(
			OnNetworkCompleteListener onNetworkCompleteListener) {
		this.onNetworkCompleteListener = onNetworkCompleteListener;
		return this;
	}


	public String getUrlString() {
		return urlString;
	}


	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public BaseParser<IResponse<T>> getParser() {
		return parser;
	}


	public void setParser(BaseParser<IResponse<T>> parser) {
		this.parser = parser;
	}

	public IResponse<T> getResponseObject() {
		return responseObject;
	}


	public void setResponseObject(IResponse<T> responseObject) {
		this.responseObject = responseObject;
	}
	
	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
	
	/**
	 * 构建IRequest的Builder
	 * @param <T>
	 */
	public static class Builder<T> {
	
		/**
		 * 重试次数
		 */
		private int retryNumber;
		
		/**
		 * 请求方式
		 */
		private RequestMethod requestMethod;
		
		/**
		 * 请求所用协议
		 */
		private RequestProtocol requestProtocol;
		
		/**
		 * 接口参数列表
		 */
		private Map<String, String> paramsMap;
		
		/**
		 * POST
		 */
		private Object postObject;
		
		/**
		 * 默认解析器解析出的类型
		 */
		private Type modelType;
		
		/**
		 * 请求完成之后的回调
		 */
		@SuppressWarnings("rawtypes")
		private OnNetworkCompleteListener onNetworkCompleteListener;
		
		/**
		 * 请求的URL地址
		 */
		private String urlString;
		
		/**
		 * 如果不使用默认解析,传入的特定解析器
		 */
		private BaseParser<IResponse<T>> parser;
		
		/**
		 * 请求得到的结果
		 */
		private IResponse<T> responseObject;
		
		/**
		 * 一个IRequest可以附带一个tag
		 */
		private Object tag;
		
		
		/**
		 * 是否使用和服务器端定义的通信协议,true为使用.
		 * 使用时,会将{@link #paramsMap}生成定义的通信格式
		 * 如不使用,则将{@link #paramsMap}的内容,依次作为请求的Key,Value传递
		 */
		private boolean isUseCustomRules;
		

		public boolean isUseCustomRules() {
			return isUseCustomRules;
		}



		public Builder<T> setUseCustomRules(boolean isUseCustomRules) {
			this.isUseCustomRules = isUseCustomRules;
			return this;
		}



		public Object getTag() {
			return tag;
		}



		public Builder<T> setTag(Object tag) {
			this.tag = tag;
			return this;
		}



		public int getRetryNumber() {
			return retryNumber;
		}



		public Builder<T> setRetryNumber(int retryNumber) {
			this.retryNumber = retryNumber;
			return this;
		}



		public RequestMethod getRequestMethod() {
			return requestMethod;
		}



		public Builder<T> setRequestMethod(RequestMethod requestMethod) {
			this.requestMethod = requestMethod;
			return this;
		}



		public RequestProtocol getRequestProtocol() {
			return requestProtocol;
		}



		public Builder<T> setRequestProtocol(RequestProtocol requestProtocol) {
			this.requestProtocol = requestProtocol;
			return this;
		}



		public Map<String, String> getParamsMap() {
			return paramsMap;
		}



		public Builder<T> setParamsMap(Map<String, String> paramsMap) {
			this.paramsMap = paramsMap;
			return this;
		}



		public Type getModelType() {
			return modelType;
		}



		public Builder<T> setModelType(Type modelType) {
			this.modelType = modelType;
			return this;
		}


		@SuppressWarnings("rawtypes")
		public OnNetworkCompleteListener getOnNetworkCompleteListener() {
			return onNetworkCompleteListener;
		}


		@SuppressWarnings("rawtypes")
		public Builder<T> setOnNetworkCompleteListener(
				OnNetworkCompleteListener onNetworkCompleteListener) {
			this.onNetworkCompleteListener = onNetworkCompleteListener;
			return this;
		}



		public String getUrlString() {
			return urlString;
		}



		public Builder<T> setUrlString(String urlString) {
			this.urlString = urlString;
			return this;
		}



		public BaseParser<IResponse<T>> getParser() {
			return parser;
		}



		public Builder<T> setParser(BaseParser<IResponse<T>> parser) {
			this.parser = parser;
			return this;
		}



		public IResponse<T> getResponseObject() {
			return responseObject;
		}



		public Builder<T> setResponseObject(IResponse<T> responseObject) {
			this.responseObject = responseObject;
			return this;
		}


		/**
		 * 创建一个IRequest对象
		 * @return
		 */
		public IRequest<T> create() {
			return new IRequest<T>(this);
		}



		public Object getPostObject() {
			return postObject;
		}



		public Builder<T> setPostObject(Object postObject) {
			this.postObject = postObject;
			return this;
		}
	}
	
	/**
	 * 生成定义的通信协议的请求信息集合
	 * @param paramsMap
	 * @return
	 */
	public static Map<String, String> getCustomRuleParamsMap(Map<String, String> paramsMap) {
		Map<String, String> customRuleParamsMap = new HashMap<String, String>();
		
		Map<String, Map<String, String>> jsonMap = new HashMap<String, Map<String, String>>();
		Map<String, String> commonParamsMap = getCommonParamsMap();
		
		jsonMap.put("commonParams", commonParamsMap);
		jsonMap.put("serviceParams", paramsMap);
		String requestJsonString = "";
		try {
			requestJsonString = new Gson().toJson(jsonMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		customRuleParamsMap.put("params", requestJsonString);
		
		
		return customRuleParamsMap;
	}

	/**
	 * 获取通用参数
	 * @return
	 */
	public static Map<String, String> getCommonParamsMap() {
		Map<String, String> commonParamsMap = new HashMap<String, String>();
		
		commonParamsMap.put("gc-mac", StringUtil.changeNullToEmpty(HttpRequestManager.macString));
		commonParamsMap.put("gc-imei", StringUtil.changeNullToEmpty(HttpRequestManager.imeiString));
		commonParamsMap.put("Authorization", StringUtil.changeNullToEmpty(HttpRequestManager.sessionIdString));
		commonParamsMap.put("gc-channelNo", StringUtil.changeNullToEmpty(HttpRequestManager.channelNoString));
		commonParamsMap.put("gc-deviceChannelName", StringUtil.changeNullToEmpty(HttpRequestManager.deviceChannelNameString));
		commonParamsMap.put("gc-deviceExactName", StringUtil.changeNullToEmpty(HttpRequestManager.deviceExactNameString));
		commonParamsMap.put("gc-appVersion", StringUtil.changeNullToEmpty(HttpRequestManager.appVersionString));
		commonParamsMap.put("gc-deviceInfo", StringUtil.changeNullToEmpty(HttpRequestManager.deviceInfoString));
		commonParamsMap.put("gc-osVersion", StringUtil.changeNullToEmpty(HttpRequestManager.osVersionString));
		commonParamsMap.put("gc-timeStamp", StringUtil.changeNullToEmpty(HttpRequestManager.timeStampString));
		commonParamsMap.put("gc-dataInfo", StringUtil.changeNullToEmpty(HttpRequestManager.dataInfoString));
		return commonParamsMap;
	}

	public Object getPostObject() {
		return postObject;
	}

	public void setPostObject(Object postObject) {
		this.postObject = postObject;
	}


	
}
