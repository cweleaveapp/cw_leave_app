package com.google.appengine.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.appengine.mct.ApplyLeave;

/**
 * This class used to get value in configuration file
 *
 */
public class MyProperties {
	
	private static final Logger log = Logger.getLogger(MyProperties.class);
	
	/**
	 * To get the error message from common configuration file
	 * @param key the key
	 * @return The value from common configuration file by key
	 * @throws IOException If input or output exception occurred
	 */
	public static String getErrorMsg(String key)
	{
		Properties pro = new Properties();
		String val = null;
		try {
			pro.load(MyProperties.class.getResourceAsStream("/error.properties"));
			val = pro.getProperty(key);
		} catch (IOException e) {
			log.error("Error in reading config file"); 
		}
		return val;
	}
	
	/**
	 * To get the value from common configuration file
	 * @param key the key
	 * @return The value from common configuration file by key
	 * @throws IOException If input or output exception occurred
	 */
	public static String getValue(String key) throws IOException
	{
		Properties pro = new Properties();
		String val = null;
		try {
			pro.load(MyProperties.class.getResourceAsStream("/config.properties"));
			val = pro.getProperty(key);
		} catch (IOException e) {
			log.error("Error in reading config file"); 
			throw e;
		}
		return val;
	}
}
