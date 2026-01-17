package org.example.model;

import org.example.exception.ValidationException;

import java.time.LocalDateTime;

public class SimpleExpense extends Expense {
    
    public SimpleExpense(String userId, String category, double amount, String description) throws ValidationException {
        super(userId, category, amount, description);
    }

    public SimpleExpense(String expenseId, String userId, String category, double amount, LocalDateTime dateTime, String description) throws ValidationException {
        super(expenseId, userId, category, amount, dateTime, description);
    }
}
