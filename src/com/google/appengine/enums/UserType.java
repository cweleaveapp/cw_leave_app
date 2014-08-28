package com.google.appengine.enums;

/**
 * The enumeration represents all user types.
 */
public enum UserType {
	
	ADMINISTRATOR (1, "Administrator"),
	SUPERVISOR (2, "Supervisor"),
	EMPLOYEE (3, "Employee");
	
	private UserType(int userTypeId, String userTypeName) {
		this.userTypeId = userTypeId;
		this.userTypeName = userTypeName;
	}

	public final int userTypeId;
	
	public final String userTypeName;

	/**
	 * @return the userTypeId
	 */
	public int getUserTypeId() {
		return userTypeId;
	}

	/**
	 * @return the userTypeName
	 */
	public String getUserTypeName() {
		return userTypeName;
	}	
	 
}
