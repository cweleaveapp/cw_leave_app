package com.google.appengine.datastore;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.entities.Department;

/**
 * Manager of department entities
 *
 */
public class DepartmentService extends DataStoreUtil {

	private static DepartmentService instance;
	
	public static DepartmentService getInstance() {
	      if(instance == null) {
	    	  instance = new DepartmentService();
	      }
	      return instance;
	}
	
	public String addDepartment(String name_en, String name_tc,
		boolean exgratiaLeaveDayEnabled, String approverEmail, String delegateEmail){
		Entity deptEntity = new Entity(Department.class.getSimpleName());
		deptEntity.setProperty("name_en", name_en);
		deptEntity.setProperty("name_tc", name_tc);
		deptEntity.setProperty("exgratia_leave_day_enabled", exgratiaLeaveDayEnabled);
		deptEntity.setProperty("approver_email", approverEmail);
		if(delegateEmail.length()>0){
			deptEntity.setProperty("delegate_email", delegateEmail);
		}		
		getDatastore().put(deptEntity);
		return KeyFactory.keyToString(deptEntity.getKey());  
	}
	
	public String updateDepartment(String id, String name_en, String name_tc,
		boolean exgratiaLeaveDayEnabled, String approverEmail, String delegateEmail){
		Entity e = findEntity(id);
		if(e!=null){
			e.setProperty("name_en", name_en);
			e.setProperty("name_tc", name_tc);
			e.setProperty("exgratia_leave_day_enabled", exgratiaLeaveDayEnabled);
			e.setProperty("approver_email", approverEmail);
			if(delegateEmail.length()>0){
				e.setProperty("delegate_email", delegateEmail);
			}
			getDatastore().put(e);
			return KeyFactory.keyToString(e.getKey());  
		}
		return null;
	}
	
	public Department getDepartmentByName(String dept_name) throws EntityNotFoundException{
		Department department = null;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Department.class.getSimpleName());
		query.setFilter(FilterOperator.EQUAL.of("name_en", dept_name));
		for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())){
			String dept_key = KeyFactory.keyToString(entity.getKey());
			if(entity.hasProperty("delegate_email")){
				department = new Department(dept_key,(String)entity.getProperty("name_en"),(String)entity.getProperty("name_tc"),
							(boolean)entity.getProperty("exgratia_leave_day_enabled"),(String)entity.getProperty("approver_email"),(String)entity.getProperty("delegate_email"));
			} else {
				department = new Department(dept_key,(String)entity.getProperty("name_en"),(String)entity.getProperty("name_tc"),(boolean)entity.getProperty("exgratia_leave_day_enabled"),(String)entity.getProperty("approver_email"));
			}
		}
		return department;
	}
	
	public Department getDepartmentByKey(String keyStr){
		Department deptObj = null;
		Entity dept = findEntity(keyStr);
		deptObj = new Department(keyStr,(String)dept.getProperty("name_en"),(String)dept.getProperty("name_tc"),
				(boolean) dept.getProperty("exgratia_leave_day_enabled"), (String) dept.getProperty("approver_email"));
		if(dept.hasProperty("delegate_email")){
			deptObj.setDelegate((String)dept.getProperty("delegate_email"));
		}
		return deptObj;
	}
	
	public List<Department> getDepartments() {
		Query query = new Query(Department.class.getSimpleName());
		query.addSort("approver_email");
		List<Department> results = new ArrayList<Department>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Department dept = null;
			String dept_key = KeyFactory.keyToString(entity.getKey());
			if(entity.hasProperty("delegate_email")){				
				dept = new Department(dept_key,(String)entity.getProperty("name_en"),(String)entity.getProperty("name_tc"),
							(boolean)entity.getProperty("exgratia_leave_day_enabled"),(String)entity.getProperty("approver_email"),(String)entity.getProperty("delegate_email"));
			} else {
				dept = new Department(dept_key,(String)entity.getProperty("name_en"),(String)entity.getProperty("name_tc"),(boolean)entity.getProperty("exgratia_leave_day_enabled"),(String)entity.getProperty("approver_email"));
			}
			results.add(dept);
		}
		return results;
	}
	
	
	public void deleteDepartment(String deptKeyStr) {
		Key deptKey = KeyFactory.stringToKey(deptKeyStr);
		Entity dept;
		try {
			dept = getDatastore().get(deptKey);
			if(dept!=null){
				getDatastore().delete(deptKey);
			}
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Entity dept = findEntityByKey(deptKey);
		
	}
}
