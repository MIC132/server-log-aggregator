package classes;/*
Copyright (c) 2015, AGH University of Science and Technology
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;
import static org.h2.util.StringUtils.isNullOrEmpty;

/**
 * Class responsible for establishing and validating connection with database.
 */
public class H2DatabaseConnector {
    private static final String dbDriver = "org.h2.Driver";
    private final String username;
    private final String password;
    private final String databaseURL;

    /**
     * Constructs a new @see classes.H2DatabaseConnector with username, password and databaseURL
     *
     * @param username    - name of user account used for connecting with database
     * @param password    - password credentials
     * @param databaseURL - full URL that identifies database (host/directory + database name)
     *
     * @throws NullPointerException - when one of given parameters is null or empty
     */
    public H2DatabaseConnector(String username, String password, String databaseURL) {
        if (isNullOrEmpty(username)) {
            throw new NullPointerException("username cannot be null");
        }
        requireNonNull(password);
        if (isNullOrEmpty(databaseURL)) {
            throw new NullPointerException("databaseURL cannot be null");
        }

        this.username = username;
        this.password = password;
        this.databaseURL = databaseURL;
    }

    /**
     * Returns the username
     *
     * @return Value of <code>username</code> field
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the password
     *
     * @return Value of <code>password</code> field
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the database URL
     *
     * @return Value of <code>databaseURL</code> field
     */
    public String getDatabaseURL() {
        return this.databaseURL;
    }

    /**
     * Returns the instance of <code>Connection</code> class using values of fields username, password and databaseURL
     *
     * @return New instance of <code>Connection</code>
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public Connection getConnection() throws SQLException {
        Connection connection;
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 driver is missing", e);
        }

        try {
            connection = DriverManager.getConnection(databaseURL, username, password);
        } catch (SQLException e) {
            throw new SQLException("Could not open connection to database", e);
        }

        return connection;
    }
}
