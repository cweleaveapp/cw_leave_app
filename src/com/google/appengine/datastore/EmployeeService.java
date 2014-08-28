package com.google.appengine.datastore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.entities.*;
import com.google.appengine.enums.UserType;
import com.google.appengine.mct.MCEmployee;
import com.google.appengine.mct.SortByRegion;
import com.google.appengine.util.ConstantUtils;

public class EmployeeService extends DataStoreUtil {

	private static final long serialVersionUID = 1L;
	
	private static EmployeeService instance;
	
	public static EmployeeService getInstance() {
	      if(instance == null) {
	    	  instance = new EmployeeService();
	      }
	      return instance;
	}
	
	public Employee findEmployeeByKey(Key key){
		Entity e = findEntityByKey(key);
		Employee emp = new Employee();
		emp.setstaffId((String)e.getProperty("staffId"));
		emp.setEmailAddress((String)e.getProperty("emailAddress"));
		emp.setFullName((String)e.getProperty("fullName"));
		emp.setDepartment((String)e.getProperty("department"));
		emp.setHiredDate((String)e.getProperty("hiredDate"));
		emp.setBirthDate((String)e.getProperty("birthDate"));
		emp.setSupervisor((String)e.getProperty("supervisor"));
		emp.setUserType((String)e.getProperty("userType"));			
		emp.setJobTitle((String)e.getProperty("jobTitle"));
		emp.setEmpKey(KeyFactory.keyToString(e.getKey()));
		Employee sObj = findEmployeeByColumnName("emailAddress",(String)e.getProperty("supervisor"));
		emp.setSuperObj(sObj);
		Entity ae = findEntity(KeyFactory.keyToString(e.getParent().getParent()));
		Employee aObj = findEmployeeByColumnName("emailAddress",(String)ae.getProperty("approver_email"));
		emp.setDeptApprover(aObj);
		if(ae.hasProperty("delegate_email")){
			Employee dObj = findEmployeeByColumnName("emailAddress",(String)ae.getProperty("delegate_email"));
			emp.setDeptDelegator(dObj);
		}
		return emp;
		
	}
	public Employee findEmployeeByColumnName(String columnName, String value) {
		Iterable<Entity> e = listEntities(Employee.class.getSimpleName(),  columnName,  value, ConstantUtils.EQUAL);
		Employee emp = new Employee();
		for(Entity entity : e){
			emp.setstaffId((String)entity.getProperty("staffId"));
			emp.setEmailAddress((String)entity.getProperty("emailAddress"));
			emp.setFullName((String)entity.getProperty("fullName"));
			emp.setDepartment((String)entity.getProperty("department"));
			emp.setHiredDate((String)entity.getProperty("hiredDate"));
			emp.setBirthDate((String)entity.getProperty("birthDate"));
			emp.setSupervisor((String)entity.getProperty("supervisor"));
			emp.setUserType((String)entity.getProperty("userType"));			
			emp.setJobTitle((String)entity.getProperty("jobTitle"));
			emp.setEmpKey(KeyFactory.keyToString(entity.getKey()));
		}		
		return emp;
	}
	
	public Employee getFullEmployeeDetails(String emailAddress){		
		Entity emp = findEntityByColumn(Employee.class.getSimpleName(),"emailAddress", emailAddress);
		Employee empObj = findEmployeeByColumnName("emailAddress", emailAddress);
		if(emp.hasProperty("supervisor")){
			Employee sor = findEmployeeByColumnName("emailAddress", empObj.getSupervisor());
			empObj.setSuperObj(sor);
		}	
		Key deptKey = emp.getParent().getParent();
		Entity dept = findEntityByKey(deptKey);
		empObj.setEmpKey(KeyFactory.keyToString(emp.getKey()));
		Employee approveObj = findEmployeeByColumnName("emailAddress", (String)dept.getProperty("approver_email"));
		empObj.setDeptApprover(approveObj);
		if(dept.hasProperty("delegate_email")){
			Employee dgObj = findEmployeeByColumnName("emailAddress", (String)dept.getProperty("delegate_email"));
			empObj.setDeptDelegator(dgObj);
		}
		Calendar cal = Calendar.getInstance();
		String currYear = String.valueOf(cal.get(cal.YEAR));
		EmployeeLeaveDetails edtls = EmployeeLeaveDetailsService.getInstance().findEmployeeLeaveDetails(emailAddress, currYear);
		empObj.setEmployeeLeaveDetails(edtls);
		return empObj;
	}
		
	public String addEmployee(String staffId, String emailAddress, String fullName, String department, 
			String hiredDate, String birthDate, String supervisor, String jobTitle) {
		Key employeeKey = KeyFactory.createKey(Employee.class.getSimpleName(), emailAddress);
		Entity employeeEntity = new Entity(Employee.class.getSimpleName(),employeeKey);
		employeeEntity.setProperty("staffId",staffId);
		employeeEntity.setProperty("emailAddress", emailAddress);
		employeeEntity.setProperty("fullName", fullName);
		employeeEntity.setProperty("department", department);
		employeeEntity.setProperty("hiredDate", hiredDate);
		employeeEntity.setProperty("birthDate", birthDate);
		employeeEntity.setProperty("supervisor", supervisor);
		employeeEntity.setProperty("userType", UserType.EMPLOYEE.userTypeName);
		employeeEntity.setProperty("jobTitle", jobTitle);
		getDatastore().put(employeeEntity);
		return KeyFactory.keyToString(employeeEntity.getKey());   
	}
	
	public String addEmployee(Employee employee) throws EntityNotFoundException {		
		Key employeeKey = KeyFactory.createKey(Employee.class.getSimpleName(), employee.getEmailAddress());	
		Department dept = DepartmentService.getInstance().getDepartmentByName(employee.getDepartment());
		if(dept.getid()!=null){
			employeeKey = KeyFactory.createKey(KeyFactory.stringToKey(dept.getid()),Employee.class.getSimpleName(),employee.getEmailAddress());
		}		
		Entity employeeEntity = new Entity(Employee.class.getSimpleName(),employeeKey);
		employeeEntity.setProperty("staffId", employee.getstaffId());
		employeeEntity.setProperty("emailAddress", employee.getEmailAddress());
		employeeEntity.setProperty("fullName", employee.getFullName());
		employeeEntity.setProperty("department", employee.getDepartment());
		employeeEntity.setProperty("hiredDate", employee.getHiredDate());
		employeeEntity.setProperty("birthDate", employee.getBirthDate());
		employeeEntity.setProperty("supervisor", employee.getSupervisor());
		employeeEntity.setProperty("userType", UserType.EMPLOYEE.userTypeName);
		employeeEntity.setProperty("jobTitle", employee.getJobTitle());
		getDatastore().put(employeeEntity);
		return KeyFactory.keyToString(employeeEntity.getKey());   
	}
	
	public List<Employee> getDepartmentEmployee(String deptKey){
		Query query = new Query(Employee.class.getSimpleName());
		query.setAncestor(KeyFactory.stringToKey(deptKey));
		List<Employee> results = new ArrayList<Employee>();
		List<Entity> emps = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : emps){
			Employee emp = new Employee();
			emp.setstaffId((String)entity.getProperty("staffId"));
			emp.setEmailAddress((String)entity.getProperty("emailAddress"));
			emp.setFullName((String)entity.getProperty("fullName"));
			emp.setDepartment((String)entity.getProperty("department"));
			emp.setHiredDate((String)entity.getProperty("hiredDate"));
			emp.setBirthDate((String)entity.getProperty("birthDate"));
			emp.setUserType((String)entity.getProperty("userType"));
			emp.setSupervisor((String)entity.getProperty("supervisor"));
			emp.setJobTitle((String)entity.getProperty("jobTitle"));
			emp.setEmpKey(KeyFactory.keyToString(entity.getKey()));
			results.add(emp);
		}
		return results;
	}
	
	public List<Employee> getEmployees() {
		Query query = new Query(Employee.class.getSimpleName());
		List<Employee> results = new ArrayList<Employee>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Employee emp = new Employee();
			emp.setstaffId((String)entity.getProperty("staffId"));
			emp.setEmailAddress((String)entity.getProperty("emailAddress"));
			emp.setFullName((String)entity.getProperty("fullName"));
			emp.setDepartment((String)entity.getProperty("department"));
			emp.setHiredDate((String)entity.getProperty("hiredDate"));
			emp.setBirthDate((String)entity.getProperty("birthDate"));
			emp.setUserType((String)entity.getProperty("userType"));
			emp.setSupervisor((String)entity.getProperty("supervisor"));
			emp.setJobTitle((String)entity.getProperty("jobTitle"));
			emp.setEmpKey(KeyFactory.keyToString(entity.getKey()));
			results.add(emp);
		}
		return results;
	}

	public void updateEmployee(String staffId, String emailAddress, String fullName, String department, 
		String hiredDate, String birthDate, String supervisor, String userType, String jobTitle) throws EntityNotFoundException {

		Query query = new Query(Employee.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			String tmp = (String)entity.getProperty("emailAddress");
			if (tmp.equalsIgnoreCase(emailAddress)) {
				Entity employee = getDatastore().get(entity.getKey());
				employee.setProperty("staffId", staffId);
				employee.setProperty("emailAddress", emailAddress);
				employee.setProperty("fullName", fullName);
				employee.setProperty("department", department);
				employee.setProperty("hiredDate", hiredDate);
				employee.setProperty("birthDate", birthDate);					
				employee.setProperty("supervisor", supervisor);
				employee.setProperty("userType", userType);
				employee.setProperty("jobTitle", jobTitle);
				getDatastore().put(employee);
			}
		}
	}
	
	public void updateEmployee(Employee employee) throws EntityNotFoundException {
		Query query = new Query(Employee.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			String tmp = (String)entity.getProperty("emailAddress");
			if (tmp.toLowerCase().equals(employee.getEmailAddress().toLowerCase())) {
				Entity emp= getDatastore().get(entity.getKey());
				emp.setProperty("emailAddress", employee.getEmailAddress());
				emp.setProperty("fullName", employee.getFullName());
				emp.setProperty("department", employee.getDepartment());
				emp.setProperty("hiredDate", employee.getHiredDate());
				emp.setProperty("birthDate", employee.getBirthDate());
				emp.setProperty("supervisor", employee.getSupervisor());
				emp.setProperty("userType", employee.getUserType());
				emp.setProperty("jobTitle", employee.getJobTitle());
				getDatastore().put(emp);
			}
		}
}
	
	public void deleteEmployee(String emailAddress) {
		Iterable<Entity> e = listEntities(Employee.class.getSimpleName(),  "emailAddress",  emailAddress, ConstantUtils.EQUAL);
		for (Entity entity : e) {
			String tmp = (String)entity.getProperty("emailAddress");
			if (tmp.equalsIgnoreCase(emailAddress)) {
				getDatastore().delete(entity.getKey());
			}
		}
	}
	
	public MCEmployee findMCEmployeeByColumnName(String columnName, String value){
		Iterable<Entity> e = listEntities(MCEmployee.class.getSimpleName(),  columnName,  value, ConstantUtils.EQUAL);
		MCEmployee employee = new MCEmployee();
		for(Entity entity : e){
			employee.setEmailAddress((String)entity.getProperty("emailAddress"));
			employee.setFullName((String)entity.getProperty("fullName"));
			employee.setRegion((String)entity.getProperty("region"));
			employee.setHiredDate((String)entity.getProperty("hiredDate"));
			employee.setBirthDate((String)entity.getProperty("birthDate"));
			employee.setResignedDate((String)entity.getProperty("resignedDate"));
			employee.setSupervisor((String)entity.getProperty("supervisor"));
			employee.setJobTitle((String)entity.getProperty("jobTitle"));
			employee.setId(KeyFactory.keyToString(entity.getKey()));
		}
		
		return employee;
	}	
	
	public String addMCEmployee(String emailAddress, String fullName, String region, 
			String hiredDate, String birthDate, String resignedDate, String supervisor, String jobTitle) {
		Key employeeKey = KeyFactory.createKey(MCEmployee.class.getSimpleName(), "employee");
		Entity employeeEntity = new Entity(MCEmployee.class.getSimpleName(),employeeKey);
		employeeEntity.setProperty("emailAddress", emailAddress);
		employeeEntity.setProperty("fullName", fullName);
		employeeEntity.setProperty("region", region);
		employeeEntity.setProperty("hiredDate", hiredDate);
		employeeEntity.setProperty("birthDate", birthDate);
		employeeEntity.setProperty("resignedDate", resignedDate);
		employeeEntity.setProperty("supervisor", supervisor);
		employeeEntity.setProperty("jobTitle", jobTitle);
		getDatastore().put(employeeEntity);
		return KeyFactory.keyToString(employeeEntity.getKey());   
	}
		
	public String addMCEmployee(MCEmployee employee) {
		Key employeeKey = KeyFactory.createKey(MCEmployee.class.getSimpleName(), "employee");
		Entity employeeEntity = new Entity(MCEmployee.class.getSimpleName(),employeeKey);
		employeeEntity.setProperty("emailAddress", employee.getEmailAddress());
		employeeEntity.setProperty("fullName", employee.getFullName());
		employeeEntity.setProperty("region", employee.getRegion());
		employeeEntity.setProperty("hiredDate", employee.getHiredDate());
		employeeEntity.setProperty("birthDate", employee.getBirthDate());
		employeeEntity.setProperty("resignedDate", employee.getResignedDate());
		employeeEntity.setProperty("supervisor", employee.getSupervisor());
		employeeEntity.setProperty("jobTitle", employee.getJobTitle());
		getDatastore().put(employeeEntity);
		return KeyFactory.keyToString(employeeEntity.getKey());   
	}

	public List<MCEmployee> getMCEmployees() {
		Query query = new Query(MCEmployee.class.getSimpleName());
		List<MCEmployee> results = new ArrayList<MCEmployee>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			MCEmployee employee = new MCEmployee();
			employee.setEmailAddress((String)entity.getProperty("emailAddress"));
			employee.setFullName((String)entity.getProperty("fullName"));
			employee.setRegion((String)entity.getProperty("region"));
			employee.setHiredDate((String)entity.getProperty("hiredDate"));
			employee.setBirthDate((String)entity.getProperty("birthDate"));
			employee.setResignedDate((String)entity.getProperty("resignedDate"));
			employee.setSupervisor((String)entity.getProperty("supervisor"));
			employee.setJobTitle((String)entity.getProperty("jobTitle"));
			employee.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(employee);
		}

		/* Sort by region */
		Collections.sort(results, new SortByRegion());
		return results;
	}

	
	
	public void deleteMCEmployee(String emailAddress) {
			Query query = new Query(MCEmployee.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("emailAddress");
				if (tmp.equalsIgnoreCase(emailAddress)) {
					getDatastore().delete(entity.getKey());
				}
			}
	}

	

	public void updateMCEmployee(String emailAddress, String fullName, String region, 
			String hiredDate, String birthDate, String resignedDate, String supervisor, String jobTitle) throws EntityNotFoundException {

			Query query = new Query(MCEmployee.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("emailAddress");
				if (tmp.equalsIgnoreCase(emailAddress)) {
					Entity employee = getDatastore().get(entity.getKey());
					employee.setProperty("emailAddress", emailAddress);
					employee.setProperty("fullName", fullName);
					employee.setProperty("region", region);
					employee.setProperty("hiredDate", hiredDate);
					employee.setProperty("birthDate", birthDate);
					employee.setProperty("resignedDate", resignedDate);
					employee.setProperty("supervisor", supervisor);
					employee.setProperty("jobTitle", jobTitle);
					getDatastore().put(employee);
				}
			}
	}
	
	public void updateMCEmployee(MCEmployee employee) throws EntityNotFoundException {
			Query query = new Query(MCEmployee.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("emailAddress");
				if (tmp.toLowerCase().equals(employee.getEmailAddress().toLowerCase())) {
					Entity emp= getDatastore().get(entity.getKey());
					emp.setProperty("emailAddress", employee.getEmailAddress());
					emp.setProperty("fullName", employee.getFullName());
					emp.setProperty("region", employee.getRegion());
					emp.setProperty("hiredDate", employee.getHiredDate());
					emp.setProperty("birthDate", employee.getBirthDate());
					emp.setProperty("resignedDate", employee.getResignedDate());
					emp.setProperty("supervisor", employee.getSupervisor());
					emp.setProperty("jobTitle", employee.getJobTitle());
					getDatastore().put(emp);
				}
			}
	}
	
}
