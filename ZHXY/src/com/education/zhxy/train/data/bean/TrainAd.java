package com.education.zhxy.train.data.bean;

import java.io.Serializable;

public class TrainAd implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String taImages;
	
	private String taName;
	
	private String taIntro;
	
	private String taAdderss;
	
	private String taTel;
	
	private String stuHtp;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTaImages() {
		return taImages;
	}

	public void setTaImages(String taImages) {
		this.taImages = taImages;
	}

	public String getTaName() {
		return taName;
	}

	public void setTaName(String taName) {
		this.taName = taName;
	}

	public String getTaIntro() {
		return taIntro;
	}

	public void setTaIntro(String taIntro) {
		this.taIntro = taIntro;
	}

	public String getTaAdderss() {
		return taAdderss;
	}

	public void setTaAdderss(String taAdderss) {
		this.taAdderss = taAdderss;
	}

	public String getTaTel() {
		return taTel;
	}

	public void setTaTel(String taTel) {
		this.taTel = taTel;
	}

	public String getStuHtp() {
		return stuHtp;
	}

	public void setStuHtp(String stuHtp) {
		this.stuHtp = stuHtp;
	}
	
}
