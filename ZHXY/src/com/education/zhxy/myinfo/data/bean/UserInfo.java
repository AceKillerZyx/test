package com.education.zhxy.myinfo.data.bean;

import java.io.Serializable;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private int poosid;

	private String image;

	private String usName;

	private String usSex;

	private String usAge;

	private String rolename;

	private String usConstellation;

	private String usPersonalNote;

	private String classname;

	private String usAdderss;

	private String usTel;

	public String getUsTel() {
		return usTel;
	}

	public void setUsTel(String usTel) {
		this.usTel = usTel;
	}

	private String msgsl;// 消息未读标示

	private String postname;// 角色

	/**
	 * 获取角色名称
	 * 
	 * @return
	 */
	public String getPostname() {
		return postname;
	}

	public void setPostname(String postname) {
		this.postname = postname;
	}

	public String getMsgsl() {
		return msgsl;
	}

	public void setMsgsl(String msgsl) {
		this.msgsl = msgsl;
	}

	private String tel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPoosid() {
		return poosid;
	}

	public void setPoosid(int poosid) {
		this.poosid = poosid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUsName() {
		return usName;
	}

	public void setUsName(String usName) {
		this.usName = usName;
	}

	public String getUsSex() {
		return usSex;
	}

	public void setUsSex(String usSex) {
		this.usSex = usSex;
	}

	public String getUsAge() {
		return usAge;
	}

	public void setUsAge(String usAge) {
		this.usAge = usAge;
	}

	public String getUsConstellation() {
		return usConstellation;
	}

	public void setUsConstellation(String usConstellation) {
		this.usConstellation = usConstellation;
	}

	public String getUsPersonalNote() {
		return usPersonalNote;
	}

	public void setUsPersonalNote(String usPersonalNote) {
		this.usPersonalNote = usPersonalNote;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getUsAdderss() {
		return usAdderss;
	}

	public void setUsAdderss(String usAdderss) {
		this.usAdderss = usAdderss;
	}

	/*
	 * public String getUsTel() { return usTel; }
	 * 
	 * public void setUsTel(String usTel) { this.usTel = usTel; }
	 */

	public String getRolename() {
		return rolename;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

}
