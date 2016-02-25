package com.core;

/**
 * IExcelReader.java 
 * Purpose: This interface contains method for
 * reading/writing from Excel file
 * 
 * @author : Abhay Bharti
 * @version 1.0 21/05/14
 */
public interface IExcelReader {
	public void GetSuite();

	public void GetTestCase();

	public void ORGenerator(String tObject);

}
