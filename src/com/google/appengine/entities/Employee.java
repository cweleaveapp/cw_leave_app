package com.google.appengine.entities;

import java.io.Serializable;

import com.google.appengine.api.users.User;
import com.google.appengine.enums.UserType;

public class Employee implements Serializable{	

	private String empKey;
	
	private String staffId;
	
	private String emailAddress;
	
	private String fullName;
	
	private String jobTitle;
	
	private String department;
	
	private Employee deptApprover;
	
	private Employee deptDelegator;
	
	private String hiredDate;
	
	private String birthDate;
	
	private String supervisor;
	
	private String userType;
	
	private User domainUser;
	
	private Employee superObj;
	
	private EmployeeLeaveDetails edtls;
		
	public Employee() {
		super();
	}

	public Employee(String staffId, String emailAddress, String fullName,
			String jobTitle, String department, String hiredDate,
			String birthDate, String supervisor, String userType) {
		this.staffId = staffId;
		this.emailAddress = emailAddress;
		this.fullName = fullName;
		this.jobTitle = jobTitle;
		this.department = department;
		this.hiredDate = hiredDate;
		this.birthDate = birthDate;
		this.supervisor = supervisor;
		this.userType = userType != null ? userType : UserType.EMPLOYEE.userTypeName;
	}

	/**
	 * @return the empKey
	 */
	public String getEmpKey() {
		return empKey;
	}

	/**
	 * @param empKey the empKey to set
	 */
	public void setEmpKey(String empKey) {
		this.empKey = empKey;
	}

	/**
	 * @return the staffId
	 */
	public String getstaffId() {
		return staffId;
	}

	/**
	 * @param staffid the staffId to set
	 */
	public void setstaffId(String staffId) {
		this.staffId = staffId;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the jobTitle
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @param jobTitle the jobTitle to set
	 */
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

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

	/**
	 * @return the deptApprover
	 */
	public Employee getDeptApprover() {
		return deptApprover;
	}

	/**
	 * @param deptApprover the deptApprover to set
	 */
	public void setDeptApprover(Employee deptApprover) {
		this.deptApprover = deptApprover;
	}

	/**
	 * @return the deptDelegator
	 */
	public Employee getDeptDelegator() {
		return deptDelegator;
	}

	/**
	 * @param deptDelegator the deptDelegator to set
	 */
	public void setDeptDelegator(Employee deptDelegator) {
		this.deptDelegator = deptDelegator;
	}

	/**
	 * @return the hiredDate
	 */
	public String getHiredDate() {
		return hiredDate;
	}

	/**
	 * @param hiredDate the hiredDate to set
	 */
	public void setHiredDate(String hiredDate) {
		this.hiredDate = hiredDate;
	}

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
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
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType!=null ? userType: UserType.EMPLOYEE.userTypeName;
	}

	/**
	 * @return the domainUser
	 */
	public User getDomainUser() {
		return domainUser;
	}

	/**
	 * @param domainUser the domainUser to set
	 */
	public void setDomainUser(User domainUser) {
		this.domainUser = domainUser;
	}

	/**
	 * @return the superObj
	 */
	public Employee getSuperObj() {
		return superObj;
	}

	/**
	 * @param superObj the superObj to set
	 */
	public void setSuperObj(Employee superObj) {
		this.superObj = superObj;
	}

	/**
	 * @return the etls
	 */
	public EmployeeLeaveDetails getEmployeeLeaveDetails() {
		return edtls;
	}

	/**
	 * @param etls the etls to set
	 */
	public void setEmployeeLeaveDetails(EmployeeLeaveDetails edtls) {
		this.edtls = edtls;
	}

}
