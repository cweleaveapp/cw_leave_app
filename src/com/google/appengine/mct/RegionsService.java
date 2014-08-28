package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.entities.Department;

public class RegionsService extends DataStoreUtil {

	private static final long serialVersionUID = 1L;

	public String addRegions(String region, String regionAbbreviation, String regionCalendarURL, String regionSalesOps) {
		Entity regionEntity = new Entity(Regions.class.getSimpleName());
		regionEntity.setProperty("region", region);
		regionEntity.setProperty("regionAbbreviation", regionAbbreviation);
		regionEntity.setProperty("regionCalendarURL", regionCalendarURL);
		regionEntity.setProperty("regionSalesOps", regionSalesOps);
		getDatastore().put(regionEntity);
		return KeyFactory.keyToString(regionEntity.getKey());   
	}

	
	
	public List<Regions> getRegions(){
		List<Regions> regions = new ArrayList<Regions>();
		return regions;
	}
	
	public void deleteRegion() {
			Query query = new Query(Regions.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				getDatastore().delete(entity.getKey());
				}
	}


	public void deleteRegion(String region) {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Query query = new Query(Regions.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("region");
				if (tmp.equalsIgnoreCase(region)) {
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

	public void updateRegion(String region, String regionAbbreviation, String regionCalendarURL, String regionSalesOps) throws EntityNotFoundException {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Query query = new Query(Regions.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty("region");
				if (tmp.equalsIgnoreCase(region)) {
					Entity reg = getDatastore().get(entity.getKey());
					reg.setProperty("region", region);
					reg.setProperty("regionAbbreviation", regionAbbreviation);
					reg.setProperty("regionCalendarURL", regionCalendarURL);
					reg.setProperty("regionSalesOps", regionSalesOps);
					getDatastore().put(reg);
					txn.commit();
				}
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
