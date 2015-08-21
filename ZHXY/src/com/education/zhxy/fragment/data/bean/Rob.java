package com.education.zhxy.fragment.data.bean;

import java.io.Serializable;

public class Rob implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String images;
	
	private String name;
	
	private int number;
	
	private String startRobdate;
	
	private String strtTime;
	
	private String text1;
	
	private String text2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getStartRobdate() {
		return startRobdate;
	}

	public void setStartRobdate(String startRobdate) {
		this.startRobdate = startRobdate;
	}

	public String getStrtTime() {
		return strtTime;
	}

	public void setStrtTime(String strtTime) {
		this.strtTime = strtTime;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}
	
}
