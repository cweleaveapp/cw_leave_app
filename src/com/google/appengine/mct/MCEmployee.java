package com.google.appengine.mct;

import java.io.Serializable;

public class MCEmployee implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String emailAddress;
	private String fullName;
	private String region;
	private String hiredDate;
	private String birthDate;
	private String resignedDate;
	private String supervisor;
	private String jobTitle;

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

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
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getHiredDate() {
		return hiredDate;
	}

	public void setHiredDate(String hiredDate) {
		this.hiredDate = hiredDate;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getResignedDate() {
		return resignedDate;
	}

	public void setResignedDate(String resignedDate) {
		this.resignedDate = resignedDate;
	}
    
}
