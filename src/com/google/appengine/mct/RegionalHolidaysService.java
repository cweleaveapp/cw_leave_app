package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.datastore.DataStoreUtil;

public class RegionalHolidaysService extends DataStoreUtil {

	
	public String addRegionalHoliday(String date, String description, String region) {
		Entity holidaysEntity = new Entity(RegionalHolidays.class.getSimpleName());
		holidaysEntity.setProperty("date", date);
		holidaysEntity.setProperty("description", description);
		holidaysEntity.setProperty("region", region);
		getDatastore().put(holidaysEntity);
		return KeyFactory.keyToString(holidaysEntity.getKey());   
	}
	
	
	public List<RegionalHolidays> getRegionalHolidays() {
		Query query = new Query(RegionalHolidays.class.getSimpleName());
		List<RegionalHolidays> results = new ArrayList<RegionalHolidays>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			RegionalHolidays holidays = new RegionalHolidays();
			holidays.setDate((String)entity.getProperty("date"));
			holidays.setDescription((String)entity.getProperty("description"));
			holidays.setRegion((String)entity.getProperty("region"));
			holidays.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(holidays);
		}
		/* Sort by region */
		Collections.sort(results, new SortRegionalHolidays());
		return results;
	}
	
	public void deleteRegionalHolidays() {
		Query query = new Query(RegionalHolidays.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				getDatastore().delete(entity.getKey());
			}
			
	}
	
	
	public void deleteRegionalHolidays(String id) {
		Transaction txn = getDatastore().beginTransaction();
		try {
			Filter regionalHolidaysFilter = new FilterPredicate("__key__",
                    FilterOperator.EQUAL,
                    KeyFactory.stringToKey(id));
			Query query = new Query(RegionalHolidays.class.getSimpleName()).setFilter(regionalHolidaysFilter);
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
