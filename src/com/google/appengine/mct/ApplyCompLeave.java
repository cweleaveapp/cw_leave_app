package com.google.appengine.mct;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveQueueService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class ApplyCompLeave extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(ApplyCompLeave.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(ApplyCompLeave.class);
		Map<String, String> errorMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
			  properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
			} catch (IOException e) {
			  e.printStackTrace();
			}
		String timeNow = "";
		String startDate = "", endDate = "";
		String numOfDays = req.getParameter("numOfDays");
		String projectName = req.getParameter("projectName");
		String remark = req.getParameter("remark");
		String approvalFrom = req.getParameter("approvalFrom");
		String region = req.getParameter("region");
		String empName = "";
		Misc misc = new Misc();
		String eAdd = (String)req.getSession().getAttribute("emailAdd");
		EmployeeService emps = new EmployeeService();
		MCEmployee supervisor = emps.findMCEmployeeByColumnName("emailAddress", approvalFrom);
			MCEmployee em = emps.findMCEmployeeByColumnName("emailAddress", eAdd);
			region = em.getRegion();
			empName = em.getFullName();
			
		
		if (approvalFrom.equalsIgnoreCase("Default")) {
			approvalFrom = "";
		}
		
		/* check if numOfDays contains other characters that are not digits */
		if(!NumberUtils.isNumber(numOfDays)) {
				log.error(""+properties.getProperty("wrong.number.of.days"));
				errorMap.put("wrong.number.of.days", properties.getProperty("wrong.number.of.days"));
				req.setAttribute("errorMap", errorMap);
				try{
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		EmailSettingService ess = new EmailSettingService();
		LeaveQueueService lqs = new LeaveQueueService();
		HistoryService hs = new HistoryService();
		timeNow = Misc.now();
		hs.addToHistory(timeNow, eAdd, numOfDays, "", "", ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT, approvalFrom, remark, region, projectName, "", eAdd);
		lqs.addLeaveQueue(timeNow, eAdd, numOfDays, "", "", ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT, approvalFrom, remark, projectName, "","");
		try {
			misc.notifySupervisor(supervisor.getFullName(),approvalFrom, timeNow, empName, numOfDays, ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT, remark, "", "");
			
			List<String> eAddArray = new ArrayList<String>();
			for(int i=0; i < ess.getEmailSettingList().size(); i++){
				EmailSetting esetting = ess.getEmailSettingList().get(i);
				String regionArray [] = esetting.getRegion().split(",");
				for(int z=0; z<regionArray.length; z++){
					if(region.replaceAll(" ", "").toLowerCase().equals(regionArray[z].replaceAll(" ", "").toLowerCase())){
						log.debug("send email to "+esetting.getEmailAddress());
						eAddArray.add(esetting.getEmailAddress());
					}
				}
			}
			
			Queue queue = QueueFactory.getQueue("SendHREmailQueue");
			queue.add(withUrl("/SendHREmailQueue").param("emailAddress", eAddArray.toString())
					.param("timeNow", timeNow).param("empName", empName)
					.param("numOfDays", numOfDays).param("leaveType", ConstantUtils.COMPENSATION_LEAVE_ENTITLEMENT)
					.param("remark", remark).method(Method.POST));
			
			log.error(""+properties.getProperty("submitted.comp.leave.request"));
			req.setAttribute("feedback", ConstantUtils.OK);
			req.setAttribute("message", properties.getProperty("submitted.comp.leave.request"));
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
			return;
			
		} catch (MessagingException e) {
			log.error("ApplyCompLeave doPost error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* write to GDocs for record */
		String tmpStr = "Time="+timeNow+",Employee="+eAdd+",NumberOfDays="+numOfDays
		+",StartDate="+startDate+",EndDate="+endDate+",Supervisor="+approvalFrom
		+",LeaveType="+"Compensation Leave Entitlement"+",Remark="+remark;
//		misc.storeInGDocsHistory(tmpStr);
		
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ApplyCompLeave.class);
		String emailAddress = (String)request.getSession().getAttribute("emailAdd");
		AdministratorService admin = new AdministratorService();
		Administrator ad = admin.findAdministratorByEmailAddress(emailAddress);
		if(StringUtils.isBlank(ad.getEmailAddress())){
			EmployeeService  ems = new EmployeeService();
			MCEmployee employee = ems.findMCEmployeeByColumnName("emailAddress", emailAddress);
			request.setAttribute("approvalFrom", employee.getSupervisor());
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/mct-comp-leave-form.jsp").forward(request, response);
				return;
			} catch (ServletException e) {
				log.error("ApplyCompLeave - doPost error: " + e.getMessage());
				e.printStackTrace();
			}
			
		}
		response.sendRedirect("/admin-comp-leave-form.jsp");
	}
}
