package com.google.appengine.mct;

import java.io.Serializable;

public class Notify implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String addNotifyFreq;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddNotifyFreq() {
		return addNotifyFreq;
	}
	public void setAddNotifyFreq(String addNotifyFreq) {
		this.addNotifyFreq = addNotifyFreq;
	}

}
