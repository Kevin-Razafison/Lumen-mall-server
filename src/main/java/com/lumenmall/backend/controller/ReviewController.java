package com.lumenmall.backend.controller;

import com.lumenmall.backend.model.Review; // Absolute path to your model
import com.lumenmall.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        // This will work now because the Review type matches the Repository generic
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewRepository.findByProductId(productId));
    }
}