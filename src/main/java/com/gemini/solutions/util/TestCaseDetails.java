package com.gemini.solutions.util;

public enum TestCaseDetails {

	TC001CASENAME("ListAllCountriesMissingInTablePresentInApiResponse"),
	TC001CASEDESCRIPTION("List of all the countries which are missing in table: countries but present in API response "
			+ "I have deleted some countries from my database and executed Query No 4 from data.sql file"),
	TC002CASENAME("AllCountriesWhosDataMisMatchBetweenDBAndApi"),
	TC002CASEDESCRIPTION("All Countries of which data (Capital and Currency )between table and API having mismatch"
			+ "I have updated some of existing country data and can be found in data.sql file Query No 5"),
	TC003CASENAME("CheckWhetherCountryReturnedSameForMaxNumberOfBoundaryFromAPIAndDB"), TC003CASEDESCRIPTION(
			"Execute the API and check whether country which has maximum number of countries bordering with are same or not between API and DB");

	private final String value;

	private TestCaseDetails(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}
