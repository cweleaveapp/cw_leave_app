package com.google.appengine.mct;

import java.util.Comparator;

import com.google.appengine.entities.EmployeeLeaveDetails;

public class SortEmpLeaveDetails implements Comparator {

	public int compare(Object o1, Object o2) {
		EmployeeLeaveDetails u1 = (EmployeeLeaveDetails) o1;
		EmployeeLeaveDetails u2 = (EmployeeLeaveDetails) o2;
		return u1.getName().compareTo(u2.getName());
	}
}
