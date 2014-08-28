package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class AmendLeaveRequest extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AmendLeaveRequest.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AmendLeaveRequest.class);
		String emailAddress = (String)req.getSession().getAttribute("emailAdd");
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
		Boolean isAdmin = false;
		if(StringUtils.isNotBlank(ad.getEmailAddress())){
			isAdmin = true;
		}
		LeaveQueueService lqs = new LeaveQueueService();
		ApprovedLeaveService als = new ApprovedLeaveService();
		// extracting data from the radio button field
		String[] amendAppLQlist = req.getParameterValues("amendAppLQlist");
		
		for (int i=0; i<amendAppLQlist.length; i++) {
			if(als.findEntity(amendAppLQlist[i])!=null){
				Entity entity = als.findEntity(amendAppLQlist[i]);
				MCApprovedLeave appLeave = new MCApprovedLeave();
				appLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
				appLeave.setNumOfDays((String)entity.getProperty("numOfDays"));
				appLeave.setStartDateBean((String)entity.getProperty("startDate"));
				appLeave.setEndDateBean((String)entity.getProperty("endDate"));
				appLeave.setLeaveType((String)entity.getProperty("leaveType"));
				appLeave.setChangeType((String)entity.getProperty("changeType"));
				appLeave.setProjectName((String)entity.getProperty("projectName"));
				appLeave.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
				appLeave.setSupervisor((String)entity.getProperty("supervisor"));
				appLeave.setRemark((String)entity.getProperty("remark"));
				appLeave.setRegion((String)entity.getProperty("region"));
				appLeave.setId(KeyFactory.keyToString(entity.getKey()));
				req.setAttribute("id", appLeave.getId());
				req.setAttribute("emailAddress", appLeave.getEmailAdd());
				req.setAttribute("numOfDays", appLeave.getNumOfDays());
				req.setAttribute("leaveType", appLeave.getLeaveType());
				req.setAttribute("startDate", appLeave.getStartDateBean());
				req.setAttribute("endDate", appLeave.getEndDateBean());
				req.setAttribute("approvalFrom", appLeave.getSupervisor());
				req.setAttribute("remark", appLeave.getRemark());
				req.setAttribute("oldLeaveType", appLeave.getLeaveType());
				req.setAttribute("changeType", appLeave.getChangeType());
				req.setAttribute("projectName", appLeave.getProjectName());
				req.setAttribute("attachmentUrl", appLeave.getAttachmentUrl());
			}
			else{
				if(lqs.findEntity(amendAppLQlist[i])!=null){
					Entity entity = lqs.findEntity(amendAppLQlist[i]);
					LeaveQueue leaveQ = new LeaveQueue();
					leaveQ.setEmailAdd((String)entity.getProperty("emailAdd"));
					leaveQ.setNumOfDays((String)entity.getProperty("numOfDays"));
					leaveQ.setStartDate((String)entity.getProperty("startDate"));
					leaveQ.setEndDate((String)entity.getProperty("endDate"));
					leaveQ.setLeaveType((String)entity.getProperty("leaveType"));
					leaveQ.setChangeType((String)entity.getProperty("changeType"));
					leaveQ.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
					leaveQ.setSupervisor((String)entity.getProperty("supervisor"));
					leaveQ.setRemark((String)entity.getProperty("remark"));
					leaveQ.setProjectName((String)entity.getProperty("projectName"));
					leaveQ.setId(KeyFactory.keyToString(entity.getKey()));
					req.setAttribute("id", leaveQ.getId());
					req.setAttribute("emailAddress", leaveQ.getEmailAdd());
					req.setAttribute("numOfDays", leaveQ.getNumOfDays());
					req.setAttribute("leaveType", leaveQ.getLeaveType());
					req.setAttribute("startDate", leaveQ.getStartDate());
					req.setAttribute("endDate", leaveQ.getEndDate());
					req.setAttribute("approvalFrom", leaveQ.getSupervisor());
					req.setAttribute("projectName", leaveQ.getProjectName());
					req.setAttribute("remark", leaveQ.getRemark());
					req.setAttribute("oldLeaveType", leaveQ.getLeaveType());
					req.setAttribute("changeType", leaveQ.getChangeType());
					req.setAttribute("attachmentUrl", leaveQ.getAttachmentUrl());
				}
			}
		}
		try {
			if (isAdmin) {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-amend-leave-action.jsp").forward(req, resp);
			} else {
				getServletConfig().getServletContext().getRequestDispatcher("/mct-amend-leave-action.jsp").forward(req, resp);
			}
			return;
		} catch (ServletException e) {
			log.error("AmendLeaveRequest * doPost - error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(AmendLeaveRequest.class);
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
		Boolean isAdmin = false;
		if(StringUtils.isNotBlank(ad.getEmailAddress())){
			isAdmin = true;
		}
		
		List<MCApprovedLeave> approvedLeaveList = new LinkedList<MCApprovedLeave>();
		ApprovedLeaveService als = new ApprovedLeaveService();
		
			for(MCApprovedLeave result : als.getApprovedLeave()){
				if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getStartDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getEndDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getChangeType()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase())) 
						){
					if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(result.getLeaveType())){
						if(isAdmin){
							approvedLeaveList.add(result); // add approve leave that matches given search criterion
					}
					else{
						if(result.getEmailAdd().equals(emailAddress)){ // employee view on owner record
							approvedLeaveList.add(result); // add approve leave that matches given search criterion
						}
					}
					}
					
					
				}
			
		}
		
		
		List<LeaveQueue> leaveQueueList = new LinkedList<LeaveQueue>();
		
		LeaveQueueService lqs = new LeaveQueueService();	
		
		
			for (LeaveQueue result : lqs.getLeaveQueue()) {
				if((StringUtils.lowerCase(result.getTime()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getStartDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getEndDate()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) || 
				   (StringUtils.lowerCase(result.getChangeType()).contains(dataTableModel.sSearch.toLowerCase())) ||
				   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase()))){
					
					if(isAdmin){
							leaveQueueList.add(result); // add Leave Queue that matches given search criterion
					}
					else{
						if(result.getEmailAdd().equals(emailAddress)){ // employee view on owner record
							leaveQueueList.add(result); // add approve leave that matches given search criterion
						}
					}
					
				}
			
		}
		
		
		iTotalDisplayRecords += leaveQueueList.size();// number of leave Queue that match search criterion should be returned
		iTotalDisplayRecords += approvedLeaveList.size(); // number of approve leave that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(approvedLeaveList, new Comparator<MCApprovedLeave>(){
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
					return c1.getChangeType().compareTo(c2.getChangeType()) * sortDirection;
				case 8:
					return c1.getRemark().compareTo(c2.getRemark()) * sortDirection;
				}
				return 0;
			}
		});
		
		Collections.sort(leaveQueueList, new Comparator<LeaveQueue>(){
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
					return c1.getStartDate().compareTo(c2.getStartDate()) * sortDirection;
				case 4:
					return c1.getEndDate().compareTo(c2.getEndDate()) * sortDirection;
				case 5:
					return c1.getSupervisor().compareTo(c2.getSupervisor()) * sortDirection;
				case 6:
					return c1.getLeaveType().compareTo(c2.getLeaveType()) * sortDirection;
				case 7:
					return c1.getChangeType().compareTo(c2.getChangeType()) * sortDirection;
				case 8:
					return c1.getRemark().compareTo(c2.getRemark()) * sortDirection;
				}
				return 0;
			}
		});
		
		
		List totalList = new LinkedList();
		
		totalList.addAll(approvedLeaveList);
		totalList.addAll(leaveQueueList);
		
		if(totalList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			totalList = totalList.subList(dataTableModel.iDisplayStart, totalList.size());
		} else {
			totalList = totalList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			String todayDate = Misc.getCalendarByDateRev(new Date());
			for(Object object : totalList){
				MCApprovedLeave approvedLeave = new MCApprovedLeave();
				if(object instanceof MCApprovedLeave){
					approvedLeave = (MCApprovedLeave)object;
						if(todayDate.compareTo(approvedLeave.getStartDate()) >= 0 == false){
							JsonArray row = new JsonArray();
							row.add(new JsonPrimitive("<input type=\"radio\" name=\"amendAppLQlist\" value=\"" + approvedLeave.getId() + "\"" + "onClick=\"javascript:cmd_parm();\"/>"));
							row.add(new JsonPrimitive(approvedLeave.getTime()));
							row.add(new JsonPrimitive(approvedLeave.getEmailAdd()));
							row.add(new JsonPrimitive(approvedLeave.getNumOfDays()));
							row.add(new JsonPrimitive(StringUtils.isNotBlank(approvedLeave.getStartDate()) ? approvedLeave.getStartDate() : "-"));
							row.add(new JsonPrimitive(StringUtils.isNotBlank(approvedLeave.getEndDate()) ? approvedLeave.getEndDate() : "-"));
							row.add(new JsonPrimitive(approvedLeave.getSupervisor()));
							row.add(new JsonPrimitive(approvedLeave.getLeaveType()));
							row.add(new JsonPrimitive(StringUtils.isNotBlank(approvedLeave.getChangeType()) ? approvedLeave.getChangeType() : "-"));
							row.add(new JsonPrimitive(StringUtils.isNotBlank(approvedLeave.getRemark()) ? approvedLeave.getRemark() : "-"));
							data.add(row);	
						}
					
					
				}
				LeaveQueue leaveQueue = new LeaveQueue();
				if(object instanceof LeaveQueue){
					leaveQueue = (LeaveQueue)object;
					JsonArray row = new JsonArray();
					row.add(new JsonPrimitive("<input type=\"radio\" name=\"amendAppLQlist\" value=\"" + leaveQueue.getId() +"\"" + "onClick=\"javascript:cmd_parm();\"/>"));
					row.add(new JsonPrimitive(leaveQueue.getTime()));
					row.add(new JsonPrimitive(leaveQueue.getEmailAdd()));
					row.add(new JsonPrimitive(leaveQueue.getNumOfDays()));
					row.add(new JsonPrimitive(StringUtils.isNotBlank(leaveQueue.getStartDate()) ? leaveQueue.getStartDate() : "-"));
					row.add(new JsonPrimitive(StringUtils.isNotBlank(leaveQueue.getEndDate()) ? leaveQueue.getEndDate() : "-"));
					row.add(new JsonPrimitive(leaveQueue.getSupervisor()));
					row.add(new JsonPrimitive(leaveQueue.getLeaveType()));
					row.add(new JsonPrimitive(StringUtils.isNotBlank(leaveQueue.getChangeType()) ? leaveQueue.getChangeType() : "-"));
					row.add(new JsonPrimitive(StringUtils.isNotBlank(leaveQueue.getRemark()) ? leaveQueue.getRemark() : "-"));
					data.add(row);
				}
				
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
