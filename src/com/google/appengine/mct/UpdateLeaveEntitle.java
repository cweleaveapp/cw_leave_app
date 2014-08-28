package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class UpdateLeaveEntitle extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateLeaveEntitle.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateLeaveEntitle.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<LeaveEntitle> LeaveEntitleList = new LinkedList<LeaveEntitle>();
		LeaveEntitleService als = new LeaveEntitleService();
		
			for(LeaveEntitle result : als.getLeaveEntitle()){
				if((StringUtils.lowerCase(result.getAddAnnualLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||
						   (StringUtils.lowerCase(result.getAddBirthdayLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||
						   (StringUtils.lowerCase(result.getAddMaternityLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||
						   (StringUtils.lowerCase(result.getHospitalization()).contains(dataTableModel.sSearch.toLowerCase())) || 						   
						   (StringUtils.lowerCase(result.getAddWeddingLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||
						   (StringUtils.lowerCase(result.getAddCompassionateLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||				   
						   (StringUtils.lowerCase(result.getAddExGratia()).contains(dataTableModel.sSearch.toLowerCase()))
								){
							LeaveEntitleList.add(result); // add leave entitle that matches given search criterion
										
				}			
		}
		
		iTotalDisplayRecords += LeaveEntitleList.size(); // number of leave that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(LeaveEntitleList, new Comparator<LeaveEntitle>(){
			@Override
			public int compare(LeaveEntitle c1, LeaveEntitle c2) {	
				switch(sortColumnIndex){
				/*case 0:
				return c1.getDepartment().compareTo(c2.getDepartment()) * sortDirection;*/
			case 0:
				return c1.getAddAnnualLeave().compareTo(c2.getAddAnnualLeave()) * sortDirection;
			case 1:
				return c1.getAddBirthdayLeave().compareTo(c2.getAddBirthdayLeave()) * sortDirection;
			case 2:
				return c1.getAddMaternityLeave().compareTo(c2.getAddMaternityLeave()) * sortDirection;				
			case 3:
				return c1.getAddExGratia().compareTo(c2.getAddExGratia())* sortDirection;
			case 4:
				return c1.getAddWeddingLeave().compareTo(c2.getAddWeddingLeave()) * sortDirection;
			case 5:
				return c1.getAddWeddingLeave().compareTo(c2.getAddCompassionateLeave()) * sortDirection;
			case 6:
				return c1.getHospitalization().compareTo(c2.getHospitalization()) * sortDirection;
				}
				return 0;
			}
		});
		
		
		if(LeaveEntitleList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			LeaveEntitleList = LeaveEntitleList.subList(dataTableModel.iDisplayStart, LeaveEntitleList.size());
		} else {
			LeaveEntitleList = LeaveEntitleList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(LeaveEntitle LeaveEntitle : LeaveEntitleList){
					JsonArray row = new JsonArray();
					row.add(new JsonPrimitive("<a href=\"AddLeaveEntitle?leaveEntitleRad="+
							LeaveEntitle.getId() + "\"><i class=\"icon-edit\"></i></a>"));	
					
					row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"dellist\"  value=\"" +
					LeaveEntitle.getId() + "\"" + ">"));
									
					row.add(new JsonPrimitive(LeaveEntitle.getAddAnnualLeave()));
					row.add(new JsonPrimitive(LeaveEntitle.getAddBirthdayLeave()));
					row.add(new JsonPrimitive(LeaveEntitle.getAddExGratia()));
					row.add(new JsonPrimitive(LeaveEntitle.getAddMaternityLeave()));				
					row.add(new JsonPrimitive(LeaveEntitle.getAddWeddingLeave()));					
					row.add(new JsonPrimitive(LeaveEntitle.getAddCompassionateLeave()));
					row.add(new JsonPrimitive(LeaveEntitle.getHospitalization()));					
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
