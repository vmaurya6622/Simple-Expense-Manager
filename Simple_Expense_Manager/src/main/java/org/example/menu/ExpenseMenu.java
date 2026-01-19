package org.example.menu;

import org.example.model.*;
import org.example.repository.ExpenseRepository;
import org.example.exception.ExpenseNotFoundException;
import org.example.exception.ValidationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            System.out.println("2. Update Expense Amount by Exp. ID");
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

    private LocalDate getUpdatedDateFromUser() {
        while (true) {
            System.out.println("Choose date option:");
            System.out.println("1. Use current date");
            System.out.println("2. Enter custom date (yyyy-MM-dd)");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                return LocalDate.now();
            }

            if (choice.equals("2")) {
                System.out.print("Enter date (yyyy-MM-dd): ");
                String input = scanner.nextLine().trim();

                try {
                    return LocalDate.parse(
                            input,
                            DateTimeFormatter.ISO_LOCAL_DATE
                    );
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Try again.");
                }
            } else {
                System.out.println("Invalid choice. Please select 1 or 2.");
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
            System.out.println("Choose date option:");
            System.out.println("1. Use current date");
            System.out.println("2. Enter custom date (yyyy-MM-dd)");
            System.out.print("Enter choice: ");

            LocalDate date;

            try {
                int dateChoice = Integer.parseInt(scanner.nextLine().trim());

                if (dateChoice == 1) {
                    date = LocalDate.now();
                }
                else if (dateChoice == 2) {
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    String dateInput = scanner.nextLine().trim();

                    try {
                        date = LocalDate.parse(
                                dateInput,
                                DateTimeFormatter.ISO_LOCAL_DATE
                        );
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format! Please use yyyy-MM-dd.");
                        return;
                    }
                }
                else {
                    System.out.println("Invalid date option! Please select 1 or 2.");
                    return;
                }

                expense = createSimpleExpense(category, amount, date);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric choice.");
            }

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

    private Expense createSimpleExpense(String category, double amount, LocalDate date) throws ValidationException {
        return new org.example.model.SimpleExpense(currentUser.getUserId(), category, amount, date);
    }

    private void handleUpdateExpense() {
        System.out.println("\n--- UPDATE EXPENSE ---");
        System.out.print("Enter expense ID: ");
        String expenseId = scanner.nextLine().trim();

        try {
            Expense existing = expenseRepository.getExpenseById(expenseId);

            if (!existing.getUserId().equals(currentUser.getUserId())) {
                System.out.println("Error: You can only update your own expenses!");
                return;
            }

            System.out.println("\nCurrent expense details:");
            System.out.println(existing.toFormattedString());

            /* ================= UPDATE AMOUNT ================= */

            double amount = existing.getAmount();
            System.out.print("\nUpdate amount? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.print("Enter new amount: ");
                amount = Double.parseDouble(scanner.nextLine().trim());
            }

            /* ================= UPDATE CATEGORY ================= */

            String category = existing.getCategory();
            System.out.print("\nUpdate category? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("1. Food\n2. Travel\n3. Electricity\n4. Miscellaneous");
                System.out.print("Enter new category choice: ");

                category = switch (scanner.nextLine().trim()) {
                    case "1" -> "Food";
                    case "2" -> "Travel";
                    case "3" -> "Electricity";
                    case "4" -> "Miscellaneous";
                    default -> throw new ValidationException("Invalid category choice");
                };
            }

            /* ================= UPDATE DATE ================= */

            LocalDate date = existing.getDate();
            System.out.print("\nUpdate date? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                date = getUpdatedDateFromUser();
            }

            Expense updatedExpense = new SimpleExpense(
                    existing.getExpenseId(),
                    existing.getUserId(),
                    category,
                    amount,
                    date
            );

            expenseRepository.updateExpense(expenseId, updatedExpense);

            System.out.println("\nExpense updated successfully!");
            System.out.println(updatedExpense.toFormattedString());

        } catch (ExpenseNotFoundException | IOException |
                 ValidationException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
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
        System.out.println("\n--- EXPENSES BY DATE RANGE ---");

        try {
            System.out.print("Enter start date (yyyy-MM-dd): ");
            String startInput = scanner.nextLine().trim();

            System.out.print("Enter end date (yyyy-MM-dd): ");
            String endInput = scanner.nextLine().trim();

            LocalDate startDate = LocalDate.parse(
                    startInput, DateTimeFormatter.ISO_LOCAL_DATE);

            LocalDate endDate = LocalDate.parse(
                    endInput, DateTimeFormatter.ISO_LOCAL_DATE);

            if (startDate.isAfter(endDate)) {
                System.out.println("Start date cannot be after end date.");
                return;
            }

            List<Expense> expenses =
                    expenseRepository.getAllExpenses().stream()
                            .filter(e ->
                                    !e.getDate().isBefore(startDate) &&
                                            !e.getDate().isAfter(endDate))
                            .toList();

            if (expenses.isEmpty()) {
                System.out.println("No expenses found between "
                        + startDate + " and " + endDate);
                return;
            }

            System.out.println("\nExpenses from " + startDate + " to " + endDate + ":");
            expenses.forEach(e ->
                    System.out.println(e.toFormattedString()));

            double total = expenses.stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

            System.out.println("\nSummary:");
            System.out.println("Total Expenses: $" + String.format("%.2f", total));

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Please use yyyy-MM-dd.");
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
