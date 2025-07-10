package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.ReviewRequest;
import com.realestate.real_estate_platform.dto.ReviewResponse;
import com.realestate.real_estate_platform.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody ReviewRequest request, Authentication auth) {
        reviewService.addReview(request, auth.getName());
        return ResponseEntity.ok("Review added successfully");
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getReviewsForProperty(propertyId));
    }
}
