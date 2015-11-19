import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DatabaseTests {
    private H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "tcp://localhost/~/test");

    private final String tableName = "people";
    private final List<String> columnNames = Arrays.asList("id", "first_name", "last_name");
    private final List<String> primaryKeys = Arrays.asList("id");

    @Test
    public void createTableTest() {
        assertTrue(accessor.addTable(tableName, columnNames, primaryKeys));
    }

    @Test
    public void dropTableTest() {
        assertTrue(accessor.dropTable(tableName));
    }

    @Test
    public void createTableAndInsertValuesTest() {
        assertTrue(accessor.addTable(tableName, columnNames, primaryKeys));

        List<String> rowOne = Arrays.asList("1", "Jan", "Kowalski");
        List<String> rowTwo = Arrays.asList("2", "Andrzej", "Nowak");
        List<String> rowThree = Arrays.asList("3", "Roman", "Chrobry");

        assertTrue(accessor.addRowToTable(tableName, columnNames, rowOne));
        assertTrue(accessor.addRowToTable(tableName, columnNames, rowTwo));
        assertTrue(accessor.addRowToTable(tableName, columnNames, rowThree));
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
