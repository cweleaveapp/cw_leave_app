package com.google.appengine.mct;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.datastore.DataStoreUtil;
import com.google.appengine.entities.LeaveRequest;
import com.google.appengine.util.ConstantUtils;

public class UploadFile extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(UploadFile.class);
	
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    	String referer = req.getHeader("referer");
    	List<String> blobKeys = new ArrayList<String>();
    	List<String> fileNames = new ArrayList<String>();
    	Map<String, List<FileInfo>> fileInfoMap = blobstoreService.getFileInfos(req);    	
        Map<String, List<BlobKey>> blobsMap = blobstoreService.getUploads(req);
        
        for(Entry<String, List<BlobKey>> blobs : blobsMap.entrySet()){
        	List<FileInfo> fileInfoList = fileInfoMap.get(blobs.getKey());
        	log.debug("No of files upload : " + blobs.getValue().size());
        	for(int i=0; i < blobs.getValue().size(); i++){
        		BlobKey blobKey =  blobs.getValue().get(i);
        		log.debug("The upload file name : " + fileInfoList.get(i).getFilename());
        		log.debug("The upload blob key : " + blobKey.getKeyString());
        		if(blobs.getValue().size() > 0){
        			blobKeys.add(blobKey.getKeyString());
        			fileNames.add(fileInfoList.get(i).getFilename());
        		}
        	}
        }
        LeaveRequest lreq = (LeaveRequest) req.getSession().getAttribute("LeaveRequest");
        lreq.setBlobKeys(blobKeys);
        lreq.setAttachments(fileNames);      		
        req.getSession().setAttribute("LeaveRequest", lreq);
        resp.sendRedirect("/AdjustLeaveBalance");
//      getServletConfig().getServletContext().getRequestDispatcher("/leave/adj.jsp").forward(req, resp);
        return;
//        DataStoreUtil util = new DataStoreUtil();
//        if(leave!= null) {
//        	
//        	util.getDatastore().put(leave);
//        }
//      req.setAttribute("feedback", ConstantUtils.OK);
//		req.setAttribute("message", "Save success");
//		getServletConfig().getServletContext().getRequestDispatcher("/feedback.jsp").forward(req, resp);
//		return;
        //res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
//            res.sendRedirect("/");
//        } else {
//            res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
//        }
    }
}
