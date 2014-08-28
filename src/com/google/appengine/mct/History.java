package com.google.appengine.mct;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.util.ConstantUtils;

public class History implements Serializable, Comparator<History> {

	private static final long serialVersionUID = 1L;

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
	private String status;
	private String projectName;
	private String changeType;
	private String createdBy;
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
		String day = time.substring(0, 2);
		String month = time.substring(3, 5);
		String year = time.substring(6, 10);
		String hour = time.substring(11, 19);
		return year+"-"+month+"-"+day+" "+hour;
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
		if(StringUtils.isNotBlank(startDate)){
			String day = startDate.substring(0, 2);
			String month = startDate.substring(3, 5);
			String year = startDate.substring(6, 10);
			return year+"-"+month+"-"+day;
		}
		return startDate;
		
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		if(StringUtils.isNotBlank(endDate)){
		String day = endDate.substring(0, 2);
		String month = endDate.substring(3, 5);
		String year = endDate.substring(6, 10);
		return year+"-"+month+"-"+day;
		}
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
	
	public static Comparator<History> dateComparator = new Comparator<History>() {
		 
        @Override
        public int compare(History e1, History e2) {
        	return e1.getTime().compareTo(e2.getTime());
        }
    };
	
	
	@Override
	public int compare(History o1, History o2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
