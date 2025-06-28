package com.realestate.real_estate_platform.dto;

import com.realestate.real_estate_platform.entity.Role;
import lombok.Data;



@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
