/**
 * 
 */
package com.framework.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.framework.report.DetailedLogs;
import com.google.common.base.Optional;

/**
 * CommonFunctionLib.java contains general purpose functions
 */
public class CommonFunctionLib {
	public WebDriver driver;
	WebDriverWait wait;
	Properties properties;
	DesiredCapabilities objCapabilities;
	ChromeOptions chromeoptions;
	DetailedLogs AppLogs = new DetailedLogs();

	public Set<String> arrKnownBrowserHwnd; 
	public String hwndFirstWindow; 
	public String hwndMostRecentWindow; 
	public Boolean locationServiceEnabled;
	public Boolean doFullReset;

	/**
	 * Purpose : Constructor with WebDriver argument
	 * @param driver
	 */
	public CommonFunctionLib(WebDriver driver) {
		this.driver = driver;
		locationServiceEnabled = Boolean.parseBoolean(properties
				.getProperty("locationServiceEnabled").trim().toLowerCase());
		doFullReset = true;
	}

	/**
	 * Purpose : Constructor with no argument
	 */
	public CommonFunctionLib() {

	}


	public boolean IsElementExist(By by, Optional<Long> timeoutInSeconds) {
		long timeout = timeoutInSeconds.isPresent() ? timeoutInSeconds.get()
				: 9999999;
		if (timeout == 9999999) {
			timeout = Long.parseLong(properties.getProperty("globalTimeOut"));
		}
		try {
			// AcceptAlert();
			/*
			 * if( FindElement(by, Optional.of(timeout))!= null){
			 * if(CommonVariables.CurrentTestCaseLog != null){
			 * CommonVariables.CurrentTestCaseLog
			 * .info("Info. Element '"+by+"' exists on '"
			 * +driver.getTitle()+"' page.");} else{
			 * CommonVariables.CurrentTestClassLog
			 * .info("Info. Element '"+by+"' exists on '"
			 * +driver.getTitle()+"' page.");}
			 * 
			 * return true;} else{ if(CommonVariables.CurrentTestCaseLog !=
			 * null){
			 * CommonVariables.CurrentTestCaseLog.info("Info. Element '"+by
			 * +"' deos not exist on '"+driver.getTitle()+"' page.");} else{
			 * CommonVariables.CurrentTestClassLog.info("Info. Element '"+by+
			 * "' deos not exist on '"+driver.getTitle()+"' page.");}
			 * 
			 * return false;}
			 */
		} catch (NullPointerException e) {
			return false;
		}
		return doFullReset;
	}



	// Function will return an hashmap with the driverinfo.
	// Usage: System.out.println(GetDriverInfo().get("DriverType") +
	// System.out.println(GetDriverInfo().get("DriverName"));
	public Map<String, String> GetDriverInfo() {
		Map<String, String> DriverInfo = new HashMap<String, String>();
		try {
			String DriverType = "";
			String DriverName = "";
			if (driver.getClass().toString().toLowerCase().contains("chrome")) {
				DriverType = "Desktop";
				DriverName = "Chrome";
			} else if (driver.getClass().toString().toLowerCase()
					.contains("remotewebdriver")) {
				Capabilities caps = ((RemoteWebDriver) driver)
						.getCapabilities();
				try {
					DriverName = caps.getCapability("device").toString();
				} catch (NullPointerException e) { // to handle Android Chrome
													// case
					String browsername = caps.getCapability("browserName")
							.toString();
					if (browsername.equals("chrome")) {
						DriverName = "androidchrome";
					}
				}

				if (DriverName.toLowerCase().contains("chrome")) {
					DriverType = "Mobile";
				} else if (DriverName.toLowerCase().contains("ipad")) {
					DriverType = "Tablet";
				} else {
					DriverType = "Mobile";
				}
			}
			DriverInfo.put("DriverType", DriverType);
			DriverInfo.put("DriverName", DriverName);
			return DriverInfo;
		} catch (Exception e) {
			String DriverType = "Mobile";
			String DriverName = "Android";
			DriverInfo.put("DriverType", DriverType);
			DriverInfo.put("DriverName", DriverName);
			return DriverInfo;
			// return null;
		}
	}

	
	public void ScrollToTop() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0,0);");
		} catch (Exception e) {

		}

	}

	public void ScrollToBottom() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0,document.documentElement.scrollHeight);");
		} catch (Exception e) {

		}
	}

	public void SwipeTop(WebElement element) {
		double browser_top_offset = 0.0;
		if (GetDriverInfo().get("DriverType").trim().equalsIgnoreCase("mobile")) {
			browser_top_offset = 0;
		} else if (GetDriverInfo().get("DriverType").trim()
				.equalsIgnoreCase("tablet")) {
			browser_top_offset = 80;
		}
		RemoteWebElement remoteelem = ((RemoteWebElement) element);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		Point eloc = remoteelem.getLocation();
		double yloc = eloc.getY();
		double xloc = eloc.getX() + remoteelem.getSize().width / 2;
		double swipe_xratio = xloc;
		double elemheight = remoteelem.getSize().getHeight();
		double yStartRatio = (yloc + elemheight / 2 + browser_top_offset) / 2;
		double yEndRatio = (eloc.getY() + browser_top_offset);
		if (swipe_xratio < 10.0) {
			swipe_xratio = 10.0;
		}
		if (yEndRatio < 50.0) {
			yEndRatio = 50.0;
		}
		HashMap<String, Double> swipeObject = new HashMap<String, Double>();
		swipeObject.put("startX", swipe_xratio);
		swipeObject.put("startY", yEndRatio);
		swipeObject.put("endX", swipe_xratio);
		swipeObject.put("endY", yStartRatio);
		swipeObject.put("duration", 1.0);
		js.executeScript("mobile: swipe", swipeObject);
	}

	public void SetiOSGeoLocation(double latitude, double longitude) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		HashMap<String, Double> locationObject = new HashMap<String, Double>();
		locationObject.put("latitude", latitude);
		locationObject.put("longitude", longitude);
		js.executeScript("mobile: setLocation", locationObject);
	}

	public boolean assertLocationAlertPresent(boolean alertShouldBeThereOrNot,
			Long waitingTime) {
		WebDriverWait wait = new WebDriverWait(driver, waitingTime);
		Alert alert = null;
		try {
			alert = wait.until(ExpectedConditions.alertIsPresent());
		} catch (TimeoutException e) {

		}
		Set<String> windowHandles = driver.getWindowHandles();
		if (windowHandles.size() > 1 && alertShouldBeThereOrNot == true) {
			alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (!alertText
					.equals("\"Safari\" Would Like to Use Your Current Location")) {
				alert = null;
			}
		}

		if (alert != null) {
			System.out.println("alert is present");
		} else {
			System.out.println("alert is not present");
		}

		if (alertShouldBeThereOrNot == true && alert != null) {
			return true;
		} else if (alertShouldBeThereOrNot == true && alert == null) {
			return false;
		}
		if (alertShouldBeThereOrNot == false && alert != null) {
			return false;
		} else if (alertShouldBeThereOrNot == true && alert == null) {
			return true;
		} else
			return false;
	}

	/**
	 * 
	 * @param testName
	 * @param xls
	 * @return
	 */
	public static Object[][] readData(String testName, Reader xls) {
		int testStartRowNum = 0;
		String sheetName = null;
		String temp[];
		temp = xls.getTestDataRow(testName,xls);
		sheetName = temp[0];
		testName= temp[1];
		testStartRowNum = Integer.parseInt(temp[2]);
	
		for (int rNum = 1; rNum <= xls.getRowCount(sheetName); rNum++) {
			if (xls.getCellData(sheetName, 0, rNum).equals(testName)) {
				testStartRowNum = rNum;
				break;
			}
		}
		
		int colStartRowNum = testStartRowNum + 1;
		int totalCols = 0;
		while (!xls.getCellData(sheetName, totalCols, colStartRowNum).equals("")) {
			totalCols++;
		}
	
		int dataStartRowNum = testStartRowNum + 2;
		int totalRows = 0;
		while (!xls.getCellData(sheetName, 0, dataStartRowNum + totalRows)
				.equals("")) {
			totalRows++;
		}

		Object[][] data = new Object[totalRows][1];
		Hashtable<String, String> table = null;
		int index = 0;
		for (int rNum = dataStartRowNum; rNum < (dataStartRowNum + totalRows); rNum++) {
			table = new Hashtable<String, String>();
			for (int cNum = 0; cNum < totalCols; cNum++) {
				table.put(xls.getCellData(sheetName, cNum, colStartRowNum),
						xls.getCellData(sheetName, cNum, rNum));
			}
			data[index][0] = table;
			index++;
		}
		return data;
	}

	/**
	 * Purpose : This function injects file path into Windows File Upload dialog
	 * @param filePath
	 * @throws AWTException
	 */
	public static void UploadFile(String filePath) throws AWTException {
		StringSelection stringSelection = new StringSelection(filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(stringSelection, null);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	/***
	 * 
	 * @param location
	 */
	public void CreateFolder(String location) {
		File Log = new File(location);
		if (Log.exists()) {
			AppLogs.debug("Directory already exists ...");
		} else {
			new File(location).mkdir();
			AppLogs.debug("Successfully created new directory");
		}
	}
}