package com.education.zhxy.http.core;

public interface HttpListener {
	public void noNet(HttpTask task);

	public void noData(HttpTask task, HttpResult result);

	public void onLoadFailed(HttpTask task, HttpResult result);

	public void onLoadFinish(HttpTask task, HttpResult result);
}
