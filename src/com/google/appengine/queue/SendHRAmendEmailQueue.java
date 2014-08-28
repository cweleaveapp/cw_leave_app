package com.google.appengine.queue;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.mct.BaseServlet;
import com.google.appengine.mct.Misc;

@SuppressWarnings("serial")
public class SendHRAmendEmailQueue extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(SendHRAmendEmailQueue.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(SendHRAmendEmailQueue.class);
		
		log.debug(SendHREmailQueue.class);
		String emailAddress = request.getParameter("emailAddress");
		String timeNow = request.getParameter("timeNow");
		String empName = request.getParameter("empName");
		String numOfDays = request.getParameter("numOfDays");
		String leaveType = request.getParameter("leaveType");
		String remark = request.getParameter("remark");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String changeType = request.getParameter("changeType");
		String newStartDate = request.getParameter("newStartDate");
		String newEndDate = request.getParameter("newEndDate");
		String oldLeaveType = request.getParameter("oldLeaveType");
		
		Misc misc = new Misc();
		try {
			misc.notifyHRAmend(emailAddress, timeNow, empName, 
					numOfDays, leaveType, remark, startDate, endDate, 
					changeType, newStartDate, newEndDate, oldLeaveType);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.debug("finish send HR Amend email queue");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(SendHRAmendEmailQueue.class);

	}

}
