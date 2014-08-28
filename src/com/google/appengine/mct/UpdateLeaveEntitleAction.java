package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.datastore.LeaveEntitleService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class UpdateLeaveEntitleAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateLeaveEntitleAction.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateLeaveEntitleAction.class);
		String leaveEntitleId = req.getParameter("id");
		String sickLeaveId [] = req.getParameterValues("sickLeaveId[]");
		String department = req.getParameter("department");
		String addAnnualLeave = req.getParameter("addAnnualLeave");
		String hospitalization = req.getParameter("hospitalization");		
		String addMaternityLeave = req.getParameter("addMaternityLeave");
		String addBirthdayLeave = req.getParameter("addBirthdayLeave");
		String addWeddingLeave = req.getParameter("addWeddingLeave");
		String addCompassionateLeave = req.getParameter("addCompassionateLeave");
		//String compensationLeaveExp = req.getParameter("compensationLeaveExp");
		
		String sickLeaveDay [] = req.getParameterValues("sickLeaveDay[]");
		String sickLeaveYear [] = req.getParameterValues("sickLeaveYear[]");
		String sickLeaveType [] = req.getParameterValues("sickLeaveType[]");
		
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();
		Properties properties = new Properties();
		try {
		  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		}
		
		if(sickLeaveDay != null && sickLeaveDay.length > 0){
			for(int i = 0; i < sickLeaveDay.length; i++){
				if(!NumberUtils.isDigits(sickLeaveDay[i])){
					log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
					errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
					break;
				}
			}
		}
		
		if(sickLeaveYear != null && sickLeaveYear.length > 0){
			for(int i = 0; i < sickLeaveYear.length; i++){
					if(!NumberUtils.isDigits(sickLeaveYear[i])){
						log.error(""+properties.getProperty("sick.leave.invalid.numerical"));
						errorMap.put("sick.leave.invalid.numerical", properties.getProperty("sick.leave.invalid.numerical"));
						break;
					}
				}
			}
		
		if(!NumberUtils.isDigits(addAnnualLeave)){
			log.error(""+properties.getProperty("entitled.annual.invalid.numerical"));
			errorMap.put("entitled.annual.invalid.numerical", properties.getProperty("entitled.annual.invalid.numerical"));
		}
		
		if(!NumberUtils.isDigits(hospitalization)){
			log.error(""+properties.getProperty("hospitalization.invalid.numerical"));
			errorMap.put("hospitalization.invalid.numerical", properties.getProperty("hospitalization.invalid.numerical"));
		}
		
		if(!NumberUtils.isDigits(addMaternityLeave)){
			log.error(""+properties.getProperty("maternity.leave.invalid.numerical"));
			errorMap.put("maternity.leave.invalid.numerical", properties.getProperty("maternity.leave.invalid.numerical"));
		}
		
		if(!NumberUtils.isDigits(addBirthdayLeave)){
			log.error(""+properties.getProperty("birthday.leave.invalid.numerical"));
			errorMap.put("birthday.leave.invalid.numerical", properties.getProperty("birthday.leave.invalid.numerical"));
		}
		
		if(!NumberUtils.isDigits(addWeddingLeave)){
			log.error(""+properties.getProperty("wedding.leave.invalid.numerical"));
			errorMap.put("wedding.leave.invalid.numerical", properties.getProperty("wedding.leave.invalid.numerical"));
		}
		
		if(!NumberUtils.isDigits(addCompassionateLeave)){
			log.error(""+properties.getProperty("compassionate.leave.invalid.numerical"));
			errorMap.put("compassionate.leave.invalid.numerical", properties.getProperty("compassionate.leave.invalid.numerical"));
		}
		
		/*if(!NumberUtils.isDigits(compensationLeaveExp)){
			log.error(""+properties.getProperty("compensation.leave.invalid.numerical"));
			errorMap.put("compensation.leave.invalid.numerical", properties.getProperty("compensation.leave.invalid.numerical"));
		}*/
		
		List<String> sickLeaveTypeList = new ArrayList<String>(Arrays.asList(sickLeaveType));
		Boolean duplicate = false;
		if(Collections.frequency(sickLeaveTypeList, ConstantUtils.LESS_THAN) > 1){
			duplicate = true;
		}
		else if(Collections.frequency(sickLeaveTypeList, ConstantUtils.LESS_THAN_OR_EQUAL) > 1){
			duplicate = true;
		}
		else if(Collections.frequency(sickLeaveTypeList, ConstantUtils.GREATER_THAN) > 1){
			duplicate = true;
		}
		else if(Collections.frequency(sickLeaveTypeList, ConstantUtils.GREATER_THAN_OR_EQUAL) > 1){
			duplicate = true;
		}
		
		if(duplicate == true){
			errorMap.put("duplicate.type", properties.getProperty("duplicate.type"));
		}
		
		if(!errorMap.isEmpty()){
        	try {
				req.setAttribute("errorMap", errorMap);
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
        	} catch (Exception e1) {
    			log.error("UpdateLeaveEntitleAction validate error: " + e1.getMessage());
    			e1.printStackTrace();
    		}
        }
		
		
		try {
			LeaveEntitleService ss = new LeaveEntitleService();
			ss.updateLeaveEntitle(addAnnualLeave, hospitalization, addMaternityLeave,
					addBirthdayLeave, addWeddingLeave, addCompassionateLeave, department);
					//,compensationLeaveExp);
			
			for(int i=0; i<sickLeaveId.length; i++){
				ss.updateSickLeaveEntitle(sickLeaveDay[i], sickLeaveType[i],
						sickLeaveYear[i], leaveEntitleId);
			}
			// add into sickLeaveIdList to prevent unsupportexception
			List<String> sickLeaveIdList = new ArrayList<String>(Arrays.asList(sickLeaveId));
			
			// add empty value into sickLeaveIdList to prevent out of bound
			for(int i =0; i < sickLeaveDay.length; i++){
				if(sickLeaveId.length <= i){
					sickLeaveIdList.add("");
				}
					
					
			}
			
			// add only if no sick leave id 
			for(int i =0; i < sickLeaveDay.length; i++){
				if(StringUtils.isEmpty(sickLeaveIdList.get(i))){
					ss.addSickLeaveEntitle(sickLeaveDay[i], sickLeaveType[i], sickLeaveYear[i],
							leaveEntitleId);
				}
			}
			
			
			log.error(""+properties.getProperty("update.success"));
			errorMap.put("update.success", properties.getProperty("update.success"));
			req.setAttribute("errorMap", errorMap);
			getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
    		return;
		} catch (EntityNotFoundException e) {
			log.error("UpdateLeaveEntitleAction - doPost: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateLeaveEntitleAction.class);
		String id = request.getParameter("id");
		try {
		LeaveEntitleService ss = new LeaveEntitleService();
		ss.deleteSickLeave(id);
		log.debug("delete sick leave");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
