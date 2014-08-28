package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.datastore.DepartmentService;
import com.google.appengine.entities.Department;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class AddDepartment extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddDepartment.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddDepartment.class);
		
		boolean exgratia_leave_day_enabled = false;
		String name_en = req.getParameter("name_en");
		String name_tc = req.getParameter("name_tc");		
		String approver = req.getParameter("approver");
		String delegate = "";
		if(req.getParameter("delegate").length() >0){
			delegate = req.getParameter("delegate");
		}
		
		if(req.getParameter("exgratia_leave_day_enabled")!=null && req.getParameter("exgratia_leave_day_enabled").equals("1")){
			exgratia_leave_day_enabled = true;
		}
		/* check if exist in the database */
		boolean exist = false;
		//String keyname = name_en.replaceAll(" ","").toLowerCase();
		Department dept = null;
		try{
			dept = DepartmentService.getInstance().getDepartmentByName(name_en);
		} catch(Exception e){}
		if(dept !=null){
			exist = true;
		}
		/*for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			String tmp = (String)entity.getProperty("department");
			if (tmp == null) {
				exist = false;
			} else if (tmp.equalsIgnoreCase(department)) {
				exist = true;
			}
		}
		*/
		if (exist == true) {
			try {
				req.setAttribute("feedback", ConstantUtils.ERROR);
				req.setAttribute("message", "The department already exist in the database.");
        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
        		return;
			} catch (ServletException e) {
				log.error("AddDepartment * doPost - error: " + e.getMessage());
				e.printStackTrace();
			}
		} else if (exist == false) {
			
			String deptKey = DepartmentService.getInstance().addDepartment(name_en, name_tc, exgratia_leave_day_enabled, approver, delegate);
			try{
			req.setAttribute("feedback", ConstantUtils.OK);
			req.setAttribute("message", "Save success.");
			req.setAttribute("deptKey", deptKey);
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
    		return;
			} catch (ServletException e) {
				log.error("AddDepartment * doPost - error 2: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//doPost(request, response);
		getServletConfig().getServletContext().getRequestDispatcher("/admin-add-dept.jsp").forward(request, response);
	}
}
