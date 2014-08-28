package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DataStoreUtil;

public class RejectedLeaveService extends DataStoreUtil {

	
	public String addRejectedLeave(String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String region) {
		Entity rejectedLeaveEntity = new Entity(RejectedLeave.class.getSimpleName());
		rejectedLeaveEntity.setProperty("time", time);
		rejectedLeaveEntity.setProperty("emailAdd", emailAdd);
		rejectedLeaveEntity.setProperty("numOfDays", numOfDays);
		rejectedLeaveEntity.setProperty("startDate", startDate);
		rejectedLeaveEntity.setProperty("endDate", endDate);
		rejectedLeaveEntity.setProperty("leaveType", leaveType);
		rejectedLeaveEntity.setProperty("supervisor", supervisor);
		rejectedLeaveEntity.setProperty("remark", remark);
		rejectedLeaveEntity.setProperty("region", region);
		getDatastore().put(rejectedLeaveEntity);
		return KeyFactory.keyToString(rejectedLeaveEntity.getKey());   
	}
	
	
	public List<RejectedLeave> getFastRejectedLeave() {
		Query query = new Query(RejectedLeave.class.getSimpleName());
		List<RejectedLeave> results = new ArrayList<RejectedLeave>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			RejectedLeave rejectedLeave = new RejectedLeave();
			rejectedLeave.setTime((String)entity.getProperty("time"));
			rejectedLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
			rejectedLeave.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(rejectedLeave);
		}
		/* Sort rejected leave descendingly based on time */
		Collections.sort(results, new SortRejected());
		return results;
	}
	
	
	public List<RejectedLeave> getRejectedLeave() {
		Query query = new Query(RejectedLeave.class.getSimpleName());
		List<RejectedLeave> results = new ArrayList<RejectedLeave>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			RejectedLeave rejectedLeave = new RejectedLeave();
			rejectedLeave.setTime((String)entity.getProperty("time"));
			rejectedLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
			rejectedLeave.setNumOfDays((String)entity.getProperty("numOfDays"));
			rejectedLeave.setStartDate((String)entity.getProperty("startDate"));
			rejectedLeave.setEndDate((String)entity.getProperty("endDate"));
			rejectedLeave.setLeaveType((String)entity.getProperty("leaveType"));
			rejectedLeave.setSupervisor((String)entity.getProperty("supervisor"));
			rejectedLeave.setRemark((String)entity.getProperty("remark"));
			rejectedLeave.setRegion((String)entity.getProperty("region"));
			rejectedLeave.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(rejectedLeave);
		}
		/* Sort rejected leave descendingly based on time */
		Collections.sort(results, new SortRejected());
		return results;
	}
	
	
	public void deleteRejectedLeave(String id) {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Filter filter = new FilterPredicate("__key__",
                    FilterOperator.EQUAL,
                    KeyFactory.stringToKey(id));
			Query query = new Query(RejectedLeave.class.getSimpleName()).setFilter(filter);
			Entity entity = getDatastore().prepare(query).asSingleEntity();
			getDatastore().delete(entity.getKey());
			txn.commit();
				
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
