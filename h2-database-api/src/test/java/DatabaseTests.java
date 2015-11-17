import org.junit.Test;

public class DatabaseTests {
    private H2DatabaseAccessor accessor = new H2DatabaseAccessor("sa", "", "~/test");

    @Test
    public void createTableTest() {
        String[] columns = {"id", "first_name", "last_name"};
        String[] primaryKeys = {"id"};
        boolean result = accessor.addTable("people", columns, primaryKeys);
        System.out.print("");
    }

}
