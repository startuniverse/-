package com.education.platform.util;

import java.lang.reflect.Method;
import java.util.Base64;

public class SimplePasswordTest {
    public static void main(String[] args) throws Exception {
        // Try to manually verify BCrypt
        String rawPassword = "admin123";
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBa3BjR0v9bHri";

        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        System.out.println("Hash format check: " + (encodedPassword.startsWith("$2a$10$") ? "Valid BCrypt" : "Invalid format"));

        // Try to load BCryptPasswordEncoder dynamically
        try {
            Class<?> encoderClass = Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
            Object encoder = encoderClass.getDeclaredConstructor().newInstance();
            Method matchesMethod = encoderClass.getMethod("matches", String.class, String.class);
            Method encodeMethod = encoderClass.getMethod("encode", String.class);

            boolean matches = (boolean) matchesMethod.invoke(encoder, rawPassword, encodedPassword);
            System.out.println("Matches: " + matches);

            String newHash = (String) encodeMethod.invoke(encoder, rawPassword);
            System.out.println("New hash: " + newHash);
        } catch (Exception e) {
            System.out.println("Could not use BCryptPasswordEncoder: " + e.getMessage());
            System.out.println("Trying alternative approach...");

            // Check if the hash contains any unusual characters
            for (int i = 0; i < encodedPassword.length(); i++) {
                char c = encodedPassword.charAt(i);
                if (c < 32 || c > 126) {
                    System.out.println("Found non-printable character at index " + i + ": " + (int)c);
                }
            }
        }
    }
}
