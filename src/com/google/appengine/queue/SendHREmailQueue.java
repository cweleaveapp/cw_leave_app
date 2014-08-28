package com.google.appengine.queue;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.mct.BaseServlet;
import com.google.appengine.mct.Misc;

@SuppressWarnings("serial")
public class SendHREmailQueue extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(SendHREmailQueue.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(SendHREmailQueue.class);
		String emailAddress = request.getParameter("emailAddress");
		String timeNow = request.getParameter("timeNow");
		String empName = request.getParameter("empName");
		String numOfDays = request.getParameter("numOfDays");
		String leaveType = request.getParameter("leaveType");
		String remark = request.getParameter("remark");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		Misc misc = new Misc();
		try {
			misc.notifyHR(emailAddress,timeNow, empName, 
					numOfDays, leaveType, remark, startDate, endDate);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.debug("finish send HR email queue");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(SendHREmailQueue.class);

	}
}
