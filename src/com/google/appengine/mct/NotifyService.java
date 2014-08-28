package com.google.appengine.mct;

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
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.datastore.DataStoreUtil;

@SuppressWarnings("serial")
public class NotifyService extends DataStoreUtil {

	public String addToNotify(String addNotifyFreq
			) {
		Key notifyKey = KeyFactory.createKey(MCEmployee.class.getSimpleName(), "notify");
		Entity notifyEntity = new Entity(Notify.class.getSimpleName(),notifyKey);
		notifyEntity.setProperty("addNotifyFreq", addNotifyFreq);
		getDatastore().put(notifyEntity);
		return KeyFactory.keyToString(notifyEntity.getKey());   
	}

	
	public List<Notify> getNotify() {
		Query query = new Query(Notify.class.getSimpleName());
		List<Notify> results = new ArrayList<Notify>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Notify notifyQ = new Notify();
			notifyQ.setAddNotifyFreq((String)entity.getProperty("addNotifyFreq"));
			notifyQ.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(notifyQ);
		}
		return results;
	}
	
	public void updateNotify(String addNotifyFreq
			) throws EntityNotFoundException {
		
		List<Notify> notifyList = getNotify();
		
		if(notifyList != null && !notifyList.isEmpty()){
			
				Query query = new Query(Notify.class.getSimpleName());
				for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
					Entity notifyEntity = getDatastore().get(entity.getKey());
					notifyEntity.setProperty("addNotifyFreq", addNotifyFreq);

					getDatastore().put(notifyEntity);
				}
			
		}
		else{
			addToNotify(addNotifyFreq);
		}
		
		
	}
}
