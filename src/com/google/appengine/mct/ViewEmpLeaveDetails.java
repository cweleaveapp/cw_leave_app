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
import java.util.Vector;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
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
public class ViewEmpLeaveDetails extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewEmpLeaveDetails.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(ViewEmpLeaveDetails.class);
		String yearSelected = req.getParameter("year");
		String viewId = req.getParameter("view");
		String isAdmin = ConstantUtils.FALSE;
		AdministratorService addService = new AdministratorService();
		String eAdd = (String)req.getSession().getAttribute("emailAdd");
		Administrator admin = addService.findAdministratorByEmailAddress(eAdd);
		if(StringUtils.isNotBlank(admin.getEmailAddress())){
			 isAdmin = ConstantUtils.TRUE; 
		}else{
			isAdmin = ConstantUtils.FALSE;
		}
		
		req.setAttribute("isAdmin", isAdmin);
		
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		ApprovedLeaveService als = new ApprovedLeaveService();
		for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
			if (eld.getId().equalsIgnoreCase(viewId)) {
					req.setAttribute("fullName", eld.getName());
				}
			
		}
		Integer lastYear = 0;
		SimpleDateFormat standardDFV = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_REV);
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		Integer currentYear = 0;
		Integer nextYear = 0;
		
		String year = yearSelected;
		currentYear = Integer.parseInt(year);
		lastYear = currentYear - 1;
		nextYear = currentYear + 1;
			
			EmployeeLeaveDetails eld = elds.findEmployeeLeaveDetailsByValue(Entity.KEY_RESERVED_PROPERTY,viewId,ConstantUtils.EQUAL);
			req.setAttribute("eld", eld);
			
			List<MCApprovedLeave> appList = als.getApproveLeaveListByEmail(eld.getEmailAddress());
			Collections.sort(appList, MCApprovedLeave.dateComparator);
			
			List<MCApprovedLeave> newAppList = new ArrayList<MCApprovedLeave>();
			if(appList != null && !appList.isEmpty()){
				for(MCApprovedLeave approvedLeave : appList){
					Calendar currMonth = Calendar.getInstance(Locale.getDefault());
					if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(approvedLeave.getLeaveType())){
						try {
							currMonth.setTime(standardDFV.parse(approvedLeave.getStartDate()));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if((currMonth.get(Calendar.MONTH) > 2 && 
								currMonth.get(Calendar.YEAR) == Integer.parseInt(yearSelected))
								|| (currMonth.get(Calendar.MONTH) <= 2 && 
										currMonth.get(Calendar.YEAR) == Integer.parseInt(yearSelected)+1)){
							MCApprovedLeave appLeave = new MCApprovedLeave();
							appLeave.setTimeBean(approvedLeave.getTime());
							appLeave.setEmailAdd(approvedLeave.getEmailAdd());
							appLeave.setNumOfDays(approvedLeave.getNumOfDays());
							appLeave.setStartDateBean(approvedLeave.getStartDate());
							appLeave.setEndDateBean(approvedLeave.getEndDate());
							appLeave.setLeaveType(approvedLeave.getLeaveType());
							appLeave.setSupervisor(approvedLeave.getSupervisor());
							appLeave.setRemark(approvedLeave.getRemark());
							appLeave.setRegion(approvedLeave.getRegion());
							appLeave.setChangeType(approvedLeave.getChangeType());
							appLeave.setAttachmentUrl(approvedLeave.getAttachmentUrl());
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
						if((currMonth.get(Calendar.MONTH) > 2 && 
								currMonth.get(Calendar.YEAR) == Integer.parseInt(yearSelected))
								|| (currMonth.get(Calendar.MONTH) <= 2 && 
										currMonth.get(Calendar.YEAR) == Integer.parseInt(yearSelected)+1)){
							MCApprovedLeave appLeave = new MCApprovedLeave();
							appLeave.setTimeBean(approvedLeave.getTime());
							appLeave.setEmailAdd(approvedLeave.getEmailAdd());
							appLeave.setNumOfDays(approvedLeave.getNumOfDays());
							appLeave.setStartDateBean(approvedLeave.getStartDate());
							appLeave.setEndDateBean(approvedLeave.getEndDate());
							appLeave.setLeaveType(approvedLeave.getLeaveType());
							appLeave.setSupervisor(approvedLeave.getSupervisor());
							appLeave.setRemark(approvedLeave.getRemark());
							appLeave.setRegion(approvedLeave.getRegion());
							appLeave.setChangeType(approvedLeave.getChangeType());
							appLeave.setAttachmentUrl(approvedLeave.getAttachmentUrl());
							appLeave.setProjectName(approvedLeave.getProjectName());
							appLeave.setId(approvedLeave.getId());
							newAppList.add(appLeave);
						}
						
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
			
			req.setAttribute("yearSelected", yearSelected);
			req.setAttribute("lastYear", lastYear.toString());
			req.setAttribute("entitledTotal", entitledTotal.toString());
			req.setAttribute("others", others.toString());
			req.setAttribute("currentYear", currentYear.toString());
			req.setAttribute("nextYear", nextYear.toString());
			
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
				getServletConfig().getServletContext().getRequestDispatcher("/admin-view-emp-leave-details-action.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("ViewEmpLeaveDetails * doPost - error: " + e.getMessage());
				e.printStackTrace();
			}
	
	}
	

}
