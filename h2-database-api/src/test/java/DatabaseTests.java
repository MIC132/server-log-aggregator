import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DatabaseTests {
    private H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "tcp://localhost/~/test");

    private final String tableName = "people";
    private final String nonExistingTableName = "nonExistingTable";
    private final List<String> columnNames = Arrays.asList("first_name", "last_name");
    private final List<String> primaryKeys = Arrays.asList("id");

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

        List<String> valuesOne = Arrays.asList("Jan", "Kowalski");
        List<String> valuesTwo = Arrays.asList("Andrzej", "Nowak");
        List<String> valuesThree = Arrays.asList("Roman", "Chrobry");
        List<String> valuesFour = Arrays.asList("Lech", "Kulesza");

        try {
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesOne));
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesTwo));
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesThree));
            assertTrue(accessor.addRowToTable(tableName, columnNames, valuesFour));
        } catch (SQLException e) {
            throw new RuntimeException("Cos sie zejbalo");
        }
    }

    @Test(expected = SQLException.class)
    public void nonExistingTableTest() throws SQLException {
        List<String> valuesOne = Arrays.asList("Jan", "Kowalski");
        accessor.addRowToTable(nonExistingTableName, columnNames, valuesOne);
    }

    @Test
    public void selectFromTableTest() {
        createTableAndInsertValuesTest();

        // TODO ResultSet is inaccesible after connection is closed. All info must be copied to a new structure or connection must be kept longer (bad idea..)
        /*ResultSet resultSet = accessor.selectValuesFromTable(tableName, columnNames);

        try {
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id") + " " + resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

    }

}
