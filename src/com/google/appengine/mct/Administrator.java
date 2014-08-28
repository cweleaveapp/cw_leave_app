package com.google.appengine.mct;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Administrator implements Serializable {

	private String id;
	private String emailAddress;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
}
