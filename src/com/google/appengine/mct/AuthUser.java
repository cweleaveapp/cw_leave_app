package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class AuthUser extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AuthUser.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AuthUser.class);
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(AuthUser.class);
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		JSONObject rs = new JSONObject();
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
		log.debug("AuthUser emailAddress "+emailAddress);
		try {
			
		if(StringUtils.isBlank(emailAddress)){
			rs.put("success", ConstantUtils.FALSE);
		}
		else{
			if(StringUtils.isBlank(ad.getEmailAddress())){
				EmployeeService  ems = new EmployeeService();
				MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress", emailAddress);
				if(StringUtils.isBlank(employee.getEmailAddress())){
						rs.put("success", ConstantUtils.FALSE);
				}
				else{
					rs.put("success", ConstantUtils.TRUE);
				}
				
			}
			else{
				rs.put("success", ConstantUtils.TRUE);
			}
		}
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(rs.toString());
		log.debug("login status "+rs.toString());
		} catch (Exception e) {
			rs.put("success", ConstantUtils.FALSE);
			rs.put("msg", "Authentication with Google failed.");
			e.printStackTrace();
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(e.getMessage());
		} 
	}

}
