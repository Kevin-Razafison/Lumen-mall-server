package com.lumenmall.backend.repository;

import com.lumenmall.backend.model.ReviewHelpfulVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewHelpfulVoteRepository extends JpaRepository<ReviewHelpfulVote, Long> {

    boolean existsByReviewIdAndUserEmail(Long reviewId, String userEmail);

    @Transactional
    @Modifying
    void deleteByReviewIdAndUserEmail(Long reviewId, String userEmail);
}