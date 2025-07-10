package com.realestate.real_estate_platform.dto;



import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SellerDashboardDTO {
    private List<PropertyDTO> properties;
    private List<ContactDTO> contactRequests;
    private List<ReviewResponse> reviews;
}

