package com.gemini.solutions.testFramework.utils;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.gemini.solutions.constants.DBQueries;
import com.gemini.solutions.core.ApiExecutor;
import com.gemini.solutions.database.DBFunctions;
import com.gemini.solutions.model.V2AllResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

//Class use for populating data in countries and borders table with data get in API call https://restcountries.eu/rest/v2/all
public class DataPopulation {

	ApiExecutor apiExecutor = new ApiExecutor();
	DBFunctions dbFunctions = new DBFunctions();
	
	@Test
	public void populateData() throws ClassNotFoundException, SQLException {
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("Content-Type", ContentType.JSON);

		HashMap<String, Object> queryParamsMap = new HashMap<String, Object>();

		String baseUri = "https://restcountries.eu/rest";
		String basePath = "/v2/all";

		Response response = apiExecutor.get(headerMap, baseUri, basePath, queryParamsMap);
		
		List<V2AllResponse> responseList = new Gson().fromJson(response.prettyPrint(),
				new TypeToken<List<V2AllResponse>>() {
				}.getType());
		
		System.out.println(responseList.size());
		
		dbFunctions.connectDatabase();
		int totalBorderCount = 0;
		
		for (V2AllResponse v2AllResponse : responseList) {
			String cId = v2AllResponse.getAlpha3Code();
			String country = v2AllResponse.getName();
			String capital = v2AllResponse.getCapital();
			String currencyCode = v2AllResponse.getCurrencies().get(0).getCode();
			
			String query = "insert into countries values (\""+cId+"\", \""+country+"\", \""+capital+"\", \""+currencyCode+"\");";
			System.out.println(query);
			System.out.println("Country insert query : " + dbFunctions.insertQuery(query));
			
			List<String> borderList = v2AllResponse.getBorders();
			totalBorderCount += borderList.size();
			
			for (String border : borderList) {
				query = "insert into borders values (\"" + cId + "\", \"" + border + "\");";
				System.out.println(query);
				System.out.println("Border insert query : " + dbFunctions.insertQuery(query));
			}
		}
		
		dbFunctions.disconnectDataBase();
		
		int countryCount = dbFunctions.getCountFromDB(DBQueries.countryCountQuery);
		
		int borderCount = dbFunctions.getCountFromDB(DBQueries.borderCountQuery);
		
		assertEquals(countryCount, responseList.size());
		assertEquals(borderCount, totalBorderCount);	
	}
}
