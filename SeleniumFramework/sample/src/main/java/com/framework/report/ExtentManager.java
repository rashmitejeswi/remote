package com.framework.report;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.NetworkMode;

public class ExtentManager {

private static ExtentReports extent;
    public static ExtentReports getInstance() {
       if (extent == null) {
          extent = new ExtentReports(System.getProperty("user.dir")+"\\Report\\Result.html", true,NetworkMode.OFFLINE );
          extent.config()
            .documentTitle("Selenium Framrwork Execution Report")
            .reportName("Regression")
            .reportHeadline("Automation Report");
          extent
            .addSystemInfo("Selenium Version", "2.47")
            .addSystemInfo("Environment", "QA");
        }
        return extent;
    }
}