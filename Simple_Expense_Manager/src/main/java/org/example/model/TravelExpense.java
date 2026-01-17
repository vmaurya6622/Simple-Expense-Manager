package org.example.model;

import org.example.exception.ValidationException;

public class TravelExpense extends Expense {
    private String modeOfTransport; // Car, Bus, Train, Flight, Taxi
    private String destination;
    private double distance; // in kilometers

    public TravelExpense(String userId, double amount, String description, String modeOfTransport, String destination, double distance) throws ValidationException {
        super(userId, "Travel", amount, description);
        this.modeOfTransport = modeOfTransport;
        this.destination = destination;
        this.distance = distance;
        validate();
    }

    public TravelExpense(String expenseId, String userId, double amount, String description, java.time.LocalDateTime dateTime, String modeOfTransport, String destination, double distance) throws ValidationException {
        super(expenseId, userId, "Travel", amount, dateTime, description);
        this.modeOfTransport = modeOfTransport;
        this.destination = destination;
        this.distance = distance;
        validate();
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (modeOfTransport == null || modeOfTransport.trim().isEmpty()) {
            throw new ValidationException("Mode of transport cannot be empty for travel expenses");
        }
        String[] validModes = {"Car", "Bus", "Train", "Flight", "Taxi"};
        boolean isValid = false;
        for (String mode : validModes) {
            if (mode.equalsIgnoreCase(modeOfTransport)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new ValidationException("Mode of transport must be one of: Car, Bus, Train, Flight, Taxi");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new ValidationException("Destination cannot be empty");
        }
        if (distance < 0) {
            throw new ValidationException("Distance cannot be negative");
        }
        if (distance > 50000) {
            throw new ValidationException("Distance cannot exceed 50,000 km");
        }
    }

    @Override
    public String toFormattedString() {
        return super.toFormattedString() + String.format(" | Transport: %s | Destination: %s | Distance: %.2f km", 
                modeOfTransport, destination, distance);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + modeOfTransport + "," + destination + "," + distance;
    }

    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(String modeOfTransport) throws ValidationException {
        this.modeOfTransport = modeOfTransport;
        validate();
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) throws ValidationException {
        this.destination = destination;
        validate();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) throws ValidationException {
        this.distance = distance;
        validate();
    }
}
