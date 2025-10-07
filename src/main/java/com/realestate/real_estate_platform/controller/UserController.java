package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.BuyerDashboardDTO;
import com.realestate.real_estate_platform.dto.UserDTO;
import com.realestate.real_estate_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile") // ⚡️ Matches the path used in the frontend script
    @PreAuthorize("isAuthenticated()") // Only requires a valid token
    public ResponseEntity<UserDTO> getUserProfile() {
        // 1. Get the authenticated username (email) from the Spring Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // This is typically the email/username

        // 2. Fetch the full UserDTO from the service layer
        UserDTO userProfile = userService.getUserByUsername(username);

        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }

        // The returned UserDTO must contain the role property (e.g., "role": "ADMIN")
        return ResponseEntity.ok(userProfile);
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BuyerDashboardDTO> getBuyerDashboard(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getDashboardData(email));
    }

}
