package com.gemini.solutions.testFramework.testClass;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.gemini.solutions.core.ApiExecutor;
import com.gemini.solutions.database.DBFunctions;
import com.gemini.solutions.extentReporting.ExtentTestManager;
import com.gemini.solutions.model.V2AllResponse;
import com.gemini.solutions.util.DateTimeUtil;
import com.gemini.solutions.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.converters.ReportParser;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DataVerificationTest {

	ApiExecutor apiExecutor = new ApiExecutor();
	DBFunctions dbFunctions = new DBFunctions();
	FileUtil fileUtil = new FileUtil();

	public List<V2AllResponse> executeApi() {
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("Content-Type", ContentType.JSON);

		HashMap<String, Object> queryParamsMap = new HashMap<String, Object>();

		String baseUri = "https://restcountries.eu/rest";
		String basePath = "/v2/all";

		Response response = apiExecutor.get(headerMap, baseUri, basePath, queryParamsMap);

		List<V2AllResponse> responseList = new Gson().fromJson(response.prettyPrint(),
				new TypeToken<List<V2AllResponse>>() {
				}.getType());

		ExtentTestManager.getTest().log(LogStatus.PASS, "API Executed", "https://restcountries.eu/rest/v2/all");

		return responseList;
	}

	@Test(description = "List of all the countries which are missing in table: countries but present in API response"
			+ "I have deleted some countries from my database and executed Query No 4 from data.sql file")
	public void TC001_ListAllCountriesMissingInTablePresentInApiResponse() throws ClassNotFoundException, SQLException {
		ExtentTestManager.startTest("ListAllCountriesMissingInTablePresentInApiResponse",
				"List of all the countries which are missing in table: countries but present in API response"
						+ "I have deleted some countries from my database and executed Query No 4 from data.sql file");

		List<V2AllResponse> responseList = executeApi();

		HashSet<String> countrySet = new HashSet<String>();

		dbFunctions.connectDatabase();

		String countQuery = "select Country from countries;";
		ResultSet resultSet = dbFunctions.selectQuery(countQuery);

		while (resultSet.next()) {
			countrySet.add(resultSet.getString("Country"));
		}

		dbFunctions.disconnectDataBase();

		for (V2AllResponse v2AllResponse : responseList) {
			String currCountry = v2AllResponse.getName();
			if (!countrySet.contains(currCountry)) {
				System.out.println(currCountry);
				ExtentTestManager.getTest().log(LogStatus.INFO, "Missing Countried Entry In Database", currCountry);
			}
		}

	}

	@Test(description = "All Countries of which data (Capital and Currency )between table and API having mismatch"
			+ "I have updated some of existing country data and can be found in data.sql file Query No 4")
	public void TC002_AllCountriesWhosDataMisMatchBetweenDBAndApi() throws ClassNotFoundException, SQLException {
		ExtentTestManager.startTest("AllCountriesWhosDataMisMatchBetweenDBAndApi",
				"All Countries of which data (Capital and Currency )between table and API having mismatch"
						+ "I have updated some of existing country data and can be found in data.sql file Query No 4");
		List<V2AllResponse> responseList = executeApi();

		HashMap<String, List<String>> countryMap = new HashMap<String, List<String>>();

		for (V2AllResponse v2AllResponse : responseList) {
			String country = v2AllResponse.getName();
			String capital = v2AllResponse.getCapital();
			String currencyCode = v2AllResponse.getCurrencies().get(0).getCode();
			List<String> capital_currencyList = new ArrayList<String>();
			capital_currencyList.add(capital);
			capital_currencyList.add(currencyCode);

			countryMap.put(country, capital_currencyList);
		}

		System.out.println(countryMap);

		dbFunctions.connectDatabase();

		String countQuery = "select Country, Capital, Currency_Code from countries;";
		ResultSet resultSet = dbFunctions.selectQuery(countQuery);

		StringBuilder fileOutput = new StringBuilder();

		while (resultSet.next()) {
			String currCountry = resultSet.getString("Country");
			String currCapital = resultSet.getString("Capital");
			String currCode = resultSet.getString("Currency_Code");

			List<String> db_capital_currencyList = new ArrayList<String>();
			db_capital_currencyList.add(currCapital);
			db_capital_currencyList.add(currCode);

			if (!countryMap.get(currCountry).equals(db_capital_currencyList)) {
				ExtentTestManager.getTest().log(LogStatus.INFO, "Data Matched", "Country Found with mismatched data");

				fileOutput.append("Country Name: ").append(currCountry).append("\n");
				fileOutput.append("Data: Capital").append("\n");
				fileOutput.append("API Value: ").append(countryMap.get(currCountry).get(0)).append("\n");
				fileOutput.append("DB Value: ").append(db_capital_currencyList.get(0)).append("\n");
				fileOutput.append("Data: Currency Code").append("\n");
				fileOutput.append("API Value: ").append(countryMap.get(currCountry).get(1)).append("\n");
				fileOutput.append("DB Value: ").append(db_capital_currencyList.get(1)).append("\n\n");

//				ExtentTestManager.getTest().log(LogStatus.INFO, "Data Mismatch between API and DB", fileOutput.toString());
				System.out.println(fileOutput.toString());
			}
		}

		dbFunctions.disconnectDataBase();
		
		ExtentTestManager.getTest().log(LogStatus.INFO, "Data Mismatch between API and DB", fileOutput.toString());

		assertNotNull(fileUtil.writeByte(fileOutput.toString().getBytes(), ".//target//testOutput//mismatchData"
				+ DateTimeUtil.getFormattedDateTime("dd-MMM-yyyy_HH-mm-ss.SSS") + ".txt"));
	}

	@Test(description = "Execute the API and check whether country which has maximum number of countries bordering with are same or not between API and DB")
	public void TC003_checkWhetherCountryReturnedSameForMaxNumberOfBoundaryFromAPIAndDB()
			throws ClassNotFoundException, SQLException {
		ExtentTestManager.startTest("checkWhetherCountryReturnedSameForMaxNumberOfBoundaryFromAPIAndDB",
				"Execute the API and check whether country which has maximum number of countries bordering with are same or not between API and DB");

		List<V2AllResponse> responseList = executeApi();

		int maxBorderFromApi = -1;
		String countryNameforMaxBorderFromApi = "";

		for (V2AllResponse v2AllResponse : responseList) {
			String country = v2AllResponse.getName();
			List<String> borderList = v2AllResponse.getBorders();

			if (maxBorderFromApi < borderList.size()) {
				maxBorderFromApi = borderList.size();
				countryNameforMaxBorderFromApi = country;
			}
		}

		System.out.println(countryNameforMaxBorderFromApi + " , " + maxBorderFromApi);

		ExtentTestManager.getTest().log(LogStatus.INFO, "Country With Max Border From API",
				countryNameforMaxBorderFromApi + " with " + maxBorderFromApi + " borders");

		dbFunctions.connectDatabase();

		String countQuery = "select c.Country, COUNT(b.C_ID) AS count from borders b inner join countries c on c.CID=b.C_ID group by b.C_ID order by count desc limit 1;";
		ResultSet resultSet = dbFunctions.selectQuery(countQuery);
		String currCountry = null;
		int borderCount = 0;

		while (resultSet.next()) {
			currCountry = resultSet.getString("Country");
			borderCount = resultSet.getInt("count");
		}

		dbFunctions.disconnectDataBase();

		System.out.println(currCountry + " , " + borderCount);
		ExtentTestManager.getTest().log(LogStatus.INFO, "Country With Max Border From API",
				currCountry + " with " + borderCount + " borders");

		assertEquals(countryNameforMaxBorderFromApi, currCountry);
		assertEquals(maxBorderFromApi, borderCount);
	}
}
