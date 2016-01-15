package com.letv.tvos.gamecenter.application.network;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.LoginFilter.UsernameFilterGeneric;

import com.google.gson.reflect.TypeToken;
import com.letv.tvos.gamecenter.application.network.IRequest.RequestMethod;
import com.letv.tvos.gamecenter.application.network.IRequest.RequestProtocol;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel;
import com.letv.tvos.gamecenter.model.DesktopItem;
import com.letv.tvos.gamecenter.util.Logger;
import com.letv.tvos.gamecenter.util.StringUtil;



public class RequestMaker {

	private RequestMaker() {
		
	}

	private static RequestMaker requestMaker = null;

	/**
	 * 得到JsonMaker的实例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static RequestMaker getInstance() {
		if (requestMaker == null) {
			requestMaker = new RequestMaker();
			return requestMaker;
		} else {
			return requestMaker;
		}
	}
	
	public IRequest<ArrayList<DesktopItem>> getDeskTopItemsRequest(){
		//生成一个请求
		IRequest<ArrayList<DesktopItem>> request = new IRequest.Builder<ArrayList<DesktopItem>>()
				.setRequestMethod(RequestMethod.GET)									
				.setRequestProtocol(RequestProtocol.HTTP)								
				.setUrlString(UrlSet.URL_DESKTOP_ITEMS)								
				.setRetryNumber(1)
				.setModelType(new TypeToken<IResponse<ArrayList<DesktopItem>>>(){}.getType())	
				.setUseCustomRules(true)												
				.create();	
		
		return request;
	}
	
	public IRequest<DeskTopResponseModel> getDeskTopItems1Request(){
		//生成一个请求
		IRequest<DeskTopResponseModel> request = new IRequest.Builder<DeskTopResponseModel>()
				.setRequestMethod(RequestMethod.GET)									
				.setRequestProtocol(RequestProtocol.HTTP)								
				.setUrlString(UrlSet.URL_DESKTOP_ITEMS1)								
				.setRetryNumber(1)
				.setModelType(new TypeToken<IResponse<DeskTopResponseModel>>(){}.getType())	
				.setUseCustomRules(true)												
				.create();	
		
		return request;
	}
}
