package com.denissys.dbunit.generator;

import static java.lang.Class.forName;
import static java.lang.String.format;
import static java.sql.DriverManager.getConnection;
import static org.dbunit.dataset.xml.FlatXmlDataSet.write;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Statement;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This program create the XML from SELECT instructions, to load in DUnit Tests
 * 
 * @author Denis Santos
 */
public abstract class DataSetGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetGenerator.class);

	static {
		Connection jdbcConnection;
		try {
			File file = new File("test.db");
			if(file.exists()) file.delete();
			forName("org.sqlite.JDBC");
			jdbcConnection = getConnection("jdbc:sqlite:test.db");
			connection = new DatabaseConnection(jdbcConnection);
		} catch (Exception e) {
			LOGGER.error(format("Create connection fail, message: %s", e.getMessage()));
		}
	}

	private static IDatabaseConnection connection;
	
	/**
	 * Initialize
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		createTable();
		createXml();
	}

	/**
	 * Creates and populates table
	 */
	public static void createTable() {
		Statement stmt = null;
		try {
			stmt = connection.getConnection().createStatement();
			stmt.executeUpdate("create table Login (id int primary key not null, email text not null, password text not null)");
			stmt.executeUpdate("insert into Login (id, email, password) values (1, 'test@test.com', 'Zaz143')");
			stmt.close();
		} catch (Exception e) {
			LOGGER.error(format("Create table fail, message: %s", e.getMessage()));
		}
	}

	/**
	 * creates an XML from a SELECT statement
	 */
	public static void createXml() {
		try {
			QueryDataSet queryDataSet = new QueryDataSet(connection);
			queryDataSet.addTable("Login", "select * from Login");
			write(queryDataSet, new FileOutputStream("target/Login.xml"));
		} catch(Exception e) {
			LOGGER.error(format("Create Xml Fail, message: %s", e.getMessage()));
		}
	}
}