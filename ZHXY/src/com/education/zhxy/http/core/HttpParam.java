package com.education.zhxy.http.core;

import java.util.HashMap;

public class HttpParam {
	private String url;

    private boolean isPost;

    private HashMap<String, String> params;

	public HttpParam(String url, boolean isPost) {
		this.url = url;
		this.isPost = isPost;
	}

	public HashMap<String, String> getParams() {
        return params;
    }
    
    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isPost() {
		return isPost;
	}

	public void setPost(boolean isPost) {
		this.isPost = isPost;
	}
}
