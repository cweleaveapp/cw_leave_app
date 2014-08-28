package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;

public class ApprovedLeaveService extends DataStoreUtil {
	
	public List<MCApprovedLeave> getApproveLeaveListByEmail(String email){
		Query q = new Query(MCApprovedLeave.class.getSimpleName());
//		Filter emailFilter = new FilterPredicate("emailAdd",
//				                      FilterOperator.GREATER_THAN_OR_EQUAL,
//				                      email);
//		Filter emailFilter2 = new FilterPredicate("emailAdd",
//                FilterOperator.LESS_THAN_OR_EQUAL,
//                email);
//		Filter filter =
//				  CompositeFilterOperator.and(emailFilter, emailFilter2);
		
//		q.addSort("startDate", SortDirection.ASCENDING);
		PreparedQuery pq = getDatastore().prepare(q);
		QueryResultList<Entity> results =  pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		List<MCApprovedLeave> entityList = new ArrayList<MCApprovedLeave>();
		for(Entity result : results){
			MCApprovedLeave approvedLeave = new MCApprovedLeave();
			approvedLeave.setTime((String)result.getProperty("time"));
			approvedLeave.setEmailAdd((String)result.getProperty("emailAdd"));
			approvedLeave.setNumOfDays((String)result.getProperty("numOfDays"));
			approvedLeave.setStartDate((String)result.getProperty("startDate"));
			approvedLeave.setEndDate((String)result.getProperty("endDate"));
			approvedLeave.setSupervisor((String)result.getProperty("supervisor"));
			approvedLeave.setLeaveType((String)result.getProperty("leaveType"));
			approvedLeave.setChangeType((String)result.getProperty("changeType"));
			approvedLeave.setRemark((String)result.getProperty("remark"));
			approvedLeave.setAttachmentUrl((String)result.getProperty("attachmentUrl"));
			approvedLeave.setProjectName((String)result.getProperty("projectName"));
			if(email.equalsIgnoreCase(approvedLeave.getEmailAdd())){
				entityList.add(approvedLeave);
			}
			
			}
		return entityList;
		
	}
	
	public MCApprovedLeave findApprovedLeaveByValue(String columnName, String value, String filterOperator){
		MCApprovedLeave appLeave = new MCApprovedLeave();
		Iterable<Entity> e = listEntities(MCApprovedLeave.class.getSimpleName(), columnName, value, filterOperator);
		for(Entity entity : e){
			appLeave.setTime((String)entity.getProperty("time"));
			appLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
			appLeave.setNumOfDays((String)entity.getProperty("numOfDays"));
			appLeave.setStartDate((String)entity.getProperty("startDate"));
			appLeave.setEndDate((String)entity.getProperty("endDate"));
			appLeave.setLeaveType((String)entity.getProperty("leaveType"));
			appLeave.setSupervisor((String)entity.getProperty("supervisor"));
			appLeave.setRemark((String)entity.getProperty("remark"));
			appLeave.setRegion((String)entity.getProperty("region"));
			appLeave.setChangeType((String)entity.getProperty("changeType"));
			appLeave.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
			appLeave.setProjectName((String)entity.getProperty("projectName"));
			appLeave.setId(KeyFactory.keyToString(entity.getKey()));
		}
		return appLeave;
	}
	
	public void updateApprovedLeave(String id, String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String region, String changeType, String attachmentUrl,
			String projectName) {
		Filter approvedLeaveFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(id));
		Query query = new Query(MCApprovedLeave.class.getSimpleName()).setFilter(approvedLeaveFilter);
		Entity appLeaveEntity = getDatastore().prepare(query).asSingleEntity();
		appLeaveEntity.setProperty("time", time);
		appLeaveEntity.setProperty("emailAdd", emailAdd);
		appLeaveEntity.setProperty("numOfDays", numOfDays);
		appLeaveEntity.setProperty("startDate", startDate);
		appLeaveEntity.setProperty("endDate", endDate);
		appLeaveEntity.setProperty("leaveType", leaveType);
		appLeaveEntity.setProperty("supervisor", supervisor);
		appLeaveEntity.setProperty("remark", remark);
		appLeaveEntity.setProperty("region", region);
		appLeaveEntity.setProperty("changeType", changeType);
		appLeaveEntity.setProperty("attachmentUrl", attachmentUrl);
		appLeaveEntity.setProperty("projectName", projectName);
		getDatastore().put(appLeaveEntity);
	}

	public String addApprovedLeave(String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String region, String changeType, String attachmentUrl,
			String projectName) {
		Entity appLeaveEntity = new Entity(MCApprovedLeave.class.getSimpleName());
		appLeaveEntity.setProperty("time", time);
		appLeaveEntity.setProperty("emailAdd", emailAdd);
		appLeaveEntity.setProperty("numOfDays", numOfDays);
		appLeaveEntity.setProperty("startDate", startDate);
		appLeaveEntity.setProperty("endDate", endDate);
		appLeaveEntity.setProperty("leaveType", leaveType);
		appLeaveEntity.setProperty("supervisor", supervisor);
		appLeaveEntity.setProperty("remark", remark);
		appLeaveEntity.setProperty("region", region);
		appLeaveEntity.setProperty("changeType", changeType);
		appLeaveEntity.setProperty("attachmentUrl", attachmentUrl);
		appLeaveEntity.setProperty("projectName", projectName);
		getDatastore().put(appLeaveEntity);
		return KeyFactory.keyToString(appLeaveEntity.getKey());   
	}
	
	
	public List<MCApprovedLeave> getFastApprovedLeave() {
		Query query = new Query(MCApprovedLeave.class.getSimpleName());
		List<MCApprovedLeave> results = new ArrayList<MCApprovedLeave>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			MCApprovedLeave appLeave = new MCApprovedLeave();
			appLeave.setTime((String)entity.getProperty("time"));
			appLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
			appLeave.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(appLeave);
		}
		/* Sort approved leave descendingly based on time */
		Collections.sort(results, new SortApproved());
		return results;
	}
	
	public List<MCApprovedLeave> getApprovedLeaveByRegion(String region) {
		Filter filter = new FilterPredicate("region",
                FilterOperator.EQUAL,
                region);
		Query query = new Query(MCApprovedLeave.class.getSimpleName()).setFilter(filter);
		List<MCApprovedLeave> results = new ArrayList<MCApprovedLeave>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			MCApprovedLeave appLeave = new MCApprovedLeave();
			appLeave.setTime((String)entity.getProperty("time"));
			appLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
			appLeave.setNumOfDays((String)entity.getProperty("numOfDays"));
			appLeave.setStartDate((String)entity.getProperty("startDate"));
			appLeave.setEndDate((String)entity.getProperty("endDate"));
			appLeave.setLeaveType((String)entity.getProperty("leaveType"));
			appLeave.setSupervisor((String)entity.getProperty("supervisor"));
			appLeave.setRemark((String)entity.getProperty("remark"));
			appLeave.setRegion((String)entity.getProperty("region"));
			appLeave.setChangeType((String)entity.getProperty("changeType"));
			appLeave.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
			appLeave.setProjectName((String)entity.getProperty("projectName"));
			appLeave.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(appLeave);
		}
		/* Sort approved leave descendingly based on time */
		Collections.sort(results, new SortApproved());
		return results;
	}


	public List<MCApprovedLeave> getApprovedLeave() {
		Query query = new Query(MCApprovedLeave.class.getSimpleName());
		List<MCApprovedLeave> results = new ArrayList<MCApprovedLeave>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			MCApprovedLeave appLeave = new MCApprovedLeave();
			appLeave.setTime((String)entity.getProperty("time"));
			appLeave.setEmailAdd((String)entity.getProperty("emailAdd"));
			appLeave.setNumOfDays((String)entity.getProperty("numOfDays"));
			appLeave.setStartDate((String)entity.getProperty("startDate"));
			appLeave.setEndDate((String)entity.getProperty("endDate"));
			appLeave.setLeaveType((String)entity.getProperty("leaveType"));
			appLeave.setSupervisor((String)entity.getProperty("supervisor"));
			appLeave.setRemark((String)entity.getProperty("remark"));
			appLeave.setRegion((String)entity.getProperty("region"));
			appLeave.setChangeType((String)entity.getProperty("changeType"));
			appLeave.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
			appLeave.setProjectName((String)entity.getProperty("projectName"));
			appLeave.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(appLeave);
		}
		/* Sort approved leave descendingly based on time */
		Collections.sort(results, new SortApproved());
		return results;
	}


	public void deleteApprovedLeave(String id) {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Filter approvedLeaveFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                    FilterOperator.EQUAL,
                    KeyFactory.stringToKey(id));
			Query query = new Query(MCApprovedLeave.class.getSimpleName()).setFilter(approvedLeaveFilter);
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
