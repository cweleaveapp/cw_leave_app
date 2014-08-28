package com.google.appengine.mct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.commons.lang.math.NumberUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class AddNotify extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(AddNotify.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddNotify.class);
		String addNotifyFreq = req.getParameter("addNotifyFreq");
		/*Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.clear();*/		
		
		/* to check if exist in the database */
		boolean exist = false;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Notify.class.getSimpleName());
		for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults())) {
			String tmp = (String)entity.getProperty("addNotifyFreq");
			if (tmp == null) {
				exist = false;
			} else if (tmp.equalsIgnoreCase(addNotifyFreq)) {
				exist = true;
			}
		}
		
		// Only numeric number are allowed
		if(!NumberUtils.isDigits(addNotifyFreq)){
			try {
	        	req.setAttribute("feedback", ConstantUtils.ERROR);
	        	req.setAttribute("message", "Only numeric number are allowed");
	        	getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	        	return;
	        } catch (Exception e1) {
	    		log.error("AddNotify validate notification error: " + e1.getMessage());
	    		e1.printStackTrace();
	    	}
		}
		
		if (exist == true) {
			try {
				req.setAttribute("addNotifyFreq", addNotifyFreq);
				req.setAttribute("feedback", ConstantUtils.ERROR);
				req.setAttribute("message", "The notification value already exist in the database.");
        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
        		return;
			} catch (ServletException e) {
				log.error("AddNotify * doPost - error: " + e.getMessage());
				e.printStackTrace();
			}
			
		} else if (exist == false) {
			NotifyService ns = new NotifyService();					
			
			ns.addToNotify(addNotifyFreq);
			try{
				req.setAttribute("feedback", ConstantUtils.OK);
				req.setAttribute("message", "Save success.");
				getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("AddNotify * doPost - error 2: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
