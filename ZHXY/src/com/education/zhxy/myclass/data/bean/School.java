package com.education.zhxy.myclass.data.bean;

import java.io.Serializable;

public class School implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String countyname;
	
	private String stuAdderss;
	
	private String stuNames;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountyname() {
		return countyname;
	}

	public void setCountyname(String countyname) {
		this.countyname = countyname;
	}

	public String getStuAdderss() {
		return stuAdderss;
	}

	public void setStuAdderss(String stuAdderss) {
		this.stuAdderss = stuAdderss;
	}

	public String getStuNames() {
		return stuNames;
	}

	public void setStuNames(String stuNames) {
		this.stuNames = stuNames;
	}
	
	@Override
	public String toString() {
		return stuNames;
	}

}
