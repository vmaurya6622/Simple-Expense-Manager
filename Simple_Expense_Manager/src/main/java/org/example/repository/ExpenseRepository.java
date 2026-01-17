package org.example.repository;

import org.example.model.Expense;
import org.example.exception.ExpenseNotFoundException;
import org.example.service.ExpenseFileService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExpenseRepository {
    private ExpenseFileService fileService;
    private String csvId;
    private String userId;

    public ExpenseRepository(ExpenseFileService fileService, String csvId, String userId) {
        this.fileService = fileService;
        this.csvId = csvId;
        this.userId = userId;
    }

    public void addExpense(Expense expense) throws IOException {
        fileService.addExpense(csvId, expense);
    }

    public void updateExpense(String expenseId, Expense updatedExpense) throws ExpenseNotFoundException, IOException {
        try {
            fileService.updateExpense(csvId, expenseId, updatedExpense);
        } catch (org.example.exception.ValidationException e) {
            throw new IOException("Validation error: " + e.getMessage(), e);
        }
    }

    private List<Expense> getAllExpensesInternal() throws IOException {
        return fileService.loadAllExpenses(csvId, userId);
    }

    public List<Expense> getAllExpenses() {
        try {
            return getAllExpensesInternal();
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return List.of();
        }
    }

    public List<Expense> getExpensesByCategory(String category) {
        try {
            return getAllExpensesInternal().stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return List.of();
        }
    }

    public List<Expense> getExpensesByDate(LocalDate date) {
        try {
            return getAllExpensesInternal().stream()
                    .filter(e -> e.getDateTime().toLocalDate().equals(date))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return List.of();
        }
    }

    public Expense getExpenseById(String expenseId) throws ExpenseNotFoundException {
        try {
            Optional<Expense> expense = getAllExpensesInternal().stream()
                    .filter(e -> e.getExpenseId().equals(expenseId))
                    .findFirst();
            
            if (expense.isEmpty()) {
                throw new ExpenseNotFoundException("Expense with ID '" + expenseId + "' not found");
            }
            
            return expense.get();
        } catch (IOException e) {
            throw new ExpenseNotFoundException("Error loading expenses: " + e.getMessage());
        }
    }

    public double getTotalExpenses() {
        try {
            return getAllExpensesInternal().stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return 0.0;
        }
    }

    public double getTotalExpensesByCategory(String category) {
        try {
            return getAllExpensesInternal().stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .mapToDouble(Expense::getAmount)
                    .sum();
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return 0.0;
        }
    }

    public List<String> getAvailableCategories() {
        try {
            return getAllExpensesInternal().stream()
                    .map(Expense::getCategory)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return List.of();
        }
    }
}
