package com.google.appengine.datastore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.mct.Misc;
import com.google.appengine.mct.SortEmpLeaveDetails;

public class EmployeeLeaveDetailsService extends DataStoreUtil {
	
	private static EmployeeLeaveDetailsService instance;
	
	public static EmployeeLeaveDetailsService getInstance() {
	      if(instance == null) {
	    	  instance = new EmployeeLeaveDetailsService();
	      }
	      return instance;
	}

	public String addEmpLeaveDetails(String emailAddress, String year, String lastYearBalance, String entitledAnnual,
			String balance, String sickLeaveFP, String sickLeavePP, String annualLeave, 
			String compassionateLeave, String compensationLeave, String examLeave,String injuryLeave,String juryLeave,
			String marriageLeave, String maternityLeave, String paternityLeave, String noPayLeave) {
		Entity empE = findEntityByColumn(Employee.class.getSimpleName(),"emailAddress",emailAddress);
		Key eK = empE.getKey();
		Query q = new Query(EmployeeLeaveDetails.class.getSimpleName());
		q.setAncestor(eK);
		Entity eld = getDatastore().prepare(q).asSingleEntity();
		if(eld==null){
			eld = new Entity(EmployeeLeaveDetails.class.getSimpleName(),eK);
		}
		eld.setProperty("year",year);
		eld.setProperty("lastYearBalance",lastYearBalance);
		eld.setProperty("entitledAnnual",entitledAnnual);
		eld.setProperty("balance",balance);
		eld.setProperty("sickLeaveFP",sickLeaveFP);
		eld.setProperty("sickLeavePP",sickLeavePP);
		eld.setProperty("annualLeave",annualLeave);
		eld.setProperty("compassionateLeave",compassionateLeave);
		eld.setProperty("compensationLeave",compensationLeave);
		eld.setProperty("examLeave",examLeave);
		eld.setProperty("injuryLeave",injuryLeave);
		eld.setProperty("juryLeave",juryLeave);
		eld.setProperty("marriageLeave",marriageLeave);
		eld.setProperty("maternityLeave",maternityLeave);
		eld.setProperty("paternityLeave",paternityLeave);
		eld.setProperty("noPayLeave",noPayLeave);
		getDatastore().put(eld);
		return KeyFactory.keyToString(eld.getKey());		
	}
	
	public EmployeeLeaveDetails findEmployeeLeaveDetails(String emailAddress, String year){
		Entity empE = findEntityByColumn(Employee.class.getSimpleName(),"emailAddress",emailAddress);
		Key eK = empE.getKey();
		Query q = new Query(EmployeeLeaveDetails.class.getSimpleName());
		q.setAncestor(eK);
		Entity eld = getDatastore().prepare(q).asSingleEntity();
		EmployeeLeaveDetails details = null;
		if(eld!=null){
			details = new EmployeeLeaveDetails();
			details.setId(KeyFactory.keyToString(eld.getKey()));
			details.setName((String)empE.getProperty("fullName"));
			details.setEmailAddress(emailAddress);
			details.setYear((String) eld.getProperty("year"));
			details.setLastYearBalance((String)eld.getProperty("lastYearBalance"));
			details.setEntitledAnnual((String)eld.getProperty("entitledAnnual"));
			details.setBalance((String)eld.getProperty("balance"));			
			details.setSickLeaveFP((String)eld.getProperty("sickLeaveFP"));
			details.setSickLeavePP((String)eld.getProperty("sickLeavePP"));
			details.setAnnualLeave((String)eld.getProperty("annualLeave"));
			details.setBirthdayLeave((String)eld.getProperty("birthdayLeave"));
			details.setCompensationLeave((String)eld.getProperty("compensationLeave"));
			details.setCompassionateLeave((String)eld.getProperty("compassionateLeave"));
			details.setExamLeave((String)eld.getProperty("examLeave"));
			details.setInjuryLeave((String)eld.getProperty("injuryLeave"));
			details.setJuryLeave((String)eld.getProperty("juryLeave"));
			details.setMarriageLeave((String)eld.getProperty("marriageLeave"));
			details.setMaternityLeave((String)eld.getProperty("maternityLeave"));
			details.setPaternityLeave((String)eld.getProperty("paternityLeave"));			
			details.setNoPayLeave((String)eld.getProperty("noPayLeave"));			
		}		
		return details;
	}	
	
	public List<EmployeeLeaveDetails> getEmployeeLeaveDetails() {
		Query query = new Query(EmployeeLeaveDetails.class.getSimpleName());
		List<EmployeeLeaveDetails> results = new ArrayList<EmployeeLeaveDetails>();
		for (Entity eld : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			EmployeeLeaveDetails details = new EmployeeLeaveDetails();
			details.setId(KeyFactory.keyToString(eld.getKey()));
			details.setYear((String)eld.getProperty("year"));
			details.setLastYearBalance((String)eld.getProperty("lastYearBalance"));
			details.setEntitledAnnual((String)eld.getProperty("entitledAnnual"));
			details.setBalance((String)eld.getProperty("balance"));			
			details.setSickLeaveFP((String)eld.getProperty("sickLeaveFP"));
			details.setSickLeavePP((String)eld.getProperty("sickLeavePP"));
			details.setAnnualLeave((String)eld.getProperty("annualLeave"));
			details.setBirthdayLeave((String)eld.getProperty("birthdayLeave"));
			details.setCompensationLeave((String)eld.getProperty("compensationLeave"));
			details.setCompassionateLeave((String)eld.getProperty("compassionateLeave"));
			details.setExamLeave((String)eld.getProperty("examLeave"));
			details.setInjuryLeave((String)eld.getProperty("injuryLeave"));
			details.setJuryLeave((String)eld.getProperty("juryLeave"));
			details.setMarriageLeave((String)eld.getProperty("marriageLeave"));
			details.setMaternityLeave((String)eld.getProperty("maternityLeave"));
			details.setPaternityLeave((String)eld.getProperty("paternityLeave"));			
			details.setNoPayLeave((String)eld.getProperty("noPayLeave"));	
			results.add(details);
		}
		return results;
	}
	
	public String updateEmpLeaveBal(Key empKey, String type, String noOfDays){
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		Query q = new Query(EmployeeLeaveDetails.class.getSimpleName());
		q.setAncestor(empKey);
		List<Entity> list = getDatastore().prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity eld : list){
			String year = (String) eld.getProperty("year");
			if(year.equals(String.valueOf(curYear))){
				eld.setProperty(type,noOfDays);
				Date now = Misc.setCalendarByLocale();
				eld.setProperty("lastUpdate",now);
				Key eldK = getDatastore().put(eld);
				return KeyFactory.keyToString(eldK);
			}
		}
		return null;
	}
	
	// EmployeeLeaveDetails has been updated. Do not use this one
	public String addDetails(String name, String emailAddress, String year, String lastYearBalance, String entitledAnnual, 
			String entitledExgratia, String noPayLeave, String sickLeave, String annualLeave,
			String exgratiaLeave, String compassionateLeave, String birthdayLeave,
			String maternityLeave, String weddingLeave, String others, String balance, String department) {
		Entity employee = findEntityByColumn(Employee.class.getSimpleName(), "emailAddress", emailAddress);
		//Use employee key as parent key
		Key employeeDetailsKey = KeyFactory.createKey(employee.getKey(),EmployeeLeaveDetails.class.getSimpleName(),"employeedetails");
		//Key employeeDetailsKey = KeyFactory.createKey(EmployeeLeaveDetails.class.getSimpleName(), "employeedetails");
		Entity empLeaveDetails = new Entity(EmployeeLeaveDetails.class.getSimpleName(),employeeDetailsKey);
		empLeaveDetails.setProperty("name", name);
		empLeaveDetails.setProperty("emailAddress", emailAddress);
		empLeaveDetails.setProperty("year", year);
		empLeaveDetails.setProperty("lastYearBalance", lastYearBalance);
		empLeaveDetails.setProperty("entitledAnnual", entitledAnnual);
		empLeaveDetails.setProperty("entitledExgratia", entitledExgratia);
		empLeaveDetails.setProperty("noPayLeave", noPayLeave);
		empLeaveDetails.setProperty("sickLeave", sickLeave);
		empLeaveDetails.setProperty("annualLeave", annualLeave);
		empLeaveDetails.setProperty("exgratiaLeave", exgratiaLeave);
		empLeaveDetails.setProperty("compassionateLeave", compassionateLeave);
		empLeaveDetails.setProperty("birthdayLeave", birthdayLeave);
		empLeaveDetails.setProperty("maternityLeave", maternityLeave);
		empLeaveDetails.setProperty("weddingLeave", weddingLeave);
		empLeaveDetails.setProperty("others", others);
		empLeaveDetails.setProperty("balance", balance);
		empLeaveDetails.setProperty("department", department);
		getDatastore().put(empLeaveDetails);
		return KeyFactory.keyToString(empLeaveDetails.getKey());   
	}
	
	

	// EmployeeLeaveDetails has been updated. Do not use this one
	public EmployeeLeaveDetails findEmployeeLeaveDetailsByValue(String columnName, String value, String filterOperator){
		EmployeeLeaveDetails eld = new EmployeeLeaveDetails();
		/*Iterable<Entity> e = listEntities(EmployeeLeaveDetails.class.getSimpleName(), columnName, value, filterOperator);
		for(Entity entity : e){
			eld.setName((String)entity.getProperty("name"));
			eld.setEmailAddress((String)entity.getProperty("emailAddress"));
			eld.setYear((String)entity.getProperty("year"));
			eld.setLastYearBalance((String)entity.getProperty("lastYearBalance"));
			eld.setEntitledAnnual((String)entity.getProperty("entitledAnnual"));
			eld.setNoPayLeave((String)entity.getProperty("noPayLeave"));
			eld.setSickLeaveFP((String)entity.getProperty("sickLeaveFP"));
			eld.setSickLeavePP((String)entity.getProperty("sickLeavePP"));
			eld.setAnnualLeave((String)entity.getProperty("annualLeave"));
			eld.setCompassionateLeave((String)entity.getProperty("compassionateLeave"));
			eld.setBirthdayLeave((String)entity.getProperty("birthdayLeave"));
			eld.setMaternityLeave((String)entity.getProperty("maternityLeave"));
			eld.setMarriageLeave((String)entity.getProperty("weddingLeave"));
			eld.setOthers((String)entity.getProperty("others"));
			eld.setBalance((String)entity.getProperty("balance"));
			eld.setDepartment((String)entity.getProperty("department"));
			eld.setId(KeyFactory.keyToString(entity.getKey()));
		}
*/		return eld;
	}
	


	public void deleteEmployeeLeaveDetails(String id) {
		Transaction txn = getDatastore().beginTransaction();
		try{
			Filter historyFilter = new FilterPredicate("__key__",
                    FilterOperator.EQUAL,
                    KeyFactory.stringToKey(id));
			Query query = new Query(EmployeeLeaveDetails.class.getSimpleName()).setFilter(historyFilter);
			Entity entity = getDatastore().prepare(query).asSingleEntity();
			getDatastore().delete(entity.getKey());
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	// EmployeeLeaveDetails has been updated. Do not use this one
	public void updateEmployeeLeaveDetails(String name, String emailAddress, String year, String lastYearBalance, String entitledAnnual, 
			String entitledExgratia, String noPayLeave, String sickLeave, String annualLeave,
			String exgratiaLeave, String compassionateLeave, String birthdayLeave,
			String maternityLeave, String weddingLeave, String others, String balance, String department) throws EntityNotFoundException {
			EmployeeLeaveDetails empLeaveDetails = findEmployeeLeaveDetails(emailAddress,year);
			/*Entity eld = findEntity(empLeaveDetails.getId());
						eld.setProperty("name", name);
						eld.setProperty("emailAddress", emailAddress);
						eld.setProperty("year", year);
						eld.setProperty("lastYearBalance", lastYearBalance);
						eld.setProperty("entitledAnnual", entitledAnnual);
						eld.setProperty("entitledExgratia", entitledExgratia);
						eld.setProperty("noPayLeave", noPayLeave);
						eld.setProperty("sickLeave", sickLeave);
						eld.setProperty("annualLeave", annualLeave);
						eld.setProperty("exgratiaLeave", exgratiaLeave);
						eld.setProperty("compassionateLeave", compassionateLeave);
						eld.setProperty("birthdayLeave", birthdayLeave);
						eld.setProperty("maternityLeave", maternityLeave);
						eld.setProperty("weddingLeave", weddingLeave);
						eld.setProperty("others", others);
						eld.setProperty("balance", balance);
						eld.setProperty("department", department);
						getDatastore().put(eld);
			*/		
		}
}
