package com.lumenmall.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "review_helpful_votes")
@Data
public class ReviewHelpfulVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reviewId;
    private String userEmail;

    // Default constructor for Hibernate
    public ReviewHelpfulVote() {}

    // Convenience constructor
    public ReviewHelpfulVote(Long reviewId, String userEmail) {
        this.reviewId = reviewId;
        this.userEmail = userEmail;
    }
}