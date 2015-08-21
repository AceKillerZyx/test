package com.education.zhxy.http.core;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.education.zhxy.util.NetUtil;
import com.education.zhxy.util.StringUtil;

@SuppressLint("UseValueOf") 
public class HttpTask extends AsyncTask<HttpParam, Integer, HttpResult> {
	private static final String TAG = HttpTask.class.getSimpleName();

	private Context context;

	private HttpListener listener;

	public HttpTask(Context context, HttpListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		if (NetUtil.noNet(context)) {
			if (null != listener) {
				listener.noNet(this);
			}
			this.cancel(true);
		}
		super.onPreExecute();
	}

	/**
	 * 拼装post请求Entity
	 * 
	 * @param param
	 * @return
	 */
	private UrlEncodedFormEntity getEntity(HttpParam param) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HashMap<String, String> hashMap = param.getParams();
		if (null != hashMap) {
			Set<String> keyset = hashMap.keySet();
			for (String key : keyset) {
				String value = hashMap.get(key);
				nameValuePairs.add(new BasicNameValuePair(key, value));
			}
		}

		UrlEncodedFormEntity entity = null;

		try {
			entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
		} catch (Exception e) {
			Log.e(TAG, "getEntity error.");
		}

		return entity;
	}

	/**
	 * 拼装get请求Url
	 * 
	 * @param param
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String getUrl(HttpParam param) {
		StringBuffer url = new StringBuffer();
		url.append(param.getUrl());

		HashMap<String, String> hashMap = param.getParams();
		if (null != hashMap) {
			Set<String> keyset = hashMap.keySet();
			boolean isFirstParam = true;
			for (String key : keyset) {
				url.append(isFirstParam ? StringUtil.PROBLEM : StringUtil.AND)
						.append(URLEncoder.encode(key))
						.append(StringUtil.EQUALS)
						.append(URLEncoder.encode(hashMap.get(key)));
				isFirstParam = false;
			}
		}

		return url.toString();
	}

	@Override
	protected HttpResult doInBackground(HttpParam... params) {
		HttpResult result = new HttpResult(this);
		try {
			if (null != params[0]) {
				HttpResponse response;
				HttpParam param = params[0];
				HttpClient client = HttpClientManager.getNewInstance(context);
				if (param.isPost()) {
					Log.i(TAG,"http post url :"+ param.getUrl() + "--   context :"+ HttpClientManager.getClientContext(context));
					HttpPost post = new HttpPost(param.getUrl());
					post.setEntity(getEntity(param));
					post.addHeader("charset", HTTP.UTF_8);
					post.addHeader("Accept-Language", "zh-cn,zh;q=0.5");
					post.addHeader("Requested-With", "XMLHttpRequest");
					response = client.execute(post,HttpClientManager.getClientContext(context));
				} else {
					Log.i(TAG,"http get url :" + getUrl(param) + "--   context :" + HttpClientManager.getClientContext(context));
					HttpGet get = new HttpGet(getUrl(param));
					get.addHeader("charset", HTTP.UTF_8);
					get.addHeader("Accept-Language", "zh-cn,zh;q=0.5");
					get.addHeader("Requested-With", "XMLHttpRequest");
					response = client.execute(get,HttpClientManager.getClientContext(context));
				}

				if (null != response && 200 == response.getStatusLine().getStatusCode()) {
					String entity = EntityUtils.toString(response.getEntity()); // 修改输出出现乱码情况
					result.setData(entity);
				} else {
					result.setError();
				}

				if (null != response) {
					Log.i(TAG, "http statusCode : " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (java.io.IOException e) {
			Log.e(TAG, e.getMessage(), e);
			result.setStatus(HttpResult.STATUS_FAILED);
			result.setErrorMsg("网络异常，请确认是否连接网络!");
		}

		return result;
	}

	@Override
	protected void onPostExecute(HttpResult result) {
		super.onPostExecute(result);
		if (null != listener) {
			switch (result.getStatus()) {
			case HttpResult.STATUS_FAILED:
				listener.onLoadFailed(this, result);
				break;
			case HttpResult.STATUS_NO_DATA:
				listener.noData(this, result);
				break;
			case HttpResult.STATUS_SUCCESS:
				listener.onLoadFinish(this, result);
				break;
			default:
				break;
			}
		}
	}
}
