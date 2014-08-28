package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class AuditActivities extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AuditActivities.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(AuditActivities.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		AuditActivitiesService auditActivitiesService = new AuditActivitiesService();
		List<AuditLog> aLog = new ArrayList<AuditLog>();
		
		for(AuditLog result : auditActivitiesService.getAllAuditLog()){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase())) ||
					   (StringUtils.lowerCase(result.getName()).contains(dataTableModel.sSearch.toLowerCase())) ||
					   (StringUtils.lowerCase(Misc.getCalendarByDateRev(result.getTime())).contains(dataTableModel.sSearch.toLowerCase()))){
				aLog.add(result); // add AuditLog that matches given search criterion
					}
		}
		
		iTotalDisplayRecords = aLog.size(); // number of AuditLog that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(aLog, new Comparator<AuditLog>(){
			@Override
			public int compare(AuditLog c1, AuditLog c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 1:
					return c1.getName().compareTo(c2.getName()) * sortDirection;
				case 2:
					return c1.getTime().compareTo(c2.getTime()) * sortDirection;
				}
				return 0;
			}
		});
		
		if( aLog.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			 aLog =  aLog.subList(dataTableModel.iDisplayStart,  aLog.size());
		} else {
			 aLog =  aLog.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(AuditLog auditLog :  aLog){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive(Misc.getCalendarByDateRev(auditLog.getTime())));
				row.add(new JsonPrimitive(auditLog.getName()));
				row.add(new JsonPrimitive(auditLog.getEmailAddress()));
				
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
