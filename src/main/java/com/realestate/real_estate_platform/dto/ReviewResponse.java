package com.realestate.real_estate_platform.dto;

import com.realestate.real_estate_platform.entity.Review;
import com.realestate.real_estate_platform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ReviewResponse {
    private int userId;
    private String reviewerName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .userId(Math.toIntExact(review.getUser().getId()))
                .reviewerName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

