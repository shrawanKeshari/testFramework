package com.gemini.solutions.extentReporting;

import com.gemini.solutions.util.DateTimeUtil;
import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {

	private static ExtentReports extent;

	public synchronized static ExtentReports getReporter() {
		if (extent == null) {
			String workingDirectory = System.getProperty("user.dir");
			System.out.println("CurrentDirectory " + workingDirectory);
			extent = new ExtentReports(workingDirectory + "/ExtentReports/ExtentReportResults"
					+ DateTimeUtil.getFormattedDateTime("dd-MMM-yyyy_HH-mm-ss.SSS") + ".html", true);
		}

		return extent;
	}

}
