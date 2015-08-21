package com.education.zhxy.myinfo.data.bean;

import java.io.Serializable;

public class Job implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String postName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}
	
	@Override
	public String toString() {
		return postName;
	}

}
