package com.google.appengine.util;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.datastore.*;
import com.google.appengine.entities.*;

public class Helper {

	private Helper() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean checkEmailValid(String emailAddress){
		boolean valid = false;
		String domain = "";
		try {
			domain = MyProperties.getValue("app.domain");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		//check email pattern
        if (!pattern.matcher(emailAddress).matches()) {        	
        	return valid;
        }
	    if(domain.length() > 0){			
			String emailDomain [] = emailAddress.split("@");
			//for(String d : domain.split(",")){
				if(domain.equals(emailDomain[1])){
					valid = true;
				}
			//}
		}
	    EmployeeService es = new EmployeeService(); 
		try {
			/* check if exist in the database */
			Employee emp = es.findEmployeeByColumnName("emailAddress", emailAddress);
			if(emp.getEmailAddress()!=null){
				valid = true;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return valid;
	}
}
