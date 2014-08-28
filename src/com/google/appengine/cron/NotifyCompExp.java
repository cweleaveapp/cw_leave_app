package com.google.appengine.cron;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.mct.ApprovedLeaveService;
import com.google.appengine.mct.BaseServlet;
import com.google.appengine.mct.MCEmployee;
import com.google.appengine.mct.Misc;
import com.google.appengine.mct.Notification;
import com.google.appengine.util.ConstantUtils;
import com.ibm.icu.util.Calendar;

@SuppressWarnings("serial")
public class NotifyCompExp extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(NotifyCompExp.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(NotifyCompExp.class);
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(NotifyCompExp.class);
		
		Misc misc = new Misc();
		NotifyCompExpService notifyCompExpService = new NotifyCompExpService();
		EmployeeService empService = new EmployeeService();
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		standardDF.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		List<Notification> pendingList = notifyCompExpService.getAllPendingNotifyComp();
		if(pendingList != null && !pendingList.isEmpty()){
			for(Notification notify : pendingList){
				Calendar cal = Calendar.getInstance();
				Calendar cur = Calendar.getInstance();
				try {
					cal.setTime(standardDF.parse(notify.getApproveTime()));
					cal.add(Calendar.MONTH, 2);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(cur.getTime().after(cal.getTime())){
					MCEmployee emp = empService.findMCEmployeeByColumnName("emailAddress", notify.getEmail());
					misc.notifyCompExp(notify.getEmail(),emp.getFullName());
					notifyCompExpService.updateNotifyComp(notify);
				}
			}
		}
		
		log.debug("finish send notify email compensation leave cron job");
	}

}
