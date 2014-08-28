package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class DeleteAdministrator extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(DeleteAdministrator.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(DeleteAdministrator.class);
		Vector delAdVec = new Vector();
		// extracting data from the checkbox field
		String[] delAdlist = req.getParameterValues("delAdlist[]");
		for (int i=0; i<delAdlist.length; i++) {
			log.debug("DeleteAdministrator - record to be deleted: " + delAdlist[i]);
			delAdVec.add(delAdlist[i]);
		}
		
		for (int j=0; j<delAdVec.size(); j++) {
			String name = delAdVec.elementAt(j).toString();
			AdministratorService ads = new AdministratorService();
			for (Administrator ad : ads.getAdministrators()) {
				if (ad.getEmailAddress().equalsIgnoreCase(name)) {
					ads.deleteAdministrator(name);
				}				
			}
		}
//		resp.sendRedirect("/admin-delete-admin.jsp");
		return;
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeleteAdministrator.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<Administrator> adminList = new LinkedList<Administrator>();
		
		AdministratorService as = new AdministratorService();
		
		for(Administrator result : as.getAdministrators()){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase()))){
				adminList.add(result); // add history that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = adminList.size(); // number of history that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(adminList, new Comparator<Administrator>(){
			@Override
			public int compare(Administrator c1, Administrator c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(adminList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			adminList = adminList.subList(dataTableModel.iDisplayStart, adminList.size());
		} else {
			adminList = adminList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(Administrator admin : adminList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"delAdlist\" class=\"dellist\" value=\"" + admin.getEmailAddress() + "\"" + ">"));
				row.add(new JsonPrimitive(admin.getEmailAddress()));
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
