package com.google.appengine.mct;

import java.util.Comparator;

public class SortByRegion implements Comparator {

	public int compare(Object o1, Object o2) {
		MCEmployee u1 = (MCEmployee) o1;
		MCEmployee u2 = (MCEmployee) o2;
		return u1.getRegion().compareTo(u2.getRegion());
	}
}
