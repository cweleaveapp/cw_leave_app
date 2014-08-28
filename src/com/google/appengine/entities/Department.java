/**
 * 
 */
package com.google.appengine.entities;

/**
 * The class represents department entity
 *
 */
public class Department {
	
	public String id;
	
	public String name_en;
	public String name_tc;
	public boolean exgratiaLeaveDayEnabled;
	public String approverEmail;
	public String delegateEmail;
	

	public Department(String id, String name_en, String name_tc,
			boolean exgratiaLeaveDayEnabled, String approverEmail) {
		this.id = id;
		this.name_en = name_en;
		this.name_tc = name_tc;
		this.exgratiaLeaveDayEnabled = exgratiaLeaveDayEnabled;
		this.approverEmail = approverEmail;
	}

	public Department(String id, String name_en, String name_tc,
			boolean exgratiaLeaveDayEnabled, String approverEmail, String delegateEmail) {
		this.id = id;
		this.name_en = name_en;
		this.name_tc = name_tc;
		this.exgratiaLeaveDayEnabled = exgratiaLeaveDayEnabled;
		this.approverEmail = approverEmail;
		this.delegateEmail = delegateEmail;
	}
	/**
	 * @return the id
	 */
	public String getid() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setid(String id) {
		this.id = id;
	}


	/**
	 * @return the name_en
	 */
	public String getNameEn() {
		return name_en;
	}


	/**
	 * @param name_en the name_en to set
	 */
	public void setNameEn(String name_en) {
		this.name_en = name_en;
	}


	/**
	 * @return the name_tc
	 */
	public String getNameTc() {
		return name_tc;
	}


	/**
	 * @param name_tc the name_tc to set
	 */
	public void setNameTc(String name_tc) {
		this.name_tc = name_tc;
	}


	/**
	 * @return the exgratiaLeaveDayEnabled
	 */
	public boolean isExgratiaLeaveDayEnabled() {
		return exgratiaLeaveDayEnabled;
	}


	/**
	 * @param exgratiaLeaveDayEnabled the exgratiaLeaveDayEnabled to set
	 */
	public void setisExgratiaLeaveDayEnabled(boolean exgratiaLeaveDayEnabled) {
		this.exgratiaLeaveDayEnabled = exgratiaLeaveDayEnabled;
	}


	/**
	 * @return the approverEmail
	 */
	public String getApproverEmail() {
		return approverEmail;
	}


	/**
	 * @param approverEmail the approverEmail to set
	 */
	public void setApproverEmail(String approverEmail) {
		this.approverEmail = approverEmail;
	}

	/**
	 * @return the delegate
	 */
	public String getDelegateEmail() {
		return delegateEmail;
	}


	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(String delegateEmail) {
		this.delegateEmail = delegateEmail;
	}
}
