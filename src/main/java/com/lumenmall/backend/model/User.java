package com.lumenmall.backend.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @Column(nullable = false)
    private String role = "ROLE_USER"; // Default role for everyone

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

}