import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class DatabaseTests {
    private final H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "tcp://localhost/~/test");

    private static final String TABLE_NAME_UPPER_CASE = "TEST_TABLE";
    private static final String TABLE_NAME_LOWER_CASE = "test_table";
    private static final String TABLE_NAME_MIXED_CASE = "Test_Table";
    private static final String NON_EXISTING_TABLE_NAME = "nonExistingTable";
    private final List<String> columnNames = asList("first_name", "last_name");
    private final List<String> primaryKeys = asList("id");

    private final List<String> valuesOne = asList("Jan", "Kowalski");
    private final List<String> valuesTwo = asList("Andrzej", "Nowak");
    private final List<String> valuesThree = asList("Roman", "Chrobry");
    private final List<String> valuesFour = asList("Lech", "Kulesza");

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
    public void createTableAndInsertValuesTest() {
        try {
            assertTrue(accessor.addTable(TABLE_NAME_UPPER_CASE, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowToTable(TABLE_NAME_UPPER_CASE, columnNames, valuesOne));
            assertTrue(accessor.addRowToTable(TABLE_NAME_UPPER_CASE, columnNames, valuesTwo));
            assertTrue(accessor.addRowToTable(TABLE_NAME_UPPER_CASE, columnNames, valuesThree));
            assertTrue(accessor.addRowToTable(TABLE_NAME_UPPER_CASE, columnNames, valuesFour));
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
            assertTrue(accessor.addRowToTable(TABLE_NAME_LOWER_CASE, columnNames, valuesOne));
            assertTrue(accessor.addRowToTable(TABLE_NAME_LOWER_CASE, columnNames, valuesTwo));
            assertTrue(accessor.addRowToTable(TABLE_NAME_LOWER_CASE, columnNames, valuesThree));
            assertTrue(accessor.addRowToTable(TABLE_NAME_LOWER_CASE, columnNames, valuesFour));
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
            assertTrue(accessor.addRowToTable(TABLE_NAME_MIXED_CASE, columnNames, valuesOne));
            assertTrue(accessor.addRowToTable(TABLE_NAME_MIXED_CASE, columnNames, valuesTwo));
            assertTrue(accessor.addRowToTable(TABLE_NAME_MIXED_CASE, columnNames, valuesThree));
            assertTrue(accessor.addRowToTable(TABLE_NAME_MIXED_CASE, columnNames, valuesFour));
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
        accessor.addRowToTable(NON_EXISTING_TABLE_NAME, columnNames, valuesOne);
    }

    @Test
    public void selectFromTableTest() {
        createTableAndInsertValuesTest();
        try {
            List<List<String>> resultsOne = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, columnNames);
            assertTrue(resultsOne.get(0).containsAll(valuesOne));
            assertTrue(resultsOne.get(3).containsAll(valuesFour));

            List<List<String>> resultsTwo = accessor.selectValuesFromTable(TABLE_NAME_UPPER_CASE, asList("*"));
            assertTrue(resultsTwo.get(0).containsAll(asList("1", "Jan", "Kowalski")));
            assertTrue(resultsTwo.get(3).containsAll(asList("4", "Lech", "Kulesza")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
