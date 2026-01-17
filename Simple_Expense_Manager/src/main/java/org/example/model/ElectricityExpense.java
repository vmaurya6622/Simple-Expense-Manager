package org.example.model;

import org.example.exception.ValidationException;

public class ElectricityExpense extends Expense {
    private String billNumber;
    private double unitsConsumed; // kWh
    private String provider;

    public ElectricityExpense(String userId, double amount, String description, String billNumber, double unitsConsumed, String provider) throws ValidationException {
        super(userId, "Electricity", amount, description);
        this.billNumber = billNumber;
        this.unitsConsumed = unitsConsumed;
        this.provider = provider;
        validate();
    }

    public ElectricityExpense(String expenseId, String userId, double amount, String description, java.time.LocalDateTime dateTime, String billNumber, double unitsConsumed, String provider) throws ValidationException {
        super(expenseId, userId, "Electricity", amount, dateTime, description);
        this.billNumber = billNumber;
        this.unitsConsumed = unitsConsumed;
        this.provider = provider;
        validate();
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (billNumber == null || billNumber.trim().isEmpty()) {
            throw new ValidationException("Bill number cannot be empty for electricity expenses");
        }
        if (unitsConsumed <= 0) {
            throw new ValidationException("Units consumed must be greater than 0");
        }
        if (unitsConsumed > 100000) {
            throw new ValidationException("Units consumed cannot exceed 100,000 kWh");
        }
        if (provider == null || provider.trim().isEmpty()) {
            throw new ValidationException("Provider name cannot be empty");
        }
    }

    @Override
    public String toFormattedString() {
        return super.toFormattedString() + String.format(" | Bill No: %s | Units: %.2f kWh | Provider: %s", 
                billNumber, unitsConsumed, provider);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + billNumber + "," + unitsConsumed + "," + provider;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) throws ValidationException {
        this.billNumber = billNumber;
        validate();
    }

    public double getUnitsConsumed() {
        return unitsConsumed;
    }

    public void setUnitsConsumed(double unitsConsumed) throws ValidationException {
        this.unitsConsumed = unitsConsumed;
        validate();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) throws ValidationException {
        this.provider = provider;
        validate();
    }
}
