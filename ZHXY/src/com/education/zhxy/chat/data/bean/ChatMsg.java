package com.education.zhxy.chat.data.bean;

import java.io.Serializable;

public class ChatMsg implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private int usersid;
	
	private String usersimage;
	
	private String usersname;
	
	private String haDate;
	
	private String haContent;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsersid() {
		return usersid;
	}

	public void setUsersid(int usersid) {
		this.usersid = usersid;
	}

	public String getUsersimage() {
		return usersimage;
	}

	public void setUsersimage(String usersimage) {
		this.usersimage = usersimage;
	}

	public String getUsersname() {
		return usersname;
	}

	public void setUsersname(String usersname) {
		this.usersname = usersname;
	}

	public String getHaDate() {
		return haDate;
	}

	public void setHaDate(String haDate) {
		this.haDate = haDate;
	}

	public String getHaContent() {
		return haContent;
	}

	public void setHaContent(String haContent) {
		this.haContent = haContent;
	}
	
}
