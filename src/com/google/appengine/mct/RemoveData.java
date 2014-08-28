package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class RemoveData extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(RemoveData.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {	
		log.debug(RemoveData.class);
		// extracting data from the checkbox field
		String[] history = req.getParameterValues("history");
		String[] region = req.getParameterValues("region");
		String[] holidays = req.getParameterValues("holidays");
		String[] supervisor = req.getParameterValues("supervisor");
		
					if (history != null && history.length > 0){
					
						HistoryService hs = new HistoryService();
						hs.deleteHistory();
					}
					
					if((region != null && region.length > 0)){
						RegionsService rs = new RegionsService();
						rs.deleteRegion();
					}
					
					if(holidays != null && holidays.length > 0){
						RegionalHolidaysService rhs = new RegionalHolidaysService();
						rhs.deleteRegionalHolidays();
					}
					
					if(supervisor != null && supervisor.length > 0){
						MCSupervisorService ss = new MCSupervisorService();
						ss.deleteSupervisor();
					}
					
					
				try {
					getServletConfig().getServletContext().getRequestDispatcher("/remove-data.jsp").forward(req, resp);
					return;
				} catch (ServletException e) {
					log.error("DeleteHistory * doPost - error1: " + e.getMessage());
					e.printStackTrace();
				}
					
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}

}
