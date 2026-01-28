package com.lumenmall.backend.service;

import com.lumenmall.backend.model.Review;
import com.lumenmall.backend.model.Order;
import com.lumenmall.backend.model.ReviewHelpfulVote;
import com.lumenmall.backend.repository.ProductRepository;
import com.lumenmall.backend.repository.ReviewHelpfulVoteRepository;
import com.lumenmall.backend.repository.ReviewRepository;
import com.lumenmall.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewHelpfulVoteRepository voteRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        for (Review review : reviews) {
            if (review.getProductId() != null) {
                productRepository.findById(Long.parseLong(review.getProductId()))
                        .ifPresent(product -> review.setProductName(product.getName()));
            }
        }
        return reviews;
    }

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

    public Review addReply(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setAdminReply(reply);
        return reviewRepository.save(review);
    }

    // TOGGLE LOGIC: This single method handles both Like and Unlike
    public Review markHelpful(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        boolean alreadyVoted = voteRepository.existsByReviewIdAndUserEmail(reviewId, userEmail);

        if (alreadyVoted) {
            // If they already voted, delete the record and subtract 1
            voteRepository.deleteByReviewIdAndUserEmail(reviewId, userEmail);
            review.setHelpfulCount(Math.max(0, review.getHelpfulCount() - 1));
        } else {
            // If they haven't voted, create the record and add 1
            voteRepository.save(new ReviewHelpfulVote(reviewId, userEmail));
            review.setHelpfulCount(review.getHelpfulCount() + 1);
        }

        return reviewRepository.save(review);
    }

    public Long getValidOrderIdForReview(String email, Long productId) {
        List<Order> userOrders = orderRepository.findByCustomerEmail(email);
        for (Order order : userOrders) {
            boolean purchased = order.getItems().stream()
                    .anyMatch(item -> item.getProductId().equals(productId));
            if (purchased) {
                return order.getId();
            }
        }
        return null;
    }
}