package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datastore.ActorService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;

import java.util.*;

public class AdminLeaveApproval extends BaseServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Employee approver = (Employee)req.getSession().getAttribute("Employee");
		boolean isApprover = (Boolean)req.getSession().getAttribute("isApprover");
		boolean isSuper = (Boolean)req.getSession().getAttribute("isSuper");
		String role = (String)req.getParameter("role");
		String email = approver.getEmailAddress();
		LeaveRequest l = null;
		List<LeaveRequest> pendingList = new ArrayList<LeaveRequest>();
		if(req.getParameter("decision")!= null){			
			String decision = (String)req.getParameter("decision");
			String key = (String)req.getParameter("key");
			boolean isDeptApproved = Boolean.parseBoolean((String)req.getParameter("isDeptApproved"));
			if(decision.equals("rejected")){
				String reason = (String)req.getParameter("reason");
				if(isDeptApproved){
					l = LeaveRequestService.getInstance().updateLeaveRequestStatus(key, LeaveStatus.APPROVER_REJECTED.id, "approver:" + reason, isDeptApproved);
					Key leaveKey = KeyFactory.stringToKey(l.getId());
					Date createDate = Misc.setCalendarByLocale();
					LeaveQueueService.getInstance().addLeaveQueue(createDate, leaveKey, l.getLeaveType().getId(), approver.getEmailAddress());
				} else {
					l = LeaveRequestService.getInstance().updateLeaveRequestStatus(key, LeaveStatus.SUPERVISOR_REJECTED.id, "supervisor:" + reason, isDeptApproved);
				}				
			} else {
				if(isDeptApproved){
					l = LeaveRequestService.getInstance().updateLeaveRequestStatus(key, LeaveStatus.APPROVER_APPROVED.id, isDeptApproved);
					Key leaveKey = KeyFactory.stringToKey(l.getId());
					Date createDate = Misc.setCalendarByLocale();
					LeaveQueueService.getInstance().addLeaveQueue(createDate, leaveKey, l.getLeaveType().getId(), approver.getEmailAddress());
				} else {
					l = LeaveRequestService.getInstance().updateLeaveRequestStatus(key, LeaveStatus.SUPERVISOR_APPROVED.id, isDeptApproved);
				}
			}			
			resp.setContentType("text/plain");
			if(l!=null){				
				resp.getWriter().write("success");				
			} else {
				resp.getWriter().write("error");
			}
			return;
		}
		if(isSuper){
			pendingList = LeaveRequestService.getInstance().getPendingMyApprovalRequest(email, role);
		}
		if(isApprover){
			pendingList = LeaveRequestService.getInstance().getPendingMyApprovalRequest(email, role);
		}
		req.getSession().setAttribute("pendingList", pendingList);
		getServletConfig().getServletContext().getRequestDispatcher("/admin/list.jsp").forward(req, resp);
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String recordId = (String)req.getParameter("recordId");
		List<LeaveRequest> pendingList = (List<LeaveRequest>)req.getSession().getAttribute("pendingList");
		boolean isAdmin = (Boolean)req.getSession().getAttribute("isAdmin");
		boolean isApprover = (Boolean)req.getSession().getAttribute("isApprover");
		boolean isSuper = (Boolean)req.getSession().getAttribute("isSuper");
		for(LeaveRequest l : pendingList){
			if(l.getId().equals(recordId)){
				req.getSession().setAttribute("LeaveRequest",l);
				Employee e = EmployeeService.getInstance().findEmployeeByKey(l.getEmpKey());
				req.setAttribute("applicant", e);
				req.setAttribute("approval", true);
				req.setAttribute("viewOnly", false);
				if(isSuper){
					if(l.getLeaveStatus().equals(LeaveStatus.SUPERVISOR_APPROVED) || l.getLeaveStatus().equals(LeaveStatus.SUPERVISOR_REJECTED)){
						req.setAttribute("approval",false);
						req.setAttribute("viewOnly", true);
					}
				}
				if(isApprover){
					if(l.getLeaveStatus().equals(LeaveStatus.APPROVER_APPROVED) || l.getLeaveStatus().equals(LeaveStatus.APPROVER_REJECTED)){
						req.setAttribute("approval",false);
						req.setAttribute("viewOnly", true);
					}
					req.setAttribute("isDeptApproved", true);
				}				
				getServletConfig().getServletContext().getRequestDispatcher("/leave/fulldetails.jsp").forward(req, resp);
				return;
			}
		}
	}

}
