import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class DatabaseTests {
    private final H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "tcp://localhost/~/test");

    private static final String TABLE_NAME_UPPER_CASE = "TEST_TABLE";
    private static final String TABLE_NAME_LOWER_CASE = "test_table";
    private static final String TABLE_NAME_MIXED_CASE = "Test_Table";
    private static final String NON_EXISTING_TABLE_NAME = "nonExistingTable";
    private final List<String> allColumnNames = asList("ID", "FIRST_NAME", "LAST_NAME");
    private final List<String> columnNames = asList("FIRST_NAME", "LAST_NAME");
    private final List<String> primaryKeys = asList("ID");

    private final List<String> valuesOne = asList("Jan", "Kowalski");
    private final List<String> valuesTwo = asList("Andrzej", "Nowak");
    private final List<String> valuesThree = asList("Roman", "Kaskader");
    private final List<String> valuesFour = asList("Jaroslaw", "Kulesza");

    private final List<String> returnOne = asList("1", "Jan", "Kowalski");
    private final List<String> returnTwo = asList("3", "Roman", "Kaskader");
    private final List<String> returnThree = asList("4", "Jaroslaw", "Kulesza");

    @Test
    public void createTableTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_UPPER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dropTableTest() {
        try {
            assertTrue(accessor.dropTable(TABLE_NAME_UPPER_CASE));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    @Test
    public void createTableAndInsertValuesTestTwo() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_UPPER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowsToTable(TABLE_NAME_UPPER_CASE, columnNames, asList(valuesOne, valuesTwo, valuesThree)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = SQLException.class)
    public void nonExistingTableTest() throws SQLException {
        accessor.addRowsToTable(NON_EXISTING_TABLE_NAME, columnNames, asList(valuesOne));
    }

    @Test
    public void selectFromTableTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames, null);
            assertTrue(resultsOne.get(0).containsAll(valuesOne));
            assertTrue(resultsOne.get(3).containsAll(valuesFour));

            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), null);
            assertTrue(resultsTwo.get(0).containsAll(returnOne));
            assertTrue(resultsTwo.get(3).containsAll(returnThree));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selectFromTableWithRegexTest() {
        createTableAndInsertValuesUpperCaseTest();
        try {
            HashMap<String, String> regexMap = new HashMap<>(2);
            regexMap.put("first_name", "^J.*$");
            regexMap.put("last_name", "^K.*$");
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), regexMap);
            assertTrue(resultsOne.get(0).containsAll(returnOne));
            assertTrue(resultsOne.get(1).containsAll(returnThree));

            regexMap.put("first_name", "^(J|R).*$");
            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"), regexMap);
            assertTrue(resultsTwo.get(0).containsAll(returnOne));
            assertTrue(resultsTwo.get(1).containsAll(returnTwo));
            assertTrue(resultsTwo.get(2).containsAll(returnThree));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
}
