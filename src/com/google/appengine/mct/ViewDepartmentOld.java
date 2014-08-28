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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.datastore.DepartmentService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.Department;
import com.google.appengine.entities.Employee;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewDepartmentOld extends BaseServlet {

	private static final Logger log = Logger.getLogger(ViewDepartmentOld.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ViewDepartmentOld.class);
		//get department approver details
		if(request.getParameter("deptKey")!=null){
			String deptKey = (String)request.getParameter("deptKey");
			Entity dept = DataStoreUtil.findEntityByKey(KeyFactory.stringToKey(deptKey));
			EmployeeService ems = new EmployeeService();
			if("approver_email" !=null) {
				Employee approver = ems.findEmployeeByColumnName("emailAddress", (String)dept.getProperty("approver_email"));
				try {
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.addProperty("approverName",approver.getFullName());
					response.setContentType("application/Json");
					response.getWriter().print(jsonResponse.toString());
					
				} catch (JsonIOException e) {
					e.printStackTrace();
					response.setContentType("text/html");
					response.getWriter().print(e.getMessage());
				}
			}
			
		}
		
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<Department> depts = DepartmentService.getInstance().getDepartments();
		if(!depts.isEmpty()){		
		
//		for(Regions result : rs.getRegions()){
//			if((StringUtils.lowerCase(result.getRegion()).contains(dataTableModel.sSearch.toLowerCase())) ||
//			   (StringUtils.lowerCase(result.getRegionAbbreviation()).contains(dataTableModel.sSearch.toLowerCase())) ||
//			   (StringUtils.lowerCase(result.getRegionSalesOps()).contains(dataTableModel.sSearch.toLowerCase())) ||
//			   (StringUtils.lowerCase(result.getRegionCalendarURL()).contains(dataTableModel.sSearch.toLowerCase())) 
//					){
//				regionList.add(result); // add region that matches given search criterion
//			}
//		}
		
			iTotalDisplayRecords = depts.size(); // number of region that match search criterion should be returned
		}
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(depts, new Comparator<Department>(){
			@Override
			public int compare(Department c1, Department c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getNameEn().compareTo(c2.getNameEn()) * sortDirection;
				case 1:
					return c1.getApproverEmail().compareTo(c2.getApproverEmail()) * sortDirection;
				case 2:
					return c1.getNameTc().compareTo(c2.getNameTc()) * sortDirection;
				case 3:
					return c1.getDelegateEmail().compareTo(c2.getDelegateEmail()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(depts.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			depts = depts.subList(dataTableModel.iDisplayStart, depts.size());
		} else {
			depts = depts.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(Department dept : depts){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive(dept.getNameEn()));
				row.add(new JsonPrimitive(dept.getApproverEmail()));
				row.add(new JsonPrimitive(dept.isExgratiaLeaveDayEnabled()));
				row.add(new JsonPrimitive(dept.getNameTc()));
				if(dept.getDelegateEmail()!=null){
					row.add(new JsonPrimitive(dept.getDelegateEmail()));
				} else {
					row.add(new JsonPrimitive(""));
				}
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

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	
	}
}
