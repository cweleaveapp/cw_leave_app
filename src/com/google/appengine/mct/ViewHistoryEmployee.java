package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewHistoryEmployee extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewHistoryEmployee.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}
	
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		
		List<History> historyList = new LinkedList<History>();
		List<History> entityList = new LinkedList<History>();
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(History.class.getSimpleName());
		
		
		Filter filter = new FilterPredicate("emailAdd",
				                      FilterOperator.EQUAL,
				                      emailAddress);
				
		q.setFilter(filter);
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		// fetch results from datastore based on offset and page size
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
			for(Entity result : results){
				History history = new History();
				history.setTime((String)result.getProperty("time"));
				history.setEmailAdd((String)result.getProperty("emailAdd"));
				history.setNumOfDays((String)result.getProperty("numOfDays"));
				history.setStartDate((String)result.getProperty("startDate"));
				history.setEndDate((String)result.getProperty("endDate"));
				history.setSupervisor((String)result.getProperty("supervisor"));
				history.setLeaveType((String)result.getProperty("leaveType"));
				history.setRemark((String)result.getProperty("remark"));
				entityList.add(history);
			}
		
		
		for(History result : entityList){
			if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getStartDate()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEndDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase()))){
			   historyList.add(result); // add history that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = historyList.size(); // number of history that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(historyList, new Comparator<History>(){
			@Override
			public int compare(History c1, History c2) {	
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
		
		if(historyList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			historyList = historyList.subList(dataTableModel.iDisplayStart, historyList.size());
		} else {
			historyList = historyList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(History history : historyList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive(history.getTime()));
				row.add(new JsonPrimitive(history.getEmailAdd()));
				row.add(new JsonPrimitive(history.getNumOfDays()));
				row.add(new JsonPrimitive(history.getStartDate()));
				row.add(new JsonPrimitive(history.getEndDate()));
				row.add(new JsonPrimitive(history.getSupervisor()));
				row.add(new JsonPrimitive(history.getLeaveType()));
				row.add(new JsonPrimitive(history.getRemark()));
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
