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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class DeleteHoliday extends BaseServlet {

	private static final Logger log = Logger.getLogger(DeleteHoliday.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String regionSelected = req.getParameter("cri_region");
		log.debug("DeleteHoliday region selected = " + regionSelected);
		// extracting data from the checkbox field
		String [] delHolList = req.getParameterValues("delHolList[]");
		
		
			if (delHolList != null && delHolList.length > 0) {
				for (int j=0; j<delHolList.length; j++) {
					String id = delHolList[j];
					RegionalHolidaysService rhs = new RegionalHolidaysService();
					rhs.deleteRegionalHolidays(id);			
				}
				
				try {
					req.setAttribute("cri_region", regionSelected);
					getServletConfig().getServletContext().getRequestDispatcher("/admin-delete-holiday.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteHoliday * doPost - error1: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				
				try {
					req.setAttribute("cri_region", regionSelected);
					getServletConfig().getServletContext().getRequestDispatcher("/admin-delete-holiday.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteHoliday * doPost - error2: " + e.getMessage());
					e.printStackTrace();
				}
			}
		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeleteHoliday.class);
		String regionSelected = request.getParameter("cri_region");
		log.debug("DeleteHoliday region selected = " + regionSelected);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<RegionalHolidays> regionalHolidaysList = new LinkedList<RegionalHolidays>();
		List<RegionalHolidays> entityList = new LinkedList<RegionalHolidays>();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(RegionalHolidays.class.getSimpleName());
		
		Filter regionalHolidaysFilter = new FilterPredicate("region",
				                      FilterOperator.EQUAL,
				                      StringUtils.defaultString(regionSelected, "Malaysia"));
				
		q.setFilter(regionalHolidaysFilter);
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			RegionalHolidays regionalHolidays = new RegionalHolidays();
			regionalHolidays.setId(KeyFactory.keyToString(result.getKey()));
			String date = (String)result.getProperty("date");
			regionalHolidays.setDate(date.replace("/", "-"));
			regionalHolidays.setRegion((String)result.getProperty("region"));
			regionalHolidays.setDescription((String)result.getProperty("description"));
			entityList.add(regionalHolidays);
		}
		
		for(RegionalHolidays result : entityList){
			if((StringUtils.lowerCase(result.getRegion()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getDate()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getDescription()).contains(dataTableModel.sSearch.toLowerCase())) 
					){
				regionalHolidaysList.add(result); // add regionalHolidaysList that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = regionalHolidaysList.size(); // number of regionalHolidaysList that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(regionalHolidaysList, new Comparator<RegionalHolidays>(){
			@Override
			public int compare(RegionalHolidays c1, RegionalHolidays c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getDate().compareTo(c2.getDate()) * sortDirection;
				case 1:
					return c1.getDescription().compareTo(c2.getDescription()) * sortDirection;
				case 2:
					return c1.getRegion().compareTo(c2.getRegion()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(regionalHolidaysList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			regionalHolidaysList = regionalHolidaysList.subList(dataTableModel.iDisplayStart, regionalHolidaysList.size());
		} else {
			regionalHolidaysList = regionalHolidaysList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(RegionalHolidays region : regionalHolidaysList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"delHolList\" value=\"" + region.getId() + "\"" + ">"));
				row.add(new JsonPrimitive(region.getDate()));
				row.add(new JsonPrimitive(region.getDescription()));
				row.add(new JsonPrimitive(region.getRegion()));
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
