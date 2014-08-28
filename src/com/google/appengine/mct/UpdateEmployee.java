package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.appengine.entities.Employee;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class UpdateEmployee extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateEmployee.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String empKey = (String) req.getParameter("empKey");
		DataStoreUtil util = new DataStoreUtil();
		Entity entity = util.findEntity(empKey);
		if(entity!=null){
			entity.setProperty("staffId", (String)req.getParameter("staffId"));
			entity.setProperty("emailAddress", (String)req.getParameter("emailAddress"));
			entity.setProperty("fullName", (String)req.getParameter("fullName"));
			entity.setProperty("department", (String)req.getParameter("department"));
			entity.setProperty("hiredDate", (String)req.getParameter("hiredDate"));
			entity.setProperty("birthDate", (String)req.getParameter("birthDate"));					
			entity.setProperty("supervisor", (String)req.getParameter("supervisor"));
			entity.setProperty("jobTitle", (String)req.getParameter("jobTitle"));
			util.getDatastore().put(entity);
			req.setAttribute("feedback", "OK");
			req.setAttribute("message", "save success");
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateEmployee * doPost - error2: " + e.getMessage());
				e.printStackTrace();
			}
		}
		/*log.debug(UpdateEmployee.class);
		String regionSelected = req.getParameter("cri_region");
		String radioButton = req.getParameter("empRad");
		EmployeeService es = new EmployeeService();
		
		if (radioButton != null) {
			for (Employee emp : es.getMCEmployees()) {
				if (emp.getEmailAddress().equalsIgnoreCase(radioButton)) {
					req.setAttribute("emailAddress", emp.getEmailAddress());
					req.setAttribute("fullName", emp.getFullName());
					req.setAttribute("region", emp.getRegion());
					req.setAttribute("hiredDate", emp.getHiredDate());
					req.setAttribute("birthDate", emp.getBirthDate());
					req.setAttribute("resignedDate", emp.getResignedDate());
					req.setAttribute("supervisor", emp.getSupervisor());
					req.setAttribute("jobTitle", emp.getJobTitle());
				}
			}
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-update-emp-action.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateEmployee * doPost - error1: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			try {
				req.setAttribute("cri_region", regionSelected);
				getServletConfig().getServletContext().getRequestDispatcher("/admin-update-emp.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateEmployee * doPost - error2: " + e.getMessage());
				e.printStackTrace();
			}
		}*/
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String editId = req.getParameter("edit");
		EmployeeService es = new EmployeeService();
			for (Employee emp : es.getEmployees()) {
				if (emp.getEmailAddress().equalsIgnoreCase(editId)) {
						req.setAttribute("empKey",emp.getEmpKey());
						req.setAttribute("emailAddress", emp.getEmailAddress());
						req.setAttribute("fullName", emp.getFullName());
						req.setAttribute("staffId", emp.getstaffId());
						req.setAttribute("department", emp.getDepartment());
						req.setAttribute("hiredDate", emp.getHiredDate());
						req.setAttribute("birthDate", emp.getBirthDate());						
						req.setAttribute("supervisor", emp.getSupervisor());
						req.setAttribute("jobTitle", emp.getJobTitle());
						req.setAttribute("update", true);
					}
				
			}
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.debug("UpdateEmployee * doPost - error1: " + e.getMessage());
				e.printStackTrace();
			}
	}
	
	/*protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateEmployee.class);
		String editId = request.getParameter("edit");
		String regionSelected = request.getParameter("cri_region");
		
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<Employee> employeeList = new LinkedList<Employee>();
		List<Employee> entityList = new LinkedList<Employee>();
					
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Query q = new Query(Employee.class.getSimpleName());
				
				
				Filter regionFilter = new FilterPredicate("region",
						                      FilterOperator.EQUAL,
						                      StringUtils.defaultString(regionSelected, "Malaysia"));
						
				q.setFilter(regionFilter);
				
				// PreparedQuery contains the methods for fetching query results from the datastore
				PreparedQuery pq = datastore.prepare(q);
				
				iTotalRecords = pq.countEntities(FetchOptions.Builder.withDefaults());
				
				QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
				for(Entity result : results){
					Employee employee = new Employee();
					employee.setEmailAddress((String)result.getProperty("emailAddress"));
					employee.setFullName((String)result.getProperty("fullName"));
					employee.setJobTitle((String)result.getProperty("jobTitle"));
					String hiredDate = (String)result.getProperty("hiredDate");
					String birthDate = (String)result.getProperty("birthDate");
					String resignedDate = (String)result.getProperty("resignedDate");
					employee.setHiredDate(hiredDate.replace("/", "-"));
					employee.setBirthDate(birthDate.replace("/", "-"));
					employee.setResignedDate(StringUtils.isNotEmpty(resignedDate.replace("/", "-")) ? resignedDate.replace("/", "-") : "-");
					entityList.add(employee);
					}
				
				
				for(Employee result : entityList){
					if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase())) ||
					   (StringUtils.lowerCase(result.getFullName()).contains(dataTableModel.sSearch.toLowerCase())) ||
					   (StringUtils.lowerCase(result.getHiredDate()).contains(dataTableModel.sSearch.toLowerCase())) ||
					   (StringUtils.lowerCase(result.getBirthDate()).contains(dataTableModel.sSearch.toLowerCase())) || 
					   (StringUtils.lowerCase(result.getResignedDate()).contains(dataTableModel.sSearch.toLowerCase()))){
						employeeList.add(result); // add employee that matches given search criterion
					}
				}
				
				final int sortColumnIndex = dataTableModel.iSortColumnIndex;
				final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
				
				Collections.sort(employeeList, new Comparator<Employee>(){
					@Override
					public int compare(Employee c1, Employee c2) {	
						switch(sortColumnIndex){
						case 0:
							return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
						case 1:
							return c1.getFullName().compareTo(c2.getFullName()) * sortDirection;
						case 2:
							return c1.getHiredDate().compareTo(c2.getHiredDate()) * sortDirection;
						case 3:
							return c1.getBirthDate().compareTo(c2.getBirthDate()) * sortDirection;
						case 4:
							return c1.getResignedDate().compareTo(c2.getResignedDate()) * sortDirection;
						}
						return 0;
					}
				});
				
				if(employeeList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
					employeeList = employeeList.subList(dataTableModel.iDisplayStart, employeeList.size());
				} else {
					employeeList = employeeList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
				}
				
				try {
					JsonObject jsonResponse = new JsonObject();			
					jsonResponse.addProperty("sEcho", sEcho);
					jsonResponse.addProperty("iTotalRecords", iTotalRecords);
					jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
					
					for(Employee employee : employeeList){
						JsonArray row = new JsonArray();
						row.add(new JsonPrimitive("<input type=\"radio\" name=\"" + "empRad" + "\"" + " value=\"" + employee.getEmailAddress() + "\"" + "onclick=\"javascript:cmd_parm();\"/>"));
						row.add(new JsonPrimitive(employee.getEmailAddress()));
						row.add(new JsonPrimitive(employee.getFullName()));
						row.add(new JsonPrimitive(employee.getHiredDate()));
						row.add(new JsonPrimitive(employee.getBirthDate()));
						row.add(new JsonPrimitive(employee.getResignedDate()));
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
		
	}*/

}
