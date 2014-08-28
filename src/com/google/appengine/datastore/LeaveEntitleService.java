package com.google.appengine.datastore;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.entities.LeaveEntitlement;
import com.google.appengine.mct.LeaveEntitle;
import com.google.appengine.mct.SickLeave;
import com.google.appengine.util.ConstantUtils;

public class LeaveEntitleService extends DataStoreUtil {

	private static final long serialVersionUID = 1L;
	
	private static LeaveEntitleService instance;
	
	public static LeaveEntitleService getInstance() {
	      if(instance == null) {
	    	  instance = new LeaveEntitleService();
	      }
	      return instance;
	}
	
	public SickLeave getLessThanYear(String id){
		
		Filter idfilter = new FilterPredicate("leaveEntitleId",
                FilterOperator.EQUAL,
                id);
		
		Filter lesfilter = new FilterPredicate("sickLeaveType",
                FilterOperator.EQUAL,
                ConstantUtils.LESS_THAN);
		
		Filter filter = CompositeFilterOperator.and(idfilter, lesfilter);
		
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			SickLeave sl = new SickLeave();
			sl.setRegion((String)entity.getProperty("department"));	
			sl.setLeaveEntitleId((String)entity.getProperty("leaveEntitleId"));
			sl.setSickLeaveDay((String)entity.getProperty("sickLeaveDay"));
			sl.setSickLeaveType((String)entity.getProperty("sickLeaveType"));
			sl.setSickLeaveYear((String)entity.getProperty("sickLeaveYear"));
			sl.setId(KeyFactory.keyToString(entity.getKey()));
		return sl;
		
	}
	
	public SickLeave getLessThanYearOrEqual(String id){
		
		Filter idfilter = new FilterPredicate("leaveEntitleId",
                FilterOperator.EQUAL,
                id);
		
		Filter lesfilter = new FilterPredicate("sickLeaveType",
                FilterOperator.EQUAL,
                ConstantUtils.LESS_THAN_OR_EQUAL);
		
		Filter filter = CompositeFilterOperator.and(idfilter, lesfilter);
		
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			SickLeave sl = new SickLeave();
			sl.setRegion((String)entity.getProperty("department"));	
			sl.setLeaveEntitleId((String)entity.getProperty("leaveEntitleId"));
			sl.setSickLeaveDay((String)entity.getProperty("sickLeaveDay"));
			sl.setSickLeaveType((String)entity.getProperty("sickLeaveType"));
			sl.setSickLeaveYear((String)entity.getProperty("sickLeaveYear"));
			sl.setId(KeyFactory.keyToString(entity.getKey()));
		return sl;
		
	}
	
	public SickLeave getGreaterThan(String id){
		
		Filter idfilter = new FilterPredicate("leaveEntitleId",
                FilterOperator.EQUAL,
                id);
		
		Filter lesfilter = new FilterPredicate("sickLeaveType",
                FilterOperator.EQUAL,
                ConstantUtils.GREATER_THAN);
		Filter filter = CompositeFilterOperator.and(idfilter, lesfilter);
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			SickLeave sl = new SickLeave();
			sl.setRegion((String)entity.getProperty("department"));	
			sl.setLeaveEntitleId((String)entity.getProperty("leaveEntitleId"));
			sl.setSickLeaveDay((String)entity.getProperty("sickLeaveDay"));
			sl.setSickLeaveType((String)entity.getProperty("sickLeaveType"));
			sl.setSickLeaveYear((String)entity.getProperty("sickLeaveYear"));
			sl.setId(KeyFactory.keyToString(entity.getKey()));
		return sl;
		
	}
	
	public SickLeave getGreaterThanOrEqual(String id){
		
		Filter idfilter = new FilterPredicate("leaveEntitleId",
                FilterOperator.EQUAL,
                id);
		
		Filter lesfilter = new FilterPredicate("sickLeaveType",
                FilterOperator.EQUAL,
                ConstantUtils.GREATER_THAN_OR_EQUAL);
		Filter filter = CompositeFilterOperator.and(idfilter, lesfilter);
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			SickLeave sl = new SickLeave();
			sl.setRegion((String)entity.getProperty("department"));	
			sl.setLeaveEntitleId((String)entity.getProperty("leaveEntitleId"));
			sl.setSickLeaveDay((String)entity.getProperty("sickLeaveDay"));
			sl.setSickLeaveType((String)entity.getProperty("sickLeaveType"));
			sl.setSickLeaveYear((String)entity.getProperty("sickLeaveYear"));
			sl.setId(KeyFactory.keyToString(entity.getKey()));
		return sl;
		
	}
	
	public LeaveEntitlement getLeaveEntitlementByYear(String year){
		Entity e = findEntityByColumn(LeaveEntitlement.class.getSimpleName(), "leaveYear", year);
		LeaveEntitlement let = null;
		if(e!=null){
			let = new LeaveEntitlement(year);
			let.setId(KeyFactory.keyToString(e.getKey()));
			let.setAddBirthdayLeave((String)e.getProperty("addBirthdayLeave"));
			let.setAddCompassionateLeave((String)e.getProperty("addCompassionateLeave"));
			let.setAddExaminationLeave((String)e.getProperty("addExaminationLeave"));
			let.setAddExGratia((String)e.getProperty("addExGratia"));
			let.setAddFPSickLeave((String)e.getProperty("addFPSickLeave"));
			let.setAddPPSickLeave((String)e.getProperty("addPPSickLeave"));
			let.setAddInjuryLeave((String)e.getProperty("addInjuryLeave"));
			let.setAddJuryLeave((String)e.getProperty("addJuryLeave"));
			let.setAddMarriageLeave((String)e.getProperty("addMarriageLeave"));
			let.setAddMaternityLeave((String)e.getProperty("addMaternityLeave"));
			let.setAddPaternityLeave((String)e.getProperty("addPaternityLeave"));
		}
		return let;
	}
	
	public List<LeaveEntitlement> getLeaveEntitlements(){
		List<LeaveEntitlement> results = new ArrayList<LeaveEntitlement>();
		Query q = new Query(LeaveEntitlement.class.getSimpleName());
		for (Entity e : getDatastore().prepare(q).asIterable(FetchOptions.Builder.withDefaults())) {
			String year = (String)e.getProperty("leaveYear");
			LeaveEntitlement let = new LeaveEntitlement(year);
			let.setId(KeyFactory.keyToString(e.getKey()));
			let.setAddBirthdayLeave((String)e.getProperty("addBirthdayLeave"));
			let.setAddCompassionateLeave((String)e.getProperty("addCompassionateLeave"));
			let.setAddExaminationLeave((String)e.getProperty("addExaminationLeave"));
			let.setAddExGratia((String)e.getProperty("addExGratia"));
			let.setAddFPSickLeave((String)e.getProperty("addFPSickLeave"));
			let.setAddPPSickLeave((String)e.getProperty("addPPSickLeave"));
			let.setAddInjuryLeave((String)e.getProperty("addInjuryLeave"));
			let.setAddJuryLeave((String)e.getProperty("addJuryLeave"));
			let.setAddMarriageLeave((String)e.getProperty("addMarriageLeave"));
			let.setAddMaternityLeave((String)e.getProperty("addMaternityLeave"));
			let.setAddPaternityLeave((String)e.getProperty("addPaternityLeave"));
			results.add(let);
		}
		return results;
	}
	
	public String addLeaveEntitlement(String year, String addBirthdayLeave, String addCompassionateLeave, String addExaminationLeave,
			String addInjuryLeave, String addJuryLeave, String addExGratia, String addMarriageLeave,
			String addMaternityLeave, String addPaternityLeave, String addFPSickLeave, String addPPSickLeave){
		Entity e = findEntityByColumn(LeaveEntitlement.class.getSimpleName(), "leaveYear", year);
		if(e==null){
			e = new Entity(LeaveEntitlement.class.getSimpleName());
		}
		e.setProperty("leaveYear",year);
		e.setProperty("addBirthdayLeave",addBirthdayLeave);
		e.setProperty("addCompassionateLeave",addCompassionateLeave);
		e.setProperty("addExaminationLeave",addExaminationLeave);
		e.setProperty("addInjuryLeave",addInjuryLeave);
		e.setProperty("addJuryLeave",addJuryLeave);
		e.setProperty("addExGratia",addExGratia);
		e.setProperty("addMarriageLeave",addMarriageLeave);
		e.setProperty("addMaternityLeave",addMaternityLeave);
		e.setProperty("addPaternityLeave",addPaternityLeave);
		e.setProperty("addFPSickLeave",addFPSickLeave);
		e.setProperty("addPPSickLeave",addPPSickLeave);
		getDatastore().put(e);
		return KeyFactory.keyToString(e.getKey());
	}
	
	public void addSickLeaveEntitle(String sickLeaveDay, String sickLeaveType,
			String sickLeaveYear, String leaveEntitleId) {
		Entity entity = new Entity(SickLeave.class.getSimpleName(), KeyFactory.stringToKey(leaveEntitleId));
		//entity.setProperty("department", department);
		entity.setProperty("sickLeaveDay", sickLeaveDay);
		entity.setProperty("sickLeaveType", sickLeaveType);
		entity.setProperty("sickLeaveYear", sickLeaveYear);
		entity.setProperty("leaveEntitleId", leaveEntitleId);
		
		getDatastore().put(entity);
	}
	
	public void updateSickLeaveEntitle(String sickLeaveDay, String sickLeaveType,
			String sickLeaveYear, String leaveEntitleId){
		/*Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(id));*/
		Query query = new Query(SickLeave.class.getSimpleName());
		query.setAncestor(KeyFactory.stringToKey(leaveEntitleId));
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			Entity sickLeaveEntity = findEntityByKey2(entity.getKey());
			//sickLeaveEntity.setProperty("department", department);
			sickLeaveEntity.setProperty("sickLeaveDay", sickLeaveDay);
			sickLeaveEntity.setProperty("sickLeaveType", sickLeaveType);
			sickLeaveEntity.setProperty("sickLeaveYear", sickLeaveYear);
			sickLeaveEntity.setProperty("leaveEntitleId", leaveEntitleId);
			getDatastore().put(sickLeaveEntity);
		
	}
	
	
	public void deleteSickLeave(String id){
		Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(id));
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
		getDatastore().delete(entity.getKey());
		
			
	}
	
	public SickLeave getSickLeave(String id) {
		
		Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(id));
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
		SickLeave sl = new SickLeave();
		sl.setRegion((String)entity.getProperty("department"));	
		sl.setLeaveEntitleId((String)entity.getProperty("leaveEntitleId"));
		sl.setSickLeaveDay((String)entity.getProperty("sickLeaveDay"));
		sl.setSickLeaveType((String)entity.getProperty("sickLeaveType"));
		sl.setSickLeaveYear((String)entity.getProperty("sickLeaveYear"));
		sl.setId(KeyFactory.keyToString(entity.getKey()));
		
		return sl;
	}
	
	public List<SickLeave> getSickLeaveById(String id) {
		try {
			List<SickLeave> result = new ArrayList<SickLeave>();
		Filter filter = new FilterPredicate("leaveEntitleId",
                FilterOperator.EQUAL,
                id);
		Query query = new Query(SickLeave.class.getSimpleName()).setFilter(filter);
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			SickLeave sl = new SickLeave();
			sl.setRegion((String)entity.getProperty("department"));	
			sl.setLeaveEntitleId((String)entity.getProperty("leaveEntitleId"));
			sl.setSickLeaveDay((String)entity.getProperty("sickLeaveDay"));
			sl.setSickLeaveType((String)entity.getProperty("sickLeaveType"));
			sl.setSickLeaveYear((String)entity.getProperty("sickLeaveYear"));
			sl.setId(KeyFactory.keyToString(entity.getKey()));
			result.add(sl);
		}
		return result;
		
		} catch (Exception e) {
			e.printStackTrace();
			 return null;
		}
	}
	

	public String addLeaveEntitle(String addAnnualLeave, String addMaternityLeave,
			String addBirthdayLeave, String addWeddingLeave, String addCompassionateLeave, String addExGratia,
			String hospitalization) {
		Entity leaveEntitleEntity = new Entity(LeaveEntitle.class.getSimpleName());		
		leaveEntitleEntity.setProperty("addAnnualLeave", addAnnualLeave);
		leaveEntitleEntity.setProperty("addMaternityLeave", addMaternityLeave);
		leaveEntitleEntity.setProperty("addBirthdayLeave", addBirthdayLeave);
		leaveEntitleEntity.setProperty("addWeddingLeave", addWeddingLeave);
		leaveEntitleEntity.setProperty("addCompassionateLeave", addCompassionateLeave);
		leaveEntitleEntity.setProperty("addExGratia", addExGratia);
//		leaveEntitleEntity.setProperty("compensationLeaveExp", compensationLeaveExp);
		leaveEntitleEntity.setProperty("hospitalization", hospitalization);
		getDatastore().put(leaveEntitleEntity);
		return KeyFactory.keyToString(leaveEntitleEntity.getKey());   
	}
	
	public LeaveEntitle getLeaveEntitlebyRegion(String region) {
		LeaveEntitle LeaveEntitleQ = new LeaveEntitle();	
//		try {
//		Filter filter = new FilterPredicate("department",
//                FilterOperator.EQUAL,
//                region);
//		Query query = new Query(LeaveEntitle.class.getSimpleName()).setFilter(filter);
//		Entity entity = getDatastore().prepare(query).asSingleEntity();			
//		if(entity!=null){
//			LeaveEntitleQ.setRegion((String)entity.getProperty("region"));		
//			LeaveEntitleQ.setAddAnnualLeave((String)entity.getProperty("addAnnualLeave"));
//			LeaveEntitleQ.setAddMaternityLeave((String)entity.getProperty("addMaternityLeave"));
//			LeaveEntitleQ.setAddBirthdayLeave((String)entity.getProperty("addBirthdayLeave"));
//			LeaveEntitleQ.setAddWeddingLeave((String)entity.getProperty("addWeddingLeave"));			
//			LeaveEntitleQ.setAddCompassionateLeave((String)entity.getProperty("addCompassionateLeave"));
//			LeaveEntitleQ.setCompensationLeaveExp((String)entity.getProperty("compensationLeaveExp"));
//			LeaveEntitleQ.setHospitalization((String)entity.getProperty("hospitalization"));
//			
//			LeaveEntitleQ.setId(KeyFactory.keyToString(entity.getKey()));
//		}
//		
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//			 return null;
//		}
		return LeaveEntitleQ;
	}


	public List<LeaveEntitle> getLeaveEntitle() {
		Query query = new Query(LeaveEntitle.class.getSimpleName());
		List<LeaveEntitle> results = new ArrayList<LeaveEntitle>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			LeaveEntitle LeaveEntitleQ = new LeaveEntitle();				
			LeaveEntitleQ.setAddAnnualLeave((String)entity.getProperty("addAnnualLeave"));
			LeaveEntitleQ.setAddMaternityLeave((String)entity.getProperty("addMaternityLeave"));
			LeaveEntitleQ.setAddBirthdayLeave((String)entity.getProperty("addBirthdayLeave"));
			LeaveEntitleQ.setAddWeddingLeave((String)entity.getProperty("addWeddingLeave"));			
			LeaveEntitleQ.setAddCompassionateLeave((String)entity.getProperty("addCompassionateLeave"));
			LeaveEntitleQ.setAddExGratia((String)entity.getProperty("addExGratia"));
			//LeaveEntitleQ.setCompensationLeaveExp((String)entity.getProperty("compensationLeaveExp"));
			LeaveEntitleQ.setHospitalization((String)entity.getProperty("hospitalization"));
			LeaveEntitleQ.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(LeaveEntitleQ);
		}
		
		return results;
	}
	
	public void deleteLeaveEntitle() {
		Query query = new Query(LeaveEntitle.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			getDatastore().delete(entity.getKey());
			}
	}
		
	public void deleteLeaveEntitleByKey(String key) {
		Entity leaveEntitle = findEntity(key);
		if(leaveEntitle!=null) {
			getDatastore().delete(leaveEntitle.getKey());
		}
	}
	
	public void deleteLeaveEntitle(String department) {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Query query = new Query(LeaveEntitle.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("department");
				if (tmp.equalsIgnoreCase(department)) {
					getDatastore().delete(entity.getKey());
					txn.commit();
				}
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	/*public void deleteLeaveEntitle(String department) {
		Query query = new Query(LeaveEntitle.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			String tmp = (String)entity.getProperty("department");
			if (tmp.equalsIgnoreCase(department)) {
				getDatastore().delete(entity.getKey());
			}
		}
	}*/

	public String updateLeaveEntitle(String addAnnualLeave, String hospitalization, String addMaternityLeave,
			String addBirthdayLeave, String addWeddingLeave, String addCompassionateLeave, String addExGratia) 
					throws EntityNotFoundException {
//		Filter filter = new FilterPredicate("department",
//                FilterOperator.EQUAL,
//                department);
		Query query = new Query(LeaveEntitle.class.getSimpleName());//.setFilter(filter);
			Entity entity = getDatastore().prepare(query).asSingleEntity() ;
					Entity leaveEntitleEntity = getDatastore().get(entity.getKey());
					leaveEntitleEntity.setProperty("addAnnualLeave", addAnnualLeave);
					leaveEntitleEntity.setProperty("addMaternityLeave", addMaternityLeave);
					leaveEntitleEntity.setProperty("addBirthdayLeave", addBirthdayLeave);
					leaveEntitleEntity.setProperty("addWeddingLeave", addWeddingLeave);
					leaveEntitleEntity.setProperty("addCompassionateLeave", addCompassionateLeave);
					leaveEntitleEntity.setProperty("addExGratia", addExGratia);
					//leaveEntitleEntity.setProperty("compensationLeaveExp", compensationLeaveExp);
					leaveEntitleEntity.setProperty("hospitalization", hospitalization);

					getDatastore().put(leaveEntitleEntity);
					return leaveEntitleEntity.getKey().toString();
	}
}
