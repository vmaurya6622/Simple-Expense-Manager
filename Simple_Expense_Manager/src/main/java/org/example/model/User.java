package org.example.model;

import org.example.interfaces.Validatable;
import org.example.exception.ValidationException;

import java.util.Objects;

public class User implements Validatable {
    private String name;
    private String username;
    private String password;
    private String userId;
    private String csvId; // Unique CSV file identifier

    public User(String name, String username, String password, String csvId) throws ValidationException {
        this.name = name;
        this.username = username;
        this.password = password;
        this.userId = generateUserId(username);
        this.csvId = csvId;
        validate();
    }

    public User(String name, String username, String password, String userId, String csvId) throws ValidationException {
        this.name = name;
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.csvId = csvId;
        validate();
    }

    private String generateUserId(String username) {
        return username.toLowerCase().replaceAll("\\s+", "_") + "_" + System.currentTimeMillis();
    }

    @Override
    public void validate() throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
        if (name.length() < 3) {
            throw new ValidationException("Name must be at least 3 characters long");
        }
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new ValidationException("Name can only contain letters and spaces");
        }
        
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (username.length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        if (password.length() < 4) {
            throw new ValidationException("Password must be at least 4 characters long");
        }
        
        if (csvId == null || csvId.trim().isEmpty()) {
            throw new ValidationException("CSV ID cannot be empty");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        this.name = name;
        validate();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws ValidationException {
        this.username = username;
        validate();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws ValidationException {
        this.password = password;
        validate();
    }

    public String getUserId() {
        return userId;
    }

    public String getCsvId() {
        return csvId;
    }

    public void setCsvId(String csvId) {
        this.csvId = csvId;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) || Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", csvId='" + csvId + '\'' +
                '}';
    }
}
