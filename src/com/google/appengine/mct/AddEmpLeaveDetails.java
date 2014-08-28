package com.google.appengine.mct;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.util.ConstantUtils;
import com.google.appengine.util.Helper;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;

@SuppressWarnings("serial")
public class AddEmpLeaveDetails extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddEmpLeaveDetails.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddEmpLeaveDetails.class);
		String emailAddress = req.getParameter("emailAddress");
		String year = req.getParameter("year");
		String lastYearBal = req.getParameter("lastYearBal");
		String entitledAnnual = req.getParameter("entitledAnnual");
		String annualLeave = req.getParameter("annualLeave");
		String compensationLeave = req.getParameter("compensationLeave");
		String compassionateLeave = req.getParameter("compassionateLeave");
		String sickLeaveFP = req.getParameter("sickLeaveFP");
		String sickLeavePP = req.getParameter("sickLeavePP");
		String examLeave = req.getParameter("examLeave");
		String injuryLeave = req.getParameter("injuryLeave");
		String juryLeave = req.getParameter("juryLeave");
		String marriageLeave = req.getParameter("marriageLeave");
		String maternityLeave = req.getParameter("maternityLeave");
		String paternityLeave = req.getParameter("paternityLeave");
		String noPayLeave = req.getParameter("noPayLeave");
		String dept = req.getParameter("dept");
		String cmd = req.getParameter("cmd");
		req.setAttribute("emailAddress", emailAddress);
		req.setAttribute("year", year);
		req.setAttribute("lastYearBal", lastYearBal);
		req.setAttribute("entitledAnnual", entitledAnnual);
		req.setAttribute("annualLeave", annualLeave);
		req.setAttribute("compensationLeave", compensationLeave);
		req.setAttribute("compassionateLeave", compassionateLeave);		
		req.setAttribute("sickLeaveFP", sickLeaveFP);
		req.setAttribute("sickLeavePP", sickLeavePP);
		req.setAttribute("examLeave", examLeave);
		req.setAttribute("injuryLeave", injuryLeave);
		req.setAttribute("juryLeave", juryLeave);
		req.setAttribute("marriageLeave", marriageLeave);
		req.setAttribute("maternityLeave", maternityLeave);
		req.setAttribute("paternityLeave", paternityLeave);
		req.setAttribute("noPayLeave", noPayLeave);
		req.setAttribute("dept", dept);
		boolean existDirectory = true;
		double balanceDb;
		String balance;
		String domain = "";
		String appAdminAccount = "";
		String appAdminPassword = "";
		
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		
		/*SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				domain = set.getAppDomain();
				appAdminAccount = set.getAppAdminAcc();
				appAdminPassword = set.getAppAdminAccPass();
			
		}*/

		if (cmd != null && !cmd.equalsIgnoreCase("") && !cmd.equalsIgnoreCase("End")) {
			if (cmd.equalsIgnoreCase("Import")) {
				
				String spdSvAcc = "";
				String spdSvAccPwd = "";
				
				List<String> myVec = new ArrayList<String>();
				
				try {
					SettingService setting = new SettingService();
					for (Setting set : setting.getSetting()) {
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
						title = entry.getTitle().getPlainText();
						docHref = entry.getSpreadsheetLink().getHref();
						if (StringUtils.isNotBlank(title)) {
//							int spaceInd = title.indexOf(" ");
//							String name = title.substring(0, spaceInd);
//							String years = title.substring(spaceInd+1, title.length());
//							int yearNum = 0;
//							yearNum = title.length() - (spaceInd+1);
//							if (yearNum == 4) {
								if (!myVec.contains(title)) {
									String data = title + "|" + docHref;
									myVec.add(data);
//								}
							}
						}
					}
					Collections.sort(myVec);
					
					req.setAttribute("myVec", myVec);
					
					getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-leave-details-import.jsp").forward(req, resp);

				} catch (Exception e) {
					log.error("Misc listDocumentsSb() error: " + e.getMessage());
					e.printStackTrace();
				}
				
			}
		} else {
			boolean emailValid = Helper.checkEmailValid(emailAddress);
			if(emailValid==false){
				log.error(""+properties.getProperty("invalid.domain.name"));
				errorMap.put("invalid.domain.name", properties.getProperty("invalid.domain.name"));
			}
			/*EmployeeService ems = new EmployeeService();
			
			boolean hasDomain = false;
			for(String d : domain.split(",")){
				if (emailAddress.split("@")[1].equals(d)) {
					hasDomain = true;
				}
			}
			if (hasDomain == false) {
				log.error(""+properties.getProperty("invalid.domain.name"));
				errorMap.put("invalid.domain.name", properties.getProperty("invalid.domain.name"));
			}*/
			
			if(!NumberUtils.isNumber(year)){
				log.error(""+properties.getProperty("year.invalid.numerical"));
				errorMap.put("year.invalid.numerical", properties.getProperty("year.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(lastYearBal)){
				log.error(""+properties.getProperty("lastYearBal.invalid.numerical"));
				errorMap.put("lastYearBal.invalid.numerical", properties.getProperty("lastYearBal.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(entitledAnnual)){
				log.error(""+properties.getProperty("entitled.annual.invalid.numerical"));
				errorMap.put("entitled.annual.invalid.numerical", properties.getProperty("entitled.annual.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(noPayLeave)){
				log.error(""+properties.getProperty("noPayLeave.invalid.numerical"));
				errorMap.put("noPayLeave.invalid.numerical", properties.getProperty("noPayLeave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(sickLeaveFP)){
				log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
				errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(sickLeavePP)){
				log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
				errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(annualLeave)){
				log.error(""+properties.getProperty("annual.leave.invalid.numerical"));
				errorMap.put("annual.leave.invalid.numerical", properties.getProperty("annual.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(compensationLeave)){
				log.error(""+properties.getProperty("compensation.leave.invalid.numerical"));
				errorMap.put("compensation.leave.invalid.numerical", properties.getProperty("compensation.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(compassionateLeave)){
				log.error(""+properties.getProperty("compassionate.leave.invalid.numerical"));
				errorMap.put("compassionate.leave.invalid.numerical", properties.getProperty("compassionate.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(maternityLeave)){
				log.error(""+properties.getProperty("maternity.leave.invalid.numerical"));
				errorMap.put("maternity.leave.invalid.numerical", properties.getProperty("maternity.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(marriageLeave)){
				log.error(""+properties.getProperty("wedding.leave.invalid.numerical"));
				errorMap.put("wedding.leave.invalid.numerical", properties.getProperty("wedding.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(paternityLeave)){
				log.error(""+properties.getProperty("paternity.leave.invalid.numerical"));
				errorMap.put("paternity.leave.invalid.numerical", properties.getProperty("paternity.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(injuryLeave)){
				log.error(""+properties.getProperty("injury.leave.invalid.numerical"));
				errorMap.put("injury.leave.invalid.numerical", properties.getProperty("injury.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(juryLeave)){
				log.error(""+properties.getProperty("jury.leave.invalid.numerical"));
				errorMap.put("jury.leave.invalid.numerical", properties.getProperty("jury.leave.invalid.numerical"));
			}
			
			if(!NumberUtils.isNumber(examLeave)){
				log.error(""+properties.getProperty("exam.leave.invalid.numerical"));
				errorMap.put("exam.leave.invalid.numerical", properties.getProperty("exam.leave.invalid.numerical"));
			}
//				Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
//				//check email pattern
//		        if (!pattern.matcher(emailAddress).matches()) {
//		        	log.error(""+properties.getProperty("invalid.email.format"));
//					errorMap.put("invalid.email.format", properties.getProperty("invalid.email.format"));
//		        	
//		        }
//		        
//		        MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress",emailAddress);
//		        if(StringUtils.isBlank(employee.getEmailAddress())){
//		        	log.error(""+properties.getProperty("employee.not.exist"));
//					errorMap.put("employee.not.exist", properties.getProperty("employee.not.exist"));
//		        }
		        
		        if(!errorMap.isEmpty()){
		        	try {
						req.setAttribute("errorMap", errorMap);
						getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
						return;
		        	} catch (Exception e1) {
		    			log.error("AddApprovedRequest validate email error: " + e1.getMessage());
		    			e1.printStackTrace();
		    		}
		        }
				try {
//					String appDomain = domain;
//					appDomain = appDomain.replace("@", "");
//					AppsForYourDomainClient client = new AppsForYourDomainClient(appAdminAccount, appAdminPassword, appDomain);
//					UserFeed uf = client.retrieveAllUsers();
//					String tmpAdd = emailAddress;
//					int ind = tmpAdd.indexOf("@");
//					tmpAdd = tmpAdd.substring(0, ind);
//					/* check if exist in domain directory listing */
//					for (int j=0; j<uf.getEntries().size(); j++) {
//						UserEntry entry = uf.getEntries().get(j);
//						if (entry.getTitle().getPlainText().contains(tmpAdd)){
//							existDirectory = true;
//							break;
//						}
//					}

//					if (existDirectory == true) {
						

					/* check if exist in the database */
					boolean exist = false;
					EmployeeLeaveDetails eldt = EmployeeLeaveDetailsService.getInstance().findEmployeeLeaveDetails(emailAddress, year);
					if(eldt!=null){
						exist = true;
					}
					if (exist == false) {
						balanceDb = (Double.parseDouble(entitledAnnual) + Double.parseDouble(lastYearBal)) - 
									 (Double.parseDouble(annualLeave));
						balance = Double.toString(balanceDb);
						EmployeeService ems = new EmployeeService();
						Employee employee = ems.findEmployeeByColumnName("emailAddress", emailAddress);
						EmployeeLeaveDetailsService.getInstance().addEmpLeaveDetails(emailAddress, year, lastYearBal,
								 entitledAnnual, balance, sickLeaveFP, sickLeavePP, annualLeave,
								 compassionateLeave, compensationLeave, examLeave, injuryLeave, juryLeave,
								 marriageLeave, maternityLeave, paternityLeave, noPayLeave);
						log.error(""+properties.getProperty("save.success"));
						errorMap.put("save.success", properties.getProperty("save.success"));
						req.setAttribute("errorMap", errorMap);
						getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.js" +
								"p").forward(req, resp);
						return;
							
					} else if (exist == true) {
						try {
							log.error(""+properties.getProperty("employee.exist"));
							errorMap.put("employee.exist", properties.getProperty("employee.exist"));
							req.setAttribute("errorMap", errorMap);
							getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
							return;
						} catch (ServletException e) {
							log.error("AddEmpLeaveDetails * doPost - error 4: " + e.getMessage());
							e.printStackTrace();
						}
					}
//					} 
				} catch (Exception e) {
					log.error("AddEmpLeaveDetails validate email error: " + e.getMessage());
				}
			}
		}
	


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
