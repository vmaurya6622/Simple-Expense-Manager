package org.example;

import org.example.menu.ExpenseMenu;
import org.example.menu.UserMenu;
import org.example.model.User;
import org.example.repository.ExpenseRepository;
import org.example.service.ExpenseFileService;
import org.example.service.UserStorageService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserStorageService userStorageService = new UserStorageService();
        ExpenseFileService expenseFileService = new ExpenseFileService();

        System.out.println("========================================");
        System.out.println("  EXPENSE MANAGEMENT SYSTEM");
        System.out.println("========================================");

        while (true) {
            UserMenu userMenu = new UserMenu(scanner, userStorageService);
            User currentUser = userMenu.displayMenu();

            if (currentUser != null) {
                ExpenseRepository expenseRepository = new ExpenseRepository(
                        expenseFileService, 
                        currentUser.getCsvId(), 
                        currentUser.getUserId()
                );
                ExpenseMenu expenseMenu = new ExpenseMenu(scanner, expenseRepository, currentUser);
                expenseMenu.displayMenu();
            }
        }
    }
}
