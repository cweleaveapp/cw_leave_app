package com.google.appengine.entities;

import java.io.Serializable;

public class Supervisor implements Serializable {
	
	private String id;
	private String emailAddress;
	private String dept;
	private String fullName;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the dept
	 */
	public String getDepartment() {
		return dept;
	}

	/**
	 * @param dept the dept to set
	 */
	public void setDepartment(String dept) {
		this.dept = dept;
	}
	
	
}
