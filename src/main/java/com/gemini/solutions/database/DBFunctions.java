package com.gemini.solutions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}
