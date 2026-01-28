package com.lumenmall.backend.repository;

import com.lumenmall.backend.model.Review; // <--- MAKE SURE THIS IS CORRECT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(String productId);
}