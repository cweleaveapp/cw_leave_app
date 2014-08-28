package com.google.appengine.mct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;

@SuppressWarnings("serial")
public class UpdateEmployeeAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateEmployeeAction.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateEmployeeAction.class);
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		String emailAddress = req.getParameter("emailAddress");
		String fullName = req.getParameter("fullName");
		String region = req.getParameter("region");
		String hiredDate = req.getParameter("hiredDate");
		String birthDate = req.getParameter("birthDate");
		String resignedDate = req.getParameter("resignedDate");
		String supervisor = req.getParameter("supervisor");
		String jobTitle = req.getParameter("jobTitle");
		String cmd = req.getParameter("cmd");
		req.setAttribute("emailAddress", emailAddress);
		req.setAttribute("fullName", fullName);
		req.setAttribute("region", region);
		req.setAttribute("hiredDate", hiredDate);
		req.setAttribute("birthDate", birthDate);
		req.setAttribute("resignedDate", resignedDate);
		req.setAttribute("supervisor", supervisor);
		req.setAttribute("jobTitle", jobTitle);
		
		boolean existDirectory = true;
		String domain = "";
		String appAdminAccount = "";
		String appAdminPassword = "";
		
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
				domain = set.getAppDomain();
				appAdminAccount = set.getAppAdminAcc();
				appAdminPassword = set.getAppAdminAccPass();
			
		}
		
		if(StringUtils.isBlank(jobTitle)){
			log.error(""+properties.getProperty("job.title.mandatory"));
			errorMap.put("job.title.mandatory", properties.getProperty("job.title.mandatory"));
			req.setAttribute("errorMap", errorMap);
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean hasDomain = false;
		for(String d : domain.split(",")){
			if(d.equals(emailAddress.split("@")[1])){
				hasDomain = true;
			}
		}
		if (hasDomain == false) {
			log.error(""+properties.getProperty("invalid.domain.name"));
			errorMap.put("invalid.domain.name", properties.getProperty("invalid.domain.name"));
			req.setAttribute("errorMap", errorMap);
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			/* check for numerical characters */
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
	    			e1.printStackTrace();
	    		}
	        }
			try {
				//checking the real domain in google apps
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

//				if (existDirectory == true) {
					/* check for numerical characters in name */
					for (int i=0; i<fullName.length(); i++) {
						if (Character.isDigit(fullName.charAt(i))) {
							
							log.error(""+properties.getProperty("invalid.name.format"));
			    			errorMap.put("invalid.name.format", properties.getProperty("invalid.name.format"));
			    			req.setAttribute("errorMap", errorMap);
			    			getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
							return;
						}
						if (!Character.isLetter(fullName.charAt(i))) {
							if (!Character.isWhitespace(fullName.charAt(i)) && fullName.charAt(i) != ',') {
								log.error(""+properties.getProperty("invalid.name.format"));
				    			errorMap.put("invalid.name.format", properties.getProperty("invalid.name.format"));
				    			req.setAttribute("errorMap", errorMap);
				    			getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
								return;
							}
						}
					}
					try {
							EmployeeService es = new EmployeeService();
							es.updateMCEmployee(emailAddress, fullName, region, hiredDate, birthDate,
									resignedDate, supervisor, jobTitle);
							errorMap.put("save.success", properties.getProperty("save.success"));
							req.setAttribute("errorMap", errorMap);
							getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
			        		return;
						
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
//				} else if (existDirectory == false) {
//					
//					log.error(""+properties.getProperty("not.exist.directory"));
//					errorMap.put("not.exist.directory", properties.getProperty("not.exist.directory"));
//					req.setAttribute("errorMap", errorMap);
//					try {
//						
//						getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
//						return;
//					} catch (ServletException e) {
//						e.printStackTrace();
//					}
//				}
			} catch (Exception e) {
				log.error("UpdateEmployeeAction validate email error: " + e.getMessage());
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String domain = "";
		String appAdminAccount = "";
		String appAdminPassword = "";
		String emailAddress = request.getParameter("emailAddress");
		boolean existDirectory = true;
		
		
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
	    			log.error("UpdateEmployeeAction validate email error: " + e1.getMessage());
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
	    				Query query = new Query(MCEmployee.class.getSimpleName());
	    				for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
	    					String tmp = (String)entity.getProperty("emailAddress");
	    					if (tmp.equalsIgnoreCase(emailAddress)) {
	    						exist = true;
	    					}
	    				}
	    				
	    				
//	    			} 
	    			
	    			
	    			if (exist == false) {
	    				try {
	    					request.setAttribute("emailAddress", emailAddress);
	    					request.setAttribute("feedback", ConstantUtils.ERROR);
    		        		request.setAttribute("message", "This email address "+emailAddress+" does not exist, please check.");
    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
    		        		return;
	    				} catch (ServletException e) {
	    					log.error("UpdateEmployeeAction * doPost - error3: " + e.getMessage());
	    					e.printStackTrace();
	    				}
	    			}
	    			
	    			
	    		} catch (Exception e1) {
	    			log.error("UpdateEmployeeAction validate email error: " + e1.getMessage());
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
    			log.error("UpdateEmployeeAction validate email error: " + e.getMessage());
    			e.printStackTrace();
    		}
    		
    		
		}
	}

}
