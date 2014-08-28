package com.google.appengine.mct;

import java.util.Comparator;

public class SortAdministrators implements Comparator {

	public int compare(Object o1, Object o2) {
		Administrator u1 = (Administrator) o1;
		Administrator u2 = (Administrator) o2;
		return u1.getEmailAddress().compareTo(u2.getEmailAddress());
	}
}
