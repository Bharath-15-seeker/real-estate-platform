package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.BuyerDashboardDTO;
import com.realestate.real_estate_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<BuyerDashboardDTO> getBuyerDashboard(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getDashboardData(email));
    }

}
