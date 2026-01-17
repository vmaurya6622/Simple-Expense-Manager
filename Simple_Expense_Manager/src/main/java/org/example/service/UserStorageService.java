package org.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.model.User;
import org.example.exception.DuplicateUserException;
import org.example.exception.UserNotFoundException;
import org.example.exception.ValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserStorageService {
    private static final String USERS_JSON_FILE = "users.json";
    private Gson gson;

    public UserStorageService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private List<User> loadUsers() {
        try (FileReader reader = new FileReader(USERS_JSON_FILE)) {
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> users = gson.fromJson(reader, listType);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) throws IOException {
        try (FileWriter writer = new FileWriter(USERS_JSON_FILE)) {
            gson.toJson(users, writer);
        }
    }

    public void register(User user) throws DuplicateUserException, IOException, ValidationException {
        List<User> users = loadUsers();
        
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        
        if (exists) {
            throw new DuplicateUserException("Username '" + user.getUsername() + "' already exists");
        }
        
        // Generate unique CSV ID
        String csvId = "CSV_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        User newUser = new User(user.getName(), user.getUsername(), user.getPassword(), user.getUserId(), csvId);
        
        users.add(newUser);
        saveUsers(users);
    }

    public User login(String username, String password) throws UserNotFoundException, IOException {
        List<User> users = loadUsers();
        
        User user = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        
        if (!user.checkPassword(password)) {
            throw new UserNotFoundException("Invalid password");
        }
        
        return user;
    }

    public User getUserByCsvId(String csvId) throws UserNotFoundException, IOException {
        List<User> users = loadUsers();
        
        return users.stream()
                .filter(u -> u.getCsvId().equals(csvId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with CSV ID '" + csvId + "' not found"));
    }

    public boolean userExists(String username) throws IOException {
        List<User> users = loadUsers();
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }
}
