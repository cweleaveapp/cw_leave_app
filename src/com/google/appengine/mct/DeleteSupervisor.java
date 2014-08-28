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

import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class DeleteSupervisor extends BaseServlet {
	private static final Logger log = Logger.getLogger(DeleteSupervisor.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(DeleteSupervisor.class);
		
		Vector delSupVec = new Vector();
		// extracting data from the checkbox field
		String[] delSuplist = req.getParameterValues("delSuplist[]");
		for (int i=0; i<delSuplist.length; i++) {
			System.err.println("DeleteSupervisor - record to be deleted: " + delSuplist[i]);
			delSupVec.add(delSuplist[i]);
		}
		for (int j=0; j<delSupVec.size(); j++) {
			String name = delSupVec.elementAt(j).toString();
			MCSupervisorService ss = new MCSupervisorService();
			for (MCSupervisor sup : ss.getSupervisors()) {
				if (sup.getEmailAddress().equalsIgnoreCase(name)) {
					ss.deleteSupervisor(name);
				}				
			}
		}
		resp.sendRedirect("/admin-delete-supervisor.jsp");
		return;
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeleteSupervisor.class);
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<MCSupervisor> supervisorList = new LinkedList<MCSupervisor>();
		
		MCSupervisorService ss = new MCSupervisorService();
		
		for(MCSupervisor result : ss.getSupervisors()){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase()))||
					(StringUtils.lowerCase(result.getRegion()).contains(dataTableModel.sSearch.toLowerCase()))){
				supervisorList.add(result); // add supervisor that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = supervisorList.size(); // number of supervisor that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(supervisorList, new Comparator<MCSupervisor>(){
			@Override
			public int compare(MCSupervisor c1, MCSupervisor c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 1:
					return c1.getRegion().compareTo(c2.getRegion()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(supervisorList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			supervisorList = supervisorList.subList(dataTableModel.iDisplayStart, supervisorList.size());
		} else {
			supervisorList = supervisorList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(MCSupervisor supervisor : supervisorList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<a href=\"UpdateSupervisor?edit="+ supervisor.getEmailAddress() + "\"><i class=\"icon-edit\"></i></a>"));
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"delSuplist\" value=\"" + supervisor.getEmailAddress() + "\"" + ">"));
				row.add(new JsonPrimitive(supervisor.getEmailAddress()));
				row.add(new JsonPrimitive(supervisor.getRegion()));
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
