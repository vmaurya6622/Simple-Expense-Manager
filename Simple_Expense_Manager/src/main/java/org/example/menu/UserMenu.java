package org.example.menu;

import org.example.model.User;
import org.example.service.UserStorageService;
import org.example.exception.DuplicateUserException;
import org.example.exception.UserNotFoundException;
import org.example.exception.ValidationException;

import java.io.IOException;
import java.util.Scanner;

public class UserMenu {
    private Scanner scanner;
    private UserStorageService userStorageService;

    public UserMenu(Scanner scanner, UserStorageService userStorageService) {
        this.scanner = scanner;
        this.userStorageService = userStorageService;
    }

    public User displayMenu() {
        while (true) {
            System.out.println("\n========== USER MANAGEMENT ==========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter specified numbers only.");
                continue;
            }

            switch (choice) {
                case 1:
                    User registeredUser = handleRegister();
                    if (registeredUser != null) {
                        return registeredUser;
                    }
                    break;
                case 2:
                    User loggedInUser = handleLogin();
                    if (loggedInUser != null) {
                        return loggedInUser;
                    }
                    break;
                case 3:
                    System.out.println("Thank you for using Expense Manager! Goodbye!");
                    System.exit(0);
                    return null;
                default:
                    System.out.println("Invalid choice! Please Enter Correct Choice.");
            }
        }
    }

    private User handleRegister() {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try {
            // Create a temporary user to generate userId, csvId will be set in storage service
            User user = new User(name, username, password, "TEMP_CSV_ID");
            userStorageService.register(user);
            
            // Reload user to get the actual csvId
            user = userStorageService.login(username, password);
            System.out.println("Registration successful! Welcome, " + user.getName() + "!");
            System.out.println("Your CSV ID: " + user.getCsvId());
            return user;
        } catch (DuplicateUserException | UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        } catch (ValidationException | IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private User handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = userStorageService.login(username, password);
            System.out.println("Login successful! Welcome back, " + user.getName() + "!");
            return user;
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
