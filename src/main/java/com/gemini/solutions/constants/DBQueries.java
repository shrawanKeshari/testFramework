package com.gemini.solutions.constants;

public class DBQueries {
	
	public static final String countryCountQuery = "select count(*) as count from countries;";
	public static final String borderCountQuery = "select count(*) as count from borders;";
	public static final String getCountry = "select Country from countries;";
	public static final String getCountryCapitalCurrencyData = "select Country, Capital, Currency_Code from countries;";
	public static final String getCountryWithBorderCount = "select c.Country, COUNT(b.C_ID) AS count from borders b inner join countries c on c.CID=b.C_ID group by b.C_ID order by count desc limit 1;";
}
