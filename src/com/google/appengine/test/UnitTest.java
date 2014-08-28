package com.google.appengine.test;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;

import com.google.appengine.mct.EmailSetting;
import com.google.appengine.mct.EmailSettingService;
import com.google.appengine.mct.Misc;
import com.google.appengine.util.ConstantUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class UnitTest {
	
	@Test
	public void testcontain(){
		String email = "damon.leong@hkmci.com";
		if("hkmci.com".contains(email)){
			System.out.println("true");
		}
	}
	
	
	public void swapString(){
		String a = "09-01-2014 18:01:01";
		String day = a.substring(0, 2);
		String month = a.substring(3, 5);
		String year = a.substring(6, 10);
		String hour = a.substring(11, 19);
		
		System.out.println(year+"-"+month+"-"+day+" "+hour);
		
	}
	
	
	public void compareDate(){
		String a = "2014-01-08";
		String b = "2014-01-09";
//		String c = "2014-02-01 01:01:01";
//		String d = "2014-01-11 01:01:01";
		
		System.out.println(a.compareTo(b) >= 0);
		
		List<String> myList = new ArrayList<>();
        myList.add(a);
        myList.add(b);
//        myList.add(c);
//        myList.add(d);
         
        Collections.sort(myList, new Comparator<String>(){
 
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
         
        for(int i = 0; i < myList.size(); i++){
            System.out.println(myList.get(i));
        }
		
	}
	
	
	public void replaceString(){
		
		String a = "01-01-2013";
		String year = "2013";
		StringBuilder b = new StringBuilder(a);
		b.replace(a.lastIndexOf(year), a.lastIndexOf(year) + 4, "2014" );
		a = b.toString();
		System.out.println(a);
	}
	
	
	public void stringcompare(){
		
		String array = " Hong Kong, Malaysia, Singapore";
		String compare = "Hong Kong";
		
		String [] a = array.split(",");
		for(int i =0; i < a.length; i++){
			System.out.println(compare.equals(a[i]));
		}
	}
	
	
	public void stringarray(){
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		
		System.out.println(list.toString().replace("[", "").replace("]", ""));
		
		String [] s = list.toString().replace("[", "").replace("]", "").split(",");
		
		List<String> t = new ArrayList<String>(s.length);
		
		for(int i =0; i < t.size(); i++){
			System.out.println(t.get(i));
		}
	}
	
public void defaultlocaletime(){
		TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATE_TIME_FORMAT);
		
		Calendar cal = Calendar.getInstance();
		System.out.println(sdf.format(cal.getTime()));
		sdf.setTimeZone(timeZone);
		System.out.println(sdf.format(cal.getTime()));
		System.out.println(cal.getTimeZone());
		
		
		
		 
		
		
	}
	
	
	
	public void testtime(){
		
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("b");
		list.add("c");
		list.add("");
		list.add("");
		list.add("");
		
//		System.out.println("a : " + Collections.frequency(list, "a"));
		
		int count = 0;
		
		for(int i=0; i <10; i++){
//			System.out.println(++count);
		}
	
		Calendar end = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		
		;
		System.out.println(start.get(Calendar.MONTH));
		
		String s = "10-05-2014";
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		try {
			start.setTime(standardDF.parse("10-05-2014"));
			end.setTime(standardDF.parse("02-04-2013"));
			
			
			System.out.println(String.valueOf(start.getActualMaximum(Calendar.DAY_OF_MONTH))+
			s.substring(2));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void testemail(){
		Misc misc = new Misc();
		EmailSettingService ess = new EmailSettingService();
		for(int i=0; i < ess.getEmailSettingList().size(); i++){
			EmailSetting esetting = ess.getEmailSettingList().get(i);
			
			if(esetting.getRegion().equalsIgnoreCase("Malaysia")){
				System.out.println(esetting.getRegion());
				System.out.println(esetting.getEmailAddress());
//				try {
//					misc.notifyHR(esetting.getEmailAddress(),misc.now(),
//							"Damon Leong", "1", ConstantUtils.ANNUAL_LEAVE,
//							"testing", "", "");
//				} catch (MessagingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
	}

	
	public void testreturnerror(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("one", "one");
		map.put("two", "two");
		map.put("three", "three");
		map.put("four", "four");
		map.put("five", "five");
		
		String searchChar = "11.5";
		
		
		String substring = searchChar.substring(Math.max(searchChar.length() - 2, 0));
		
		System.out.println(substring);
		
		
		List<String []> list = new ArrayList<String[]>();
		list.add(new String []{"approveiugbdsiugbiu3bi23"});
		list.add(new String []{"reject23h9238g98bgi34ngerng"});
		for (String[] arr : list) {
			String value = Arrays.toString(arr);
            System.out.println(value.replace("[", "").replace("]", ""));
            
        }
		String entitledComp = "2.5q";
		
		if(NumberUtils.isNumber(entitledComp)){
			System.out.println("true");
		}
		
		
//		for (Map.Entry<String, String> entry : map.entrySet()) {
//			System.out.println(entry.getKey()+" "+entry.getValue());
//		}
		
//		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
//		Calendar currYear;
//		try {
//			currYear = getDatePart(standardDF.parse("10-01-2013"));
//			
//			
//			
//			System.out.println(currYear.get(Calendar.MONTH));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		
		
	}
	
	public void test(){
		Properties properties = new Properties();
		try {
			
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		
		System.out.println(properties.getProperty("wrong.number.of.days"));
	}
	
	
	public void testdate(){
		
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse("31-01-2013"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date date = new Date();
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date);
		cal2.add(Calendar.MONTH, 2);
		cal2.add(Calendar.DATE, 22);
		
//		System.out.println(cal.get(Calendar.MONTH));
		int year = cal.get(Calendar.YEAR);
//		System.out.println(year-1);
//		System.out.println(cal.getTime());
		
		String name = "eqwe";
		if(StringUtils.isAlphanumericSpace(name)){
			System.out.println(true);
		}
		
	}
	
	
	public void testclosedate(){
		
		if(StringUtils.containsAny("0","11.5")){
			System.out.println("NumberUtils.isDigits(0.5) ");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
//		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(sdf.parse("31-03-2013"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		int year [] = {2011,2012,2013,2014,2009,2010};
		List list = new ArrayList();
		for(int i : year){
			list.add(i);
		}
		
//		Collections.sort(list);
		Collections.reverse(list);
		
		
//		System.out.println(list.get(0));
//		
//		
//		System.out.println(NumberUtils.isNumber("11.5"));
		double actual = 57;
		double originalnumofday = 3.0;
		double newnum = 7;
		
		double total = originalnumofday - newnum; 
//		System.out.println(actual + total);
		try {
		String startdate = "08-02-2013";
		String enddate = "14-02-2013";
		SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy");
		long diffdate = daysBetween(d.parse(startdate),d.parse(enddate));
		long dateremove = 0;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(d.parse(startdate)); 
		List<String> totalDate = new ArrayList<String>();
		for(int i=0; i<diffdate; i++){
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				dateremove += 1;
			}
			totalDate.add(d.format(cal.getTime()));
			cal.add(Calendar.DATE, 1);
		}
		System.out.println(dateremove);
		
		List<String> holiday = new ArrayList<String>();
		holiday.add("2/11/2013");
		holiday.add("2/12/2013");
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(d.parse(startdate));
		
		
		for(String date : holiday){
			for(int i=0; i<diffdate; i++){
				if(d.format(df.parse(date)).equals(totalDate.get(i))){
					dateremove += 1;
				}
			}
			
		}
		System.out.println(dateremove);
		
		System.out.println(diffdate-dateremove);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void retrieveCalendar(){
		String feedUrl = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
		
		try {
			URL metafeedUrl = new URL(feedUrl);
			SpreadsheetEntry spreadSheetEntry = null;
			Calendar currentYear = Calendar.getInstance();
			SpreadsheetService service = new SpreadsheetService(ConstantUtils.MCKL_PUBLIC_HOLIDAYS+currentYear.get(Calendar.YEAR));
			service.setUserCredentials("LeaveAppsAdmin@gab.hkmci.com", "zKvWo5i)y{i+E5Om");
			SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
			String spreadsheetName = ConstantUtils.MCKL_PUBLIC_HOLIDAYS+"2013";
			
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			for (int i = 0; i < spreadsheets.size(); i++) {
				SpreadsheetEntry entry = spreadsheets.get(i);
				if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
					spreadSheetEntry = entry;
					break;
				}
			}
			List<String> holidayList = new ArrayList<String>();
			if(spreadSheetEntry != null){
				List<WorksheetEntry> worksheets = spreadSheetEntry.getWorksheets();
				if(worksheets.size() > 0) {
					WorksheetEntry worksheet = worksheets.get(0);
					URL listFeedUrl = worksheet.getListFeedUrl();
					ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
					
					 for (ListEntry row : listFeed.getEntries()) {
					      for (String tag : row.getCustomElements().getTags()) {
					    	  if(tag.equals("date"))
					    		  holidayList.add(row.getCustomElements().getValue(tag));
					      }
					      
					    }
				}
			}
			
			String startdate = "08-02-2013";
			String enddate = "14-02-2013";
			SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy");
			long diffdate = daysBetween(d.parse(startdate),d.parse(enddate));
			long dateremove = 0;
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(d.parse(startdate)); 
			for(int i=0; i<diffdate; i++){
				if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					dateremove += 1;
				}
				cal.add(Calendar.DATE, 1);
			}
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(d.parse(startdate)); 
			
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			if(!holidayList.isEmpty()){
				for(String date : holidayList){
					for(int i=0; i< diffdate; i++){
						if(df.parse(date).compareTo(cal2.getTime()) == 0){
							dateremove += 1;
						}
						cal2.add(Calendar.DATE, 1);
					}
					
				}
			}
			
			
			System.out.println(diffdate-dateremove);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Calendar getDatePart(Date date){
	    Calendar cal = Calendar.getInstance();       // get calendar instance
	    cal.setTime(date);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
	    cal.set(Calendar.MINUTE, 0);                 // set minute in hour
	    cal.set(Calendar.SECOND, 0);                 // set second in minute
	    cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second
	    return cal;
	    
	}
	
	public static long daysBetween(Date startDate, Date endDate) {
		  Calendar sDate = getDatePart(startDate);
		  Calendar eDate = getDatePart(endDate);

		  long daysBetween = 1;
		 
			  while (sDate.before(eDate)) {
			      sDate.add(Calendar.DAY_OF_MONTH, 1);
			      daysBetween++;
			  }
			  
			  return daysBetween;
		  
	}
	
}
