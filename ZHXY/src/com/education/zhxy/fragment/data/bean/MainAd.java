package com.education.zhxy.fragment.data.bean;

import java.io.Serializable;

public class MainAd implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String advConten;
	
	private String advImage;
	
	private String advTitle;
	
	private String advHtp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdvConten() {
		return advConten;
	}

	public void setAdvConten(String advConten) {
		this.advConten = advConten;
	}

	public String getAdvImage() {
		return advImage;
	}

	public void setAdvImage(String advImage) {
		this.advImage = advImage;
	}

	public String getAdvTitle() {
		return advTitle;
	}

	public void setAdvTitle(String advTitle) {
		this.advTitle = advTitle;
	}

	public String getAdvHtp() {
		return advHtp;
	}

	public void setAdvHtp(String advHtp) {
		this.advHtp = advHtp;
	}
	
}
