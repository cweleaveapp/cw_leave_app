package com.google.appengine.datastore;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.entities.ActingPerson;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveEntitlement;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;
import com.google.appengine.enums.LeaveType;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.mct.LeaveEntitle;
import com.google.appengine.mct.Misc;
import com.google.appengine.mct.NewLeaveRequest;
import com.google.appengine.mct.RegionalHolidays;
import com.google.appengine.mct.RegionalHolidaysService;
import com.google.appengine.mct.SickLeave;
import com.google.appengine.util.ConstantUtils;
import com.google.appengine.util.MyProperties;

/**
 * Manage Leave request
 */
public class LeaveRequestService extends DataStoreUtil {

	private static LeaveRequestService instance;
	
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
	
	private static final Logger log = Logger.getLogger(LeaveRequestService.class);
	
	private static Calendar calendar;
	
	private static SimpleDateFormat cwDateFormatter = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_CW);
	
	private static boolean valid;
	
	private static List<String> specialAllowances= new ArrayList<String>(); 
	
	private static String errorMsg = "";
	
	public static LeaveRequestService getInstance() {
	      if(instance == null) {
	    	  instance = new LeaveRequestService();
	    	  specialAllowances.add("exgratiaLeave");
	    	  specialAllowances.add("satOff");
	      }
	      return instance;
	}
	
	public LeaveRequest makeNewLeaveRequest(String empKey, int leaveTypeId, String supervisor, String approver, String start,
			String end, String remarks){
		double noOfDays = daysBetween(start, end);
		String state = validateLeaveStart(leaveTypeId, start, end);
		long currentTimeStamp = calendar.getInstance().getTimeInMillis();
		String ref = String.valueOf(currentTimeStamp);				
		for(LeaveType t : LeaveType.values()){
			if(t.getId()==leaveTypeId){
				ref = t.getAbbreviation() + ref;	
			}
		}
		if(state.equals("late")){
			ref = ref + "L";
		} else if(state.equals("retrospective")){
			ref= ref + "R";
		}		
		
		DateFormat format = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		try {
			Date startDate = cwDateFormatter.parse(start);
			Date endDate = cwDateFormatter.parse(end);
			Calendar sDate = getDatePart(startDate);
			Calendar eDate = getDatePart(endDate);
			start = format.format(startDate);
			end = format.format(eDate.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		
		LeaveRequest leaveRequest = new LeaveRequest(KeyFactory.stringToKey(empKey), leaveTypeId, supervisor, approver, start, end, noOfDays, remarks, ref);
		leaveRequest.setCreateDate(Misc.setCalendarByLocale());
		leaveRequest.setLeaveStatus(LeaveStatus.PENDING_REVIEW.id);
		return leaveRequest;
	}	
		
	public static String validateLeaveStart(int leaveType, String startDayStr, String endDayStr){
		String state = "invalid";
		Calendar toDay = calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_CW);		
		try {
			Date start = cwDateFormatter.parse(startDayStr);
			Date end = cwDateFormatter.parse(endDayStr);
			Calendar calStart = getDatePart(start);
			Calendar calEnd = getDatePart(end);
			if(calStart.after(calEnd)){
				return state;
			}			
			if(leaveType==LeaveType.SICK_LEAVE_FP.id || leaveType==LeaveType.SICK_LEAVE_PP.id){						
				if(calStart.before(toDay)){
					state = "valid";
				}
				if(calStart.after(toDay)){
					return state;
				} 
				
			} else {
				if(calStart.before(toDay)){					
					state = "retrospective";
				}
				if(calStart.after(toDay)){
					String toDayStr = sdf.format(toDay.getTime());
					long diff = daysBetween(toDayStr,startDayStr);
					if(diff < 5){
						state = "late";
					} else {
						state = "valid";
					}					
				} 
				
			}			
		} catch (ParseException pe) {
			log.error("validateLeaveStart ParseException : " + pe);
			return state;
		}
		//state = "valid";
		return state;
	}

	public boolean validateLeaveBalance(Employee emp, EmployeeLeaveDetails eldt, int leaveType, String startDateStr, String endDateStr, HttpServletRequest request) {
		double balance = Double.valueOf(eldt.getBalance());
		boolean bvalid = false;
		String state = validateLeaveStart(leaveType,startDateStr,endDateStr);
		if(state.equals("invalid")){
			request.setAttribute("errorMsg", MyProperties.getErrorMsg("invalid.start.date"));
			return bvalid;
		}				
		
		int year = calendar.getInstance().get(Calendar.YEAR);
		double noOfDays = (double)daysBetween(startDateStr, endDateStr);
		LeaveEntitlement let = LeaveEntitleService.getInstance().getLeaveEntitlementByYear(String.valueOf(year));
		if(noOfDays < 1){
			request.setAttribute("errorMsg", "No of leave day should be at least 1 day");
			return bvalid;
		}
		switch (leaveType){
		
		case 1:
		// Annual Leave
		balance = Double.parseDouble(eldt.getEntitledAnnual()) - Double.parseDouble(eldt.getAnnualLeave());		
		if(balance > noOfDays){
			bvalid = true;
		}
		break;
		case 2: 
		// Birthday Leave
		if(!isBirthDayLeaveAvailable(emp.getBirthDate())){
			//errorMsg = errorMsg + MyProperties.getErrorMsg("invalid.birthday.leave");
			request.setAttribute("errorMsg", MyProperties.getErrorMsg("invalid.birthday.leave"));
			return bvalid;
		}
		bvalid = validateBirthdayLeave(emp.getBirthDate(), emp.getDepartment(), startDateStr, endDateStr);
		return bvalid;
		// Compassionate Leave
		case 3:
		balance = Double.parseDouble(let.getAddCompassionateLeave()) - Double.parseDouble(eldt.getCompassionateLeave());
		break;
		case 5:
	    // Examination Leave
		balance = Double.parseDouble(let.getAddExaminationLeave()) - Double.parseDouble(eldt.getExamLeave());
		break;
		case 6:
		// Injury Leave
		balance = Double.parseDouble(let.getAddInjuryLeave()) - Double.parseDouble(eldt.getInjuryLeave());
		break;
		case 7:
		// Jury Leave
		balance = Double.parseDouble(let.getAddJuryLeave()) - Double.parseDouble(eldt.getJuryLeave());
		break;
		// Marriage Leave
		case 8:
		balance = Double.parseDouble(let.getAddMarriageLeave()) - Double.parseDouble(eldt.getMarriageLeave());
		break;
		// Maternity Leave
		case 9:
		balance = Double.parseDouble(let.getAddMaternityLeave()) - Double.parseDouble(eldt.getMaternityLeave());
		break;
		// Paternity Leave
		case 10:
		balance = Double.parseDouble(let.getAddPaternityLeave()) - Double.parseDouble(eldt.getPaternityLeave());
		break;
		case 12:
		// Sick Leave (FP)
		balance = Double.parseDouble(let.getAddFPSickLeave()) - Double.parseDouble(eldt.getSickLeaveFP());
		break;
		case 13:
		// Sick Leave (PP)
		balance = Double.parseDouble(let.getAddPPSickLeave()) - Double.parseDouble(eldt.getSickLeavePP());
		break;			
		}
		if(balance > noOfDays){
			bvalid = true;
		}
		return bvalid;
	}

	public boolean validateSickLeave(double sickLeaveUsed, String hireddate, String empDept, String startDayStr, String endDayStr){
		LeaveEntitle let = LeaveEntitleService.getInstance().getLeaveEntitle().get(0);
		Calendar cal = Calendar.getInstance();
		Calendar curr = Calendar.getInstance();
		SimpleDateFormat sdtf = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
   		try {
			cal.setTime(sdtf.parse(hireddate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		int yearCal = curr.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
   		long numOfDays = daysBetween(startDayStr,endDayStr);
   		sickLeaveUsed = sickLeaveUsed + (double)numOfDays;
		int allowSickLeave = 0;
		List<SickLeave> sickLeaveList = LeaveEntitleService.getInstance().getSickLeaveById(let.getId());
			
			for(SickLeave sl : sickLeaveList){
				if(ConstantUtils.LESS_THAN.equals(sl.getSickLeaveType())){
					if(yearCal < Integer.parseInt(sl.getSickLeaveYear())){
						allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
					}
				}
				else if(ConstantUtils.LESS_THAN_OR_EQUAL.equals(sl.getSickLeaveType())){
					SickLeave lessThan = LeaveEntitleService.getInstance().getLessThanYear(let.getId());
					if(lessThan != null ){
							if(yearCal <= Integer.parseInt(sl.getSickLeaveYear()) &&
									yearCal >= Integer.parseInt(lessThan.getSickLeaveYear())){
								allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
							}
					}
					
				}
				else if(ConstantUtils.GREATER_THAN.equals(sl.getSickLeaveType())){
					if(yearCal > Integer.parseInt(sl.getSickLeaveYear())){
						allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
					}
				}
				else if(ConstantUtils.GREATER_THAN_OR_EQUAL.equals(sl.getSickLeaveType())){
					if(yearCal >= Integer.parseInt(sl.getSickLeaveYear())){
						allowSickLeave = Integer.parseInt(sl.getSickLeaveDay());
					}
				}
			}
			
		if(allowSickLeave <= 0){
			valid = false;
			log.error("System error pleace contact administrator");
		}	
			
		if(sickLeaveUsed < allowSickLeave){
			valid = true;
		} else {
			valid = false;
		}
		return valid;
	}
	
	public boolean validateBirthdayLeave(String birthDate, String empDept, String startDayStr, String endDayStr){
		boolean valid = false;
		LeaveEntitle let = LeaveEntitleService.getInstance().getLeaveEntitle().get(0);
		double num =  daysBetween(startDayStr, endDayStr);
		if (num == Integer.parseInt(let.getAddBirthdayLeave())) {
			Vector availableDates = new Vector();
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			Calendar time = new GregorianCalendar();
			Date tmpDate = new Date();
			Date date1 = new Date();
			
			String strDate = "";
			String tmp = birthDate.substring(0, birthDate.lastIndexOf("-")+1);
			Calendar cal = Calendar.getInstance();
			birthDate = tmp + cal.get(Calendar.YEAR);
			try {
				date1 = (Date)format.parse(birthDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time.setTime(date1);
			if (time.get(Calendar.DAY_OF_WEEK)==7) {	/* Saturday */
				tmpDate.setTime(date1.getTime() + 2 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			} else if (time.get(Calendar.DAY_OF_WEEK)==6) {		/* Friday */
				availableDates.add(birthDate);
				tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() + 3 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			} else if (time.get(Calendar.DAY_OF_WEEK)==5) {		/* Thursday */
				availableDates.add(birthDate);
				tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			} else if (time.get(Calendar.DAY_OF_WEEK)==4) {		/* Wednesday */
				availableDates.add(birthDate);
				tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			} else if (time.get(Calendar.DAY_OF_WEEK)==3) {		/* Tuesday */
				availableDates.add(birthDate);
				tmpDate.setTime(date1.getTime() - 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			} else if (time.get(Calendar.DAY_OF_WEEK)==2) {		/* Monday */
				availableDates.add(birthDate);
				tmpDate.setTime(date1.getTime() - 3 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			} else if (time.get(Calendar.DAY_OF_WEEK)==1) {		/* Sunday */
				tmpDate.setTime(date1.getTime() - 2 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
				tmpDate.setTime(date1.getTime() + 1 * 24 * 60 * 60 * 1000);
				strDate = formatter.format(tmpDate);
				availableDates.add(strDate);
			}
			
			//SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_CW);
			Calendar y = Calendar.getInstance();
			try {
				y.setTime(cwDateFormatter.parse(startDayStr));
				
			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			// add future year, if apply for future year birthday, only allow future 1 year
			String nextYear = String.valueOf(y.get(Calendar.YEAR) + 1);
			String thisYear =  String.valueOf(y.get(Calendar.YEAR));
			String nextYearStartDate = "";
			String startDay = format.format(y.getTime());
			StringBuilder b = new StringBuilder(startDay);
			b.replace(startDay.lastIndexOf(thisYear),startDay.lastIndexOf(thisYear)+ 4, nextYear );
			nextYearStartDate = b.toString();
			
			for (int i=0; i<availableDates.size(); i++) {
				if (startDay.equalsIgnoreCase(availableDates.elementAt(i).toString()) 
						|| nextYearStartDate.equals(availableDates.elementAt(i).toString())) {
					valid = true;
				}
			}
		}
		return valid;
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
	
	/**
	 * Calculate no of leave day from start day and end day (exclude holidays)
	 * Use adjustLeaveDays when half day off is set.
	 * @return daysBetween
	 */	
	public static long daysBetween(String startDateStr, String endDateStr) {
		long daysBetween = 1;
		long dateremove = 0;
		try {
			Date startDate = cwDateFormatter.parse(startDateStr);
			Date endDate = cwDateFormatter.parse(endDateStr);
			Calendar sDate = getDatePart(startDate);
			Calendar eDate = getDatePart(endDate);
			while (sDate.before(eDate)) {
				sDate.add(Calendar.DAY_OF_MONTH, 1);
				daysBetween++;
			}	
		
			log.debug("Time Difference : " + daysBetween);
			List<String> totalDate = new ArrayList<String>();
			Calendar cal= Calendar.getInstance();
			cal.setTime(startDate);
			for(int i=0; i<daysBetween; i++){
				if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					dateremove += 1;
				}
				totalDate.add(cwDateFormatter.format(sDate.getTime()));
				cal.add(Calendar.DATE, 1);
			}
			log.debug("no of day removed : " + dateremove);
		
			daysBetween = daysBetween - dateremove;
			log.debug("daysBetween after deduction : " + daysBetween);
			
			List<String> holidayList = getHolidayList();
			if(!holidayList.isEmpty()){
				for(String date : holidayList){
					for(int i=0; i< daysBetween; i++){
						if(date.equals(totalDate.get(i))){
							dateremove += 1;
						}
					}
					
				}
			}
		} catch (ParseException pe) {
			log.error("daysBetween ParseException : " + pe);
		}
		return daysBetween;		  
	}
	
	public static double pendingDeducedByType(List<LeaveRequest> list, LeaveType type){
		double days = 0;
		for(LeaveRequest l : list){
			if(!l.getLeaveStatus().equals(LeaveStatus.HR_APPROVED) && 
					!l.getLeaveStatus().equals(LeaveStatus.HR_REJECTED) &&
							!l.getLeaveStatus().equals(LeaveStatus.CANCELLED)){
				if(l.getLeaveType().equals(type)){
					days = days + l.getNoOfDays();
				}
			}			
		}
		return days;
	} 
	
	public String saveLeaveRequest(LeaveRequest leave, String empId){
		Key empKey = KeyFactory.stringToKey(empId);
		Entity leaveEntity;
		Iterable<Entity> e = listEntities(LeaveRequest.class.getSimpleName(),"ref",leave.getRef(), ConstantUtils.EQUAL);
		if(e.iterator().hasNext()){
			leaveEntity = e.iterator().next();
		} else {
			leaveEntity = new Entity(LeaveRequest.class.getSimpleName(),empKey);
		}
		Field[] fields = LeaveRequest.class.getDeclaredFields();
		for(Field f : fields) {
			try {
				if(f.getName().equalsIgnoreCase("aplist")){
					if(leave.getApList()!=null){
						List<Key> apListKey = ActorService.getInstance().saveActingPerson(leave.getApList());
						leaveEntity.setProperty("apList",apListKey);
					}					
				}
				if(f.getName().equalsIgnoreCase("leavetype")){
					leaveEntity.setProperty("LeaveType", leave.getLeaveType().getId());
				} else if(f.getName().equalsIgnoreCase("leavestatus")){
					leaveEntity.setProperty("LeaveStatus", leave.getLeaveStatus().getId());
				} else if(f.getClass().getSimpleName().equals("Date")){
					leaveEntity.setProperty(f.getName(), ((Date)f.get(leave)).getTime());
				} else if(f.getClass().getSimpleName().equals("List")){
					List l = (List)f.get(leave);
					if(l.size() > 0){
						leaveEntity.setProperty(f.getName(), l);
					}					
				} else {
					if(f.get(leave)!=null){
						leaveEntity.setProperty(f.getName() , f.get(leave));
					}					
				}				
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				// TODO Auto-generated catch block
				log.error("LeaveRequestService saveLeaveRequestException : " + ex.getMessage());
			}
		}
//		if(leave.getApList()!=null && leave.getApList().size()>0){
//			List<String> apListKey = ActorService.getInstance().saveActingPerson(leave.getApList());
//			leaveEntity.setProperty("apList",apListKey);
//		}
		Key leaveKey = getDatastore().put(leaveEntity);	
		return KeyFactory.keyToString(leaveKey);
	}
	
	public List<String> getSpecialAllowanceType(){
		return specialAllowances;
	}
	
	public List<LeaveRequest> getAllLeaveRequests(String empKey){
		Query query = new Query(LeaveRequest.class.getSimpleName());
		query.setAncestor(KeyFactory.stringToKey(empKey));
		List<LeaveRequest> requests = new ArrayList<LeaveRequest>();
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		if(!results.isEmpty()){
			for(Entity r: results){
				long lt = (long) r.getProperty("LeaveType");
				LeaveRequest lreq = new LeaveRequest(KeyFactory.stringToKey(empKey), Integer.valueOf(String.valueOf(lt)), 
						(String) r.getProperty("supervisor"), (String) r.getProperty("approver"), (String) r.getProperty("start"), 
						(String) r.getProperty("end"), (Double) r.getProperty("noOfDays"), (String) r.getProperty("remarks"), (String) r.getProperty("ref"));
				lreq.setLeaveStatus(Integer.valueOf(String.valueOf((long)r.getProperty("LeaveStatus"))));
				lreq.setId(KeyFactory.keyToString(r.getKey()));
				if(r.hasProperty("attachments")){
					List<String> atch = (List<String>)r.getProperty("attachments");
					lreq.setAttachments(atch);					
				}
				if(r.hasProperty("blobKeys")){
					List<String> atch = (List<String>)r.getProperty("blobKeys");
					lreq.setAttachments(atch);					
				}
				if(r.hasProperty("apList")){
					List<ActingPerson> apList = new ArrayList<ActingPerson>();
					List<Key> keys = (List<Key>)r.getProperty("apList");
					for(Key k : keys){
						ActingPerson a = ActorService.getInstance().getActingRecord(k);
						apList.add(a);
					}
					lreq.setApList(apList);
				}
				if(r.hasProperty("createDate")){
					lreq.setCreateDate((Date)r.getProperty("createDate"));
				}
//				if(r.hasProperty("startDayHalf")){
//					lreq.setStartDayHalf((String)r.getProperty("startDayHalf"));					
//				}
//				if(r.hasProperty("endDayHalf")){
//					lreq.setEndDayHalf((String)r.getProperty("endDayHalf"));					
//				}
//				if(r.hasProperty("satOffs")){
//					lreq.setSatOffs((String)r.getProperty("satOffs"));					
//				}
//				if(r.hasProperty("exGratiaClaim")){
//					lreq.setExGratiaClaim((Double)r.getProperty("exGratiaClaim"));					
//				}
//				if(r.hasProperty("bDayOffClaim")){
//					lreq.setbDayOffClaim((String)r.getProperty("bDayOffClaim"));					
//				}
//				if(r.hasProperty("totalClaims")){
//					double claims = (Double) r.getProperty("totalClaims");
//					if(claims > 0) {
//						lreq.setTotalClaims(claims);
//					}
//				}
				requests.add(lreq);
			}
		}
		return requests;
	}
	
	public List<LeaveRequest> getPendingMyApprovalRequest(String email, String role){
		List<LeaveRequest> lists = new ArrayList<LeaveRequest>();	
		Query q = new Query(LeaveRequest.class.getSimpleName());
		Filter filter = new FilterPredicate(role,
                FilterOperator.EQUAL,
                email);
		q.setFilter(filter);
		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		if(!results.isEmpty()){				
			for(Entity e : results){
				int status = ((Long)e.getProperty("LeaveStatus")).intValue();
				if(status==LeaveStatus.ACTING_APPROVED.id || status==LeaveStatus.PENDING_REVIEW.id){
					if(role.equalsIgnoreCase("supervisor") && 
							status!=LeaveStatus.SUPERVISOR_APPROVED.id &&
							status!=LeaveStatus.SUPERVISOR_REJECTED.id){
						LeaveRequest l = getLeaveRequest(KeyFactory.keyToString(e.getKey()));
						lists.add(l);
					}
					if(role.equalsIgnoreCase("approver") && 
							status!=LeaveStatus.APPROVER_APPROVED.id &&
							status!=LeaveStatus.APPROVER_REJECTED.id){
						LeaveRequest l = getLeaveRequest(KeyFactory.keyToString(e.getKey()));
						lists.add(l);
					}
				}
			}
		}
		return lists;
	}
	
	public List<LeaveRequest> getLeaveRequestListByStatus(int leaveStatus){
		List<LeaveRequest> lists = new ArrayList<LeaveRequest>();	
		Query q = new Query(LeaveRequest.class.getSimpleName());
		Filter filter = new FilterPredicate("leaveStatus",
                FilterOperator.EQUAL,
                leaveStatus);
		q.setFilter(filter);
		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		if(!results.isEmpty()){				
			for(Entity e : results){
				LeaveRequest l = getLeaveRequest(KeyFactory.keyToString(e.getKey()));
				lists.add(l);
			}
		}
		return lists;
	}
	
	public LeaveRequest getLeaveRequest(String keyStr){
		Entity e = findEntity(keyStr);
		LeaveRequest lreq = null;
		if(e!=null){
			long lt = (long) e.getProperty("LeaveType");
			lreq = new LeaveRequest((Key) e.getProperty("empKey"), Integer.valueOf(String.valueOf(lt)), 
					(String) e.getProperty("supervisor"), (String) e.getProperty("approver"), (String) e.getProperty("start"), 
					(String) e.getProperty("end"), (Double) e.getProperty("noOfDays"), (String) e.getProperty("remarks"), (String) e.getProperty("ref"));
			lreq.setLeaveStatus(Integer.valueOf(String.valueOf((long)e.getProperty("LeaveStatus"))));
			lreq.setId(KeyFactory.keyToString(e.getKey()));
			Date created = (Date)e.getProperty("createDate");
			lreq.setCreateDate(created);
			if(e.hasProperty("lastUpdate")){
				Date updated = (Date)e.getProperty("lastUpdate");
				lreq.setLastUpdate(updated);
			}
			if(e.hasProperty("approveDate")){
				Date approveDate = (Date)e.getProperty("approveDate");
				lreq.setApproveDate(approveDate);
			}			
			if(e.hasProperty("reason")){
				lreq.setReason((String)e.getProperty("reason"));
			}
			if(e.hasProperty("apList")){
				List<ActingPerson> apObjs = new ArrayList<ActingPerson>();
				List<Key> apKeys = (List<Key>)e.getProperty("apList");
				for(Key ak: apKeys){
					Entity ae = findEntityByKey(ak);
					Key apKey = ae.getParent();
					if(ae!=null){
						ActingPerson p = new ActingPerson();
						p.setApKey(apKey);
						p.setName((String)ae.getProperty("actor"));
						p.setDuties((String)ae.getProperty("duties"));
						p.setId(KeyFactory.keyToString(ak));
						if(ae.hasProperty("decision")){
							p.setDecision((String)ae.getProperty("decision"));
						}
						if(ae.hasProperty("reason")){
							p.setReason((String)ae.getProperty("reason"));
						}
						apObjs.add(p);
					}
				}
				lreq.setApList(apObjs);
			}
			if(e.hasProperty("attachments")){
				List<String> atch = (List<String>)e.getProperty("attachments");
				lreq.setAttachments(atch);					
			}
			if(e.hasProperty("blobKeys")){
				List<String> blobs = (List<String>)e.getProperty("blobKeys");
				lreq.setBlobKeys(blobs);					
			}
			if(e.hasProperty("startDayHalf")){
				lreq.setStartDayHalf((String)e.getProperty("startDayHalf"));					
			}
			if(e.hasProperty("endDayHalf")){
				lreq.setEndDayHalf((String)e.getProperty("endDayHalf"));					
			}
			if(e.hasProperty("satOffs")){
				lreq.setSatOffs((String)e.getProperty("satOffs"));					
			}
			if(e.hasProperty("exGratiaClaim")){
				lreq.setExGratiaClaim((Double)e.getProperty("exGratiaClaim"));					
			}
			if(e.hasProperty("bDayOffClaim")){
				lreq.setbDayOffClaim((String)e.getProperty("bDayOffClaim"));					
			}
			if(e.hasProperty("totalClaims")){
				double claims = (Double) e.getProperty("totalClaims");
				if(claims > 0) {
					lreq.setTotalClaims(claims);
				}
			}		
		}
		return lreq;
	}
	
	public LeaveRequest updateLeaveRequestStatus(String key, int newStatus, boolean isDeptApproved){
		Entity e = findEntity(key);
		Date updateTime = Misc.setCalendarByLocale();
		e.setProperty("LeaveStatus", newStatus);
		e.setProperty("lastUpdate",updateTime);
		if(isDeptApproved) {
			e.setProperty("approveDate", updateTime);
		}
		Key leaveKey = datastore.put(e);
		LeaveRequest leave = getLeaveRequest(KeyFactory.keyToString(leaveKey));
		return leave;
	}
	
	public LeaveRequest updateLeaveRequestStatus(String key, int newStatus, String reason, boolean isDeptApproved){
		Entity e = findEntity(key);
		Date updateTime = Misc.setCalendarByLocale();
		e.setProperty("LeaveStatus", newStatus);		
		e.setProperty("lastUpdate",updateTime.getTime());
		if(isDeptApproved) {
			String fromSuper = (String)e.getProperty("reason");
			e.setProperty("reason", fromSuper + "," + reason);
			e.setProperty("approveDate", updateTime.getTime());
		} else {
			e.setProperty("reason", reason);
		}
		Key leaveKey = datastore.put(e);
		LeaveRequest leave = getLeaveRequest(KeyFactory.keyToString(leaveKey));
		return leave;
	}
	
	public static List<String> getHolidayList(){
		List<String> holidayList = new ArrayList<String>();		    
		Query query = new Query(RegionalHolidays.class.getSimpleName());
		for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			holidayList.add((String)entity.getProperty("date"));
		}
		return holidayList;
	}

	public static List<String> genSaturdayList(String start, String end){
		List<String> satList = new ArrayList<String>();	
		SimpleDateFormat satFormat = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		int diff= 0;
		try {
			Date startDate = cwDateFormatter.parse(start);
			Date endDate = cwDateFormatter.parse(end);
			Calendar sDate = getDatePart(startDate);
			Calendar eDate = getDatePart(endDate);
			eDate.add(Calendar.DATE, 1);
			while (sDate.before(eDate)) {
				sDate.add(Calendar.DAY_OF_MONTH, 1);
				diff++;
			}
			Calendar cal= Calendar.getInstance();
			cal.setTime(startDate);
			for(int i=0; i<diff; i++){
				if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					satList.add(satFormat.format(cal.getTime()));
				}
				cal.add(Calendar.DATE, 1);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return satList;
	}
	
	public static boolean isBirthDayLeaveAvailable(String birthDate) {
		boolean isAvailable = false;
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		int year = calendar.getInstance().get(Calendar.YEAR);
		Calendar now = calendar.getInstance();
		Calendar bDay = calendar.getInstance();
		try {
			bDay.setTime(format.parse(birthDate.substring(0, birthDate.lastIndexOf("-")+1) + String.valueOf(year)));
			if(bDay.after(now)){
				isAvailable = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return isAvailable;
	}
}
