package com.lumenmall.backend.controller;

import com.lumenmall.backend.model.Review;
import com.lumenmall.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.saveReview(review));
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<Review> replyToReview(@PathVariable Long id, @RequestBody String reply) {
        return ResponseEntity.ok(reviewService.addReply(id, reply));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }


    // NEW: Check if the logged-in user can leave a star rating
    @GetMapping("/can-review/{productId}")
    public ResponseEntity<Long> checkReviewEligibility(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) return ResponseEntity.ok(null);

        Long orderId = reviewService.getValidOrderIdForReview(userDetails.getUsername(), productId);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews()); // Ensure this method exists in your ReviewService
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<?> markHelpful(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String email = body.get("userEmail");
        return ResponseEntity.ok(reviewService.markHelpful(id, email));
    }
}