package org.example.menu;

import org.example.model.*;
import org.example.repository.ExpenseRepository;
import org.example.exception.ExpenseNotFoundException;
import org.example.exception.ValidationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ExpenseMenu {
    private Scanner scanner;
    private ExpenseRepository expenseRepository;
    private User currentUser;

    public ExpenseMenu(Scanner scanner, ExpenseRepository expenseRepository, User currentUser) {
        this.scanner = scanner;
        this.expenseRepository = expenseRepository;
        this.currentUser = currentUser;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n========== EXPENSE MANAGEMENT ==========");
            System.out.println("1. Add Expense");
            System.out.println("2. Update Expense");
            System.out.println("3. View All Expenses");
            System.out.println("4. View Expenses by Category");
            System.out.println("5. View Expenses by Duration");
            System.out.println("6. Export Expenses to File");
            System.out.println("7. View Summary");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");

            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddExpense();
                    break;
                case 2:
                    handleUpdateExpense();
                    break;
                case 3:
                    handleViewAllExpenses();
                    break;
                case 4:
                    handleViewExpensesByCategory();
                    break;
                case 5:
                    handleViewExpensesByDate();
                    break;
                case 6:
                    handleExportToFile();
                    break;
                case 7:
                    handleViewSummary();
                    break;
                case 8:
                    System.out.println("Logging out... Thank you for using Expense Manager!");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void handleAddExpense() {
        System.out.println("\n--- ADD EXPENSE ---");
        System.out.println("Select category:");
        System.out.println("1. Food");
        System.out.println("2. Travel");
        System.out.println("3. Electricity");
        System.out.println("4. Miscellaneous");
        System.out.print("Enter category choice: ");

        try {
            int categoryChoice = Integer.parseInt(scanner.nextLine().trim());
            Expense expense = null;

            System.out.print("Enter amount: ");
            String amountInput = scanner.nextLine().trim();
            // Remove $ sign if user includes it
            if (amountInput.startsWith("$")) {
                amountInput = amountInput.substring(1).trim();
            }
            double amount = Double.parseDouble(amountInput);

            String category = "";
            switch (categoryChoice) {
                case 1:
                    category = "Food";
                    break;
                case 2:
                    category = "Travel";
                    break;
                case 3:
                    category = "Electricity";
                    break;
                case 4:
                    category = "Miscellaneous";
                    break;
                default:
                    System.out.println("Invalid category choice!");
                    return;
            }

            //Inputting date for inclusion of datetime
            System.out.println("Choose date option:");
            System.out.println("1. Use current date & time");
            System.out.println("2. Enter custom date (ddMMyyyy)");
            System.out.print("Enter choice: ");

            int dateChoice = Integer.parseInt(scanner.nextLine().trim());
            LocalDate date;

            if (dateChoice == 1) {
                date = LocalDate.now();
            } else if (dateChoice == 2) {
                System.out.print("Enter date (ddMMyyyy): ");
                String dateInput = scanner.nextLine().trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                date = LocalDate.parse(dateInput, formatter);
            } else {
                System.out.println("Invalid date option!");
                return;
            }
            
            expense = createSimpleExpense(category, amount);

            if (expense != null) {
                expenseRepository.addExpense(expense);
                System.out.println("Expense added successfully!");
                System.out.println(expense.toFormattedString());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
        } catch (ValidationException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Expense createSimpleExpense(String category, double amount) throws ValidationException {
        return new org.example.model.SimpleExpense(currentUser.getUserId(), category, amount, "");
    }

    private void handleUpdateExpense() {
        System.out.println("\n--- UPDATE EXPENSE ---");
        System.out.print("Enter expense ID: ");
        String expenseId = scanner.nextLine().trim();

        try {
            Expense existingExpense = expenseRepository.getExpenseById(expenseId);
            
            if (!existingExpense.getUserId().equals(currentUser.getUserId())) {
                System.out.println("Error: You can only update your own expenses!");
                return;
            }

            System.out.println("Current expense details:");
            System.out.println(existingExpense.toFormattedString());
            System.out.println("\nEnter new details:");

            System.out.print("Enter new amount (press Enter to keep current): ");
            String amountInput = scanner.nextLine().trim();
            double amount = amountInput.isEmpty() ? existingExpense.getAmount() : Double.parseDouble(amountInput);

            // Create updated expense with same category but new amount (description is always empty)
            Expense updatedExpense = new org.example.model.SimpleExpense(
                    existingExpense.getExpenseId(),
                    existingExpense.getUserId(),
                    existingExpense.getCategory(),
                    amount,
                    existingExpense.getDateTime(),
                    ""
            );

            expenseRepository.updateExpense(expenseId, updatedExpense);
            System.out.println("Expense updated successfully!");
            System.out.println(updatedExpense.toFormattedString());
        } catch (ExpenseNotFoundException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }


    private void handleViewAllExpenses() {
        System.out.println("\n--- ALL EXPENSES ---");
        List<Expense> expenses = expenseRepository.getAllExpenses();
        
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            expenses.forEach(expense -> System.out.println(expense.toFormattedString()));
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.println("\nTotal Expenses: $" + String.format("%.2f", total));
        }
    }

    private void handleViewExpensesByCategory() {
        System.out.println("\n--- EXPENSES BY CATEGORY ---");
        System.out.println("Select category:");
        System.out.println("1. Food");
        System.out.println("2. Travel");
        System.out.println("3. Electricity");
        System.out.println("4. Miscellaneous");
        System.out.print("Enter category choice (1-4): ");

        try {
            int categoryChoice = Integer.parseInt(scanner.nextLine().trim());
            String category = "";

            switch (categoryChoice) {
                case 1:
                    category = "Food";
                    break;
                case 2:
                    category = "Travel";
                    break;
                case 3:
                    category = "Electricity";
                    break;
                case 4:
                    category = "Miscellaneous";
                    break;
                default:
                    System.out.println("Invalid category choice! Please enter a number between 1 and 4.");
                    return;
            }

            List<Expense> expenses = expenseRepository.getExpensesByCategory(category);
            
            if (expenses.isEmpty()) {
                System.out.println("No expenses found for category: " + category);
            } else {
                expenses.forEach(expense -> System.out.println(expense.toFormattedString()));
                double total = expenseRepository.getTotalExpensesByCategory(category);
                System.out.println("\nTotal for " + category + ": $" + String.format("%.2f", total));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number between 1 and 4.");
        }
    }

    private void handleViewExpensesByDate() {
        System.out.println("\n--- EXPENSES BY DATE ---");
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            List<Expense> expenses = expenseRepository.getExpensesByDate(date);
            
            if (expenses.isEmpty()) {
                System.out.println("No expenses found for date: " + date);
            } else {
                expenses.forEach(expense -> System.out.println(expense.toFormattedString()));
                double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
                System.out.println("\nTotal for " + date + ": $" + String.format("%.2f", total));
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Please use yyyy-MM-dd format.");
        }
    }

    private void handleExportToFile() {
        System.out.println("\n--- EXPORT EXPENSES TO FILE ---");
        System.out.print("Enter filename (without extension): ");
        String filename = scanner.nextLine().trim();
        
        if (filename.isEmpty()) {
            filename = currentUser.getName() + "_expenses_" + System.currentTimeMillis();
        }
        
        filename += ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            List<Expense> expenses = expenseRepository.getAllExpenses();
            
            if (expenses.isEmpty()) {
                System.out.println("No expenses to export.");
                return;
            }

            writer.write("ExpenseID,UserID,Category,Amount,DateTime\n");
            for (Expense expense : expenses) {
                writer.write(expense.toCSV() + "\n");
            }

            System.out.println("Expenses exported successfully to: " + filename);
            System.out.println("Total expenses exported: " + expenses.size());
        } catch (IOException e) {
            System.out.println("Error exporting expenses: " + e.getMessage());
        }
    }

    private void handleViewSummary() {
        System.out.println("\n--- EXPENSE SUMMARY ---");
        double totalExpenses = expenseRepository.getTotalExpenses();
        List<String> categories = expenseRepository.getAvailableCategories();
        
        System.out.println("Total Expenses: $" + String.format("%.2f", totalExpenses));
        System.out.println("\nBreakdown by Category:");
        
        categories.forEach(category -> {
            double categoryTotal = expenseRepository.getTotalExpensesByCategory(category);
            double percentage = totalExpenses > 0 ? (categoryTotal / totalExpenses) * 100 : 0;
            System.out.printf("  %s: $%.2f (%.2f%%)\n", category, categoryTotal, percentage);
        });
    }
}
