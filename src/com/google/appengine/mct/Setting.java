package com.google.appengine.mct;

import java.io.Serializable;

public class Setting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String calServiceAccPass;
	private String sysAdminEmailAdd;
	private String appDomain;
	private String appAdminAcc;
	private String appAdminAccPass;
	private String calServiceAcc;
	private String adminEmailAcc;
	private String spreadsheetServiceAcc;
	private String spreadsheetServiceAccPass;
	private String emailSenderAcc;
	private String hrEmailAdd;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCalServiceAccPass() {
		return calServiceAccPass;
	}
	public void setCalServiceAccPass(String calServiceAccPass) {
		this.calServiceAccPass = calServiceAccPass;
	}
	public String getSysAdminEmailAdd() {
		return sysAdminEmailAdd;
	}
	public void setSysAdminEmailAdd(String sysAdminEmailAdd) {
		this.sysAdminEmailAdd = sysAdminEmailAdd;
	}
	public String getAppDomain() {
		return appDomain;
	}
	public void setAppDomain(String appDomain) {
		this.appDomain = appDomain;
	}
	public String getAppAdminAcc() {
		return appAdminAcc;
	}
	public void setAppAdminAcc(String appAdminAcc) {
		this.appAdminAcc = appAdminAcc;
	}
	public String getAppAdminAccPass() {
		return appAdminAccPass;
	}
	public void setAppAdminAccPass(String appAdminAccPass) {
		this.appAdminAccPass = appAdminAccPass;
	}
	public String getCalServiceAcc() {
		return calServiceAcc;
	}
	public void setCalServiceAcc(String calServiceAcc) {
		this.calServiceAcc = calServiceAcc;
	}
	public String getAdminEmailAcc() {
		return adminEmailAcc;
	}
	public void setAdminEmailAcc(String adminEmailAcc) {
		this.adminEmailAcc = adminEmailAcc;
	}
	public String getSpreadsheetServiceAcc() {
		return spreadsheetServiceAcc;
	}
	public void setSpreadsheetServiceAcc(String spreadsheetServiceAcc) {
		this.spreadsheetServiceAcc = spreadsheetServiceAcc;
	}
	public String getSpreadsheetServiceAccPass() {
		return spreadsheetServiceAccPass;
	}
	public void setSpreadsheetServiceAccPass(String spreadsheetServiceAccPass) {
		this.spreadsheetServiceAccPass = spreadsheetServiceAccPass;
	}
	public String getEmailSenderAcc() {
		return emailSenderAcc;
	}
	public void setEmailSenderAcc(String emailSenderAcc) {
		this.emailSenderAcc = emailSenderAcc;
	}
	public String getHrEmailAdd() {
		return hrEmailAdd;
	}
	public void setHrEmailAdd(String hrEmailAdd) {
		this.hrEmailAdd = hrEmailAdd;
	}
	
	
}
