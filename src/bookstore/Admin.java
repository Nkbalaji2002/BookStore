package bookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
	Scanner scanner = new Scanner(System.in);

	public void adminLogin() {
		System.out.print("Enter Admin Username: ");
		String username = scanner.next();
		System.out.print("Enter Password: ");
		String password = scanner.next();

		if (login(username, password, "admin")) {
			System.out.println("Admin login successful!");
			adminMenu();
		} else {
			System.out.println("Invalid credentials. Try again.");
		}
	}

	private boolean login(String username, String password, String role) {
		try (Connection conn = DatabaseUtil.getConnection()) {
			String query = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, role);

			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void adminMenu() {
		while (true) {
			System.out.println("\nAdmin Menu");
			System.out.println("1. Add Book");
			System.out.println("2. Delete Book");
			System.out.println("3. View Books");
			System.out.println("4. Search Books");
			System.out.println("5. Logout");
			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();

			switch (choice) {
			case 1:
				addBook();
				break;
			case 2:
				deleteBook();
				break;
			case 3:
				showBooks();
				break;
			case 4:
				searchBooks();
				break;
			case 5:
				System.out.println("Logged out successfully!");
				return;
			default:
				System.out.println("Invalid choice. Try again.");
			}
		}
	}

	private void addBook() {
		try (Connection conn = DatabaseUtil.getConnection()) {
			System.out.print("Enter Book Title: ");
			scanner.nextLine(); // consume newline
			String title = scanner.nextLine();
			System.out.print("Enter Author: ");
			String author = scanner.nextLine();
			System.out.print("Enter Price: ");
			double price = scanner.nextDouble();
			System.out.print("Enter Quantity: ");
			int quantity = scanner.nextInt();

			String query = "INSERT INTO Books (title, author, price, quantity) VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, title);
			ps.setString(2, author);
			ps.setDouble(3, price);
			ps.setInt(4, quantity);
			ps.executeUpdate();

			System.out.println("Book added successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteBook() {
		try (Connection conn = DatabaseUtil.getConnection()) {
			System.out.print("Enter Book ID to delete: ");
			int bookId = scanner.nextInt();

			String query = "DELETE FROM Books WHERE id = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, bookId);
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Book deleted successfully!");
			} else {
				System.out.println("Book not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showBooks() {
		try (Connection conn = DatabaseUtil.getConnection()) {
			String query = "SELECT * FROM Books";
			PreparedStatement ps = conn.prepareStatement(query);

			ResultSet rs = ps.executeQuery();
			System.out.println("\nSearch Results:");
			while (rs.next()) {
				System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Author: "
						+ rs.getString("author") + ", Price: " + rs.getDouble("price") + ", Quantity: "
						+ rs.getInt("quantity"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void searchBooks() {
		try (Connection conn = DatabaseUtil.getConnection()) {
			System.out.print("Enter search keyword (title/author): ");
			scanner.nextLine(); // consume newline
			String keyword = scanner.nextLine();

			String query = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, "%" + keyword + "%");
			ps.setString(2, "%" + keyword + "%");

			ResultSet rs = ps.executeQuery();
			System.out.println("\nSearch Results:");
			while (rs.next()) {
				System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Author: "
						+ rs.getString("author") + ", Price: " + rs.getDouble("price") + ", Quantity: "
						+ rs.getInt("quantity"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
