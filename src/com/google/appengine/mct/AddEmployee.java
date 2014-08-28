package com.google.appengine.mct;

import java.io.IOException;

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
import com.google.appengine.entities.Employee;
import com.google.appengine.enums.UserType;
import com.google.appengine.util.ConstantUtils;
import com.google.appengine.util.MyProperties;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;

import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class AddEmployee extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddEmployee.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		log.debug(AddEmployee.class);
		
		String staffId = req.getParameter("staffId");
		String emailAddress = req.getParameter("emailAddress");
		String fullName = req.getParameter("fullName");
		String department = req.getParameter("department");
		String hiredDate = req.getParameter("hiredDate");
		String birthDate = req.getParameter("birthDate");
		String supervisor = req.getParameter("supervisor");
		String jobTitle = req.getParameter("jobTitle");
		
		String cmd2 = req.getParameter("cmd2");
		boolean existDirectory = false;
		String domain = MyProperties.getValue("app.domain");
		
//		SettingService ss = new SettingService();
//		for (Setting set : ss.getSetting()) {
//				domain = set.getAppDomain();
//			
//		}

		if (cmd2 != null && !cmd2.equalsIgnoreCase("") && !cmd2.equalsIgnoreCase("End")) {
			if (cmd2.equalsIgnoreCase("Import")) {
				resp.sendRedirect("/admin-add-emp-import.jsp");
				return;
			} else if (cmd2.equalsIgnoreCase("Checking")) {
				/* Compare if it exist by checking the domain's directory */
				
			}
		} else {
			
			if(StringUtils.isBlank(jobTitle)){
				try {
	        		req.setAttribute("feedback", ConstantUtils.ERROR);
	        		req.setAttribute("message", "Job Title is mandatory");
	        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	        		return;
	        	} catch (Exception e1) {
	    			log.error("AddEmployee validate job title error: " + e1.getMessage());
	    			e1.printStackTrace();
	    		}
			}
			
			if(StringUtils.isNotBlank(emailAddress)){
			
				Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
				//check email pattern
		        if (!pattern.matcher(emailAddress).matches()) {
		        	
		        	try {
		        		req.setAttribute("feedback", ConstantUtils.ERROR);
		        		req.setAttribute("message", "Invalid email format");
		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
		        		return;
		        	} catch (Exception e1) {
		    			log.error("AddEmployee validate email error: " + e1.getMessage());
		    			e1.printStackTrace();
		    		}
		        }
		        else{
				
				String emailDomain [] = emailAddress.split("@");
				boolean hasDomain = false;
				for(String d : domain.split(",")){
					if(d.equals(emailDomain[1])){
						hasDomain = true;
					}
				}
			if (hasDomain == false) {
				try {
					req.setAttribute("feedback", ConstantUtils.ERROR);
					req.setAttribute("message", "Email address must contain "+emailDomain[1]+".");
	        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	        		return;
				} catch (ServletException e) {
					log.error("AddEmployee * doPost - error 1: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				/* check for numerical characters */
//				for (int i=0; i<emailAddress.length(); i++) {
//					if (Character.isDigit(emailAddress.charAt(i))) {
//						try {
//							
//							req.setAttribute("feedback", ConstantUtils.ERROR);
//							req.setAttribute("message", "Email address must contain @chunwo.com. Digital characters are not allowed.");
//			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
//			        		return;
//						} catch (ServletException e) {
//							log.error("AddEmployee * doPost - error 2: " + e.getMessage());
//							e.printStackTrace();
//						}
//					}
//				}
				//checking the real domain in google apps
//				try {
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
//				} catch (Exception e) {
//					log.error("AddEmployee validate email error: " + e.getMessage());
//				}
			}
			
			
				/* check for numerical characters in name */
//				for (int i=0; i<fullName.length(); i++) {
//					if (Character.isDigit(fullName.charAt(i))) {
//						try {							
//							req.setAttribute("feedback", ConstantUtils.ERROR);
//							req.setAttribute("message", "Digital characters and characters that are not alphabets are not allowed.");
//			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
//			        		return;
//						} catch (ServletException e) {
//							log.error("AddEmployee * doPost - error 7: " + e.getMessage());
//							e.printStackTrace();
//						}
//					}
//				}
				
				EmployeeService es = new EmployeeService(); 
				try {
					/* check if exist in the database */
					Employee emp = es.findEmployeeByColumnName("emailAddress", emailAddress);
					
					if (emp.getEmailAddress()!=null && emp.getEmailAddress().equalsIgnoreCase(emailAddress)) {
						try {
							req.setAttribute("feedback", ConstantUtils.ERROR);
							req.setAttribute("message", "This employee "+emailAddress+" already exist in the database.");
			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			        		return;
						} catch (ServletException e) {
							log.error("AddEmployee * doPost - error 6: " + e.getMessage());
							e.printStackTrace();
						}
					} else {
						emp = new Employee(staffId, emailAddress, fullName,
								jobTitle, department, hiredDate,
								birthDate, supervisor, UserType.EMPLOYEE.userTypeName);
						es.addEmployee(emp);
						req.setAttribute("feedback", ConstantUtils.OK);
						req.setAttribute("message", "Save success");
						getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
		        		return;
							
					}
				} catch (Exception e) {
					log.error("AddEmployee check employee exist error: " + e.getMessage());
					resp.sendRedirect("/admin-view-emp.jsp");
					return;
				}
		        }
			}else{
				req.setAttribute("feedback", ConstantUtils.ERROR);
				req.setAttribute("message", "Mandatory field");
	    		try{
	    			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	    			return;
	    		} catch (Exception e) {
	    			log.error("AddEmployee validate email error: " + e.getMessage());
	    			e.printStackTrace();
	    		}
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String domain = "";
		String emailAddress = "";
		
		if(request.getParameter("emailAddress")!=null){
			
			emailAddress = request.getParameter("emailAddress");
			
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
		    			log.error("AddEmployee validate email error: " + e1.getMessage());
		    			e1.printStackTrace();
		    		}
		        }
		        else{
		        	//get the company domain from properties 
		        	domain = MyProperties.getValue("app.domain");
		        	
	//	        	SettingService ss = new SettingService();
	//	    		for (Setting set : ss.getSetting()) {
	//	    				domain = set.getAppDomain();
	//	    			
	//	    		}
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
		    			
		    				/* check if exist in the database */
		    				boolean exist = false;
		    				
		    				EmployeeService es = new EmployeeService();
		    				Employee emp = es.findEmployeeByColumnName("emailAddress", emailAddress);
		    				if(emp.getEmailAddress()!=null && emp.getEmailAddress().equalsIgnoreCase(emailAddress)) {
		    					exist = true;
		    				}
		    				
		    				if (exist == true) {
		    					try {
		    						request.setAttribute("feedback", ConstantUtils.ERROR);
		    		        		request.setAttribute("message", "This employee "+emailAddress+" already exist in the database.");
		    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
		    		        		return;
		    					} catch (ServletException e) {
		    						log.error("AddEmployee * doGet - error2: " + e.getMessage());
		    						e.printStackTrace();
		    					}
		    				}
		    		} catch (Exception e1) {
		    			log.error("AddEmployee validate email error: " + e1.getMessage());
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
	    			log.error("AddEmployee validate email error: " + e.getMessage());
	    			e.printStackTrace();
	    		}
	    		
	    		
			}
		}
		response.sendRedirect("/admin-add-emp.jsp");
	}
	
}
