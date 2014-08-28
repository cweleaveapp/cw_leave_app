package com.google.appengine.queue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.mct.BaseServlet;
import com.google.appengine.mct.MCEmployee;
import com.google.appengine.mct.Misc;
import com.google.appengine.mct.TaskStatus;
import com.google.appengine.mct.TaskStatusService;
import com.google.appengine.mct.ViewSupervisor;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class ImpEmpCSVQueue extends BaseServlet {
	private static final Logger log = Logger.getLogger(ImpEmpCSVQueue.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(ImpEmpCSVQueue.class);
		String loginUser = request.getParameter("loginUser");
        String name = request.getParameter("name");
        
        try {
    
            BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));
            BlobstoreInputStream stream = new BlobstoreInputStream(blobKey);
            EmployeeService employeeService = new EmployeeService();
            TaskStatusService	taskStatusService = new TaskStatusService();
                log.debug("import csv data");
                
                CSVReader reader = new CSVReader(new InputStreamReader(stream,"UTF-8"));
                List<String []> myEntries = reader.readAll();
                
                if(myEntries.size() > 0){
                	
                	for(int i = 1; i < myEntries.size(); i++){
                		String [] myArray = myEntries.get(i);
                		if(myArray.length > 7){
                			if(ConstantUtils.INSERT.equals(myArray[8])){
                				MCEmployee emp = new MCEmployee();
                				if(myArray.length >= 0){
                					emp.setEmailAddress(myArray[0]);
                        		}
                        		if(myArray.length > 0){
                        			emp.setFullName(myArray[1]);
                        		}
                        		if(myArray.length > 1){
                        			emp.setJobTitle(myArray[2]);
                        		}
                        		if(myArray.length > 2){
                        			emp.setRegion(myArray[3]);
                        		}
                        		if(myArray.length > 3){
                        			emp.setHiredDate(myArray[4]);
                        		}
                        		if(myArray.length > 4){
                        			emp.setBirthDate(myArray[5]);
                        		}
                        		if(myArray.length > 5){
                        			emp.setResignedDate(myArray[6]);
                        		}
                        		if(myArray.length > 14){
                        			emp.setSupervisor(myArray[7]);
                        		}
                				employeeService.addMCEmployee(emp);
                			}
                			else if(ConstantUtils.UPDATE.equals(myArray[8])){
                				MCEmployee emp = employeeService.findMCEmployeeByColumnName("emailAddress", myArray[0]);
                				
                            	
                        		if(emp != null && StringUtils.isNotBlank(emp.getEmailAddress())){
                        			if(myArray.length >= 0){
                    					emp.setEmailAddress(myArray[0]);
                            		}
                            		if(myArray.length > 0){
                            			emp.setFullName(myArray[1]);
                            		}
                            		if(myArray.length > 1){
                            			emp.setJobTitle(myArray[2]);
                            		}
                            		if(myArray.length > 2){
                            			emp.setRegion(myArray[3]);
                            		}
                            		if(myArray.length > 3){
                            			emp.setHiredDate(myArray[4]);
                            		}
                            		if(myArray.length > 4){
                            			emp.setBirthDate(myArray[5]);
                            		}
                            		if(myArray.length > 5){
                            			emp.setResignedDate(myArray[6]);
                            		}
                            		if(myArray.length > 14){
                            			emp.setSupervisor(myArray[7]);
                            		}
                        			employeeService.updateMCEmployee(emp);
                    			}
                        		else{
                        			MCEmployee employee = new MCEmployee();
                        			if(myArray.length >= 0){
                        				employee.setEmailAddress(myArray[0]);
                            		}
                            		if(myArray.length > 0){
                            			employee.setFullName(myArray[1]);
                            		}
                            		if(myArray.length > 1){
                            			employee.setJobTitle(myArray[2]);
                            		}
                            		if(myArray.length > 2){
                            			employee.setRegion(myArray[3]);
                            		}
                            		if(myArray.length > 3){
                            			employee.setHiredDate(myArray[4]);
                            		}
                            		if(myArray.length > 4){
                            			employee.setBirthDate(myArray[5]);
                            		}
                            		if(myArray.length > 5){
                            			employee.setResignedDate(myArray[6]);
                            		}
                            		if(myArray.length > 6){
                            			employee.setSupervisor(myArray[7]);
                            		}
                    				employeeService.addMCEmployee(employee);
                        		}
                        		
                			}
                			else if(ConstantUtils.DELETE.equals(myArray[8])){
                				employeeService.deleteMCEmployee(myArray[0]);
                			}
                			else{
                				MCEmployee emp = new MCEmployee();
                				if(myArray.length >= 0){
                					emp.setEmailAddress(myArray[0]);
                        		}
                        		if(myArray.length > 0){
                        			emp.setFullName(myArray[1]);
                        		}
                        		if(myArray.length > 1){
                        			emp.setJobTitle(myArray[2]);
                        		}
                        		if(myArray.length > 2){
                        			emp.setRegion(myArray[3]);
                        		}
                        		if(myArray.length > 3){
                        			emp.setHiredDate(myArray[4]);
                        		}
                        		if(myArray.length > 4){
                        			emp.setBirthDate(myArray[5]);
                        		}
                        		if(myArray.length > 5){
                        			emp.setResignedDate(myArray[6]);
                        		}
                        		if(myArray.length > 6){
                        			emp.setSupervisor(myArray[7]);
                        		}
                				employeeService.addMCEmployee(emp);
                			}
                		}
                		else if(myArray.length == 15){
                			MCEmployee emp = new MCEmployee();
                			if(myArray.length >= 0){
            					emp.setEmailAddress(myArray[0]);
                    		}
                    		if(myArray.length > 0){
                    			emp.setFullName(myArray[1]);
                    		}
                    		if(myArray.length > 1){
                    			emp.setJobTitle(myArray[2]);
                    		}
                    		if(myArray.length > 2){
                    			emp.setRegion(myArray[3]);
                    		}
                    		if(myArray.length > 3){
                    			emp.setHiredDate(myArray[4]);
                    		}
                    		if(myArray.length > 4){
                    			emp.setBirthDate(myArray[5]);
                    		}
                    		if(myArray.length > 5){
                    			emp.setResignedDate(myArray[6]);
                    		}
                    		if(myArray.length > 6){
                    			emp.setSupervisor(myArray[7]);
                    		}
            				employeeService.addMCEmployee(emp);
            			
                		}
                		else{
                			log.debug("File Imported Employee Fail");
                			Queue queue = QueueFactory.getQueue("impEmpCSVQueue");
                			queue.purge();
                			Misc.importEmployee(loginUser, name, "fail to import.\n\nPlease ensure the format is correct "
                        			+ "and follow the exact format as sample CSV file.");
                		}
                		
                		
                    }
                    		
                	//File uploaded successfully
                    log.debug("File Imported Employee Success");
                    Misc.importEmployee(loginUser, name, "success import");
                    
                    
                    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	            	 Key taskStatusKey = KeyFactory.createKey(TaskStatus.class.getSimpleName(), "taskStatus");
			        	Entity entity = new Entity(TaskStatus.class.getSimpleName(),taskStatusKey);
			        	entity.setProperty("blobKey", blobKey.getKeyString());
			        	entity.setProperty("status", ConstantUtils.COMPLETE);
			        	datastore.put(entity);
			        	
                    TaskStatus taskStatus = taskStatusService.getStatusByBlobkey(blobKey.getKeyString());
                	taskStatus.setStatus(ConstantUtils.COMPLETE);
                	taskStatusService.update(taskStatus);
                }
                else{
                	//File uploaded fail
                	log.debug("File Imported Fail");
                	Queue queue = QueueFactory.getQueue("impEmpCSVQueue");
        			queue.purge();
                	
        			Misc.importEmployee(loginUser, name, "fail import.\n\nFile imported cannot empty");
                	
                	TaskStatus taskStatus = taskStatusService.getStatusByBlobkey(blobKey.getKeyString());
                	taskStatus.setStatus(ConstantUtils.FAIL);
                	taskStatusService.update(taskStatus);
                }
            
        } catch (Exception ex) {
        	log.debug("File Imported Fail "+ex);
        	Queue queue = QueueFactory.getQueue("impEmpCSVQueue");
			queue.purge();
			Misc.importEmployee(loginUser, name, "fail import. \n\nPlease ensure the format is correct "
        			+ "and follow the exact format as sample CSV file.");
        }          
		
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

}
