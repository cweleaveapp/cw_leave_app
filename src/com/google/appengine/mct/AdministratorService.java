package com.google.appengine.mct;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DataStoreUtil;

public class AdministratorService extends DataStoreUtil {
	
	public Administrator findAdministratorByEmailAddress(String emailAddress){
		try {
		Administrator administrator = new Administrator();
		Filter approvedLeaveFilter = new FilterPredicate("emailAddress",
                FilterOperator.EQUAL,
                emailAddress.toLowerCase());
		Query query = new Query(Administrator.class.getSimpleName()).setFilter(approvedLeaveFilter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
		if(entity!=null){
			administrator.setEmailAddress((String)entity.getProperty("emailAddress"));
			administrator.setId(KeyFactory.keyToString(entity.getKey()));
		}
		return administrator;
		} catch (Exception e) {
			e.printStackTrace();
			 return null;
		}
	}
	
	public String addAdministrator(String emailAddress) {
		Key administratorKey = KeyFactory.createKey(Administrator.class.getSimpleName(), "administrator");
		Entity adminEntity = new Entity(Administrator.class.getSimpleName(),administratorKey);
		adminEntity.setProperty("emailAddress", emailAddress);
		getDatastore() .put(adminEntity);
		return KeyFactory.keyToString(adminEntity.getKey());   
	}
	
	
	public List<Administrator> getAdministrators() {
		Query query = new Query(Administrator.class.getSimpleName());
		List<Administrator> results = new ArrayList<Administrator>();
		for (Entity entity : getDatastore() .prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Administrator admin = new Administrator();
			admin.setEmailAddress((String)entity.getProperty("emailAddress"));
			admin.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(admin);
		}
		/* Sort administrators alphabetically */
		Collections.sort(results, new SortAdministrators());
		return results;
	}
	
	
	public void deleteAdministrator(String emailAddress) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		try {
			Query query = new Query(Administrator.class.getSimpleName());
			for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("emailAddress");
				if (tmp.equalsIgnoreCase(emailAddress)) {
					datastore.delete(entity.getKey());
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
