package com.framework.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

/**
 * CustomLogging.java contains method to write in logs for test pass, fail, skipp, success
 */

public class CustomLogging extends TestListenerAdapter implements ITestListener, IInvokedMethodListener {
  
	DetailedLogs AppLogs = new DetailedLogs();
	protected ExtentReports extent = ExtentManager.getInstance();
	protected static ExtentTest test;

/**
 * Purpose : Called when the test-method execution starts
 * @param result 
 */
  @Override
  public void onTestStart(ITestResult result) {
        AppLogs.info("Test Case Execution started: "+ result.getName()+ " time is: "+getCurrentTime());
        test = extent.startTest(result.getName());
   }

  /**
   * Purpose : Called when the test-method execution is a success
   * @param result
   */
  @Override
  public void onTestSuccess(ITestResult result) {
	   AppLogs.info("Test Case Success: "+ result.getName()+ "  time is: "+getCurrentTime());
  }

  /**
   * Purpose : Called when the test-method execution fails
   * @param result
   */
  @Override
  public void onTestFailure(ITestResult result) {
	  AppLogs.error("Test Case failed: "+ result.getName()+ "  and time is: "+getCurrentTime());
  }

  /**
   * Purpose : Called when the test-method is skipped
   * @param result
   */
  @Override
  public void onTestSkipped(ITestResult result) {
	  AppLogs.info("Test method skipped: "+ result.getName()+ " and time is: "+getCurrentTime());    
  }

  /**
   * Purpose :  Called when the test-method fails within success percentage 
   */
  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

  /**
   * Purpose : Called when the test in xml suite starts
   * @param context
   */
  @Override
  public void onStart(ITestContext context) {
	  AppLogs.info("Test in a suite started: "+ context.getName()+ " and time is: "+getCurrentTime());
  }

  /**
   * Purpose : Called when the test in xml suite finishes
   * @param context
   */
  @Override
  public void onFinish(ITestContext context) {
	  AppLogs.info("Test in a suite finished: "+ context.getName()+ " and time is: "+getCurrentTime());
  }
  
  /**
   * Purpose : Returns the current time when the method is called
   */
  public String getCurrentTime(){
	  DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
	  Date dt = new Date();
    return dateFormat.format(dt);    
  }

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {}

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {}
}