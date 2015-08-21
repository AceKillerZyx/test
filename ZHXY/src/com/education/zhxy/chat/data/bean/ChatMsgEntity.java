
package com.education.zhxy.chat.data.bean;

import java.io.Serializable;

public class ChatMsgEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;

	private int usid;
    
    private String usersname;
    
    private String images;
    
    private String gcDate;

    private String gcContent;
    
    private String postname;
    
    private boolean isComMeg = true;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public int getUsid() {
		return usid;
	}

	public void setUsid(int usid) {
		this.usid = usid;
	}

	public String getUsersname() {
		return usersname;
	}

	public void setUsersname(String usersname) {
		this.usersname = usersname;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getGcDate() {
		return gcDate;
	}

	public void setGcDate(String gcDate) {
		this.gcDate = gcDate;
	}

	public String getGcContent() {
		return gcContent;
	}

	public void setGcContent(String gcContent) {
		this.gcContent = gcContent;
	}

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

	public String getPostname() {
		return postname;
	}

	public void setPostname(String postname) {
		this.postname = postname;
	}
    
}
