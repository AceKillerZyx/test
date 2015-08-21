package com.education.zhxy.http.core;

public class HttpResult {
	public static final int STATUS_FAILED = -1;

	public static final int STATUS_SUCCESS = 0;

	public static final int STATUS_NO_DATA = 1;

	private int status;

	private String data;

	private String errorMsg;

	private HttpTask ower;

	public HttpResult(HttpTask ower) {
		this.ower = ower;
		status = STATUS_FAILED;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
		this.status = STATUS_SUCCESS;
	}

	public void setNoData() {
		status = STATUS_NO_DATA;
	}

	public void setError() {
		status = STATUS_FAILED;
	}

	public HttpTask getOwer() {
		return ower;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
