import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class DatabaseTests {
    private H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "tcp://localhost/~/test");

    private final String tableName = "TEST_TABLE";
    private final String nonExistingTableName = "nonExistingTable";
    private final List<String> columnNames = asList("first_name", "last_name");
    private final List<String> primaryKeys = asList("id");

    private final List<String> valuesOne = asList("Jan", "Kowalski");
    private final List<String> valuesTwo = asList("Andrzej", "Nowak");
    private final List<String> valuesThree = asList("Roman", "Chrobry");
    private final List<String> valuesFour = asList("Lech", "Kulesza");

    @Test
    public void createTableTest() {
        try {
            assertTrue(accessor.addTable(tableName, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dropTableTest() {
        try {
            assertTrue(accessor.dropTable(tableName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createTableAndInsertValuesTest() {
        try {
            assertTrue(accessor.addTable(tableName, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesOne));
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesTwo));
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesThree));
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesFour));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createTableAndInsertValuesTestTwo() {
        try {
            assertTrue(accessor.addTable(tableName, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertTrue(accessor.addRowsToTable(tableName, columnNames, asList(valuesOne, valuesTwo, valuesThree)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = SQLException.class)
    public void nonExistingTableTest() throws SQLException {
        accessor.addRowToTable(nonExistingTableName, columnNames, valuesOne);
    }

    @Test
    public void selectFromTableTest() {
        createTableAndInsertValuesTest();
        try {
            List<List<String>> resultsOne = accessor.selectValuesFromTable(tableName, columnNames);
            assertTrue(resultsOne.get(0).containsAll(valuesOne));
            assertTrue(resultsOne.get(3).containsAll(valuesFour));

            List<List<String>> resultsTwo = accessor.selectValuesFromTable(tableName, asList("*"));
            assertTrue(resultsTwo.get(0).containsAll(asList("1", "Jan", "Kowalski")));
            assertTrue(resultsTwo.get(3).containsAll(asList("4", "Lech", "Kulesza")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("KOOPA");
    }

}
