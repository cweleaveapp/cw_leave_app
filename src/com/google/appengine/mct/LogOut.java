package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class LogOut extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(LogOut.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.debug(LogOut.class);
		req.getSession().removeAttribute("emailAdd");
		resp.sendRedirect("https://mcleaveapp.appspot.com/sign-in.jsp");
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
