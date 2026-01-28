package com.lumenmall.backend.controller;

import com.lumenmall.backend.model.User;
import com.lumenmall.backend.repository.UserRepository;
import com.lumenmall.backend.service.EmailService;
import com.lumenmall.backend.service.UserService;
import com.lumenmall.backend.security.JwtUtils; // Ensure this matches your package
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private EmailService emailService;

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
                    // 1. CHECK IF VERIFIED (THE BOUNCER)
                    if (!user.isEnabled()) {
                        return ResponseEntity.status(401)
                                .body(Map.of("message", "Please verify your email first!"));
                    }

                    // 2. CHECK PASSWORD
                    if (passwordEncoder.matches(password, user.getPassword())) {
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
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        boolean verified = userService.verifyToken(token);
        if (verified) {
            return ResponseEntity.ok("Verified");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Token");
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use!"));
        }

        // Setup User
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setEnabled(false); // New: account starts as inactive

        // Generate Token
        String token = java.util.UUID.randomUUID().toString();
        user.setVerificationToken(token);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);

        return ResponseEntity.ok(Map.of("message", "Registration successful! Please check your email."));
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return userRepository.findByEmail(email).map(user -> {
            if (user.isEnabled()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Account is already verified."));
            }

            // Use existing token or generate a fresh one
            String token = user.getVerificationToken();
            if (token == null) {
                token = java.util.UUID.randomUUID().toString();
                user.setVerificationToken(token);
                userRepository.save(user);
            }

            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);
            return ResponseEntity.ok(Map.of("message", "Verification email resent!"));
        }).orElse(ResponseEntity.status(404).body(Map.of("message", "User not found.")));
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