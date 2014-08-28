package com.google.appengine.mct;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.util.ConstantUtils;

@SuppressWarnings("serial")
public class ViewHistoryTable  extends BaseServlet {

	private static final Logger log = Logger.getLogger(ViewHistoryTable.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
//		String pageCount = (String) request.getParameter("pageCount");
		String off = (String) request.getParameter("offset");
//		String footer = (String) request.getParameter("footer");
//		List<Entity> results = (List<Entity>) request.getAttribute("results");
		String regionSelected = (String) request.getAttribute("cri_region"); 
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(History.class.getSimpleName());
		q.addFilter("region", Query.FilterOperator.EQUAL, regionSelected);
		// PreparedQuery contains the methods for fetching query results from the datastore
		PreparedQuery pq = datastore.prepare(q);
		int rowCount = pq.countEntities();
		
		// set offset for the results to be fetched
		int offset = 0;
	    if (off != null)
	      offset = Integer.parseInt(off);
	    
	 // fetch results from datastore based on offset and page size
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(ConstantUtils.PAGE_SIZE);
	    
	 // set footer for the table
	    float pageCount = (float) rowCount / ConstantUtils.PAGE_SIZE;
	    String footer = "";
	    if (pageCount > rowCount / ConstantUtils.PAGE_SIZE)
	      pageCount = (int) pageCount + 1;
	    else
	      pageCount = (int) pageCount;
	    
	    for (int i = 0; i < pageCount; i++) {
	        footer += "<a href=\"#\" onclick=\"fillBody(" + i * ConstantUtils.PAGE_SIZE + ")\">"
	            + (i + 1) + "</a>  ";
	      }

	      if (rowCount > 10) {
	        if ((offset / 10) != (pageCount - 1))
	          footer += "<a href=\"#\" onclick=\"fillBody(" + (offset + 10)
	              + ")\">Next</a>";
	        else
	          footer += "<a href=\"#\" onclick=\"fillBody(" + (pageCount - 1)
	              * ConstantUtils.PAGE_SIZE + ")\">Next</a>";
	      }
	      
	      QueryResultList<Entity> results =  pq.asQueryResultList(fetchOptions.offset(offset));
	      
	      request.setAttribute("pageCount", pageCount);
	      request.setAttribute("results", results);
	      request.setAttribute("footer", footer);
		
		
		try {
			getServletConfig().getServletContext().getRequestDispatcher("/admin-view-history-table.jsp").forward(request, response);
			return;
		} catch (ServletException e) {
			log.error("ViewHistoryTable * doPost - error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
}
