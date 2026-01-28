package com.lumenmall.backend.controller;

import com.lumenmall.backend.model.Order;
import com.lumenmall.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderService.placeOrder(order);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            System.err.println("CRITICAL ORDER ERROR: " + e.getMessage());

            return ResponseEntity.status(400).body(java.util.Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody String newStatus) {
        Order updatedOrder = orderService.updateStatus(id, newStatus.replace("\"", ""));
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<Order>> getOrdersByEmail(@PathVariable String email) {
        // THIS WILL SHOW IN YOUR IDE CONSOLE IMMEDIATELY
        System.out.println(">>> RECEIVED REQUEST FOR EMAIL: " + email);

        List<Order> userOrders = orderService.getOrdersByCustomerEmail(email);

        System.out.println(">>> DATABASE FOUND " + userOrders.size() + " ORDERS");
        return ResponseEntity.ok(userOrders);
    }
}