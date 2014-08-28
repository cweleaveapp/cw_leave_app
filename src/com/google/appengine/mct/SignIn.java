package com.google.appengine.mct;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.datastore.DepartmentService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.datastore.SupervisorService;
import com.google.appengine.entities.Department;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.entities.Supervisor;
import com.google.appengine.sso.UserInfo;
import com.google.appengine.util.ConstantUtils;
import com.google.gdata.util.ServiceException;


@SuppressWarnings("serial")
public class SignIn extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(SignIn.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String emailAdd = req.getParameter("emailAdd");
		String password = req.getParameter("password");
		boolean validCredentials = false;
		boolean isAdmin = false;
		boolean isSuper = false;
		boolean isApprover = false;
//		String sessionId = req.getSession().getId();
//		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService(null);
		
		UserService userService = UserServiceFactory.getUserService();
		
		UserInfo userInfo = (UserInfo)req.getSession().getAttribute("user");
		AdministratorService as = new AdministratorService();
		SupervisorService ss = new SupervisorService();
		EmployeeService emp = new EmployeeService();

		/* check if exist in the database */
		boolean exist = false;
		String eAdd = "test1@chunwo.com";
		for (Administrator adm : as.getAdministrators()) {
			if (adm.getEmailAddress().equalsIgnoreCase(eAdd) 
					) {
				exist = true;
			}
		}
		if (exist == false) {
			as.addAdministrator("test1@chunwo.com");
		}
		
		CompanyPolicyService cs = new CompanyPolicyService();
		CompanyPolicy companyPolicy = new CompanyPolicy();
		companyPolicy = cs.getCompanyPolicy();
		if(StringUtils.isNotBlank(companyPolicy.getContent())){
			req.setAttribute("content", companyPolicy.getContent());
		}
		
		
		try{
		// uncomment for local developement 
		// 201408 modified for CW leave app
		
		if(StringUtils.isNotBlank(emailAdd)){
			req.getSession().setAttribute("emailAdd", emailAdd);
		//if(StringUtils.isNotBlank(emailAdd) && StringUtils.isNotBlank(password)){
			//MCEmployee employee = emp.findMCEmployeeByColumnName("emailAddress", emailAdd);
			Employee employee = EmployeeService.getInstance().getFullEmployeeDetails(emailAdd);				
			if(employee.getEmailAddress()!=null && employee.getEmployeeLeaveDetails()!=null){
				req.getSession().setAttribute("Employee", employee);				
			}
			
			AuditLog aLog = new AuditLog();
			aLog.setTime(Misc.setCalendarByLocale());
			aLog.setEmailAddress(emailAdd);
			aLog.setName(employee.getFullName());
			AuditActivitiesService auditActivitiesService = new AuditActivitiesService();
			auditActivitiesService.saveLog(aLog);
			
			
			
			for(Department dept : DepartmentService.getInstance().getDepartments()){
				if(dept.getApproverEmail().equals(emailAdd)){
					isApprover = true;
				}
				if(dept.getDelegateEmail()!=null && dept.getDelegateEmail().equals(emailAdd)){
					isApprover = true;
				}
			}
			req.getSession().setAttribute("isApprover",isApprover);		
			
			for(Supervisor s : ss.getSupervisors()){
				if(emailAdd.equalsIgnoreCase(s.getEmailAddress())) {
					isSuper = true;
				}
			}			
			req.getSession().setAttribute("isSuper",isSuper);
			
			for (Administrator adm : as.getAdministrators()) {
				if (emailAdd.equalsIgnoreCase(adm.getEmailAddress())) {
					isAdmin = true;					
				}
			}
			req.getSession().setAttribute("isAdmin",isAdmin);
			
			
			if(isAdmin){
				getServletConfig().getServletContext().getRequestDispatcher("/admin-emp-policy.jsp").forward(req, resp);
				return;
			} else if(isApprover){
				resp.sendRedirect("/AdminLeaveApproval?role=approver");
				return;
			} else if(isSuper){
				resp.sendRedirect("/AdminLeaveApproval?role=supervisor");
				return;
			} else {
				resp.sendRedirect("/LeaveDetails");
//				getServletConfig().getServletContext().getRequestDispatcher("/leave/new-request.jsp").forward(req, resp);
				return;
			}
			
		}
		
		/* Single-Sign-On */
		// uncomment for production	
		/*if(userInfo != null){
			emailAdd = userInfo.getEmail();
			log.debug("userInfo emailAdd "+emailAdd);
		Employee employee = emp.findMCEmployeeByColumnName("emailAddress", emailAdd);
		
		AuditLog aLog = new AuditLog();
		aLog.setTime(Misc.setCalendarByLocale());
		aLog.setEmailAddress(emailAdd);
		aLog.setName(employee.getFullName());
		AuditActivitiesService auditActivitiesService = new AuditActivitiesService();
		auditActivitiesService.saveLog(aLog);
			req.getSession().setAttribute("emailAdd", emailAdd);
			for (Administrator adm : as.getAdministrators()) {
				if (emailAdd != null) {
					if (emailAdd.equalsIgnoreCase(adm.getEmailAddress())) {
						isAdmin = true;
						getServletConfig().getServletContext().getRequestDispatcher("/admin-emp-policy.jsp").forward(req, resp);
//						resp.sendRedirect("/admin-emp-policy.jsp");
					}
				}
			}

			if (isAdmin == false) {
				getServletConfig().getServletContext().getRequestDispatcher("/mct-emp-policy.jsp").forward(req, resp);
//				resp.sendRedirect("/mct-emp-policy.jsp");
			}
			
		}else {
			log.debug("error redirect /sign-in-invalid.jsp");
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}*/
		
//		} catch (ServletException e) {
		} catch (Exception e) {
			log.error("SignIn * doPost - error1a: " + e.getMessage());
			e.printStackTrace();
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

}