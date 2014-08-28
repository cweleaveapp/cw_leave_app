package com.google.appengine.mct;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.LeaveQueue;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class DeletePendingLeaveQueue extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(DeletePendingLeaveQueue.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(DeletePendingLeaveQueue.class);
		String cmd2 = req.getParameter("cmd2");
		// extracting data from the checkbox field
		String[] delLQlist = req.getParameterValues("delLQlist[]");
		
			for (int i=0; i<delLQlist.length; i++) {
				String id = delLQlist[i];
				LeaveQueueService lqs = new LeaveQueueService();
				lqs.deleteLeaveQueue(id);
					
			}
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-delete-emp-leave-queue.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("DeletePendingLeaveQueue * doPost - error2: " + e.getMessage());
				e.printStackTrace();
			}
		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeletePendingLeaveQueue.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<LeaveQueue> leaveQueueList = LeaveQueueService.getInstance().getPendingLeaveQueue();
		
		//LeaveQueueService lqs = new LeaveQueueService();	
		
		/*for (LeaveQueue result : lqs.getLeaveQueue()) {
			if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getStartDateBean()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getEndDateBean()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase()))){
				leaveQueueList.add(result); // add LeaveQueue that matches given search criterion
			}
		}*/
		
		Collections.sort(leaveQueueList, LeaveQueue.dateComparator);
		
		iTotalDisplayRecords = leaveQueueList.size(); // number of LeaveQueue that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		/*Collections.sort(leaveQueueList, new Comparator<LeaveQueue>(){
			@Override
			public int compare(LeaveQueue c1, LeaveQueue c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getTime().compareTo(c2.getTime()) * sortDirection;
				case 1:
					return c1.getEmailAdd().compareTo(c2.getEmailAdd()) * sortDirection;
				case 2:
					return c1.getNumOfDays().compareTo(c2.getNumOfDays()) * sortDirection;
				case 3:
					return c1.getStartDateBean().compareTo(c2.getStartDateBean()) * sortDirection;
				case 4:
					return c1.getEndDateBean().compareTo(c2.getEndDateBean()) * sortDirection;
				case 5:
					return c1.getSupervisor().compareTo(c2.getSupervisor()) * sortDirection;
				case 6:
					return c1.getLeaveType().compareTo(c2.getLeaveType()) * sortDirection;
				case 7:
					return c1.getRemark().compareTo(c2.getRemark()) * sortDirection;
				}
				return 0;
			}
		});*/
		
		if(leaveQueueList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			leaveQueueList = leaveQueueList.subList(dataTableModel.iDisplayStart, leaveQueueList.size());
		} else {
			leaveQueueList = leaveQueueList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			SimpleDateFormat formatter = new SimpleDateFormat();
			for(LeaveQueue leaveQueue : leaveQueueList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"delLQlist\" value=\"" + leaveQueue.getId() + "\"" + ">"));
				row.add(new JsonPrimitive(formatter.format(leaveQueue.getCreateDate())));
				row.add(new JsonPrimitive(leaveQueue.getApprovedBy()));
				//row.add(new JsonPrimitive(leaveQueue.getNumOfDays()));
				//row.add(new JsonPrimitive(StringUtils.isBlank(leaveQueue.getStartDateBean()) ? "-" : leaveQueue.getStartDateBean()));
				//row.add(new JsonPrimitive(StringUtils.isBlank(leaveQueue.getEndDateBean()) ? "-" : leaveQueue.getEndDateBean()));
				//row.add(new JsonPrimitive(leaveQueue.getSupervisor()));
				//row.add(new JsonPrimitive(leaveQueue.getLeaveType()));
				//row.add(new JsonPrimitive(leaveQueue.getRemark()));
				data.add(row);
			}
			jsonResponse.add("aaData", data);
			response.setContentType("application/Json");
			response.setCharacterEncoding("UTF8");
			response.getWriter().print(jsonResponse.toString());
			
		} catch (JsonIOException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print(e.getMessage());
		}
	
	}
	
}
