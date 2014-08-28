package com.google.appengine.mct;

import java.util.Comparator;

public class SortRegion implements Comparator {

	public int compare(Object o1, Object o2) {
		Regions u1 = (Regions) o1;
		Regions u2 = (Regions) o2;
		return u1.getRegion().compareTo(u2.getRegion());
	}
}
