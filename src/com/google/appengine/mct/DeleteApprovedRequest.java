package com.google.appengine.mct;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class DeleteApprovedRequest extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(DeleteApprovedRequest.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(DeleteApprovedRequest.class);
		int bal = 0;
		int index = 0;
		String regionSelected = req.getParameter("cri_region");
		// extracting data from the checkbox field
		String[] delAppLQlist = req.getParameterValues("delAppLQlist[]");
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_REV);
		
		Map<String, String> errorMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		
			if (delAppLQlist != null) {
				
				try {
					
				for (int j=0; j<delAppLQlist.length; j++) {
					String id = delAppLQlist[j];
					ApprovedLeaveService als = new ApprovedLeaveService();
					MCApprovedLeave approveLeave = als.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY, id, ConstantUtils.EQUAL);
					
					EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
					
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(approveLeave.getLeaveType())){
						Calendar calYear = getDatePart(standardDF.parse(approveLeave.getTime()));
						EmployeeLeaveDetails employeeD = elds.findEmployeeLeaveDetails(approveLeave.getEmailAdd(), 
								String.valueOf(calYear.get(Calendar.YEAR)));
						String numOfDays = approveLeave.getNumOfDays();
						Double noPayLeave = Double.parseDouble(employeeD.getNoPayLeave());
						Double sickLeave = Double.parseDouble(employeeD.getSickLeaveFP());
						Double annualLeave = Double.parseDouble(employeeD.getAnnualLeave());
						Double compensationLeave = Double.parseDouble(employeeD.getEntitledAnnual());
						Double compassionateLeave = Double.parseDouble(employeeD.getCompassionateLeave());
						Double birthdayLeave = Double.parseDouble(employeeD.getBirthdayLeave());
						Double maternityLeave = Double.parseDouble(employeeD.getMaternityLeave());
						Double weddingLeave = Double.parseDouble(employeeD.getMarriageLeave());
						Double others = Double.parseDouble(employeeD.getOthers());
						Double balance = Double.parseDouble(employeeD.getBalance());
						Double entitledCompensation = Double.parseDouble(employeeD.getEntitledAnnual());
						
//						if(compensationLeave < entitledCompensation){
//							try {
//								log.error(""+properties.getProperty("invalid.delete.approve.day"));
//								errorMap.put("invalid.delete.approve.day", properties.getProperty("invalid.delete.approve.day"));
//								req.setAttribute("errorMap", errorMap);
//								req.setAttribute("cri_region", regionSelected);
//								getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
//								return;
//							} catch (ServletException e) {
//								log.error("DeleteApprovedRequest * doPost - error2: " + e.getMessage());
//								e.printStackTrace();
//							}
//						}
						
						entitledCompensation = entitledCompensation-Double.parseDouble(numOfDays);
						
						elds.updateEmployeeLeaveDetails(employeeD.getName(), 
								employeeD.getEmailAddress(), employeeD.getYear(), 
								employeeD.getLastYearBalance(), employeeD.getEntitledAnnual(),
								entitledCompensation.toString(), noPayLeave.toString(), 
								sickLeave.toString(), annualLeave.toString(), 
								compensationLeave.toString(), compassionateLeave.toString(), 
								birthdayLeave.toString(), maternityLeave.toString(), 
								weddingLeave.toString(), others.toString(),
								balance.toString(), employeeD.getRegion());
					}
					else{
						
					Calendar calYear = getDatePart(standardDF.parse(approveLeave.getStartDate()));
					
					// update current year after current year Apr
					if(calYear.get(Calendar.MONTH) > 2){
						EmployeeLeaveDetails employeeD = elds.findEmployeeLeaveDetails(approveLeave.getEmailAdd(), 
								String.valueOf(calYear.get(Calendar.YEAR)));
						String leaveType = approveLeave.getLeaveType();
						String numOfDays = approveLeave.getNumOfDays();
						Double noPayLeave = Double.parseDouble(employeeD.getNoPayLeave());
						Double sickLeave = Double.parseDouble(employeeD.getSickLeaveFP());
						Double annualLeave = Double.parseDouble(employeeD.getAnnualLeave());
						Double compensationLeave = Double.parseDouble(employeeD.getEntitledAnnual());
						Double compassionateLeave = Double.parseDouble(employeeD.getCompassionateLeave());
						Double birthdayLeave = Double.parseDouble(employeeD.getBirthdayLeave());
						Double maternityLeave = Double.parseDouble(employeeD.getMaternityLeave());
						Double weddingLeave = Double.parseDouble(employeeD.getMarriageLeave());
						Double others = Double.parseDouble(employeeD.getOthers());
						Double entitledCompensation = Double.parseDouble(employeeD.getEntitledAnnual());
						Double balance = Double.parseDouble(employeeD.getBalance());
						
						if(ConstantUtils.NO_PAY_LEAVE.equals(leaveType)){
							noPayLeave = noPayLeave -Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.SICK_LEAVE.equals(leaveType)){
							sickLeave = sickLeave -Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.ANNUAL_LEAVE.equals(leaveType)){
							annualLeave = annualLeave -Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.COMPENSATION_LEAVE.equals(leaveType)){
							compensationLeave = compensationLeave -Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.COMPANSSIONATE_LEAVE.equals(leaveType)){
							compassionateLeave = compassionateLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.BIRTHDAY_LEAVE.equals(leaveType)){
							birthdayLeave = birthdayLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.MATERNITY_LEAVE.equals(leaveType)){
							maternityLeave = Double.parseDouble(employeeD.getMaternityLeave()) -Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.WEDDING_LEAVE.equals(leaveType)){
							weddingLeave = maternityLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.OTHERS.equals(leaveType)){
							others = others-Double.parseDouble(numOfDays);
						}
						
						if(ConstantUtils.ANNUAL_LEAVE.equals(leaveType)){
							balance = balance + Double.parseDouble(numOfDays);
						}
						
						
						elds.updateEmployeeLeaveDetails(employeeD.getName(), 
								employeeD.getEmailAddress(), employeeD.getYear(), 
								employeeD.getLastYearBalance(), employeeD.getEntitledAnnual(),
								entitledCompensation.toString(), noPayLeave.toString(), 
								sickLeave.toString(), annualLeave.toString(), 
								compensationLeave.toString(), compassionateLeave.toString(), 
								birthdayLeave.toString(), maternityLeave.toString(), 
								weddingLeave.toString(), others.toString(),
								balance.toString(), employeeD.getRegion());
						
					}
					// update last year before current year Apr
					else{
						EmployeeLeaveDetails employeeD = elds.findEmployeeLeaveDetails(approveLeave.getEmailAdd(), 
								String.valueOf(calYear.get(Calendar.YEAR)-1));
						String leaveType = approveLeave.getLeaveType();
						String numOfDays = approveLeave.getNumOfDays();
						Double noPayLeave = Double.parseDouble(employeeD.getNoPayLeave());
						Double sickLeave = Double.parseDouble(employeeD.getSickLeaveFP());
						Double annualLeave = Double.parseDouble(employeeD.getAnnualLeave());
						Double compensationLeave = Double.parseDouble(employeeD.getEntitledAnnual());
						Double compassionateLeave = Double.parseDouble(employeeD.getCompassionateLeave());
						Double birthdayLeave = Double.parseDouble(employeeD.getBirthdayLeave());
						Double maternityLeave = Double.parseDouble(employeeD.getMaternityLeave());
						Double weddingLeave = Double.parseDouble(employeeD.getMarriageLeave());
						Double others = Double.parseDouble(employeeD.getOthers());
						Double entitledCompensation = Double.parseDouble(employeeD.getEntitledAnnual());
						Double balance = Double.parseDouble(employeeD.getBalance());
						
						if(ConstantUtils.NO_PAY_LEAVE.equals(leaveType)){
							noPayLeave = noPayLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.SICK_LEAVE.equals(leaveType)){
							sickLeave = sickLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.ANNUAL_LEAVE.equals(leaveType)){
							annualLeave = annualLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.COMPENSATION_LEAVE.equals(leaveType)){
							compensationLeave = compensationLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.COMPANSSIONATE_LEAVE.equals(leaveType)){
							compassionateLeave = compassionateLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.BIRTHDAY_LEAVE.equals(leaveType)){
							birthdayLeave = birthdayLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.MATERNITY_LEAVE.equals(leaveType)){
							maternityLeave = maternityLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.WEDDING_LEAVE.equals(leaveType)){
							weddingLeave = weddingLeave-Double.parseDouble(numOfDays);
						}
						else if(ConstantUtils.OTHERS.equals(leaveType)){
							others = others-Double.parseDouble(numOfDays);
						}
						
						if(ConstantUtils.ANNUAL_LEAVE.equals(leaveType)){
							balance = balance + Double.parseDouble(numOfDays);
						}
						
						
						elds.updateEmployeeLeaveDetails(employeeD.getName(), 
								employeeD.getEmailAddress(), String.valueOf(calYear.get(Calendar.YEAR)-1), 
								employeeD.getLastYearBalance(), employeeD.getEntitledAnnual(),
								entitledCompensation.toString(), noPayLeave.toString(), 
								sickLeave.toString(), annualLeave.toString(), 
								compensationLeave.toString(), compassionateLeave.toString(), 
								birthdayLeave.toString(), maternityLeave.toString(), 
								weddingLeave.toString(), others.toString(),
								balance.toString(), employeeD.getRegion());
					}
					
					}
					als.deleteApprovedLeave(id);
					
				}
					req.setAttribute("cri_region", regionSelected);
					getServletConfig().getServletContext().getRequestDispatcher("/admin-delete-approved.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteApprovedRequest * doPost - error1: " + e.getMessage());
					e.printStackTrace();
				} catch (ParseException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (EntityNotFoundException e){
					e.printStackTrace();
				}
			} else {
				
				try {
					req.setAttribute("cri_region", regionSelected);
					getServletConfig().getServletContext().getRequestDispatcher("/admin-delete-approved.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteApprovedRequest * doPost - error2: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeleteApprovedRequest.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String regionSelected = request.getParameter("cri_region");
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<MCApprovedLeave> approveList = new LinkedList<MCApprovedLeave>();
		List<MCApprovedLeave> entityList = new LinkedList<MCApprovedLeave>();
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(MCApprovedLeave.class.getSimpleName());
		
		
		Filter regionFilter = new FilterPredicate("region",
				                      FilterOperator.EQUAL,
				                      StringUtils.defaultString(regionSelected, "Malaysia"));
				
		q.setFilter(regionFilter);
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			MCApprovedLeave approvedLeave = new MCApprovedLeave();
			approvedLeave.setId(KeyFactory.keyToString(result.getKey()));
			approvedLeave.setTime((String)result.getProperty("time"));
			approvedLeave.setEmailAdd((String)result.getProperty("emailAdd"));
			approvedLeave.setNumOfDays((String)result.getProperty("numOfDays"));
			approvedLeave.setStartDate((String)result.getProperty("startDate"));
			approvedLeave.setEndDate((String)result.getProperty("endDate"));
			approvedLeave.setSupervisor((String)result.getProperty("supervisor"));
			approvedLeave.setLeaveType((String)result.getProperty("leaveType"));
			approvedLeave.setRemark((String)result.getProperty("remark"));
			entityList.add(approvedLeave);
			}
		
		for(MCApprovedLeave result : entityList){
			if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getStartDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getEndDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase())) 
					){
				approveList.add(result); // add approve leave that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = approveList.size(); // number of approve leave that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(approveList, new Comparator<MCApprovedLeave>(){
			@Override
			public int compare(MCApprovedLeave c1, MCApprovedLeave c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getTime().compareTo(c2.getTime()) * sortDirection;
				case 1:
					return c1.getEmailAdd().compareTo(c2.getEmailAdd()) * sortDirection;
				case 2:
					return c1.getNumOfDays().compareTo(c2.getNumOfDays()) * sortDirection;
				case 3:
					return c1.getStartDate().compareTo(c2.getStartDate()) * sortDirection;
				case 4:
					return c1.getEndDate().compareTo(c2.getEndDate()) * sortDirection;
				case 5:
					return c1.getSupervisor().compareTo(c2.getSupervisor()) * sortDirection;
				case 6:
					return c1.getLeaveType().compareTo(c2.getLeaveType()) * sortDirection;
				case 7:
					return c1.getRemark().compareTo(c2.getRemark()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(approveList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			approveList = approveList.subList(dataTableModel.iDisplayStart, approveList.size());
		} else {
			approveList = approveList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(MCApprovedLeave approvedLeave : approveList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"delAppLQlist\" value=\"" + approvedLeave.getId() + "\"" + ">"));
				row.add(new JsonPrimitive(approvedLeave.getTime()));
				row.add(new JsonPrimitive(approvedLeave.getEmailAdd()));
				row.add(new JsonPrimitive(approvedLeave.getNumOfDays()));
				row.add(new JsonPrimitive(StringUtils.isBlank(approvedLeave.getStartDate()) ? "-" : approvedLeave.getStartDate()));
				row.add(new JsonPrimitive(StringUtils.isBlank(approvedLeave.getEndDate()) ? "-" : approvedLeave.getEndDate()));
				row.add(new JsonPrimitive(approvedLeave.getSupervisor()));
				row.add(new JsonPrimitive(approvedLeave.getLeaveType()));
				row.add(new JsonPrimitive(approvedLeave.getRemark()));
				data.add(row);
			}
			jsonResponse.add("aaData", data);
			response.setCharacterEncoding("UTF8");
			response.setContentType("application/Json");
			response.getWriter().print(jsonResponse.toString());
			
		} catch (JsonIOException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print(e.getMessage());
		}
	}
	
	public static Calendar getDatePart(Date date){
	    Calendar cal = Calendar.getInstance(Locale.getDefault());       // get calendar instance
	    cal.setTime(date);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
	    cal.set(Calendar.MINUTE, 0);                 // set minute in hour
	    cal.set(Calendar.SECOND, 0);                 // set second in minute
	    cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second
	    return cal;
	    
	}
}
