package com.google.appengine.mct;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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

public class ImportEmpLeaveDetails extends BaseServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String radioButton = req.getParameter("docRad");
		System.out.println("ImportEmpLeaveDetails - radioButton selected: " + radioButton);
		int index = 0;
		int nameIndex = 0;
		String region = "";
		String resourceID = "";
		String totalID = "";
		String emailAddress = "";
		String lastYearBalance = "0";
		String entitledAnnual = "0";
		String titleYear = "";
		String noPayLeave = "0";
		String noPayLeaveID = "";
		String sickLeave = "0";
		String sickLeaveID = "";
		String annualLeave = "0";
		String annualLeaveID = "";
		String compensationLeave = "0";
		String compensationLeaveID = "";
		String entitledCompensation = "0";
		String entitledCompensationID = "";
		String birthdayLeave = "0";
		String birthdayLeaveID = "";
		String weddingLeave = "0";
		String weddingLeaveID = "";
		String maternityLeave = "0";
		String maternityLeaveID = "";
		String compassionateLeave = "0";
		String compassionateLeaveID = "";
		Vector leaveTypeVec = new Vector();
		String domain = "";
		String spdSvAcc = "";
		String spdSvAccPwd = "";
		
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
				System.out.println("ImportEmpLeaveDetails first parent title: " + resultEntry.getParentLinks().get(0).getTitle());

				String folderId = resultEntry.getParentLinks().get(0).getHref();
				URL urlFolder = new URL(folderId);
				DocumentListEntry folderEntry = myService.getEntry(urlFolder, DocumentListEntry.class);
				System.out.println("ImportEmpLeaveDetails second parent title: " + folderEntry.getParentLinks().get(0).getTitle());
//				region = folderEntry.getParentLinks().get(0).getTitle();
//				if (region.equalsIgnoreCase("MCKL")) {
//					region = "Malaysia";
//				} else if (region.equalsIgnoreCase("MCCN")) {
//					region = "China";
//				} else if (region.equalsIgnoreCase("MCTW")) {
//					region = "Taiwan";
//				} else if (region.equalsIgnoreCase("MCHK")) {
//					region = "Hong Kong";
//				} else if (region.equalsIgnoreCase("MCSG")) {
//					region = "Singapore";
//				}

				SpreadsheetService service = new SpreadsheetService("wise");
				service.setUserCredentials(spdSvAcc, spdSvAccPwd);
				URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
				SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
				List<SpreadsheetEntry> spreadsheets = feed.getEntries();
				for (int i = 0; i < spreadsheets.size(); i++) {
					SpreadsheetEntry entry = spreadsheets.get(i);
					if (entry.getSpreadsheetLink().getHref().contains(resourceID)) {
						List<WorksheetEntry> worksheets = entry.getWorksheets();
						WorksheetEntry worksheet = worksheets.get(0);
						String title = worksheet.getTitle().getPlainText();
						nameIndex = title.indexOf(" ");
						titleYear = title.substring(nameIndex+1, title.length());
						emailAddress = title.substring(0, nameIndex) + domain;
						URL cellFeedUrl = worksheet.getCellFeedUrl();
						CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
						for (CellEntry cell : cellFeed.getEntries()) {
							String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
							if (cell.getCell().getValue().equalsIgnoreCase("Total")) {
								totalID = shortId.substring(0, shortId.indexOf("C")+1);
								entitledCompensationID = totalID + "6";
								noPayLeaveID = totalID + "7";
								sickLeaveID = totalID + "8";
								annualLeaveID = totalID + "9";
								compensationLeaveID = totalID + "10";
							}
							if (cell.getCell().getValue().equalsIgnoreCase("Birthday Leave") ||
									cell.getCell().getValue().equalsIgnoreCase("Birthday")) {
								birthdayLeaveID = shortId.substring(0, shortId.indexOf("C")+1) + "11";
								leaveTypeVec.add(birthdayLeaveID + "|" + "birthday");
							}
							if (cell.getCell().getValue().equalsIgnoreCase("Maternity Leave") ||
									cell.getCell().getValue().equalsIgnoreCase("Maternity")) {
								maternityLeaveID = shortId.substring(0, shortId.indexOf("C")+1) + "11";
								leaveTypeVec.add(maternityLeaveID + "|" + "maternity");
							}
							if (cell.getCell().getValue().equalsIgnoreCase("Wedding Leave") ||
									cell.getCell().getValue().equalsIgnoreCase("Wedding")) {
								weddingLeaveID = shortId.substring(0, shortId.indexOf("C")+1) + "11";
								leaveTypeVec.add(weddingLeaveID + "|" + "wedding");
							}
							if (cell.getCell().getValue().equalsIgnoreCase("Compassionate Leave") ||
									cell.getCell().getValue().equalsIgnoreCase("Compassionate")) {
								compassionateLeaveID = shortId.substring(0, shortId.indexOf("C")+1) + "11";
								leaveTypeVec.add(compassionateLeaveID + "|" + "compassionate");
							}
							if (shortId.equalsIgnoreCase("R7C12")) {
								lastYearBalance = cell.getCell().getValue();
							}
							if (shortId.equalsIgnoreCase("R8C6")) {
								entitledAnnual = cell.getCell().getValue();
							}
							if (shortId.equalsIgnoreCase(noPayLeaveID)) {
								noPayLeave = cell.getCell().getValue();
							}
							if (shortId.equalsIgnoreCase(sickLeaveID)) {
								sickLeave = cell.getCell().getValue();
							}
							if (shortId.equalsIgnoreCase(annualLeaveID)) {
								annualLeave = cell.getCell().getValue();
							}
							if (shortId.equalsIgnoreCase(compensationLeaveID)) {
								compensationLeave = cell.getCell().getValue();
							}
							if (shortId.equalsIgnoreCase(entitledCompensationID)) {
								String tmp = cell.getCell().getValue();
								int tmpNum = Integer.parseInt(tmp);
								int tmpAnn = Integer.parseInt(entitledAnnual);
								int bal = tmpNum - tmpAnn;
								entitledCompensation = Integer.toString(bal);
								System.out.println("ImportEmpLeaveDetails tmpNum = " + tmpNum);
								System.out.println("ImportEmpLeaveDetails tmpAnn = " + entitledAnnual);
								System.out.println("ImportEmpLeaveDetails bal = " + bal);
							}
						}
						Collections.sort(leaveTypeVec);
						for (CellEntry cell : cellFeed.getEntries()) {
							for (int j=0; j<leaveTypeVec.size(); j++) {
								String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
								String tmpStr = leaveTypeVec.elementAt(j).toString();
								if (shortId.equalsIgnoreCase(tmpStr.substring(0, tmpStr.indexOf("|")))) {
									String type = tmpStr.substring(tmpStr.indexOf("|")+1, tmpStr.length());
									if (type.equalsIgnoreCase("birthday")) {
										birthdayLeave = cell.getCell().getValue();
									} else if (type.equalsIgnoreCase("wedding")) {
										weddingLeave = cell.getCell().getValue();
									} else if (type.equalsIgnoreCase("maternity")) {
										maternityLeave = cell.getCell().getValue();
									} else if (type.equalsIgnoreCase("compassionate")) {
										compassionateLeave = cell.getCell().getValue();
									}
								}
							}
						}
						break;
					}
				}
			} catch (Exception e) {
				System.err.println("ImportEmpLeaveDetails error: " + e.getMessage());
				e.printStackTrace();
			}
			req.setAttribute("emailAddress", emailAddress);
			req.setAttribute("lastYearBal", lastYearBalance);
			req.setAttribute("entitledAnnual", entitledAnnual);
			req.setAttribute("year", titleYear);
			req.setAttribute("noPayLeave", noPayLeave);
			req.setAttribute("sickLeave", sickLeave);
			req.setAttribute("annualLeave", annualLeave);
			req.setAttribute("compensationLeave", compensationLeave);
			req.setAttribute("entitledComp", entitledCompensation);
			req.setAttribute("birthdayLeave", birthdayLeave);
			req.setAttribute("maternityLeave", maternityLeave);
			req.setAttribute("compassionateLeave", compassionateLeave);
			req.setAttribute("weddingLeave", weddingLeave);
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/admin-add-emp-leave-details.jsp").forward(req, resp);
			} catch (ServletException e) {
				System.err.println("ImportEmpLeaveDetails * doPost - error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}
