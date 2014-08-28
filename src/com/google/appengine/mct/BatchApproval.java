package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;

public class BatchApproval extends BaseServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String[] recordIds = req.getParameterValues("recordIds");
		String type = req.getParameter("type");
		boolean isApprover = (Boolean) req.getSession().getAttribute("isApprover");
		boolean isSuper = (Boolean) req.getSession().getAttribute("isSuper");
		for(String rid: recordIds){
			Entity l = LeaveRequestService.getInstance().findEntity(rid);
			if(type.equals("approval")){
				if(isApprover){
					l.setProperty("", LeaveStatus.APPROVER_APPROVED.id);
				}
				if(isSuper){				
					l.setProperty("", LeaveStatus.SUPERVISOR_APPROVED.id);
				}
				if(type.equals("acting")){
					l.setProperty("", LeaveStatus.ACTING_APPROVED.id);
				}
			}
		}
	}

}
