package org.example.service;

import org.example.model.*;
import org.example.exception.ExpenseNotFoundException;
import org.example.exception.ValidationException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFileService {
    private static final String EXPENSES_DIR = "expenses";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ExpenseFileService() {
        // Create expenses directory if it doesn't exist
        File dir = new File(EXPENSES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getCsvFilePath(String csvId) {
        return EXPENSES_DIR + File.separator + csvId + ".csv";
    }

    public void addExpense(String csvId, Expense expense) throws IOException {
        String filePath = getCsvFilePath(csvId);
        boolean fileExists = new File(filePath).exists();

        try (FileWriter writer = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(writer)) {
            
            // Write header if file is new
            if (!fileExists) {
                bw.write("ExpenseID,UserID,Category,Amount,DateTime,Description,AdditionalInfo");
                bw.newLine();
            }
            
            bw.write(expense.toCSV());
            bw.newLine();
        }
    }

    public List<Expense> loadAllExpenses(String csvId, String userId) throws IOException {
        String filePath = getCsvFilePath(csvId);
        File file = new File(filePath);
        
        if (!file.exists()) {
            return new ArrayList<>();
        }

        List<Expense> expenses = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                try {
                    Expense expense = parseExpenseFromCsv(line, userId);
                    if (expense != null) {
                        expenses.add(expense);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing expense line: " + line + " - " + e.getMessage());
                }
            }
        }
        
        return expenses;
    }

    private Expense parseExpenseFromCsv(String csvLine, String userId) throws ValidationException {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6) return null;

        String expenseId = parts[0];
        String category = parts[2];
        double amount = Double.parseDouble(parts[3]);
        LocalDateTime dateTime = LocalDateTime.parse(parts[4], DATE_TIME_FORMATTER);
        String description = parts[5];

        switch (category) {
            case "Food":
                if (parts.length >= 8) {
                    return new FoodExpense(expenseId, userId, amount, description, dateTime, parts[6], parts[7]);
                }
                break;
            case "Travel":
                if (parts.length >= 9) {
                    return new TravelExpense(expenseId, userId, amount, description, dateTime, 
                            parts[6], parts[7], Double.parseDouble(parts[8]));
                }
                break;
            case "Electricity":
                if (parts.length >= 9) {
                    return new ElectricityExpense(expenseId, userId, amount, description, dateTime, 
                            parts[6], Double.parseDouble(parts[7]), parts[8]);
                }
                break;
        }
        
        return null;
    }

    public void saveAllExpenses(String csvId, List<Expense> expenses) throws IOException {
        String filePath = getCsvFilePath(csvId);
        
        try (FileWriter writer = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(writer)) {
            
            // Write header
            bw.write("ExpenseID,UserID,Category,Amount,DateTime,Description,AdditionalInfo");
            bw.newLine();
            
            // Write all expenses
            for (Expense expense : expenses) {
                bw.write(expense.toCSV());
                bw.newLine();
            }
        }
    }

    public void updateExpense(String csvId, String expenseId, Expense updatedExpense) throws ExpenseNotFoundException, IOException, ValidationException {
        List<Expense> expenses = loadAllExpenses(csvId, updatedExpense.getUserId());
        
        boolean found = false;
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getExpenseId().equals(expenseId)) {
                expenses.set(i, updatedExpense);
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new ExpenseNotFoundException("Expense with ID '" + expenseId + "' not found");
        }
        
        saveAllExpenses(csvId, expenses);
    }
}
