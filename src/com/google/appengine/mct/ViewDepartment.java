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

import com.google.appengine.datastore.DepartmentService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.Department;
import com.google.appengine.entities.LeaveTypes;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewDepartment extends BaseServlet {
	private static final Logger log = Logger.getLogger(ViewDepartment.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(ViewDepartment.class);
		boolean exgratiaLeaveDayEnabled = false;
		String id = req.getParameter("dId");
		String name_en = req.getParameter("name_en");
		String name_tc = req.getParameter("name_tc");		
		String approver = req.getParameter("approver");
		String delegate = "";
		if(req.getParameter("delegate").length() >0){
			delegate = req.getParameter("delegate");
		}
		
		if(req.getParameter("exgratia_leave_day_enabled")!=null && req.getParameter("exgratia_leave_day_enabled").equals("1")){
			exgratiaLeaveDayEnabled = true;
		}
		String deptKey = "";
		if(id.length()>0){
			deptKey = DepartmentService.getInstance().updateDepartment(id, name_en, name_tc, exgratiaLeaveDayEnabled, approver, delegate);
		} else {
			deptKey = DepartmentService.getInstance().addDepartment(name_en, name_tc, exgratiaLeaveDayEnabled, approver, delegate);
		}
		try{
			req.setAttribute("feedback", ConstantUtils.OK);
			req.setAttribute("message", "Save success.");
			req.setAttribute("deptKey", deptKey);
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			return;
		} catch (ServletException e) {
			log.error("AddDepartment * doPost - error 2: " + e.getMessage());
			e.printStackTrace();
		}
		resp.sendRedirect("/admin-dept.jsp");
		return;
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		List<Department> depts = DepartmentService.getInstance().getDepartments();
		
		if(request.getParameter("action")!=null){
			String id = request.getParameter("dId");
			String action = request.getParameter("action");
			if(action.equals("view")){
				for(Department d: depts){
					if(d.getid().equals(id)){
						request.setAttribute("Department", d);
						getServletConfig().getServletContext().getRequestDispatcher("/admin-add-dept.jsp").forward(request, response);
						return;
					}
				}
			}
		}
		
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		
//		DepartmentService rs = new DepartmentService();
		
//		for(Department result : rs.getDepartment()){
//			if((StringUtils.lowerCase(result.getRegion()).contains(dataTableModel.sSearch.toLowerCase())) ||
//			   (StringUtils.lowerCase(result.getRegionAbbreviation()).contains(dataTableModel.sSearch.toLowerCase())) ||
//			   (StringUtils.lowerCase(result.getRegionSalesOps()).contains(dataTableModel.sSearch.toLowerCase())) ||
//			   (StringUtils.lowerCase(result.getRegionCalendarURL()).contains(dataTableModel.sSearch.toLowerCase())) 
//					){
//				depts.add(result); // add region that matches given search criterion
//			}
//		}
		
		iTotalDisplayRecords = depts.size(); // number of region that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(depts, new Comparator<Department>(){
			@Override
			public int compare(Department c1, Department c2) {	
				switch(sortColumnIndex){
				case 1:
					return c1.getNameEn().compareTo(c2.getNameEn()) * sortDirection;
				case 3:
					return c1.getApproverEmail().compareTo(c2.getApproverEmail()) * sortDirection;
				}
				return 1;
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
				row.add(new JsonPrimitive("<a href=\"ViewDepartment?dId=" +
						dept.getid() + "&action=view\"><i class=\"icon-edit\"></i></a>"));
				row.add(new JsonPrimitive(dept.getNameEn()));
				row.add(new JsonPrimitive(dept.getNameTc()));
				row.add(new JsonPrimitive(dept.getApproverEmail()));
				row.add(new JsonPrimitive(dept.isExgratiaLeaveDayEnabled()));
				if(dept.getDelegateEmail()!=null){
					row.add(new JsonPrimitive(dept.getDelegateEmail()));
				} else {
					row.add(new JsonPrimitive(""));
				}
				data.add(row);
			}
			jsonResponse.add("aaData", data);
			
			response.setContentType("application/Json; charset=utf-8");
			response.getWriter().print(jsonResponse.toString());
			
		} catch (JsonIOException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print(e.getMessage());
		}
	}
}
