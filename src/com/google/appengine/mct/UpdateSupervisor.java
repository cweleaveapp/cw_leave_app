package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class UpdateSupervisor extends BaseServlet {
	private static final Logger log = Logger.getLogger(ViewSupervisor.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateSupervisor.class);
		// extracting data from the radio button field
		String supervisor = req.getParameter("supervisor");
		String regionSelected = req.getParameter("cri_region");
		
		if (supervisor != null) {
			MCSupervisorService ss = new MCSupervisorService();
			
			for(MCSupervisor s : ss.getSupervisors()){
				if (s.getEmailAddress().equalsIgnoreCase(supervisor)) {
					req.setAttribute("emailAddress", s.getEmailAddress());
				}
			}
			
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-update-supervisor-action.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateSupervisor - doPost error: " + e.getMessage());
				e.printStackTrace();
			}
			
		} else {
			try {
				req.setAttribute("cri_region", regionSelected);
				getServletConfig().getServletContext().getRequestDispatcher("/admin-update-supervisor.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateSupervisor * doPost - error2: " + e.getMessage());
				e.printStackTrace();
			}
		}
			
		
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String editId = req.getParameter("edit");
		MCSupervisorService ss = new MCSupervisorService();
			for (MCSupervisor sup : ss.getSupervisors()) {
				if (sup.getEmailAddress().equalsIgnoreCase(editId)) {
						req.setAttribute("emailAddress", sup.getEmailAddress());
						req.setAttribute("region", sup.getRegion());
					}
				
			}
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-update-supervisor-action.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.debug("UpdateSupervisor * doPost - error1: " + e.getMessage());
				e.printStackTrace();
			}
	}
	
	/*protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateSupervisor.class);
		String regionSelected = request.getParameter("cri_region");
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<Supervisor> supervisorList = new LinkedList<Supervisor>();
		List<Supervisor> filterList = new LinkedList<Supervisor>();
		
		SupervisorService ss = new SupervisorService();
		List<Supervisor> resultList = ss.getSupervisors();
		
		for(Supervisor filter : resultList){
			if(filter.getRegion().contains(regionSelected)){
				filterList.add(filter);
			}
		}
		
		for(Supervisor result : filterList){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase()))||
					(StringUtils.lowerCase(result.getRegion()).contains(dataTableModel.sSearch.toLowerCase()))){
				supervisorList.add(result); // add history that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = supervisorList.size(); // number of history that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(supervisorList, new Comparator<Supervisor>(){
			@Override
			public int compare(Supervisor c1, Supervisor c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 1:
					return c1.getRegion().compareTo(c2.getRegion()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(supervisorList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			supervisorList = supervisorList.subList(dataTableModel.iDisplayStart, supervisorList.size());
		} else {
			supervisorList = supervisorList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(Supervisor supervisor : supervisorList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"radio\" name=\"" + "supervisor" + "\"" + " value=\"" + supervisor.getEmailAddress() + "\"" + "onClick=\"javascript:cmd_parm();\"/>"));
				row.add(new JsonPrimitive(supervisor.getEmailAddress()));
				row.add(new JsonPrimitive(supervisor.getRegion()));
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
		
	}*/
	
}
