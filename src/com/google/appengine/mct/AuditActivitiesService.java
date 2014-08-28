package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.datastore.DataStoreUtil;

public class AuditActivitiesService extends DataStoreUtil {
	
	public void saveLog(AuditLog aLog){
		Entity entity = new Entity(AuditLog.class.getSimpleName());
		entity.setProperty("time", aLog.getTime());
		entity.setProperty("emailAddress", aLog.getEmailAddress());
		entity.setProperty("name", aLog.getName());
		getDatastore().put(entity);
	}
	
	public List<AuditLog> getAllAuditLog(){
		Query query = new Query(AuditLog.class.getSimpleName()).addSort("time", SortDirection.DESCENDING);
		List<AuditLog> results = new ArrayList<AuditLog>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			AuditLog auditLog = new AuditLog();
			auditLog.setEmailAddress((String)entity.getProperty("emailAddress"));
			auditLog.setName((String)entity.getProperty("name"));
			auditLog.setTime((Date)entity.getProperty("time"));
			auditLog.setId((KeyFactory.keyToString(entity.getKey())));
			results.add(auditLog);
		}
		return results;
	}

}
