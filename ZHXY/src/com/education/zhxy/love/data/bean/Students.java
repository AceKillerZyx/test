package com.education.zhxy.love.data.bean;

import java.io.Serializable;

public class Students implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String adderss;
	
	private int age;
	
	private String sex;
	
	private String images;
	
	private String names;
	
	private String poor;
	
	private String poorContent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdderss() {
		return adderss;
	}

	public void setAdderss(String adderss) {
		this.adderss = adderss;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getPoor() {
		return poor;
	}

	public void setPoor(String poor) {
		this.poor = poor;
	}

	public String getPoorContent() {
		return poorContent;
	}

	public void setPoorContent(String poorContent) {
		this.poorContent = poorContent;
	}
	
}
