package bookstore;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
			Admin admin = new Admin();
			User user = new User();

			while (true) {
			    System.out.println("Welcome to the Book Store");
			    System.out.println("1. Admin Login");
			    System.out.println("2. User Login");
			    System.out.println("3. Register as User");
			    System.out.println("4. Exit");
			    System.out.print("Enter your choice: ");
			    int choice = scanner.nextInt();

			    switch (choice) {
			        case 1:
			            admin.adminLogin();
			            break;
			        case 2:
			            user.userLogin();
			            break;
			        case 3:
			            user.registerUser();
			            break;
			        case 4:
			            System.out.println("Thank you for using the Book Store!");
			            System.exit(0);
			        default:
			            System.out.println("Invalid choice. Please try again.");
			    }
			}
		}
    }
}
