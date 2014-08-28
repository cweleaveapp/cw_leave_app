package com.google.appengine.mct;

import java.io.Serializable;
import java.util.Date;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class CompanyPolicy implements Serializable {
	
	private String id;
	private Text content;
	private Date time;
	private String createdBy;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void setContent(String content) {
		this.content = new Text(content);
	}

	public String getContent() {
		if(content != null){
			return this.content.getValue();
		}
		return "";
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
}
