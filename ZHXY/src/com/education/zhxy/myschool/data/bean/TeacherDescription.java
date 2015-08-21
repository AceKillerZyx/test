package com.education.zhxy.myschool.data.bean;

import java.io.Serializable;

public class TeacherDescription implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int tid;
	
	private String tnames;//称号 ：骨干
	
	private String tworkingtime;//从业年限
	
	private String tculture;//学历：博士
	
	private String tcertificate;//证书编号
	
	private String greadname;
	
	private int userage;
	
	private String usimage;
	
	private String usersname;

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getTnames() {
		return tnames;
	}

	public void setTnames(String tnames) {
		this.tnames = tnames;
	}

	public String getTworkingtime() {
		return tworkingtime;
	}

	public void setTworkingtime(String tworkingtime) {
		this.tworkingtime = tworkingtime;
	}

	public String getTculture() {
		return tculture;
	}

	public void setTculture(String tculture) {
		this.tculture = tculture;
	}

	public String getTcertificate() {
		return tcertificate;
	}

	public void setTcertificate(String tcertificate) {
		this.tcertificate = tcertificate;
	}

	public String getGreadname() {
		return greadname;
	}

	public void setGreadname(String greadname) {
		this.greadname = greadname;
	}

	public int getUserage() {
		return userage;
	}

	public void setUserage(int userage) {
		this.userage = userage;
	}

	public String getUsimage() {
		return usimage;
	}

	public void setUsimage(String usimage) {
		this.usimage = usimage;
	}

	public String getUsersname() {
		return usersname;
	}

	public void setUsersname(String usersname) {
		this.usersname = usersname;
	}

	
}
