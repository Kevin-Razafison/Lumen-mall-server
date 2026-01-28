package com.lumenmall.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    @Column(length = 500)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private Integer quantity;
    private Double price;
}