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
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;

@SuppressWarnings("serial")
public class AddAdministrator extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddAdministrator.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddAdministrator.class);
		String emailAddress = req.getParameter("emailAddress");
		String cmd = req.getParameter("cmd");
		boolean existDirectory = false;
		String domain = "";

		if (!emailAddress.contains(domain)) {
			try {
				req.setAttribute("emailAddress", emailAddress);
				req.setAttribute("feedback", ConstantUtils.ERROR);
				req.setAttribute("message", "Email address must contain @[domain name].");
        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
        		return;
			} catch (ServletException e) {
				log.error("AddAdministrator * doPost - error1a: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			/* check for numerical characters */
//			for (int i=0; i<emailAddress.length(); i++) {
//				if (Character.isDigit(emailAddress.charAt(i))) {
//					try {
//						req.setAttribute("emailAddress", emailAddress);
//						req.setAttribute("feedback", ConstantUtils.ERROR);
//						req.setAttribute("message", "Email address must contain @[domain name]. Digital characters are not allowed.");
//		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
//		        		return;
//					} catch (ServletException e) {
//						log.error("AddAdministrator * doPost - error1b: " + e.getMessage());
//						e.printStackTrace();
//					}
//				}
//			}
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
//					if (entry.getTitle().getPlainText().equalsIgnoreCase(tmpAdd)){
//						existDirectory = true;
//						break;
//					}
//				}
				EmployeeService es = new EmployeeService(); 
    			Employee employee = es.findEmployeeByColumnName("emailAddress", emailAddress);
    			if(StringUtils.isNotBlank(employee.getEmailAddress())){
    				existDirectory = true;
    			}

				if (existDirectory == true) {
					/* check if exist in the database */
					boolean exist = false;
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Query query = new Query(Administrator.class.getSimpleName());
					for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
						String tmp = (String)entity.getProperty("emailAddress");
						if (tmp.equalsIgnoreCase(emailAddress)) {
							exist = true;
						}
					}
					if (exist == false) {
						AdministratorService as = new AdministratorService();
						as.addAdministrator(emailAddress);
						employee.setUserType(UserType.ADMINISTRATOR.userTypeName);
						es.updateEmployee(employee);
						req.setAttribute("feedback", ConstantUtils.OK);
						req.setAttribute("message", "Save success");
						getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
		        		return;
					} else if (exist == true) {
						try {
							req.setAttribute("emailAddress", emailAddress);
							req.setAttribute("feedback", ConstantUtils.ERROR);
							req.setAttribute("message", "This administrator already exist in the database.");
			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			        		return;
						} catch (ServletException e) {
							log.error("AddAdministrator * doPost - error2: " + e.getMessage());
							e.printStackTrace();
						}
					}
				} else if (existDirectory == false) {
					try {
						req.setAttribute("emailAddress", emailAddress);
						req.setAttribute("feedback", ConstantUtils.ERROR);
						req.setAttribute("message", "This email address does not exist, please check.");
		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
		        		return;
					} catch (ServletException e) {
						log.error("AddAdministrator * doPost - error3: " + e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				log.error("AddAdministrator validate email error: " + e1.getMessage());
			}
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}
}
