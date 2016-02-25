package com.util;

/**
 * IUtility.java 
 * Purpose: This interface contains general purpose method
 * 
 * @author : Abhay Bharti
 * @version 1.0 21/05/14
 */

public interface IUtility {

	public void setup();
		
	public void tearDown();
	
	public boolean isObjectEnabled(); // verify that Object is enabled
	
	public boolean isObjectDisplayed(); //verify that object is displayed
	
	public String GetTime(); // returns Machine current Time
	
	public String GetDate(); //returns Machine current Date
	
	public boolean StringCompare(String input, String output); //returns Machine current Date
	
	public void launchBrowser(); // launches browser & opens URL
	
}
