package com.google.appengine.mct;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.datastore.DepartmentService;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.enums.*;
import com.google.appengine.entities.Department;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.util.ConstantUtils;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class Misc extends BaseServlet {
	private static final Logger log = Logger.getLogger(Misc.class);

	public static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm:ss";
	
	public static final String DATE_FORMAT_NOW_REV = "yyyy-MM-dd HH:mm:ss";

	public String listDocumentsSb() {
		Vector myVec = new Vector();
		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		int bal = 0;
		int index = 0;
		int allCount = 0;
		int docCount = 0;
		String spdSvAcc = "";
		String spdSvAccPwd = "";
		
		try {
			SettingService ss = new SettingService();
			for (Setting set : ss.getSetting()) {
					spdSvAcc = set.getSpreadsheetServiceAcc();
					spdSvAccPwd = set.getSpreadsheetServiceAccPass();
				
			}
			
			SpreadsheetService service = new SpreadsheetService("wise");
			service.setUserCredentials(spdSvAcc, spdSvAccPwd);
			String title = "";
			String docHref = "";
			log.debug("List of documents in gDoc");
			URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			for (int i = 0; i < spreadsheets.size(); i++) {
				title = "";
				SpreadsheetEntry entry = spreadsheets.get(i);
				allCount = allCount + 1;
				title = entry.getTitle().getPlainText();
				docHref = entry.getSpreadsheetLink().getHref();
//				if (title.contains(".") && title.contains(" ")) {
//					int spaceInd = title.indexOf(" ");
//					String name = title.substring(0, spaceInd);
//					String year = title.substring(spaceInd+1, title.length());
//					int yearNum = 0;
//					yearNum = title.length() - (spaceInd+1);
//					if (yearNum == 4) {
						docCount = docCount + 1;
						if (!myVec.contains(title)) {
							String data = title + "|" + docHref;
							myVec.add(data);
						}
//					}
//				}
			}
			Collections.sort(myVec);

			log.debug("Document all count: " + allCount);
			log.debug("Document document count: " + docCount);
			for (int j=0; j<myVec.size(); j++) {
				int indexVec = 0;
				String tmpVecStr = myVec.elementAt(j).toString();
				indexVec = tmpVecStr.indexOf("|");
				String ssTitle = tmpVecStr.substring(0, indexVec);
				String ssHref = tmpVecStr.substring(indexVec+1, tmpVecStr.length());

				bal = index % 2;
				if (bal == 0) {
					sb.append("<tr bgcolor=\"#FFF8C6\">");
				} else if (bal == 1) {
					sb.append("<tr bgcolor=\"#F0F0F0\">");
				}
				sb.append("<td align=\"center\"><input type=\"radio\" name=\"" + "docRad" + "\"" + " value=\"" + ssHref + "\"" + "onClick=\"javascript:cmd_parm();\"/></td>");
				sb.append("<td>" + ssTitle + "</td>");
				sb.append("</tr>");
				index = index + 1;
			}
		} catch (Exception e) {
			log.error("Misc listDocumentsSb() error: " + e.getMessage());
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public String listSupervisors(){
		StringBuffer sb = new StringBuffer();
		EmployeeService es = new EmployeeService();
		sb.append("<option value=\"\">-- Please select --</option>");
		if(!DepartmentService.getInstance().getDepartments().isEmpty()){
			for(Department dept : DepartmentService.getInstance().getDepartments()) {
				Employee emp = es.findEmployeeByColumnName("emailAddress", dept.getApproverEmail());
				if(emp.getFullName()!= null) {
					sb.append("<option value=\"" + emp.getEmailAddress() + "\">");
					sb.append(emp.getFullName());
				}				
				sb.append("</option>");
			}				
		}
		return sb.toString();
	}
	

	public static String selectDeptListOpitons(){
		StringBuffer sb = new StringBuffer();
		if(!DepartmentService.getInstance().getDepartments().isEmpty()){
		for(Department dept : DepartmentService.getInstance().getDepartments()){
			sb.append("<option value=\"" + dept.getid() +"\" >");
			sb.append(dept.getNameEn() + "</option>");
			}
		}
		return sb.toString();
	}
	
	public String listMCSupervisors() {
		String empRegion = "", empAdd = "";
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			if (user.getNickname() != null) {
				empAdd = user.getNickname();
			}
		}
		EmployeeService es = new EmployeeService();
		for (MCEmployee emp : es.getMCEmployees()) {
			if (emp.getEmailAddress().equalsIgnoreCase(empAdd)) {
				empRegion = emp.getRegion();
			}
		}
		StringBuffer sb = new StringBuffer();
		MCSupervisorService ss = new MCSupervisorService();
		sb.append("<option value=\"\">-- Please select supervisor --</option>");
		for (MCSupervisor sup : ss.getSupervisors()) {
			MCEmployee emp = es.findMCEmployeeByColumnName("emailAddress", sup.getEmailAddress());
			if ((sup.getRegion().equalsIgnoreCase(empRegion) ||
					sup.getRegion().contains(empRegion)) && StringUtils.isBlank(emp.getResignedDate())) {
				sb.append("<option value=\"" + sup.getEmailAddress() + "\">");
				sb.append(sup.getEmailAddress());
				sb.append("</option>");
			}
		}
		return sb.toString();
	}
	
	public static Date setCalendarByLocale(){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
		return cal.getTime();
	}

	public static String getCalendarByLocale(Locale locale){
		Calendar cal = Calendar.getInstance(locale);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	
	public static String getCalendarByDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(date);
	}
	
	public static String getCalendarByDateRev(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW_REV);
		return sdf.format(date);
	}
	
	public static String getCalendarByDayRev(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_REV);
		return sdf.format(date);
	}

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		return sdf.format(cal.getTime());
	}


	public void regionalHolidayCalendar(String thisDate, String description, String region) {
		CalendarService myService = new CalendarService("MCT-Calendar");
		try {
			String calAcc = "";
			String calAccPwd = "";
			SettingService ss = new SettingService();
			for (Setting set : ss.getSetting()) {
					calAcc = set.getCalServiceAcc();
					calAccPwd = set.getCalServiceAccPass();
				
			}
			myService.setUserCredentials(calAcc, calAccPwd);
			try {
				String url = "";
				String regAbb = "";
				RegionsService rs = new RegionsService();
//				for (Regions reg : rs.getRegions()) {
//					if (reg.getRegion().equalsIgnoreCase(region)) {
//						url = reg.getRegionCalendarURL();
//						regAbb = reg.getRegionAbbreviation();
//					}
//				}
				
				URL postUrl = null;
				postUrl = new URL(url);
				description = regAbb + " PH - " + description;

				DateFormat formatter;
				DateFormat format;
				Date date;
				String myDate = "";
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					date = (Date)format.parse(thisDate);
					myDate = formatter.format(date);
					int ind = myDate.indexOf("T");
					myDate = myDate.substring(0, ind);
					myDate = myDate + "T09:00:00-09:00";
				} catch (ParseException e1) {
					log.error("Parsing error: " + e1.getMessage());
				}

				CalendarEventEntry myEntry = new CalendarEventEntry();
				myEntry.setTitle(new PlainTextConstruct(description));
				DateTime startTime = DateTime.parseDateTime(myDate);
				startTime.setDateOnly(true);
				DateTime endTime = DateTime.parseDateTime(myDate);
				endTime.setDateOnly(true);
				com.google.gdata.data.extensions.When eventTimes = new com.google.gdata.data.extensions.When();
				eventTimes.setStartTime(startTime);
				eventTimes.setEndTime(endTime);
				myEntry.addTime(eventTimes);

				try {
					try {
						CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
					} catch (IOException e) {
						log.error("IO error: " + e.getMessage());
					}
				} catch (ServiceException e) {
					log.error("Service error: " + e.getMessage());
				}
			} catch (MalformedURLException e1) {
				log.error("MalformedURL error: " + e1.getMessage());
			}
		} catch (AuthenticationException e1) {
			log.error("Authentication error: " + e1.getMessage());
		}
	}


	public void updateCalendar(String startDate, String endDate, String description, String region) {
		CalendarService myService = new CalendarService("MCT-Calendar");
		try {
			String calAcc = "";
			String calAccPwd = "";
			SettingService ss = new SettingService();
			for (Setting set : ss.getSetting()) {
					calAcc = set.getCalServiceAcc();
					calAccPwd = set.getCalServiceAccPass();
				
			}
			
			myService.setUserCredentials(calAcc, calAccPwd);
			try {
				String url = "";
				RegionsService rs = new RegionsService();
//				for (Regions reg : rs.getRegions()) {
//					if (reg.getRegion().equalsIgnoreCase(region)) {
//						url = reg.getRegionCalendarURL();
//					}
//				}
				
				URL postUrl = null;
				postUrl = new URL(url);

				DateFormat formatter;
				DateFormat format;
				Date date1 = new Date();
				Date date2 = new Date();
				Date date3 = new Date();
				Date dateTmp = new Date();
				String myDate = "";
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					date1 = (Date)format.parse(startDate);
					date2 = (Date)format.parse(endDate);
					int diff = 0;
					diff = (int)((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
					diff = diff + 1;
					if (diff == 1) {
						myDate = formatter.format(date1);
						int ind = myDate.indexOf("T");
						myDate = myDate.substring(0, ind);
						myDate = myDate + "T09:00:00-09:00";
						
						
						CalendarEventEntry myEntry = new CalendarEventEntry();
						myEntry.setTitle(new PlainTextConstruct(description));
						DateTime startTime = DateTime.parseDateTime(myDate);
						startTime.setDateOnly(true);
						DateTime endTime = DateTime.parseDateTime(myDate);
						endTime.setDateOnly(true);
						com.google.gdata.data.extensions.When eventTimes = new com.google.gdata.data.extensions.When();
						eventTimes.setStartTime(startTime);
						eventTimes.setEndTime(endTime);
						myEntry.addTime(eventTimes);
						try {
							try {
								CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
							} catch (IOException e) {
								log.error("Misc * updateCalendar 1 - IO error: " + e.getMessage());
							}
						} catch (ServiceException e) {
							log.error("Misc * updateCalendar 1 - Service error: " + e.getMessage());
						}
					} else if (diff > 1) {
						for (int i=1; i<=diff; i++) {
							if (i == 1) {
								dateTmp.setTime(date1.getTime() - 1  * 24 * 60 * 60 * 1000);
								date3.setTime(date1.getTime());
							} else {
								date3.setTime(dateTmp.getTime() + i * 24 * 60 * 60 * 1000);
							}
							Calendar time = new GregorianCalendar();
							time.setTime(date3);
							if (time.get(Calendar.DAY_OF_WEEK)!=7 && time.get(Calendar.DAY_OF_WEEK)!=1) {
								myDate = formatter.format(date3);
								int ind = myDate.indexOf("T");
								myDate = myDate.substring(0, ind);
								myDate = myDate + "T09:00:00-09:00";
								CalendarEventEntry myEntry = new CalendarEventEntry();
								myEntry.setTitle(new PlainTextConstruct(description));
								DateTime startTime = DateTime.parseDateTime(myDate);
								startTime.setDateOnly(true);
								DateTime endTime = DateTime.parseDateTime(myDate);
								endTime.setDateOnly(true);
								com.google.gdata.data.extensions.When eventTimes = new com.google.gdata.data.extensions.When();
								eventTimes.setStartTime(startTime);
								eventTimes.setEndTime(endTime);
								myEntry.addTime(eventTimes);
								try {
									try {
										CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
									} catch (IOException e) {
										log.error("Misc * updateCalendar 2 - IO error: " + e.getMessage());
									}
								} catch (ServiceException e) {
									log.error("Misc * updateCalendar 2 - Service error: " + e.getMessage());
								}
							}
						}
					}
				} catch (ParseException e1) {
					log.error("Misc * updateCalendar - Parsing error: " + e1.getMessage());
					e1.printStackTrace();
				}
			} catch (MalformedURLException e1) {
				log.error("Misc * updateCalendar - MalformedURL error: " + e1.getMessage());
				e1.printStackTrace();
			}
		} catch (AuthenticationException e1) {
			log.error("Misc * updateCalendar - Authentication error: " + e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	
//	public String listSettings() {
//		StringBuffer sb = new StringBuffer();
//		try {
//			SettingService setting = new SettingService();
//			for (int i=0;  i<setting.getSetting().size(); i++) {
//				Setting sets = setting.getSetting().get(i);
//				String propertyName = "";
//				propertyName = sets.getPropertyName();
//				propertyName = propertyName.replaceAll(" ", "");
//				sb.append("<tr>\n");
//				sb.append("<td align=\"left\" style=\"padding-left:0px;padding-bottom:16px; color:black;\" width=\"100px\">\n");
//				sb.append(sets.getPropertyName());
//				sb.append("</td>\n");
//				sb.append("<td width=\"230px\">\n");
//				sb.append("<div class=\"textbox\">\n");
//				sb.append("<input type=\"text\" name=\"" + i + "\"" + " value=\"" + sets.getPropertyValue() + "\"" + " maxlength=\"150\" size=\"29\"/>\n");
//				sb.append("</div>\n");
//				sb.append("</td>\n");
//				sb.append("</tr>\n");
//			}
//
//		} catch(Exception e) {
//			log.error("Misc * listSettings() Error: " + e.getMessage());
//		}
//		return sb.toString();
//	}


	public String listFiles() {
		
		log.debug("listFiles");
		
		String spdSvAcc = "";
		String spdSvAccPwd = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				spdSvAcc = set.getSpreadsheetServiceAcc();
				spdSvAccPwd = set.getSpreadsheetServiceAccPass();
			
		}
		
		StringBuffer sb = new StringBuffer();
		try {
			DocsService service = new DocsService("wise");
			service.setUserCredentials(spdSvAcc, spdSvAccPwd);
			URL documentListFeedUrl = new URL("https://docs.google.com/feeds/default/private/full");
			DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
			feed.getEntries();
			sb.append("<option value=\"Default\">-- Please select a file --</option>");
			for (int a=0; a<feed.getEntries().size(); a++) {
				DocumentListEntry et = feed.getEntries().get(a);
				String title = et.getTitle().getPlainText();
				String titleValue = title.replace(" ", "_");
				if (et.getParentLinks().size() > 0) {
					String folderName = "";
					for (int b=0; b<et.getParentLinks().size(); b++) {
						folderName = et.getParentLinks().get(b).getTitle();
						if (ConstantUtils.LEAVE_APPROVAL_PROGRAM.equalsIgnoreCase(folderName)) {
							sb.append("<option value=" + titleValue + ">" + title + "</option>");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Misc * listFiles() Error: " + e.getMessage());
			sb.append("<option value=\"Default\">-- No files to import --</option>");
		}
		return sb.toString();
	}


	public String checkboxRegion() {
		StringBuffer sb = new StringBuffer();
		RegionsService rs = new RegionsService();
		
//		for (Regions reg : rs.getRegions()) {
//			sb.append("<label class=\"checkbox\"><input type=\"checkbox\" name=\"region\" class=\"reg\" value=\"" + 
//					reg.getRegion() + "\"" + "/>&nbsp;" + reg.getRegion() + "<label><br>");
//		}
		return sb.toString();
	}
	
	public String supervisorCheckBoxRegion(){
		StringBuilder sb = new StringBuilder();
		RegionsService rs = new RegionsService();
		MCSupervisorService ss = new MCSupervisorService();
		MCSupervisor supervisor = ss.getSupervisors().get(0);
		
//		for (int i =0; i<rs.getRegions().size(); i++) {
//			Regions reg = rs.getRegions().get(i);
//				if(supervisor.getRegion().contains((reg.getRegion()))){
//					sb.append("<label>&nbsp;<input type=\"checkbox\" name=\"region\" class=\"reg\" value=\"" + 
//							reg.getRegion() + "\" checked " + "/>&nbsp;" + reg.getRegion() + "</label><br>");
//				}
//				else{
//					sb.append("<label>&nbsp;<input type=\"checkbox\" name=\"region\" class=\"reg\" value=\"" + 
//							reg.getRegion() + "\"" + "/>&nbsp;" + reg.getRegion() + "</label><br>");
//				}
//			
//		}
		return sb.toString();
		
	}


	public String listDepartment() {
		StringBuffer sb = new StringBuffer();
		sb.append("<option value=\"\"> Please select a department </option>");
		for (Department dept : DepartmentService.getInstance().getDepartments()) {
			sb.append("<option value=\"" + dept.getNameEn() + "\"" + ">" + dept.getNameEn() + "</option>");
		}
		return sb.toString();
	}

	/*
	 * Populate leave type select options
	 */
	public String listLeaveType() {		
		LeaveType[] leaveTypes = LeaveType.values();
		StringBuffer sb = new StringBuffer();
		sb.append("<option value=\"Default\">-- Please select leave type --</option>");
		for(LeaveType type :leaveTypes){
			sb.append("<option value=\"" + String.valueOf(type.getId()) + "\">" + type.getType() + "</option>");
		}
		return sb.toString();
	}

	//do not use this one.
	public String leaveTypes() {
		StringBuffer sb = new StringBuffer();
		sb.append("<option value=\"Default\">-- Please select leave type --</option>");
		sb.append("<option value=\"Annual Leave\">Annual Leave</option>");
		sb.append("<option value=\"Sick Leave\">Sick Leave</option>");
		sb.append("<option value=\"Compensation Leave\">Compensation Leave</option>");
		sb.append("<option value=\"No Pay Leave\">No Pay Leave</option>");
		sb.append("<option value=\"Maternity Leave\">Maternity Leave</option>");
		sb.append("<option value=\"Birthday Leave\">Birthday Leave</option>");
		sb.append("<option value=\"Wedding Leave\">Wedding Leave</option>");
		sb.append("<option value=\"Compassionate Leave\">Compassionate Leave</option>");
		sb.append("<option value=\"Compensation Leave Entitlement\">Compensation Leave Entitlement</option>");
		return sb.toString();
	}


	public String listChangeType() {
		StringBuffer sb = new StringBuffer();
		sb.append("<option value=\"Default\">-- Select amend/cancel --</option>");
		sb.append("<option value=\"Amend Leave\">Amend leave</option>");
		sb.append("<option value=\"Cancel Leave\" selected>Cancel Leave</option>");
		return sb.toString();
	}


	public String delEmpList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		EmployeeService es = new EmployeeService();
		for (MCEmployee emp : es.getMCEmployees()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td width=\"10\"><input type=\"checkbox\" name=\"delEmployeeList\" value=\"" + emp.getEmailAddress() + "\"" + "><br></td>");
			sb.append("<td>" + emp.getEmailAddress() + "</td>");
			sb.append("<td>" + emp.getFullName() + "</td>");
			sb.append("<td>" + emp.getRegion() + "</td>");
			sb.append("<td>" + emp.getHiredDate() + "</td>");
			sb.append("<td>" + emp.getBirthDate() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}

	
	public String supervisorListSelect() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		MCSupervisorService ss = new MCSupervisorService();
		for (MCSupervisor sup : ss.getSupervisors()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td width=\"5\"><input type=\"radio\" name=\"supervisorList\" value=\"" + sup.getEmailAddress() + "\"" + "><br></td>");
			sb.append("<td>" + sup.getEmailAddress() + "</td>");
			sb.append("<td>" + sup.getRegion() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}
	

	public String supervisorList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		MCSupervisorService ss = new MCSupervisorService();
		for (MCSupervisor sup : ss.getSupervisors()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td>" + sup.getEmailAddress() + "</td>");
			sb.append("<td>" + sup.getRegion() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String delSupervisorList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		MCSupervisorService ss = new MCSupervisorService();
		for (MCSupervisor sup : ss.getSupervisors()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td width=\"10\"><input type=\"checkbox\" name=\"delSuplist\" value=\"" + sup.getEmailAddress() + "\"" + "><br></td>");
			sb.append("<td>" + sup.getEmailAddress() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}
	
	
	public String regionList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		RegionsService rs = new RegionsService();
//		for (Regions reg : rs.getRegions()) {
//			bal = index % 2;
//			if (bal == 0) {
//				sb.append("<tr bgcolor=\"#FFF8C6\">");
//			} else if (bal == 1) {
//				sb.append("<tr bgcolor=\"#F0F0F0\">");
//			}
//			sb.append("<td width=\"600px\">" + reg.getRegion() + "</td>");
//			sb.append("<td width=\"600px\">" + reg.getRegionAbbreviation() + "</td>");
//			sb.append("<td width=\"400px\">" + reg.getRegionSalesOps() + "</td>");
//			sb.append("<td width=\"400px\">" + reg.getRegionCalendarURL() + "</td>");
//			sb.append("</tr>");
//			index = index + 1;
//		}
		return sb.toString();
	}
	
	
	public String delRegionList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		RegionsService rs = new RegionsService();
//		for (Regions reg : rs.getRegions()) {
//			bal = index % 2;
//			if (bal == 0) {
//				sb.append("<tr bgcolor=\"#FFF8C6\">");
//			} else if (bal == 1) {
//				sb.append("<tr bgcolor=\"#F0F0F0\">");
//			}
//			sb.append("<td width=\"10\"><input type=\"checkbox\" name=\"delReglist\" value=\"" + reg.getRegion() + "\"" + "><br></td>");
//			sb.append("<td>" + reg.getRegion() + "</td>");
//			sb.append("</tr>");
//			index = index + 1;
//		}
		return sb.toString();
	}


	public String administratorList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		AdministratorService as = new AdministratorService();
		for (Administrator adm : as.getAdministrators()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td>" + adm.getEmailAddress() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String delAdminList() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int bal = 0;
		AdministratorService as = new AdministratorService();
		for (Administrator adm : as.getAdministrators()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td width=\"10\"><input type=\"checkbox\" name=\"delAdlist\" value=\"" + adm.getEmailAddress() + "\"" + "><br></td>");
			sb.append("<td>" + adm.getEmailAddress() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String leaveQlist() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		LeaveQueueService lqs = new LeaveQueueService();
		for (LeaveQueue lq : lqs.getLeaveQueue()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			}
			sb.append("<td width=\"128\"><font face=\"verdana\" size=\"1\">" + lq.getTime() + "</font></td>");
			sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + lq.getEmailAdd() + "</font></td>");
			sb.append("<td width=\"60\"><font face=\"verdana\" size=\"1\">" + lq.getNumOfDays() + "</font></td>");
			sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + lq.getStartDate() + "</font></td>");
			sb.append("<td width=\"220\"><font face=\"verdana\" size=\"1\">" + lq.getEndDate() + "</font></td>");
			sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + lq.getSupervisor() + "</font></td>");
			sb.append("<td width=\"85\"><font face=\"verdana\" size=\"1\">" + lq.getLeaveType() + "</font></td>");
			sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + lq.getRemark() + "</font></td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String delLeaveQlist() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		LeaveQueueService lqs = new LeaveQueueService();
		for (LeaveQueue lq : lqs.getLeaveQueue()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			}
			sb.append("<td width=\"5\"><input type=\"checkbox\" name=\"delLQlist\" value=\"" + lq.getTime() + "\"" + "><br></td>");
			sb.append("<td width=\"128\"><font face=\"verdana\" size=\"1\">" + lq.getTime() + "</font></td>");
			sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + lq.getEmailAdd() + "</font></td>");
			sb.append("<td width=\"40\"><font face=\"verdana\" size=\"1\">" + lq.getNumOfDays() + "</font></td>");
			sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + lq.getStartDate() + "</font></td>");
			sb.append("<td width=\"220\"><font face=\"verdana\" size=\"1\">" + lq.getEndDate() + "</font></td>");
			sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + lq.getSupervisor() + "</font></td>");
			sb.append("<td width=\"85\"><font face=\"verdana\" size=\"1\">" + lq.getLeaveType() + "</font></td>");
			sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + lq.getRemark() + "</font></td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String mctAppLeaveQlist() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		ApprovedLeaveService als = new ApprovedLeaveService();
		for (MCApprovedLeave al : als.getApprovedLeave()) {
			if (user.getEmail() != null) {
				if (user.getEmail().equalsIgnoreCase(al.getEmailAdd())) {
					bal = index % 2;
					if (bal == 0) {
						sb.append("<tr bgcolor=\"#F0F0F0\">");
					} else if (bal == 1) {
						sb.append("<tr bgcolor=\"#FFF8C6\">");
					}
					sb.append("<td width=\"128\"><font face=\"verdana\" size=\"1\">" + al.getTime() + "</font></td>");
					sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + al.getEmailAdd() + "</font></td>");
					sb.append("<td width=\"60\"><font face=\"verdana\" size=\"1\">" + al.getNumOfDays() + "</font></td>");
					sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + al.getStartDate() + "</font></td>");
					sb.append("<td width=\"220\"><font face=\"verdana\" size=\"1\">" + al.getEndDate() + "</font></td>");
					sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + al.getSupervisor() + "</font></td>");
					sb.append("<td width=\"85\"><font face=\"verdana\" size=\"1\">" + al.getLeaveType() + "</font></td>");
					sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + al.getRemark() + "</font></td>");
					sb.append("</tr>");
					index = index + 1;
				}
			}
		}
		return sb.toString();
	}


	public String mctAmendLeaveList() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		ApprovedLeaveService als = new ApprovedLeaveService();
		for (MCApprovedLeave al : als.getApprovedLeave()) {
			if (user.getEmail() != null) {
				if (user.getEmail().equalsIgnoreCase(al.getEmailAdd())) {
					bal = index % 2;
					if (bal == 0) {
						sb.append("<tr bgcolor=\"#F0F0F0\" align=\"left\">");
					} else if (bal == 1) {
						sb.append("<tr bgcolor=\"#FFF8C6\" align=\"left\">");
					}
					sb.append("<td width=\"5\"><input type=\"radio\" name=\"amendAppLQlist\" value=\"" + al.getTime() + "|" + al.getEmailAdd() + "\"" + "><br></td>");
					sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + al.getTime() + "</font></td>");
					sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + al.getEmailAdd() + "</font></td>");
					sb.append("<td width=\"60\"><font face=\"verdana\" size=\"1\">" + al.getNumOfDays() + "</font></td>");
					sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + al.getStartDate() + "</font></td>");
					sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + al.getEndDate() + "</font></td>");
					sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + al.getSupervisor() + "</font></td>");
					sb.append("<td width=\"130\"><font face=\"verdana\" size=\"1\">" + al.getLeaveType() + "</font></td>");
					sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + al.getRemark() + "</font></td>");
					sb.append("</tr>");
					index = index + 1;
				}
			}
		}
		LeaveQueueService lqs = new LeaveQueueService();
		for (LeaveQueue lq : lqs.getLeaveQueue()) {
			if (user.getEmail() != null) {
				if (user.getEmail().equalsIgnoreCase(lq.getEmailAdd())) {
					bal = index % 2;
					if (bal == 0) {
						sb.append("<tr bgcolor=\"#F0F0F0\">");
					} else if (bal == 1) {
						sb.append("<tr bgcolor=\"#FFF8C6\">");
					}
					sb.append("<td width=\"5\"><input type=\"radio\" name=\"amendAppLQlist\" value=\"" + lq.getTime() + "|" + lq.getEmailAdd() + "\"" + "><br></td>");
					sb.append("<td width=\"128\"><font face=\"verdana\" size=\"1\">" + lq.getTime() + "</font></td>");
					sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + lq.getEmailAdd() + "</font></td>");
					sb.append("<td width=\"40\"><font face=\"verdana\" size=\"1\">" + lq.getNumOfDays() + "</font></td>");
					sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + lq.getStartDate() + "</font></td>");
					sb.append("<td width=\"220\"><font face=\"verdana\" size=\"1\">" + lq.getEndDate() + "</font></td>");
					sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + lq.getSupervisor() + "</font></td>");
					sb.append("<td width=\"85\"><font face=\"verdana\" size=\"1\">" + lq.getLeaveType() + "</font></td>");
					sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + lq.getRemark() + "</font></td>");
					sb.append("</tr>");
					index = index + 1;
				}
			}
		}
		return sb.toString();
	}


	public String amendLeaveList() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		ApprovedLeaveService als = new ApprovedLeaveService();
		for (MCApprovedLeave al : als.getApprovedLeave()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#F0F0F0\" align=\"left\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#FFF8C6\" align=\"left\">");
			}
			sb.append("<td width=\"5\"><input type=\"radio\" name=\"amendAppLQlist\" value=\"" + al.getTime() + "|" + al.getEmailAdd() + "\"" + "><br></td>");
			sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + al.getTime() + "</font></td>");
			sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + al.getEmailAdd() + "</font></td>");
			sb.append("<td width=\"60\"><font face=\"verdana\" size=\"1\">" + al.getNumOfDays() + "</font></td>");
			sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + al.getStartDate() + "</font></td>");
			sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + al.getEndDate() + "</font></td>");
			sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + al.getSupervisor() + "</font></td>");
			sb.append("<td width=\"130\"><font face=\"verdana\" size=\"1\">" + al.getLeaveType() + "</font></td>");
			sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + al.getRemark() + "</font></td>");
			sb.append("</tr>");
			index = index + 1;
		}
		LeaveQueueService lqs = new LeaveQueueService();
		for (LeaveQueue lq : lqs.getLeaveQueue()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			}
			sb.append("<td width=\"5\"><input type=\"radio\" name=\"amendAppLQlist\" value=\"" + lq.getTime() + "|" + lq.getEmailAdd() + "\"" + "><br></td>");
			sb.append("<td width=\"128\"><font face=\"verdana\" size=\"1\">" + lq.getTime() + "</font></td>");
			sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + lq.getEmailAdd() + "</font></td>");
			sb.append("<td width=\"40\"><font face=\"verdana\" size=\"1\">" + lq.getNumOfDays() + "</font></td>");
			sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + lq.getStartDate() + "</font></td>");
			sb.append("<td width=\"220\"><font face=\"verdana\" size=\"1\">" + lq.getEndDate() + "</font></td>");
			sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + lq.getSupervisor() + "</font></td>");
			sb.append("<td width=\"85\"><font face=\"verdana\" size=\"1\">" + lq.getLeaveType() + "</font></td>");
			sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + lq.getRemark() + "</font></td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String mctRejLeaveQlist() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RejectedLeaveService rls = new RejectedLeaveService();
		for (RejectedLeave rl : rls.getRejectedLeave()) {
			if (user.getEmail() != null) {
				if (user.getEmail().equalsIgnoreCase(rl.getEmailAdd())) {
					bal = index % 2;
					if (bal == 0) {
						sb.append("<tr bgcolor=\"#F0F0F0\">");
					} else if (bal == 1) {
						sb.append("<tr bgcolor=\"#FFF8C6\">");
					}
					sb.append("<td width=\"128\"><font face=\"verdana\" size=\"1\">" + rl.getTime() + "</font></td>");
					sb.append("<td width=\"210\"><font face=\"verdana\" size=\"1\">" + rl.getEmailAdd() + "</font></td>");
					sb.append("<td width=\"60\"><font face=\"verdana\" size=\"1\">" + rl.getNumOfDays() + "</font></td>");
					sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + rl.getStartDate() + "</font></td>");
					sb.append("<td width=\"220\"><font face=\"verdana\" size=\"1\">" + rl.getEndDate() + "</font></td>");
					sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + rl.getSupervisor() + "</font></td>");
					sb.append("<td width=\"85\"><font face=\"verdana\" size=\"1\">" + rl.getLeaveType() + "</font></td>");
					sb.append("<td width=\"200\"align=\"left\"><font face=\"verdana\" size=\"1\">" + rl.getRemark() + "</font></td>");
					sb.append("</tr>");
					index = index + 1;
				}
			}
		}
		return sb.toString();
	}

	public String mctHistoryList() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		HistoryService hs = new HistoryService();
		for (History history : hs.getHistory()) {
			if (user.getEmail() != null) {
				if (user.getEmail().equalsIgnoreCase(history.getEmailAdd())) {
					bal = index % 2;
					if (bal == 0) {
						sb.append("<tr bgcolor=\"#FFF8C6\">");
					} else if (bal == 1) {
						sb.append("<tr bgcolor=\"#F0F0F0\">");
					}
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getTime() + "</td>");
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getEmailAdd() + "</td>");
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getNumOfDays() + "</td>");
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getStartDate() + "</td>");
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getEndDate() + "</td>");
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getSupervisor() + "</td>");
					sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getLeaveType() + "</td>");
					sb.append("<td align=\"left\"><font face=\"verdana\" size=\"1\">" + history.getRemark() + "</td>");
					sb.append("</tr>");
					index = index + 1;
				}
			}
		}
		return sb.toString();
	}


	public String historyList() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		HistoryService hs = new HistoryService();
		for (History history : hs.getHistory()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\">");
			}
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getTime() + "</td>");
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getEmailAdd() + "</td>");
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getNumOfDays() + "</td>");
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getStartDate() + "</td>");
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getEndDate() + "</td>");
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getSupervisor() + "</td>");
			sb.append("<td><font face=\"verdana\" size=\"1\">" + history.getLeaveType() + "</td>");
			sb.append("<td align=\"left\"><font face=\"verdana\" size=\"1\">" + history.getRemark() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String delHistoryList() {
		int index = 0;
		int bal = 0;
		StringBuffer sb = new StringBuffer();
		HistoryService hs = new HistoryService();
		for (History history : hs.getHistory()) {
			bal = index % 2;
			if (bal == 0) {
				sb.append("<tr bgcolor=\"#FFF8C6\" align=\"left\">");
			} else if (bal == 1) {
				sb.append("<tr bgcolor=\"#F0F0F0\" align=\"left\">");
			}
			sb.append("<td width=\"10\"><input type=\"checkbox\" name=\"delHisList\" value=\"" + history.getTime() + "|" + history.getEmailAdd() + "\"" + "><br></td>");
			sb.append("<td width=\"130\"><font face=\"verdana\" size=\"1\">" + history.getTime() + "</td>");
			sb.append("<td width=\"200\"><font face=\"verdana\" size=\"1\">" + history.getEmailAdd() + "</td>");
			sb.append("<td width=\"80\"><font face=\"verdana\" size=\"1\">" + history.getNumOfDays() + "</td>");
			sb.append("<td width=\"180\"><font face=\"verdana\" size=\"1\">" + history.getStartDate() + "</td>");
			sb.append("<td width=\"190\"><font face=\"verdana\" size=\"1\">" + history.getEndDate() + "</td>");
			sb.append("<td width=\"150\"><font face=\"verdana\" size=\"1\">" + history.getSupervisor() + "</td>");
			sb.append("<td width=\"120\"><font face=\"verdana\" size=\"1\">" + history.getLeaveType() + "</td>");
			sb.append("<td width=\"130\"><font face=\"verdana\" size=\"1\">" + history.getRemark() + "</td>");
			sb.append("</tr>");
			index = index + 1;
		}
		return sb.toString();
	}


	public String mctEmpLeaveDetails() {
		StringBuffer sb = new StringBuffer();
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
			if (user.getEmail() != null) {
				if (user.getEmail().equalsIgnoreCase(eld.getName())) {
					sb.append("<tr>");
					sb.append("<td width=\"190px\">Email Address</td>");
					sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getName() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Year</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getYear() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Last Year's Balance</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getLastYearBalance() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Entitled Annual</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Entitled Compensation</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Annual Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getAnnualLeave() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Sick Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getSickLeaveFP() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Birthday Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getBirthdayLeave() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>No Pay Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getBirthdayLeave() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Compensation Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Compassionate Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getCompassionateLeave() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Maternity Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getMaternityLeave() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Wedding Leave</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getMarriageLeave() + "\" readOnly/>" + "</div></td>");
					sb.append("</tr><tr>");
					sb.append("<td>Balance</td>");
					sb.append("<td><div class=\"textbox\">" + 
							"<input type=\"text\" value=\"" + eld.getBalance() + " Days" + "\" readOnly/>" + "</div></td>");
					sb.append("</tr>");
					sb.append("<tr><td colspan=\"2\"><hr/></td></tr>");
				}
			}
		}
		return sb.toString();
	}


	public String empLeaveDetails() {
		StringBuffer sb = new StringBuffer();
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
			sb.append("<tr>");
			sb.append("<td width=\"190px\">Name</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getName() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Year</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getYear() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Last Year's Balance</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getLastYearBalance() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Entitled Annual</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Entitled Compensation</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Annual Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getAnnualLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Sick Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getSickLeaveFP() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Birthday Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getBirthdayLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>No Pay Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getNoPayLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Compensation Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Compassionate Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getCompassionateLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Maternity Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getMaternityLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Wedding Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getMarriageLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Balance</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getBalance() + " Days" + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("</tr><tr><td colspan=\"2\"><hr></td></tr>");
		}
		return sb.toString();
	}


	public String delEmpLeaveDetails() {
		StringBuffer sb = new StringBuffer();
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
			sb.append("<tr>");
			sb.append("<td bgcolor=\"#C0C0C0\" width=\"10\" rowspan=\"14\" valign=\"top\"><input type=\"checkbox\" name=\"delEmpLeaveDetList\" value=\"" + eld.getName() + "|" + eld.getYear() + "\"" + "><br></td>");
			sb.append("<td width=\"190px\">Name</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getName() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Year</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getYear() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Last Year's Balance</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getLastYearBalance() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Entitled Annual</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Entitled Compensation</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Annual Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getAnnualLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Sick Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getSickLeaveFP() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Birthday Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getBirthdayLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>No Pay Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getNoPayLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Compensation Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getEntitledAnnual() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Compassionate Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getCompassionateLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Maternity Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getMaternityLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Wedding Leave</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getMarriageLeave() + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td>Balance</td>");
			sb.append("<td width=\"540px\"><div class=\"textbox\">" + 
					"<input type=\"text\" value=\"" + eld.getBalance() + " Days" + "\" readOnly/>" + "</div></td>");
			sb.append("</tr><tr>");
			sb.append("<td colspan=\"3\"><hr/></td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}
	
	public static void importEmployee(String emailAddress, String empName, String status){
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String subject = "Import Employee Status";
		String msgBody = "Dear " + empName + ",\n\n" +
		"This is to inform you that import progress was "+status+" \n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(emailAddress, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifyCompExp - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifyCompExp - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifyCompExp - Exception error: " + e.getMessage());
		}
	}
	
	public void notifyCompExp(String emailAddress, String empName){
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String subject = "Compensation Leave Expired Notify";
		String msgBody = "Dear " + empName + ",\n\n" +
		"This is to inform you that your compensation leave will expire in 1 month."+" \n\n" +
		"Please apply compensation leave before it expired." + "\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(emailAddress, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifyCompExp - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifyCompExp - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifyCompExp - Exception error: " + e.getMessage());
		}
	}


	public void notifyEmployee(String empName, String time, String emp, String numOfDays, String leaveType, String remark, 
			String startDate, String endDate, String status) throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String sb = "";
		if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
			  sb = "Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
				   "End Date(dd/mm/yyyy): " + endDate + "\n\n";
				 	
		}

		String subject = "Leave Request Status - " + status;
		String msgBody = "Dear " + empName + ",\n\n" +
		"This is to inform you that your request has been " + status + ".\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + empName + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		sb +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(emp, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifyEmployee - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifyEmployee - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifyEmployee - Exception error: " + e.getMessage());
		}
	}


	public void notifySupervisorAmend(String recipient, String time, String emp, String numOfDays, String leaveType,
			String remark, String startDate, String endDate, String changeType, String newStartDate, String newEndDate, String oldLeaveType)
	throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String sb = "";
		if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
			  sb = "Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
						"End Date(dd/mm/yyyy): " + endDate + "\n\n" +
						"New Start Date (dd/mm/yyyy): " + newStartDate + "\n\n" +
						"New End Date(dd/mm/yyyy): " + newEndDate + "\n\n";
				 	
		}

		String subject = "Employee Leave Request - Amendment/Cancellation";
		String msgBody = "Dear " + recipient + ",\n\n" +
		"The following employee has requested for your approval of amendmenet / cancellation of leave.\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Action Type: " + changeType + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + emp + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
//		"Previous Leave Type: " + oldLeaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		sb +
		"Please visit the site https://mcleaveapp.appspot.com to approve / reject the request.\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(recipient, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifySupervisorAmend - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifySupervisorAmend - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifySupervisorAmend - Exception error: " + e.getMessage());
		}
	}


	public void notifySupervisor(String recipientName, String recipient, String time, String emp, String numOfDays, String leaveType,
			String remark, String startDate, String endDate)
	throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String sb = "";
		if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
			  sb = "Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
				   "End Date(dd/mm/yyyy): " + endDate + "\n\n";
				 	
		}

		String subject = "Employee Leave Request";
		String msgBody = "Dear " + recipientName + ",\n\n" +
		"The following employee has requested for your approval of leave.\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + emp + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		sb +
		"Please visit the site https://mcleaveapp.appspot.com to approve / reject the request.\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(recipient, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifySupervisor - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifySupervisor - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifySupervisor - Exception error: " + e.getMessage());
		}
	}


	public void notifyHRAmend(String hrEmail, String time, String emp, String numOfDays, String leaveType,
			String remark, String startDate, String endDate, String changeType, String newStartDate,
			String newEndDate, String oldLeaveType)
	throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String sb = "";
		if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
			  sb = "Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
						"End Date(dd/mm/yyyy): " + endDate + "\n\n" +
						"New Start Date (dd/mm/yyyy): " + newStartDate + "\n\n" +
						"New End Date(dd/mm/yyyy): " + newEndDate + "\n\n";
				 	
		}

		String subject = "Employee Leave Request Notification - Amendment/Cancellation";
		String msgBody = "Dear HR\n\n" +
		"This is to inform you that the employee below has applied for amendment/cancellation of leave.\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Action Type: " + changeType + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + emp + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
//		"Previous Leave Type: " + oldLeaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		sb +
		"Please visit the site https://mcleaveapp.appspot.com to refer.\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			String [] hrEmailArray = hrEmail.replace("[", "").replace("]", "").split(",");
			InternetAddress[] myList= new InternetAddress[hrEmailArray.length];
			if(hrEmailArray.length > 0){
				for(int i =0; i < hrEmailArray.length; i++){
					myList[i] = new InternetAddress(hrEmailArray[i]);
				}
			}
			msg.setRecipients(Message.RecipientType.TO, myList);
//			msg.addRecipient(Message.RecipientType.TO,
//					new InternetAddress(recipient, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifyHRAmend - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifyHRAmend - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifyHRAmend - Exception error: " + e.getMessage());
		}
	}


	public void notifyHR(String hrEmail, String time, String emp, String numOfDays, String leaveType,
			String remark, String startDate, String endDate)
	throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		String sb = "";
		if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
			  sb = "Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
				   "End Date(dd/mm/yyyy): " + endDate + "\n\n";
				 	
		}

		String subject = "Employee Leave Request Notification";
		String msgBody = "Dear HR\n\n" +
		"This is to inform you that the employee below has applied for leave.\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + emp + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		sb +
		"Please visit the site https://mcleaveapp.appspot.com to refer.\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			String [] hrEmailArray = hrEmail.replace("[", "").replace("]", "").split(",");
			InternetAddress[] myList= new InternetAddress[hrEmailArray.length];
			if(hrEmailArray.length > 0){
				for(int i =0; i < hrEmailArray.length; i++){
					myList[i] = new InternetAddress(hrEmailArray[i]);
				}
			}
			msg.setRecipients(Message.RecipientType.TO, myList);
//			msg.addRecipient(Message.RecipientType.TO,
//					new InternetAddress(hrEmail, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifyHR - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifyHR - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifyHR - Exception error: " + e.getMessage());
		}
	}


	public void notifySickMail(String recipientName, String time, String emp, String numOfDays, String leaveType,
			String remark, String startDate, String endDate, String recipient)
	throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String subject = "Employee Leave Request - Sick Leave";
		String msgBody = "Dear " + recipientName + "\n\n" +
		"This is to inform you that the employee below has applied for sick leave.\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + emp + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		"Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
		"End Date(dd/mm/yyyy): " + endDate + "\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(recipient, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * notifySickMail - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * notifySickMail - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * notifySickMail - Exception error: " + e.getMessage());
		}
	}


	public void insufficientBalMail(String time, String emp, String numOfDays, String leaveType, String remark, 
			String startDate, String endDate)
	throws MessagingException {
		String emailFrom = "";
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String subject = "Leave Request - Insufficient Balance";
		String msgBody = "Dear " + emp + ",\n\n" +
		"This is to inform you that you do not have sufficient leave balance to apply for the leave you requested.\n\n" +
		"DETAILS" + "\n" +
		"--------------" + "\n\n" +
		"Timestamp: " + time + "\n\n" +
		"Employee Name: " + emp + "\n\n" +
		"Number of Days: " + numOfDays + "\n\n" +
		"Leave Type: " + leaveType + "\n\n" +
		"Remark: " + remark + "\n\n" +
		"Start Date (dd/mm/yyyy): " + startDate + "\n\n" +
		"End Date(dd/mm/yyyy): " + endDate + "\n\n" +
		"Please notify HR if you have any queries.\n\n" +
		"Sincerely," + "\n\n" + 
		"Master Concept Group";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(emp, ""));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("Misc * insufficientBalMail - AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("Misc * insufficientBalMail - MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Misc * insufficientBalMail - Exception error: " + e.getMessage());
		}
	}


	public void postMailToSysAdmin(String message) throws MessagingException {
		String emailFrom = "";
		String systemSupport = "";
		
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				emailFrom = set.getEmailSenderAcc();
				systemSupport = set.getSysAdminEmailAdd();
			
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailFrom, "HR"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(systemSupport, "System Admin"));
			msg.setSubject("GAE Leave Apps Error Alert");
			msg.setText(message);
			Transport.send(msg);
		} catch (AddressException e) {
			log.error("AddressException error: " + e.getMessage());
		} catch (MessagingException e) {
			log.error("MessagingException error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception error: " + e.getMessage());
		}
	}


	public void storeInGDocsHistory(String nameValuePairs) {
		try {
			String spdSvAcc = "";
			String spdSvAccPwd = "";
			
			SettingService ss = new SettingService();
			for (Setting set : ss.getSetting()) {
					spdSvAcc = set.getSpreadsheetServiceAcc();
					spdSvAccPwd = set.getSpreadsheetServiceAccPass();
				
			}
			
			SpreadsheetService myService = new SpreadsheetService("wise");
			myService.setUserCredentials(spdSvAcc, spdSvAccPwd);
			URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			for (int i = 0; i < spreadsheets.size(); i++) {
				SpreadsheetEntry entry = spreadsheets.get(i);
				if (entry.getTitle().getPlainText().equalsIgnoreCase("MCT Leave History Records")) {
					List worksheets = entry.getWorksheets();
					WorksheetEntry worksheetEntry = (WorksheetEntry)worksheets.get(0);
					URL listFeedUrl = worksheetEntry.getListFeedUrl();
					ListEntry newEntry = new ListEntry();
					for (String nameValuePair : nameValuePairs.split(",")) {
						// Then, split by the equal sign.
						String[] parts = nameValuePair.split("=", 2);
						String tag = parts[0]; // such as "name"
						String value = parts[1]; // such as "Rosa"
						newEntry.getCustomElements().setValueLocal(tag, value);
					}
					ListEntry insertedRow = myService.insert(listFeedUrl, newEntry);
				}
			}
		} catch (Exception e) {
			log.error("Misc * storeInGDocsHistory - error: " + e.getMessage());
		}
	}


	public void storeInGDocsApprovedHistory(String nameValuePairs) {
		try {
			String spdSvAcc = "";
			String spdSvAccPwd = "";
			
			SettingService ss = new SettingService();
			for (Setting set : ss.getSetting()) {
					spdSvAcc = set.getSpreadsheetServiceAcc();
					spdSvAccPwd = set.getSpreadsheetServiceAccPass();
				
			}
			
			SpreadsheetService myService = new SpreadsheetService("wise");
			myService.setUserCredentials(spdSvAcc, spdSvAccPwd);
			URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			for (int i = 0; i < spreadsheets.size(); i++) {
				SpreadsheetEntry entry = spreadsheets.get(i);
				if (entry.getTitle().getPlainText().equalsIgnoreCase("MCT Approved Leave History Records")) {
					List worksheets = entry.getWorksheets();
					WorksheetEntry worksheetEntry = (WorksheetEntry)worksheets.get(0);
					URL listFeedUrl = worksheetEntry.getListFeedUrl();
					ListEntry newEntry = new ListEntry();
					for (String nameValuePair : nameValuePairs.split(",")) {
						// Then, split by the equal sign.
						String[] parts = nameValuePair.split("=", 2);
						String tag = parts[0]; // such as "name"
						String value = parts[1]; // such as "Rosa"
						newEntry.getCustomElements().setValueLocal(tag, value);
					}
					ListEntry insertedRow = myService.insert(listFeedUrl, newEntry);
				}
			}
		} catch (Exception e) {
			log.error("Misc * storeInGDocsApprovedHistory - error: " + e.getMessage());
		}
	}


	public void storeInGDocsRejectedHistory(String nameValuePairs) {
		try {
			String spdSvAcc = "";
			String spdSvAccPwd = "";
			
			SettingService ss = new SettingService();
			for (Setting set : ss.getSetting()) {
					spdSvAcc = set.getSpreadsheetServiceAcc();
					spdSvAccPwd = set.getSpreadsheetServiceAccPass();
				
			}
			
			SpreadsheetService myService = new SpreadsheetService("wise");
			myService.setUserCredentials(spdSvAcc, spdSvAccPwd);
			URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			for (int i = 0; i < spreadsheets.size(); i++) {
				SpreadsheetEntry entry = spreadsheets.get(i);
				if (entry.getTitle().getPlainText().equalsIgnoreCase("MCT Rejected Leave History Records")) {
					List worksheets = entry.getWorksheets();
					WorksheetEntry worksheetEntry = (WorksheetEntry)worksheets.get(0);
					URL listFeedUrl = worksheetEntry.getListFeedUrl();
					ListEntry newEntry = new ListEntry();
					for (String nameValuePair : nameValuePairs.split(",")) {
						// Then, split by the equal sign.
						String[] parts = nameValuePair.split("=", 2);
						String tag = parts[0]; // such as "name"
						String value = parts[1]; // such as "Rosa"
						newEntry.getCustomElements().setValueLocal(tag, value);
					}
					ListEntry insertedRow = myService.insert(listFeedUrl, newEntry);
				}
			}
		} catch (Exception e) {
			log.error("Misc * storeInGDocsRejectedHistory - error: " + e.getMessage());
		}
	}
}
