package com.lumenmall.backend.service;

import com.lumenmall.backend.model.User;
import com.lumenmall.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserRole(Long id, String newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setRole(newRole);
        return userRepository.save(user);
    }
    public User updateUserName(String email, String newName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(newName);
        return userRepository.save(user);
    }

    public User updateFullProfile(String currentEmail, String newName, String newEmail, String newImage) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(newName);
        user.setEmail(newEmail);
        user.setImageUrl(newImage);

        return userRepository.save(user);
    }

    public boolean verifyToken(String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            user.setVerificationToken(null); // Clear the token so it can't be used twice
            userRepository.save(user);
            return true;
        }
        return false;
    }
}