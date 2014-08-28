package com.google.appengine.mct;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewMctEmpLeaveDet extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewMctEmpLeaveDet.class);

	public static StringBuffer empDetailsListTable = new StringBuffer();
	public static StringBuffer selectedEmpDetails = new StringBuffer();
	public static StringBuffer transactionDetails = new StringBuffer();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
//		String yearSelected = req.getParameter("cri_year");
		String radioButton = req.getParameter("empDetRad");
		
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		ApprovedLeaveService als = new ApprovedLeaveService();
		Integer lastYear = 0;
		SimpleDateFormat standardDFV = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_REV);
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		Integer currentYear = 0;
		Integer nextYear = 0;
		
			EmployeeLeaveDetails eld = elds.findEmployeeLeaveDetailsByValue(Entity.KEY_RESERVED_PROPERTY,radioButton,ConstantUtils.EQUAL);
			req.setAttribute("eld", eld);
			
			String year = eld.getYear();
			currentYear = Integer.parseInt(year);
			lastYear = currentYear - 1;
			nextYear = currentYear + 1;
			
			List<MCApprovedLeave> appList = als.getApproveLeaveListByEmail(eld.getEmailAddress());
			List<MCApprovedLeave> newAppList = new ArrayList<MCApprovedLeave>();
			for(MCApprovedLeave approvedLeave : appList){
				Calendar currMonth = Calendar.getInstance(Locale.getDefault());
				if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(approvedLeave.getLeaveType())){
					try {
						currMonth.setTime(standardDFV.parse(approvedLeave.getStartDate()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(currMonth.get(Calendar.MONTH) > 2 && currMonth.get(Calendar.YEAR) == Integer.parseInt(year)
							|| (currMonth.get(Calendar.MONTH) <= 2 && 
							currMonth.get(Calendar.YEAR) == Integer.parseInt(year)+1)){
						MCApprovedLeave appLeave = new MCApprovedLeave();
						appLeave.setTimeBean(approvedLeave.getTime());
						appLeave.setEmailAdd(approvedLeave.getEmailAdd());
						appLeave.setNumOfDays(approvedLeave.getNumOfDays());
						appLeave.setStartDateBean(approvedLeave.getStartDate());
						appLeave.setEndDateBean(approvedLeave.getEndDate());
						appLeave.setLeaveType(approvedLeave.getLeaveType());
						appLeave.setSupervisor(approvedLeave.getSupervisor());
						appLeave.setRemark(approvedLeave.getRemark());
						appLeave.setAttachmentUrl(approvedLeave.getAttachmentUrl());
						appLeave.setRegion(approvedLeave.getRegion());
						appLeave.setProjectName(approvedLeave.getProjectName());
						appLeave.setChangeType(approvedLeave.getChangeType());
						appLeave.setId(approvedLeave.getId());
						newAppList.add(appLeave);
					}
				}
				else{
					try {
						currMonth.setTime(standardDFV.parse(approvedLeave.getTime()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(currMonth.get(Calendar.MONTH) > 2 && currMonth.get(Calendar.YEAR) == Integer.parseInt(year)
							|| (currMonth.get(Calendar.MONTH) <= 2 && 
							currMonth.get(Calendar.YEAR) == Integer.parseInt(year)+1)){
						MCApprovedLeave appLeave = new MCApprovedLeave();
						appLeave.setTimeBean(approvedLeave.getTime());
						appLeave.setEmailAdd(approvedLeave.getEmailAdd());
						appLeave.setNumOfDays(approvedLeave.getNumOfDays());
						appLeave.setStartDateBean(approvedLeave.getStartDate());
						appLeave.setEndDateBean(approvedLeave.getEndDate());
						appLeave.setLeaveType(approvedLeave.getLeaveType());
						appLeave.setSupervisor(approvedLeave.getSupervisor());
						appLeave.setRemark(approvedLeave.getRemark());
						appLeave.setAttachmentUrl(approvedLeave.getAttachmentUrl());
						appLeave.setRegion(approvedLeave.getRegion());
						appLeave.setProjectName(approvedLeave.getProjectName());
						appLeave.setChangeType(approvedLeave.getChangeType());
						appLeave.setId(approvedLeave.getId());
						newAppList.add(appLeave);
					}
					
				}
			}
			req.setAttribute("appList", newAppList);
			
			Double entitledTotal = 0.0;
			Double others = 0.0;
			String lastYearBalance = eld.getLastYearBalance() == null ? "0" : eld.getLastYearBalance();
			String entitledAnnual= eld.getEntitledAnnual() == null ? "0" : eld.getEntitledAnnual();
			String entitledCompensation = eld.getEntitledAnnual()  == null ? "0" : eld.getEntitledAnnual();
			entitledTotal = (Double.parseDouble(lastYearBalance)) + 
					(Double.parseDouble(entitledAnnual)) + 
					(Double.parseDouble(entitledCompensation));
			
			String compassionateLeave = eld.getCompassionateLeave() == null ? "0" : eld.getCompassionateLeave();
			String birthdayLeave = eld.getBirthdayLeave() == null ? "0" : eld.getBirthdayLeave();
			String maternityLeave = eld.getMaternityLeave() == null ? "0" : eld.getMaternityLeave();
			String weddingLeave = eld.getMarriageLeave()  == null ? "0" : eld.getMarriageLeave(); 
			
			others = (Double.parseDouble(compassionateLeave)) + 
					(Double.parseDouble(birthdayLeave)) + 
					(Double.parseDouble(maternityLeave)) + 
					(Double.parseDouble(weddingLeave));
			
			req.setAttribute("yearSelected", year);
			req.setAttribute("lastYear", lastYear.toString());
			req.setAttribute("entitledTotal", entitledTotal.toString());
			req.setAttribute("others", others.toString());
			req.setAttribute("currentYear", currentYear.toString());
			req.setAttribute("nextYear", nextYear.toString());
			req.setAttribute("fullName", eld.getName());
			
			EmployeeService els = new EmployeeService();
			MCEmployee employee = els.findMCEmployeeByColumnName("emailAddress", eld.getEmailAddress());
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			Calendar curr = Calendar.getInstance(Locale.getDefault());
       		try {
				cal.setTime(standardDF.parse(employee.getHiredDate()));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
       		int yearCal = curr.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
			LeaveEntitleService les = new LeaveEntitleService();
			int allowSickLeave = 0;
			LeaveEntitle le = les.getLeaveEntitle().get(0);
				List<SickLeave> sickLeaveList = les.getSickLeaveById(le.getId());
				
				for(SickLeave sl : sickLeaveList){
					if(ConstantUtils.LESS_THAN.equals(sl.getSickLeaveType())){
						if(yearCal < Integer.parseInt(sl.getSickLeaveYear())){
							allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
						}
					}
					else if(ConstantUtils.LESS_THAN_OR_EQUAL.equals(sl.getSickLeaveType())){
						SickLeave lessThan = les.getLessThanYear(le.getId());
						if(lessThan != null ){
								if(yearCal <= Integer.parseInt(sl.getSickLeaveYear()) &&
										yearCal >= Integer.parseInt(lessThan.getSickLeaveYear())){
									allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
								}
						}
						
					}
					else if(ConstantUtils.GREATER_THAN.equals(sl.getSickLeaveType())){
						if(yearCal > Integer.parseInt(sl.getSickLeaveYear())){
							allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
						}
					}
					else if(ConstantUtils.GREATER_THAN_OR_EQUAL.equals(sl.getSickLeaveType())){
						if(yearCal >= Integer.parseInt(sl.getSickLeaveYear())){
							allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
						}
					}
				}
			
			
				req.setAttribute("allowSickLeave", String.valueOf(allowSickLeave));
			
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/mct-view-emp-leave-details-action.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("ViewEmpLeaveDetails * doPost - error: " + e.getMessage());
				e.printStackTrace();
			}
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		log.debug(ViewMctEmpLeaveDet.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		
		List<EmployeeLeaveDetails> employeeLeaveDetailsList = new LinkedList<EmployeeLeaveDetails>();
		List<EmployeeLeaveDetails> entityList = new LinkedList<EmployeeLeaveDetails>();
//		if(StringUtils.isBlank(dataTableModel.sSearch)){
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(EmployeeLeaveDetails.class.getSimpleName());
		
		Filter emailFilter = new FilterPredicate("emailAddress",
                FilterOperator.EQUAL,
                emailAddress);
				
		q.setFilter(emailFilter);
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			EmployeeLeaveDetails employeeLeaveDetails = new EmployeeLeaveDetails();
			employeeLeaveDetails.setId(KeyFactory.keyToString(result.getKey()));
			employeeLeaveDetails.setName((String)result.getProperty("name"));
			employeeLeaveDetails.setEmailAddress((String)result.getProperty("emailAddress"));
			employeeLeaveDetails.setYear((String)result.getProperty("year"));
			entityList.add(employeeLeaveDetails);
		}
		
		for(EmployeeLeaveDetails result : entityList){
			if(
			   (StringUtils.lowerCase(result.getYear()).contains(dataTableModel.sSearch.toLowerCase()))){
				employeeLeaveDetailsList.add(result); // add employee that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = employeeLeaveDetailsList.size(); // number of employee that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(employeeLeaveDetailsList, new Comparator<EmployeeLeaveDetails>(){
			@Override
			public int compare(EmployeeLeaveDetails c1, EmployeeLeaveDetails c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getYear().compareTo(c2.getYear()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(employeeLeaveDetailsList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			employeeLeaveDetailsList = employeeLeaveDetailsList.subList(dataTableModel.iDisplayStart, employeeLeaveDetailsList.size());
		} else {
			employeeLeaveDetailsList = employeeLeaveDetailsList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(EmployeeLeaveDetails employeeLeaveDetails : employeeLeaveDetailsList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"radio\" name=\"" + "empDetRad" + "\"" + " value=\"" + employeeLeaveDetails.getId() + "\"" + "onClick=\"javascript:cmd_parm();\"/>"));
				row.add(new JsonPrimitive(employeeLeaveDetails.getYear()));
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
