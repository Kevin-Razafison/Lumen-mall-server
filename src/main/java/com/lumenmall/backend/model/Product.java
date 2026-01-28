package com.lumenmall.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;
    private String category;

    @ElementCollection
    @Column(name = "feature")
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> features;


    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private Integer stock;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private Double salePrice;
}