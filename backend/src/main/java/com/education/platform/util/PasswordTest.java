package com.education.platform.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // The password we're trying
        String rawPassword = "admin123";

        // The hash in the database
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBa3BjR0v9bHri";

        // Test if it matches
        boolean matches = encoder.matches(rawPassword, encodedPassword);

        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        System.out.println("Matches: " + matches);

        // Generate a new hash to see what it looks like
        String newHash = encoder.encode(rawPassword);
        System.out.println("New hash: " + newHash);
    }
}
