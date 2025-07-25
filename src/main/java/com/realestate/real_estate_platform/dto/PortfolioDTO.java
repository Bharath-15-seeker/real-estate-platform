package com.realestate.real_estate_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioDTO {
    private String title;
    private String description;
    private String category;

    private boolean isPublic;
}

