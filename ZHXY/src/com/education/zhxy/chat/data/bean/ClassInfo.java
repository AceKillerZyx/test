package com.education.zhxy.chat.data.bean;

import java.io.Serializable;
import java.util.List;

import com.education.zhxy.myinfo.data.bean.UserInfo;

public class ClassInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String className;
	
	private String slogan;
	
	private String sendWord;
	
	private String tel;
	
	private List<UserInfo> list;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getSendWord() {
		return sendWord;
	}

	public void setSendWord(String sendWord) {
		this.sendWord = sendWord;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public List<UserInfo> getList() {
		return list;
	}

	public void setList(List<UserInfo> list) {
		this.list = list;
	}
	
}
