package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.SupervisorService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.Supervisor;
import com.google.appengine.enums.UserType;
import com.google.appengine.util.ConstantUtils;
import com.google.appengine.util.MyProperties;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class AddSupervisor extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddSupervisor.class);
	
	private SupervisorService ss = new SupervisorService();
	
	private EmployeeService es = new EmployeeService(); 

	@SuppressWarnings("unused")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddSupervisor.class);
		String domain = "";
		String emailAddress = req.getParameter("emailAddress");
		String deptSelected[] = req.getParameterValues("department[]"); 
		String cmd = req.getParameter("cmd2");
		boolean existDirectory = false;
		String strDept = "";
		
			// Add supervisor and region(s)
			for (int i=0; i<deptSelected.length; i++) {
				if (i == 0) {
					strDept = deptSelected[i].toString();
				} else {
					strDept = strDept + ", " + deptSelected[i].toString();
				}
			}
			
			/* Check with directory listing to see if this address exist */
			try {
				//checking the real domain in google apps
//				String appDomain = domain;
//				appDomain = appDomain.replace("@", "");
//				AppsForYourDomainClient client = new AppsForYourDomainClient(appAdminAcc, appAdminPwd, appDomain);
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
				
    			Employee employee = es.findEmployeeByColumnName("emailAddress", emailAddress);
    			if(StringUtils.isNotBlank(employee.getEmailAddress())){
    				existDirectory = true;
    			}
    			
				if (existDirectory == true) {
					/* check if exist in the database */
					boolean exist = false;
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Query query = new Query(Supervisor.class.getSimpleName());
					for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
						String tmp = (String)entity.getProperty("emailAddress");
						if (tmp.equalsIgnoreCase(emailAddress)) {
							exist = true;
						}
					}
					if (exist == false) {
						if (deptSelected==null || deptSelected.length < 0) {
							req.setAttribute("feedback", ConstantUtils.ERROR);
							req.setAttribute("message", "Please select region.");
			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
							return;
						} else {							
							ss.addSupervisor(emailAddress, strDept.replaceAll("-", " "));
							employee.setUserType(UserType.SUPERVISOR.userTypeName);
							es.updateEmployee(employee);
							req.setAttribute("feedback", ConstantUtils.OK);
							req.setAttribute("message", "Save success.");
							getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			        		return;
						}
					} else if (exist == true) {
						try {
							req.setAttribute("feedback", ConstantUtils.ERROR);
							req.setAttribute("message", "The supervisor "+emailAddress+" already exist in the database.");
			        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
							return;
						} catch (ServletException e) {
							log.error("AddSupervisor * doPost - error 5: " + e.getMessage());
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
						log.error("AddSupervisor * doPost - error 1: " + e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				log.error("AddSupervisor doPost error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(AddSupervisor.class);
		String domain = "";
		String keyString = "";
		String emailAddress = "";
		String appAdminAccount = "";
		String appAdminPassword = "";		
		boolean existDirectory = false;
		if(request.getParameter("action")!=null && request.getParameter("deptkeyStr") != null){
			String deptKeyStr = (String)request.getParameter("deptkeyStr");
			List<Supervisor> supervisorList = ss.getSupervisorByDepartment(deptKeyStr);
			if(!supervisorList.isEmpty()){
				log.debug(supervisorList);		
			}
		}
		if(request.getParameter("emailAddress")==null && request.getParameter("action") == null && request.getParameter("keyString") == null) {
			try{
				if(request.getSession().getAttribute("Supervisor")!=null){
					request.getSession().removeAttribute("Supervisor");
				}
				createDataTable(request,response);
				//response.setContentType("application/Json");
				//getServletConfig().getServletContext().getRequestDispatcher("/admin-delete-supervisor.jsp").forward(request, response);
    			return;
    		} catch (Exception e) {
    			log.error("AddSupervisor init forward error: " + e.getMessage());
    			e.printStackTrace();
    		}
		}
		
		if(request.getParameter("action") != null && request.getParameter("keyString")!= null) {
			String action = request.getParameter("action");
			keyString = request.getParameter("keyString");
			if(action.equals("update")){
				try{
					Supervisor sor = ss.findSupervisorByColumnName("key", keyString);
					if(sor.getEmailAddress()!=null){
						request.getSession().setAttribute("Supervisor", sor);			    			
					}
					getServletConfig().getServletContext().getRequestDispatcher("/admin-add-supervisor.jsp").forward(request, response);
					return;
				} catch (Exception e) {
	    			log.error("AddSupervisor update forward error: " + e.getMessage());
	    			e.printStackTrace();
	    		}
			}
		}
		
		if(request.getParameter("emailAddress") != null) {
			emailAddress = request.getParameter("emailAddress");
		//if(StringUtils.isNotBlank(emailAddress)){
			
			Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
			//check email pattern
	        if (!pattern.matcher(emailAddress).matches()) {
	        	
	        	try {
	        		request.setAttribute("feedback", ConstantUtils.ERROR);
	        		request.setAttribute("message", "Invalid email format");
	        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
	        		return;
	        	} catch (Exception e1) {
	    			log.error("AddSupervisor validate email error: " + e1.getMessage());
	    			e1.printStackTrace();
	    		}
	        }
	        else{
	        	domain = MyProperties.getValue("app.domain");
//	        	SettingService ss = new SettingService();
//	    		for (Setting set : ss.getSetting()) {
//	    				domain = set.getAppDomain();
//	    				appAdminAccount = set.getAppAdminAcc();
//	    				appAdminPassword = set.getAppAdminAccPass();
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
	    			
	    			EmployeeService es = new EmployeeService(); 
	    			Employee employee = es.findEmployeeByColumnName("emailAddress", emailAddress);
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
	    			
	    			if (existDirectory == true) {
	    				/* check if exist in the database */
	    				boolean exist = false;
	    				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    				Query query = new Query(Supervisor.class.getSimpleName());
	    				for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
	    					String tmp = (String)entity.getProperty("emailAddress");
	    					if (tmp.equalsIgnoreCase(emailAddress)) {
	    						exist = true;
	    					}
	    				}
	    				if (exist == true) {
	    					try {
	    						
	    						request.setAttribute("feedback", ConstantUtils.ERROR);
	    		        		request.setAttribute("message", "This supervisor "+emailAddress+" already exist in the database.");
	    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
	    		        		return;
//	    						getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-exist-in-db.jsp").forward(request, response);
//	    						return;
	    					} catch (ServletException e) {
	    						log.error("AddSupervisor * doPost - error2: " + e.getMessage());
	    						e.printStackTrace();
	    					}
	    				}
	    			} else if (existDirectory == false) {
	    				try {
	    					request.setAttribute("emailAddress", emailAddress);
	    					request.setAttribute("feedback", ConstantUtils.ERROR);
    		        		request.setAttribute("message", "This email address "+emailAddress+" does not exist, please check.");
    		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(request, response);
    		        		return;
//	    					getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-not-exist.jsp").forward(request, response);
//	    					return;
	    				} catch (ServletException e) {
	    					log.error("AddSupervisor * doPost - error3: " + e.getMessage());
	    					e.printStackTrace();
	    				}
	    			}
	    		} catch (Exception e1) {
	    			log.error("AddSupervisor validate email error: " + e1.getMessage());
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
	
	private void createDataTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<Supervisor> supervisorList = new ArrayList<Supervisor>();
		
		SupervisorService ss = new SupervisorService();
		supervisorList = ss.getSupervisors();
//		for(Supervisor result : ss.getSupervisors()){
//			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase()))||
//					(StringUtils.lowerCase(result.getDepartment()).contains(dataTableModel.sSearch.toLowerCase()))){
//				supervisorList.add(result); // add supervisor that matches given search criterion
//			}
//		}
		
		iTotalDisplayRecords = supervisorList.size(); // number of supervisor that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(supervisorList, new Comparator<Supervisor>(){
			@Override
			public int compare(Supervisor c1, Supervisor c2) {	
				switch(sortColumnIndex){
				case 2:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 3:
					return c1.getDepartment().compareTo(c2.getDepartment()) * sortDirection;
				}
				return 3;
			}
		});
		
		if(supervisorList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			supervisorList = supervisorList.subList(dataTableModel.iDisplayStart, supervisorList.size());
		} else {
			supervisorList = supervisorList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(Supervisor supervisor : supervisorList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<a href=\"Supervisor?action=update&keyString="+ supervisor.getId() + "\"><i class=\"icon-edit\"></i></a>"));
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"delSuplist\" value=\"" + supervisor.getId() + "\"" + ">"));
				row.add(new JsonPrimitive(supervisor.getEmailAddress()));
				row.add(new JsonPrimitive(supervisor.getDepartment()));
				data.add(row);
			}
			jsonResponse.add("aaData", data);
			
			response.setContentType("application/Json");
			response.getWriter().print(jsonResponse.toString());
			
		} catch (JsonIOException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print(e.getMessage());
		}
	}
	
}
