package com.google.appengine.util;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.Query.FilterOperator;

public final class ConstantUtils {

	public static final String LEAVE_APPROVAL_PROGRAM = "Leave Approval Program"; 
	
	public static final String EMP_LEAVE_REQ_FORM = "Employee Leave Request Form";
	
	public static final String MCT_LEAVE_HISTORY = "MCT Leave History";
	
	public static final String EMP_COMPENSATION_LEAVE_ENTITLEMENT_FORM = "Employee Compensation Leave Entitlement Form";
	
	public static final int PAGE_SIZE = 10;
	
	public static final String RESET = "Reset";
	
	public static final String APPROVE = "approve";
	
	public static final String REJECT = "reject";
	
	public static final String APP_DOMAIN =  "App Domain";
	
	public static final String APP_ADMIN_ACCOUNT = "App Admin Account";
	
	public static final String APP_ADMIN_ACC_PASS = "App Admin Account Password";
	
	//filter operator
	public static final String EQUAL = "EQUAL";
	
	public static final String IN = "IN";
	
	public static final String GREATER_THAN = "GREATER_THAN";
	
	public static final String GREATER_THAN_OR_EQUAL = "GREATER_THAN_OR_EQUAL";
	
	public static final String LESS_THAN = "LESS_THAN";
	
	public static final String LESS_THAN_OR_EQUAL = "LESS_THAN_OR_EQUAL";
	
	public static final String NOT_EQUAL = "NOT_EQUAL";
	
	public static Map<String,Object> mapFilter(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("EQUAL", FilterOperator.EQUAL);
		map.put("IN", FilterOperator.IN);
		map.put("GREATER_THAN", FilterOperator.GREATER_THAN);
		map.put("GREATER_THAN_OR_EQUAL", FilterOperator.GREATER_THAN_OR_EQUAL);
		map.put("LESS_THAN", FilterOperator.LESS_THAN);
		map.put("LESS_THAN_OR_EQUAL", FilterOperator.LESS_THAN_OR_EQUAL);
		map.put("NOT_EQUAL", FilterOperator.NOT_EQUAL);
		return map;
	}
	
	public static final String OK = "OK";
	
	public static final String ERROR = "ERROR";
	
	public static final String TRUE = "true";
	
	public static final String FALSE = "false";
	
	public static final String AMEND_LEAVE = "Amend Leave";
	
	public static final String CANCEL_LEAVE = "Cancel Leave";
	
	public static final String COMPENSATION_LEAVE = "Compensation Leave";
	
	public static final String COMPENSATION_LEAVE_ENTITLEMENT = "Compensation Leave Entitlement";
	
	public static final String COMPANSSIONATE_LEAVE = "Compassionate Leave";
	
	public static final String ANNUAL_LEAVE = "Annual Leave";
	
	public static final String SICK_LEAVE = "Sick Leave";
	
	public static final String NO_PAY_LEAVE = "No Pay Leave";
	
	public static final String BIRTHDAY_LEAVE = "Birthday Leave";
	
	public static final String MATERNITY_LEAVE = "Maternity Leave";
	
	public static final String WEDDING_LEAVE = "Wedding Leave";
	
	public static final String OTHERS = "Others";
	
	public static final String MAX_EX_GRATIA_ALLOWANCE = "2.5";
	
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	
	public static final String DATE_FORMAT_CW = "EEE MMM dd yyyy";
	
	public static final String DATE_FORMAT_REV = "yyyy-MM-dd";
	
	public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	
	public static final String DATE_TIME_FORMAT_REV = "yyyy-MM-dd HH:mm:ss";
	
	public static final String MCKL_PUBLIC_HOLIDAYS = "MCKL Public Holidays ";
	
	public static final String MCSG_PUBLIC_HOLIDAYS = "MCSG Public Holidays ";
	
	public static final String MCCN_PUBLIC_HOLIDAYS = "MCCN Public Holidays ";
	
	public static final String MCTW_PUBLIC_HOLIDAYS = "MCTW Public Holidays ";
	
	public static final String MCHK_PUBLIC_HOLIDAYS = "MCHK Public Holidays ";
	
	public static final String SPREADSSHEETS_FEEDURL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";

	public static final String SPREADSSHEET_SERVICE_ACCOUNT = "Spreadsheet Service Account";
	
	public static final String SPREADSSHEET_SERVICE_ACCOUNT_PASS = "Spreadsheet Service Account Password";
	
	public static final String CALENDAR_SERVICE_ACCOUNT  = "Calendar Service Account";
	
	public static final String CALENDAR_SERVICE_ACCOUNT_PASSWORD  = "Calendar Service Account Password";
	
	public static final String EMAIL_SENDER_ACCOUNT = "Email Sender Account";
	
	public static final String HR_EMAIL_ADDRESS = "HR Email Address";
	
	public static final String SYSTEM_ADMIN_EMAIL_ADDRESS = "System Admin Email Address";
	
	public static final String HK = "HK";
	
	public static final String CN = "CN";
	
	public static final String MY = "MY";
	
	public static final String TW = "TW";
	
	public static final String SG = "SG";
	
	public static final String MO = "MO";
	
	public static final String HONGKONG = "Hong Kong";
	
	public static final String CHINA = "China";
	
	public static final String MALAYSIA = "Malaysia";
	
	public static final String SINGAPORE = "Singapore";
	
	public static final String TAIWAN = "Taiwan";
	
	public static final String MACAU = "Macau";
	
	public static final String PENDING = "Pending";
	
	public static final String SEND = "Send";
	
	public static final String ACTIVE = "Active";
	
	public static final String SUCCESS = "Success";
	
	public static final String COMPLETE = "Complete";
	
	public static final String FAIL = "Fail";
	
	public static final String INSERT = "I";
	
	public static final String UPDATE = "U";
	
	public static final String DELETE = "D";
	
	public static final String SOANALYTICS_DOMAIN = "soanalytics.com";
	
}
