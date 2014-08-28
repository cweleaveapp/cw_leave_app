package com.google.appengine.datastore;

import java.util.*;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.entities.ActingPerson;
import com.google.appengine.entities.LeaveRequest;

public class ActorService extends DataStoreUtil {
	
	private static ActorService instance;
	
	public static ActorService getInstance() {
	      if(instance == null) {
	    	  instance = new ActorService();
	      }
	      return instance;
	}
	
	private ActorService() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Key> saveActingPerson(List<ActingPerson> apList){
		List<Key> apKeys = new ArrayList<Key>();
		for(ActingPerson p: apList){
			Entity ae = findEntityByKey(p.getApKey());
			String aName = (String) ae.getProperty("fullName");
			Entity act = new Entity("ActingPerson",p.getApKey());				
			act.setProperty("actor", aName);
			act.setProperty("duties",p.getDuties());
			act.setProperty("empKey",p.getEmpKey());
			act.setProperty("refNo", p.getRefNo());
			Key actK = getDatastore().put(act);
			apKeys.add(actK);
		}
		return apKeys;
	}
	
	public List<LeaveRequest> findPendingActorList(String actorKey){
		List<LeaveRequest> lreqs = new ArrayList<LeaveRequest>();		
		Query query = new Query(ActingPerson.class.getSimpleName());
		query.setAncestor(KeyFactory.stringToKey(actorKey));
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if(!results.isEmpty()){
			for(Entity r : results){
				String refNo = (String) r.getProperty("refNo");
				Query q2 = new Query(LeaveRequest.class.getSimpleName());
				Filter filter = new FilterPredicate("ref", FilterOperator.EQUAL,refNo);
				q2.setFilter(filter);				
				Entity let = datastore.prepare(q2).asSingleEntity();
				if(let!=null){
					LeaveRequest leave = LeaveRequestService.getInstance().getLeaveRequest(KeyFactory.keyToString(let.getKey()));
					lreqs.add(leave);
				}				
			}
		}
		return lreqs;
	}
	
	public String actionUpdateDecision(String decision, String key){
		Entity e = findEntity(key);
		String acKeyStr = "";
		if(e!=null){
			e.setProperty("decision",decision);
			Key actK = getDatastore().put(e);
			acKeyStr = KeyFactory.keyToString(actK);
		}
		return acKeyStr;
	}
	
	public String actionUpdateDecision(String decision, String reason, String key){
		Entity e = findEntity(key);
		String acKeyStr = "";
		if(e!=null){
			e.setProperty("decision",decision);
			e.setProperty("reason",reason);
			Key actK = getDatastore().put(e);
			acKeyStr = KeyFactory.keyToString(actK);
		}
		return acKeyStr;
	}
	
	
	public ActingPerson getActingRecord(Key key){
		ActingPerson a = new ActingPerson();
		Entity e = findEntityByKey(key);
		String apKey = KeyFactory.keyToString(e.getParent());
		a.setApKey(apKey);
		a.setName((String)e.getProperty("actor"));
		a.setDuties((String)e.getProperty("duties"));
		a.setRefNo((String)e.getProperty("refNo"));
		if(e.hasProperty("decision")){
			a.setDecision((String)e.getProperty("decision"));
		}
		if(e.hasProperty("reason")){
			a.setDecision((String)e.getProperty("reason"));
		}
		return a;
	}
}
