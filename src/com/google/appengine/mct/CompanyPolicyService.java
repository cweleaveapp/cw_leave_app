package com.google.appengine.mct;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.api.datastore.Text;

public class CompanyPolicyService extends DataStoreUtil {
	
	public CompanyPolicy savePolicy(CompanyPolicy companyPolicy){
		
		Entity entity = new Entity(CompanyPolicy.class.getSimpleName());
		entity.setProperty("content", new Text(companyPolicy.getContent()));
		entity.setProperty("createdBy", companyPolicy.getCreatedBy());
		entity.setProperty("time", companyPolicy.getTime());
		
		getDatastore().put(entity);
		
		return findCompanyPolicyById(KeyFactory.keyToString(entity.getKey()));
		
	}
	
	public CompanyPolicy updatePolicy(CompanyPolicy companyPolicy){
		Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                FilterOperator.EQUAL,
                KeyFactory.stringToKey(companyPolicy.getId()));
		Query query = new Query(CompanyPolicy.class.getSimpleName()).setFilter(filter);
		Entity entity = getDatastore().prepare(query).asSingleEntity();
			Entity policyEntity = findEntityByKey2(entity.getKey());
			policyEntity.setProperty("content", new Text(companyPolicy.getContent()));
			policyEntity.setProperty("createdBy", companyPolicy.getCreatedBy());
			policyEntity.setProperty("time", companyPolicy.getTime());
			getDatastore().put(policyEntity);
		
			return findCompanyPolicyById(KeyFactory.keyToString(policyEntity.getKey()));
	}
	
	public CompanyPolicy findCompanyPolicyById(String id){
		CompanyPolicy companyPolicy = new CompanyPolicy();
		Entity entity = findEntity(id); 
		Text content = (Text)entity.getProperty("content");
		companyPolicy.setContent(content.getValue());
		companyPolicy.setCreatedBy((String)entity.getProperty("createdBy"));
		companyPolicy.setTime((Date)entity.getProperty("time"));
		companyPolicy.setCreatedBy((String)entity.getProperty("createdBy"));
		companyPolicy.setId(KeyFactory.keyToString(entity.getKey()));
		return companyPolicy;
	}

	
	public CompanyPolicy getCompanyPolicy(){
		CompanyPolicy companyPolicy = new CompanyPolicy();
		Query query = new Query(CompanyPolicy.class.getSimpleName());
		Entity entity = getDatastore().prepare(query).asSingleEntity();
		if(entity != null){
			Text content = (Text)entity.getProperty("content");
			companyPolicy.setContent(content.getValue());
			companyPolicy.setCreatedBy((String)entity.getProperty("createdBy"));
			companyPolicy.setTime((Date)entity.getProperty("time"));
			companyPolicy.setCreatedBy((String)entity.getProperty("createdBy"));
			companyPolicy.setId(KeyFactory.keyToString(entity.getKey()));
		}
		
		return companyPolicy;
	}
}
