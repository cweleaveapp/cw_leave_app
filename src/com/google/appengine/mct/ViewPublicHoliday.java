package com.google.appengine.mct;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class ViewPublicHoliday extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewPublicHoliday.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, 
	IOException {
		log.debug(ViewPublicHoliday.class);
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		String employeeEmail = request.getParameter("emailAddress");
		EmployeeService ems = new EmployeeService();
		
		AdministratorService admin = new AdministratorService();
		if(StringUtils.isBlank(employeeEmail)){
			employeeEmail = "";
		}
		Administrator ad = admin.findAdministratorByEmailAddress(employeeEmail);
		Boolean isAdmin = false;
		if(StringUtils.isNotBlank(ad.getEmailAddress())){
			isAdmin = true;
		}
		
		MCEmployee employee = new MCEmployee();
		
		if(StringUtils.isBlank(employeeEmail)){
			 employee = ems.findMCEmployeeByColumnName("emailAddress", emailAddress);
		}
		else{
			 employee = ems.findMCEmployeeByColumnName("emailAddress", employeeEmail);
		}
		
		JSONArray rs = new JSONArray();
		RegionalHolidaysService hs = new RegionalHolidaysService();
		List<RegionalHolidays> result = hs.getRegionalHolidays();
		List<Date> dateList = new ArrayList<Date>();
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		SimpleDateFormat calendarDF = new SimpleDateFormat("MM/dd/yyyy");
		try{
		
		for(RegionalHolidays rh : result){
			JSONObject obj = new JSONObject();
			
			if(rh.getRegion().equals(employee.getRegion())){
				
				try {
					obj.put("holiday", calendarDF.format(sdf.parse(rh.getDate())));
					obj.put("desc", rh.getDescription());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			rs.add(obj);
		}
		
		response.setContentType("application/Json");
		response.getWriter().print(rs.toString());
			
     } catch (Exception e) {
     	e.printStackTrace();
     	response.setContentType("text/html");
     	response.getWriter().print(e.getMessage());
        throw new ServletException("ViewPublicHoliday ", e);
     }
		
		
	}

}
