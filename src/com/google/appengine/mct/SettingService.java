package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.datastore.DataStoreUtil;

@SuppressWarnings("serial")
public class SettingService extends DataStoreUtil {

	public String addToSetting(String calServiceAccPass, String sysAdminEmailAdd, String appDomain,
			String appAdminAcc, String appAdminAccPass, String calServiceAcc, String adminEmailAcc,
			String spreadsheetServiceAcc, String spreadsheetServiceAccPass, String emailSenderAcc
			) {
		Key settingKey = KeyFactory.createKey(MCEmployee.class.getSimpleName(), "setting");
		Entity settingEntity = new Entity(Setting.class.getSimpleName(),settingKey);
		settingEntity.setProperty("calServiceAccPass", calServiceAccPass);
		settingEntity.setProperty("sysAdminEmailAdd", sysAdminEmailAdd);
		settingEntity.setProperty("appDomain", appDomain);
		settingEntity.setProperty("appAdminAcc", appAdminAcc);
		settingEntity.setProperty("appAdminAccPass", appAdminAccPass);
		settingEntity.setProperty("calServiceAcc", calServiceAcc);
		settingEntity.setProperty("adminEmailAcc", adminEmailAcc);
		settingEntity.setProperty("spreadsheetServiceAcc", spreadsheetServiceAcc);
		settingEntity.setProperty("spreadsheetServiceAccPass", spreadsheetServiceAccPass);
		settingEntity.setProperty("emailSenderAcc", emailSenderAcc);
		getDatastore().put(settingEntity);
		return KeyFactory.keyToString(settingEntity.getKey());   
	}

	
	public List<Setting> getSetting() {
		Query query = new Query(Setting.class.getSimpleName());
		List<Setting> results = new ArrayList<Setting>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			Setting settingQ = new Setting();
			settingQ.setAdminEmailAcc((String)entity.getProperty("adminEmailAcc"));
			settingQ.setAppAdminAcc((String)entity.getProperty("appAdminAcc"));
			settingQ.setAppAdminAccPass((String)entity.getProperty("appAdminAccPass"));
			settingQ.setAppDomain((String)entity.getProperty("appDomain"));
			settingQ.setCalServiceAcc((String)entity.getProperty("calServiceAcc"));
			settingQ.setCalServiceAccPass((String)entity.getProperty("calServiceAccPass"));
			settingQ.setEmailSenderAcc((String)entity.getProperty("emailSenderAcc"));
			settingQ.setSysAdminEmailAdd((String)entity.getProperty("sysAdminEmailAdd"));
			settingQ.setSpreadsheetServiceAcc((String)entity.getProperty("spreadsheetServiceAcc"));
			settingQ.setSpreadsheetServiceAccPass((String)entity.getProperty("spreadsheetServiceAccPass"));
			settingQ.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(settingQ);
		}
		return results;
	}
	
	
	public void deleteSetting(String propertyName, String value) {
		
			Query query = new Query(History.class.getSimpleName());
			for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
				String tmp = (String)entity.getProperty(propertyName);
				String tmpTime = (String)entity.getProperty(value);
				if (tmp.equalsIgnoreCase(propertyName)) {
					if (tmpTime.equalsIgnoreCase(value)) {
						getDatastore().delete(entity.getKey());
					}
				}
			}
	}
	
	public void updateSetting(String calServiceAccPass, String sysAdminEmailAdd, String appDomain,
			String appAdminAcc, String appAdminAccPass, String calServiceAcc, String adminEmailAcc,
			String spreadsheetServiceAcc, String spreadsheetServiceAccPass, String emailSenderAcc
			) throws EntityNotFoundException {
		
		List<Setting> settingList = getSetting();
		
		if(settingList != null && !settingList.isEmpty()){
			
				Query query = new Query(Setting.class.getSimpleName());
				for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
					Entity settingEntity = getDatastore().get(entity.getKey());
					settingEntity.setProperty("calServiceAccPass", calServiceAccPass);
					settingEntity.setProperty("sysAdminEmailAdd", sysAdminEmailAdd);
					settingEntity.setProperty("appDomain", appDomain);
					settingEntity.setProperty("appAdminAcc", appAdminAcc);
					settingEntity.setProperty("appAdminAccPass", appAdminAccPass);
					settingEntity.setProperty("calServiceAcc", calServiceAcc);
					settingEntity.setProperty("adminEmailAcc", adminEmailAcc);
					settingEntity.setProperty("spreadsheetServiceAcc", spreadsheetServiceAcc);
					settingEntity.setProperty("spreadsheetServiceAccPass", spreadsheetServiceAccPass);
					settingEntity.setProperty("emailSenderAcc", emailSenderAcc);
					getDatastore().put(settingEntity);
				}
			
		}
		else{
			addToSetting( calServiceAccPass,  sysAdminEmailAdd,  appDomain,
					 appAdminAcc,  appAdminAccPass,  calServiceAcc,  adminEmailAcc,
					 spreadsheetServiceAcc,  spreadsheetServiceAccPass,  emailSenderAcc
					 );
		}
		
		
	}
}
