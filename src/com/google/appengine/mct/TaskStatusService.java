package com.google.appengine.mct;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DataStoreUtil;

public class TaskStatusService extends DataStoreUtil {

	public TaskStatus getStatusByBlobkey(String blobKey) {
		Filter filter = new FilterPredicate("blobKey",
                FilterOperator.EQUAL,
                blobKey);
		Query query = new Query(TaskStatus.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
		TaskStatus taskStatus = new TaskStatus();
		taskStatus.setBlobKey((String)entity.getProperty("blobKey"));
		taskStatus.setStatus((String)entity.getProperty("status"));
		taskStatus.setId(KeyFactory.keyToString(entity.getKey()));
		return taskStatus;
	}
	
	public void save(TaskStatus task) {
		Key taskStatusKey = KeyFactory.createKey(TaskStatus.class.getSimpleName(), "task");
		Entity entity = new Entity(TaskStatus.class.getSimpleName(),taskStatusKey);
		entity.setProperty("blobKey", task.getBlobKey());
		entity.setProperty("status", task.getStatus());
		getDatastore().put(entity);
		
	}

	public void update(TaskStatus task) {
		try {
			Entity entity = getDatastore().get(KeyFactory.stringToKey(task.getId()));
			entity.setProperty("blobKey", task.getBlobKey());
			entity.setProperty("status", task.getStatus());
			getDatastore().put(entity);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
