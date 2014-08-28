package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class UpdateEmailSettingAction extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateEmailSettingAction.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateEmailSettingAction.class);
		String emailAddress = req.getParameter("emailAddress");
		String regionSelected[] = req.getParameterValues("region[]");
		String strRegion = "";
		
		if (regionSelected.length > 0 && regionSelected != null) {
			for (int i=0; i<regionSelected.length; i++) {
				if (i == 0) {
					strRegion = regionSelected[i].toString().replaceAll("-", " ");
				} else {
					strRegion = strRegion + "," + regionSelected[i].toString().replaceAll("-", " ");
				}
			}
		}
		
		try {
			EmailSettingService ss = new EmailSettingService();
			ss.updateEmailSetting(emailAddress, strRegion);
			req.setAttribute("feedback", ConstantUtils.OK);
			req.setAttribute("message", "Update success.");
			getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
    		return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(UpdateEmailSetting.class);
		// extracting data from the radio button field
		String emailAddress = req.getParameter("emailAddress");
		
		if (emailAddress != null) {
			EmailSettingService ess = new EmailSettingService();
			
			for(EmailSetting s : ess.getEmailSettingList()){
				if (s.getEmailAddress().equalsIgnoreCase(emailAddress)) {
					req.setAttribute("emailAddress", s.getEmailAddress());
				}
			}
			
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/update-email-setting-action.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateEmailSetting - doPost error: " + e.getMessage());
				e.printStackTrace();
			}
			
		} else {
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/update-email-setting.jsp").forward(req, resp);
				return;
			} catch (ServletException e) {
				log.error("UpdateEmailSetting * doPost - error2: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
}
