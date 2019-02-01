package fr.epsi.book.dal;

import java.sql.*;

public class PersistenceManager {
	
	private static final String DB_URL = "jdbc:sqlite:C:/Users/Eddy/Documents/SQLite/Database/db_java.db";
	private static final String DB_LOGIN = "";
	private static final String DB_PWD = "";
	
	private static Connection connection;
	
	private PersistenceManager() {}
	
	public static Connection getConnection() throws SQLException {
		if ( null == connection || connection.isClosed() ) {
            try {
                // create a connection to the database1
                connection = DriverManager.getConnection( DB_URL);

                System.out.println("Connection to SQLite has been established.");

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
		}
		
		return connection;
	}
	
	public static void closeConnection() throws SQLException {
		if ( null != connection && !connection.isClosed() ) {
			connection.close();
		}
	}
}
