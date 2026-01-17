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
            System.out.println("5. View Expenses by Date");
            System.out.println("6. Export Expenses to File");
            System.out.println("7. View Summary");
            System.out.println("8. Exit");
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
            
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();

            switch (categoryChoice) {
                case 1:
                    expense = createFoodExpense(amount, description);
                    break;
                case 2:
                    expense = createTravelExpense(amount, description);
                    break;
                case 3:
                    expense = createElectricityExpense(amount, description);
                    break;
                default:
                    System.out.println("Invalid category choice!");
                    return;
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

    private Expense createFoodExpense(double amount, String description) throws ValidationException {
        System.out.print("Enter restaurant name: ");
        String restaurantName = scanner.nextLine().trim();
        if (restaurantName.isEmpty()) {
            throw new ValidationException("Restaurant name cannot be empty");
        }
        
        System.out.print("Enter meal type (Breakfast/Lunch/Dinner/Snacks): ");
        String mealType = scanner.nextLine().trim();
        if (mealType.isEmpty()) {
            throw new ValidationException("Meal type cannot be empty");
        }
        
        return new FoodExpense(currentUser.getUserId(), amount, description, restaurantName, mealType);
    }

    private Expense createTravelExpense(double amount, String description) throws ValidationException {
        System.out.print("Enter mode of transport (Car/Bus/Train/Flight/Taxi): ");
        String modeOfTransport = scanner.nextLine().trim();
        
        System.out.print("Enter destination: ");
        String destination = scanner.nextLine().trim();
        
        System.out.print("Enter distance (km): ");
        double distance = Double.parseDouble(scanner.nextLine().trim());
        
        return new TravelExpense(currentUser.getUserId(), amount, description, modeOfTransport, destination, distance);
    }

    private Expense createElectricityExpense(double amount, String description) throws ValidationException {
        System.out.print("Enter bill number: ");
        String billNumber = scanner.nextLine().trim();
        
        System.out.print("Enter units consumed (kWh): ");
        double unitsConsumed = Double.parseDouble(scanner.nextLine().trim());
        
        System.out.print("Enter provider name: ");
        String provider = scanner.nextLine().trim();
        
        return new ElectricityExpense(currentUser.getUserId(), amount, description, billNumber, unitsConsumed, provider);
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

            System.out.print("Enter new description (press Enter to keep current): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                description = existingExpense.getDescription();
            }

            Expense updatedExpense = null;
            if (existingExpense instanceof FoodExpense) {
                updatedExpense = updateFoodExpense((FoodExpense) existingExpense, amount, description);
            } else if (existingExpense instanceof TravelExpense) {
                updatedExpense = updateTravelExpense((TravelExpense) existingExpense, amount, description);
            } else if (existingExpense instanceof ElectricityExpense) {
                updatedExpense = updateElectricityExpense((ElectricityExpense) existingExpense, amount, description);
            }

            if (updatedExpense != null) {
                expenseRepository.updateExpense(expenseId, updatedExpense);
                System.out.println("Expense updated successfully!");
                System.out.println(updatedExpense.toFormattedString());
            }
        } catch (ExpenseNotFoundException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    private Expense updateFoodExpense(FoodExpense foodExpense, double amount, String description) throws ValidationException {
        return new FoodExpense(foodExpense.getExpenseId(), foodExpense.getUserId(), 
                amount, description, foodExpense.getDateTime(), 
                foodExpense.getRestaurantName(), foodExpense.getMealType());
    }

    private Expense updateTravelExpense(TravelExpense travelExpense, double amount, String description) throws ValidationException {
        return new TravelExpense(travelExpense.getExpenseId(), travelExpense.getUserId(), 
                amount, description, travelExpense.getDateTime(), 
                travelExpense.getModeOfTransport(), travelExpense.getDestination(), travelExpense.getDistance());
    }

    private Expense updateElectricityExpense(ElectricityExpense electricityExpense, double amount, String description) throws ValidationException {
        return new ElectricityExpense(electricityExpense.getExpenseId(), electricityExpense.getUserId(), 
                amount, description, electricityExpense.getDateTime(), 
                electricityExpense.getBillNumber(), electricityExpense.getUnitsConsumed(), electricityExpense.getProvider());
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
        List<String> categories = expenseRepository.getAvailableCategories();
        
        if (categories.isEmpty()) {
            System.out.println("No expenses found. No categories available.");
            return;
        }

        System.out.println("Available categories: " + String.join(", ", categories));
        System.out.print("Enter category name: ");
        String category = scanner.nextLine().trim();

        List<Expense> expenses = expenseRepository.getExpensesByCategory(category);
        
        if (expenses.isEmpty()) {
            System.out.println("No expenses found for category: " + category);
        } else {
            expenses.forEach(expense -> System.out.println(expense.toFormattedString()));
            double total = expenseRepository.getTotalExpensesByCategory(category);
            System.out.println("\nTotal for " + category + ": $" + String.format("%.2f", total));
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

            writer.write("ExpenseID,UserID,Category,Amount,DateTime,Description,AdditionalInfo\n");
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
