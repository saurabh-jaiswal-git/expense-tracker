package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Mock login: always return a dummy token if email and password are non-empty
        if (loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()
                && loginRequest.getPassword() != null && !loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.ok(Map.of("token", "dummy-token-for-dev"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
} 