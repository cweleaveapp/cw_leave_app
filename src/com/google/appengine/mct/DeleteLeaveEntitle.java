package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
public class DeleteLeaveEntitle extends BaseServlet {
	private static final Logger log = Logger.getLogger(DeleteLeaveEntitle.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(DeleteLeaveEntitle.class);
		Vector delLeaveEntitleVec = new Vector();
		// extracting data from the checkbox field
		String[] dellist = req.getParameterValues("dellist[]");
		
		if(dellist != null && dellist.length > 0){
			for (int i=0; i<dellist.length; i++) {
				log.error("DeleteLeaveEntitle - record to be deleted: " + dellist[i]);
				delLeaveEntitleVec.add(dellist[i]);
			}
			
			for (int j=0; j<delLeaveEntitleVec.size(); j++) {
				String name = delLeaveEntitleVec.elementAt(j).toString();
				LeaveEntitleService les = new LeaveEntitleService();
				if(!les.getLeaveEntitle().isEmpty()){
					for (LeaveEntitle le : les.getLeaveEntitle()) {
						if(le.getId().equalsIgnoreCase(name)){
							les.deleteLeaveEntitleByKey(name);
						}
//						if (le.getDepartment().equalsIgnoreCase(name)) {
//							les.deleteLeaveEntitle(name);
//						}				
					}
					List<SickLeave> sls = les.getSickLeaveById(name);
					if(!sls.isEmpty()){
						for(SickLeave sl : sls){
							les.deleteSickLeave(sl.getId());
						}
					}
				}
				
			}
			resp.sendRedirect("/admin-delete-leave-entitle.jsp");
			return;
		}	
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeleteLeaveEntitle.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<LeaveEntitle> leaveEntitleList = new LinkedList<LeaveEntitle>();
		
		LeaveEntitleService les = new LeaveEntitleService();
		
		for(LeaveEntitle result : les.getLeaveEntitle()){
			if((StringUtils.lowerCase(result.getAddAnnualLeave()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getHospitalization()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getAddMaternityLeave()).contains(dataTableModel.sSearch.toLowerCase())) || 				   
				   (StringUtils.lowerCase(result.getAddBirthdayLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getAddWeddingLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getAddCompassionateLeave()).contains(dataTableModel.sSearch.toLowerCase())) ||				   
				   (StringUtils.lowerCase(result.getAddExGratia()).contains(dataTableModel.sSearch.toLowerCase()))
						){
				leaveEntitleList.add(result); // add leave entitle that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = leaveEntitleList.size(); // number of leave entitle that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(leaveEntitleList, new Comparator<LeaveEntitle>(){
			@Override
			public int compare(LeaveEntitle c1, LeaveEntitle c2) {	
				switch(sortColumnIndex){
				/*case 0:
					return c1.getDepartment().compareTo(c2.getDepartment()) * sortDirection;*/
				case 0:
					return c1.getAddAnnualLeave().compareTo(c2.getAddAnnualLeave()) * sortDirection;
				case 1:
					return c1.getHospitalization().compareTo(c2.getHospitalization()) * sortDirection;
				case 2:
					return c1.getAddMaternityLeave().compareTo(c2.getAddMaternityLeave()) * sortDirection;				
				case 3:
					return c1.getAddBirthdayLeave().compareTo(c2.getAddBirthdayLeave()) * sortDirection;
				case 4:
					return c1.getAddWeddingLeave().compareTo(c2.getAddWeddingLeave()) * sortDirection;
				case 5:
					return c1.getAddWeddingLeave().compareTo(c2.getAddCompassionateLeave()) * sortDirection;
				case 6:
					return c1.getAddExGratia().compareTo(c2.getAddExGratia())* sortDirection;
				}
				return 0;
			}
		});
		
		if(leaveEntitleList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			leaveEntitleList = leaveEntitleList.subList(dataTableModel.iDisplayStart, leaveEntitleList.size());
		} else {
			leaveEntitleList = leaveEntitleList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(LeaveEntitle leaveEntitle : leaveEntitleList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"dellist\" value=\"" + leaveEntitle.getId() + "\"" + ">"));
				row.add(new JsonPrimitive(leaveEntitle.getAddAnnualLeave()));
				row.add(new JsonPrimitive(leaveEntitle.getHospitalization()));
				row.add(new JsonPrimitive(leaveEntitle.getAddMaternityLeave()));				
				row.add(new JsonPrimitive(leaveEntitle.getAddBirthdayLeave()));
				row.add(new JsonPrimitive(leaveEntitle.getAddWeddingLeave()));					
				row.add(new JsonPrimitive(leaveEntitle.getAddCompassionateLeave()));
				row.add(new JsonPrimitive(leaveEntitle.getAddExGratia()));
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
