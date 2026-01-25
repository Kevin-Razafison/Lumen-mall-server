package com.lumenmall.backend.service;

import com.lumenmall.backend.model.Order;
import com.lumenmall.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order placeOrder(Order order) {
        if ("Bank Transfer".equals(order.getPaymentMethod())) {
            order.setStatus("AWAITING_PAYMENT");
        } else if (order.getStatus() == null) {
            order.setStatus("PAID");
        }


        return orderRepository.save(order);
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
}