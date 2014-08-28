package com.google.appengine.enums;

/**
 * The enumeration represents leave status
 *
 */
public enum LeaveStatus {
	
	PENDING_REVIEW (1,"Pending", "Pending"),	
	SUPERVISOR_APPROVED (2, "Approved by Supervisor", "Supervisor Approved"),
	SUPERVISOR_REJECTED (3, "Rejected by Supervisor", "Supervisor Rejected"),
	ACTING_APPROVED (4, "Approved by Actor", "Actor Approved"),
	ACTING_REJECTED (5, "Rejected by Actor", "Actor Rejected"),
	APPROVER_APPROVED (6, "Approved by Site/Department Approver", "Approver Approved"),
	APPROVER_REJECTED (7, "Rejected by Site/Department Approver", "Approver Rejected"),
	HR_APPROVED(8, "Approved by HR", "HR Approved"),
	HR_REJECTED(9, "Rejected by HR", "HR Rejected"),
	CANCELLED(10, "Cancelled", "Cancelled"),
	//reserved 
	EXPIRED(11, "Expired", "Expired");
	

	public final int id;
	
	public final String leaveStatus;
	
	public final String abbreviation;


	private LeaveStatus(int id, String leaveStatus, String abbreviation) {
		this.id = id;
		this.leaveStatus = leaveStatus;
		this.abbreviation = abbreviation;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the leaveStatus
	 */
	public String getLeaveStatus() {
		return leaveStatus;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
	
}
