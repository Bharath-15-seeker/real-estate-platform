package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.ReviewRequest;
import com.realestate.real_estate_platform.dto.ReviewResponse;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.Review;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.ReviewRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final PropertyRepository propertyRepo;
    private final UserRepository userRepo;

    public void addReview(ReviewRequest request, String userEmail) {
        Property property = propertyRepo.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User reviewer = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setCreatedAt(LocalDateTime.now());
        review.setProperty(property);
        review.setUser(reviewer);

        reviewRepo.save(review);
    }

    public List<ReviewResponse> getReviewsForProperty(Long propertyId) {
        return reviewRepo.findByPropertyId(propertyId)
                .stream()
                .map(review -> new ReviewResponse(
                        review.getUser().getName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
