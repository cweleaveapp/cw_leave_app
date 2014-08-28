package com.google.appengine.mct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;

@SuppressWarnings("serial")
public class UpdateEmpLeaveDetailsAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateEmpLeaveDetailsAction.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateEmpLeaveDetailsAction.class);
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		String emailAddress = req.getParameter("emailAddress");
		String year = req.getParameter("year");
		String lastYearBal = req.getParameter("lastYearBal");
		String entitledAnnual = req.getParameter("entitledAnnual");
		String entitledComp = req.getParameter("entitledComp");
		String noPayLeave = req.getParameter("noPayLeave");
		String sickLeave = req.getParameter("sickLeave");
		String annualLeave = req.getParameter("annualLeave");
		String birthdayLeave = req.getParameter("birthdayLeave");
		String compensationLeave = req.getParameter("compensationLeave");
		String compassionateLeave = req.getParameter("compassionateLeave");
		String maternityLeave = req.getParameter("maternityLeave");
		String weddingLeave = req.getParameter("weddingLeave");
		String others = req.getParameter("others");
		String region = req.getParameter("cri_region");
		String cmd = req.getParameter("cmd");
		boolean existDirectory = true;
		String balance;
		double balanceDb;
		String domain = "";
		String appAdminAccount = "";
		String appAdminPassword = "";
		
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				domain = set.getAppDomain();
				appAdminAccount = set.getAppAdminAcc();
				appAdminPassword = set.getAppAdminAccPass();
		}
		
		req.setAttribute("emailAddress", emailAddress);
		req.setAttribute("year", year);
		req.setAttribute("lastYearBal", lastYearBal);
		req.setAttribute("entitledAnnual", entitledAnnual);
		req.setAttribute("entitledComp", entitledComp);
		req.setAttribute("noPayLeave", noPayLeave);
		req.setAttribute("sickLeave", sickLeave);
		req.setAttribute("annualLeave", annualLeave);
		req.setAttribute("birthdayLeave", birthdayLeave);
		req.setAttribute("compensationLeave", compensationLeave);
		req.setAttribute("compassionateLeave", compassionateLeave);
		req.setAttribute("maternityLeave", maternityLeave);
		req.setAttribute("weddingLeave", weddingLeave);
		req.setAttribute("others", others);
		req.setAttribute("cri_region", region);
		boolean hasDomain = false;
		for(String d : domain.split(",")){
			if(d.equals(emailAddress.split("@")[1])){
				hasDomain = true;
			}
		}
		
		if (hasDomain == false) {
			log.error(""+properties.getProperty("invalid.domain.name"));
			errorMap.put("invalid.domain.name", properties.getProperty("invalid.domain.name"));
			
		}
		
		
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
		
		if(!NumberUtils.isNumber(entitledComp)){
			log.error(""+properties.getProperty("entitled.Comp.invalid.numerical"));
			errorMap.put("entitled.Comp.invalid.numerical", properties.getProperty("entitled.Comp.invalid.numerical"));
		}
		
		if(!NumberUtils.isNumber(noPayLeave)){
			log.error(""+properties.getProperty("noPayLeave.invalid.numerical"));
			errorMap.put("noPayLeave.invalid.numerical", properties.getProperty("noPayLeave.invalid.numerical"));
		}
		
		if(!NumberUtils.isNumber(sickLeave)){
			log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
			errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
		}
		
		if(!NumberUtils.isNumber(annualLeave)){
			log.error(""+properties.getProperty("annual.leave.invalid.numerical"));
			errorMap.put("annual.leave.invalid.numerical", properties.getProperty("annual.leave.invalid.numerical"));
		}
		
		if(!NumberUtils.isNumber(birthdayLeave)){
			log.error(""+properties.getProperty("birthday.leave.invalid.numerical"));
			errorMap.put("birthday.leave.invalid.numerical", properties.getProperty("birthday.leave.invalid.numerical"));
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
		
		if(!NumberUtils.isNumber(weddingLeave)){
			log.error(""+properties.getProperty("wedding.leave.invalid.numerical"));
			errorMap.put("wedding.leave.invalid.numerical", properties.getProperty("wedding.leave.invalid.numerical"));
		}
		
		if(!NumberUtils.isNumber(others)){
			log.error(""+properties.getProperty("others.invalid.numerical"));
			errorMap.put("others.invalid.numerical", properties.getProperty("others.invalid.numerical"));
		}
		
			/* check for numerical characters */
			Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
			//check email pattern
	        if (!pattern.matcher(emailAddress).matches()) {
	        	
	        		log.error(""+properties.getProperty("invalid.email.format"));
	    			errorMap.put("invalid.email.format", properties.getProperty("invalid.email.format"));
	        }
	        
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
					
					balanceDb = (Double.parseDouble(entitledAnnual) + Double.parseDouble(lastYearBal)) - (Double.parseDouble(annualLeave));
					// annual leave balance 
					balance = Double.toString(balanceDb);
					EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
					
						try {
							EmployeeService ems = new EmployeeService();
							MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress",emailAddress);
							elds.updateEmployeeLeaveDetails(employee.getFullName(), emailAddress, year, lastYearBal, entitledAnnual, 
									entitledComp, noPayLeave, sickLeave, annualLeave, compensationLeave,
									compassionateLeave, birthdayLeave, maternityLeave, weddingLeave, others, balance, region);

							log.error(""+properties.getProperty("update.success"));
			    			errorMap.put("update.success", properties.getProperty("update.success"));
			    			req.setAttribute("errorMap", errorMap);
			    			getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
							return;
						} catch (EntityNotFoundException e) {
							e.printStackTrace();
						}
					
					
			
			} catch (Exception e) {
				log.error("UpdateEmpLeaveDetailsAction validate email error: " + e.getMessage());
			
			}
		}
	


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
