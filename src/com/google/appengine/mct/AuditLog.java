package com.google.appengine.mct;

import java.io.Serializable;
import java.util.Date;

public class AuditLog implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private Date time;
	private String name;
	private String emailAddress;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
}
