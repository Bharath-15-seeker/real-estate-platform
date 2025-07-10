package com.realestate.real_estate_platform.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BuilderProfileRequest {
    private String name;
    private String email;
    private String phone;
    private String experience;
    private String description;
    private String specialization;
}

