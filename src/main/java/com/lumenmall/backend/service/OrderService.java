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

    @Transactional
    public Order placeOrder(Order order) {
        // Logic to check stock/inventory [cite: 2026-01-25]
        deductStock(order);

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

    @Transactional
    public void markOrderAsPaid(String providerOrderId) {
        Order order = orderRepository.findByProviderOrderId(providerOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found with provider ID: " + providerOrderId));

        order.setStatus("PAID");
        orderRepository.save(order);
    }
    private void deductStock(Order order) {
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found ID: " + item.getProductId()));

                if (product.getStock() != null) {
                    int updatedStock = product.getStock() - item.getQuantity();
                    product.setStock(Math.max(updatedStock, 0)); // Prevent negative stock
                    productRepository.save(product);
                }
            });
        }
    }


    public void processOrderAfterPayment(String orderId) {

        this.markOrderAsPaid(orderId);
        System.out.println("Inventory check placeholder triggered for Order: " + orderId);
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