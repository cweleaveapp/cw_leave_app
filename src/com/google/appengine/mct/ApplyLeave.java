package com.google.appengine.mct;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.appengine.util.ConstantUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;


import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.entities.EmployeeLeaveDetails;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

@SuppressWarnings("serial")
public class ApplyLeave extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ApplyLeave.class);

	public static String errorMessage = "";

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(ApplyLeave.class);
		Map<String, String> errorMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		String numOfDays = req.getParameter("numOfDays");
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		String leaveType = req.getParameter("leaveType");
		String remark = req.getParameter("remark");
		String approvalFrom = req.getParameter("approvalFrom");
		String attachmentUrl = req.getParameter("attachmentUrl");
		req.setAttribute("numOfDays", numOfDays);
		req.setAttribute("startDate", startDate);
		req.setAttribute("endDate", endDate);
		req.setAttribute("leaveType", leaveType);
		req.setAttribute("remark", remark);
		req.setAttribute("attachmentUrl", attachmentUrl);
		
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		boolean existInDatabase = false;
		Calendar currentYear = Calendar.getInstance();
		try {
			currentYear.setTime(standardDF.parse(startDate));
			
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		Integer yr = currentYear.get(Calendar.MONTH) > 2 ?
				currentYear.get(Calendar.YEAR) :
			currentYear.get(Calendar.YEAR)-1;
		String year = yr.toString();
		String timeNow = "";
		Misc misc = new Misc();
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		String isAdmin = ConstantUtils.FALSE;
		String eAdd = (String)req.getSession().getAttribute("emailAdd");
		AdministratorService addService = new AdministratorService();
		Administrator admin = addService.findAdministratorByEmailAddress(eAdd);
		EmployeeService  ems = new EmployeeService();
		MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress", eAdd);
		// get actual number of day for no pay leave
		String noPayLeaveDay = numOfDays;
		String othersDay = numOfDays;
		
		if(StringUtils.isNotBlank(admin.getEmailAddress())){
			 isAdmin = ConstantUtils.TRUE; 
		}else{
			req.setAttribute("approvalFrom", employee.getSupervisor());
		}

//		 check if numOfDays day difference is correct
//		 check public holiday, saturday, sunday, halfday
		try{
//		URL metafeedUrl = new URL(ConstantUtils.SPREADSSHEETS_FEEDURL);
//		SpreadsheetEntry spreadSheetEntry = null;
//		Calendar currentYear = Calendar.getInstance();
//		SpreadsheetService service = new SpreadsheetService(ConstantUtils.MCKL_PUBLIC_HOLIDAYS+currentYear.get(Calendar.YEAR));
//		SettingService ss = new SettingService();
//		List<Setting> acc = ss.getSetting();
//		String spreadsheetAcc = "";
//		String spreadsheetPass = "";
//		for(Setting setting : acc){
//			if(ConstantUtils.SPREADSSHEET_SERVICE_ACCOUNT.equals(setting.getPropertyName())){
//				spreadsheetAcc = setting.getPropertyValue();
//			}else if(ConstantUtils.SPREADSSHEET_SERVICE_ACCOUNT_PASS.equals(setting.getPropertyName())){
//				spreadsheetPass = setting.getPropertyValue();
//			}
//		}
//		service.setUserCredentials(spreadsheetAcc, spreadsheetPass);
//		SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
//		String spreadsheetName = ConstantUtils.MCKL_PUBLIC_HOLIDAYS+currentYear.get(Calendar.YEAR);
//		
//		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
//		for (int i = 0; i < spreadsheets.size(); i++) {
//			SpreadsheetEntry entry = spreadsheets.get(i);
//			if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
//				spreadSheetEntry = entry;
//				break;
//			}
//		}
//		List<String> holidayList = new ArrayList<String>();
//		if(spreadSheetEntry != null){
//			List<WorksheetEntry> worksheets = spreadSheetEntry.getWorksheets();
//			if(worksheets.size() > 0) {
//				WorksheetEntry worksheet = worksheets.get(0);
//				URL listFeedUrl = worksheet.getListFeedUrl();
//				ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
//				
//				 for (ListEntry row : listFeed.getEntries()) {
//				      for (String tag : row.getCustomElements().getTags()) {
//				    	  if(tag.equals("date"))
//				    		  //retrieve all holiday spreadsheet from column date value
//				    		  holidayList.add(row.getCustomElements().getValue(tag));
//				      }
//				      
//				    }
//			}
//		}
			
		List<String> holidayList = new ArrayList<String>();
		RegionalHolidaysService rhs = new RegionalHolidaysService();
		List<RegionalHolidays> list = rhs.getRegionalHolidays();
		for(RegionalHolidays rh : list){
			if(employee.getRegion().equals(rh.getRegion())){
				holidayList.add(rh.getDate());
			}
		}
		
		List<String> totalDate = new ArrayList<String>();
		
		//calculate diff date from start date till end date
		long diffdate = daysBetween(standardDF.parse(startDate),standardDF.parse(endDate));
		long dateremove = 0;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(standardDF.parse(startDate));
		//check if Sunday or Saturday 
		for(int i=0; i<diffdate; i++){
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				dateremove += 1;
			}
			totalDate.add(standardDF.format(cal.getTime()));
			cal.add(Calendar.DATE, 1);
		}
		
		//create another new calendar instance to avoid addition add date cause by  cal.add(Calendar.DATE, 1); above
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(standardDF.parse(startDate)); 
		
//		SimpleDateFormat calendarDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		if(!holidayList.isEmpty()){
			for(String date : holidayList){
				for(int i=0; i< diffdate; i++){
					if(date.equals(totalDate.get(i))){
						dateremove += 1;
					}
				}
				
			}
		}
		
		/* check if numOfDays contains other characters that are not digits */
		
		if(!NumberUtils.isNumber(numOfDays)) {
				log.error(""+properties.getProperty("wrong.number.of.days"));
				errorMap.put("wrong.number.of.days", properties.getProperty("wrong.number.of.days"));
				numOfDays = "0";
		}
		
		Double originalNumOfDays = Double.parseDouble(numOfDays);
		
//		if (ConstantUtils.NO_PAY_LEAVE.equals(leaveType)) {
//			
//			if(originalNumOfDays != diffdate){
//				 log.error(""+properties.getProperty("different.number.of.days"));
//				 errorMap.put("different.number.of.days", properties.getProperty("different.number.of.days"));
//				 
//			}
//		}
		
		
		numOfDays = Long.toString(diffdate-dateremove);
		String str = String.valueOf(originalNumOfDays).substring(Math.max(String.valueOf(originalNumOfDays).length() - 2, 0));
   		if(StringUtils.equals(".5",str)){
			Double num = Double.parseDouble(numOfDays) - 0.5;
			numOfDays = num.toString();
		}
		
		//number of day must same with number of day from start date until end date 
   			if(originalNumOfDays != Double.parseDouble(numOfDays)){
   			 log.error(""+properties.getProperty("different.number.of.days"));
   			 errorMap.put("different.number.of.days", properties.getProperty("different.number.of.days"));
   			 
   			}
		
		
		req.setAttribute("errorMap", errorMap);
			if(!errorMap.isEmpty()){
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
			}
		
		} catch (Exception e) {
			log.error(""+properties.getProperty("system.error.please.contact.administrator"));
			errorMap.put("system.error.please.contact.administrator", properties.getProperty("system.error.please.contact.administrator"));
			req.setAttribute("errorMap", errorMap);
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
					
				return;
			} catch (ServletException es) {
				// TODO Auto-generated catch block
				es.printStackTrace();
			}
		}
			
		try {
			if (StringUtils.isNotBlank(leaveType) 
					&& StringUtils.isNotBlank(approvalFrom)
					&& StringUtils.isNotBlank(remark)) {
				String region = "";
				String empName = "";
				String birthDate = "";
				double checkBalance = 0;
				double checkBirthdayBalance = 0;
				double checkCompensationBalance = 0;
				double compBalExp = 0;
				double lastYearBal = 0;
				double entitledAnnual = 0;
				double entitledComp = 0;
				double noPayLeave = 0;
				double sickLeave = 0;
				double annualLeave = 0;
				double compensationLeave = 0;
				double compassionateLeave = 0;
				double birthdayLeave = 0;
				double maternityLeave = 0;
				double weddingLeave = 0;
				double others = 0;
				double balance = 0;
				
				/* get employee's region */
				EmployeeService es = new EmployeeService();
				for (MCEmployee emp: es.getMCEmployees()) {
					if (emp.getEmailAddress().equalsIgnoreCase(eAdd)) {
						region = emp.getRegion();
						empName = emp.getFullName();
						birthDate = emp.getBirthDate();
					}
				}
				
				LeaveEntitleService les = new LeaveEntitleService();
				LeaveEntitle leaveEntitle = les.getLeaveEntitlebyRegion(region);
				ApprovedLeaveService als = new ApprovedLeaveService();
				EmailSettingService ess = new EmailSettingService();
				MCEmployee supervisor = es.findMCEmployeeByColumnName("emailAddress", approvalFrom.toLowerCase());
				String recipientName = supervisor.getFullName();

				/* retrieve leave balance first */
				EmployeeLeaveDetails eld = elds.findEmployeeLeaveDetails(employee.getEmailAddress(), year);
						existInDatabase = true;
						checkBalance = Double.parseDouble(eld.getBalance());
						checkBirthdayBalance = Double.parseDouble(eld.getBirthdayLeave());
						//checkCompensationBalance = Double.parseDouble(eld.getEntitledAnnual()) - Double.parseDouble(eld.getEntitledAnnual());
						// minus expire entitle compensation leave
						//Calendar current = Calendar.getInstance();
						//current.add(Calendar.DATE, -Integer.parseInt(leaveEntitle.getExgratiaLeaveExp()));
						// leave it first, TODO in future
//						List<ApprovedLeave> app = als.getApproveLeaveListByEmail(eAdd);
//						for(ApprovedLeave appleave : app){
//							// check expire 3 month compensation leave balance
//							if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(appleave.getLeaveType())){
//								if(standardDF.parse(appleave.getTime()).before(current.getTime())){
//									compBalExp += Double.parseDouble(appleave.getNumOfDays());
//								}
//							}
//							
//						}
//						checkCompensationBalance = checkCompensationBalance - compBalExp;
						lastYearBal = Double.parseDouble(eld.getLastYearBalance());
						entitledAnnual = Double.parseDouble(eld.getEntitledAnnual());
//						entitledComp = Double.parseDouble(eld.getEntitledAnnual());
						noPayLeave = Double.parseDouble(eld.getNoPayLeave());
						sickLeave = Double.parseDouble(eld.getSickLeaveFP());
						annualLeave = Double.parseDouble(eld.getAnnualLeave());
//						compensationLeave = Double.parseDouble(eld.getEntitledAnnual());
						compassionateLeave = Double.parseDouble(eld.getCompassionateLeave());
						birthdayLeave = Double.parseDouble(eld.getBirthdayLeave());
						maternityLeave = Double.parseDouble(eld.getMaternityLeave());
						weddingLeave = Double.parseDouble(eld.getMarriageLeave());
						others = Double.parseDouble(eld.getOthers());
						balance = Double.parseDouble(eld.getBalance());
					
					
				if (existInDatabase == false) {
					log.error(""+properties.getProperty("invalid.emp.leave.detail"));
					errorMap.put("invalid.emp.leave.detail", properties.getProperty("invalid.emp.leave.detail"));
					req.setAttribute("errorMap", errorMap);
					
				} else if (existInDatabase == true) {
					LeaveQueueService lqs = new LeaveQueueService();
					HistoryService hs = new HistoryService();
					

					/*** Annual Leave ***/
					if (ConstantUtils.ANNUAL_LEAVE.equals(leaveType)) {
						if (checkBalance >= Double.parseDouble(numOfDays)) {
							timeNow = Misc.now();
							hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region, "", "",eAdd);
							lqs.addLeaveQueue(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, "", "", "");
							misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate);
							List<String> eAddArray = new ArrayList<String>();
							for(int i=0; i < ess.getEmailSettingList().size(); i++){
								EmailSetting esetting = ess.getEmailSettingList().get(i);
								String regionArray [] = esetting.getRegion().split(",");
								for(int z=0; z<regionArray.length; z++){
									if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
										log.debug("send email to "+esetting.getEmailAddress());
										eAddArray.add(esetting.getEmailAddress());
										
									}
								}
							}
							
							Queue queue = QueueFactory.getQueue("SendHREmailQueue");
							queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
									.param("timeNow", timeNow).param("empName", empName)
									.param("numOfDays", numOfDays).param("leaveType", leaveType)
									.param("remark", remark).param("startDate", startDate)
									.param("endDate", endDate).method(Method.POST));
							
							/* write to GDocs for record */
							String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
							+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
							+",LeaveType="+leaveType+",Remark="+remark;
//							misc.storeInGDocsHistory(tmpStr);
						} else {
							log.error(""+properties.getProperty("invalid.insufficient.leave"));
							errorMap.put("invalid.insufficient.leave", properties.getProperty("invalid.insufficient.leave"));
//							misc.insufficientBalMail(timeNow, eAdd, numOfDays, leaveType, remark, startDate, endDate);
						}

					/*** Sick Leave ***/
					} else if (ConstantUtils.SICK_LEAVE.equals(leaveType)) {
						Calendar cal = Calendar.getInstance();
						Calendar curr = Calendar.getInstance();
			       		cal.setTime(standardDF.parse(employee.getHiredDate()));
			       		int yearCal = curr.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
						sickLeave = sickLeave + Double.parseDouble(numOfDays);
						int allowSickLeave = 0;
						LeaveEntitle le = les.getLeaveEntitlebyRegion(employee.getRegion());
							List<SickLeave> sickLeaveList = les.getSickLeaveById(le.getId());
							
							for(SickLeave sl : sickLeaveList){
								if(ConstantUtils.LESS_THAN.equals(sl.getSickLeaveType())){
									if(yearCal < Integer.parseInt(sl.getSickLeaveYear())){
										allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
									}
								}
								else if(ConstantUtils.LESS_THAN_OR_EQUAL.equals(sl.getSickLeaveType())){
									SickLeave lessThan = les.getLessThanYear(le.getId());
									if(lessThan != null ){
											if(yearCal <= Integer.parseInt(sl.getSickLeaveYear()) &&
													yearCal >= Integer.parseInt(lessThan.getSickLeaveYear())){
												allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
											}
									}
									
								}
								else if(ConstantUtils.GREATER_THAN.equals(sl.getSickLeaveType())){
									if(yearCal > Integer.parseInt(sl.getSickLeaveYear())){
										allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
									}
								}
								else if(ConstantUtils.GREATER_THAN_OR_EQUAL.equals(sl.getSickLeaveType())){
									if(yearCal >= Integer.parseInt(sl.getSickLeaveYear())){
										allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
									}
								}
							}
							
							
							
						if(sickLeave > allowSickLeave){
							log.error(""+properties.getProperty("sick.leave.maximum.day"));
							if(allowSickLeave <= 0){
								errorMap.put("system.error.please.contact.administrator", properties.getProperty("system.error.please.contact.administrator"));
							}else{
								errorMap.put("sick.leave.maximum.day", properties.getProperty("sick.leave.maximum.day")+" "+allowSickLeave);
							}
							
							req.setAttribute("errorMap", errorMap);
						}
						if(allowSickLeave <= 0){
							log.error(""+properties.getProperty("system.error.please.contact.administrator"));
							errorMap.put("system.error.please.contact.administrator", properties.getProperty("system.error.please.contact.administrator"));
							req.setAttribute("errorMap", errorMap);
						}
						
						// not mandatory for region hong kong, china, taiwan, macau
						// temporary remove from singapore and malaysia
						if(StringUtils.isBlank(attachmentUrl)){
//							if (region.equalsIgnoreCase("Malaysia")){
//								log.error(""+properties.getProperty("sick.leave.attachment"));
//								errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
//								req.setAttribute("errorMap", errorMap);
//							} else if (region.equalsIgnoreCase("China")){
//								log.error(""+properties.getProperty("sick.leave.attachment"));
//								errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
//								req.setAttribute("errorMap", errorMap);
//							} else if (region.equalsIgnoreCase("Taiwan")){
//								log.error(""+properties.getProperty("sick.leave.attachment"));
//								errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
//								req.setAttribute("errorMap", errorMap);
//							} else if (region.equalsIgnoreCase("Singapore")){
//								log.error(""+properties.getProperty("sick.leave.attachment"));
//								errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
//								req.setAttribute("errorMap", errorMap);
//							} else if (region.equalsIgnoreCase("Macau")){
//								log.error(""+properties.getProperty("sick.leave.attachment"));
//								errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
//								req.setAttribute("errorMap", errorMap);
//							} 
			       			
			       		}
						
						if(errorMap.isEmpty()){
							String salesAdd = "";
							timeNow = Misc.now();
							if (region.equalsIgnoreCase("Malaysia")) {
								salesAdd = "sales.op.my@hkmci.com";
							} else if (region.equalsIgnoreCase("China")) {
								salesAdd = "sales.op.cn@hkmci.com";
							} else if (region.equalsIgnoreCase("Taiwan")) {
								salesAdd = "sales.op.tw@hkmci.com";
							} else if (region.equalsIgnoreCase("Singapore")) {
								salesAdd = "sales.op.sg@hkmci.com";
							} else if (region.equalsIgnoreCase("Hong Kong")) {
								salesAdd = "sales.op.hk@hkmci.com";
							} else if (region.equalsIgnoreCase("Macau")) {
								salesAdd = "sales.op.mo@hkmci.com";
							}
							List<String> eAddArray = new ArrayList<String>();
							for(int i=0; i < ess.getEmailSettingList().size(); i++){
								EmailSetting esetting = ess.getEmailSettingList().get(i);
								String regionArray [] = esetting.getRegion().split(",");
								for(int z=0; z<regionArray.length; z++){
									if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
										log.debug("send email to "+esetting.getEmailAddress());
										eAddArray.add(esetting.getEmailAddress());
										
									}
								}
							}
							
							Queue queue = QueueFactory.getQueue("SendHREmailQueue");
							queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
									.param("timeNow", timeNow).param("empName", empName)
									.param("numOfDays", numOfDays).param("leaveType", leaveType)
									.param("remark", remark).param("startDate", startDate)
									.param("endDate", endDate).method(Method.POST));
							
//							misc.notifySickMail(timeNow, eAdd, numOfDays, leaveType, remark, startDate, endDate, salesAdd);
							misc.notifySickMail(recipientName, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate, approvalFrom);
//							misc.notifySickMail(timeNow, eAdd, numOfDays, leaveType, remark, startDate, endDate, properties.getProperty("admin.account"));
							hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
							als.addApprovedLeave(timeNow, eAdd, numOfDays, startDate, 
									endDate, leaveType, approvalFrom, remark, region,"", attachmentUrl, "");
							elds.updateEmployeeLeaveDetails(empName, eAdd, year, Double.toString(lastYearBal), 
									Double.toString(entitledAnnual), Double.toString(entitledComp), 
									Double.toString(noPayLeave), Double.toString(sickLeave), 
									Double.toString(annualLeave), Double.toString(compensationLeave), 
									Double.toString(compassionateLeave), Double.toString(birthdayLeave), 
									Double.toString(maternityLeave), Double.toString(weddingLeave), 
									Double.toString(others), Double.toString(balance),
									region);
							/* update calendar */
							String desc = eAdd + " - " + ConstantUtils.SICK_LEAVE;
							misc.updateCalendar(startDate, endDate, desc, region);
							/* write to GDocs for record */
							String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
							+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
							+",LeaveType="+leaveType+",Remark="+remark;
//							misc.storeInGDocsHistory(tmpStr);
						}
						
						
					/*** No Pay Leave ***/
					} else if (ConstantUtils.NO_PAY_LEAVE.equals(leaveType)) {
						timeNow = Misc.now();
						hs.addToHistory(timeNow, eAdd, noPayLeaveDay, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
						lqs.addLeaveQueue(timeNow, eAdd, noPayLeaveDay, startDate, endDate, leaveType, approvalFrom, remark,"","","");
						misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, noPayLeaveDay, leaveType, remark, startDate, endDate);
						List<String> eAddArray = new ArrayList<String>();
						for(int i=0; i < ess.getEmailSettingList().size(); i++){
							EmailSetting esetting = ess.getEmailSettingList().get(i);
							String regionArray [] = esetting.getRegion().split(",");
							for(int z=0; z<regionArray.length; z++){
								if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
									log.debug("send email to "+esetting.getEmailAddress());
									eAddArray.add(esetting.getEmailAddress());
								}
							}
						}
						
						Queue queue = QueueFactory.getQueue("SendHREmailQueue");
						queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
								.param("timeNow", timeNow).param("empName", empName)
								.param("numOfDays", numOfDays).param("leaveType", leaveType)
								.param("remark", remark).param("startDate", startDate)
								.param("endDate", endDate).method(Method.POST));
						
						/* write to GDocs for record */
						String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+noPayLeaveDay
						+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
						+",LeaveType="+leaveType+",Remark="+remark;
//						misc.storeInGDocsHistory(tmpStr);
						
						/*** Others ***/
					} else if (ConstantUtils.OTHERS.equals(leaveType)) {
						timeNow = Misc.now();
						hs.addToHistory(timeNow, eAdd, othersDay, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
						lqs.addLeaveQueue(timeNow, eAdd, othersDay, startDate, endDate, leaveType, approvalFrom, remark,"","","");
						misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, othersDay, leaveType, remark, startDate, endDate);
						List<String> eAddArray = new ArrayList<String>();
						for(int i=0; i < ess.getEmailSettingList().size(); i++){
							EmailSetting esetting = ess.getEmailSettingList().get(i);
							String regionArray [] = esetting.getRegion().split(",");
							for(int z=0; z<regionArray.length; z++){
								if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
									log.debug("send email to "+esetting.getEmailAddress());
									eAddArray.add(esetting.getEmailAddress());
								}
							}
						}
						
						Queue queue = QueueFactory.getQueue("SendHREmailQueue");
						queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
								.param("timeNow", timeNow).param("empName", empName)
								.param("numOfDays", numOfDays).param("leaveType", leaveType)
								.param("remark", remark).param("startDate", startDate)
								.param("endDate", endDate).method(Method.POST));
						
						/* write to GDocs for record */
						String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+othersDay
						+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
						+",LeaveType="+leaveType+",Remark="+remark;
//						misc.storeInGDocsHistory(tmpStr);
						
					/*** Compensation Leave ***/	
					} else if (ConstantUtils.COMPENSATION_LEAVE.equals(leaveType)) {
						if (checkCompensationBalance >= Double.parseDouble(numOfDays)) {
							timeNow = Misc.now();
							hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
							lqs.addLeaveQueue(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark,"","","");
							misc.notifySupervisor(recipientName, approvalFrom, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate);
							List<String> eAddArray = new ArrayList<String>();
							for(int i=0; i < ess.getEmailSettingList().size(); i++){
								EmailSetting esetting = ess.getEmailSettingList().get(i);
								String regionArray [] = esetting.getRegion().split(",");
								for(int z=0; z<regionArray.length; z++){
									if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
										log.debug("send email to "+esetting.getEmailAddress());
										eAddArray.add(esetting.getEmailAddress());
									}
								}
							}
							Queue queue = QueueFactory.getQueue("SendHREmailQueue");
							queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
									.param("timeNow", timeNow).param("empName", empName)
									.param("numOfDays", numOfDays).param("leaveType", leaveType)
									.param("remark", remark).param("startDate", startDate)
									.param("endDate", endDate).method(Method.POST));
							/* write to GDocs for record */
							String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
							+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
							+",LeaveType="+leaveType+",Remark="+remark;
//							misc.storeInGDocsHistory(tmpStr);
						} else {
							log.error(""+properties.getProperty("invalid.insufficient.leave"));
							errorMap.put("invalid.insufficient.leave", properties.getProperty("invalid.insufficient.leave"));
//							misc.insufficientBalMail(timeNow, eAdd, numOfDays, leaveType, remark, startDate, endDate);
						}
						
					/*** Compassionate Leave ***/
					} else if (ConstantUtils.COMPANSSIONATE_LEAVE.equals(leaveType)) {
						
						if(compassionateLeave + Double.parseDouble(numOfDays) > Integer.parseInt(leaveEntitle.getAddCompassionateLeave())){
							log.error(""+properties.getProperty("compassionate.leave.maximum.day"));
							errorMap.put("compassionate.leave.maximum.day", properties.getProperty("compassionate.leave.maximum.day"));
							req.setAttribute("errorMap", errorMap);
						}
						else{
							timeNow = Misc.now();
							hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
							lqs.addLeaveQueue(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark,"","","");
							misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate);
							List<String> eAddArray = new ArrayList<String>();
							for(int i=0; i < ess.getEmailSettingList().size(); i++){
								EmailSetting esetting = ess.getEmailSettingList().get(i);
								String regionArray [] = esetting.getRegion().split(",");
								for(int z=0; z<regionArray.length; z++){
									if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
										log.debug("send email to "+esetting.getEmailAddress());
										eAddArray.add(esetting.getEmailAddress());
									}
								}
							}
							Queue queue = QueueFactory.getQueue("SendHREmailQueue");
							queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
									.param("timeNow", timeNow).param("empName", empName)
									.param("numOfDays", numOfDays).param("leaveType", leaveType)
									.param("remark", remark).param("startDate", startDate)
									.param("endDate", endDate).method(Method.POST));
							/* write to GDocs for record */
							String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
							+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
							+",LeaveType="+leaveType+",Remark="+remark;
//							misc.storeInGDocsHistory(tmpStr);
						}
						
						
					/*** Birthday Leave ***/
					} else if (ConstantUtils.BIRTHDAY_LEAVE.equals(leaveType)) {
						double num = Double.parseDouble(numOfDays);
						if (num > Integer.parseInt(leaveEntitle.getAddBirthdayLeave())) {
							log.error(""+properties.getProperty("invalid.birthday.leave"));
							errorMap.put("invalid.birthday.leave", properties.getProperty("invalid.birthday.leave"));
							req.setAttribute("errorMap", errorMap);
						} else if (num == Integer.parseInt(leaveEntitle.getAddBirthdayLeave())) {
							if (checkBirthdayBalance == 0) {
								Vector availableDates = new Vector();
								DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
								DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
								Calendar time = new GregorianCalendar();
								Date tmpDate = new Date();
								Date date1 = new Date();
								boolean isAvailable = false;
								String strDate = "";
								String tmp = birthDate.substring(0, birthDate.lastIndexOf("-")+1);
								Calendar cal = Calendar.getInstance();
								birthDate = tmp + cal.get(Calendar.YEAR);
								date1 = (Date)format.parse(birthDate);
								time.setTime(date1);
								if (time.get(Calendar.DAY_OF_WEEK)==7) {	/* Saturday */
									tmpDate.setTime(date1.getTime() + 2 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								} else if (time.get(Calendar.DAY_OF_WEEK)==6) {		/* Friday */
									availableDates.add(birthDate);
									tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() + 3 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								} else if (time.get(Calendar.DAY_OF_WEEK)==5) {		/* Thursday */
									availableDates.add(birthDate);
									tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								} else if (time.get(Calendar.DAY_OF_WEEK)==4) {		/* Wednesday */
									availableDates.add(birthDate);
									tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								} else if (time.get(Calendar.DAY_OF_WEEK)==3) {		/* Tuesday */
									availableDates.add(birthDate);
									tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								} else if (time.get(Calendar.DAY_OF_WEEK)==2) {		/* Monday */
									availableDates.add(birthDate);
									tmpDate.setTime(date1.getTime() - 3 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								} else if (time.get(Calendar.DAY_OF_WEEK)==1) {		/* Sunday */
									tmpDate.setTime(date1.getTime() - 2 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
									tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
									strDate = formatter.format(tmpDate);
									availableDates.add(strDate);
								}
								
								Calendar y = Calendar.getInstance();
								try {
									y.setTime(standardDF.parse(startDate));
									
								} catch (ParseException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}
								// add future year, if apply for future year birthday, only allow future 1 year
								String nextYear = String.valueOf(y.get(Calendar.YEAR) + 1);
								String thisYear =  String.valueOf(y.get(Calendar.YEAR));
								String nextYearStartDate = "";
								StringBuilder b = new StringBuilder(startDate);
								b.replace(startDate.lastIndexOf(thisYear),startDate.lastIndexOf(thisYear)+ 4, nextYear );
								nextYearStartDate = b.toString();
								
								for (int i=0; i<availableDates.size(); i++) {
									if (startDate.equalsIgnoreCase(availableDates.elementAt(i).toString()) 
											|| nextYearStartDate.equals(availableDates.elementAt(i).toString())) {
										isAvailable = true;
									}
								}
								if (isAvailable == false) {
									
									log.error(""+properties.getProperty("invalid.birthday.leave"));
									errorMap.put("invalid.birthday.leave", properties.getProperty("invalid.birthday.leave"));
									
								} else if (isAvailable == true) {
									timeNow = Misc.now();
									hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
									lqs.addLeaveQueue(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark,"","","");
									misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate);
									List<String> eAddArray = new ArrayList<String>();
									for(int i=0; i < ess.getEmailSettingList().size(); i++){
										EmailSetting esetting = ess.getEmailSettingList().get(i);
										String regionArray [] = esetting.getRegion().split(",");
										for(int z=0; z<regionArray.length; z++){
											if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
												log.debug("send email to "+esetting.getEmailAddress());
												eAddArray.add(esetting.getEmailAddress());
											}
										}
									}
									Queue queue = QueueFactory.getQueue("SendHREmailQueue");
									queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
											.param("timeNow", timeNow).param("empName", empName)
											.param("numOfDays", numOfDays).param("leaveType", leaveType)
											.param("remark", remark).param("startDate", startDate)
											.param("endDate", endDate).method(Method.POST));
									/* write to GDocs for record */
									String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
									+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
									+",LeaveType="+leaveType+",Remark="+remark;
//									misc.storeInGDocsHistory(tmpStr);
								}
								
							} else
							if (checkBirthdayBalance > 0) {
								log.error(""+properties.getProperty("invalid.birthday.leave"));
								errorMap.put("invalid.birthday.leave", properties.getProperty("invalid.birthday.leave"));
								
							}
						}
						
					/*** Maternity Leave ***/
					} else if (ConstantUtils.MATERNITY_LEAVE.equals(leaveType)) {
						
						if(ConstantUtils.MALAYSIA.equals(employee.getRegion()) 
								&& maternityLeave + Double.parseDouble(numOfDays) > Integer.parseInt(leaveEntitle.getAddMaternityLeave()) ){
							log.error(""+properties.getProperty("maternity.leave.maximum.day"));
							errorMap.put("maternity.leave.maximum.day", properties.getProperty("maternity.leave.maximum.day"));
							req.setAttribute("errorMap", errorMap);
						}
						else{
							timeNow = Misc.now();
							hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
							lqs.addLeaveQueue(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark,"","","");
							misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate);
							List<String> eAddArray = new ArrayList<String>();
							for(int i=0; i < ess.getEmailSettingList().size(); i++){
								EmailSetting esetting = ess.getEmailSettingList().get(i);
								String regionArray [] = esetting.getRegion().split(",");
								for(int z=0; z<regionArray.length; z++){
									if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
										log.debug("send email to "+esetting.getEmailAddress());
										eAddArray.add(esetting.getEmailAddress());
									}
								}
							}
							Queue queue = QueueFactory.getQueue("SendHREmailQueue");
							queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
									.param("timeNow", timeNow).param("empName", empName)
									.param("numOfDays", numOfDays).param("leaveType", leaveType)
									.param("remark", remark).param("startDate", startDate)
									.param("endDate", endDate).method(Method.POST));
							/* write to GDocs for record */
							String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
							+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
							+",LeaveType="+leaveType+",Remark="+remark;
//							misc.storeInGDocsHistory(tmpStr);
						}
						
						
					/*** Wedding Leave ***/
					} else if (ConstantUtils.WEDDING_LEAVE.equals(leaveType)) {
						if(weddingLeave + Double.parseDouble(numOfDays) > Integer.parseInt(leaveEntitle.getAddWeddingLeave())
								&& !ConstantUtils.CHINA.equals(employee.getRegion()) ){
							log.error(""+properties.getProperty("wedding.leave.maximum.day"));
							errorMap.put("wedding.leave.maximum.day", properties.getProperty("wedding.leave.maximum.day"));
							req.setAttribute("errorMap", errorMap);
						}
						else{
							timeNow = Misc.now();
							hs.addToHistory(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region,"","",eAdd);
							lqs.addLeaveQueue(timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark,"","","");
							misc.notifySupervisor(recipientName,approvalFrom, timeNow, empName, numOfDays, leaveType, remark, startDate, endDate);
							List<String> eAddArray = new ArrayList<String>();
							for(int i=0; i < ess.getEmailSettingList().size(); i++){
								EmailSetting esetting = ess.getEmailSettingList().get(i);
								String regionArray [] = esetting.getRegion().split(",");
								for(int z=0; z<regionArray.length; z++){
									if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
										log.debug("send email to "+esetting.getEmailAddress());
										eAddArray.add(esetting.getEmailAddress());
									}
								}
							}
							Queue queue = QueueFactory.getQueue("SendHREmailQueue");
							queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
									.param("timeNow", timeNow).param("empName", empName)
									.param("numOfDays", numOfDays).param("leaveType", leaveType)
									.param("remark", remark).param("startDate", startDate)
									.param("endDate", endDate).method(Method.POST));
							/* write to GDocs for record */
							String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
							+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
							+",LeaveType="+leaveType+",Remark="+remark;
//							misc.storeInGDocsHistory(tmpStr);
						}
						
					}
				}

				if(!errorMap.isEmpty() && errorMap != null){
					req.setAttribute("errorMap", errorMap);
					getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
					return;
				}
				else{
					
					log.debug(""+properties.getProperty("submitted.leave.request"));
					req.setAttribute("message", properties.getProperty("submitted.leave.request"));
					req.setAttribute("feedback", ConstantUtils.OK);
					getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
					return;
				}
				
			} else {
				if(StringUtils.isBlank(leaveType)){
					log.error(""+properties.getProperty("mandotory.leavetype"));
					errorMap.put("mandotory.leavetype", properties.getProperty("mandotory.leavetype"));
				}
				if(StringUtils.isBlank(approvalFrom)){
					log.error(""+properties.getProperty("mandotory.supervisor"));
					errorMap.put("mandotory.supervisor", properties.getProperty("mandotory.supervisor"));
				}
				if(StringUtils.isBlank(remark)){
					log.error(""+properties.getProperty("mandotory.remark"));
					errorMap.put("mandotory.remark", properties.getProperty("mandotory.remark"));
				}
				
				req.setAttribute("errorMap", errorMap);
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
			
				return;
			}
		} catch (Exception e) {
			
			String message = "ApplyLeave exception error: " + e.getMessage();
			try {
				misc.postMailToSysAdmin(message);
			} catch (MessagingException e1) {
				log.error("ApplyLeave MessagingException error: " + e1.getMessage());
			}
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ApplyLeave.class);
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		if(StringUtils.isNotBlank(emailAddress)){
			AdministratorService admin = new AdministratorService();
			Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
			EmployeeService  ems = new EmployeeService();
			MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress", emailAddress);
			String region = "";
			if(!ConstantUtils.MALAYSIA.equals(employee.getRegion()) && !ConstantUtils.SINGAPORE.equals(employee.getRegion())){
				region = "true";
			}
			request.setAttribute("approvalFrom", employee.getSupervisor());
			request.setAttribute("region",region);
			if(StringUtils.isBlank(ad.getEmailAddress())){
				
				try {
					getServletConfig().getServletContext().getRequestDispatcher("/mct-emp-leave-form.jsp").forward(request, response);
					return;
				} catch (ServletException e) {
					log.error("ApplyLeave - doPost error: " + e.getMessage());
					e.printStackTrace();
				}
				
			}
		}
		else{
			response.sendRedirect("/service-error.jsp");
		}
		
		response.sendRedirect("/admin-emp-leave-form.jsp");
	}


	public String errorMsg() {
		return errorMessage;
	}
	
	public static Calendar getDatePart(Date date){
	    Calendar cal = Calendar.getInstance();       // get calendar instance
	    cal.setTime(date);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
	    cal.set(Calendar.MINUTE, 0);                 // set minute in hour
	    cal.set(Calendar.SECOND, 0);                 // set second in minute
	    cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second
	    return cal;
	    
	}
	
	public static long daysBetween(Date startDate, Date endDate) {
		  Calendar sDate = getDatePart(startDate);
		  Calendar eDate = getDatePart(endDate);

		  long daysBetween = 1;
		 
			  while (sDate.before(eDate)) {
			      sDate.add(Calendar.DAY_OF_MONTH, 1);
			      daysBetween++;
			  }
			  
			  return daysBetween;
		  
	}
}
