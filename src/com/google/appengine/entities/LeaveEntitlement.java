package com.google.appengine.entities;

import java.io.Serializable;

public class LeaveEntitlement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;

	private String addBirthdayLeave;
	
	private String addCompassionateLeave;

	private String addExaminationLeave;

	private String addInjuryLeave;

	private String addJuryLeave;

	private String addExGratia;

	private String addMarriageLeave;

	private String addMaternityLeave;

	private String addPaternityLeave;

	private String addFPSickLeave;

	private String addPPSickLeave;

	private String leaveYear;
	
	public LeaveEntitlement(String leaveYear) {
		this.leaveYear = leaveYear;
	}	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddBirthdayLeave() {
		return addBirthdayLeave;
	}

	public void setAddBirthdayLeave(String addBirthdayLeave) {
		this.addBirthdayLeave = addBirthdayLeave;
	}

	public String getAddCompassionateLeave() {
		return addCompassionateLeave;
	}

	public void setAddCompassionateLeave(String addCompassionateLeave) {
		this.addCompassionateLeave = addCompassionateLeave;
	}

	public String getAddExaminationLeave() {
		return addExaminationLeave;
	}

	public void setAddExaminationLeave(String addExaminationLeave) {
		this.addExaminationLeave = addExaminationLeave;
	}

	public String getAddInjuryLeave() {
		return addInjuryLeave;
	}

	public void setAddInjuryLeave(String addInjuryLeave) {
		this.addInjuryLeave = addInjuryLeave;
	}

	public String getAddJuryLeave() {
		return addJuryLeave;
	}

	public void setAddJuryLeave(String addJuryLeave) {
		this.addJuryLeave = addJuryLeave;
	}

	public String getAddExGratia() {
		return addExGratia;
	}

	public void setAddExGratia(String addExGratia) {
		this.addExGratia = addExGratia;
	}

	public String getAddMarriageLeave() {
		return addMarriageLeave;
	}

	public void setAddMarriageLeave(String addMarriageLeave) {
		this.addMarriageLeave = addMarriageLeave;
	}

	public String getAddMaternityLeave() {
		return addMaternityLeave;
	}

	public void setAddMaternityLeave(String addMaternityLeave) {
		this.addMaternityLeave = addMaternityLeave;
	}

	public String getAddPaternityLeave() {
		return addPaternityLeave;
	}

	public void setAddPaternityLeave(String addPaternityLeave) {
		this.addPaternityLeave = addPaternityLeave;
	}

	public String getAddFPSickLeave() {
		return addFPSickLeave;
	}

	public void setAddFPSickLeave(String addFPSickLeave) {
		this.addFPSickLeave = addFPSickLeave;
	}

	public String getAddPPSickLeave() {
		return addPPSickLeave;
	}

	public void setAddPPSickLeave(String addPPSickLeave) {
		this.addPPSickLeave = addPPSickLeave;
	}

	public String getLeaveYear() {
		return leaveYear;
	}

	public void setLeaveYear(String leaveYear) {
		this.leaveYear = leaveYear;
	}
}