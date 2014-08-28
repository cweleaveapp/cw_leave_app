package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.LeaveQueue;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;

public class HR extends BaseServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		boolean isAdmin = (Boolean)req.getSession().getAttribute("isAdmin");
		if(isAdmin){
			if(req.getParameter("view")!=null){
				List<LeaveQueue> hrQ = new ArrayList<LeaveQueue>();
				String view = req.getParameter("view");
				if(view.equals("pending")){
					hrQ = LeaveQueueService.getInstance().getPendingLeaveQueue();
				} else if(view.equals("document")){
					hrQ = LeaveQueueService.getInstance().getDocsLeaveQueue();
				}
				req.getSession().setAttribute("hrQueue", hrQ);
				getServletConfig().getServletContext().getRequestDispatcher("/admin/hrlist.jsp").forward(req, resp);
			}
		} else {
			resp.sendRedirect("/sign-in.jsp");
		}
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = req.getParameter("action");		
		if(req.getParameter("recordId")!=null){
			String recordId = req.getParameter("recordId");
			LeaveRequest l = LeaveRequestService.getInstance().getLeaveRequest(recordId);
			if(action.equals("view")){				
				req.getSession().setAttribute("LeaveRequest", l);
				req.setAttribute("viewOnly", false);
				req.setAttribute("approval", true);				
			}
			if(action.equals("approved") || action.equals("rejected")){
				Employee emp = (Employee)req.getSession().getAttribute("Employee");
				String result = LeaveQueueService.getInstance().updateLeaveQueue(recordId, emp.getEmailAddress(), action);
				if(result.equals(action)){
					if(action.equals("approved")){
						l = LeaveRequestService.getInstance().updateLeaveRequestStatus(recordId, LeaveStatus.HR_APPROVED.id, false);
					} else {
						l = LeaveRequestService.getInstance().updateLeaveRequestStatus(recordId, LeaveStatus.HR_REJECTED.id, false);
					}
					req.getSession().setAttribute("LeaveRequest", l);
					req.setAttribute("viewOnly", false);
					req.setAttribute("approval", false);
					req.setAttribute("hrreviewed", "HR " + action);
				}
			}
			getServletConfig().getServletContext().getRequestDispatcher("/leave/fulldetails.jsp").forward(req, resp);
			return;
		}
		
	}

}
