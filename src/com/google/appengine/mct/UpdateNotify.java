package com.google.appengine.mct;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.commons.lang.math.NumberUtils;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class UpdateNotify extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateNotify.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		log.debug(UpdateNotify.class);		
		String addNotifyFreq = req.getParameter("addNotifyFreq");
		
		// Only numeric number are allowed
		if(!NumberUtils.isNumber(addNotifyFreq)){
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
		try {
			
			NotifyService ns = new NotifyService();
			Map<String, Object> errorMap = new HashMap<String, Object>();
			errorMap.clear();
			Properties properties = new Properties();
			try {
				properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
				
				ns.updateNotify(addNotifyFreq);				
				log.error(""+properties.getProperty("update.success"));
    			errorMap.put("update.success", properties.getProperty("update.success"));
				req.setAttribute("errorMap",errorMap);
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
			
		} catch (EntityNotFoundException ee) {
			log.error("UpdateNotify - doPost error: " + ee.getMessage());
			ee.printStackTrace();
		} catch (ServletException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateNotify.class);
		NotifyService ns = new NotifyService();
		List<Notify> result = ns.getNotify();
		for(Notify notify : result){
			request.setAttribute("addNotifyFreq", notify.getAddNotifyFreq());
		}
		
		try{
			getServletConfig().getServletContext().getRequestDispatcher("/admin-update-notify-frequency.jsp").forward(request, response);
			return;
		} catch (ServletException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
}
