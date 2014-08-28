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
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewApproved extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ViewApproved.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(ViewApproved.class);
		String appId = req.getParameter("appId");
		
		if(appId != null){
			ApprovedLeaveService als = new ApprovedLeaveService();
			MCApprovedLeave appLeave = als.findApprovedLeaveByValue(Entity.KEY_RESERVED_PROPERTY,appId,ConstantUtils.EQUAL);
			req.setAttribute("appId", appId);
			req.setAttribute("emailAddress", appLeave.getEmailAdd());
			req.setAttribute("numOfDays", appLeave.getNumOfDays());
			req.setAttribute("startDate", appLeave.getStartDate());
			req.setAttribute("endDate", appLeave.getEndDate());
			req.setAttribute("leaveType", appLeave.getLeaveType());
			req.setAttribute("remark", appLeave.getRemark());
			req.setAttribute("region", appLeave.getRegion());
			req.setAttribute("supervisor", appLeave.getSupervisor());
			req.setAttribute("attachmentUrl", appLeave.getAttachmentUrl());
			req.setAttribute("projectName", appLeave.getProjectName());
			
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-update-approved-req.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("ViewApproved * doPost - error1: " + e.getMessage());
				e.printStackTrace();
			}
		}
		else{
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-view-approved.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("ViewApproved * doPost - error2: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ViewApproved.class);
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
		Query q = new Query(MCApprovedLeave.class.getSimpleName());
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
		
		List<MCApprovedLeave> approveList = new LinkedList<MCApprovedLeave>();
		List<MCApprovedLeave> entityList = new LinkedList<MCApprovedLeave>();
			
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			MCApprovedLeave approvedLeave = new MCApprovedLeave();
			approvedLeave.setId(KeyFactory.keyToString(result.getKey()));
			approvedLeave.setTime((String)result.getProperty("time"));
			approvedLeave.setEmailAdd((String)result.getProperty("emailAdd"));
			approvedLeave.setNumOfDays((String)result.getProperty("numOfDays"));
			approvedLeave.setStartDate((String)result.getProperty("startDate"));
			approvedLeave.setEndDate((String)result.getProperty("endDate"));
			approvedLeave.setSupervisor((String)result.getProperty("supervisor"));
			approvedLeave.setLeaveType((String)result.getProperty("leaveType"));
			approvedLeave.setRemark((String)result.getProperty("remark"));
			entityList.add(approvedLeave);
			}
		
		for(MCApprovedLeave result : entityList){
			if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getStartDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getEndDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase())) 
					){
				approveList.add(result); // add approve leave that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = approveList.size(); // number of approve leave that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(approveList, new Comparator<MCApprovedLeave>(){
			@Override
			public int compare(MCApprovedLeave c1, MCApprovedLeave c2) {	
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
		
		if(approveList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			approveList = approveList.subList(dataTableModel.iDisplayStart, approveList.size());
		} else {
			approveList = approveList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(MCApprovedLeave approvedLeave : approveList){
				JsonArray row = new JsonArray();
//				row.add(new JsonPrimitive("<input type=\"radio\" name=\"" + "appId" + "\"" + " value=\"" + approvedLeave.getId() + "\"" + "onclick=\"javascript:cmd_parm();\"/>"));
				row.add(new JsonPrimitive(approvedLeave.getTime()));
				row.add(new JsonPrimitive(approvedLeave.getEmailAdd()));
				row.add(new JsonPrimitive(approvedLeave.getNumOfDays()));
				row.add(new JsonPrimitive(StringUtils.isBlank(approvedLeave.getStartDate()) ? "-" : approvedLeave.getStartDate()));
				row.add(new JsonPrimitive(StringUtils.isBlank(approvedLeave.getEndDate()) ? "-" : approvedLeave.getEndDate()));
				row.add(new JsonPrimitive(approvedLeave.getSupervisor()));
				row.add(new JsonPrimitive(approvedLeave.getLeaveType()));
				row.add(new JsonPrimitive(approvedLeave.getRemark()));
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
