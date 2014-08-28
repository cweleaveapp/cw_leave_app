package com.google.appengine.mct;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.util.ConstantUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class ViewRequest extends BaseServlet {

	private static final Logger log = Logger.getLogger(ViewRequest.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ViewRequest.class);
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		//get current login session user
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<LeaveQueue> leaveQueueList = new LinkedList<LeaveQueue>();
		List<LeaveQueue> entityList = new LinkedList<LeaveQueue>();
		Boolean isAdmin = false;
		Boolean isSupervisor = false;
		
		AdministratorService as = new AdministratorService();
		for(Administrator admin : as.getAdministrators()){
			if(admin.getEmailAddress().equalsIgnoreCase(emailAddress)){
				isAdmin = true;
			}
		}
		
		MCSupervisorService ss = new MCSupervisorService();
		for(MCSupervisor s : ss.getSupervisors()){
			if(s.getEmailAddress().equalsIgnoreCase(emailAddress)){
				isSupervisor = true;
			}
		}
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(LeaveQueue.class.getSimpleName());
		
		// only admin can view all leave queue
		if(!isAdmin && !isSupervisor){
			Filter filter = new FilterPredicate("emailAdd",
                    FilterOperator.EQUAL,
                    emailAddress);
			q.setFilter(filter);
		}
		else if(!isAdmin && isSupervisor){
			List<String> list = new ArrayList<String>();
			EmployeeService es = new EmployeeService();
			for(MCEmployee emp : es.getMCEmployees()){
				if(emailAddress.equalsIgnoreCase(emp.getSupervisor())){
					list.add(emp.getEmailAddress());
				}
			}
			
			// if some one under this supervisor
			if(list != null && !list.isEmpty()){
				Filter filter = new FilterPredicate("emailAdd",
	                    FilterOperator.IN,
	                    list);
				q.setFilter(filter);
			}
			else{
				// if not one under this supervisor , put update it self to avoid illegal argument
				Filter filter = new FilterPredicate("emailAdd",
	                    FilterOperator.EQUAL,
	                    emailAddress);
				q.setFilter(filter);
			}
			
		}
		
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		
		iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults().limit(1000));
		
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		for(Entity result : results){
			LeaveQueue leaveQueue = new LeaveQueue();
			leaveQueue.setId(KeyFactory.keyToString(result.getKey()));
			leaveQueue.setTime((String)result.getProperty("time"));
			leaveQueue.setEmailAdd((String)result.getProperty("emailAdd"));
			leaveQueue.setNumOfDays((String)result.getProperty("numOfDays"));
			leaveQueue.setStartDate((String)result.getProperty("startDate"));
			leaveQueue.setStartDateBean((String)result.getProperty("startDate"));
			leaveQueue.setEndDate((String)result.getProperty("endDate"));
			leaveQueue.setEndDateBean((String)result.getProperty("endDate"));
			leaveQueue.setLeaveType((String)result.getProperty("leaveType"));
			leaveQueue.setChangeType((String)result.getProperty("changeType"));
			leaveQueue.setRemark((String)result.getProperty("remark"));
			leaveQueue.setSupervisor((String)result.getProperty("supervisor"));
			entityList.add(leaveQueue);
			}
//		}
		
		for(LeaveQueue result : entityList){
			if((StringUtils.lowerCase(result.getEmailAdd()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getNumOfDays()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getStartDateBean()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getEndDateBean()).contains(dataTableModel.sSearch.toLowerCase())) || 
			   (StringUtils.lowerCase(result.getLeaveType()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getChangeType()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getSupervisor()).contains(dataTableModel.sSearch.toLowerCase())) ||
			   (StringUtils.lowerCase(result.getRemark()).contains(dataTableModel.sSearch.toLowerCase()))){
				leaveQueueList.add(result); // add leave queue that matches given search criterion
			}
		}
		
		Collections.sort(leaveQueueList, LeaveQueue.dateComparator);
		
		iTotalDisplayRecords = leaveQueueList.size(); // number of leave queue that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(leaveQueueList, new Comparator<LeaveQueue>(){
			@Override
			public int compare(LeaveQueue c1, LeaveQueue c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAdd().compareTo(c2.getEmailAdd()) * sortDirection;
				case 1:
					return c1.getNumOfDays().compareTo(c2.getNumOfDays()) * sortDirection;
				case 2:
					return c1.getStartDateBean().compareTo(c2.getStartDateBean()) * sortDirection;
				case 3:
					return c1.getEndDateBean().compareTo(c2.getEndDateBean()) * sortDirection;
				case 4:
					return c1.getLeaveType().compareTo(c2.getLeaveType()) * sortDirection;
				case 5:
					return c1.getChangeType().compareTo(c2.getChangeType()) * sortDirection;
				case 6:
					return c1.getRemark().compareTo(c2.getRemark()) * sortDirection;
				case 7:
					return c1.getSupervisor().compareTo(c2.getSupervisor()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(leaveQueueList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			leaveQueueList = leaveQueueList.subList(dataTableModel.iDisplayStart, leaveQueueList.size());
		} else {
			leaveQueueList = leaveQueueList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		EmployeeLeaveDetailsService elds = new EmployeeLeaveDetailsService();
		
		
		try {
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			for(LeaveQueue leaveQueue : leaveQueueList){
				JsonArray row = new JsonArray();
				if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(leaveQueue.getLeaveType())){
					SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
					Calendar currentYear = Calendar.getInstance(Locale.getDefault());
					try {
						currentYear.setTime(standardDF.parse(leaveQueue.getStartDate()));
						
						
					} catch (ParseException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					Integer yr = currentYear.get(Calendar.MONTH) > 2 ?
							currentYear.get(Calendar.YEAR) :
						currentYear.get(Calendar.YEAR)-1;
					EmployeeLeaveDetails employeeLeaveDetails =
							elds.findEmployeeLeaveDetails(leaveQueue.getEmailAdd(), yr.toString());
					
					row.add(new JsonPrimitive("<a href=\"ViewEmpLeaveDetails?view=" +
							employeeLeaveDetails.getId() + "&year="+employeeLeaveDetails.getYear()+"\">" +
									"<i class=\"icon-zoom-in\"></i></a>"));;
				}
				else{
					row.add(new JsonPrimitive(""));
				}
				
				row.add(new JsonPrimitive(leaveQueue.getEmailAdd()));
				row.add(new JsonPrimitive(leaveQueue.getNumOfDays()));
				row.add(new JsonPrimitive(StringUtils.isBlank(leaveQueue.getStartDateBean()) ? "-" : leaveQueue.getStartDateBean()));
				row.add(new JsonPrimitive(StringUtils.isBlank(leaveQueue.getEndDateBean()) ? "-" : leaveQueue.getEndDateBean()));
				row.add(new JsonPrimitive(leaveQueue.getLeaveType()));
				row.add(new JsonPrimitive(StringUtils.isBlank(leaveQueue.getChangeType()) ? "-" : leaveQueue.getChangeType()));
				row.add(new JsonPrimitive(leaveQueue.getRemark()));
				row.add(new JsonPrimitive(leaveQueue.getSupervisor()));
				if(isAdmin || isSupervisor){
					row.add(new JsonPrimitive("<label class=\"radio\"><input type=\"radio\" name=\"statusList"+ leaveQueue.getId() +"\" value=\"approve" + leaveQueue.getId() + "\"" + " />&nbsp;Approve</label>"));
					row.add(new JsonPrimitive("<label class=\"radio\"><input type=\"radio\" name=\"statusList"+ leaveQueue.getId() +"\" value=\"reject" + leaveQueue.getId() + "\"" + " />&nbsp;Reject</label>"));	
				}else{
					row.add(new JsonPrimitive("Pending ..."));
					row.add(new JsonPrimitive("Pending ..."));
				}
				
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
