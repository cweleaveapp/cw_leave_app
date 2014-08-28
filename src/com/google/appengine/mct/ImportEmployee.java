package com.google.appengine.mct;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.util.ConstantUtils;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class ImportEmployee extends BaseServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String radioButton = req.getParameter("docRad");
		System.out.println("ImportEmployee - radioButton selected: " + radioButton);
		int index = 0;
		String mth = "", dayDate = "", year = "";
		int nameIndex = 0;
		int firstIndex = 0;
		int secondIndex = 0;
		String region = "";
		String resourceID = "";
		String employeeName = "";
		String birthDate = "";
		String hiredDate = "";
		String emailAddress = "";
		String domain = "";
		String spdSvAcc = "";
		String spdSvAccPwd = "";
		String regAbb = "";
		
		SettingService ss = new SettingService();
		for (Setting set : ss.getSetting()) {
			if (ConstantUtils.APP_DOMAIN.equals(set.getAppDomain())) {
				domain = set.getAppDomain();
				domain = "@" + domain;
			} else if (ConstantUtils.APP_ADMIN_ACCOUNT.equals(set.getAppAdminAcc())) {
				spdSvAcc = set.getAppAdminAcc();
			} else if (ConstantUtils.APP_ADMIN_ACC_PASS.equals(set.getAppAdminAccPass())) {
				spdSvAccPwd = set.getAppAdminAccPass();
			}
		}
		
		index = radioButton.indexOf("key=");
		resourceID = radioButton.substring(index+4, radioButton.length());
		if (radioButton != null) {
			/* Get the document from GDoc */
			try {
				DocsService myService = new DocsService("wise");
				myService.setUserCredentials(spdSvAcc, spdSvAccPwd);
				myService.useSsl();
				myService.setConnectTimeout(0);
				URL url = new URL("http://docs.google.com/feeds/default/private/full/" + resourceID);
				DocumentListEntry resultEntry = myService.getEntry(url, DocumentListEntry.class);
				String folderId = resultEntry.getParentLinks().get(0).getHref();
				URL urlFolder = new URL(folderId);
				DocumentListEntry folderEntry = myService.getEntry(urlFolder, DocumentListEntry.class);
				region = folderEntry.getParentLinks().get(0).getTitle();
				
//				RegionsService rs = new RegionsService();
//				for (Regions reg : rs.getRegions()) {
//					if (reg.getRegionAbbreviation().equalsIgnoreCase(region)) {
//						region = reg.getRegion();
//					}
//				}

				SpreadsheetService service = new SpreadsheetService("wise");
				service.setUserCredentials(spdSvAcc, spdSvAccPwd);
				URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
				SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
				List<SpreadsheetEntry> spreadsheets = feed.getEntries();
				for (int i = 1; i < spreadsheets.size(); i++) {
					SpreadsheetEntry entry = spreadsheets.get(i);
					if (entry.getSpreadsheetLink().getHref().contains(resourceID)) {
//						System.out.println("ImportEmployee - spreadsheet: " + entry.getTitle().getPlainText());
//						System.out.println("ImportEmployee - spreadsheetLink: " + entry.getSpreadsheetLink().getHref());
						List<WorksheetEntry> worksheets = entry.getWorksheets();
						WorksheetEntry worksheet = worksheets.get(0);
						String title = worksheet.getTitle().getPlainText();
//						int rowCount = worksheet.getRowCount();
//						int colCount = worksheet.getColCount();
//						System.out.println("ImportEmployee title: " + title + "- rows:" + rowCount + " cols: " + colCount);
						nameIndex = title.indexOf(" ");
						emailAddress = title.substring(0, nameIndex) + domain;
						URL cellFeedUrl = worksheet.getCellFeedUrl();
						CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
						for (CellEntry cell : cellFeed.getEntries()) {
							String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
							if (shortId.equalsIgnoreCase("R4C2")) {
								employeeName = cell.getCell().getValue();
							} else if (shortId.equalsIgnoreCase("R4C11")) {
								birthDate = cell.getCell().getValue();
								firstIndex = birthDate.indexOf("-");
								secondIndex = birthDate.lastIndexOf("-");
								mth = birthDate.substring(0, firstIndex);
								dayDate = birthDate.substring(firstIndex+1, secondIndex);
								year = birthDate.substring(secondIndex+1, birthDate.length());
								birthDate = dayDate + "-" + mth + "-" + year;
							} else if (shortId.equalsIgnoreCase("R3C8")) {
								hiredDate = cell.getCell().getValue();
								firstIndex = hiredDate.indexOf("/");
								secondIndex = hiredDate.lastIndexOf("/");
								mth = hiredDate.substring(0, firstIndex);
								dayDate = hiredDate.substring(firstIndex+1, secondIndex);
								year = hiredDate.substring(secondIndex+1, hiredDate.length());
								hiredDate = dayDate + "-" + mth + "-" + year;
								hiredDate = "";
							}
						}
						break;
					}
				}
			} catch (Exception e) {
				System.err.println("ImportEmployee error: " + e.getMessage());
				e.printStackTrace();
			}
			req.setAttribute("emailAddress", emailAddress);
			req.setAttribute("region", region);
			req.setAttribute("fullName", employeeName);
			req.setAttribute("birthDate", birthDate);
			req.setAttribute("hiredDate", hiredDate);
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp.jsp").forward(req, resp);
			} catch (ServletException e) {
				System.err.println("ImportEmployee * doPost - error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
