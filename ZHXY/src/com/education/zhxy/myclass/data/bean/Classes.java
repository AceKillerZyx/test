package com.education.zhxy.myclass.data.bean;

import java.io.Serializable;

public class Classes implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String className;
	
	private String gradename;
	
	private String sendWord;
	
	private String slogan;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getGradename() {
		return gradename;
	}

	public void setGradename(String gradename) {
		this.gradename = gradename;
	}

	public String getSendWord() {
		return sendWord;
	}

	public void setSendWord(String sendWord) {
		this.sendWord = sendWord;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}
	
	@Override
	public String toString() {
		return className;
	}

}
