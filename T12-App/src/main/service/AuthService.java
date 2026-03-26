package main.service;

import main.model.User;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final Map<String, User> users = new HashMap<>();

    public AuthService() {
        seedUsers();
    }

    private void seedUsers() {
        users.put("customer@ipos.com",
                new User("customer@ipos.com", "Test123!", "CUSTOMER", true, "Test Customer"));

        users.put("admin@ipos.com",
                new User("admin@ipos.com", "Admin123!", "ADMIN", false, "System Admin"));
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        String cleanEmail = email.trim().toLowerCase();
        User user = users.get(cleanEmail);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    public boolean changePassword(User user, String newPassword) {
        if (user == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        if (newPassword.length() < 6) {
            return false;
        }

        user.setPassword(newPassword);
        user.setFirstLogin(false);
        return true;
    }

    public String registerNonCommercialMember(String fullName, String email) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }

        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        String cleanEmail = email.trim().toLowerCase();

        if (users.containsKey(cleanEmail)) {
            return null;
        }

        String tempPassword = "Temp123!";
        User newUser = new User(cleanEmail, tempPassword, "CUSTOMER", true, fullName.trim());
        users.put(cleanEmail, newUser);

        return tempPassword;
    }

    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return users.containsKey(email.trim().toLowerCase());
    }
}