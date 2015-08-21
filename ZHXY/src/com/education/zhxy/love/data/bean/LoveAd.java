package com.education.zhxy.love.data.bean;

import java.io.Serializable;

public class LoveAd implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String images;
	
	private String title;
	
	private String details;
	
	private String describe;
	
	private String activityDate;
	
	private String activityHtp;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}

	public String getActivityHtp() {
		return activityHtp;
	}

	public void setActivityHtp(String activityHtp) {
		this.activityHtp = activityHtp;
	}
	
}
