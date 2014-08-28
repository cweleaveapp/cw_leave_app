package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.util.ConstantUtils;
import com.ibm.icu.util.Calendar;

@SuppressWarnings("serial")
public class RequestAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(RequestAction.class);

	public static String passed = "";
	public static String failed = "";
	public static String eAdd = "";
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(RequestAction.class);
		
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
//		String statusList [] = req.getParameterValues("statusList[]");
		Map<String,String[]> statusMap = req.getParameterMap();
		List<String[]> statusList = new ArrayList<String[]>();
		if(statusMap != null && !statusMap.isEmpty()){
			for (Map.Entry<String, String[]> entry : statusMap.entrySet()) {
				for(int i =0; i < entry.getValue().length; i++){
					if(!entry.getValue()[i].equals("10")){
						statusList.add(entry.getValue());
					}
				}
			}
			
		}
		
		String leaveType = "";
		String abb = "";
		String emailAddress = (String)req.getSession().getAttribute("emailAdd");
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
		Boolean isAdmin = false;
		if(StringUtils.isNotBlank(ad.getEmailAddress())){
			isAdmin = true;
		}
		
		Misc misc = new Misc();
		try{
		if(statusList != null && !statusList.isEmpty() ){
			
			for(String [] value : statusList){
				
				String status = Arrays.toString(value).replace("[", "").replace("]", "");
				String id = Arrays.toString(value).replaceAll(ConstantUtils.APPROVE, "").replaceAll(ConstantUtils.REJECT, "").replace("[", "").replace("]", "");
				
				LeaveQueueService lqs = new LeaveQueueService();
				if(status.contains(ConstantUtils.APPROVE)){
					for (LeaveQueue lq : lqs.getLeaveQueue()) {
						if (id.equalsIgnoreCase(lq.getId())) {
							int tmpInd = 0;
							int month = 0;
							boolean exist = false;
							String year = ""; 
							String monthStr = "";
							String tmpDate = lq.getTime();
							tmpInd = tmpDate.lastIndexOf("-");
							String eAdd = lq.getEmailAdd();
							String numDays = lq.getNumOfDays();
							String lastYearBalance = "";
							String entitledAnnual = "", entitledCompensation = "";
							String noPayLeave = "", sickLeave = "", annualLeave = "";
							String compensationLeave = "", compassionateLeave = "";
							String birthdayLeave = "", maternityLeave = "";
							String weddingLeave = "", others = "", balance = ""; 
							String region = "";
							if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(lq.getLeaveType())){
								String startDate = lq.getStartDate();
								int firstInd = 0;
								int lastInd = 0;
								firstInd = startDate.indexOf("-");
								lastInd = startDate.lastIndexOf("-");
								year = startDate.substring(lastInd+1, startDate.length());
								monthStr = startDate.substring(firstInd+1, lastInd);
								month = Integer.parseInt(monthStr);
								if (month < 3) {
									int yearInt = Integer.parseInt(year);
									yearInt = yearInt - 1;
									year = Integer.toString(yearInt);
								}
								
							}
							else{
								Calendar currYear = Calendar.getInstance(Locale.getDefault());
								month = currYear.get(Calendar.MONTH);
								year = String.valueOf(currYear.get(Calendar.YEAR));
								if (month < 3) {
									int yearInt = Integer.parseInt(year);
									yearInt = yearInt - 1;
									year = Integer.toString(yearInt);
								}
								
								
							}
							
							EmployeeService ems = new EmployeeService();
							MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress",lq.getEmailAdd());
							
							leaveType = lq.getLeaveType();
							EmailSettingService ess = new EmailSettingService();
							EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
//							for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
							EmployeeLeaveDetails eld = new EmployeeLeaveDetails();
							eld = elds.findEmployeeLeaveDetails(eAdd,year);
							if(eld != null){
								exist = true;
								double tmpInt1 = 0, tmpInt2 = 0, tmpInt3 = 0;
								entitledCompensation = eld.getEntitledAnnual();
								entitledAnnual = eld.getEntitledAnnual();
								compensationLeave = eld.getEntitledAnnual();
								compassionateLeave = eld.getCompassionateLeave();
								lastYearBalance = eld.getLastYearBalance();
								noPayLeave = eld.getNoPayLeave();
								sickLeave = eld.getSickLeaveFP();
								annualLeave = eld.getAnnualLeave();
								birthdayLeave = eld.getBirthdayLeave();
								maternityLeave = eld.getMaternityLeave();
								weddingLeave = eld.getMarriageLeave();
								others = eld.getOthers();
								balance = eld.getBalance();
								region = eld.getRegion();
								
								if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)){
									
								if (eAdd.equalsIgnoreCase(eld.getEmailAddress()) && 
										(eld.getYear().equals(year))){
									
									if (ConstantUtils.ANNUAL_LEAVE.equals(leaveType)) {
										
										if(!ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											if(Double.parseDouble(numDays) >
											Double.parseDouble(balance)){
												log.error(""+properties.getProperty("insufficient.leave.balance"));
												errorMap.put("insufficient.leave.balance", properties.getProperty("insufficient.leave.balance"));
												req.setAttribute("errorMap", errorMap);
												getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
												return;
											}
										}
										
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double annual = Double.parseDouble(annualLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												annual = annual + Double.parseDouble(numDays);
												annualLeave = annual.toString();
												// if total annual leave apply more than entitle annual leave
												if(annual > Double.parseDouble(entitledAnnual)){
													log.error(""+properties.getProperty("insufficient.leave.balance"));
													errorMap.put("insufficient.leave.balance", properties.getProperty("insufficient.leave.balance"));
													req.setAttribute("errorMap", errorMap);
													getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
													return;
												}
												
												//to get actual balance number of day apply
												Double actualDay = Double.parseDouble(originalNumOfDays) - Double.parseDouble(numDays);
												tmpInt3 = Double.parseDouble(balance) + actualDay;
											
											}
											else{
												tmpInt1 = Double.parseDouble(annualLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
												//balance annual leave
												annualLeave = Double.toString(tmpInt3);
												//total balance leave left
												tmpInt3 = Double.parseDouble(balance) -  tmpInt2;
											}
											
											balance = Double.toString(tmpInt3);
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double annual = Double.parseDouble(annualLeave) - Double.parseDouble(originalNumOfDays);
												annualLeave = annual.toString();
												
												//to get actual balance number of day apply
												Double actualDay = Double.parseDouble(originalNumOfDays);
												tmpInt3 = Double.parseDouble(balance) + actualDay;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(balance);
											}
											
										}
										else{
											tmpInt1 = Double.parseDouble(annualLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
											annualLeave = Double.toString(tmpInt3);
											tmpInt3 = Double.parseDouble(balance) - Double.parseDouble(numDays);
										}
										
										balance = Double.toString(tmpInt3);
									} 
									else if (ConstantUtils.COMPENSATION_LEAVE.equals(leaveType)) {
										
										if(!ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											if(Double.parseDouble(compensationLeave) + Double.parseDouble(numDays) >
											Double.parseDouble(entitledCompensation)){
												log.error(""+properties.getProperty("insufficient.leave.balance"));
												errorMap.put("insufficient.leave.balance", properties.getProperty("insufficient.leave.balance"));
												req.setAttribute("errorMap", errorMap);
												getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
												return;
											}
										}
										
										
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double compensation = Double.parseDouble(compensationLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												compensation = compensation + Double.parseDouble(numDays);
												
												if(compensation > Double.parseDouble(entitledCompensation)){
													log.error(""+properties.getProperty("insufficient.leave.balance"));
													errorMap.put("insufficient.leave.balance", properties.getProperty("insufficient.leave.balance"));
													req.setAttribute("errorMap", errorMap);
													getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
													return;
												}
												
												tmpInt3 = compensation;
												
											
											}
											else{
												tmpInt1 = Double.parseDouble(compensationLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double compensation = Double.parseDouble(compensationLeave) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = compensation;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(compensationLeave);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(compensationLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
											
										}
										compensationLeave = Double.toString(tmpInt3);
//									}
//									else if (ConstantUtils.SICK_LEAVE.equals(leaveType)) {
//										
//										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
//											//find original approve leave 
//											ApprovedLeaveService aps = new ApprovedLeaveService();
//											
//											//check if this leave approve before
//											if(StringUtils.isNotBlank(lq.getApproveId())){
//												Entity approve = aps.findEntity(lq.getApproveId());
//												String originalNumOfDays = (String)approve.getProperty("numOfDays");
//												//minux original numOfDays 
//												Double leave = Double.parseDouble(sickLeave) - Double.parseDouble(originalNumOfDays);
//												//add current apply numOfDays
//												leave = leave + Double.parseDouble(numDays);
//												tmpInt3 = leave;
//												
//												//to get actual balance number of day apply
////												Double actualDay = Double.parseDouble(originalNumOfDays) - Double.parseDouble(numDays);
////												tmpInt3 = Double.parseDouble(balance) + actualDay;
//											
//											}
//											else{
//												tmpInt1 = Double.parseDouble(sickLeave);
//												tmpInt2 = Double.parseDouble(numDays);
//												tmpInt3 = tmpInt1 + tmpInt2;
//												//balance sick leave
//												sickLeave = Double.toString(tmpInt3);
//												//total balance leave left
//												tmpInt3 = Double.parseDouble(balance) -  tmpInt2;
//											}
//											
//											balance = Double.toString(tmpInt3);
//										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
//											//check if this leave approve before
//											if(StringUtils.isNotBlank(lq.getApproveId())){
//												//find original approve leave 
//												ApprovedLeaveService aps = new ApprovedLeaveService();
//												Entity approve = aps.findEntity(lq.getApproveId());
//												String originalNumOfDays = (String)approve.getProperty("numOfDays");
//												 
//												//minux original numOfDays 
//												Double leave = Double.parseDouble(sickLeave) - Double.parseDouble(originalNumOfDays);
//												tmpInt3 = leave;
//												
//												//to get actual balance number of day apply
////												Double actualDay = Double.parseDouble(originalNumOfDays);
//												//tmpInt3 = Double.parseDouble(balance) + actualDay;
//												
//											}
//										}else{
//											tmpInt1 = Double.parseDouble(sickLeave);
//											tmpInt2 = Double.parseDouble(numDays);
//											tmpInt3 = tmpInt1 + tmpInt2;
//										}
//										
//										sickLeave = Double.toString(tmpInt3);
									} else if (ConstantUtils.NO_PAY_LEAVE.equals(leaveType)) {
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double leave = Double.parseDouble(noPayLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												leave = leave + Double.parseDouble(numDays);
												tmpInt3 = leave;
												
											
											}
											else{
												tmpInt1 = Double.parseDouble(noPayLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double leave = Double.parseDouble(noPayLeave) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = leave;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(noPayLeave);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(noPayLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
										}
										
										noPayLeave = Double.toString(tmpInt3);
									
									/*** Leave Type for Others ***/
									} else if (ConstantUtils.OTHERS.equals(leaveType)) {
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double leave = Double.parseDouble(others) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												leave = leave + Double.parseDouble(numDays);
												tmpInt3 = leave;
												
											
											}
											else{
												tmpInt1 = Double.parseDouble(others);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double leave = Double.parseDouble(others) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = leave;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(others);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(others);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
										}
										
										others = Double.toString(tmpInt3);
										
									} else if (ConstantUtils.COMPANSSIONATE_LEAVE.equals(leaveType)) {
										
										if(!ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())
												&& Double.parseDouble(compassionateLeave) + Double.parseDouble(numDays) >3 ){
											log.error(""+properties.getProperty("compassionate.leave.maximum.day"));
											errorMap.put("compassionate.leave.maximum.day", properties.getProperty("compassionate.leave.maximum.day"));
											req.setAttribute("errorMap", errorMap);
											getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
											return;
										}
										
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double leave = Double.parseDouble(compassionateLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												leave = leave + Double.parseDouble(numDays);
												tmpInt3 = leave;
												
											}
											else{
												tmpInt1 = Double.parseDouble(compassionateLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double leave = Double.parseDouble(compassionateLeave) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = leave;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(compassionateLeave);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(compassionateLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
										}
										
										compassionateLeave = Double.toString(tmpInt3);
									} else if (ConstantUtils.BIRTHDAY_LEAVE.equals(leaveType)) {
										
										if(!ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											if(Double.parseDouble(birthdayLeave) + Double.parseDouble(numDays) > 1 ){
												log.error(""+properties.getProperty("invalid.birthday.leave"));
												errorMap.put("invalid.birthday.leave", properties.getProperty("invalid.birthday.leave"));
												req.setAttribute("errorMap", errorMap);
												getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
												return;
											}
										}
										
										
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double leave = Double.parseDouble(birthdayLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												leave = leave + Double.parseDouble(numDays);
												tmpInt3 = leave;
											
											}
											else{
												tmpInt1 = Double.parseDouble(birthdayLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
											balance = Double.toString(tmpInt3);
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double leave = Double.parseDouble(birthdayLeave) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = leave;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(birthdayLeave);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(birthdayLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
										}
										
										birthdayLeave = Double.toString(tmpInt3);
									} else if (ConstantUtils.MATERNITY_LEAVE.equals(leaveType)) {
										
										if(ConstantUtils.MALAYSIA.equals(employee.getRegion())){
											if(!ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())
													&& Double.parseDouble(maternityLeave) + Double.parseDouble(numDays) > 60 ){
												log.error(""+properties.getProperty("maternity.leave.maximum.day"));
												errorMap.put("maternity.leave.maximum.day", properties.getProperty("maternity.leave.maximum.day"));
												req.setAttribute("errorMap", errorMap);
												getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
												return;
											}
										}
										
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double leave = Double.parseDouble(maternityLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												leave = leave + Double.parseDouble(numDays);
												tmpInt3 = leave;
												
											}
											else{
												tmpInt1 = Double.parseDouble(maternityLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double leave = Double.parseDouble(maternityLeave) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = leave;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(maternityLeave);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(maternityLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
										}
										
										maternityLeave = Double.toString(tmpInt3);
									} else if (ConstantUtils.WEDDING_LEAVE.equals(leaveType)) {
										
										if(!ConstantUtils.CHINA.equals(employee.getRegion()) &&
												!ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											if(Double.parseDouble(weddingLeave) + Double.parseDouble(numDays) > 3 ){
												log.error(""+properties.getProperty("wedding.leave.maximum.day"));
												errorMap.put("wedding.leave.maximum.day", properties.getProperty("wedding.leave.maximum.day"));
												req.setAttribute("errorMap", errorMap);
												getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
												return;
											}
										}
										
										
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double leave = Double.parseDouble(weddingLeave) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												leave = leave + Double.parseDouble(numDays);
												tmpInt3 = leave;
											
											}
											else{
												tmpInt1 = Double.parseDouble(weddingLeave);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double leave = Double.parseDouble(weddingLeave) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = leave;
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(weddingLeave);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(weddingLeave);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
										}
										
										weddingLeave = Double.toString(tmpInt3);
									}
								}
								}
							}
							else{
								try {
									String message = "GAE Leave Apps Failure at: \n"
								+"com.google.appengine.mct.RequestAction - error:";
									misc.postMailToSysAdmin(message);
									
									log.debug(""+properties.getProperty("approve.leave.request.exception"));
									errorMap.put("approve.leave.request.exception", properties.getProperty("approve.leave.request.exception"));
									req.setAttribute("errorMap", errorMap);
									getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
									return;
									
								}
								catch(Exception es) {
									es.printStackTrace();
								}
							}
							
							if (exist == false) {
								
								
								try {
									if(ConstantUtils.TRUE.equals(isAdmin)){
										log.error(""+properties.getProperty("invalid.emp.leave.detail"));
										errorMap.put("invalid.emp.leave.detail", properties.getProperty("invalid.emp.leave.detail"));
									}else{
										log.error(""+properties.getProperty("invalid.emp.leave.detail.admin"));
										errorMap.put("invalid.emp.leave.detail.admin", properties.getProperty("invalid.emp.leave.detail.admin"));
										
									}
									req.setAttribute("errorMap", errorMap);
									getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
									return;
								} catch (ServletException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if (exist == true) {
								elds.updateEmployeeLeaveDetails(employee.getFullName(), eAdd, year, lastYearBalance, 
										entitledAnnual, entitledCompensation, noPayLeave, sickLeave, 
										annualLeave, compensationLeave, compassionateLeave, birthdayLeave, 
										maternityLeave, weddingLeave, others, balance, region);
							}
							
							if (ConstantUtils.ANNUAL_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.ANNUAL_LEAVE;
							} else if (ConstantUtils.SICK_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.SICK_LEAVE;
							} else if (ConstantUtils.COMPENSATION_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.COMPENSATION_LEAVE;
							} else if (ConstantUtils.NO_PAY_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.NO_PAY_LEAVE;
							} else if (ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)) {
								abb = ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT;
							} else if (ConstantUtils.BIRTHDAY_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.BIRTHDAY_LEAVE;
							} else if (ConstantUtils.MATERNITY_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.MATERNITY_LEAVE;
							} else if (ConstantUtils.WEDDING_LEAVE.equals(leaveType)) {
								abb = ConstantUtils.WEDDING_LEAVE;
							} else if (ConstantUtils.OTHERS.equals(leaveType)) {
								abb = ConstantUtils.OTHERS;
							}
							String desc = "";
							String regioner = employee.getRegion();
							desc = lq.getEmailAdd() + " - " + abb;
							if (!ConstantUtils.AMEND_LEAVE.equals(leaveType) &&
									!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)) {
								misc.updateCalendar(lq.getStartDate(), lq.getEndDate(), desc, regioner);
							}
							if (ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveType)) {
//								List<String> listYear = new ArrayList<String>();
//								for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
//									listYear.add(eld.getYear());
									
									//to get the latest year
//									Collections.sort(listYear);
//									Collections.reverse(listYear);
//									year = listYear.get(0);
//									if (eAdd.equalsIgnoreCase(eld.getEmailAddress()) && 
//											listYear.get(0).equals(eld.getYear())) {
										double tmpInt1 = 0, tmpInt2 = 0, tmpInt3 = 0;
										entitledCompensation = eld.getEntitledAnnual();
										compensationLeave = eld.getEntitledAnnual();
										lastYearBalance = eld.getLastYearBalance();
										noPayLeave = eld.getNoPayLeave();
										sickLeave = eld.getSickLeaveFP();
										annualLeave = eld.getAnnualLeave();
										entitledAnnual = eld.getEntitledAnnual();
										compassionateLeave = eld.getCompassionateLeave();
										birthdayLeave = eld.getBirthdayLeave();
										maternityLeave = eld.getMaternityLeave();
										weddingLeave = eld.getMarriageLeave();
										others = eld.getOthers();
										balance = eld.getBalance();
										
//									}
//								}
										if(ConstantUtils.AMEND_LEAVE.equals(lq.getChangeType())){
											//find original approve leave 
											ApprovedLeaveService aps = new ApprovedLeaveService();
											
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												//minux original numOfDays 
												Double entitledComp = Double.parseDouble(entitledCompensation) - Double.parseDouble(originalNumOfDays);
												//add current apply numOfDays
												entitledComp = entitledComp + Double.parseDouble(numDays);
												tmpInt3 = entitledComp;
												
											
											}
											else{
												tmpInt1 = Double.parseDouble(entitledCompensation);
												tmpInt2 = Double.parseDouble(numDays);
												tmpInt3 = tmpInt1 + tmpInt2;
											}
											
										}else if(ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
											//check if this leave approve before
											if(StringUtils.isNotBlank(lq.getApproveId())){
												//find original approve leave 
												ApprovedLeaveService aps = new ApprovedLeaveService();
												Entity approve = aps.findEntity(lq.getApproveId());
												String originalNumOfDays = (String)approve.getProperty("numOfDays");
												 
												//minux original numOfDays 
												Double entitledComp = Double.parseDouble(entitledCompensation) - Double.parseDouble(originalNumOfDays);
												tmpInt3 = entitledComp;
												
												
											}
											else{
												// under leave queue 
												tmpInt3 = Double.parseDouble(entitledCompensation);
											}
										}
										else{
											tmpInt1 = Double.parseDouble(entitledCompensation);
											tmpInt2 = Double.parseDouble(numDays);
											tmpInt3 = tmpInt1 + tmpInt2;
											
										}
										entitledCompensation = Double.toString(tmpInt3);
										
								
								elds.updateEmployeeLeaveDetails(employee.getFullName(), eAdd, year, lastYearBalance, 
										entitledAnnual, entitledCompensation, noPayLeave, sickLeave, 
										annualLeave, compensationLeave, compassionateLeave, birthdayLeave, 
										maternityLeave, weddingLeave, others, balance, regioner);
							}
							
							ApprovedLeaveService als = new ApprovedLeaveService();
							if(!ConstantUtils.CANCEL_LEAVE.equals(lq.getChangeType())){
								als.addApprovedLeave(Misc.now(), lq.getEmailAdd(), lq.getNumOfDays(),
										lq.getStartDate(), lq.getEndDate(), lq.getLeaveType(),
										lq.getSupervisor(), lq.getRemark(), employee.getRegion(), 
										lq.getChangeType(), lq.getAttachmentUrl(), lq.getProjectName());
							}
							
							if(StringUtils.isNotBlank(lq.getApproveId())){
								als.deleteApprovedLeave(lq.getApproveId());
							}
							
							
							lqs.deleteLeaveQueue(lq.getId());
							
							misc.notifyEmployee(employee.getFullName(), lq.getTime(), lq.getEmailAdd(), lq.getNumOfDays(), leaveType, lq.getRemark(), 
									lq.getStartDate(), lq.getEndDate(), "approved");
							
							/* write to GDocs for record */
							String tmpStr = "Time="+lq.getTime()+",Employee="+lq.getEmailAdd()+",NumberOfDays="+lq.getNumOfDays()
							+",StartDate="+lq.getStartDate()+",EndDate="+lq.getEndDate()+",Supervisor="+lq.getSupervisor()
							+",LeaveType="+lq.getLeaveType()+",Remark="+lq.getRemark();
//							misc.storeInGDocsApprovedHistory(tmpStr);
						}
					}
				}
				if(status.contains(ConstantUtils.REJECT)){
					
							for (LeaveQueue lq : lqs.getLeaveQueue()) {
								if (id.equalsIgnoreCase(lq.getId())) {
									RejectedLeaveService rls = new RejectedLeaveService();
									EmployeeService ems = new EmployeeService();
									MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress",lq.getEmailAdd());
									
									rls.addRejectedLeave(lq.getTime(), lq.getEmailAdd(), lq.getNumOfDays(),
											lq.getStartDate(), lq.getEndDate(), lq.getLeaveType(),
											lq.getSupervisor(), lq.getRemark(), employee.getRegion());
									lqs.deleteLeaveQueue(lq.getId());
									misc.notifyEmployee(employee.getFullName(),lq.getTime(), lq.getEmailAdd(), lq.getNumOfDays(), leaveType, lq.getRemark(), 
											lq.getStartDate(), lq.getEndDate(), "rejected");
									
									/* write to GDocs for record */
									String tmpStr = "Time="+lq.getTime()+",Employee="+lq.getEmailAdd()+",NumberOfDays="+lq.getNumOfDays()
									+",StartDate="+lq.getStartDate()+",EndDate="+lq.getEndDate()+",Supervisor="+lq.getSupervisor()
									+",LeaveType="+lq.getLeaveType()+",Remark="+lq.getRemark();
//									misc.storeInGDocsRejectedHistory(tmpStr);
								}
							}
							
				}
				
			}
			
//			String pass = "You have successfully updated the leave request(s) status.";
//			this.getServletConfig().getServletContext().setAttribute("reqActionMsg", "");
//			this.getServletConfig().getServletContext().setAttribute("reqActionMsg", pass);
//			passed = this.getServletConfig().getServletContext().getAttribute("reqActionMsg").toString();
			
			
			
			log.debug(""+properties.getProperty("update.leave.request"));
			req.setAttribute("feedback", ConstantUtils.OK);
			req.setAttribute("message", properties.getProperty("update.leave.request"));
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			return;
		}
		else{
			log.debug(""+properties.getProperty("please.select.one.approve.reject"));
			errorMap.put("please.select.one.approve.reject", properties.getProperty("please.select.one.approve.reject"));
			req.setAttribute("errorMap", errorMap);
			getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
			return;
			
		}
		
		}catch(Exception e) {
//			String err = "There is an error in updating the leave request(s) status."
//					+ " The system administrator will be alerted immediately.";
//				this.getServletConfig().getServletContext().setAttribute("reqActionErrMsg", "");
//				this.getServletConfig().getServletContext().setAttribute("reqActionErrMsg", err);
//				failed = this.getServletConfig().getServletContext().getAttribute("reqActionErrMsg").toString();
				try {
					String message = "GAE Leave Apps Failure at: \n" +
					"com.google.appengine.mct.RequestAction - error: " + e.getMessage();
					misc.postMailToSysAdmin(message);
					log.error(""+e.getMessage());
					log.debug(""+properties.getProperty("approve.leave.request.exception"));
					errorMap.put("approve.leave.request.exception", properties.getProperty("approve.leave.request.exception"));
					req.setAttribute("errorMap", errorMap);
					getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
					return;
					
//				} catch (MessagingException me) {
//					log.error("RequestAction error: " + me.getMessage());
//					e.printStackTrace();
				}
				catch(Exception es) {
					es.printStackTrace();
				}
				
		}
		
		
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(RequestAction.class);
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		JSONObject rs = new JSONObject();
		MCSupervisorService ss = new MCSupervisorService();
		List<MCSupervisor> sList = ss.getSupervisors();
		boolean isSupervisor = false;
		try {
			
		if(StringUtils.isBlank(emailAddress)){
			isSupervisor = false;
		}
		else{
			
			if(sList != null && !sList.isEmpty()){
				for(MCSupervisor sv : sList){
					if(StringUtils.endsWithIgnoreCase(emailAddress, sv.getEmailAddress())){
						isSupervisor = true;
						
					}
				}
			}
			else{
				isSupervisor = false;
			}
			
		}
		
		rs.put("success", isSupervisor == true ? ConstantUtils.TRUE : ConstantUtils.FALSE);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(rs.toString());
		
		} catch (Exception e) {
			rs.put("success", ConstantUtils.FALSE);
			e.printStackTrace();
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(e.getMessage());
		} 
	}
	


	public String leaveReqActionMsg() {
		return passed;
	}


	public String leaveReqActionErrMsg() {
		return failed;
	}

}
