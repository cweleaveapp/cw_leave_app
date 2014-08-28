package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DataStoreUtil;

public class MCSupervisorService extends DataStoreUtil {
	
	public List<MCSupervisor> findSupervisorBy(String columnName, String value, String filterOperator){
		try {
		MCSupervisor supervisor = new MCSupervisor();
		List<MCSupervisor> supervisorList = new ArrayList<MCSupervisor>();
		Iterable<Entity> entity = listEntities(MCSupervisor.class.getSimpleName(),  columnName,  value, filterOperator);
		for(Entity result : entity){
			supervisor.setEmailAddress((String)result.getProperty("emailAddress"));
			supervisor.setRegion((String)result.getProperty("region"));
			supervisor.setId(KeyFactory.keyToString(result.getKey()));
			supervisorList.add(supervisor);
			
		}
			return supervisorList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public MCSupervisor findSupervisorByColumnName(String columnName, String value){
		MCSupervisor supervisor = new MCSupervisor();
		try {			
			Entity entity = findEntities(MCSupervisor.class.getSimpleName(), columnName, value);
			if(entity!=null){
				supervisor.setEmailAddress((String)entity.getProperty("emailAddress"));
				supervisor.setRegion((String)entity.getProperty("region"));
				supervisor.setId(KeyFactory.keyToString(entity.getKey()));
			}
			
			} catch (Exception e) {
				e.printStackTrace();
				 return null;
			}
		return supervisor;
	}
	
	public String addSupervisor(String emailAddress, String region) {
		Entity supervisorEntity = new Entity(MCSupervisor.class.getSimpleName());
		supervisorEntity.setProperty("emailAddress", emailAddress);
		supervisorEntity.setProperty("region", region);
		getDatastore().put(supervisorEntity);
		return KeyFactory.keyToString(supervisorEntity.getKey());   
	}
	
	
	public List<MCSupervisor> getSupervisors() {
		Query query = new Query(MCSupervisor.class.getSimpleName());
		List<MCSupervisor> results = new ArrayList<MCSupervisor>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			MCSupervisor supervisor = new MCSupervisor();
			supervisor.setEmailAddress((String)entity.getProperty("emailAddress"));
			supervisor.setRegion((String)entity.getProperty("region"));
			supervisor.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(supervisor);
		}

		/* Sort supervisors alphabetically */
		Collections.sort(results, new SortSupervisors());
		return results;
	}
	
	
	public void deleteSupervisor(String emailAddress) {;
		Transaction txn = getDatastore().beginTransaction();
		try {
			Query query = new Query(MCSupervisor.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("emailAddress");
				if (tmp.equalsIgnoreCase(emailAddress)) {
					getDatastore().delete(entity.getKey());
					txn.commit();
				}
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	public void deleteSupervisor() {
		Query query = new Query(MCSupervisor.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				getDatastore().delete(entity.getKey());
			}
	}
	
	
	public void updateSupervisor(String emailAddress, String region) throws EntityNotFoundException {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Query query = new Query(MCSupervisor.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("emailAddress");
				if (tmp.equalsIgnoreCase(emailAddress)) {
					Entity supervisor = getDatastore().get(entity.getKey());
					supervisor.setProperty("emailAddress", emailAddress);
					supervisor.setProperty("region", region);
					getDatastore().put(supervisor);
					txn.commit();
				}
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
