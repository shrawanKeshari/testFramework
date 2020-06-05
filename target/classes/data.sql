Query No 1: create database geminiSolutions;

Query No 2: create table countries (CID varchar(30) PRIMARY KEY, Country varchar(255) NOT NULL, Capital varchar(255) NOT NULL, Currency_Code varchar(30) NOT NULL);

Query No 3: create table borders (C_ID varchar(30) NOT NULL, B_ID varchar(30) NOT NULL, PRIMARY KEY (C_ID,B_ID));

Query No 4: delete from countries where Country in ("Aruba","Burundi","Canada","Djibouti","Estonia","Guinea","Hungary","Kyrgyzstan","Libya","Zambia");

Query No 5: for updating some existing date
Angola
update countries set Capital="test1",Currency_Code="T1" where Country="Angola";
 
Benin
update countries set Capital="test2",Currency_Code="T2" where Country="Benin";

Chile
update countries set Capital="test3",Currency_Code="T3" where Country="Chile";

Dominica
update countries set Capital="test4",Currency_Code="T4" where Country="Dominica";

Ecuador
update countries set Capital="test5",Currency_Code="T5" where Country="Ecuador";

Finland
update countries set Capital="test6",Currency_Code="T6" where Country="Finland";

French Polynesia
update countries set Capital="test17",Currency_Code="T17" where Country="French Polynesia";

Gabon
update countries set Capital="test7",Currency_Code="T7" where Country="Gabon";

Hong Kong
update countries set Capital="test8",Currency_Code="T8" where Country="Hong Kong";

Indonesia
update countries set Capital="test9",Currency_Code="T9" where Country="Indonesia";

Jamaica
update countries set Capital="test10",Currency_Code="T10" where Country="Jamaica";

Kazakhstan
update countries set Capital="test11",Currency_Code="T11" where Country="Kazakhstan";

Lebanon
update countries set Capital="test12",Currency_Code="T12" where Country="Lebanon";

Micronesia (Federated States of)
update countries set Capital="test15",Currency_Code="T15" where Country="Micronesia (Federated States of)";

Moldova (Republic of)
update countries set Capital="test16",Currency_Code="T16" where Country="Moldova (Republic of)";

Yemen
update countries set Capital="test13",Currency_Code="T13" where Country="Yemen";

Vanuatu
update countries set Capital="test14",Currency_Code="T14" where Country="Vanuatu";

Virgin Islands (British)
update countries set Capital="test18",Currency_Code="T18" where Country="Virgin Islands (British)";