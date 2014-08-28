package com.google.appengine.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.util.ConstantUtils;

public class DataStoreUtil {

	public static DatastoreService datastore;

	public DataStoreUtil(){
		datastore = DatastoreServiceFactory.getDatastoreService();              
	}
	
	public DatastoreService getDatastore() {
		return datastore;
	}

	public void setDatastore(DatastoreService datastore) {
		this.datastore = datastore;
	}
	
	public static Entity findEntityByKey(Key key) {
		try {
			return datastore.get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Entity findEntityByKey2(Key key) {
		try {
			return datastore.get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Entity findEntity(String key) {
		try {
			 return datastore.get(KeyFactory.stringToKey(key));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			 return null;
		}
	}
	
	public Iterable<Entity> listEntities(String objectName, String columnName, String value,  Object filterOperator) {
	    Query query = new Query(objectName);
	    
	    if (value != null && !"".equals(value)) {
	    	
	    	if(columnName.equals(Entity.KEY_RESERVED_PROPERTY)){
	    		Filter filter = new FilterPredicate(columnName,
		    			(FilterOperator) ConstantUtils.mapFilter().get(filterOperator),
		    			KeyFactory.stringToKey(value));
	    		query.setFilter(filter);
	    	}
	    	else{
	    		Filter filter = new FilterPredicate(columnName,
		    			(FilterOperator) ConstantUtils.mapFilter().get(filterOperator),
		    			value);
	    		query.setFilter(filter);
	    	}
	    	
	    	
	    }
	    PreparedQuery pq = datastore.prepare(query);
	    return pq.asIterable();
	  }
	
	public static Entity findEntityByColumn(String objectName, String columnName, String value) {
	    Query query = new Query(objectName);
	    if (value != null && !"".equals(value)) {
	    	Filter filter = new FilterPredicate(columnName,
                    FilterOperator.EQUAL,
                    value.toLowerCase());
	    	query.setFilter(filter);
	    }
	    PreparedQuery pq = datastore.prepare(query);
	    return pq.asSingleEntity();
	  }
	
	public Entity findEntities(String objectName, String columnName, String value) {
	    Query query = new Query(objectName);
	    if (value != null && !"".equals(value)) {
	    	Filter filter = new FilterPredicate(columnName,
                    FilterOperator.EQUAL,
                    value.toLowerCase());
	    	query.setFilter(filter);
	    }
	    PreparedQuery pq = datastore.prepare(query);
	    return pq.asSingleEntity();
	  }
}
