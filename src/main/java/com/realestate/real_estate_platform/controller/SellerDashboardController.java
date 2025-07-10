package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.SellerDashboardDTO;
import com.realestate.real_estate_platform.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/dashboard")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final SellerDashboardService sellerDashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<SellerDashboardDTO> getDashboard(Authentication authentication) {
        String sellerEmail = authentication.getName();
        return ResponseEntity.ok(sellerDashboardService.getDashboard(sellerEmail));
    }
}

