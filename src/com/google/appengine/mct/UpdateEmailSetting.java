package com.google.appengine.mct;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.appengine.datatable.DataTableModel;
import com.google.appengine.datatable.DataTablesUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("serial")
public class UpdateEmailSetting extends BaseServlet {
	private static final Logger log = Logger.getLogger(UpdateEmailSetting.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UpdateEmailSetting.class);
		String regionSelected = request.getParameter("cri_region");
		DataTableModel dataTableModel = DataTablesUtility.getParam(request);
		String sEcho = dataTableModel.sEcho;
		int iTotalRecords = 0; // total number of records (unfiltered)
		int iTotalDisplayRecords = 0; //value will be set when code filters companies by keyword
		JsonArray data = new JsonArray(); //data that will be shown in the table
		
		List<EmailSetting> emailSettingList = new LinkedList<EmailSetting>();
		List<EmailSetting> filterList = new LinkedList<EmailSetting>();
		
		EmailSettingService ss = new EmailSettingService();
		List<EmailSetting> resultList = ss.getEmailSettingList();
		
		for(EmailSetting filter : resultList){
			if(filter.getRegion().contains(regionSelected)){
				filterList.add(filter);
			}
		}
		
		for(EmailSetting result : filterList){
			if((StringUtils.lowerCase(result.getEmailAddress()).contains(dataTableModel.sSearch.toLowerCase()))||
					(StringUtils.lowerCase(result.getRegion()).contains(dataTableModel.sSearch.toLowerCase()))){
				emailSettingList.add(result); // add emailSettingList that matches given search criterion
			}
		}
		
		iTotalDisplayRecords = emailSettingList.size(); // number of emailSettingList that match search criterion should be returned
		
		final int sortColumnIndex = dataTableModel.iSortColumnIndex;
		final int sortDirection = dataTableModel.sSortDirection.equals("asc") ? -1 : 1;
		
		Collections.sort(emailSettingList, new Comparator<EmailSetting>(){
			@Override
			public int compare(EmailSetting c1, EmailSetting c2) {	
				switch(sortColumnIndex){
				case 0:
					return c1.getEmailAddress().compareTo(c2.getEmailAddress()) * sortDirection;
				case 1:
					return c1.getRegion().compareTo(c2.getRegion()) * sortDirection;
				}
				return 0;
			}
		});
		
		if(emailSettingList.size()< dataTableModel.iDisplayStart + dataTableModel.iDisplayLength) {
			emailSettingList = emailSettingList.subList(dataTableModel.iDisplayStart, emailSettingList.size());
		} else {
			emailSettingList = emailSettingList.subList(dataTableModel.iDisplayStart, dataTableModel.iDisplayStart + dataTableModel.iDisplayLength);
		}
		
		try {
			JsonObject jsonResponse = new JsonObject();			
			jsonResponse.addProperty("sEcho", sEcho);
			jsonResponse.addProperty("iTotalRecords", iTotalRecords);
			jsonResponse.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(EmailSetting emailSetting : emailSettingList){
				JsonArray row = new JsonArray();
				row.add(new JsonPrimitive("<a href=\"UpdateEmailSettingAction?emailAddress="+
				emailSetting.getEmailAddress() + "\"><i class=\"icon-edit\"></i></a>"));
				
				row.add(new JsonPrimitive("<input type=\"checkbox\" name=\"dellist\"  value=\"" 
				+ emailSetting.getEmailAddress() + "\"" + ">"));
				
				
				row.add(new JsonPrimitive(emailSetting.getEmailAddress()));
				row.add(new JsonPrimitive(emailSetting.getRegion()));
				data.add(row);
			}
			jsonResponse.add("aaData", data);
			
			response.setContentType("application/Json");
			response.getWriter().print(jsonResponse.toString());
			
		} catch (JsonIOException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print(e.getMessage());
		}
		
	}
	
}
