package com.google.appengine.mct;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewEmployee extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewEmployee.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ViewEmployee.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String regionSelected = request.getParameter("cri_region");
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<MCEmployee> employeeList = new LinkedList<MCEmployee>();
		List<MCEmployee> entityList = new LinkedList<MCEmployee>();
		
//		if(StringUtils.isBlank(dataTableModel.sSearch)){
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(MCEmployee.class.getSimpleName());
		
		
		Filter regionFilter = new FilterPredicate("region",
				                      FilterOperator.EQUAL,
				                      StringUtils.defaultString(regionSelected, "Malaysia"));
				
		q.setFilter(regionFilter);
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			MCEmployee employee = new MCEmployee();
			employee.setEmailAddress((String)result.getProperty("emailAddress"));
			employee.setFullName((String)result.getProperty("fullName"));
			String hiredDate = (String)result.getProperty("hiredDate");
			String birthDate = (String)result.getProperty("birthDate");
			String resignedDate = (String)result.getProperty("resignedDate");
			employee.setHiredDate(hiredDate.replace("/", "-"));
			employee.setBirthDate(birthDate.replace("/", "-"));
			employee.setResignedDate(StringUtils.isNotEmpty(resignedDate.replace("/", "-")) ? resignedDate.replace("/", "-") : "-");
			entityList.add(employee);
			}
//		}
		
		for(MCEmployee result : entityList){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getFullName()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getHiredDate()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getBirthDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getResignedDate()).contains(dataTableModel.sSearch.toLowerCase()))){
				employeeList.add(result); // add employee that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = employeeList.size(); // number of employee that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(employeeList, new Comparator<MCEmployee>(){
			@Override
			public int compare(MCEmployee c1, MCEmployee c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 1:
					return c1.getFullName().compareTo(c2.getFullName()) * sortDirection;
				case 2:
					return c1.getHiredDate().compareTo(c2.getHiredDate()) * sortDirection;
				case 3:
					return c1.getBirthDate().compareTo(c2.getBirthDate()) * sortDirection;
				case 4:
					return c1.getResignedDate().compareTo(c2.getResignedDate()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(employeeList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			employeeList = employeeList.subList(dataTableModel.iDisplayStart, employeeList.size());
		} else {
			employeeList = employeeList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(MCEmployee employee : employeeList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive(employee.getEmailAddress()));
				row.add(new JsonPrimitive(employee.getFullName()));
				row.add(new JsonPrimitive(employee.getHiredDate()));
				row.add(new JsonPrimitive(employee.getBirthDate()));
				row.add(new JsonPrimitive(employee.getResignedDate()));
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
