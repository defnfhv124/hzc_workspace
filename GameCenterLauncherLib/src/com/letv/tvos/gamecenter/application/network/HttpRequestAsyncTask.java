package com.letv.tvos.gamecenter.application.network;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.letv.tvos.gamecenter.AppConfig;
import com.letv.tvos.gamecenter.application.network.IRequest.RequestMethod;

/**
 * 网络请求任务
 * 
 * @author linan1 2013年10月8日 下午5:55:04
 */
public class HttpRequestAsyncTask extends AsyncTask<IRequest<?>, Void, IRequest<?>> {
	// 超时时间
	private static final int RESPONSE_TIME_OUT = 15000;
	private static final int REQUEST_TIME_OUT = 15000;

	private String resultString;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	private static AndroidHttpClient httpClient;

	static {

		httpClient = AndroidHttpClient.newInstance("");
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIME_OUT);
		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, RESPONSE_TIME_OUT);
	}

	private HttpClient buildHttpClient() {
		return httpClient;
	}

	@Override
	protected IRequest<?> doInBackground(IRequest<?>... params) {

		IRequest<?> request = null;

		try {
			HttpClient httpClient = buildHttpClient();
			request = params[0];

			HttpResponse httpResponse;
			HttpGet httpGet = null;
			HttpPost httpPost = null;

			// 获取请求URL
			String urlString = request.getUrlString();

			// 获取参数列表
			Set<Map.Entry<String, String>> paramsSet = null;
			if (null != request.getParamsMap()) {
				// 如果是使用定义的协议则需要生成定义好的信息
				// if (request.isUseCustomRules() && request.getRequestMethod() == RequestMethod.POST) {
				// paramsSet = IRequest.getCustomRuleParamsMap(request.getParamsMap()).entrySet();
				// }else {
				paramsSet = request.getParamsMap().entrySet();
				// }
			}

			// 获取通用参数
			Set<Map.Entry<String, String>> commonParamsSet = IRequest.getCommonParamsMap().entrySet();
			//
			if (RequestMethod.GET.equals(request.getRequestMethod())) {
				// GET请求
				StringBuilder stringBuilder = new StringBuilder(urlString);
				if (null != paramsSet) {
					stringBuilder.append("?");
					for (Map.Entry<String, String> entry : paramsSet) {
						stringBuilder.append(entry.getKey());
						stringBuilder.append('=');
						stringBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
						stringBuilder.append('&');
					}
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
				}
				httpGet = new HttpGet(stringBuilder.toString());
				// 添加头信息
				for (Iterator<Map.Entry<String, String>> it = commonParamsSet.iterator(); it.hasNext();) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}

				httpResponse = httpClient.execute(httpGet);
//				if (AppConfig.DEBUG_MODE) {
					System.out.println("请求方式GET,地址:" + stringBuilder.toString());
//				}

			} else {
				// POST请求
				httpPost = new HttpPost(urlString);
				// 添加头信息
				for (Iterator<Map.Entry<String, String>> it = commonParamsSet.iterator(); it.hasNext();) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
				if (null != request.getPostObject()) {
					String postBodyString = new Gson().toJson(request.getPostObject());
					StringEntity stringEntity = new StringEntity(postBodyString, "UTF-8");
					httpPost.setEntity(stringEntity);
					httpPost.addHeader("Content-Type", "application/json");
				}else if (null != paramsSet){
					ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>();
					for (Map.Entry<String, String> entry : paramsSet) {
						localArrayList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
					}
					httpPost.setEntity(new UrlEncodedFormEntity(localArrayList, "UTF-8"));
				}
				
				httpResponse = httpClient.execute(httpPost);
//				if (AppConfig.DEBUG_MODE) {
					System.out.println("请求方式POST,地址:" + urlString);
//				}
			}
			request.setHttpCode(httpResponse.getStatusLine().getStatusCode());
			// 处理返回结果
			if (httpResponse.getStatusLine().getStatusCode() != 200 && httpResponse.getStatusLine().getStatusCode() != 500) {
				System.out.println("返回getStatusCode=" + httpResponse.getStatusLine().getStatusCode());
				if (null != httpPost) {
					httpPost.abort();
				}
				if (null != httpGet) {
					httpGet.abort();
				}
				// 将来可能添加回调
			} else {
				System.out.println("返回getStatusCode=" + httpResponse.getStatusLine().getStatusCode());
				resultString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				System.out.println("返回result=" + resultString);
				// 如果有parser则用parser解析,没有则使用默认的解析方式
				Object object = null;
				if (null != request.getParser()) {
					object = request.getParser().parse(resultString);
				} else {
					try {
						object = new Gson().fromJson(resultString, request.getModelType());
						if (null == object) {
							System.out.println("解析失败");
						}
			 		} catch (Exception e) {
						e.printStackTrace();
					}
				}

				request.setResponseObject((IResponse) object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onPostExecute(IRequest<?> result) {
		super.onPostExecute(result);
		if (null != result && null != result.getOnNetworkCompleteListener()) {
			// 处理成功之后的回调
			OnNetworkCompleteListener onNetworkCompleteListener = result.getOnNetworkCompleteListener();
			boolean isSuccess = false;
			// 如果没有设定Parser,则stateCode为STATE_CODE_SUCCESS时为成功
			
			if(result.isUseCustomRules()) {
				if (null != result.getResponseObject() && result.getResponseObject().getStateCode() == IResponse.STATE_CODE_SUCCESS) {
					isSuccess = true;
				}
			}else {
				if (null != result.getResponseObject()) {
					isSuccess = true;
				}
			}
			
			// 调用回调方法
			if (isSuccess) {
				onNetworkCompleteListener.onNetworkCompleteSuccess(result, resultString);
			} else {
				onNetworkCompleteListener.onNetworkCompleteFailed(result, resultString);
			}

		}

	}

}
