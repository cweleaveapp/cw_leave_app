package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.LeaveEntitlement;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class AddLeaveEntitle extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddLeaveEntitle.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddLeaveEntitle.class);
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		String year = req.getParameter("leaveYear");
		String addBirthdayLeave = req.getParameter("addBirthdayLeave");
		String addCompassionateLeave = req.getParameter("addCompassionateLeave");
		String addExGratia = req.getParameter("addExGratia");
		String addExaminationLeave = req.getParameter("addExaminationLeave");
		String addInjuryLeave = req.getParameter("addInjuryLeave");
		String addJuryLeave = req.getParameter("addJuryLeave");
		String addFPSickLeave = req.getParameter("addFPSickLeave");
		String addPPSickLeave = req.getParameter("addPPSickLeave");
		String addMarriageLeave = req.getParameter("addMarriageLeave");
		String addMaternityLeave = req.getParameter("addMaternityLeave");
		String addPaternityLeave = req.getParameter("addPaternityLeave");
		
		Pattern numPattern = Pattern.compile("^\\d*(\\.(5|0))?$");
		
		if(!NumberUtils.isDigits(year)){
			log.error(""+properties.getProperty("injury.leave.invalid.numerical"));
			errorMap.put("injury.leave.invalid.numerical", properties.getProperty("injury.leave.invalid.numerical"));
		}
		
		if(!numPattern.matcher(addBirthdayLeave).matches()){
			log.error(""+properties.getProperty("birthday.leave.invalid.numerical"));
			errorMap.put("birthday.leave.invalid.numerical", properties.getProperty("birthday.leave.invalid.numerical"));
		}		
		
		if(!numPattern.matcher(addCompassionateLeave).matches()){
			log.error(""+properties.getProperty("compassionate.leave.invalid.numerical"));
			errorMap.put("compassionate.leave.invalid.numerical", properties.getProperty("compassionate.leave.invalid.numerical"));
		}		

		if(!numPattern.matcher(addExGratia).matches()){
			log.error(""+properties.getProperty("exg.leave.invalid.numerical"));
			errorMap.put("exg.leave.invalid.numerical", properties.getProperty("exg.leave.invalid.numerical"));
		}
		
		if(!numPattern.matcher(addExaminationLeave).matches()){
			log.error(""+properties.getProperty("exam.leave.invalid.numerical"));
			errorMap.put("exam.leave.invalid.numerical", properties.getProperty("exam.leave.invalid.numerical"));
		}
		
		if(!numPattern.matcher(addInjuryLeave).matches()){
			log.error(""+properties.getProperty("injury.leave.invalid.numerical"));
			errorMap.put("injury.leave.invalid.numerical", properties.getProperty("injury.leave.invalid.numerical"));
		}
		if(!numPattern.matcher(addJuryLeave).matches()){
			log.error(""+properties.getProperty("jury.leave.invalid.numerical"));
			errorMap.put("jury.leave.invalid.numerical", properties.getProperty("jury.leave.invalid.numerical"));
		}
		if(!numPattern.matcher(addFPSickLeave).matches()){
			log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
			errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
		}
		if(!numPattern.matcher(addPPSickLeave).matches()){
			log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
			errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
		}
		
		if(!numPattern.matcher(addMarriageLeave).matches()){
			log.error(""+properties.getProperty("wedding.leave.invalid.numerical"));
			errorMap.put("wedding.leave.invalid.numerical", properties.getProperty("wedding.leave.invalid.numerical"));
		}
		
		if(!numPattern.matcher(addMaternityLeave).matches()){
			log.error(""+properties.getProperty("maternity.leave.invalid.numerical"));
			errorMap.put("maternity.leave.invalid.numerical", properties.getProperty("maternity.leave.invalid.numerical"));
		}
		
		if(!numPattern.matcher(addPaternityLeave).matches()){
			log.error(""+properties.getProperty("paternity.leave.invalid.numerical"));
			errorMap.put("paternity.leave.invalid.numerical", properties.getProperty("paternity.leave.invalid.numerical"));
		}
		
		/* to check if exist in the database */
		boolean exist = false;
		LeaveEntitlement l = LeaveEntitleService.getInstance().getLeaveEntitlementByYear(year);
		if(l!=null){
			exist = true;
		}
		
		//if (exist == true) {
		
			
		if(!errorMap.isEmpty()){
        	try {
				req.setAttribute("errorMap", errorMap);
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
        	} catch (Exception e1) {
    			log.error("AddLeaveEntitle validate error: " + e1.getMessage());
    			e1.printStackTrace();
    		}
        } else {
        	String leaveEntitleId = LeaveEntitleService.getInstance().addLeaveEntitlement(year, addBirthdayLeave, 
    				addCompassionateLeave, addExaminationLeave, addInjuryLeave, addJuryLeave, addExGratia,
    				addMarriageLeave, addMaternityLeave, addPaternityLeave,addFPSickLeave, addPPSickLeave);
        	if(leaveEntitleId.length() > 0) {
    			try{
    				log.error(""+properties.getProperty("save.success"));
    				errorMap.put("save.success", properties.getProperty("save.success"));
    				req.setAttribute("errorMap", errorMap);
    				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
    				return;
    			} catch (ServletException e) {
    				log.error("AddLeaveEntitle * doPost - error 2: " + e.getMessage());
    				e.printStackTrace();
    			}
    		}
        }

		
		
	}
	//}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddLeaveEntitle.class);
		//log.debug(UpdateLeaveEntitle.class);
		if(req.getParameter("sEcho")!=null){
			//generate datatable
			DataTableModel dataTableModel = DataTablesUtility.getParam(req);
			String sEcho = dataTableModel.sEcho;
			int iTotalRecords = 0; // total number of records (unfiltered)
			int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
			JsonArray data = new JsonArray(); //data that will be shown in the table
			
			List<LeaveEntitlement> list = LeaveEntitleService.getInstance().getLeaveEntitlements();
//			if(list.size()>0){
				iTotalDisplayRecords += list.size();
				final int sortColumnIndex = dataTableModel.iSortColumnIndex;
				final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
				
				Collections.sort(list, new Comparator<LeaveEntitlement>(){
					@Override
					public int compare(LeaveEntitlement c1, LeaveEntitlement c2) {	
						switch(sortColumnIndex){
					case 3:
						return c1.getLeaveYear().compareTo(c2.getLeaveYear()) * sortDirection;
						}
						return 0;
					}
				});
				
				
				if(list.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
					list = list.subList(dataTableModel.iDisplayStart, list.size());
				} else {
					list = list.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
				}
				
				try {
					JsonObject jsonResponse = new JsonObject();			
					jsonResponse.addProperty("sEcho", sEcho);
					jsonResponse.addProperty("iTotalRecords", iTotalRecords);
					jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
					
					for(LeaveEntitlement let : list){
							JsonArray row = new JsonArray();
							row.add(new JsonPrimitive("<a href=\"AddLeaveEntitle?leaveEntitleRad="+
									let.getId() + "\"><i class=\"icon-edit\"></i></a>"));	
							
							row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"dellist\"  value=\"" +
							let.getId() + "\"" + ">"));
											
							row.add(new JsonPrimitive(let.getLeaveYear()));
							row.add(new JsonPrimitive(let.getAddBirthdayLeave()));
							row.add(new JsonPrimitive(let.getAddCompassionateLeave()));
							row.add(new JsonPrimitive(let.getAddExaminationLeave()));												
							row.add(new JsonPrimitive(let.getAddInjuryLeave()));
							row.add(new JsonPrimitive(let.getAddJuryLeave()));
							row.add(new JsonPrimitive(let.getAddExGratia()));
							row.add(new JsonPrimitive(let.getAddFPSickLeave()));
							row.add(new JsonPrimitive(let.getAddPPSickLeave()));
							row.add(new JsonPrimitive(let.getAddMarriageLeave()));
							row.add(new JsonPrimitive(let.getAddMaternityLeave()));
							row.add(new JsonPrimitive(let.getAddPaternityLeave()));
							data.add(row);
						}
					
					jsonResponse.add("aaData", data);
					
					resp.setContentType("application/Json");
					resp.getWriter().print(jsonResponse.toString());
					
				} catch (JsonIOException e) {
					e.printStackTrace();
					resp.setContentType("text/html");
					resp.getWriter().print(e.getMessage());
				}
		}
		
		if(req.getParameter("leaveEntitleRad")!=null){
			String radioButton = req.getParameter("leaveEntitleRad");
			Entity ee = LeaveEntitleService.getInstance().findEntity(radioButton);
			if(ee!=null){
				req.setAttribute("id", KeyFactory.keyToString(ee.getKey()));
				req.setAttribute("leaveYear", (String)ee.getProperty("leaveYear"));				
				req.setAttribute("addBirthdayLeave", (String)ee.getProperty("addBirthdayLeave"));
				req.setAttribute("addCompassionateLeave", (String)ee.getProperty("addCompassionateLeave"));
				req.setAttribute("addExaminationLeave", (String)ee.getProperty("addExaminationLeave"));
				req.setAttribute("addInjuryLeave", (String)ee.getProperty("addInjuryLeave"));
				req.setAttribute("addJuryLeave", (String)ee.getProperty("addInjuryLeave"));
				req.setAttribute("addFPSickLeave", (String)ee.getProperty("addFPSickLeave"));
				req.setAttribute("addPPSickLeave", (String)ee.getProperty("addPPSickLeave"));
				req.setAttribute("addExGratia", (String)ee.getProperty("addExGratia"));
				req.setAttribute("addMarriageLeave", (String)ee.getProperty("addMarriageLeave"));
				req.setAttribute("addMaternityLeave", (String)ee.getProperty("addMaternityLeave"));
				req.setAttribute("addPaternityLeave", (String)ee.getProperty("addPaternityLeave"));
				try {
					getServletConfig().getServletContext().getRequestDispatcher("/admin-add-leave-entitle.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("UpdateLeaveEntitle * doPost - error1: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
