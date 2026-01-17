package org.example.repository;

import org.example.model.User;
import org.example.exception.DuplicateUserException;
import org.example.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
    }

    public void register(User user) throws DuplicateUserException {
        boolean exists = users.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(user.getName()));
        
        if (exists) {
            throw new DuplicateUserException("User with name '" + user.getName() + "' already exists");
        }
        
        users.add(user);
    }

    public User login(String name) throws UserNotFoundException {
        Optional<User> user = users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst();
        
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with name '" + name + "' not found. Please register first.");
        }
        
        return user.get();
    }

    public boolean userExists(String name) {
        return users.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(name));
    }

    public User getUserById(String userId) throws UserNotFoundException {
        Optional<User> user = users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst();
        
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with ID '" + userId + "' not found");
        }
        
        return user.get();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
