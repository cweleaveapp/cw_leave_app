package com.google.appengine.mct;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

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
import java.util.Locale;
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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.util.ConstantUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

@SuppressWarnings("serial")
public class AmendLeaveRequestAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AmendLeaveRequestAction.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AmendLeaveRequestAction.class);
		String id = req.getParameter("id");
		String emailAddress = req.getParameter("emailAddress");
		String numOfDays = req.getParameter("numOfDays");
		String remark = req.getParameter("remark");
		String leaveType = req.getParameter("leaveType");
		String changeType = req.getParameter("changeType");
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		String newStartDate = req.getParameter("newStartDate");
		String newEndDate = req.getParameter("newEndDate");
		String approvalFrom = req.getParameter("approvalFrom");
		String oldLeaveType = req.getParameter("oldLeaveType");
		String projectName = req.getParameter("projectName");
		String attachmentUrl = req.getParameter("attachmentUrl");
		
		req.setAttribute("id", id);
		req.setAttribute("emailAddress", emailAddress);
		req.setAttribute("numOfDays", numOfDays);
		req.setAttribute("leaveType", leaveType);
		req.setAttribute("startDate", startDate);
		req.setAttribute("endDate", endDate);
		req.setAttribute("newStartDate", newStartDate);
		req.setAttribute("newEndDate", newEndDate);
		req.setAttribute("approvalFrom", approvalFrom);
		req.setAttribute("oldLeaveType", oldLeaveType);
		req.setAttribute("changeType", changeType);
		req.setAttribute("remark", remark);
		req.setAttribute("attachmentUrl", attachmentUrl);
		
		Map<String, String> errorMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		
		log.debug("AmendLeaveRequestAction - leaveType = " + leaveType);
		log.debug("AmendLeaveRequestAction - oldLeaveType = " + oldLeaveType);
		log.debug("AmendLeaveRequestAction - changeType = " + changeType);
		log.debug("AmendLeaveRequestAction - newStartDate = " + newStartDate);
		log.debug("AmendLeaveRequestAction - newEndDate = " + newEndDate);
		
		String region = "";
		String empName = "";
		String adminEmail = (String)req.getSession().getAttribute("emailAdd");
		AdministratorService addService = new AdministratorService();
		Administrator admin = addService.findAdministratorByEmailAddress(adminEmail);
		String isAdmin = ConstantUtils.FALSE;
		EmployeeService  ems = new EmployeeService();
		MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress", emailAddress);
		MCEmployee supervisor = ems.findMCEmployeeByColumnName("emailAddress", approvalFrom);
		region = employee.getRegion();
		empName = employee.getFullName();
		if(StringUtils.isNotBlank(admin.getEmailAddress())){
			 isAdmin = ConstantUtils.TRUE;
			
		}
		
		Misc misc = new Misc();
		String timeNow = "";
		String eAdd = emailAddress;

		timeNow = Misc.now();
		LeaveEntitleService les = new LeaveEntitleService();
		HistoryService hs = new HistoryService();
		LeaveQueueService lqs = new LeaveQueueService();
		//EmployeeLeaveDetailsService mployeeLeaveDetailsService.getInstance() = new EmployeeLeaveDetailsService();
		ApprovedLeaveService appLeaveService = new ApprovedLeaveService();
		// get actual number of day for no pay leave
		String noPayLeaveDay = numOfDays;
		String othersDay = numOfDays;
		
		double checkCompensationBalance = 0;
   		double checkBalance = 0;
   		double checkBirthdayBalance = 0;
   		double checkSickLeaveBalance = 0;
   		String birthDate = employee.getBirthDate();
   		
   		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
   		
   		Calendar currentYear = Calendar.getInstance(Locale.getDefault());
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
		
   		
   		EmployeeLeaveDetails eld = EmployeeLeaveDetailsService.getInstance().findEmployeeLeaveDetails(emailAddress, year);
				
		if(ConstantUtils.AMEND_LEAVE.equals(changeType)){
				
				
//			       		 check if numOfDays day difference is correct
//			       		 check public holiday, saturday, sunday, halfday
//			       		URL metafeedUrl = new URL(ConstantUtils.SPREADSSHEETS_FEEDURL);
//			       		SpreadsheetEntry spreadSheetEntry = null;
//			       		Calendar currentYear = Calendar.getInstance();
//			       		SpreadsheetService service = new SpreadsheetService(ConstantUtils.MCKL_PUBLIC_HOLIDAYS+currentYear.get(Calendar.YEAR));
//			       		SettingService ss = new SettingService();
//			       		List<Setting> acc = ss.getSetting();
//			       		String spreadsheetAcc = "";
//			       		String spreadsheetPass = "";
//			       		for(Setting setting : acc){
//			       			if(ConstantUtils.SPREADSSHEET_SERVICE_ACCOUNT.equals(setting.getPropertyName())){
//			       				spreadsheetAcc = setting.getPropertyValue();
//			       			}else if(ConstantUtils.SPREADSSHEET_SERVICE_ACCOUNT_PASS.equals(setting.getPropertyName())){
//			       				spreadsheetPass = setting.getPropertyValue();
//			       			}
//			       		}
//			       		service.setUserCredentials(spreadsheetAcc, spreadsheetPass);
//			       		SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
//			       		String spreadsheetName = ConstantUtils.MCKL_PUBLIC_HOLIDAYS+currentYear.get(Calendar.YEAR);
//			       		
//			       		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
//			       		for (int i = 0; i < spreadsheets.size(); i++) {
//			       			SpreadsheetEntry entry = spreadsheets.get(i);
//			       			if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
//			       				spreadSheetEntry = entry;
//			       				break;
//			       			}
//			       		}
//			       		List<String> holidayList = new ArrayList<String>();
//			       		if(spreadSheetEntry != null){
//			       			List<WorksheetEntry> worksheets = spreadSheetEntry.getWorksheets();
//			       			if(worksheets.size() > 0) {
//			       				WorksheetEntry worksheet = worksheets.get(0);
//			       				URL listFeedUrl = worksheet.getListFeedUrl();
//			       				ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
//			       				
//			       				 for (ListEntry row : listFeed.getEntries()) {
//			       				      for (String tag : row.getCustomElements().getTags()) {
//			       				    	  if(tag.equals("date"))
//			       				    		  //retrieve all holiday spreadsheet from column date value
//			       				    		  holidayList.add(row.getCustomElements().getValue(tag));
//			       				      }
//			       				      
//			       				    }
//			       			}
//			       		}
			
					
       		
       		
					if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
						
						try {
					
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
			       		long diffdate = daysBetween(standardDF.parse(newStartDate),standardDF.parse(newEndDate));
			       		long dateremove = 0;
			       		
			       		Calendar cal = Calendar.getInstance(Locale.getDefault());
			       		cal.setTime(standardDF.parse(newStartDate));
			       		//check if Sunday or Saturday 
			       		for(int i=0; i<diffdate; i++){
			       			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			       				dateremove += 1;
			       			}
			       			totalDate.add(standardDF.format(cal.getTime()));
			       			cal.add(Calendar.DATE, 1);
			       		}
			       		
			       		//create another new calendar instance to avoid addition add date cause by  cal.add(Calendar.DATE, 1); above
//			       		Calendar cal2 = Calendar.getInstance();
//			       		cal2.setTime(standardDF.parse(newStartDate)); 
			       		
//			       		SimpleDateFormat calendarDF = new SimpleDateFormat("MM/dd/yyyy");
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
		        		
//		        		if (ConstantUtils.NO_PAY_LEAVE.equals(leaveType)) {
//		        			
//		        			if(originalNumOfDays != diffdate){
//		        				 log.error(""+properties.getProperty("different.number.of.days"));
//		        				 errorMap.put("different.number.of.days", properties.getProperty("different.number.of.days"));
//		        				 
//		        			}
//		        		}
		        		
			       		numOfDays = Long.toString(diffdate-dateremove);
			       		String str = String.valueOf(originalNumOfDays).substring(Math.max(String.valueOf(originalNumOfDays).length() - 2, 0));
			       		if(StringUtils.equals(".5",str)){
			    			Double num = Double.parseDouble(numOfDays) - 0.5;
			    			numOfDays = num.toString();
			    		}
			       		
			       			if(originalNumOfDays != Double.parseDouble(numOfDays)){
			       			 log.error(""+properties.getProperty("different.number.of.days"));
			       			 errorMap.put("different.number.of.days", properties.getProperty("different.number.of.days"));
			       			 
			       			}
			       		
			    		
			       		
			       		
			       		
			       		
			       		if(ConstantUtils.SICK_LEAVE.equals(leaveType)){
			       			
			       			ApprovedLeaveService als = new ApprovedLeaveService();
			       			
			       			if(ConstantUtils.AMEND_LEAVE.equals(changeType)){
			       				if(StringUtils.isBlank(attachmentUrl)){
					       			log.error(""+properties.getProperty("sick.leave.attachment"));
			        				errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
			        				req.setAttribute("errorMap", errorMap);
				        		 		try{
										getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
										return;
										
				        		 		} catch (ServletException e) {
										log.error("AmendLeaveRequestAction * invalid start day - error: " + e.getMessage());
				        		 		}
				        		 	
					       		}
				       			
				       			Calendar sickCal = Calendar.getInstance(Locale.getDefault());
				       			Calendar curr = Calendar.getInstance(Locale.getDefault());
				       			sickCal.setTime(standardDF.parse(employee.getHiredDate()));
				       			int yearCal = curr.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
								boolean allow = true;
								double sickLeave = 0;
								
								
								MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
						
									checkSickLeaveBalance = Double.parseDouble(eld.getSickLeaveFP())-Double.parseDouble(appLeave.getNumOfDays())
											+Double.parseDouble(numOfDays);
									
								sickLeave = checkSickLeaveBalance;
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
								errorMap.put("system.error.please.contact.administrator", 
										properties.getProperty("system.error.please.contact.administrator"));
								req.setAttribute("errorMap", errorMap);
							}
								
								if(StringUtils.isBlank(attachmentUrl)){
					       			log.error(""+properties.getProperty("sick.leave.attachment"));
									errorMap.put("sick.leave.attachment", properties.getProperty("sick.leave.attachment"));
								}
								
								req.setAttribute("errorMap", errorMap);
			        		 	if(!errorMap.isEmpty()){
			        		 		try{
									getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
									return;
									
			        		 		} catch (ServletException e) {
									log.error("AmendLeaveRequestAction * invalid start day - error: " + e.getMessage());
			        		 		}
			        		 	}
			        		 	else{
			        		 		
									als.deleteApprovedLeave(id);
									
									als.addApprovedLeave(timeNow, eAdd, numOfDays, newStartDate, 
											newEndDate, leaveType, approvalFrom, 
											remark, region,changeType, attachmentUrl, projectName);
									
									EmployeeLeaveDetailsService.getInstance().updateEmployeeLeaveDetails(eld.getName(), 
											eld.getEmailAddress(), year, eld.getLastYearBalance(), 
											eld.getEntitledAnnual(), eld.getEntitledAnnual(), 
											eld.getNoPayLeave(), String.valueOf(checkSickLeaveBalance), 
											eld.getAnnualLeave(), eld.getEntitledAnnual(), 
											eld.getCompassionateLeave(), eld.getBirthdayLeave(), 
											eld.getMaternityLeave(), eld.getMarriageLeave(), 
											eld.getOthers(), eld.getBalance(),
											region);
									
									
			        		 	}
			       			}
			       			
						}
			       		else
			       	 if (ConstantUtils.COMPENSATION_LEAVE.equals(leaveType)) {
			       		MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
			       		if(StringUtils.isNotBlank(appLeave.getEmailAdd())){
			       			// approve leave number of day apply plus total apply compensation leave
			       			checkCompensationBalance = Double.parseDouble(appLeave.getNumOfDays())-
			       					Double.parseDouble(eld.getEntitledAnnual())
			       					+Double.parseDouble(numOfDays);
			       		}
			       		else{
			       		// still under queue balance remain previous
			       			checkCompensationBalance = Double.parseDouble(eld.getEntitledAnnual())+Double.parseDouble(numOfDays);
				       		
			       		}
			       		if (checkCompensationBalance > Double.parseDouble(eld.getEntitledAnnual())) {
								log.error(""+properties.getProperty("invalid.insufficient.leave"));
								errorMap.put("invalid.insufficient.leave", properties.getProperty("invalid.insufficient.leave"));
//								misc.insufficientBalMail(timeNow, eAdd, numOfDays, leaveType, remark, startDate, endDate);
							}
							
			       	 }
			       	 else if (ConstantUtils.ANNUAL_LEAVE.equals(leaveType)) {
			       		MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
			       		if(StringUtils.isNotBlank(appLeave.getEmailAdd())){
			       			checkBalance = Double.parseDouble(appLeave.getNumOfDays())
			       					-Double.parseDouble(eld.getAnnualLeave())
			       					+Double.parseDouble(numOfDays);	
				       		
			       		}
			       		else{
			       			// still under queue balance remain previous
			       			checkBalance = Double.parseDouble(eld.getAnnualLeave())+Double.parseDouble(numOfDays);
				       		
			       		}
			       		if (checkBalance > Double.parseDouble(eld.getEntitledAnnual())) {
								log.error(""+properties.getProperty("invalid.insufficient.leave"));
								errorMap.put("invalid.insufficient.leave", properties.getProperty("invalid.insufficient.leave"));
//								misc.insufficientBalMail(timeNow, eAdd, numOfDays, leaveType, remark, startDate, endDate);
							}
			       	} else if (ConstantUtils.BIRTHDAY_LEAVE.equals(leaveType)) {
			       		checkBirthdayBalance = Double.parseDouble(eld.getBirthdayLeave());
			       		double num = Double.parseDouble(numOfDays);
			       		
						if (num > 1) {
							log.error(""+properties.getProperty("invalid.birthday.leave"));
							errorMap.put("invalid.birthday.leave", properties.getProperty("invalid.birthday.leave"));
							req.setAttribute("errorMap", errorMap);
						} else if (num == 1) {
								Vector availableDates = new Vector();
								DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
								DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
								Calendar time = new GregorianCalendar(Locale.getDefault());
								Date tmpDate = new Date();
								Date date1 = new Date();
								boolean isAvailable = false;
								String strDate = "";
								String tmp = birthDate.substring(0, birthDate.lastIndexOf("-")+1);
								birthDate = tmp + year;
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
								
								for (int i=0; i<availableDates.size(); i++) {
									if (startDate.equalsIgnoreCase(availableDates.elementAt(i).toString())) {
										isAvailable = true;
									}
								}
								if (isAvailable == false) {
									
									log.error(""+properties.getProperty("invalid.birthday.leave"));
									errorMap.put("invalid.birthday.leave", properties.getProperty("invalid.birthday.leave"));
									
								} 
								
						}
			       	}
			       	else if(ConstantUtils.COMPANSSIONATE_LEAVE.equals(leaveType)) {
			       		MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
			       		if(StringUtils.isNotBlank(appLeave.getEmailAdd())){
			       			checkBalance = Double.parseDouble(appLeave.getNumOfDays())
			       					-Double.parseDouble(eld.getCompassionateLeave())
			       					+Double.parseDouble(numOfDays);	
				       		
			       		}
			       		else{
			       			// still under queue balance remain previous
			       			checkBalance = Double.parseDouble(eld.getCompassionateLeave())+Double.parseDouble(numOfDays);
				       		
			       		}
			       		if(checkBalance > 3){
							log.error(""+properties.getProperty("compassionate.leave.maximum.day"));
							errorMap.put("compassionate.leave.maximum.day", properties.getProperty("compassionate.leave.maximum.day"));
							req.setAttribute("errorMap", errorMap);
						}
			       	}
			       	else if (ConstantUtils.MATERNITY_LEAVE.equals(leaveType)) {
			       		MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
			       		if(StringUtils.isNotBlank(appLeave.getEmailAdd())){
			       			checkBalance = Double.parseDouble(appLeave.getNumOfDays())
			       					-Double.parseDouble(eld.getMaternityLeave())
			       					+Double.parseDouble(numOfDays);	
				       		
			       		}
			       		else{
			       			// still under queue balance remain previous
			       			checkBalance = Double.parseDouble(eld.getMaternityLeave())+Double.parseDouble(numOfDays);
				       		
			       		}
			       		if(checkBalance > 60){
							log.error(""+properties.getProperty("maternity.leave.maximum.day"));
							errorMap.put("maternity.leave.maximum.day", properties.getProperty("maternity.leave.maximum.day"));
							req.setAttribute("errorMap", errorMap);
						}
			       	}
			       	else if (ConstantUtils.WEDDING_LEAVE.equals(leaveType)) {
			       		MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
			       		if(StringUtils.isNotBlank(appLeave.getEmailAdd())){
			       			checkBalance = Double.parseDouble(appLeave.getNumOfDays())
			       					-Double.parseDouble(eld.getMarriageLeave())
			       					+Double.parseDouble(numOfDays);	
				       		
			       		}
			       		else{
			       			// still under queue balance remain previous
			       			checkBalance = Double.parseDouble(eld.getMarriageLeave())+Double.parseDouble(numOfDays);
				       		
			       		}
			       		if(checkBalance > 3
								&& !ConstantUtils.CHINA.equals(employee.getRegion()) ){
							log.error(""+properties.getProperty("wedding.leave.maximum.day"));
							errorMap.put("wedding.leave.maximum.day", properties.getProperty("wedding.leave.maximum.day"));
							req.setAttribute("errorMap", errorMap);
						}
			       	}
		        		 req.setAttribute("errorMap", errorMap);
		        		 	if(!errorMap.isEmpty()){
		        		 		try{
								getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
								return;
								
		        		 		} catch (ServletException e) {
								log.error("AmendLeaveRequestAction * invalid start day - error: " + e.getMessage());
		        		 		}
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
				}
					else if (ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
						MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
						if(StringUtils.isNotBlank(appLeave.getEmailAdd())){
			       			checkBalance = Double.parseDouble(appLeave.getNumOfDays())
			       					-Double.parseDouble(eld.getEntitledAnnual())
			       					+Double.parseDouble(numOfDays);	
				       		
			       		}
			       		else{
			       			// still under queue balance remain previous
			       			checkBalance = Double.parseDouble(eld.getEntitledAnnual())+Double.parseDouble(numOfDays);
				       		
			       		}
						
						// if compensation leave have been use, and use more than amend number of day
						if(checkBalance < Double.parseDouble(eld.getEntitledAnnual())){
							log.error(""+properties.getProperty("invalid.amend.entitle.compensation"));
							errorMap.put("invalid.amend.entitle.compensation", properties.getProperty("invalid.amend.entitle.compensation"));
							req.setAttribute("errorMap", errorMap);
							try{
								getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
								return;
								
		        		 		} catch (ServletException e) {
								log.error("AmendLeaveRequestAction * invalid entitle compensation - error: " + e.getMessage());
		        		 		}
						}
						
					}
					
				
				// if apply no pay leave get actual no pay leave day
				if(ConstantUtils.NO_PAY_LEAVE.equals(leaveType)){
					numOfDays = noPayLeaveDay;
				}
				
				// if apply no pay leave get actual no pay leave day
				if(ConstantUtils.OTHERS.equals(leaveType)){
					numOfDays = othersDay;
				}
				
				if(!ConstantUtils.SICK_LEAVE.equals(leaveType)){
						lqs.updateLeaveQueue(id, timeNow, eAdd, numOfDays, newStartDate, newEndDate, leaveType, approvalFrom, remark, projectName, ConstantUtils.AMEND_LEAVE, id, attachmentUrl);						
				}
				
				hs.updateToHistory(id, timeNow, eAdd, numOfDays, newStartDate, newEndDate, leaveType, approvalFrom, remark, region, projectName, ConstantUtils.AMEND_LEAVE, adminEmail);
				
				/* write to GDocs for record */
				String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
				+",StartDate="+newStartDate+",EndDate="+newEndDate+",Supervisor="+approvalFrom
				+",LeaveType="+leaveType+",Remark="+remark;
//				misc.storeInGDocsHistory(tmpStr);
				
				
			// cancel leave
		} else {
			hs.updateToHistory(id, timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, region, projectName, ConstantUtils.CANCEL_LEAVE, adminEmail);
			List<LeaveQueue> lqList = lqs.getLeaveQueue();
			if(lqList != null && !lqList.isEmpty()){
				for(LeaveQueue lq : lqList){
					if(lq.getId().equals(id)){
						lqs.deleteLeaveQueue(id);
						
					}
				}
			}
			else{
				
			if(!ConstantUtils.SICK_LEAVE.equals(leaveType)){
				lqs.updateLeaveQueue(id, timeNow, eAdd, numOfDays, startDate, endDate, leaveType, approvalFrom, remark, projectName, ConstantUtils.CANCEL_LEAVE, id, attachmentUrl);	
			}
			else if(ConstantUtils.SICK_LEAVE.equals(leaveType)){
				
				try{
					
					ApprovedLeaveService als = new ApprovedLeaveService();
					MCApprovedLeave appLeave = appLeaveService.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,id,ConstantUtils.EQUAL);
					
					if(Double.parseDouble(eld.getSickLeaveFP()) >  Double.parseDouble(appLeave.getNumOfDays())){
						checkSickLeaveBalance =
								Double.parseDouble(eld.getSickLeaveFP())-Double.parseDouble(appLeave.getNumOfDays());
					}
					else{
						checkSickLeaveBalance = Double.parseDouble(appLeave.getNumOfDays())-
								Double.parseDouble(eld.getSickLeaveFP());
					}
					
					als.deleteApprovedLeave(id);
					
					EmployeeLeaveDetailsService.getInstance().updateEmployeeLeaveDetails(eld.getName(), 
							eld.getEmailAddress(), year, eld.getLastYearBalance(), 
							eld.getEntitledAnnual(), eld.getEntitledAnnual(), 
							eld.getNoPayLeave(), String.valueOf(checkSickLeaveBalance), 
							eld.getAnnualLeave(), eld.getEntitledAnnual(), 
							eld.getCompassionateLeave(), eld.getBirthdayLeave(), 
							eld.getMaternityLeave(), eld.getMarriageLeave(), 
							eld.getOthers(), eld.getBalance(),
							region);
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
				
				
			}
		}
			/* write to GDocs for record */
			String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
			+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
			+",LeaveType="+leaveType+",Remark="+remark;
//			misc.storeInGDocsHistory(tmpStr);
		}
		
		try {
			
			misc.notifySupervisorAmend(approvalFrom, timeNow, empName, numOfDays, leaveType, remark, 
					startDate, endDate, changeType, newStartDate, newEndDate, oldLeaveType);
			EmailSettingService ess = new EmailSettingService();
			List<String> eAddArray = new ArrayList<String>();
			for(int i=0; i < ess.getEmailSettingList().size(); i++){
				EmailSetting esetting = ess.getEmailSettingList().get(i);
				String regionArray [] = esetting.getRegion().split(",");
				for(int z=0; z<regionArray.length; z++){
					if(region.equalsIgnoreCase(regionArray[z])){
						log.debug("send email to "+esetting.getEmailAddress());
						eAddArray.add(esetting.getEmailAddress());
						
					}
				}
			}
			Queue queue = QueueFactory.getQueue("SendHRAmendEmailQueue");
			queue.add(withUrl("/SendHRAmendEmailQueue").param("emailAddress", eAddArray.toString())
					.param("timeNow", timeNow).param("empName", empName)
					.param("numOfDays", numOfDays).param("leaveType", leaveType)
					.param("remark", remark).param("startDate", startDate)
					.param("endDate", endDate).param("changeType", changeType)
					.param("newStartDate", newStartDate).param("newEndDate", newEndDate)
					.param("oldLeaveType", oldLeaveType).method(Method.POST));
			if(ConstantUtils.SICK_LEAVE.equals(leaveType)){
				log.debug(""+properties.getProperty("amend.cancel.sick.leave.request"));
				errorMap.put("amend.cancel.sick.leave.request", properties.getProperty("amend.cancel.sick.leave.request"));
				
			}
			
			if(errorMap != null && !errorMap.isEmpty()){
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
			
			
		} catch (Exception e) {
			String message = "AmendLeaveRequestAction error: " + e.getMessage();
			log.error("AmendLeaveRequestAction exception error: " + e.getMessage());
			try {
				misc.postMailToSysAdmin(message);
			} catch (MessagingException e1) {
				log.error("AmendLeaveRequestAction messaging error: " + e1.getMessage());
			}
			
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
	
	public static Calendar getDatePart(Date date){
	    Calendar cal = Calendar.getInstance(Locale.getDefault());       // get calendar instance
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
