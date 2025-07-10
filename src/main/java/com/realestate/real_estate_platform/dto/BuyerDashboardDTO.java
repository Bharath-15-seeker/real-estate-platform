package com.realestate.real_estate_platform.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BuyerDashboardDTO {
    private List<ContactDTO> contactedProperties;
    private List<PropertyDTO> favoriteProperties;
    private List<ReviewResponse> reviews;
}

