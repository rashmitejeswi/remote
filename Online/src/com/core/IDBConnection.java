package com.core;

import java.io.IOException;
import java.sql.SQLException;
/**
 * IDBConnection.java 
 * Purpose: This interface contains method for DB connection
 * 
 * @author : Abhay Bharti
 * @version 1.0 21/05/14
 */
public interface IDBConnection {
	public void CreateConnection() throws ClassNotFoundException, SQLException;
	public String GetDBValue(String query, String ReturnColumnName) throws IOException, ClassNotFoundException, SQLException;
}
