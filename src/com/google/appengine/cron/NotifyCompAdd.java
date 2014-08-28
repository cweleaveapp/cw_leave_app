package com.google.appengine.cron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.mct.MCApprovedLeave;
import com.google.appengine.mct.ApprovedLeaveService;
import com.google.appengine.mct.BaseServlet;
import com.google.appengine.mct.Notification;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class NotifyCompAdd  extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(NotifyCompAdd.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(NotifyCompAdd.class);
		
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(NotifyCompAdd.class);
		NotifyCompExpService notifyCompExpService = new NotifyCompExpService();
		ApprovedLeaveService approvedLeaveService = new ApprovedLeaveService();
		List<Notification> notifyList = notifyCompExpService.getAllNotifyComp();
		List<MCApprovedLeave> appLeaveList = approvedLeaveService.getApprovedLeave();
		List<Notification> addList = new ArrayList<Notification>();
		// if first time add notification data
		if(notifyList != null && !notifyList.isEmpty()){
			if(appLeaveList != null && !appLeaveList.isEmpty()){
				for(MCApprovedLeave appLeave : approvedLeaveService.getApprovedLeave()){
					if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(appLeave.getLeaveType())){
						Notification notification = notifyCompExpService.getNotifyCompExpByAppId(appLeave.getId());
						if(!appLeave.getId().equals(notification.getApproveId())){
							Notification newNotify = new Notification();
							newNotify.setApproveId(appLeave.getId());
							newNotify.setEmail(appLeave.getEmailAdd());
							newNotify.setStatus(ConstantUtils.PENDING);
							newNotify.setApproveTime(appLeave.getTime());
							addList.add(newNotify);
						}
					}
					
				}
			}
		}
		else{
			for(MCApprovedLeave appLeave : approvedLeaveService.getApprovedLeave()){
				if(ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT.equals(appLeave.getLeaveType())){
					Notification newNotify = new Notification();
					newNotify.setApproveId(appLeave.getId());
					newNotify.setEmail(appLeave.getEmailAdd());
					newNotify.setStatus(ConstantUtils.PENDING);
					newNotify.setApproveTime(appLeave.getTime());
					addList.add(newNotify);
				}
				
			}
		}
		
		if(addList != null && !addList.isEmpty()){
			for(Notification notify : addList){
				notifyCompExpService.saveNotifyComp(notify);
			}
			
		}
		
		log.debug("finish notify compensation leave add cron job");
		
	}

}
