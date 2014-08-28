package com.google.appengine.mct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;
import com.google.appengine.datastore.ActorService;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.datastore.LeaveRequestService;
import com.google.appengine.entities.ActingPerson;
import com.google.appengine.entities.Employee;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.enums.LeaveStatus;

public class Actor extends BaseServlet {
	
	private DataStoreUtil util = new DataStoreUtil();
	
	public Actor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			
		Employee loginEmp = (Employee)req.getSession().getAttribute("Employee");
		LeaveRequest leave = (LeaveRequest)req.getSession().getAttribute("LeaveRequest");	
		
		if(req.getParameter("decision")!= null){
			
			String decision = (String)req.getParameter("decision");
			String key = (String)req.getParameter("key");
			String act = null;
			if(decision.equals("rejected")){
				String reason = (String)req.getParameter("reason");
				act = ActorService.getInstance().actionUpdateDecision(decision, reason, key);
			} else {
				act = ActorService.getInstance().actionUpdateDecision(decision, key);
			}
			
			int approved = 0;
			List<ActingPerson> aList = leave.getApList();
			for(ActingPerson a: aList){
				if(a.getApKey().equals(KeyFactory.stringToKey(loginEmp.getEmpKey()))){
					a.setDecision(decision);
				}
				if(a.getDecision().equals("approved")){
					approved = approved + 1;
				}				
			}
			if(decision.equals("rejected")&&aList.size()==1){
				//only one actor in the list who reject the request
				LeaveRequestService.getInstance().updateLeaveRequestStatus(leave.getId(), LeaveStatus.ACTING_REJECTED.id, false);
			}
			if(approved == aList.size()){
				//all approved
				LeaveRequestService.getInstance().updateLeaveRequestStatus(leave.getId(), LeaveStatus.ACTING_APPROVED.id, false);
			} else if(approved < aList.size() && approved > 0){
				//at least one actor reject
				LeaveRequestService.getInstance().updateLeaveRequestStatus(leave.getId(), LeaveStatus.ACTING_REJECTED.id, false);
			}
			
			resp.setContentType("text/plain");
			if(act!=null && act.equals(key)){				
				resp.getWriter().write("success");				
			} else {
				resp.getWriter().write("error");
			}
			return;
		}
		
		if(req.getParameter("dept")!= null){
			String optList = "";
			String deptKeyStr = (String)req.getParameter("dept");
			
			Query query = new Query(Employee.class.getSimpleName());
			query.setAncestor(KeyFactory.stringToKey(deptKeyStr));
			List<Entity> emps = util.getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
			if(emps.size() > 0){
				for(Entity emp : emps){
					if(!KeyFactory.keyToString(emp.getKey()).equals(loginEmp.getEmpKey())){
						optList = optList + "<option value=\"" + KeyFactory.keyToString(emp.getKey()) + "\">" + emp.getProperty("fullName") + "</option>";
					}					
				}
			}
			resp.setContentType("text/html");
			resp.getWriter().write(optList);
			return;
		}
		
		Employee emp = (Employee)req.getSession().getAttribute("Employee");
		List<LeaveRequest> acList = ActorService.getInstance().findPendingActorList(emp.getEmpKey());
		req.setAttribute("view", "acting");
		req.getSession().setAttribute("PendingActorList", acList);
		getServletConfig().getServletContext().getRequestDispatcher("/acting/list.jsp").forward(req, resp);
		return;
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		LeaveRequest lreq = null;
		if(req.getParameterValues("actorName[]")!=null && req.getParameterValues("duties[]")!=null){
			String[] actors = req.getParameterValues("actorName[]");
			String[] duties = req.getParameterValues("duties[]");
			lreq = (LeaveRequest)req.getSession().getAttribute("LeaveRequest");
			Key empKey = lreq.getEmpKey();
			String refNo = lreq.getRef();
			
			List<ActingPerson> apList = new ArrayList<ActingPerson>();
			for(int i=0; i<actors.length; i++){			
				if(!actors[i].contains("Employee")){
					ActingPerson p = new ActingPerson(actors[i],duties[i],empKey,refNo);
					apList.add(p);
				}			
			}
			if(apList.size()>0){
				lreq.setApList(apList);
			}
				req.getSession().setAttribute("LeaveRequest", lreq);
				getServletConfig().getServletContext().getRequestDispatcher("/leave/confirmation.jsp").forward(req, resp);
				return;
			
		}
		
		
		if(req.getParameter("recordId")!=null){
			String keyStr = (String)req.getParameter("recordId");
			if(req.getParameter("viewByAssign")!=null){
				LeaveRequest l = (LeaveRequest) req.getSession().getAttribute("LeaveRequest");
				String actorname = (String)req.getParameter("actorname");
				for(ActingPerson a : l.getApList()){
					if(a.getName().equals(actorname)){
						req.setAttribute("viewOnly", true);
						req.setAttribute("actorname", actorname);
						req.getSession().setAttribute("LeaveRequest", l);
						getServletConfig().getServletContext().getRequestDispatcher("/acting/details.jsp").forward(req, resp);
						return;
					}
				}
				
			}
			List<LeaveRequest> recordlist = new ArrayList<LeaveRequest>();
			if(req.getSession().getAttribute("PendingActorList")!=null) {
				recordlist = (List<LeaveRequest>) req.getSession().getAttribute("PendingActorList");						
				for(LeaveRequest l : recordlist){
					if(l.getId().equals(keyStr)){
						Employee assign = EmployeeService.getInstance().findEmployeeByKey(l.getEmpKey());
						req.getSession().setAttribute("assigned",assign);
						req.getSession().setAttribute("LeaveRequest", l);
						getServletConfig().getServletContext().getRequestDispatcher("/acting/details.jsp").forward(req, resp);
						return;
					}					
				}
			}
//			act.setProperty("duties",duties[i]);
//			act.setProperty("empKey",lreq.getEmpKey());
//			act.setProperty("refNo", lreq.getRef());
		}
//				act.setProperty("actor", aName);
//				act.setProperty("duties",duties[i]);
//				act.setProperty("empKey",lreq.getEmpKey());
//				act.setProperty("refNo", lreq.getRef());
//				Key actK = util.getDatastore().put(act);
//				
//				ActingPerson ap = new ActingPerson(KeyFactory.keyToString(actK),aName,duties[i],lreq.getEmpKey(),lreq.getRef());
//				apList.add(ap);
//				i++;
//			}			
//		}
		
	}
	
}
