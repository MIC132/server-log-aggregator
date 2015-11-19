import java.sql.*;
import java.util.List;

/**
 * Provides access to specified database.
 * Requires informations about database name and host/domain, also needs user security credentials (username, password).
 * Provides basic CRUD operations through implemented methods.
 */
public class H2DatabaseAccessor {

    /**
     * class {@link H2DatabaseConnector} is responsible for establishing and validating connection with database.
     */
    private final H2DatabaseConnector connector;

    /**
     * Creates an instance of @see H2DatabaseAccessor
     *
     * @param username    - name of user account used for connecting with database
     * @param password    - password credentials
     * @param databaseURL - full URL that identifies database (host/directory + database name)
     */
    public H2DatabaseAccessor(String username, String password, String databaseURL) {
        connector = new H2DatabaseConnector(username, password, String.format("jdbc:h2:%s;IFEXISTS=TRUE;DATABASE_TO_UPPER=false", databaseURL));
    }

    /**
     *
     * */
    public boolean addTable(String tableName, List<String> columnNames, List<String> primaryKeys) {
        dropTable(tableName);
        boolean result = true;
        try {
            // Check arguments validity
            if (columnNames.isEmpty()) {
                throw new Exception("No column names specified");
            }
            if (primaryKeys.isEmpty()) {
                throw new Exception("No primary key specified");
            }
            if (primaryKeys.size() > columnNames.size()) {
                throw new Exception("Number of primary keys exceeds number of columnsBuilder");
            }

            // Create query
            StringBuilder columnsBuilder = new StringBuilder();
            StringBuilder primaryKeyBuilder = new StringBuilder("primary key(");
            StringBuilder queryBuilder = new StringBuilder("create table ").append(tableName).append('(');

            for (String columnName : columnNames) {
                columnsBuilder.append(columnName).append(" varchar(255), ");
            }

            primaryKeyBuilder.append(primaryKeys.get(0));
            for (int index = 1; index < primaryKeys.size(); index++) {
                primaryKeyBuilder.append(", ").append(primaryKeys.get(index));
            }
            primaryKeyBuilder.append(')');

            queryBuilder.append(columnsBuilder)
                    .append(primaryKeyBuilder)
                    .append(");");

            // Execute query
            Connection connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            statement.executeUpdate();
            statement.close();

            connection.commit();
            connection.close();
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
            result = false;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     *
     * */
    public boolean dropTable(String tableName) {
        boolean result = true;
        try {
            // Create query
            StringBuilder queryBuilder = new StringBuilder("drop table if exists ").append(tableName).append(';');

            // Execute query
            Connection connection = connector.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            statement.executeUpdate();
            statement.close();

            connection.commit();
            connection.close();
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     *
     * */
    public boolean addRowToTable(String tableName, List<String> columnNames, List<String> rowValues) {
        boolean result = true;
        try {
            // Check arguments validity
            if (columnNames.isEmpty()) {
                throw new Exception("No column names specified");
            }
            if (rowValues.isEmpty()) {
                throw new Exception("No row values specified");
            }
            if (rowValues.size() != columnNames.size()) {
                throw new Exception("Number of values must be equal to number of column names");
            }

            // Create query
            StringBuilder queryBuilder = new StringBuilder("insert into ")
                    .append(tableName).append(" (");
            StringBuilder columnNamesBuilder = new StringBuilder();
            StringBuilder rowValuesBuilder = new StringBuilder();

            columnNamesBuilder.append(columnNames.get(0));
            for (int index = 1; index < columnNames.size(); index++) {
                columnNamesBuilder.append(", ").append(columnNames.get(index));
            }

            rowValuesBuilder.append(rowValues.get(0));
            for (int index = 1; index < rowValues.size(); index++) {
                rowValuesBuilder.append(", \'").append(rowValues.get(index)).append("\'");
            }
            queryBuilder.append(columnNamesBuilder).append(") values (")
                    .append(rowValuesBuilder).append(");");

            // Execute query
            Connection connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            int i = statement.executeUpdate();
            statement.close();

            connection.commit();
            connection.close();
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
            result = false;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    /**
     *
     * */
    public ResultSet selectValuesFromTable(String tableName, List<String> columnNames) {
        ResultSet result = null;
        try {
            if (columnNames.isEmpty()) {
                throw new Exception("No column names specified");
            }

            StringBuilder columnNamesBuilder = new StringBuilder();
            StringBuilder queryBuilder = new StringBuilder("select ");

            queryBuilder.append(columnNames.get(0));
            for (int index = 1; index < columnNames.size(); index++) {
                columnNamesBuilder.append(", ").append(columnNames.get(index));
            }

            queryBuilder.append(columnNamesBuilder).append(" from ").append(tableName).append(';');

            Connection connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

            result = statement.executeQuery();
            statement.close();

            connection.commit();
            connection.close();
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
