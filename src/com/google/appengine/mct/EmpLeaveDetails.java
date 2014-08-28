package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DepartmentService;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.Employee;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class EmpLeaveDetails extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(EmpLeaveDetails.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(EmpLeaveDetails.class);
		String regionSelected = req.getParameter("cri_region");
		// extracting data from the checkbox field
		String[] delEmpLeaveDetList = req.getParameterValues("delEmpLeaveDetList[]");

		
			if (delEmpLeaveDetList != null) {
				for (int i=0; i<delEmpLeaveDetList.length; i++) {
					String id = delEmpLeaveDetList[i];
					
					EmployeeLeaveDetailsService.getInstance().deleteEmployeeLeaveDetails(id);
					}
				
				try {
					req.setAttribute("cri_region", regionSelected);
					getServletConfig().getServletContext().getRequestDispatcher("/admin-emp-leave-details.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteEmpLeaveDetails * doPost - error2: " + e.getMessage());
					e.printStackTrace();
				}
				
			}else {
				
				try {
					req.setAttribute("cri_region", regionSelected);
					getServletConfig().getServletContext().getRequestDispatcher("/admin-emp-leave-details.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteEmpLeaveDetails * doPost - error2: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(EmpLeaveDetails.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		if(dataTableModel==null) {
			response.sendRedirect("admin-emp-leave-details.jsp");
			return;
		}
		String deptSelected = request.getParameter("cri_dept");
		String yearSelected = request.getParameter("cri_year");
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<EmployeeLeaveDetails> employeeLeaveDetailsList = new ArrayList<EmployeeLeaveDetails>();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(Employee.class.getSimpleName());
		q.setAncestor(KeyFactory.stringToKey(deptSelected));
		/*Filter regionFilter = new FilterPredicate("department",
				                      FilterOperator.EQUAL,
				                      StringUtils.defaultString(deptSelected, ""));
		
		Filter yearFilter = new FilterPredicate("year",
                FilterOperator.EQUAL,
                yearSelected);
		
		Filter filter = CompositeFilterOperator.and(regionFilter, yearFilter);
		
		q.setFilter(filter);*/
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			EmployeeLeaveDetails edtl = EmployeeLeaveDetailsService.getInstance().findEmployeeLeaveDetails((String)result.getProperty("emailAddress"),yearSelected);
			employeeLeaveDetailsList.add(edtl);
			/*EmployeeLeaveDetails employeeLeaveDetails = new EmployeeLeaveDetails();
			employeeLeaveDetails.setId(KeyFactory.keyToString(result.getKey()));
			employeeLeaveDetails.setName((String)result.getProperty("name"));
			employeeLeaveDetails.setEmailAddress((String)result.getProperty("emailAddress"));
			employeeLeaveDetails.setYear((String)result.getProperty("year"));*/
			//entityList.add(employeeLeaveDetails);
		}
		
		/*for(EmployeeLeaveDetails result : employeeLeaveDetailsList){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getName()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getYear()).contains(dataTableModel.sSearch.toLowerCase()))){
					employeeLeaveDetailsList.add(result); // add employee that matches given search criterion{
			}
		}*/
		
		iTotalDisplayRecords = employeeLeaveDetailsList.size(); // number of employee detail that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(employeeLeaveDetailsList, new Comparator<EmployeeLeaveDetails>(){
			@Override
			public int compare(EmployeeLeaveDetails c1, EmployeeLeaveDetails c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getName().compareTo(c2.getName()) * sortDirection;
				case 1:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 2:
					return c1.getYear().compareTo(c2.getYear()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(employeeLeaveDetailsList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			employeeLeaveDetailsList = employeeLeaveDetailsList.subList(dataTableModel.iDisplayStart, employeeLeaveDetailsList.size());
		} else {
			employeeLeaveDetailsList = employeeLeaveDetailsList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(EmployeeLeaveDetails employeeLeaveDetails : employeeLeaveDetailsList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<a href=\"UpdateEmpLeaveDetails?edit=" +
				employeeLeaveDetails.getId() + "&action=viewOnly\">" +
						"<i class=\"icon-zoom-in\"></i></a>"));
				row.add(new JsonPrimitive("<a href=\"UpdateEmpLeaveDetails?edit="+
						employeeLeaveDetails.getId() + "\"><i class=\"icon-edit\"></i></a>"));
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"" + "delEmpLeaveDetList" +
						"\"" + " value=\"" + employeeLeaveDetails.getId() + "\"" + "/>"));
				row.add(new JsonPrimitive(employeeLeaveDetails.getName()));
				row.add(new JsonPrimitive(employeeLeaveDetails.getEmailAddress()));
				row.add(new JsonPrimitive(employeeLeaveDetails.getYear()));
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
