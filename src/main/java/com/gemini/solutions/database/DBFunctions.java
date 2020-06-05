package com.gemini.solutions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gemini.solutions.extentReporting.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;

public class DBFunctions {

	Connection connection;
	Statement statement;
	ResultSet resultSet;

	public void connectDatabase() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/geminiSolutions", "root", "S*onu@1908");
		System.out.println("connected");
		ExtentTestManager.getTest().log(LogStatus.INFO, "Connection Made to DB",
				"Ip: localhost, port: 3306, databaseName: geminiSolutions");
		statement = connection.createStatement();

	}

	public ResultSet selectQuery(String query) {
		try {
			resultSet = statement.executeQuery(query);
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Executed Successfully", query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Execution Failed", query);
		}

		return resultSet;
	}

	public boolean insertQuery(String query) {
		try {
			statement.executeUpdate(query);
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Executed Successfully", query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Execution Failed", query);
			return false;
		}

		return true;
	}

	public boolean updateQuery(String query) {
		try {
			statement.executeUpdate(query);
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Executed Successfully", query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Execution Failed", query);
			return false;
		}
		return true;
	}

	public boolean deleteQuery(String query) {
		try {
			statement.executeUpdate(query);
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Executed Successfully", query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExtentTestManager.getTest().log(LogStatus.INFO, "Query Execution Failed", query);
			return false;
		}

		return true;
	}

	public boolean disconnectDataBase() {
		try {
			connection.close();
			ExtentTestManager.getTest().log(LogStatus.INFO, "Connection Disconnected to DB",
					"Ip: localhost, port: 3306, databaseName: geminiSolutions");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		System.out.println("Disconnected from database");
		return true;
	}
	
	public int getCountFromDB(String query) throws ClassNotFoundException, SQLException {
		int count = 0;
		connectDatabase();

		ResultSet countSet = selectQuery(query);

		try {
			countSet.next();
			count = countSet.getInt("count");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		disconnectDataBase();

		return count;
	}
	
	public List<HashMap<String, Object>> getDataFromDB(String query) throws ClassNotFoundException, SQLException {
		connectDatabase();

		List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();

		try {
			ResultSet dataSet = selectQuery(query);
			ResultSetMetaData resultSetMetaData = dataSet.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			while (dataSet.next()) {
				HashMap<String, Object> resultMap = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = resultSetMetaData.getColumnName(i);
					resultMap.put(columnName, dataSet.getObject(columnName));
				}

				resultList.add(resultMap);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		disconnectDataBase();
		
		return resultList;
	}
}
