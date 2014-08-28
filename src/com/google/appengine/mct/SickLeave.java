package com.google.appengine.mct;

import java.io.Serializable;

public class SickLeave  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String region;
	private String leaveEntitleId;
	private String sickLeaveYear;
	private String sickLeaveType;
	private String sickLeaveDay;
	
	
	public String getSickLeaveYear() {
		return sickLeaveYear;
	}
	public void setSickLeaveYear(String sickLeaveYear) {
		this.sickLeaveYear = sickLeaveYear;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getLeaveEntitleId() {
		return leaveEntitleId;
	}
	public void setLeaveEntitleId(String leaveEntitleId) {
		this.leaveEntitleId = leaveEntitleId;
	}
	
	public String getSickLeaveType() {
		return sickLeaveType;
	}
	public void setSickLeaveType(String sickLeaveType) {
		this.sickLeaveType = sickLeaveType;
	}
	public String getSickLeaveDay() {
		return sickLeaveDay;
	}
	public void setSickLeaveDay(String sickLeaveDay) {
		this.sickLeaveDay = sickLeaveDay;
	}

	
}
