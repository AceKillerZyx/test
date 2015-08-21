package com.education.zhxy.fragment.data.bean;

import java.io.Serializable;

public class MainDay implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String english;
	
	private String eveDate;
	
	private String translate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getEveDate() {
		return eveDate;
	}

	public void setEveDate(String eveDate) {
		this.eveDate = eveDate;
	}

	public String getTranslate() {
		return translate;
	}

	public void setTranslate(String translate) {
		this.translate = translate;
	}

}
