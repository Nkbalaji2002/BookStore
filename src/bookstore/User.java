package bookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    Scanner scanner = new Scanner(System.in);

    public void userLogin() {
        System.out.print("Enter Username: ");
        String username = scanner.next();
        System.out.print("Enter Password: ");
        String password = scanner.next();

        if (login(username, password, "user")) {
            System.out.println("User login successful!");
            userMenu(username);
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

    public void registerUser() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            System.out.print("Enter Username: ");
            String username = scanner.next();
            System.out.print("Enter Password: ");
            String password = scanner.next();

            String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'user')";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void userMenu(String username) {
        while (true) {
            System.out.println("\nUser Menu");
            System.out.println("1. Search Books");
            System.out.println("2. Purchase Book");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    searchBooks();
                    break;
                case 2:
                    purchaseBook(username);
                    break;
                case 3:
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
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
                System.out.println("ID: " + rs.getInt("id") +
                                   ", Title: " + rs.getString("title") +
                                   ", Author: " + rs.getString("author") +
                                   ", Price: " + rs.getDouble("price") +
                                   ", Quantity: " + rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void purchaseBook(String username) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            System.out.print("Enter Book ID to purchase: ");
            int bookId = scanner.nextInt();
            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();

            // Check book availability
            String checkQuery = "SELECT * FROM Books WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int availableQuantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                if (quantity > availableQuantity) {
                    System.out.println("Insufficient stock.");
                    return;
                }

                // Update book stock
                String updateQuery = "UPDATE Books SET quantity = quantity - ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, bookId);
                updateStmt.executeUpdate();

                // Insert transaction record
                String userQuery = "SELECT id FROM Users WHERE username = ?";
                PreparedStatement userStmt = conn.prepareStatement(userQuery);
                userStmt.setString(1, username);
                ResultSet userRs = userStmt.executeQuery();
                userRs.next();
                int userId = userRs.getInt("id");

                double totalPrice = price * quantity;
                String insertQuery = "INSERT INTO Transactions (user_id, book_id, quantity, total_price) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, bookId);
                insertStmt.setInt(3, quantity);
                insertStmt.setDouble(4, totalPrice);
                insertStmt.executeUpdate();

                System.out.println("Purchase successful! Total Price: $" + totalPrice);
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
