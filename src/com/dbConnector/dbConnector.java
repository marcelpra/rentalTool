package com.dbConnector;

import java.sql.*;
import java.util.TimeZone;

public class dbConnector {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/rentaltool?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin\n";

    //  Database credentials
    static final String USER = "tooluser";
    static final String PASS = "tool";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");
            TimeZone.setDefault(timeZone);

            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
