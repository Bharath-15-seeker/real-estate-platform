package com.realestate.real_estate_platform.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long propertyId;
    private int rating;
    private String comment;
}

