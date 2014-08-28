package com.google.appengine.mct;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class DeleteEmailSetting  extends BaseServlet {
	private static final Logger log = Logger.getLogger(DeleteEmailSetting.class);
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(DeleteEmailSetting.class);
		
		Vector delSupVec = new Vector();
		// extracting data from the checkbox field
		String[] dellist = req.getParameterValues("dellist[]");
		for (int i=0; i<dellist.length; i++) {
			delSupVec.add(dellist[i]);
		}
		for (int j=0; j<delSupVec.size(); j++) {
			String name = delSupVec.elementAt(j).toString();
			EmailSettingService ss = new EmailSettingService();
			for (EmailSetting sup : ss.getEmailSettingList()) {
				if (sup.getEmailAddress().equalsIgnoreCase(name)) {
					ss.deleteEmailSetting(name);
				}				
			}
		}
		resp.sendRedirect("/update-email-setting.jsp");
		return;
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(DeleteEmailSetting.class);
	}

}
