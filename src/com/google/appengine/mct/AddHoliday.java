package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class AddHoliday extends BaseServlet {

	private static final Logger log = Logger.getLogger(AddHoliday.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(AddHoliday.class);
		boolean exist = false;
		String holDate = req.getParameter("holDate");
		String desc = req.getParameter("desc");
		String region = req.getParameter("region");
		
		try {
			if (region != "") {
				RegionalHolidaysService rhs = new RegionalHolidaysService();
				RegionalHolidaysService ehs = new RegionalHolidaysService();
				for (RegionalHolidays hol : ehs.getRegionalHolidays()) {
					if (holDate.equalsIgnoreCase(hol.getDate())) {
						if (region.equalsIgnoreCase(hol.getRegion())) {
							exist = true;
						}
					}
				}
				if (exist == false) {
					rhs.addRegionalHoliday(holDate, desc, region);
					req.setAttribute("feedback", ConstantUtils.OK);
					req.setAttribute("message", "Save success.");
					getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	        		return;
//					Misc misc = new Misc();
					/* Add to Google Calendar */
//					misc.regionalHolidayCalendar(holDate, desc, region);
				} else {
					try {
						req.setAttribute("feedback", ConstantUtils.ERROR);
						req.setAttribute("message", "This holiday record already exist in the database.");
		        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
		        		return;
					} catch (ServletException e) {
						log.error("AddHoliday * doPost - error 2: " + e.getMessage());
						e.printStackTrace();
					}
				}
			} else {
				try {
					req.setAttribute("feedback", ConstantUtils.ERROR);
					req.setAttribute("message", "This holiday record already exist in the database.");
	        		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
	        		return;
				} catch (ServletException e) {
					log.error("AddHoliday * doPost - error 1: " + e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			resp.sendRedirect("/admin-view-holiday.jsp");
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
