package com.lumenmall.backend.repository;

import com.lumenmall.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmail(String email);
    Optional<Order> findByProviderOrderId(String providerOrderId);
}

