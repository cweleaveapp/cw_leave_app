package com.google.appengine.mct;

import java.io.Serializable;

public class LeaveEntitle  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String addAnnualLeave;
	private String addMaternityLeave;
	private String addBirthdayLeave;
	private String addWeddingLeave;
	private String addCompassionateLeave;
	private String addExGratia;
	//private String compensationLeaveExp;
	private String hospitalization;	

//	public String getExgratiaLeaveExp() {
//		return compensationLeaveExp;
//	}
//	public void setCompensationLeaveExp(String compensationLeaveExp) {
//		this.compensationLeaveExp = compensationLeaveExp;
//	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAddAnnualLeave() {
		return addAnnualLeave;
	}
	public void setAddAnnualLeave(String addAnnualLeave) {
		this.addAnnualLeave = addAnnualLeave;
	}
	public String getAddMaternityLeave() {
		return addMaternityLeave;
	}
	public void setAddMaternityLeave(String addMaternityLeave) {
		this.addMaternityLeave = addMaternityLeave;
	}
	public String getAddBirthdayLeave() {
		return addBirthdayLeave;
	}
	public void setAddBirthdayLeave(String addBirthdayLeave) {
		this.addBirthdayLeave = addBirthdayLeave;
	}
	public String getAddWeddingLeave() {
		return addWeddingLeave;
	}
	public void setAddWeddingLeave(String addWeddingLeave) {
		this.addWeddingLeave = addWeddingLeave;
	}
	public String getAddCompassionateLeave() {
		return addCompassionateLeave;
	}
	public void setAddCompassionateLeave(String addCompassionateLeave) {
		this.addCompassionateLeave = addCompassionateLeave;
	}
	/**
	 * @return the addExGratia
	 */
	public String getAddExGratia() {
		return addExGratia;
	}
	/**
	 * @param addExGratia the addExGratia to set
	 */
	public void setAddExGratia(String addExGratia) {
		this.addExGratia = addExGratia;
	}
	
	public String getHospitalization() {
		return hospitalization;
	}
	public void setHospitalization(String hospitalization) {
		this.hospitalization = hospitalization;
	}
}
