package com.google.appengine.mct;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class SortRegionalHolidays implements Comparator {

	public int compare(Object o1, Object o2) {
		RegionalHolidays u1 = (RegionalHolidays) o1;
		RegionalHolidays u2 = (RegionalHolidays) o2;
		DateFormat formatter; 
		Date d1, d2;
		int regionTmp = 0;
		formatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			regionTmp = u1.getRegion().compareTo(u2.getRegion());
			if (regionTmp != 0) {
				return regionTmp;
			} else {
				d1 = (Date)formatter.parse(u1.getDate());
				d2 = (Date)formatter.parse(u2.getDate());
				long n1 = d1.getTime();
				long n2 = d2.getTime();
				if (n1 < n2) {
					return -1;
				}
				else if (n1 > n2) {
					return 1;
				}
			}
		} catch (ParseException e) {
			System.err.println("SortLeaveQueue error: " + e.getMessage());
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
}
