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

public class HistoryService extends DataStoreUtil {

	public String addToHistory(String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String region, String projectName, String changeType,
			String createdBy) {
		Entity historyEntity = new Entity(History.class.getSimpleName());
		historyEntity.setProperty("time", time);
		historyEntity.setProperty("emailAdd", emailAdd);
		historyEntity.setProperty("numOfDays", numOfDays);
		historyEntity.setProperty("startDate", startDate);
		historyEntity.setProperty("endDate", endDate);
		historyEntity.setProperty("leaveType", leaveType);
		historyEntity.setProperty("supervisor", supervisor);
		historyEntity.setProperty("remark", remark);
		historyEntity.setProperty("region", region);
		historyEntity.setProperty("projectName", projectName);
		historyEntity.setProperty("changeType", changeType);
		historyEntity.setProperty("createdBy", createdBy);
		getDatastore().put(historyEntity);
		return KeyFactory.keyToString(historyEntity.getKey());   
	}
	
	public void updateToHistory(String id, String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String region, String projectName, String changeType,
			String createdBy) {
		Transaction txn = getDatastore().beginTransaction();
		try{
		Filter historyFilter = new FilterPredicate("__key__",
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(id));
		Query query = new Query(History.class.getSimpleName()).setFilter(historyFilter);
		Entity historyEntity = getDatastore().prepare(query).asSingleEntity();
		txn.commit();
		if(historyEntity!=null){
			historyEntity.setProperty("time", time);
			historyEntity.setProperty("emailAdd", emailAdd);
			historyEntity.setProperty("numOfDays", numOfDays);
			historyEntity.setProperty("startDate", startDate);
			historyEntity.setProperty("endDate", endDate);
			historyEntity.setProperty("leaveType", leaveType);
			historyEntity.setProperty("supervisor", supervisor);
			historyEntity.setProperty("remark", remark);
			historyEntity.setProperty("region", region);
			historyEntity.setProperty("projectName", projectName);
			historyEntity.setProperty("changeType", changeType);
			historyEntity.setProperty("createdBy", createdBy);
			getDatastore().put(historyEntity);
		}else{
			Entity entity = new Entity(History.class.getSimpleName());
			entity.setProperty("time", time);
			entity.setProperty("emailAdd", emailAdd);
			entity.setProperty("numOfDays", numOfDays);
			entity.setProperty("startDate", startDate);
			entity.setProperty("endDate", endDate);
			entity.setProperty("leaveType", leaveType);
			entity.setProperty("supervisor", supervisor);
			entity.setProperty("remark", remark);
			entity.setProperty("region", region);
			entity.setProperty("projectName", projectName);
			entity.setProperty("changeType", changeType);
			entity.setProperty("createdBy", createdBy);
			getDatastore().put(entity);
		}
		
		} finally {
		if (txn.isActive()) {
			txn.rollback();
		}
	
		}
	}
	
	
	public List<History> getFastHistory() {
		Query query = new Query(History.class.getSimpleName());
		List<History> results = new ArrayList<History>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			History historyQ = new History();
			historyQ.setTime((String)entity.getProperty("time"));
			historyQ.setEmailAdd((String)entity.getProperty("emailAdd"));
			historyQ.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(historyQ);
		}
		/* Sort history descendingly based on time */
		Collections.sort(results, new SortHistory());
		return results;
	}
	
	
	public List<History> getHistory() {
		Query query = new Query(History.class.getSimpleName());
		List<History> results = new ArrayList<History>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			History historyQ = new History();
			historyQ.setTime((String)entity.getProperty("time"));
			historyQ.setEmailAdd((String)entity.getProperty("emailAdd"));
			historyQ.setNumOfDays((String)entity.getProperty("numOfDays"));
			historyQ.setStartDate((String)entity.getProperty("startDate"));
			historyQ.setEndDate((String)entity.getProperty("endDate"));
			historyQ.setLeaveType((String)entity.getProperty("leaveType"));
			historyQ.setSupervisor((String)entity.getProperty("supervisor"));
			historyQ.setRemark((String)entity.getProperty("remark"));
			historyQ.setProjectName((String)entity.getProperty("projectName"));
			historyQ.setChangeType((String)entity.getProperty("changeType"));
			historyQ.setCreatedBy((String)entity.getProperty("createdBy"));
			historyQ.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(historyQ);
		}
		/* Sort history descendingly based on time */
		Collections.sort(results, new SortHistory());
		return results;
	}
	
	public void deleteHistory() {
			Query query = new Query(History.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				getDatastore().delete(entity.getKey());
			}
		
	}
	
	
	public void deleteHistory(String id) {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Filter historyFilter = new FilterPredicate("__key__",
                    FilterOperator.EQUAL,
                    KeyFactory.stringToKey(id));
			Query query = new Query(History.class.getSimpleName()).setFilter(historyFilter);
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
