package com.education.zhxy.parents.must.read.data.bean;

import java.io.Serializable;

public class ParentsRead implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String names;
	
	private String images;
	
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
