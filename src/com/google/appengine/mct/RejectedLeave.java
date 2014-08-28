package com.google.appengine.mct;

import java.io.Serializable;

public class RejectedLeave implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3477456254369359782L;
	private String id;
	private String time;
	private String emailAdd;
	private String numOfDays;
	private String startDate;
	private String endDate;
	private String leaveType;
	private String supervisor;
	private String remark;
	private String region;
	
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getEmailAdd() {
		return emailAdd;
	}
	
	public void setEmailAdd(String emailAdd) {
		this.emailAdd = emailAdd;
	}
	
	public String getNumOfDays() {
		return numOfDays;
	}
	
	public void setNumOfDays(String numOfDays) {
		this.numOfDays = numOfDays;
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getLeaveType() {
		return leaveType;
	}
	
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	
	public String getSupervisor() {
		return supervisor;
	}
	
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
