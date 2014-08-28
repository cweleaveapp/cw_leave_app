package com.google.appengine.mct;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.EmployeeLeaveDetails;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;
import com.google.appengine.enums.LeaveType;

public class LeaveDetails extends BaseServlet {

	public LeaveDetails() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		List<LeaveRequest> leaves = new ArrayList<LeaveRequest>();
		Employee emp = (Employee)req.getSession().getAttribute("Employee");
		EmployeeLeaveDetails elds = emp.getEmployeeLeaveDetails();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date now = Misc.setCalendarByLocale();
		Calendar toDay = LeaveRequestService.getDatePart(now);
		
		String results = "";
		if(req.getParameter("historyOnly")!=null){
			List<LeaveRequest> Allleaves = (List<LeaveRequest>) LeaveRequestService.getInstance().getAllLeaveRequests(emp.getEmpKey());
			for(LeaveRequest l : Allleaves) {
				if(!l.getLeaveStatus().equals(LeaveStatus.PENDING_REVIEW)){
					try {
						Date endDay = formatter.parse(l.getEnd());
						Calendar endDate =LeaveRequestService.getDatePart(endDay);
						if(endDate.before(toDay)){
							leaves.add(l);
							results = results + "<li><span class=\"id\">" + l.getId() + "</span><span class=\"reason\">" +
									l.getLeaveType().type + "</span><div class=\"noicon\"></div><div class=\"noicon\"></div>" +
									"<span class=\"date_of_leave\">"+l.getStart() + "-" + l.getEnd()+"</span>" 
									+ "<span class=\"total_leave\">(" + l.getNoOfDays() + ")</span>" + 
									"<span class=\"status\">" + l.getLeaveStatus().abbreviation + "</span></li>"; 
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//if(results.length() > 0){
				resp.setContentType("text/html");
				resp.getWriter().write(results);
				return;
			//}	
				
		}
		
		double pendingDeduction = 0;
		
		
		leaves = (List<LeaveRequest>) LeaveRequestService.getInstance().getAllLeaveRequests(emp.getEmpKey());
		boolean isAdmin = (Boolean)req.getSession().getAttribute("isAdmin");
		boolean isSuper = (Boolean)req.getSession().getAttribute("isSuper");
		boolean isApprover = (Boolean)req.getSession().getAttribute("isApprover");
		if(isSuper){
			List<LeaveRequest> pendingList = (List<LeaveRequest>) LeaveRequestService.getInstance().
					getPendingMyApprovalRequest(emp.getEmailAddress(), "supervisor");
			req.getSession().setAttribute("pendingList", pendingList);
		}
		if(isApprover){
			List<LeaveRequest> pendingList = (List<LeaveRequest>) LeaveRequestService.getInstance().
					getPendingMyApprovalRequest(emp.getEmailAddress(), "approver");
			req.getSession().setAttribute("pendingList", pendingList);
		}
		if(isAdmin){
			List<LeaveQueue> hrQ = LeaveQueueService.getInstance().getPendingLeaveQueue();
			req.getSession().setAttribute("hrQueue", hrQ);
		}
		if(!leaves.isEmpty()){
			for(LeaveRequest l : leaves) {
				if(l.getLeaveType().equals(LeaveType.ANNUAL_LEAVE)){
					if(l.getLeaveStatus()!=LeaveStatus.HR_APPROVED 
							&& l.getLeaveStatus()!=LeaveStatus.HR_REJECTED 
							&& l.getLeaveStatus()!=LeaveStatus.CANCELLED
							&& l.getLeaveStatus()!=LeaveStatus.EXPIRED){
						pendingDeduction = pendingDeduction + l.getNoOfDays();	
					}
				}
			}
		}		
		
		double total = Double.parseDouble(elds.getBalance()) + Double.parseDouble(elds.getLastYearBalance()) - pendingDeduction;
		
		req.getSession().setAttribute("LeaveRequests", leaves);
		req.getSession().setAttribute("MyTotalLeaveBalance", total);
		req.getSession().setAttribute("pendingDeduction", pendingDeduction);
		getServletConfig().getServletContext().getRequestDispatcher("/leave/index.jsp").forward(req, resp);
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(req.getParameter("lreqid")!=null){
			String lreqid = (String)req.getParameter("lreqid");
			 LeaveRequest lreq = LeaveRequestService.getInstance().getLeaveRequest(lreqid);
			 if(lreq!=null){
				 req.getSession().setAttribute("LeaveRequest", lreq);
				 req.setAttribute("viewOnly", true);
				 getServletConfig().getServletContext().getRequestDispatcher("/leave/fulldetails.jsp").forward(req, resp);
				 return;
			 } else {
				resp.setContentType("text/plain");
				resp.getWriter().write("notfound");
				return;
			 }
		}
	}

}
