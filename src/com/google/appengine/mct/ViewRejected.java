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
public class ViewRejected extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewRejected.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ViewRejected.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String regionSelected = request.getParameter("cri_region");
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
		Boolean isAdmin = false;
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(RejectedLeave.class.getSimpleName());
		if(StringUtils.isNotBlank(ad.getEmailAddress())){
			Filter regionFilter = new FilterPredicate("region",
                    FilterOperator.EQUAL,
                    StringUtils.defaultString(regionSelected, "Malaysia"));

			q.setFilter(regionFilter);
		}else{
			Filter filter = new FilterPredicate("emailAdd",
                    FilterOperator.EQUAL,
                    emailAddress);

			q.setFilter(filter);
		}
		
		List<RejectedLeave> rejectList = new LinkedList<RejectedLeave>();
		List<RejectedLeave> entityList = new LinkedList<RejectedLeave>();
			
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			RejectedLeave rejectLeave = new RejectedLeave();
			rejectLeave.setTime((String)result.getProperty("time"));
			rejectLeave.setEmailAdd((String)result.getProperty("emailAdd"));
			rejectLeave.setNumOfDays((String)result.getProperty("numOfDays"));
			rejectLeave.setStartDate((String)result.getProperty("startDate"));
			rejectLeave.setEndDate((String)result.getProperty("endDate"));
			rejectLeave.setSupervisor((String)result.getProperty("supervisor"));
			rejectLeave.setLeaveType((String)result.getProperty("leaveType"));
			rejectLeave.setRemark((String)result.getProperty("remark"));
			entityList.add(rejectLeave);
			}
		
		for(RejectedLeave result : entityList){
			if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getStartDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getEndDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase())) 
					){
				rejectList.add(result); // add reject leave that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = rejectList.size(); // number of reject leave that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(rejectList, new Comparator<RejectedLeave>(){
			@Override
			public int compare(RejectedLeave c1, RejectedLeave c2) {	
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
		
		if(rejectList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			rejectList = rejectList.subList(dataTableModel.iDisplayStart, rejectList.size());
		} else {
			rejectList = rejectList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(RejectedLeave rejectedLeave : rejectList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive(rejectedLeave.getTime()));
				row.add(new JsonPrimitive(rejectedLeave.getEmailAdd()));
				row.add(new JsonPrimitive(rejectedLeave.getNumOfDays()));
				row.add(new JsonPrimitive(StringUtils.isBlank(rejectedLeave.getStartDate()) ? "-" : rejectedLeave.getStartDate()));
				row.add(new JsonPrimitive(StringUtils.isBlank(rejectedLeave.getEndDate()) ? "-" : rejectedLeave.getEndDate()));
				row.add(new JsonPrimitive(rejectedLeave.getSupervisor()));
				row.add(new JsonPrimitive(rejectedLeave.getLeaveType()));
				row.add(new JsonPrimitive(rejectedLeave.getRemark()));
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
