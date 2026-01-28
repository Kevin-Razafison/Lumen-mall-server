package com.lumenmall.backend.service;

import com.lumenmall.backend.model.Review;
import com.lumenmall.backend.model.Order;
import com.lumenmall.backend.repository.ReviewRepository;
import com.lumenmall.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository; // 2. Inject it here

    public List<Review> getAllReviews() {
        return reviewRepository.findAll(); // 3. Now this will work!
    }
    @Autowired
    private OrderRepository orderRepository; // Injected to check purchase history

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new RuntimeException("Review not found with id: " + id);
        }
    }


    public Long getValidOrderIdForReview(String email, Long productId) {
        // Find all orders for this customer
        List<Order> userOrders = orderRepository.findByCustomerEmail(email);

        for (Order order : userOrders) {
            // Check if any item in the order matches the productId
            boolean purchased = order.getItems().stream()
                    .anyMatch(item -> item.getProductId().equals(productId));

            if (purchased) {
                return order.getId(); // Return the first matching order ID
            }
        }
        return null; // No purchase found
    }
}