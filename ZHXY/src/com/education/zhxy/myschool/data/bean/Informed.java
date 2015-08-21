package com.education.zhxy.myschool.data.bean;

import java.io.Serializable;

public class Informed implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int nid;
	
	private String nimage;
	
	private String ncontent;
	
	private String ndate;

	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}

	public String getNimage() {
		return nimage;
	}

	public void setNimage(String nimage) {
		this.nimage = nimage;
	}

	public String getNcontent() {
		return ncontent;
	}

	public void setNcontent(String ncontent) {
		this.ncontent = ncontent;
	}

	public String getNdate() {
		return ndate;
	}

	public void setNdate(String ndate) {
		this.ndate = ndate;
	}

}
