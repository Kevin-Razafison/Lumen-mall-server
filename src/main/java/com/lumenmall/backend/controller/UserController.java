package com.lumenmall.backend.controller;

import com.lumenmall.backend.model.User;
import com.lumenmall.backend.repository.UserRepository;
import com.lumenmall.backend.service.UserService;
import com.lumenmall.backend.security.JwtUtils; // Ensure this matches your package
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

    @Autowired
    private JwtUtils jwtUtils; // Moved inside the class!

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
        user.setRole("USER");

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody String newRole) {
        try {
            String cleanedRole = newRole.replace("\"", "");
            User updatedUser = userService.updateUserRole(id, cleanedRole);
            return ResponseEntity.ok(Map.of(
                    "message", "Role updated successfully",
                    "role", updatedUser.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        // Correctly passes two arguments: email and role
                        String token = jwtUtils.generateToken(user.getEmail(), user.getRole());

                        return ResponseEntity.ok(Map.of(
                                "token", token,
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "fullName", user.getFullName(),
                                "role", user.getRole()
                        ));
                    } else {
                        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
                    }
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> data, @RequestHeader("Authorization") String auth) {
        String currentEmail = jwtUtils.extractUsername(auth.substring(7));

        String newName = data.get("fullName");
        String newEmail = data.get("email");
        String newImage = data.get("imageUrl");

        User updatedUser = userService.updateFullProfile(currentEmail, newName, newEmail, newImage);

        // FIX: Use 'updatedUser' (not 'user') and pass BOTH arguments
        String newToken = jwtUtils.generateToken(updatedUser.getEmail(), updatedUser.getRole());

        return ResponseEntity.ok(Map.of(
                "token", newToken,
                "fullName", updatedUser.getFullName(),
                "email", updatedUser.getEmail(),
                "imageUrl", updatedUser.getImageUrl() != null ? updatedUser.getImageUrl() : "",
                "role", updatedUser.getRole()
        ));
    }
}