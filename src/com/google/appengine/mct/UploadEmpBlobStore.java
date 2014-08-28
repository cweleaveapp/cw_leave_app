package com.google.appengine.mct;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.datastore.EmployeeService;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class UploadEmpBlobStore  extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(UploadEmpBlobStore.class);
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug(UploadEmpBlobStore.class);
		log.debug("uploadEmpBlobStore");
//		String loginUser = (String)request.getSession().getAttribute("loginUser");
		String loginUser = "damon.leong@hkmci.com";
		EmployeeService empService = new EmployeeService();
		MCEmployee emp = empService.findMCEmployeeByColumnName("emailAddress", loginUser);
		String name = "";
		// to avoid first time import error
		if(StringUtils.isNotBlank(emp.getFullName())){
			name = emp.getFullName();
		}
		else{
			name = loginUser;
		}
		request.setCharacterEncoding("UTF-8");
		
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
		Map<String, List<BlobInfo>> blobInfo = blobstoreService.getBlobInfos(request);
		
		BlobInfo info = blobInfo.get("file").get(0);
		log.debug("info.getFilename() "+info.getFilename());
		log.debug("info.getSize() "+info.getSize());
		
		if(info.getSize() > 1000000){
			response.sendRedirect("/main/home/upload-fail-filesize.jsp");
		}
		else{
			if(blobs != null && !blobs.isEmpty()){
				 BlobKey blobKey = blobs.get("file").get(0);

			        if (blobKey == null) {
			        	request.setAttribute("result",ConstantUtils.FAIL);
			        	request.setAttribute("message", "Please select file to import");
			        	try {
			        		getServletConfig().getServletContext().getRequestDispatcher("/upload-status.jsp").forward(request, response);
			        		return;
			        	} catch (Exception e1) {
			    			log.error("OrgChart error: " + e1.getMessage());
			    			e1.printStackTrace();
			    		}
			        } else {
			        	
			        	BlobstoreInputStream stream = new BlobstoreInputStream(blobKey);
			        	 CSVReader reader = new CSVReader(new InputStreamReader(stream,"UTF-8"));
			             List<String []> myEntries = reader.readAll();
			             
			             if(myEntries.size() > 0){
			            	 TaskStatus taskStatus = new TaskStatus();
				             	taskStatus.setBlobKey(blobKey.getKeyString());
				             	taskStatus.setStatus(ConstantUtils.PENDING);
			            	 TaskStatusService taskStatusService = new TaskStatusService();
			            	 taskStatusService.save(taskStatus);
			            	 
					        	request.getSession().setAttribute("blobKey", blobKey.getKeyString());
					        	request.setAttribute("result",ConstantUtils.SUCCESS);
					        	request.setAttribute("message", "File upload success, data import under process ...");
					        	try {
					        		getServletConfig().getServletContext().getRequestDispatcher("/upload-status.jsp").forward(request, response);
					        		return;
					        	} catch (Exception e1) {
					    			log.error("UploadEmpBlobStore error: " + e1.getMessage());
					    			e1.printStackTrace();
					    		}
					        	
					        	Queue queue = QueueFactory.getQueue("ImpEmpCSVQueue");
					 			queue.add(withUrl("/impEmpCSVQueue")
					 					.param("blob-key", blobKey.getKeyString())
					 					.param("loginUser", loginUser)
					 					.param("name", name)
					 					.param("fileName", info.getFilename())
//					 					.header("Host", BackendServiceFactory.getBackendService().getBackendAddress("import-employee-data"))
					 					.method(Method.POST));
					 			log.debug("impEmpCSVQueue start ...");
			             }
			             else{
			            	 request.setAttribute("result",ConstantUtils.FAIL);
					        	request.setAttribute("message", "Please fail to import, please try again");
			            	 try {
					        		getServletConfig().getServletContext().getRequestDispatcher("/upload-status.jsp").forward(request, response);
					        		return;
					        	} catch (Exception e1) {
					    			log.error("UploadEmpBlobStore error: " + e1.getMessage());
					    			e1.printStackTrace();
					    		}
			             }
			             
			        }
			}
			else{
				 request.setAttribute("result",ConstantUtils.FAIL);
		        	request.setAttribute("message", "Please fail to import, please try again");
         	 try {
		        		getServletConfig().getServletContext().getRequestDispatcher("/upload-status.jsp").forward(request, response);
		        		return;
		        	} catch (Exception e1) {
		    			log.error("UploadEmpBlobStore error: " + e1.getMessage());
		    			e1.printStackTrace();
		    		}
			}
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}

	
	
	
	
}
