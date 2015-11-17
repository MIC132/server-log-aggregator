import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides access to specified database.
 * Requires informations about database name and host/domain, also needs user security credentials (username, password).
 * Provides basic CRUD operations through implemented methods.
 * */
public class H2DatabaseAccessor {

    /**
     * class {@link H2DatabaseConnector} is responsible for establishing and validating connection with database.
     * */
    private final H2DatabaseConnector connector;

    /**
     * Creates an instance of @see H2DatabaseAccessor
     * @param username - name of user account used for connecting with database
     * @param password - password credentials
     * @param databaseURL - full URL that identifies database (host/directory + database name)
     * */
    public H2DatabaseAccessor(String username, String password, String databaseURL) {
        try {
            Class.forName("org.h2.Driver");
            connector = new H2DatabaseConnector(username, password, String.format("jdbc:h2:%s;IFEXISTS=TRUE", databaseURL));
        } catch (SQLException e) {
            throw new RuntimeException("Could not open connection to database", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 driver is missing", e);
        }
    }
    /**
     *
     * */
    public boolean addTable(String tableName, String[] columnNames, String[] primaryKeys) {
        dropTable(tableName);
        boolean result = false;
        try {
            Statement statement = connector.getConnection().createStatement();

            if (columnNames.length == 0) {
                throw new Exception("No column names specified");
            }
            if (primaryKeys.length == 0) {
                throw new Exception("No primary key specified");
            }
            if (primaryKeys.length > columnNames.length) {
                throw new Exception("Number of primary keys exceeds number of columnsBuilder");
            }

            StringBuilder columnsBuilder = new StringBuilder();
            StringBuilder primaryKeyBuilder = new StringBuilder("PRIMARY KEY(");
            StringBuilder queryBuilder = new StringBuilder("create table ").append(tableName).append('(');

            for (String columnName: columnNames) {
                columnsBuilder.append(columnName).append(" varchar(255), ");
            }

            primaryKeyBuilder.append(primaryKeys[0]);
            for (int index = 1; index < primaryKeys.length; index++) {
                primaryKeyBuilder.append(", ").append(primaryKeys[index]);
            }
            primaryKeyBuilder.append(')');

            queryBuilder.append(columnsBuilder);
            queryBuilder.append(primaryKeyBuilder);
            queryBuilder.append(");");

            result = statement.execute(queryBuilder.toString());
            statement.close();
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * */
    public boolean dropTable(String tableName) {
        boolean result = false;
        try {
            Statement statement = connector.getConnection().createStatement();
            // IF OBJECT_ID(table name, 'U') IS NOT NULL DROP TABLE table_name;
            StringBuilder queryBuilder = new StringBuilder("drop table ").append(tableName).append(';');
            result = statement.execute(queryBuilder.toString());
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * */
    public ResultSet addRowToTable(String tableName, String[] rowValues) {
        ResultSet result = null;

        try {
            Statement statement = connector.getConnection().createStatement();

            if (rowValues.length == 0) {
                throw new Exception("No row values specified");
            }

            StringBuilder rowValuesBuilder = new StringBuilder();
            StringBuilder queryBuilder = new StringBuilder("insert into ").append(tableName).append(" values (");

            for (String rowValue: rowValues) {
                rowValuesBuilder.append(rowValue).append(", ");
            }

            queryBuilder.append(rowValuesBuilder);
            queryBuilder.append(");");
            result = statement.executeQuery(queryBuilder.toString());
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     *
     * */
    public ResultSet selectValuesFromTable(String tableName, String[] columnNames) {
        ResultSet result = null;

        try {
            Statement statement = connector.getConnection().createStatement();

            if (columnNames.length == 0) {
                throw new Exception("No column names specified");
            }

            StringBuilder columnNamesBuilder = new StringBuilder();
            StringBuilder queryBuilder = new StringBuilder("select ");

            queryBuilder.append(columnNames[0]);
            for (int index = 1; index < columnNames.length; index++) {
                columnNamesBuilder.append(", ").append(columnNames[index]);
            }

            queryBuilder.append(columnNames).append(" from ").append(tableName).append(';');


        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     *
     * */
    public ResultSet executeQuery(String query) {
        ResultSet result = null;

        try {
            Statement statement = connector.getConnection().createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
        }

        return result;
    }
}
