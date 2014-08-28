package com.google.appengine.mct;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.google.appengine.entities.LeaveQueue;

public class SortLeaveQueue implements Comparator {

	public int compare(Object o1, Object o2) {
		LeaveQueue u1 = (LeaveQueue) o1;
		LeaveQueue u2 = (LeaveQueue) o2;
		DateFormat formatter; 
		Date d1, d2; 
		formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		try {
			d1 = (Date)formatter.parse(u1.getTime());
			d2 = (Date)formatter.parse(u2.getTime());
			long n1 = d1.getTime();
			long n2 = d2.getTime();
			if (n1 < n2) return -1;
			else if (n1 > n2) return 1;
			else return 0;
		} catch (ParseException e) {
			System.err.println("SortLeaveQueue error: " + e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}
}
