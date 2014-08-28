package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class UpdateEmpLeaveDetails extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateEmpLeaveDetails.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String editId = req.getParameter("edit");
		Entity e = EmployeeLeaveDetailsService.findEntityByKey(KeyFactory.stringToKey(editId));
		if(e!=null){
			Entity eE = EmployeeLeaveDetailsService.findEntityByKey(e.getParent());
			Employee emp = EmployeeService.getInstance().getFullEmployeeDetails((String)eE.getProperty("emailAddress"));
			EmployeeLeaveDetails eld = emp.getEmployeeLeaveDetails();
			req.setAttribute("emailAddress", emp.getEmailAddress());
			req.setAttribute("year", eld.getYear());
			req.setAttribute("lastYearBal", eld.getLastYearBalance());
			req.setAttribute("entitledAnnual", eld.getEntitledAnnual());
			req.setAttribute("annualLeave", eld.getAnnualLeave());
			req.setAttribute("compassionateLeave", eld.getCompassionateLeave());
			req.setAttribute("sickLeaveFP", eld.getSickLeaveFP());
			req.setAttribute("sickLeavePP", eld.getSickLeavePP());
			req.setAttribute("examLeave", eld.getExamLeave());
			req.setAttribute("marriageLeave", eld.getMarriageLeave());
			req.setAttribute("maternityLeave", eld.getMaternityLeave());
			req.setAttribute("paternityLeave", eld.getPaternityLeave());
			req.setAttribute("injuryLeave", eld.getInjuryLeave());
			req.setAttribute("juryLeave", eld.getJuryLeave());
			req.setAttribute("compensationLeave", eld.getCompensationLeave());
			req.setAttribute("noPayLeave", eld.getNoPayLeave());
			req.setAttribute("dept", emp.getDepartment());
		}
			/*for (EmployeeLeaveDetails eld : elds.getEmployeeLeaveDetails()) {
				if (eld.getId().equalsIgnoreCase(editId)) {
						req.setAttribute("emailAddress", eld.getEmailAddress());
						req.setAttribute("year", eld.getYear());
						req.setAttribute("lastYearBal", eld.getLastYearBalance());
						req.setAttribute("entitledAnnual", eld.getEntitledAnnual());
						req.setAttribute("noPayLeave", eld.getNoPayLeave());
						req.setAttribute("sickLeave", eld.getSickLeaveFP());
						req.setAttribute("annualLeave", eld.getAnnualLeave());
						req.setAttribute("birthdayLeave", eld.getBirthdayLeave());
						req.setAttribute("compassionateLeave", eld.getCompassionateLeave());
						req.setAttribute("maternityLeave", eld.getMaternityLeave());
						req.setAttribute("weddingLeave", eld.getMarriageLeave());
						req.setAttribute("others", eld.getOthers());
						req.setAttribute("cri_region", eld.getRegion());
					}
				
			}*/
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-leave-details.jsp").forward(req, resp);
				return;
			} catch (ServletException ex) {
				log.debug("UpdateEmployee * doPost - error1: " + ex.getMessage());
				ex.printStackTrace();
			}
	}
	
}
