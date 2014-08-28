package com.google.appengine.entities;

import java.io.Serializable;

public class LeaveTypes implements Serializable {
	private String id;
	private String nameEn;
	private String nameTc;
	private String abbreviation;
	
	public LeaveTypes(String nameEn, String nameTc, String abbreviation) {
		this.nameEn = nameEn;
		this.nameTc = nameTc;
		this.abbreviation = abbreviation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameTc() {
		return nameTc;
	}

	public void setNameTc(String nameTc) {
		this.nameTc = nameTc;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
}
