import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
* This class is a colection of database functionality tests.
* It uses dedicated H2 database called "test" - created by default in every H2 engine
* All of the tests uses JUnit framework, but it is suggested to check effects of every test in database */
public class DatabaseTests {
    private final H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "tcp://localhost/~/test");

    private static final String TABLE_NAME_UPPER_CASE = "TEST_TABLE";
    private static final String TABLE_NAME_LOWER_CASE = "test_table";
    private static final String TABLE_NAME_MIXED_CASE = "Test_Table";
    private static final String NON_EXISTING_TABLE_NAME = "nonExistingTable";
    private final List<String> allColumnNames = asList("ID", "FIRST_NAME", "LAST_NAME");
    private final List<String> columnNames = asList("FIRST_NAME", "LAST_NAME");

    private final List<String> valuesOne = asList("Jan", "Kowalski");
    private final List<String> valuesTwo = asList("Andrzej", "Nowak");
    private final List<String> valuesThree = asList("Roman", "Kaskader");
    private final List<String> valuesFour = asList("Jaroslaw", "Kulesza");

    private final List<String> returnOne = asList("1", "Jan", "Kowalski");
    private final List<String> returnTwo = asList("3", "Roman", "Kaskader");
    private final List<String> returnThree = asList("4", "Jaroslaw", "Kulesza");

    private final List<String> insertInjection = asList("Jaroslaw", "Kulesza); drop table TEST_TABLE; --");

    /* Test for addTable method.
     Creates a sample table, without putting any rows into it*/
    @Test
    public void createTableTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_UPPER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* This test creates a sample table TEST_TABLE and initializes it with some data
     Since H2 is case sensitive to table names, we added three different test cases, for every case type (upper, lower, mixed) */
    @Test
    public void createTableAndInsertValuesUpperCaseTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_UPPER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowsToTable(TABLE_NAME_UPPER_CASE, columnNames, asList(valuesOne, valuesTwo, valuesThree, valuesFour)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* This test creates a sample table test_table and initializes it with some data
     Since H2 is case sensitive to table names, we added three different test cases, for every case type (upper, lower, mixed) */
    @Test
    public void createTableAndInsertValuesLowerCaseTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_LOWER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowsToTable(TABLE_NAME_LOWER_CASE, columnNames, asList(valuesOne, valuesTwo, valuesThree, valuesFour)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* This test creates a sample table Test_Table and initializes it with some data
     Since H2 is case sensitive to table names, we added three different test cases, for every case type (upper, lower, mixed) */
    @Test
    public void createTableAndInsertValuesMixedCaseTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_MIXED_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowsToTable(TABLE_NAME_MIXED_CASE, columnNames, asList(valuesOne, valuesTwo, valuesThree, valuesFour)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Test for inserting a data in non-existing table. Proper exception should be reported */
    @Test(expected = SQLException.class)
    public void nonExistingTableTest() throws SQLException {
        accessor.addRowsToTable(NON_EXISTING_TABLE_NAME, columnNames, asList(valuesOne));
    }

    /* Unit test for selectValuesFromTable method, without additional WHERE clauses */
    @Test
    public void selectFromTableTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames, null);
            assertTrue(resultsOne.size() == 4);
            assertTrue(resultsOne.get(0).containsAll(valuesOne));
            assertTrue(resultsOne.get(3).containsAll(valuesFour));

            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), null);
            assertTrue(resultsOne.size() == 4);
            assertTrue(resultsTwo.get(0).containsAll(returnOne));
            assertTrue(resultsTwo.get(3).containsAll(returnThree));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Unit test for selectValuesFromTable method, without additional WHERE clauses, with LIMIT and OFFSET parameters*/
    @Test
    public void selectFromTableWithLimitAndOffsetTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames, null, 2, null);
            assertTrue(resultsOne.size() == 2);
            assertTrue(resultsOne.get(0).containsAll(valuesOne));
            assertTrue(resultsOne.get(1).containsAll(valuesTwo));

            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames, null, 2, 2);
            assertTrue(resultsTwo.size() == 2);
            assertTrue(resultsTwo.get(0).containsAll(valuesThree));
            assertTrue(resultsTwo.get(1).containsAll(valuesFour));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Unit test for selectValuesFromTable method, with additional WHERE clauses */
    @Test
    public void selectFromTableWithRegexTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            HashMap<String, String> regexMap = new HashMap<>(2);
            regexMap.put("first_name", "^J.*$");
            regexMap.put("last_name", "^K.*$");
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), regexMap);
            assertEquals(resultsOne.size(), 2);
            assertTrue(resultsOne.get(0).containsAll(returnOne));
            assertTrue(resultsOne.get(1).containsAll(returnThree));

            regexMap.put("first_name", "^(J|R).*$");
            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), regexMap);
            assertEquals(resultsTwo.size(), 3);
            assertTrue(resultsTwo.get(0).containsAll(returnOne));
            assertTrue(resultsTwo.get(1).containsAll(returnTwo));
            assertTrue(resultsTwo.get(2).containsAll(returnThree));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Unit test for selectValuesFromTable method, with additional WHERE clause, LIMIT and OFFSET parameters */
    @Test
    public void selectFromTableWithRegexLimitAndOffsetTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            HashMap<String, String> regexMap = new HashMap<>(2);
            regexMap.put("first_name", "^J.*$");
            regexMap.put("last_name", "^K.*$");
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), regexMap, 1, 1);
            assertEquals(resultsOne.size(), 1);
            assertTrue(resultsOne.get(0).containsAll(returnThree));

            regexMap.put("first_name", "^(J|R).*$");
            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), regexMap, 2, 1);
            assertEquals(resultsTwo.size(), 2);
            //assertTrue(resultsTwo.get(0).containsAll(returnOne));
            assertTrue(resultsTwo.get(0).containsAll(returnTwo));
            assertTrue(resultsTwo.get(1).containsAll(returnThree));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Unit test for countValuesFromTable method, with additional WHERE clauses */
    @Test
    public void countFromTableWithRegexTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            HashMap<String, String> regexMap = new HashMap<>(2);
            regexMap.put("first_name", "^J.*$");
            regexMap.put("last_name", "^K.*$");
            int resultsOne = accessor.countValuesFromTable(TABLE_NAME_UPPER_CASE, regexMap);
            assertTrue(resultsOne == 2);

            regexMap.put("first_name", "^(J|R).*$");
            int resultsTwo = accessor.countValuesFromTable(TABLE_NAME_UPPER_CASE, regexMap);
            assertTrue(resultsTwo == 3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Unit test for getColumnNames method, with additional WHERE clauses */
    @Test
    public void getColumnNames() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            List<String> result = accessor.getColumnNames(TABLE_NAME_UPPER_CASE);
            assertTrue(result.containsAll(allColumnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Case of SQL injection, through INSERT statement.
     Injected query shouldn't be executed but added to table */
    @Test
    public void insertSqlInjectionTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_UPPER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            accessor.addRowsToTable(TABLE_NAME_UPPER_CASE, columnNames, asList(insertInjection));
            List<List<String>> result = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames, null);
            result.get(0).containsAll(asList("1", "Jaroslaw", "Kulesza); drop table TEST_TABLE; --"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Case of SQL injection, through SELECT statement.
     Injected query shouldn't be executed but added as a part of regex in WHERE clause.
     Therefore executing select statement won't return any results */
    @Test
    public void selectSqlInjectionTest() {
        createTableAndInsertValuesUpperCaseTest();
        Map<String, String> regexMap = new HashMap<>(2);
        regexMap.put("first_name", "^J.*$; drop table TEST_TABLE; --");
        try {
            List<List<String>> result = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames, regexMap);
            assertEquals(0, result.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
