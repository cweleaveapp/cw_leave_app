package com.google.appengine.mct;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.datastore.DataStoreUtil;

public class EmailSettingService extends DataStoreUtil {
	
	public EmailSetting findEmailSettingByColumnName(String columnName, String value){
		Entity entity =  findEntities(EmailSetting.class.getSimpleName(),columnName, value);
		EmailSetting emailSetting = new EmailSetting();
		if(entity != null){
			emailSetting.setEmailAddress((String)entity.getProperty("emailAddress"));
			emailSetting.setRegion((String)entity.getProperty("region"));
			emailSetting.setId(KeyFactory.keyToString(entity.getKey()));
		}
		
		return emailSetting;
	}
	
	public void updateEmailSetting(String emailAddress, String region){
		Entity emailSettingEntity = findEntities(EmailSetting.class.getSimpleName(),"emailAddress", emailAddress);
		emailSettingEntity.setProperty("emailAddress", emailAddress);
		emailSettingEntity.setProperty("region", region);
		getDatastore().put(emailSettingEntity);
	}
	
	public String addEmailSetting(String emailAddress, String region){
		Key emailsettingKey = KeyFactory.createKey(EmailSetting.class.getSimpleName(), "emailsetting");
		Entity emailSettingEntity = new Entity(EmailSetting.class.getSimpleName(),emailsettingKey);
		emailSettingEntity.setProperty("emailAddress", emailAddress);
		emailSettingEntity.setProperty("region", region);
		getDatastore().put(emailSettingEntity);
		return KeyFactory.keyToString(emailSettingEntity.getKey());   
	}
	
	public List<EmailSetting> getEmailSettingList() {
		Query query = new Query(EmailSetting.class.getSimpleName());
		List<EmailSetting> results = new ArrayList<EmailSetting>();
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			EmailSetting emailSetting = new EmailSetting();
			emailSetting.setEmailAddress((String)entity.getProperty("emailAddress"));
			emailSetting.setRegion((String)entity.getProperty("region"));
			emailSetting.setId(KeyFactory.keyToString(entity.getKey()));
			results.add(emailSetting);
		}

		return results;
	}
	
	public void deleteEmailSetting(String emailAddress) {
		Query query = new Query(EmailSetting.class.getSimpleName());
		for (Entity entity : getDatastore().prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			String tmp = (String)entity.getProperty("emailAddress");
			if (tmp.equalsIgnoreCase(emailAddress)) {
				getDatastore().delete(entity.getKey());
			}
		}
	}


}
