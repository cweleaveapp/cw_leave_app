package com.google.appengine.entities;

import java.io.Serializable;

public class EmployeeLeaveDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5651952912747478738L;
	private String id;
	private String name;
	private String year;
	private String emailAddress;
	private String department;
	private String lastYearBalance;
	private String entitledAnnual;
	private String balance;
	private String sickLeaveFP;
	private String sickLeavePP;
	private String annualLeave;
	private String birthdayLeave;	
	private String compassionateLeave;
	private String compensationLeave;
	private String examLeave;
	private String injuryLeave;
	private String juryLeave;
	private String marriageLeave;
	private String maternityLeave;
	private String paternityLeave;
	private String noPayLeave;
	private String region;
	private String others;
	
	
	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public String getLastYearBalance() {
		lastYearBalance = lastYearBalance == null ? "0" : lastYearBalance;
		return lastYearBalance;
	}
	
	public void setLastYearBalance(String lastYearBalance) {
		this.lastYearBalance = lastYearBalance;
	}
	
	public String getEntitledAnnual() {
		entitledAnnual = entitledAnnual == null ? "0" : entitledAnnual;
		return entitledAnnual;
	}
	
	public void setEntitledAnnual(String entitledAnnual) {
		this.entitledAnnual = entitledAnnual;
	}
		
	public String getBalance() {
		balance = balance == null ? "0" : balance;
		return balance;
	}
	
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	public String getAnnualLeave() {
		annualLeave = annualLeave == null ? "0" : annualLeave;
		return annualLeave;
	}
	
	public void setAnnualLeave(String annualLeave) {
		this.annualLeave = annualLeave;
	}

	public String getSickLeaveFP() {
		return sickLeaveFP;
	}

	public void setSickLeaveFP(String sickLeaveFP) {
		this.sickLeaveFP = sickLeaveFP;
	}

	public String getSickLeavePP() {
		return sickLeavePP;
	}

	public void setSickLeavePP(String sickLeavePP) {
		this.sickLeavePP = sickLeavePP;
	}

	public String getExamLeave() {
		return examLeave;
	}

	public void setExamLeave(String examLeave) {
		this.examLeave = examLeave;
	}

	public String getInjuryLeave() {
		return injuryLeave;
	}

	public void setInjuryLeave(String injuryLeave) {
		this.injuryLeave = injuryLeave;
	}

	public String getJuryLeave() {
		return juryLeave;
	}

	public void setJuryLeave(String juryLeave) {
		this.juryLeave = juryLeave;
	}

	public String getCompassionateLeave() {
		compassionateLeave = compassionateLeave == null ? "0" :compassionateLeave;
		return compassionateLeave;
	}
	
	public void setCompassionateLeave(String compassionateLeave) {
		this.compassionateLeave = compassionateLeave;
	}
	
	public String getCompensationLeave() {
		return compensationLeave;
	}

	public void setCompensationLeave(String compensationLeave) {
		this.compensationLeave = compensationLeave;
	}

	public String getBirthdayLeave() {
		birthdayLeave = birthdayLeave == null ? "0" : birthdayLeave;
		return birthdayLeave;
	}
	
	public void setBirthdayLeave(String birthdayLeave) {
		this.birthdayLeave = birthdayLeave;
	}
	
	public String getMarriageLeave() {
		marriageLeave = marriageLeave == null ? "0" : marriageLeave;
		return marriageLeave;
	}
	
	public void setMarriageLeave(String marriageLeave) {
		this.marriageLeave = marriageLeave;
	}
	
	public String getMaternityLeave() {
		maternityLeave = maternityLeave == null ? "0" : maternityLeave;
		return maternityLeave;
	}
	
	public void setMaternityLeave(String maternityLeave) {
		this.maternityLeave = maternityLeave;
	}
	
	public String getPaternityLeave() {
		paternityLeave = paternityLeave == null ? "0" : paternityLeave;
		return paternityLeave;
	}
	
	public void setPaternityLeave(String paternityLeave) {
		this.paternityLeave = paternityLeave;
	}
	
	public String getNoPayLeave() {
		noPayLeave = noPayLeave == null ? "0" : noPayLeave;
		return noPayLeave;
	}
	
	public void setNoPayLeave(String noPayLeave) {
		this.noPayLeave = noPayLeave;
	}
	
	
	public String getOthers() {
		others = others == null ? "0" : others;
		return others;
	}
	
	public void setOthers(String others) {
		this.others = others;
	}
	
}
