package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class AddSetting extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddSetting.class);

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddSetting.class);
		
		String domain = "";
		String emailAddress = req.getParameter("emailAddress");
		String regionSelected[] = req.getParameterValues("region[]"); 
		boolean existDirectory = false;
		String strRegion = "";
		
			// Add supervisor and region(s)
			for (int i=0; i<regionSelected.length; i++) {
				if (i == 0) {
					strRegion = regionSelected[i].toString();
				} else {
					strRegion = strRegion + ", " + regionSelected[i].toString();
				}
			}
			
			/* Check with directory listing to see if this address exist */
			try {
				
				SettingService ss = new SettingService();
	    		for (Setting set : ss.getSetting()) {
	    				domain = set.getAppDomain();
	    		}
	    		
	    		EmailSettingService ess = new EmailSettingService();
				
				String emailDomain [] = emailAddress.split("@");
    			boolean hasDomain = false;
    			for(String d : domain.split(",")){
    				if(d.equals(emailDomain[1])){
    					hasDomain = true;
    				}
    			}
    			if (hasDomain == false) {
    				req.setAttribute("emailAddress", emailAddress);
    				req.setAttribute("feedback", ConstantUtils.ERROR);
    				req.setAttribute("message", "This domain "+emailDomain[1]+" does not exist, please check.");
	        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	        		return;
    			}
				
    			EmployeeService es = new EmployeeService(); 
    			MCEmployee employee = es.findMCEmployeeByColumnName("emailAddress", emailAddress);
    			if(StringUtils.isNotBlank(employee.getEmailAddress())){
    				existDirectory = true;
    			}
    			
				if (existDirectory == true) {
					/* check if exist in the database */
					boolean exist = false;
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Query query = new Query(EmailSetting.class.getSimpleName());
					for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
						String tmp = (String)entity.getProperty("emailAddress");
						if (tmp.equalsIgnoreCase(emailAddress)) {
							exist = true;
						}
					}
					if (exist == false) {
						if (regionSelected==null || regionSelected.length < 0) {
							req.setAttribute("feedback", ConstantUtils.ERROR);
							req.setAttribute("message", "Please select region.");
			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
							return;
						} else {
							
							ess.addEmailSetting(emailAddress, strRegion.replaceAll("-", " "));
							req.setAttribute("feedback", ConstantUtils.OK);
							req.setAttribute("message", "Save success.");
							getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			        		return;
						}
					} else if (exist == true) {
						try {
							req.setAttribute("feedback", ConstantUtils.ERROR);
							req.setAttribute("message", "The email address "+emailAddress+" already exist in the database.");
			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
							return;
						} catch (ServletException e) {
							log.error("AddSetting * doPost - error 5: " + e.getMessage());
							e.printStackTrace();
						}
					}
				} else { /* if not in directory */
					try {
						req.setAttribute("feedback", ConstantUtils.ERROR);
						req.setAttribute("message", "This email address "+emailAddress+" does not exist, please check.");
		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
						return;
					} catch (ServletException e) {
						log.error("AddSetting * doPost - error 1: " + e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				log.error("AddSetting doPost error: " + e.getMessage());
				e.printStackTrace();
			}
		
	}


	@SuppressWarnings("unused")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String emailAddress = request.getParameter("emailAddress");
		
		String domain = "";
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
	    			log.error("AddSetting validate email error: " + e1.getMessage());
	    			e1.printStackTrace();
	    		}
	        }
	        else{
	        	SettingService ss = new SettingService();
	    		for (Setting set : ss.getSetting()) {
	    				domain = set.getAppDomain();
	    		}
	    		try {
	    			//checking the real domain in google apps
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
	    			EmailSettingService ess = new EmailSettingService();
	    			EmailSetting emailSetting = ess.findEmailSettingByColumnName("emailAddress", emailAddress);
	    			if(StringUtils.isNotBlank(emailSetting.getEmailAddress())){
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
	    			
//	    			if (existDirectory == true) {
	    				/* check if exist in the database */
	    				boolean exist = false;
	    				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    				Query query = new Query(EmailSetting.class.getSimpleName());
	    				for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
	    					String tmp = (String)entity.getProperty("emailAddress");
	    					if (tmp.equalsIgnoreCase(emailAddress)) {
	    						exist = true;
	    					}
	    				}
	    				if (exist == true) {
	    					try {
	    						
	    						request.setAttribute("feedback", ConstantUtils.ERROR);
	    		        		request.setAttribute("message", "This email address "+emailAddress+" already exist in the database.");
	    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
	    		        		return;
//	    						getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-exist-in-db.jsp").forward(request, response);
//	    						return;
	    					} catch (ServletException e) {
	    						log.error("AddSetting * doPost - error2: " + e.getMessage());
	    						e.printStackTrace();
	    					}
	    				}
//	    			} else if (existDirectory == false) {
//	    				try {
//	    					request.setAttribute("emailAddress", emailAddress);
//	    					request.setAttribute("feedback", ConstantUtils.ERROR);
//    		        		request.setAttribute("message", "This email address "+emailAddress+" does not exist, please check.");
//    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
//    		        		return;
////	    					getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-not-exist.jsp").forward(request, response);
////	    					return;
//	    				} catch (ServletException e) {
//	    					log.error("AddSupervisor * doPost - error3: " + e.getMessage());
//	    					e.printStackTrace();
//	    				}
//	    			}
	    		} catch (Exception e1) {
	    			log.error("AddSetting validate email error: " + e1.getMessage());
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
    			log.error("AddSupervisor validate email error: " + e.getMessage());
    			e.printStackTrace();
    		}
    		
    		
		}
		
		
	}
}
