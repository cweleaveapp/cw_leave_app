package com.google.appengine.mct;

import java.io.Serializable;

import javax.persistence.Entity;


/**
 * @author damon
 *
 */
@SuppressWarnings("serial")
@Entity
public class TaskStatus  implements Serializable {
	
	
	private String id;
	private String status;
	private String blobKey;
	
	
	public String getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
