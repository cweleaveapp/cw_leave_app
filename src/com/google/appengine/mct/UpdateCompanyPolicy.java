package com.google.appengine.mct;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class UpdateCompanyPolicy extends BaseServlet {
	private static final Logger log = Logger.getLogger(UpdateCompanyPolicy.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateCompanyPolicy.class);
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		String content = request.getParameter("content");
		
		CompanyPolicyService cs = new CompanyPolicyService();
		CompanyPolicy companyPolicy = new CompanyPolicy();
		companyPolicy = cs.getCompanyPolicy();
		try {
		if(!companyPolicy.getContent().isEmpty()){
			companyPolicy.setContent(content);
			companyPolicy.setCreatedBy(emailAddress);
			companyPolicy.setTime(new Date());
			companyPolicy = cs.updatePolicy(companyPolicy);
		}
		else{
			companyPolicy.setContent(content);
			companyPolicy.setCreatedBy(emailAddress);
			companyPolicy.setTime(new Date());
			companyPolicy = cs.savePolicy(companyPolicy);
		}
		
			request.setAttribute("content", companyPolicy.getContent());
			getServletConfig().getServletContext().getRequestDispatcher("/policy-content.jsp").forward(request, response);
			return;
    	} catch (Exception e1) {
			log.error("UpdateCompanyPolicy error: " + e1.getMessage());
			e1.printStackTrace();
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateCompanyPolicy.class);
		String eAdd = (String)request.getSession().getAttribute("emailAdd");
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(eAdd);
		
		CompanyPolicyService cs = new CompanyPolicyService();
		CompanyPolicy companyPolicy = new CompanyPolicy();
		companyPolicy = cs.getCompanyPolicy();
		try {
		
			request.setAttribute("content", companyPolicy.getContent());
		
		if(StringUtils.isNotBlank(ad.getEmailAddress())){
			getServletConfig().getServletContext().getRequestDispatcher("/admin-emp-policy.jsp").forward(request, response);
			return;
		}
		else{
			getServletConfig().getServletContext().getRequestDispatcher("/mct-emp-policy.jsp").forward(request, response);
			return;
		}
		
		} catch (Exception e1) {
		log.error("UpdateCompanyPolicy error: " + e1.getMessage());
		e1.printStackTrace();
		}
	}

}
