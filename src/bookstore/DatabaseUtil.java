package bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
	private static final String URL = "jdbc:mysql://localhost:3306/BookStore";
	private static final String USER = "root"; // Update with your MySQL username
	private static final String PASSWORD = "Root@1234"; // Update with your MySQL password

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
