package com.google.appengine.mct;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.util.ConstantUtils;
import com.ibm.icu.util.Calendar;

@SuppressWarnings("serial")
public class RegionLeaveReport extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(RegionLeaveReport.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(RegionLeaveReport.class);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(RegionLeaveReport.class);
		String isAdmin = ConstantUtils.FALSE;
		String eAdd = (String)request.getSession().getAttribute("emailAdd");
		AdministratorService addService = new AdministratorService();
		Administrator admin = addService.findAdministratorByEmailAddress(eAdd);
		if(StringUtils.isNotBlank(admin.getEmailAddress())){
			 isAdmin = ConstantUtils.TRUE; 
		}
		request.setAttribute("isAdmin", isAdmin);
		String regionSelected = request.getParameter("cri_region");
		String yearSelected = request.getParameter("cri_year");
		Calendar currYear = Calendar.getInstance(Locale.getDefault());
		yearSelected = StringUtils.isBlank(yearSelected) ?
				String.valueOf(currYear.get(Calendar.YEAR)) 
				: yearSelected;
		request.setAttribute("cri_year", yearSelected);
		regionSelected = regionSelected == null ? ConstantUtils.HONGKONG : regionSelected;
		request.setAttribute("cri_region", regionSelected);
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT_REV);
		ApprovedLeaveService als = new ApprovedLeaveService();
		
		Double janAL = 0.0;
		Double janCL = 0.0;
		Double janCLE = 0.0;
		Double janSL = 0.0;
		
		Double febAL = 0.0;
		Double febCL = 0.0;
		Double febCLE = 0.0;
		Double febSL = 0.0;
		
		Double marAL =  0.0;
		Double marCL =  0.0;
		Double marCLE = 0.0;
		Double marSL =  0.0;
		
		Double aprAL =  0.0;
		Double aprCL =  0.0;
		Double aprCLE =  0.0;
		Double aprSL =  0.0;
		
		Double mayAL =  0.0;
		Double mayCL =  0.0;
		Double mayCLE =  0.0;
		Double maySL =  0.0;
		
		Double junAL =  0.0;
		Double junCL =  0.0;
		Double junCLE =  0.0;
		Double junSL =  0.0;
		
		Double julAL =  0.0;
		Double julCL =  0.0;
		Double julCLE =  0.0;
		Double julSL =  0.0;
		
		Double augAL =  0.0;
		Double augCL =  0.0;
		Double augCLE =  0.0;
		Double augSL =  0.0;
		
		Double sepAL =  0.0;
		Double sepCL =  0.0;
		Double sepCLE =  0.0;
		Double sepSL =  0.0;
		
		Double octAL =  0.0;
		Double octCL =  0.0;
		Double octCLE =  0.0;
		Double octSL =  0.0;
		
		Double novAL =  0.0;
		Double novCL =  0.0;
		Double novCLE =  0.0;
		Double novSL =  0.0;
		
		Double decAL =  0.0;
		Double decCL =  0.0;
		Double decCLE =  0.0;
		Double decSL =  0.0;
		
		List<MCApprovedLeave> alList = new ArrayList<MCApprovedLeave>();
		alList = als.getApprovedLeaveByRegion(regionSelected);
		for(MCApprovedLeave al : alList){
			
			Calendar startTime = Calendar.getInstance(Locale.getDefault());
			Calendar endTime = Calendar.getInstance(Locale.getDefault());
			try {
				if(!ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
				startTime.setTime(standardDF.parse(al.getStartDate()));
				}
				else{
					startTime.setTime(standardDF.parse(al.getTime()));
				}
//				endTime.setTime(standardDF.parse(al.getEndDate()));
//				if(startTime.get(Calendar.MONTH) != endTime.get(Calendar.MONTH)){
//					long diffdate = daysBetween(standardDF.parse(al.getStartDate()),standardDF.parse(al.getEndDate()));
//					String monthEnd = String.valueOf(startTime.getActualMaximum(Calendar.DAY_OF_MONTH))
//							+al.getStartDate().substring(2);
//					long totalEndMonth = daysBetween(standardDF.parse(al.getStartDate()),standardDF.parse(monthEnd));
//					
//					diffdate = diffdate - totalEndMonth;
//					
//				}
			
				if(startTime.get(Calendar.YEAR) == Integer.parseInt(yearSelected)){
					
				if(startTime.get(Calendar.MONTH) == 0){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						janAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						janCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						janCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						janSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 1){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						febAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						febCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						febCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						febSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 2){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						marAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						marCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						marCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						marSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 3){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						aprAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						aprCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						aprCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						aprSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 4){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						mayAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						mayCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						mayCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						maySL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 5){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						junAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						junCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						junCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						junSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 6){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						julAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						julCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						julCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						julSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 7){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						augAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						augCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						augCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						augSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 8){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						sepAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						sepCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						sepCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						sepSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 9){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						octAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						octCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						octCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						octSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 10){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						novAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						novCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						novCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						novSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				else if(startTime.get(Calendar.MONTH) == 11){
					if(ConstantUtils.ANNUAL_LEAVE.equals(al.getLeaveType())){
						decAL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE.equals(al.getLeaveType())){
						decCL += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(al.getLeaveType())){
						decCLE += Double.parseDouble(al.getNumOfDays());
					}
					if(ConstantUtils.SICK_LEAVE.equals(al.getLeaveType())){
						decSL += Double.parseDouble(al.getNumOfDays());
					}
					
				}
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("['Month").append("','")
		.append(ConstantUtils.ANNUAL_LEAVE).append("','")
		.append(ConstantUtils.SICK_LEAVE).append("','")
		.append(ConstantUtils.COMPENSATION_LEAVE).append("','")
		.append(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT).append("'],")
		.append("['January").append("','")
		.append(janAL).append("','")
		.append(janSL).append("','")
		.append(janCL).append("','")
		.append(janCLE).append("'],")
		.append("['Febuary").append("','")
		.append(febAL).append("','")
		.append(febSL).append("','")
		.append(febCL).append("','")
		.append(febCLE).append("'],");
		
//		request.setAttribute("result", sb.toString());
		
		request.setAttribute("janAL", janAL);
		request.setAttribute("janCL", janCL);
		request.setAttribute("janCLE", janCLE);
		request.setAttribute("janSL", janSL);
		
		request.setAttribute("febAL", febAL);
		request.setAttribute("febCL", febCL);
		request.setAttribute("febCLE", febCLE);
		request.setAttribute("febSL", febSL);
		
		request.setAttribute("marAL", marAL);
		request.setAttribute("marCL", marCL);
		request.setAttribute("marCLE", marCLE);
		request.setAttribute("marSL", marSL);
		
		request.setAttribute("aprAL", aprAL);
		request.setAttribute("aprCL", aprCL);
		request.setAttribute("aprCLE", aprCLE);
		request.setAttribute("aprSL", aprSL);
		
		request.setAttribute("mayAL", mayAL);
		request.setAttribute("mayCL", mayCL);
		request.setAttribute("mayCLE", mayCLE);
		request.setAttribute("maySL", maySL);
		
		request.setAttribute("junAL", junAL);
		request.setAttribute("junCL", junCL);
		request.setAttribute("junCLE", junCLE);
		request.setAttribute("junSL", junSL);
		
		request.setAttribute("julAL", julAL);
		request.setAttribute("julCL", julCL);
		request.setAttribute("julCLE", julCLE);
		request.setAttribute("julSL", julSL);
		
		request.setAttribute("augAL", augAL);
		request.setAttribute("augCL", augCL);
		request.setAttribute("augCLE", augCLE);
		request.setAttribute("augSL", augSL);
		
		request.setAttribute("sepAL", sepAL);
		request.setAttribute("sepCL", sepCL);
		request.setAttribute("sepCLE", sepCLE);
		request.setAttribute("sepSL", sepSL);
		
		request.setAttribute("octAL", octAL);
		request.setAttribute("octCL", octCL);
		request.setAttribute("octCLE", octCLE);
		request.setAttribute("octSL", octSL);
		
		request.setAttribute("novAL", novAL);
		request.setAttribute("novCL", novCL);
		request.setAttribute("novCLE", novCLE);
		request.setAttribute("novSL", novSL);
		
		request.setAttribute("decAL", decAL);
		request.setAttribute("decCL", decCL);
		request.setAttribute("decCLE", decCLE);
		request.setAttribute("decSL", decSL);
		
		try {
    		getServletConfig().getServletContext().getRequestDispatcher("/leave-report.jsp").forward(request, response);
    		return;
    	} catch (Exception e1) {
			log.error("RegionLeaveReport error: " + e1.getMessage());
			e1.printStackTrace();
		}
		
		
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
	
	public static Calendar getDatePart(Date date){
	    Calendar cal = Calendar.getInstance(Locale.getDefault());       // get calendar instance
	    cal.setTime(date);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
	    cal.set(Calendar.MINUTE, 0);                 // set minute in hour
	    cal.set(Calendar.SECOND, 0);                 // set second in minute
	    cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second
	    return cal;
	    
	}

}
