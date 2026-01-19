package org.example.model;

import org.example.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SimpleExpense extends Expense {
    
    public SimpleExpense(String userId, String category, double amount, LocalDate date) throws ValidationException {
        super(userId, category, amount, date);
    }

    public SimpleExpense(String expenseId, String userId, String category, double amount, LocalDate dateTime) throws ValidationException {
        super(expenseId, userId, category, amount, dateTime);
    }

//    public SimpleExpense(String userId, String category, double amount, LocalDate date) {
//        super();
//    }
}
