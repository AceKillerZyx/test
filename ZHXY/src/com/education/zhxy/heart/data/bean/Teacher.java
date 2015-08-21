package com.education.zhxy.heart.data.bean;

import java.io.Serializable;

public class Teacher implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int usid;
	
	private int HId;
	
	private String HName;
	
	private String HWorkingtime;
	
	private String HSex;
	
	private String HCulture;
	
	private String HCertificate;//从业年限：8
	
	private String HAptitude;//称号 ：骨干
	
	private String HGrade;
	
	private int HAge;
	
	private String HImage;
	
	public String getHGrade() {
		return HGrade;
	}

	public void setHGrade(String hGrade) {
		HGrade = hGrade;
	}

	public int getUsid() {
		return usid;
	}

	public void setUsid(int usid) {
		this.usid = usid;
	}

	public int getHId() {
		return HId;
	}

	public void setHId(int hId) {
		HId = hId;
	}

	public String getHName() {
		return HName;
	}

	public void setHName(String hName) {
		HName = hName;
	}

	public String getHWorkingtime() {
		return HWorkingtime;
	}

	public void setHWorkingtime(String hWorkingtime) {
		HWorkingtime = hWorkingtime;
	}

	public String getHSex() {
		return HSex;
	}

	public void setHSex(String hSex) {
		HSex = hSex;
	}

	public String getHCulture() {
		return HCulture;
	}

	public void setHCulture(String hCulture) {
		HCulture = hCulture;
	}

	public String getHCertificate() {
		return HCertificate;
	}

	public void setHCertificate(String hCertificate) {
		HCertificate = hCertificate;
	}

	public String getHAptitude() {
		return HAptitude;
	}

	public void setHAptitude(String hAptitude) {
		HAptitude = hAptitude;
	}

	public int getHAge() {
		return HAge;
	}

	public void setHAge(int hAge) {
		HAge = hAge;
	}

	public String getHImage() {
		return HImage;
	}

	public void setHImage(String hImage) {
		HImage = hImage;
	}

}
