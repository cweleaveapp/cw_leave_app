package com.google.appengine.entities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.enums.LeaveType;

public class LeaveQueue implements Serializable , Comparator<LeaveQueue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5142209570809821806L;
	private String id;
	private Date createDate;
	private LeaveRequest leaveRequest;
	private LeaveType type;
	private String approvedBy;
	private List<String> attachments;
	// use LeaveType enum instead
	private String leaveType;
	private String time;
	private String emailAdd;
	private String supervisor;
	private String numOfDays;
	private String startDate;
	private String endDate;	
	private String remark;
	private String projectName;
	private String changeType;
	private String approveId;
	private String attachmentUrl;
	@Transient String startDateBean;
	@Transient String endDateBean;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public LeaveRequest getLeaveRequest() {
		return leaveRequest;
	}

	public void setLeaveRequest(LeaveRequest leaveRequest) {
		this.leaveRequest = leaveRequest;
	}

	public LeaveType getType() {
		return type;
	}

	public void setType(int typeId) {
		for(LeaveType t : LeaveType.values()){
			if(t.getId()==typeId){
				this.type = t;
			}
		}		
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public String getStartDateBean() {
		if(StringUtils.isNotBlank(startDateBean)){
			String day = startDateBean.substring(0, 2);
			String month = startDateBean.substring(3, 5);
			String year = startDateBean.substring(6, 10);
			return year+"-"+month+"-"+day;
		}
		return startDateBean;
	}

	public void setStartDateBean(String startDateBean) {
		this.startDateBean = startDateBean;
	}

	public String getEndDateBean() {
		if(StringUtils.isNotBlank(endDateBean)){
			String day = endDateBean.substring(0, 2);
			String month = endDateBean.substring(3, 5);
			String year = endDateBean.substring(6, 10);
			return year+"-"+month+"-"+day;
		}
		return endDateBean;
	}

	public void setEndDateBean(String endDateBean) {
		this.endDateBean = endDateBean;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getApproveId() {
		return approveId;
	}

	public void setApproveId(String approveId) {
		this.approveId = approveId;
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
	
	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
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

	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public static Comparator<LeaveQueue> dateComparator = new Comparator<LeaveQueue>() {
		 
        @Override
        public int compare(LeaveQueue e1, LeaveQueue e2) {
        	return e1.getTime().compareTo(e2.getTime());
        }
    };
	
	
	@Override
	public int compare(LeaveQueue o1, LeaveQueue o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
