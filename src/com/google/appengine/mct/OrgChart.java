package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class OrgChart extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(OrgChart.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(OrgChart.class);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(OrgChart.class);
		String isAdmin = ConstantUtils.FALSE;
		String eAdd = (String)request.getSession().getAttribute("emailAdd");
		AdministratorService addService = new AdministratorService();
		Administrator admin = addService.findAdministratorByEmailAddress(eAdd);
		if(StringUtils.isNotBlank(admin.getEmailAddress())){
			 isAdmin = ConstantUtils.TRUE; 
		}
		request.setAttribute("isAdmin", isAdmin);
		String regionSelected = request.getParameter("cri_region");
		regionSelected = regionSelected == null ? ConstantUtils.HONGKONG : regionSelected;
		
		EmployeeService employeeService = new EmployeeService();
		StringBuilder sb = new StringBuilder();
		for(MCEmployee emp :employeeService.getMCEmployees()){
			if(StringUtils.isNotBlank(emp.getSupervisor()) && StringUtils.isEmpty(emp.getResignedDate())){
				MCEmployee supervisor = employeeService.findMCEmployeeByColumnName("emailAddress", emp.getSupervisor());
				if(regionSelected.equals(emp.getRegion())){
					sb.append("[{v:'").append(emp.getFullName()).append("',f:'")
					.append("<b>"+emp.getFullName()+"</b>")
					.append("<div style=\"color:#FF8C00;\">")
					.append(emp.getJobTitle())
					.append("</div>'},").append("'")
					.append(emp.getSupervisor().equalsIgnoreCase(emp.getEmailAddress()) ? "" : supervisor.getFullName()).append("',")
					.append("'").append(emp.getEmailAddress()).append("'],");
				}
			}
			
			
		}
		request.setAttribute("result", sb.toString());
		
		try {
    		getServletConfig().getServletContext().getRequestDispatcher("/org-chart.jsp").forward(request, response);
    		return;
    	} catch (Exception e1) {
			log.error("OrgChart error: " + e1.getMessage());
			e1.printStackTrace();
		}
	}
}
