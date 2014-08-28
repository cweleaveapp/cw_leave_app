package com.google.appengine.entities;

import java.io.Serializable;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datastore.DataStoreUtil;

public class ActingPerson implements Serializable{
	
	private String id;
	
	private String name;
	
	private Key apKey;
	
	private String duties;
	
	private Key empKey;
	
	private String refNo;
	
	private String decision;
	
	private String reason;

	public ActingPerson() {
	}

	public ActingPerson(String apKey, String duties, Key empKey, String refNo) {
		DataStoreUtil util = new DataStoreUtil();
		Entity e = util.findEntity(apKey);
		if(e!=null){
			this.name = (String)e.getProperty("fullName");
		}
		this.apKey = KeyFactory.stringToKey(apKey);
		this.duties = duties;
		this.empKey = empKey;
		this.refNo = refNo;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public Key getApKey() {
		return apKey;
	}

	public void setApKey(Key apKey) {
		this.apKey = apKey;
	}
	
	public void setApKey(String apKeyString){
		this.apKey = KeyFactory.stringToKey(apKeyString);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the duties
	 */
	public String getDuties() {
		return duties;
	}

	/**
	 * @param duties the duties to set
	 */
	public void setDuties(String duties) {
		this.duties = duties;
	}

	/**
	 * @return the empKey
	 */
	public Key getEmpKey() {
		return empKey;
	}

	/**
	 * @param empKey the empKey to set
	 */
	public void setEmpKey(Key empKey) {
		this.empKey = empKey;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the refNo
	 */
	public String getRefNo() {
		return refNo;
	}

	/**
	 * @param refNo the refNo to set
	 */
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

}
