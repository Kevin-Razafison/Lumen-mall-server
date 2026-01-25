package com.lumenmall.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private String paymentMethod;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }

}