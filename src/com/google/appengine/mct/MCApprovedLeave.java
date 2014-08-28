package com.google.appengine.mct;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

public class MCApprovedLeave implements Serializable , Comparator<MCApprovedLeave>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5072153444230639674L;
	
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
	private String changeType;
	private String attachmentUrl;
	private String projectName;
	@Transient String timeBean;
	@Transient String startDateBean;
	@Transient String endDateBean;
	
	public String getStartDateBean() {
		return startDateBean;
	}

	public void setStartDateBean(String startDateBean) {
		this.startDateBean = startDateBean;
	}

	public String getEndDateBean() {
		return endDateBean;
	}

	public void setEndDateBean(String endDateBean) {
		this.endDateBean = endDateBean;
	}

	public String getTimeBean() {
		return timeBean;
	}

	public void setTimeBean(String timeBean) {
		this.timeBean = timeBean;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
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
	
	public static Comparator<MCApprovedLeave> dateComparator = new Comparator<MCApprovedLeave>() {
		 
        @Override
        public int compare(MCApprovedLeave e1, MCApprovedLeave e2) {
        	return e1.getTime().compareTo(e2.getTime());
        }
    };

	@Override
	public int compare(MCApprovedLeave o1, MCApprovedLeave o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
