package org.example.model;

import org.example.exception.ValidationException;

public class FoodExpense extends Expense {
    private String restaurantName;
    private String mealType; // Breakfast, Lunch, Dinner, Snacks

    public FoodExpense(String userId, double amount, String description, String restaurantName, String mealType) throws ValidationException {
        super(userId, "Food", amount, description);
        this.restaurantName = restaurantName;
        this.mealType = mealType;
        validate();
    }

    public FoodExpense(String expenseId, String userId, double amount, String description, java.time.LocalDateTime dateTime, String restaurantName, String mealType) throws ValidationException {
        super(expenseId, userId, "Food", amount, dateTime, description);
        this.restaurantName = restaurantName;
        this.mealType = mealType;
        validate();
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (restaurantName == null || restaurantName.trim().isEmpty()) {
            throw new ValidationException("Restaurant name cannot be empty for food expenses");
        }
        if (mealType == null || mealType.trim().isEmpty()) {
            throw new ValidationException("Meal type cannot be empty");
        }
        String[] validMealTypes = {"Breakfast", "Lunch", "Dinner", "Snacks"};
        boolean isValid = false;
        for (String type : validMealTypes) {
            if (type.equalsIgnoreCase(mealType)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new ValidationException("Meal type must be one of: Breakfast, Lunch, Dinner, Snacks");
        }
    }

    @Override
    public String toFormattedString() {
        return super.toFormattedString() + String.format(" | Restaurant: %s | Meal Type: %s", restaurantName, mealType);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + restaurantName + "," + mealType;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) throws ValidationException {
        this.restaurantName = restaurantName;
        validate();
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) throws ValidationException {
        this.mealType = mealType;
        validate();
    }
}
