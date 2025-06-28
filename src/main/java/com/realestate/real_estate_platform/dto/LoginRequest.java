package com.realestate.real_estate_platform.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

