package com.google.appengine.datastore;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.entities.LeaveTypes;

public class LeaveMgtService extends DataStoreUtil {
	
	private static LeaveMgtService instance;
	
	public static LeaveMgtService getInstance() {
	      if(instance == null) {
	    	  instance = new LeaveMgtService();
	      }
	      return instance;
	}
	
	public String editLeaveType(String id, String nameEn, String nameTc, String abbreviation){
		Entity e = null;
		if(id.length()>0){
			e = findEntity(id);
			/*if(e==null){
				e = new Entity(LeaveTypes.class.getSimpleName());
			}*/
		} else {
			e = new Entity(LeaveTypes.class.getSimpleName());
		}
		
		e.setProperty("nameEn", nameEn);
		e.setProperty("nameTc", nameTc);
		e.setProperty("abbreviation",abbreviation);
		getDatastore().put(e);
		return KeyFactory.keyToString(e.getKey());
	}
	
	public List<LeaveTypes> listLeaveTypes(){
		Query q = new Query(LeaveTypes.class.getSimpleName());
		List<LeaveTypes> lists = new ArrayList<LeaveTypes>();
		for (Entity e : getDatastore().prepare(q).asIterable(FetchOptions.Builder.withDefaults())) {
			LeaveTypes t = new LeaveTypes((String)e.getProperty("nameEn"),
						(String)e.getProperty("nameTc"),(String)e.getProperty("abbreviation"));
			t.setId(KeyFactory.keyToString(e.getKey()));
			lists.add(t);
		}
		return lists;
	}
}
