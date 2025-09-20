package com.realestate.real_estate_platform.dto;

import java.util.List;

public record PortfolioReviewsResponse(List<ReviewResponse> reviews,
                                       double averageRating) {
}
