package com.google.appengine.mct;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveEntitlement;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;
import com.google.appengine.enums.LeaveType;


public class AdjustLeaveBalance extends BaseServlet {

	private static final Logger log = Logger.getLogger(AdjustLeaveBalance.class);
		
	public AdjustLeaveBalance() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		List<LeaveRequest> lists = (List<LeaveRequest>)req.getSession().getAttribute("LeaveRequests");
		Employee emp = (Employee) req.getSession().getAttribute("Employee");
		EmployeeLeaveDetails edtl = (EmployeeLeaveDetails)emp.getEmployeeLeaveDetails();
		Calendar cal = Calendar.getInstance();
		LeaveEntitlement entitle = LeaveEntitleService.getInstance().getLeaveEntitlementByYear(String.valueOf(cal.get(Calendar.YEAR)));
		LeaveRequest lreq = (LeaveRequest) req.getSession().getAttribute("LeaveRequest");
		if(lreq.getAttachments()!=null){
			log.debug("LeaveRequest file list size : " + lreq.getAttachments().size());
		}
		
		double totalBalance = 0;
		
		//retrieve the total balance based on select leave type
		switch(lreq.getLeaveType().getId()){
		case 1:
		//Annual Leave
			totalBalance = Double.parseDouble(edtl.getEntitledAnnual()) + Double.parseDouble(edtl.getLastYearBalance())
							- Double.parseDouble(edtl.getAnnualLeave());
			break;
		case 2: 
		//birthday leave
			totalBalance = 1;
			break;
		case 3: 
		//compassionate leave
			totalBalance = Double.parseDouble(entitle.getAddCompassionateLeave()) - Double.parseDouble(edtl.getCompassionateLeave());
			break;
		case 5:
		//exam leave
			totalBalance = Double.parseDouble(entitle.getAddExaminationLeave()) - Double.parseDouble(edtl.getExamLeave());
			break;
		case 6:
		// Injury Leave
			totalBalance = Double.parseDouble(entitle.getAddInjuryLeave()) - Double.parseDouble(edtl.getInjuryLeave());
			break;
		case 7:
		// Jury Leave
			totalBalance = Double.parseDouble(entitle.getAddJuryLeave()) - Double.parseDouble(edtl.getJuryLeave());
		break;
		// Marriage Leave
		case 8:
			totalBalance = Double.parseDouble(entitle.getAddMarriageLeave()) - Double.parseDouble(edtl.getMarriageLeave());
		break;
		// Maternity Leave
		case 9:
			totalBalance = Double.parseDouble(entitle.getAddMaternityLeave()) - Double.parseDouble(edtl.getMaternityLeave());
		break;
		// Paternity Leave
		case 10:
			totalBalance = Double.parseDouble(entitle.getAddPaternityLeave()) - Double.parseDouble(edtl.getPaternityLeave());
		break;
		case 12:
		// Sick Leave (FP)
			totalBalance = Double.parseDouble(entitle.getAddFPSickLeave()) - Double.parseDouble(edtl.getSickLeaveFP());
		break;
		case 13:
		// Sick Leave (PP)
		totalBalance = Double.parseDouble(entitle.getAddPPSickLeave()) - Double.parseDouble(edtl.getSickLeavePP());
		break;		
		}
		boolean bDayLeave = LeaveRequestService.isBirthDayLeaveAvailable(emp.getBirthDate());
		req.setAttribute("totalBalance", totalBalance);
		double pendingDeduced = LeaveRequestService.pendingDeducedByType(lists,lreq.getLeaveType());
		req.setAttribute("pendingDeduced", pendingDeduced);
		req.setAttribute("bDayLeave", bDayLeave);
		getServletConfig().getServletContext().getRequestDispatcher("/leave/adj.jsp").forward(req, resp);
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LeaveRequest lreq = (LeaveRequest) req.getSession().getAttribute("LeaveRequest");
		Employee emp = (Employee) req.getSession().getAttribute("Employee");
		String startDayHalf = "";
		String endDayHalf = "";
		String satOffstr = "";
		String exGratiaClaim = "";
		String BdayLeave = "";
		String remark = "";
		Map<String,String> specialClaims = new HashMap<String,String>();
		
		if(req.getParameter("remark")!=null){
			remark = (String)req.getParameter("remark");
			if(!remark.equalsIgnoreCase(lreq.getRemarks())){
				lreq.setRemarks(remark);
			}
		}
		if(req.getParameter("startdayHalf")!=null){
			startDayHalf = (String)req.getParameter("startdayHalf");
		}
		if(req.getParameter("endDayHalf")!=null){
			endDayHalf = (String)req.getParameter("endDayHalf");
		}
		if(req.getParameter("sat-off")!=null){
			satOffstr = (String)req.getParameter("sat-off");
			specialClaims.put("satOffstr",satOffstr);
		}
		if(req.getParameter("exGratiaClaim")!=null){
			exGratiaClaim = (String)req.getParameter("exGratiaClaim");
			specialClaims.put("exGratiaClaim",exGratiaClaim);
		}		
		if(req.getParameter("BdayLeave")!=null){
			BdayLeave = (String)req.getParameter("BdayLeave");
			if(BdayLeave.length() < 3){
				specialClaims.put("BdayLeave",BdayLeave);	
			}					
		}
		LeaveRequest adjleave = adjustLeaveDays(lreq, startDayHalf, endDayHalf, specialClaims);
		req.getSession().setAttribute("LeaveRequest", adjleave);
		if(lreq.getLeaveType()!=LeaveType.SICK_LEAVE_FP && lreq.getLeaveType()!=LeaveType.SICK_LEAVE_PP){
			getServletConfig().getServletContext().getRequestDispatcher("/leave/acting.jsp").forward(req, resp);
			return;
		} else {
			getServletConfig().getServletContext().getRequestDispatcher("/leave/confirmation.jsp").forward(req, resp);
			return;
		}
	}
	
	private LeaveRequest adjustLeaveDays(LeaveRequest leave, String startDayHalf, String endDayHalf, Map<String, String> specialClaims) {
		double noOfDays = leave.getNoOfDays();
		double claims = 0;
		if(startDayHalf.length() > 0) {
			noOfDays = noOfDays - 0.5;
			leave.setStartDayHalf(startDayHalf);
		}
		if(endDayHalf.length() > 0){
			noOfDays = noOfDays - 0.5;
			leave.setEndDayHalf(endDayHalf);
		}
		if(!specialClaims.isEmpty()){
			for(Map.Entry<String, String> c: specialClaims.entrySet()){
				if(c.getKey().equals("satOffstr")){
					leave.setSatOffs(c.getValue());
					String[] satOffs = c.getValue().split(","); 
					claims = claims + satOffs.length;
				}
				if(c.getKey().equals("exGratiaClaim")){
					double exdays = Double.parseDouble(c.getValue());
					leave.setExGratiaClaim(exdays);
					claims = claims + exdays;				
				}
				if(c.getKey().equals("BdayLeave")){
					leave.setbDayOffClaim(c.getValue());
					claims = claims + 1;
				}				
			}
		}
		leave.setNoOfDays(noOfDays);
		leave.setTotalClaims(claims);
		return leave;
	}
	
	
}
