package com.google.appengine.cron;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.mct.MCApprovedLeave;
import com.google.appengine.mct.Notification;
import com.google.appengine.util.ConstantUtils;

public class NotifyCompExpService extends DataStoreUtil {
	
	public void updateNotifyComp(Notification notify) {
		Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(notify.getApproveId()));
		Query query = new Query(Notification.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
		entity.setProperty("approveId", notify.getApproveId());
		entity.setProperty("email", notify.getEmail());
		entity.setProperty("status", ConstantUtils.SEND);
		entity.setProperty("approveTime", notify.getApproveTime());
		getDatastore().put(entity);
		
	}
	
	public String saveNotifyComp(Notification notify) {
		Key notifyKey = KeyFactory.createKey(Notification.class.getSimpleName(), "notify");
		Entity entity = new Entity(Notification.class.getSimpleName(),notifyKey);
		entity.setProperty("approveId", notify.getApproveId());
		entity.setProperty("email", notify.getEmail());
		entity.setProperty("status", notify.getStatus());
		entity.setProperty("approveTime", notify.getApproveTime());
		getDatastore().put(entity);
		return KeyFactory.keyToString(entity.getKey());   
		
	}
	
	public List<Notification> getAllPendingNotifyComp() {
		List<Notification> results = new ArrayList<Notification>();
		Filter filter = new FilterPredicate("status",
                FilterOperator.EQUAL,
                ConstantUtils.PENDING);
		Query query = new Query(Notification.class.getSimpleName()).setFilter(filter);
		
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Notification notify = new Notification();
			notify.setEmail((String)entity.getProperty("email"));
			notify.setStatus((String)entity.getProperty("status"));
			notify.setId(KeyFactory.keyToString(entity.getKey()));
			notify.setApproveId((String)entity.getProperty("approveId"));
			notify.setApproveTime((String)entity.getProperty("approveTime"));
			results.add(notify);
		}
		return results;
		
	}
	
	public List<Notification> getAllNotifyComp() {
		List<Notification> results = new ArrayList<Notification>();
		Query query = new Query(Notification.class.getSimpleName());
		
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Notification notify = new Notification();
			notify.setEmail((String)entity.getProperty("email"));
			notify.setStatus((String)entity.getProperty("status"));
			notify.setId(KeyFactory.keyToString(entity.getKey()));
			notify.setApproveId((String)entity.getProperty("approveId"));
			notify.setApproveTime((String)entity.getProperty("approveTime"));
			results.add(notify);
		}
		return results;
		
	}
	
	public Notification getNotifyCompExpByAppId(String approveId) {
		Filter filter = new FilterPredicate("approveId",
                FilterOperator.EQUAL,
                approveId);
		Query query = new Query(Notification.class.getSimpleName()).setFilter(filter);
		
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			Notification notify = new Notification();
			notify.setEmail((String)entity.getProperty("email"));
			notify.setStatus((String)entity.getProperty("status"));
			notify.setId(KeyFactory.keyToString(entity.getKey()));
			notify.setApproveId((String)entity.getProperty("approveId"));
			notify.setApproveTime((String)entity.getProperty("approveTime"));
		return notify;
	}

}
