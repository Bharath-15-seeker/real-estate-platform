package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.PortfolioReviewsResponse;
import com.realestate.real_estate_platform.dto.ReviewRequest;
import com.realestate.real_estate_platform.dto.ReviewResponse;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.Review;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.ReviewRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class ReviewService {

    private final SecurityService securityService;

    public ReviewService(ReviewRepository reviewRepo, SecurityService securityService, PropertyRepository propertyRepo, PortfolioRepository portfolioRepo, UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.securityService = securityService;
        this.propertyRepo = propertyRepo;
        this.portfolioRepo = portfolioRepo;
        this.userRepo = userRepo;
    }
    private final ReviewRepository reviewRepo;
    private final PropertyRepository propertyRepo;
    private final PortfolioRepository portfolioRepo;
    private final UserRepository userRepo;


    public void addReview(ReviewRequest request, String userEmail) {
        Portfolio portfolio = portfolioRepo.findById(request.getPortfolioId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User reviewer = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setCreatedAt(LocalDateTime.now());
        review.setPortfolio(portfolio);
        review.setUser(reviewer);

        reviewRepo.save(review);
    }

    public PortfolioReviewsResponse getnReviewsForProperty(Long portfolioId) {
        List<ReviewResponse> reviewResponses = reviewRepo.findByPortfolioId(portfolioId)
                .stream()
                .map(review -> new ReviewResponse(
                        Math.toIntExact(review.getUser().getId()),
                        review.getUser().getName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()))
                .collect(Collectors.toList());

        Double averageRating = reviewRepo.findAverageRatingByPortfolioId(portfolioId);
        if (averageRating == null) averageRating = 0.0;

        return new PortfolioReviewsResponse(reviewResponses, averageRating);
    }


    public List<ReviewResponse> getReviewsForProperty(Long portfolioId) {
        return reviewRepo.findByPortfolioId(portfolioId)
                .stream()
                .map(review -> new ReviewResponse(
                        Math.toIntExact(review.getUser().getId()),
                        review.getUser().getName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional // Ensure the deletion happens within a transaction
    public void deleteReview(Long reviewId) {
        // 1. Check if the review exists
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));

        // 2. Get the currently authenticated user's ID
        // (Assuming securityService.getCurrentUserId() returns the database ID, e.g., 123)
        Long currentUserId = securityService.getCurrentUserId();

        // 3. Ownership Validation (CRITICAL SECURITY STEP)
        // Assuming your Review entity has a field 'userId' that stores the creator's ID
        if (!review.getUser().getId().equals(currentUserId)) { // 🛑 FIX IS HERE
            throw new AccessDeniedException("You do not have permission to delete this review.");
        }

        // 4. Perform the deletion
        reviewRepo.delete(review);
    }

}

class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}