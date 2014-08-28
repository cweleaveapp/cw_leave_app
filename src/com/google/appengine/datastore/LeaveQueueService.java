package com.google.appengine.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveType;
import com.google.appengine.mct.Misc;
import com.google.appengine.mct.SortLeaveQueue;

public class LeaveQueueService extends DataStoreUtil {
	
	public static LeaveQueueService instance;
	
	public static LeaveQueueService getInstance(){
		if(instance == null) {
	    	  instance = new LeaveQueueService();
	      }
	      return instance;
	}
	
	public LeaveQueue getLeaveQueue(String key){
		LeaveQueue leaveQ = new LeaveQueue();
		Entity entity = findEntity(key);
		if(entity !=null){
			String leaveKeyStr = KeyFactory.keyToString((Key)entity.getProperty("leaveKey"));			
			leaveQ.setCreateDate((Date)entity.getProperty("createDate"));
			LeaveRequest request = LeaveRequestService.getInstance().getLeaveRequest(leaveKeyStr);
			leaveQ.setLeaveRequest(request);
			leaveQ.setType((int)entity.getProperty("leaveType"));
			leaveQ.setApprovedBy((String)entity.getProperty("approvedBy"));
			if(entity.hasProperty("attachments")){
				leaveQ.setAttachments((List<String>)entity.getProperty("attachments"));
			}
		}
		return leaveQ;
	}
	
	public List<LeaveQueue> getPendingLeaveQueue(){
		Query query = new Query(LeaveQueue.class.getSimpleName());
		List<LeaveQueue> results = new ArrayList<LeaveQueue>();
		for (Entity e :  getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			if(!e.hasProperty("decision") && !e.hasProperty("review")){
				LeaveQueue leaveQ = new LeaveQueue();
				String leaveKeyStr = KeyFactory.keyToString((Key)e.getProperty("leaveKey"));			
				leaveQ.setCreateDate((Date)e.getProperty("createDate"));
				LeaveRequest request = LeaveRequestService.getInstance().getLeaveRequest(leaveKeyStr);
				leaveQ.setLeaveRequest(request);
				Long type = (Long)e.getProperty("leaveType");			
				leaveQ.setType(type.intValue());
				leaveQ.setApprovedBy((String)e.getProperty("approvedBy"));
				if(e.hasProperty("attachments")){
					leaveQ.setAttachments((List<String>)e.getProperty("attachments"));
				}
				results.add(leaveQ);
			}			
		}
		return results;
	}
	
	public List<LeaveQueue> getDocsLeaveQueue(){
		Query query = new Query(LeaveQueue.class.getSimpleName());
		List<LeaveQueue> results = new ArrayList<LeaveQueue>();
		for (Entity e :  getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			if( !e.hasProperty("decision") && !e.hasProperty("review") && 
					!e.hasProperty("attachments")){
				LeaveQueue leaveQ = new LeaveQueue();
				String leaveKeyStr = KeyFactory.keyToString((Key)e.getProperty("leaveKey"));			
				leaveQ.setCreateDate((Date)e.getProperty("createDate"));
				LeaveRequest request = LeaveRequestService.getInstance().getLeaveRequest(leaveKeyStr);
				leaveQ.setLeaveRequest(request);
				Long type = (Long)e.getProperty("leaveType");			
				leaveQ.setType(type.intValue());
				leaveQ.setApprovedBy((String)e.getProperty("approvedBy"));
				if(e.hasProperty("attachments")){
					leaveQ.setAttachments((List<String>)e.getProperty("attachments"));
				}
				results.add(leaveQ);
			}			
		}
		return results;
	}
	
	public String addLeaveQueue(Date createDate, Key leaveKey, int leaveType, String approvedBy) {
		Entity leaveQueueEntity = new Entity(LeaveQueue.class.getSimpleName());
		Entity leaveRequest = findEntityByKey(leaveKey);		
		leaveQueueEntity.setProperty("createDate", createDate);
		leaveQueueEntity.setProperty("leaveKey", leaveKey);
		leaveQueueEntity.setProperty("leaveType", leaveType);
		leaveQueueEntity.setProperty("approvedBy", approvedBy);
		if(leaveRequest.hasProperty("attachments")){
			leaveQueueEntity.setProperty("attachments", (List<String>)leaveRequest.getProperty("attachments"));
		}
		getDatastore().put(leaveQueueEntity);
		return KeyFactory.keyToString(leaveQueueEntity.getKey());   
	}
	
	public LeaveQueue findLeaveQueueById(String id){
		LeaveQueue leaveQ = new LeaveQueue();
		Entity entity = findEntity(id);
		if(entity !=null){
			leaveQ.setTime((String)entity.getProperty("time"));
			leaveQ.setEmailAdd((String)entity.getProperty("emailAdd"));
			leaveQ.setNumOfDays((String)entity.getProperty("numOfDays"));
			leaveQ.setStartDate((String)entity.getProperty("startDate"));
			leaveQ.setEndDate((String)entity.getProperty("endDate"));
			leaveQ.setLeaveType((String)entity.getProperty("leaveType"));
			leaveQ.setSupervisor((String)entity.getProperty("supervisor"));
			leaveQ.setRemark((String)entity.getProperty("remark"));
			leaveQ.setProjectName((String)entity.getProperty("projectName"));
			leaveQ.setChangeType((String)entity.getProperty("changeType"));
			leaveQ.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
			leaveQ.setId(KeyFactory.keyToString(entity.getKey()));
		}
		
		return leaveQ;
	}
	
	public String updateLeaveQueue(String keyStr, String HRemail, String decision){
		Key leaveKey = KeyFactory.stringToKey(keyStr);
		Query q = new Query(LeaveQueue.class.getSimpleName());
		Filter filter = new FilterPredicate("leaveKey",
                FilterOperator.EQUAL,leaveKey);
		q.setFilter(filter);
		Entity entity = getDatastore().prepare(q).asSingleEntity();
		if(entity!=null){
			Date now = Misc.setCalendarByLocale();
			entity.setProperty("HRemail", HRemail);
			entity.setProperty("decision", decision);
			entity.setProperty("review", now);
			getDatastore().put(entity);
			return decision;
		} else {
			return "notfound";
		}
	}
	
	public String addLeaveQueue(String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String projectName, String changeType,
			String attachmentUrl) {
		Entity leaveQueueEntity = new Entity(LeaveQueue.class.getSimpleName());
		leaveQueueEntity.setProperty("time", time);
		leaveQueueEntity.setProperty("emailAdd", emailAdd);
		leaveQueueEntity.setProperty("numOfDays", numOfDays);
		leaveQueueEntity.setProperty("startDate", startDate);
		leaveQueueEntity.setProperty("endDate", endDate);
		leaveQueueEntity.setProperty("leaveType", leaveType);
		leaveQueueEntity.setProperty("supervisor", supervisor);
		leaveQueueEntity.setProperty("remark", remark);
		leaveQueueEntity.setProperty("projectName", projectName);
		leaveQueueEntity.setProperty("changeType", changeType);
		leaveQueueEntity.setProperty("attachmentUrl", attachmentUrl);
		getDatastore().put(leaveQueueEntity);
		return KeyFactory.keyToString(leaveQueueEntity.getKey());   
	}
	
	public void updateLeaveQueue(String id, String time, String emailAdd, String numOfDays, String startDate, String endDate, 
			String leaveType, String supervisor, String remark, String projectName, String changeType,
			String approveId, String attachmentUrl) {
		
		Filter leaveQueueFilter = new FilterPredicate("__key__",
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(id));
		Query query = new Query(LeaveQueue.class.getSimpleName()).setFilter(leaveQueueFilter);
		Entity leaveQueueEntity =  getDatastore().prepare(query).asSingleEntity();
		//is existing leave queue record
		if(leaveQueueEntity!=null){
		leaveQueueEntity.setProperty("time", time);
		leaveQueueEntity.setProperty("emailAdd", emailAdd);
		leaveQueueEntity.setProperty("numOfDays", numOfDays);
		leaveQueueEntity.setProperty("startDate", startDate);
		leaveQueueEntity.setProperty("endDate", endDate);
		leaveQueueEntity.setProperty("leaveType", leaveType);
		leaveQueueEntity.setProperty("supervisor", supervisor);
		leaveQueueEntity.setProperty("remark", remark);
		leaveQueueEntity.setProperty("projectName", projectName);
		leaveQueueEntity.setProperty("changeType", changeType);
		leaveQueueEntity.setProperty("attachmentUrl", attachmentUrl);
		getDatastore().put(leaveQueueEntity);  
		}else{
			//is new leave queue record
			Entity entity = new Entity(LeaveQueue.class.getSimpleName());
			entity.setProperty("time", time);
			entity.setProperty("emailAdd", emailAdd);
			entity.setProperty("numOfDays", numOfDays);
			entity.setProperty("startDate", startDate);
			entity.setProperty("endDate", endDate);
			entity.setProperty("leaveType", leaveType);
			entity.setProperty("supervisor", supervisor);
			entity.setProperty("remark", remark);
			entity.setProperty("projectName", projectName);
			entity.setProperty("changeType", changeType);
			entity.setProperty("approveId", approveId);
			entity.setProperty("attachmentUrl", attachmentUrl);
			getDatastore().put(entity);
		}
	}
	
	
	public List<LeaveQueue> getLeaveQueue() {
		Query query = new Query(LeaveQueue.class.getSimpleName());
		List<LeaveQueue> results = new ArrayList<LeaveQueue>();
		for (Entity entity :  getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			LeaveQueue leaveQ = new LeaveQueue();
			//leaveQ.setTime((String)entity.getProperty("time"));
			leaveQ.setCreateDate((Date)entity.getProperty("createDate"));
			leaveQ.setApprovedBy((String)entity.getProperty("approvedBy"));
			Key lrk = (Key)entity.getProperty("leaveKey");
			LeaveRequest lr = LeaveRequestService.getInstance().getLeaveRequest(KeyFactory.keyToString(lrk));
			leaveQ.setLeaveRequest(lr);
			Long leaveType = (Long)entity.getProperty("leaveType");
			for(LeaveType t : LeaveType.values()){
				if(t.getId()==leaveType.intValue()){
					leaveQ.setLeaveType(t.getType());
				}
			}
			if(entity.hasProperty("attachments")){
				leaveQ.setAttachments((List<String>)entity.getProperty("attachments"));
			}
			/*leaveQ.setNumOfDays((String)entity.getProperty("numOfDays"));
			leaveQ.setStartDate((String)entity.getProperty("startDate"));
			leaveQ.setStartDateBean((String)entity.getProperty("startDate"));
			leaveQ.setEndDate((String)entity.getProperty("endDate"));
			leaveQ.setEndDateBean((String)entity.getProperty("endDate"));*/
						
			/*leaveQ.setSupervisor((String)entity.getProperty("supervisor"));
			leaveQ.setRemark((String)entity.getProperty("remark"));
			leaveQ.setProjectName((String)entity.getProperty("projectName"));
			leaveQ.setChangeType((String)entity.getProperty("changeType"));
			leaveQ.setAttachmentUrl((String)entity.getProperty("attachmentUrl"));
			leaveQ.setApproveId((String)entity.getProperty("approveId"));
			leaveQ.setId(KeyFactory.keyToString(entity.getKey()));*/
			results.add(leaveQ);
		}
		/* Sort leave queue descendingly based on time */
		Collections.sort(results, new SortLeaveQueue());
		return results;
	}
	
	
	public void deleteLeaveQueue(String id) {
		Transaction txn =  getDatastore().beginTransaction();
		try {
			Filter leaveQueueFilter = new FilterPredicate("__key__",
                    FilterOperator.EQUAL,
                    KeyFactory.stringToKey(id));
			Query query = new Query(LeaveQueue.class.getSimpleName()).setFilter(leaveQueueFilter);
			Entity entity =  getDatastore().prepare(query).asSingleEntity();
			 getDatastore().delete(entity.getKey());
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
