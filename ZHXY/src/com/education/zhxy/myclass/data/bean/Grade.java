package com.education.zhxy.myclass.data.bean;

import java.io.Serializable;

public class Grade implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String gradeName;
	
	private String studentsname;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public String getStudentsname() {
		return studentsname;
	}

	public void setStudentsname(String studentsname) {
		this.studentsname = studentsname;
	}
	
	@Override
	public String toString() {
		return gradeName;
	}
	
}
