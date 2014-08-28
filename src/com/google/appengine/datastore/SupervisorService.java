package com.google.appengine.datastore;

import java.util.ArrayList;
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
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.Supervisor;

public class SupervisorService extends DataStoreUtil {
	
	public static DataStoreUtil util = new DataStoreUtil();
	
	public List<Supervisor> findSupervisorBy(String columnName, String value, String filterOperator){
		try {
		Supervisor supervisor = new Supervisor();
		List<Supervisor> supervisorList = new ArrayList<Supervisor>();
		Iterable<Entity> entity = listEntities(Supervisor.class.getSimpleName(),  columnName,  value, filterOperator);
		for(Entity result : entity){
			supervisor.setEmailAddress((String)result.getProperty("emailAddress"));
			supervisor.setDepartment((String)result.getProperty("department"));
			supervisor.setId(KeyFactory.keyToString(result.getKey()));
			supervisorList.add(supervisor);
			
		}
			return supervisorList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Supervisor findSupervisorByColumnName(String columnName, String value){
		Supervisor supervisor = new Supervisor();
		Entity entity = null;
		try {
			if(columnName.equalsIgnoreCase("key")){
				entity = findEntityByKey(KeyFactory.stringToKey(value));
			} else {
				entity = util.findEntities(Supervisor.class.getSimpleName(), columnName, value);
			}			
			
			if(entity!=null){
				supervisor.setEmailAddress((String)entity.getProperty("emailAddress"));
				supervisor.setDepartment((String)entity.getProperty("department"));
				supervisor.setId(KeyFactory.keyToString(entity.getKey()));
			}
			
			} catch (Exception e) {
				e.printStackTrace();
				 return null;
			}
		return supervisor;
	}
	
	public String addSupervisor(String emailAddress, String departmentKey) {
		Key deptKey = KeyFactory.stringToKey(departmentKey);
		Entity supervisorEntity = new Entity(Supervisor.class.getSimpleName(),deptKey);
		Entity dept = DataStoreUtil.findEntityByKey(deptKey);
		supervisorEntity.setProperty("emailAddress", emailAddress);
		supervisorEntity.setProperty("department", dept.getProperty("name_en"));
		getDatastore().put(supervisorEntity);
		return KeyFactory.keyToString(supervisorEntity.getKey());   
	}
	
	
	public static List<Supervisor> getSupervisors() {
		Query query = new Query(Supervisor.class.getSimpleName());
		List<Supervisor> results = new ArrayList<Supervisor>();
		for (Entity entity : util.getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Entity et = findEntityByColumn(Employee.class.getSimpleName(),"emailAddress",(String)entity.getProperty("emailAddress"));
			Supervisor supervisor = new Supervisor();
			supervisor.setEmailAddress((String)entity.getProperty("emailAddress"));
			supervisor.setDepartment((String)entity.getProperty("department"));
			supervisor.setFullName((String)et.getProperty("fullName"));
			supervisor.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(supervisor);
		}
		
		return results;
	}
	
	public List<Supervisor> getSupervisorByDepartment(String deptKeyStr){
		Key deptKey = KeyFactory.stringToKey(deptKeyStr);
		Query query = new Query(Supervisor.class.getSimpleName()).setAncestor(deptKey);
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		List<Supervisor> supervisorList = new ArrayList<Supervisor>();
		for(Entity en : results){
			Supervisor sor = new Supervisor();
			sor.setDepartment((String) en.getProperty("department"));
			sor.setEmailAddress((String)en.getProperty("emailAddress"));
			sor.setId(KeyFactory.keyToString(en.getKey()));
			supervisorList.add(sor);
		}
		return supervisorList;
	}
	
 	public void deleteSupervisor(String emailAddress) {
		Entity sorEntity = findEntityByColumn(Supervisor.class.getSimpleName(),"emailAddress",emailAddress);
		if(sorEntity!=null) {
			datastore.delete(sorEntity.getKey());
		}
	}
	
	public void deleteAllSupervisor() {
		Query query = new Query(Supervisor.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				getDatastore().delete(entity.getKey());
			}
	}
	
	
	public Supervisor updateSupervisor(String emailAddress, String department) throws EntityNotFoundException {
		Supervisor sor = new Supervisor();
		Entity sorEntity = findEntityByColumn(Supervisor.class.getSimpleName(),"emailAddress",emailAddress);
		if(sorEntity!=null) {
			sorEntity.setProperty("emailAddress", emailAddress);
			sorEntity.setProperty("department", department);
			datastore.put(sorEntity);
			sor.setEmailAddress(emailAddress);
			sor.setDepartment(department);			
		}
		return sor;
	}
}
