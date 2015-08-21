package com.education.zhxy.train.data.bean;

import java.io.Serializable;

public class Train implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String tsImages;
	
	private String tsName;
	
	private String tsIntro;
	
	private String tsAdderss;
	
	private String tsTel;
	
	private Double stuLatitude;
	
	private Double stuLongitude;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTsImages() {
		return tsImages;
	}

	public void setTsImages(String tsImages) {
		this.tsImages = tsImages;
	}

	public String getTsName() {
		return tsName;
	}

	public void setTsName(String tsName) {
		this.tsName = tsName;
	}

	public String getTsIntro() {
		return tsIntro;
	}

	public void setTsIntro(String tsIntro) {
		this.tsIntro = tsIntro;
	}

	public String getTsAdderss() {
		return tsAdderss;
	}

	public void setTsAdderss(String tsAdderss) {
		this.tsAdderss = tsAdderss;
	}

	public String getTsTel() {
		return tsTel;
	}

	public void setTsTel(String tsTel) {
		this.tsTel = tsTel;
	}

	public Double getStuLatitude() {
		return stuLatitude;
	}

	public void setStuLatitude(Double stuLatitude) {
		this.stuLatitude = stuLatitude;
	}

	public Double getStuLongitude() {
		return stuLongitude;
	}

	public void setStuLongitude(Double stuLongitude) {
		this.stuLongitude = stuLongitude;
	}
	
}
