package org.example.model;

import org.example.interfaces.Exportable;
import org.example.interfaces.Validatable;
import org.example.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Expense implements Validatable, Exportable {
    protected String expenseId;
    protected String userId;
    protected String category;
    protected double amount;
    protected LocalDate dateTime;
    protected String description;

    public Expense(String userId, String category, double amount, LocalDate date) throws ValidationException {
        this.expenseId = generateExpenseId();
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.dateTime = date;
//        this.description = (description == null) ? "" : description;
        validate();
    }

    public Expense(String expenseId, String userId, String category, double amount, LocalDate date) throws ValidationException {
        this.expenseId = expenseId;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.dateTime = date;
//        this.description = (description == null) ? "" : description;
        validate();
    }

    private String generateExpenseId() {
        return "EXP_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    @Override
    public void validate() throws ValidationException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("User ID cannot be empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Category cannot be empty");
        }
        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than 0");
        }
        if (amount > 1000000) {
            throw new ValidationException("Amount cannot exceed 1,000,000");
        }
        // Description is optional (always empty for new expenses, kept for backward compatibility)
        if (description == null) {
            description = "";
        }
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%.2f,%s",
                expenseId, userId, category, amount,
                dateTime.toString());
    }

    @Override
    public String toFormattedString() {
        return String.format("Expense ID: %s | Category: %s | Amount: $%.2f | Date: %s",
                expenseId, category, amount, dateTime.toString());
    }

    // Getters and Setters
    public String getExpenseId() {
        return expenseId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) throws ValidationException {
        this.amount = amount;
        validate();
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(expenseId, expense.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }

    public LocalDate getDate() {
        return dateTime;
    }
}
