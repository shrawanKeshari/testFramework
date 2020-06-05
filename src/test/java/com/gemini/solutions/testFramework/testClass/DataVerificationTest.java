package com.gemini.solutions.testFramework.testClass;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.testng.annotations.Test;

import com.gemini.solutions.constants.DBQueries;
import com.gemini.solutions.core.ApiExecutor;
import com.gemini.solutions.database.DBFunctions;
import com.gemini.solutions.extentReporting.ExtentTestManager;
import com.gemini.solutions.model.V2AllResponse;
import com.gemini.solutions.util.DateTimeUtil;
import com.gemini.solutions.util.FileUtil;
import com.gemini.solutions.util.TestCaseDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DataVerificationTest {

	DBFunctions dbFunctions = new DBFunctions();

	public List<V2AllResponse> executeApi() {
		ApiExecutor apiExecutor = new ApiExecutor();
		
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
		ExtentTestManager.startTest(TestCaseDetails.TC001CASENAME.getValue(), TestCaseDetails.TC001CASEDESCRIPTION.getValue());

		List<V2AllResponse> responseList = executeApi();

		HashSet<String> countrySet = new HashSet<String>();

		List<HashMap<String, Object>> dataList = dbFunctions.getDataFromDB(DBQueries.getCountry);
		
		for (HashMap<String, Object> hashMap : dataList) {
			countrySet.add(String.valueOf(hashMap.get("Country")));
		}

		for (V2AllResponse v2AllResponse : responseList) {
			String currCountry = v2AllResponse.getName();
			if (!countrySet.contains(currCountry)) {
				ExtentTestManager.getTest().log(LogStatus.INFO, "Missing Countries Entry In Database", currCountry);
			}
		}

	}

	@Test(description = "All Countries of which data (Capital and Currency )between table and API having mismatch"
			+ "I have updated some of existing country data and can be found in data.sql file Query No 5")
	public void TC002_AllCountriesWhosDataMisMatchBetweenDBAndApi() throws ClassNotFoundException, SQLException {
		ExtentTestManager.startTest(TestCaseDetails.TC002CASENAME.getValue(), TestCaseDetails.TC002CASEDESCRIPTION.getValue());
		
		FileUtil fileUtil = new FileUtil();
		
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

		List<HashMap<String, Object>> dataList = dbFunctions.getDataFromDB(DBQueries.getCountryCapitalCurrencyData);

		StringBuilder fileOutput = new StringBuilder();
		int mismatchCount = 0;

		for (HashMap<String, Object> hashMap : dataList) {
			String currCountry = String.valueOf(hashMap.get("Country"));
			String currCapital = String.valueOf(hashMap.get("Capital"));
			String currCode = String.valueOf(hashMap.get("Currency_Code"));

			List<String> db_capital_currencyList = new ArrayList<String>();
			db_capital_currencyList.add(currCapital);
			db_capital_currencyList.add(currCode);

			if (!countryMap.get(currCountry).equals(db_capital_currencyList)) {
				fileOutput.append("Country Name: ").append(currCountry).append("\n");
				fileOutput.append("Data: Capital").append("\n");
				fileOutput.append("API Value: ").append(countryMap.get(currCountry).get(0)).append("\n");
				fileOutput.append("DB Value: ").append(db_capital_currencyList.get(0)).append("\n");
				fileOutput.append("Data: Currency Code").append("\n");
				fileOutput.append("API Value: ").append(countryMap.get(currCountry).get(1)).append("\n");
				fileOutput.append("DB Value: ").append(db_capital_currencyList.get(1)).append("\n\n");
				mismatchCount++;
			}
		}

		String formattedDate = DateTimeUtil.getFormattedDateTime("dd-MMM-yyyy_HH-mm-ss.SSS");

		assertNotNull(fileUtil.writeByte(fileOutput.toString().getBytes(),
				".//test-output/mismatchData//mismatchData" + formattedDate + ".txt"));

		if (mismatchCount > 0) {
			ExtentTestManager.getTest().log(LogStatus.INFO, "Data Matched",
					mismatchCount + " Countries Found with mismatched data: " + "<a href='file://" + System.getProperty("user.dir")
							+ "/test-output/mismatchData/mismatchData" + formattedDate + ".txt'>Click Here to open list</a>");
		} else {
			ExtentTestManager.getTest().log(LogStatus.INFO, "Data Matched", "No Country found with mismatched data");
		}
	}

	@Test(description = "Execute the API and check whether country which has maximum number of countries bordering with are same or not between API and DB")
	public void TC003_checkWhetherCountryReturnedSameForMaxNumberOfBoundaryFromAPIAndDB()
			throws ClassNotFoundException, SQLException {
		ExtentTestManager.startTest(TestCaseDetails.TC003CASENAME.getValue(), TestCaseDetails.TC003CASEDESCRIPTION.getValue());

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

		ExtentTestManager.getTest().log(LogStatus.INFO, "Country With Max Border From API",
				countryNameforMaxBorderFromApi + " with " + maxBorderFromApi + " borders");

		List<HashMap<String, Object>> dataList = dbFunctions.getDataFromDB(DBQueries.getCountryWithBorderCount);

		String currCountry = null;
		int borderCount = 0;

		for (HashMap<String, Object> hashMap : dataList) {
			currCountry = String.valueOf(hashMap.get("Country"));
			borderCount = Integer.parseInt(String.valueOf(hashMap.get("count")));
		}

		ExtentTestManager.getTest().log(LogStatus.INFO, "Country With Max Border From DB",
				currCountry + " with " + borderCount + " borders");

		assertEquals(countryNameforMaxBorderFromApi, currCountry);
		assertEquals(maxBorderFromApi, borderCount);
	}
}
