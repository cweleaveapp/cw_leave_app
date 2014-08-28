package com.google.appengine.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.enums.LeaveStatus;
import com.google.appengine.enums.LeaveType;

public class LeaveRequest implements Serializable {

	public String id;
	
	public Key empKey;
	
	public LeaveType leaveType;
	
	public LeaveStatus leaveStatus;
	
	public String supervisor;
	
	public String approver;
	
	public String start;
	
	public String end;
	
	public double noOfDays;

	public List<String> attachments;

	public List<String> blobKeys;
	
	public String remarks;	
	
	public String ref;
	
	public Date approveDate;
	
	public Date createDate;
	
	public Date lastUpdate;
	
	public String startDayHalf; 	
	
	public String endDayHalf;
	
	public String satOffs;
	
	public double exGratiaClaim;
	
	public String bDayOffClaim;
	
	public double totalClaims;
	
	public List<ActingPerson> apList;
	
	public String reason;
	
	public LeaveRequest(Key empKey, int leaveTypeId, String supervisor, String approver, String start,
			String end, double noOfDays, String remarks, String ref) {
		this.empKey = empKey;
		setLeaveType(leaveTypeId);
		this.supervisor = supervisor;
		this.approver = approver;
		this.start = start;
		this.end = end;
		this.noOfDays = noOfDays;
		this.remarks = remarks;
		this.ref = ref;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the empKey
	 */
	public Key getEmpKey() {
		return empKey;
	}

	/**
	 * @param empKey the empKey to set
	 */
	public void setEmpKey(Key empKey) {
		this.empKey = empKey;
	}

	/**
	 * @return the leaveType
	 */
	public LeaveType getLeaveType() {
		return leaveType;
	}

	/**
	 * @param leaveType the leaveType to set
	 */
	public void setLeaveType(int typeId) {
		for(LeaveType type : LeaveType.values()){
			if(type.getId() == typeId){
				this.leaveType = type;
			}
		}
	}

	/**
	 * @return the leaveStatus
	 */
	public LeaveStatus getLeaveStatus() {
		return leaveStatus;
	}

	/**
	 * @param leaveStatus the leaveStatus to set
	 */
	public void setLeaveStatus(int leaveStatus) {
		for(LeaveStatus s: LeaveStatus.values()) {
			if(leaveStatus == s.id){
				this.leaveStatus = s;
			}
		}		
	}

	/**
	 * @return the supervisor
	 */
	public String getSupervisor() {
		return supervisor;
	}

	/**
	 * @param supervisor the supervisor to set
	 */
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	/**
	 * @return the approver
	 */
	public String getApprover() {
		return approver;
	}

	/**
	 * @param approver the approver to set
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * @return the noOfDays
	 */
	public double getNoOfDays() {
		return noOfDays;
	}

	/**
	 * @param noOfDays the noOfDays to set
	 */
	public void setNoOfDays(double noOfDays) {
		this.noOfDays = noOfDays;
	}

	/**
	 * @return the attachment
	 */
	public List<String> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachments(String attachment) {
		if(!this.attachments.contains(attachment)){
			this.attachments.add(attachment);
		}		
	}
	
	public void setAttachments(List<String> attachments){
		this.attachments = attachments;
	}
	
	/**
	 * @return the blobKeys
	 */
	public List<String> getBlobKeys() {
		return blobKeys;
	}

	/**
	 * @param blobKeys the blobKeys to set
	 */
	public void setBlobKeys(String blobkey) {
		if(!this.blobKeys.contains(blobkey)){
			this.blobKeys.add(blobkey);
		}		
	}	
	
	public void setBlobKeys(List<String> blobkeys) {
		this.blobKeys = blobkeys;
	}
	
	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}

	/**
	 * @return the approveDate
	 */
	public Date getApproveDate() {
		return approveDate;
	}

	/**
	 * @param approveDate the approveDate to set
	 */
	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the startDayHalf
	 */
	public String getStartDayHalf() {
		return startDayHalf;
	}

	/**
	 * @param startDayHalf the startDayHalf to set
	 */
	public void setStartDayHalf(String startDayHalf) {
		this.startDayHalf = startDayHalf;
	}

	/**
	 * @return the endDayHalf
	 */
	public String getEndDayHalf() {
		return endDayHalf;
	}

	/**
	 * @param endDayHalf the endDayHalf to set
	 */
	public void setEndDayHalf(String endDayHalf) {
		this.endDayHalf = endDayHalf;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the satOffs
	 */
	public String getSatOffs() {
		return satOffs;
	}

	/**
	 * @param satOffs the satOffs to set
	 */
	public void setSatOffs(String satOffs) {
		this.satOffs = satOffs;
	}

	/**
	 * @return the exGratiaClaim
	 */
	public double getExGratiaClaim() {
		return exGratiaClaim;
	}

	/**
	 * @param exGratiaClaim the exGratiaClaim to set
	 */
	public void setExGratiaClaim(double exGratiaClaim) {
		this.exGratiaClaim = exGratiaClaim;
	}

	/**
	 * @return the bDayOffClaim
	 */
	public String getbDayOffClaim() {
		return bDayOffClaim;
	}

	/**
	 * @param bDayOffClaim the bDayOffClaim to set
	 */
	public void setbDayOffClaim(String bDayOffClaim) {
		this.bDayOffClaim = bDayOffClaim;
	}

	/**
	 * @return the totalClaims
	 */
	public double getTotalClaims() {
		return totalClaims;
	}

	/**
	 * @param totalClaims the totalClaims to set
	 */
	public void setTotalClaims(double totalClaims) {
		this.totalClaims = totalClaims;
	}
	
	public List<ActingPerson> getApList() {
		return apList;
	}	

	public void setApList(List<ActingPerson> apList) {
		this.apList = apList;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
