package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class UpdateSupervisorAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateSupervisorAction.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateSupervisorAction.class);
		String emailAddress = req.getParameter("emailAddress");
		String regionSelected[] = req.getParameterValues("region[]");
		String strRegion = "";
		
		if (regionSelected.length > 0 && regionSelected != null) {
			for (int i=0; i<regionSelected.length; i++) {
				if (i == 0) {
					strRegion = regionSelected[i].toString().replaceAll("-", " ");
				} else {
					strRegion = strRegion + ", " + regionSelected[i].toString().replaceAll("-", " ");
				}
			}
		}
		
		try {
			MCSupervisorService ss = new MCSupervisorService();
			ss.updateSupervisor(emailAddress, strRegion);
			req.setAttribute("feedback", ConstantUtils.OK);
			req.setAttribute("message", "Update success.");
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
    		return;
		} catch (EntityNotFoundException e) {
			log.error("UpdateSupervisorAction - doPost: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		resp.sendRedirect("/admin-update-supervisor.jsp");
//		return;
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	}
}
