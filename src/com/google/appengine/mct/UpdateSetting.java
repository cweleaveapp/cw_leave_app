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

import com.google.appengine.api.datastore.EntityNotFoundException;

@SuppressWarnings("serial")
public class UpdateSetting extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UpdateSetting.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		log.debug(UpdateSetting.class);
		
		String calServiceAccPass = req.getParameter("calServiceAccPass");
		String sysAdminEmailAdd = req.getParameter("sysAdminEmailAdd");
		String appDomain = req.getParameter("appDomain");
		String appAdminAcc = req.getParameter("appAdminAcc");
		String appAdminAccPass = req.getParameter("appAdminAccPass");
		String calServiceAcc = req.getParameter("calServiceAcc");
		String adminEmailAcc = req.getParameter("adminEmailAcc");
		String spreadsheetServiceAcc = req.getParameter("spreadsheetServiceAcc");
		String spreadsheetServiceAccPass = req.getParameter("spreadsheetServiceAccPass");
		String emailSenderAcc = req.getParameter("emailSenderAcc");
		
		try {
			
			SettingService ss = new SettingService();
			Map<String, Object> errorMap = new HashMap<String, Object>();
			errorMap.clear();
			Properties properties = new Properties();
			try {
			  properties.load(this.getClass().getClassLoader().getResourceAsStream("error.properties"));
			} catch (IOException e) {
			  e.printStackTrace();
			}
				
				ss.updateSetting( calServiceAccPass,  sysAdminEmailAdd,  appDomain,
							 appAdminAcc,  appAdminAccPass,  calServiceAcc,  adminEmailAcc,
							 spreadsheetServiceAcc,  spreadsheetServiceAccPass,  emailSenderAcc
							 );
				
				log.error(""+properties.getProperty("update.success"));
    			errorMap.put("update.success", properties.getProperty("update.success"));
				req.setAttribute("errorMap",errorMap);
				getServletConfig().getServletContext().getRequestDispatcher("/feedback-map.jsp").forward(req, resp);
				return;
			
		} catch (EntityNotFoundException ee) {
			log.error("UpdateSetting - doPost error: " + ee.getMessage());
			ee.printStackTrace();
		} catch (ServletException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateSetting.class);
		SettingService ss = new SettingService();
		List<Setting> result = ss.getSetting();
		for(Setting setting : result){
			request.setAttribute("calServiceAccPass", setting.getCalServiceAccPass());
			request.setAttribute("sysAdminEmailAdd", setting.getSysAdminEmailAdd());
			request.setAttribute("appDomain", setting.getAppDomain());
			request.setAttribute("appAdminAcc", setting.getAppAdminAcc());
			request.setAttribute("appAdminAccPass", setting.getAppAdminAccPass());
			request.setAttribute("calServiceAcc", setting.getCalServiceAcc());
			request.setAttribute("adminEmailAcc", setting.getAdminEmailAcc());
			request.setAttribute("spreadsheetServiceAcc", setting.getSpreadsheetServiceAcc());
			request.setAttribute("spreadsheetServiceAccPass", setting.getSpreadsheetServiceAccPass());
			request.setAttribute("emailSenderAcc", setting.getEmailSenderAcc());
		}
		
		try{
			getServletConfig().getServletContext().getRequestDispatcher("/config-setting-page.jsp").forward(request, response);
			return;
		} catch (ServletException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
}
