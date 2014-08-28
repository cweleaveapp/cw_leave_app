package com.google.appengine.cron;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.mct.BaseServlet;
import com.google.appengine.mct.Notification;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class CompExpCron extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(CompExpCron.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(CompExpCron.class);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(CompExpCron.class);
		
		NotifyCompExpService notifyCompExpService = new NotifyCompExpService();
		SimpleDateFormat standardDF = new SimpleDateFormat(ConstantUtils.DATE_FORMAT);
		List<Notification> pendingList = notifyCompExpService.getAllPendingNotifyComp();
		
	}

}
