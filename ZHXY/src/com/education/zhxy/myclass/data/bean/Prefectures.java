package com.education.zhxy.myclass.data.bean;

import java.io.Serializable;

public class Prefectures implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String city;
	
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
