package com.google.appengine.mct;

import java.util.Comparator;

public class SortSupervisors implements Comparator {

	public int compare(Object o1, Object o2) {
		MCSupervisor u1 = (MCSupervisor) o1;
		MCSupervisor u2 = (MCSupervisor) o2;
		return u1.getEmailAddress().compareTo(u2.getEmailAddress());
	}
}
