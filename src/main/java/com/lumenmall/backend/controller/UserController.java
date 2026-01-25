package com.lumenmall.backend.controller;

import com.lumenmall.backend.model.User;
import com.lumenmall.backend.repository.UserRepository;
import com.lumenmall.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use!"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        // 1. Find user by email
        return userRepository.findByEmail(email)
                .map(user -> {
                    // 2. Compare plain password with hashed password in DB
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        // Success! Return user info (except the password)
                        return ResponseEntity.ok(Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "fullName", user.getFullName(),
                                "role", user.getRole()
                        ));
                    } else {
                        return ResponseEntity.status(401).body(Map.of("message", "Invalid password"));
                    }
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "User not found")));
    }
}