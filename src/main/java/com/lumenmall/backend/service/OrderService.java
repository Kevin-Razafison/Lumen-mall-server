package com.lumenmall.backend.service;

import com.lumenmall.backend.model.Product;
import com.lumenmall.backend.model.Order;
import com.lumenmall.backend.repository.OrderRepository;
import com.lumenmall.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Order placeOrder(Order order) {
        // 1. VALIDATE: Look before you leap
        validateStock(order);

        // 2. ACT: Deduct only after validation passes
        deductStock(order);

        // 3. INITIAL STATUS MAPPING
        if ("Bank Transfer".equals(order.getPaymentMethod())) {
            order.setStatus("AWAITING_PAYMENT");
        } else if ("PayPal".equals(order.getPaymentMethod())) {
            order.setStatus("PENDING_PAYMENT");
        } else if ("Credit Card".equals(order.getPaymentMethod())) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PROCESSING");
        }

        return orderRepository.save(order);
    }

    private void validateStock(Order order) {
        if (order.getItems() == null) return;

        for (var item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found ID: " + item.getProductId()));

            if (product.getStock() != null && product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName() +
                        " (Requested: " + item.getQuantity() + ", Available: " + product.getStock() + ")");
            }
        }
    }

    private void deductStock(Order order) {
        if (order.getItems() == null) return;

        order.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // MAP DATA HERE: Transfer name and image from Product to OrderItem
            item.setProductName(product.getName());
            item.setImageUrl(product.getImageUrl());

            // Handle stock deduction
            int updatedStock = product.getStock() - item.getQuantity();
            product.setStock(updatedStock);
            productRepository.save(product);

            // Low Stock Alert
            if (updatedStock <= 5 && updatedStock > 0) {
                try {
                    emailService.sendLowStockAlert(product.getName(), updatedStock);
                } catch (Exception e) {
                    System.err.println("Failed to send stock alert: " + e.getMessage());
                }
            }
        });
    }

    @Transactional
    public void markOrderAsPaid(String providerOrderId) {
        Order order = orderRepository.findByProviderOrderId(providerOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found with provider ID: " + providerOrderId));

        order.setStatus("PAID");
        orderRepository.save(order);
    }

    public void processOrderAfterPayment(String orderId) {
        this.markOrderAsPaid(orderId);
        System.out.println("Payment processed and order status updated for: " + orderId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }
}