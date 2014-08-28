package com.google.appengine.mct;

public class Regions {

	private static final long serialVersionUID = 1L;
	private String id;
	private String region;
	private String regionAbbreviation;
	private String regionCalendarURL;
	private String regionSalesOps;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRegionAbbreviation() {
		return regionAbbreviation;
	}
	public void setRegionAbbreviation(String regionAbbreviation) {
		this.regionAbbreviation = regionAbbreviation;
	}
	public String getRegionCalendarURL() {
		return regionCalendarURL;
	}
	public void setRegionCalendarURL(String regionCalendarURL) {
		this.regionCalendarURL = regionCalendarURL;
	}
	public String getRegionSalesOps() {
		return regionSalesOps;
	}
	public void setRegionSalesOps(String regionSalesOps) {
		this.regionSalesOps = regionSalesOps;
	}

}
