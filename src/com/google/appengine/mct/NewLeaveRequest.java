package com.google.appengine.mct;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.datastore.EmployeeLeaveDetailsService;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.*;
import com.google.appengine.enums.LeaveStatus;
import com.google.appengine.enums.LeaveType;
import com.google.appengine.util.ConstantUtils;
import com.google.appengine.util.MyProperties;


/**
 * @author Admin
 *
 */
public class NewLeaveRequest extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(NewLeaveRequest.class);
	
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	Calendar cal = Calendar.getInstance();	
		
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Employee emp = null;
		if(req.getSession().getAttribute("Employee")!=null){
			emp = (Employee) req.getSession().getAttribute("Employee");
		}
		
			String emptyField ="";
			for (Map.Entry<String, String[]> entry :
				 ((Map<String, String[]>)req.getParameterMap()).entrySet()){
				if(entry.getValue()[0].trim().length()==0 && !entry.getKey().equalsIgnoreCase("submitnewleave")){				
					emptyField = emptyField + entry.getKey() + ",";				
				}
			}
			if(emptyField.length()>0){
				req.setAttribute("errorMsg", "emptyfield" + emptyField);
				getServletConfig().getServletContext().getRequestDispatcher("/leave/new-request.jsp").forward(req, resp);
				return;
			}
			String leave_start = (String)req.getParameter("leave_start");
			String leave_end = (String)req.getParameter("leave_end");
			int leaveType = Integer.valueOf((String)req.getParameter("leave_type"));
			if(req.getAttribute("errorMsg") !=null){
				req.removeAttribute("errorMsg");
			}		
			boolean valid = LeaveRequestService.getInstance().validateLeaveBalance(emp,emp.getEmployeeLeaveDetails(),leaveType,leave_start,leave_end,req);		
			if(!valid){
				String errorMsg = "";
				if(leaveType==LeaveType.SICK_LEAVE_FP.id || leaveType==LeaveType.SICK_LEAVE_PP.id){
					if(req.getAttribute("errorMsg") == null){
						errorMsg = MyProperties.getErrorMsg("sick.leave.maximum.day");
						req.setAttribute("errorMsg", errorMsg);
					}
				}	
//				} else if(leaveType==LeaveType.BIRTHDAY_LEAVE.id){
//					resp.getWriter().write(MyProperties.getErrorMsg("invalid.birthday.leave"));
//				} else {
//					resp.getWriter().write(MyProperties.getErrorMsg("invalid.insufficient.leave"));
//				}	
				if(req.getAttribute("errorMsg") == null){
					errorMsg = MyProperties.getErrorMsg("invalid.insufficient.leave");
					req.setAttribute("errorMsg", errorMsg);
				}
				getServletConfig().getServletContext().getRequestDispatcher("/leave/new-request.jsp").forward(req, resp);
				return;
			}
			String empId = req.getParameter("empId");		
			String supervisor = emp.getSupervisor();
			String approver = emp.getDeptApprover().getEmailAddress();
			String remarks = (String)req.getParameter("remark");
			LeaveRequest lreq = LeaveRequestService.getInstance().makeNewLeaveRequest(empId, Integer.valueOf(leaveType), supervisor, approver, leave_start, leave_end, remarks);
			
			req.getSession().setAttribute("LeaveRequest", lreq);
			if(lreq.getLeaveType().equals(LeaveType.ANNUAL_LEAVE)){
				List<String> satList = LeaveRequestService.genSaturdayList(leave_start, leave_end);
				req.getSession().setAttribute("satList", satList);
			}
			resp.setContentType("text/plain");
			resp.getWriter().write("success");
//			getServletConfig().getServletContext().getRequestDispatcher("/leave/adj.jsp").forward(req, resp);
	      	return;
		
		
//		      blobstoreService.createUploadUrl("/UploadFile");
//		      getServletConfig().getServletContext().getreqDispatcher("/UploadFile").forward(req, resp);
		      /*} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/	
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug(NewLeaveRequest.class);
		if(req.getParameter("start_day")!=null && req.getParameter("end_day")!=null && req.getParameter("leave_type")!=null){
			int leaveType = Integer.valueOf((String)req.getParameter("leave_type"));
			String leaveStart = (String)req.getParameter("start_day");
			String leaveEnd = (String)req.getParameter("end_day");
			String state = LeaveRequestService.validateLeaveStart(leaveType, leaveStart, leaveEnd);
			if(leaveType==LeaveType.BIRTHDAY_LEAVE.id){
				Employee emp = (Employee) req.getSession().getAttribute("Employee");
				boolean available = LeaveRequestService.getInstance().validateBirthdayLeave(emp.getBirthDate(), emp.getDepartment(), leaveStart, leaveEnd);
				if(!available){
					state = "invalid";
				}
			}			
			resp.setContentType("text/plain");
			resp.getWriter().write(state);
			return;
		}
		if(req.getParameter("confirm")!=null){
			Employee emp = (Employee) req.getSession().getAttribute("Employee");
			LeaveRequest lreq = (LeaveRequest)req.getSession().getAttribute("LeaveRequest");
			boolean confirmed = Boolean.parseBoolean((String)req.getParameter("confirm"));
			if(confirmed){
				if(lreq.getApList()!=null){
					for(ActingPerson k : lreq.getApList()){
						log.debug("LeaveRequest actor list is : " + k.getApKey());
					}
				}
				
				String entityKey = LeaveRequestService.getInstance().saveLeaveRequest(lreq, emp.getEmpKey());
				if(entityKey!=null){
					resp.setContentType("text/plain");
					resp.getWriter().write("/leave/acknowledgement.jsp");
					return;
				}
			} else {
				LeaveRequestService.getInstance().updateLeaveRequestStatus(lreq.getId(), LeaveStatus.CANCELLED.id, false);
				resp.setContentType("text/plain");
				resp.getWriter().write("You have cancelled this request.");
				return;
			}
			
			
			
			
		}
		/*if(req.getSession().getAttribute("Employee")!=null){
			req.getSession().removeAttribute("Employee");
		}
		String emailAddress = (String)req.getParameter("emailAddress");
		String emailAddress = (String)req.getSession().getAttribute("emailAdd");
		if(StringUtils.isNotBlank(emailAddress)){			 			
			Employee employee = EmployeeService.getInstance().getFullEmployeeDetails(emailAddress);				
			if(employee.getEmailAddress()!=null && employee.getEmployeeLeaveDetails()!=null){
				req.getSession().setAttribute("Employee", employee);
			}				
		}*/
		try {
			getServletConfig().getServletContext().getRequestDispatcher("/leave/new-request.jsp").forward(req, resp);
			return;
		} catch (ServletException e) {
			log.error("NewLeaveRequest - doGet error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
