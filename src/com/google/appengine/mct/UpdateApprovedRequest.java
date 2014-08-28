package com.google.appengine.mct;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class UpdateApprovedRequest extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateApprovedRequest.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateApprovedRequest.class);
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		String eAdd = (String)req.getSession().getAttribute("emailAdd");
		String appId = req.getParameter("appId");
		String emailAddress = req.getParameter("emailAddress");
		String numOfDays = req.getParameter("numOfDays");
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		String leaveType = req.getParameter("leaveType");
		String remark = req.getParameter("remark");
		String region = req.getParameter("region");
		String supervisor = req.getParameter("supervisor");
		String attachmentUrl = req.getParameter("attachmentUrl");
		String projectName = req.getParameter("projectName");
		String changeType = req.getParameter("changeType");
		req.setAttribute("emailAddress", emailAddress);
		req.setAttribute("numOfDays", numOfDays);
		req.setAttribute("startDate", startDate);
		req.setAttribute("endDate", endDate);
		req.setAttribute("leaveType", leaveType);
		req.setAttribute("remark", remark);
		req.setAttribute("region", region);
		req.setAttribute("supervisor", supervisor);
		req.setAttribute("attachmentUrl", attachmentUrl);
		req.setAttribute("projectName", projectName);
		boolean existDirectory = true;
		String domain = "";
		String appAdminAccount = "";
		String appAdminPassword = "";
		
		EmployeeService  ems = new EmployeeService();
		MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress", emailAddress);
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				domain = set.getAppDomain();
				appAdminAccount = set.getAppAdminAcc();
				appAdminPassword = set.getAppAdminAccPass();
			
		}
		boolean hasDomain = false;
		for(String d : domain.split(",")){
			if(d.equals(emailAddress.split("@")[1])){
				hasDomain = true;
			}
		}
		if (hasDomain == false) {
			try {
				log.error(""+properties.getProperty("invalid.domain.name"));
				errorMap.put("invalid.domain.name", properties.getProperty("invalid.domain.name"));
				req.setAttribute("errorMap", errorMap);
				
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateApprovedRequest * doPost - error 1: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
			//check email pattern
	        if (!pattern.matcher(emailAddress).matches()) {
	        	
	        	try {
	        		log.error(""+properties.getProperty("invalid.email.format"));
					errorMap.put("invalid.email.format", properties.getProperty("invalid.email.format"));
					req.setAttribute("errorMap", errorMap);
					getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
					return;
	        	} catch (Exception e1) {
	    			log.error("UpdateApprovedRequest validate email error: " + e1.getMessage());
	    			e1.printStackTrace();
	    		}
	        }
			try {
//				String appDomain = domain;
//				appDomain = appDomain.replace("@", "");
//				AppsForYourDomainClient client = new AppsForYourDomainClient(appAdminAccount, appAdminPassword, appDomain);
//				UserFeed uf = client.retrieveAllUsers();
//				String tmpAdd = emailAddress;
//				int ind = tmpAdd.indexOf("@");
//				tmpAdd = tmpAdd.substring(0, ind);
//				/* check if exist in domain directory listing */
//				for (int j=0; j<uf.getEntries().size(); j++) {
//					UserEntry entry = uf.getEntries().get(j);
//					if (entry.getTitle().getPlainText().contains(tmpAdd)){
//						existDirectory = true;
//						break;
//					}
//				}

				if (existDirectory == true) {
					try{
						
						if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
					
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
						
						Calendar cal = Calendar.getInstance(Locale.getDefault());
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
						Calendar cal2 = Calendar.getInstance(Locale.getDefault());
						cal2.setTime(standardDF.parse(startDate)); 
						
//						SimpleDateFormat calendarDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
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
						
//						if (ConstantUtils.NO_PAY_LEAVE.equals(leaveType)) {
//							
//							if(originalNumOfDays != diffdate){
//								 log.error(""+properties.getProperty("different.number.of.days"));
//								 errorMap.put("different.number.of.days", properties.getProperty("different.number.of.days"));
//								 
//							}
//						}
						
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
							
						}
						else if (ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
							
							/* check if numOfDays contains other characters that are not digits */
							if(!NumberUtils.isNumber(numOfDays)) {
									log.error(""+properties.getProperty("wrong.number.of.days"));
									errorMap.put("wrong.number.of.days", properties.getProperty("wrong.number.of.days"));
									req.setAttribute("errorMap", errorMap);
									try{
									getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
									return;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
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
						
					String time = Misc.now();
					ApprovedLeaveService als = new ApprovedLeaveService();
					MCApprovedLeave approveLeave = als.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY, appId, ConstantUtils.EQUAL);
					
					als.updateApprovedLeave(appId, approveLeave.getTime(), approveLeave.getEmailAdd(),
							approveLeave.getNumOfDays(), approveLeave.getStartDate(),
							approveLeave.getEndDate(), approveLeave.getLeaveType(),
							approveLeave.getSupervisor(), approveLeave.getRemark(),
							approveLeave.getRegion(),approveLeave.getChangeType(), 
							approveLeave.getAttachmentUrl(), projectName);
					
//					als.updateApprovedLeave(appId, time, emailAddress, numOfDays, startDate,
//							endDate, leaveType, supervisor, remark, region,changeType, 
//							attachmentUrl, projectName);
					
//					EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
//					
//					Calendar calYear = Calendar.getInstance();
//					
//					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
//						 calYear = getDatePart(new Date());
//					}
//					else{
//						 calYear = getDatePart(standardDF.parse(startDate));
//					}
//					
//					// update current year after current year Apr
//					if(calYear.get(Calendar.MONTH) > 2){
//						EmployeeLeaveDetails employeeD = elds.findEmployeeLeaveDetails(emailAddress,
//								String.valueOf(calYear.get(Calendar.YEAR)));
//						
//						Double noPayLeave = Double.parseDouble(employeeD.getNoPayLeave());
//						Double sickLeave = Double.parseDouble(employeeD.getSickLeaveFP());
//						Double annualLeave = Double.parseDouble(employeeD.getAnnualLeave());
//						Double compensationLeave = Double.parseDouble(employeeD.getEntitledAnnual());
//						Double compassionateLeave = Double.parseDouble(employeeD.getCompassionateLeave());
//						Double birthdayLeave = Double.parseDouble(employeeD.getBirthdayLeave());
//						Double maternityLeave = Double.parseDouble(employeeD.getMaternityLeave());
//						Double weddingLeave = Double.parseDouble(employeeD.getWeddingLeave());
//						Double entitledCompensation = Double.parseDouble(employeeD.getEntitledAnnual());
//						Double balance = Double.parseDouble(employeeD.getBalance());
//						
//						
//						
//						elds.updateEmployeeLeaveDetails(employee.getFullName(), 
//								emailAddress, employeeD.getYear(), 
//								employeeD.getLastYearBalance(), employeeD.getEntitledAnnual(),
//								entitledCompensation.toString(), noPayLeave.toString(), 
//								sickLeave.toString(), annualLeave.toString(), 
//								compensationLeave.toString(), compassionateLeave.toString(), 
//								birthdayLeave.toString(),
//								maternityLeave.toString(), weddingLeave.toString(),
//								balance.toString(), employeeD.getRegion());
//						
//					}
//					// update last year before current year Apr
//					else{
//						EmployeeLeaveDetails employeeD = elds.findEmployeeLeaveDetails(emailAddress,
//								String.valueOf(calYear.get(Calendar.YEAR)-1));
//						
//						Double noPayLeave = Double.parseDouble(employeeD.getNoPayLeave());
//						Double sickLeave = Double.parseDouble(employeeD.getSickLeaveFP());
//						Double annualLeave = Double.parseDouble(employeeD.getAnnualLeave());
//						Double compensationLeave = Double.parseDouble(employeeD.getEntitledAnnual());
//						Double compassionateLeave = Double.parseDouble(employeeD.getCompassionateLeave());
//						Double birthdayLeave = Double.parseDouble(employeeD.getBirthdayLeave());
//						Double maternityLeave = Double.parseDouble(employeeD.getMaternityLeave());
//						Double weddingLeave = Double.parseDouble(employeeD.getWeddingLeave());
//						Double entitledCompensation = Double.parseDouble(employeeD.getEntitledAnnual());
//						Double balance = Double.parseDouble(employeeD.getBalance());
//						
//						
//						elds.updateEmployeeLeaveDetails(employee.getFullName(), 
//								emailAddress, String.valueOf(calYear.get(Calendar.YEAR)-1), 
//								employeeD.getLastYearBalance(), employeeD.getEntitledAnnual(),
//								entitledCompensation.toString(), noPayLeave.toString(), 
//								sickLeave.toString(), annualLeave.toString(), 
//								compensationLeave.toString(), compassionateLeave.toString(), 
//								birthdayLeave.toString(),
//								maternityLeave.toString(), weddingLeave.toString(),
//								balance.toString(), employeeD.getRegion());
//					}
//					HistoryService hs = new HistoryService();
//					String timeNow = Misc.now();
//					hs.addToHistory(timeNow, emailAddress, numOfDays, startDate, endDate, leaveType, supervisor, remark, region, projectName, attachmentUrl,eAdd);
					
					errorMap.put("update.success", properties.getProperty("update.success"));
					req.setAttribute("errorMap", errorMap);
					getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
					return;
				} else if (existDirectory == false) {
					try {
						log.error(""+properties.getProperty("invalid.emp.leave.detail"));
						errorMap.put("invalid.emp.leave.detail", properties.getProperty("invalid.emp.leave.detail"));
						req.setAttribute("errorMap", errorMap);
						
						getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
						return;
					} catch (ServletException e) {
						log.error("UpdateApprovedRequest * doPost - error 3: " + e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				log.error("UpdateApprovedRequest validate email error: " + e1.getMessage());
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateApprovedRequest.class);
		String domain = "";
		String appAdminAccount = "";
		String appAdminPassword = "";
		String emailAddress = request.getParameter("emailAddress");
		boolean existDirectory = false;
		
		if(StringUtils.isNotBlank(emailAddress)){
			Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
			//check email pattern
	        if (!pattern.matcher(emailAddress).matches()) {
	        	
	        	try {
	        		request.setAttribute("feedback", ConstantUtils.ERROR);
	        		request.setAttribute("message", "Invalid email format");
	        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
	        		return;
	        	} catch (Exception e1) {
	    			log.error("UpdateApprovedRequest validate email error: " + e1.getMessage());
	    			e1.printStackTrace();
	    		}
	        }
	        else{
	        	SettingService ss = new SettingService();
	    		for (Setting set : ss.getSetting()) {
	    				domain = set.getAppDomain();
	    				appAdminAccount = set.getAppAdminAcc();
	    				appAdminPassword = set.getAppAdminAccPass();
	    			
	    		}
	    		try {
//	    			String appDomain = domain;
//	    			AppsForYourDomainClient client = new AppsForYourDomainClient(appAdminAccount, appAdminPassword, appDomain);
//	    			UserFeed uf = client.retrieveAllUsers();
//	    			String tmpAdd = emailAddress;
//	    			int ind = tmpAdd.indexOf("@");
//	    			tmpAdd = tmpAdd.substring(0, ind);
//	    			/* check if exist in domain directory listing */
//	    			for (int j=0; j<uf.getEntries().size(); j++) {
//	    				UserEntry entry = uf.getEntries().get(j);
//	    				if (entry.getTitle().getPlainText().equalsIgnoreCase(tmpAdd)){
//	    					existDirectory = true;
//	    					break;
//	    				}
//	    			}
	    			
	    			EmployeeService es = new EmployeeService(); 
	    			MCEmployee employee = es.findMCEmployeeByColumnName("emailAddress", emailAddress);
	    			if(StringUtils.isNotBlank(employee.getEmailAddress())){
	    				existDirectory = true;
	    			}
	    			
	    			String emailDomain [] = emailAddress.split("@");
	    			boolean hasDomain = false;
	    			for(String d : domain.split(",")){
	    				if(d.equals(emailDomain[1])){
	    					hasDomain = true;
	    				}
	    			}
	    			if (hasDomain == false) {
	    				request.setAttribute("emailAddress", emailAddress);
    					request.setAttribute("feedback", ConstantUtils.ERROR);
		        		request.setAttribute("message", "This domain "+emailDomain[1]+" does not exist, please check.");
		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
		        		return;
	    			}
	    			if (existDirectory == false) {
	    				try {
	    					request.setAttribute("emailAddress", emailAddress);
	    					request.setAttribute("feedback", ConstantUtils.ERROR);
    		        		request.setAttribute("message", "This email address "+emailAddress+" does not exist, please check.");
    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
    		        		return;
	    				} catch (ServletException e) {
	    					log.error("UpdateApprovedRequest * doPost - error3: " + e.getMessage());
	    					e.printStackTrace();
	    				}
	    			}
	    		} catch (Exception e1) {
	    			log.error("UpdateApprovedRequest validate email error: " + e1.getMessage());
	    			e1.printStackTrace();
	    		}
	        }
	        
		}
		else{
			request.setAttribute("feedback", ConstantUtils.ERROR);
    		request.setAttribute("message", "Mandatory field");
    		try{
    			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
    			return;
    		} catch (Exception e) {
    			log.error("UpdateApprovedRequest validate email error: " + e.getMessage());
    			e.printStackTrace();
    		}
    		
    		
		}
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

