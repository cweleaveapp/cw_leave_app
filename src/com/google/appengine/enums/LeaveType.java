package com.google.appengine.enums;

/**
 * The enumeration represents leave type
 *
 */
public enum LeaveType {
	
	ANNUAL_LEAVE (1, "Annual Leave", "AL"),
	BIRTHDAY_LEAVE (2, "Birthday Leave", "BL"),
	COMPASSIONATE_LEAVE (3, "Compassionate Leave", "CA"),
	COMPENSATION_LEAVE(4, "Compensation Leave", "CE"),
	EXAMINATION_LEAVE(5, "Examination Leave", "XL"),
	INJURY_LEAVE(6,"Injury Leave", "IL"),
	JURY_LEAVE (7,"Jury Leave", "JL"),
	MARRIAGE_LEAVE (8, "Marriage Leave", "MR"),
	MATERNITY_LEAVE (9, "Maternity Leave", "MT"),	
	PATERNITY_LEAVE(10, "Paternity Leave", "PL"),
	NO_PAY_LEAVE (11, "No Pay Leave", "NP"),
	SICK_LEAVE_FP (12, "Sick Leave (Full Paid)", "SL"),
	SICK_LEAVE_PP (13, "Sick Leave (4/5 Paid)", "S4");	
	
	public final int id;
	
	public final String type;
	
	public final String abbreviation;
	
	private LeaveType(int id, String type, String abbreviation) {
		this.id = id;
		this.type = type;
		this.abbreviation = abbreviation;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return the type name
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
	
}
