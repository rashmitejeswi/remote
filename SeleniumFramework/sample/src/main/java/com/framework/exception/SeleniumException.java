package com.framework.exception;

import java.io.IOException;
import java.net.MalformedURLException;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.framework.report.DetailedLogs;


/**
 * MyExeption.java
 * Purpose: This class contains user defined exception handling methods  
 * @version 1.0 21/05/14
 */
public class SeleniumException extends Exception {
	private static final long serialVersionUID = 6040492068655004146L;
	String message;
	DetailedLogs AppLogs = new DetailedLogs();

	/**
	 * Purpose : Custom message for the exception will be set here.
	 * @param message custom exception message
	 */
	public SeleniumException(String message) {
		super(message);
		this.message = message;
		AppLogs.error(this.message);
	}

	/**
	 * Purpose : Custom message for the Throwable will be set here.
	 * @param message custom exception message
	 */
	public SeleniumException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Purpose : Custom message for the message and Throwable will be set here.
	 * @param message custom exception message
	 */	
	public SeleniumException(String message,Throwable cause) {
		super(message);
		this.message = message;
		AppLogs.error(this.message);
	}

	/**
	 * Purpose : overloaded constructor to accept custom message and exception object
	 * @param message
	 * @param exe
	 */
	public SeleniumException(String message , Exception exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	/**
	 * Purpose : overloaded constructor to accept custom message and exception object
	 * @param message
	 * @param exe
	 */
	public SeleniumException(String message , MalformedURLException exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	/**
	 * Purpose : overloaded constructor to accept custom message and exception object
	 * @param message
	 * @param exe
	 */
	public SeleniumException(String message , NullPointerException exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	
	/**
	 * Purpose : overloaded constructor to accept custom message and exception object
	 * @param message
	 * @param exe
	 */
	public SeleniumException(String message , IOException exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	public SeleniumException(String message , IllegalArgumentException exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	public SeleniumException(String message , NoAlertPresentException exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	public SeleniumException(String message , UnreachableBrowserException exeception) {
		super(message);
		this.message = message + exeception.getMessage();
		AppLogs.error(this.message);
	}
	
	
	/**
	 * Purpose : This is an overridden method which is used to fetch the custom exception message
	 */
	@Override
	public String getMessage() {
		return message;
	}
	
}