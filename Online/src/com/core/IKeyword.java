package com.core;
/**
 * IKeyword.java 
 * 
 * Purpose: This interface contains method which contains list of keyword used in framework
 * 
 * @author : Abhay Bharti
 * @version 1.0 21/05/14
 */
public interface IKeyword {
	public void ORGenerator(String tObject);	
	public boolean KeywordGenerator(String tAction,String tLabelName, String tObjectName,String tParent_Object, String tInputData,String tExpectedData);
}
