package com.education.platform.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest2 {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin123";

        // Generate a new hash
        String newHash = encoder.encode(rawPassword);
        System.out.println("Generated hash: " + newHash);

        // Test if it matches
        boolean matches = encoder.matches(rawPassword, newHash);
        System.out.println("Matches itself: " + matches);

        // Test with the hash from database
        String dbHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBa3BjR0v9bHri";
        boolean dbMatches = encoder.matches(rawPassword, dbHash);
        System.out.println("Matches database hash: " + dbMatches);

        // Also test with the hash from AuthController comment
        System.out.println("\nDatabase hash: " + dbHash);
        System.out.println("Length: " + dbHash.length());
    }
}
