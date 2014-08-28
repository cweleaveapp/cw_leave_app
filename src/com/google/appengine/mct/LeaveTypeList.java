package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.datastore.LeaveMgtService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.LeaveTypes;
import com.google.appengine.entities.LeaveTypes;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LeaveTypeList extends BaseServlet {
	
	public List<LeaveTypes> lists;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if(req.getParameter("add")!= null){
			getServletConfig().getServletContext().getRequestDispatcher("/admin/ltdetails.jsp").forward(req, resp);
			return;
		}
			
		
		if(req.getParameter("action")!=null){
			String id = req.getParameter("tId");
			String action = req.getParameter("action");
			if(action.equals("view")){
				for(LeaveTypes t: lists){
					if(t.getId().equals(id)){
						req.setAttribute("LeaveType", t);
						getServletConfig().getServletContext().getRequestDispatcher("/admin/ltdetails.jsp").forward(req, resp);
						return;
					}
				}
			}
		}
		DataTableModel dataTableModel = DataTablesUtility.getParam(req);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		//List<LeaveType> lists = LeaveMgtService.getInstance().listLeaveTypes();
		if(lists.size()==0){
			lists = LeaveMgtService.getInstance().listLeaveTypes();
		}
		
		iTotalDisplayRecords = lists.size(); // number of region that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(lists, new Comparator<LeaveTypes>(){
			@Override
			public int compare(LeaveTypes c1, LeaveTypes c2) {	
				switch(sortColumnIndex){
				case 1:
					return c1.getNameEn().compareTo(c2.getNameEn()) * sortDirection;
				case 2:
					return c1.getAbbreviation().compareTo(c2.getAbbreviation()) * sortDirection;
				}
				return 1;
			}
		});
		
		if(lists.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			lists = lists.subList(dataTableModel.iDisplayStart, lists.size());
		} else {
			lists = lists.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(LeaveTypes t : lists){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<a href=\"ViewLeaveType?tId="+
						t.getId() + "&action=view\"><i class=\"icon-edit\"></i></a>"));
				row.add(new JsonPrimitive(t.getNameEn()));
				row.add(new JsonPrimitive(t.getNameTc()));
				row.add(new JsonPrimitive(t.getAbbreviation()));
				data.add(row);
			}
			jsonResponse.add("aaData", data);
			
			resp.setContentType("application/Json; charset=utf-8");
			resp.getWriter().print(jsonResponse.toString());
		} catch (JsonIOException e) {
			e.printStackTrace();
			resp.setContentType("text/html");
			resp.getWriter().print(e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("tId");
		String action = req.getParameter("action");
		if(action.equals("view")){
			for(LeaveTypes t: lists){
				if(t.getId().equals(id)){
					req.setAttribute("LeaveType", t);
					getServletConfig().getServletContext().getRequestDispatcher("/admin/ltdetails.jsp").forward(req, resp);
					return;
				}
			}
		}
		if(action.endsWith("edit")){
			String nameEn = req.getParameter("nameEn");
			String nameTc = req.getParameter("nameTc");
			String abbr = req.getParameter("abbreviation");
			String key = LeaveMgtService.getInstance().editLeaveType(id, nameEn, nameTc, abbr);
			if(key.length()>0){
				req.setAttribute("feedback", ConstantUtils.OK);
				req.setAttribute("message", "Save success");			
			} else {
				req.setAttribute("feedback", ConstantUtils.ERROR);
				req.setAttribute("message", "Cannot save leave type");
			}
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			return;
		}
		
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		lists = LeaveMgtService.getInstance().listLeaveTypes();
	}

}
