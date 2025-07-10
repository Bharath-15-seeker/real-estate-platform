package com.realestate.real_estate_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BuilderProjectResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
}

