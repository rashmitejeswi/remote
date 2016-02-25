package com.core;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * ILog.java 
 * 
 * Purpose: This interface contains method for logging execution detail
 * 
 * @author : Abhay Bharti
 * @version 1.0 21/05/14
 */
public interface ILog {
	public static org.apache.log4j.Logger log = Logger.getLogger(ILog.class);	
	//public void Debug(String str);
	public void info(String str);
	public void warn(String str);
	public void error(String str);
	public void error(Exception str);
	public void callFailError(String tAction,String tParent,String tObject,String tInput,String strDesc, String strError);
	public void callPassDebug(String tAction,String tParent,String tObject,String tInput,String strDesc);
}
