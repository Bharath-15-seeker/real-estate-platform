package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.dto.UserDTO;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.service.AdminService;
import com.realestate.real_estate_platform.service.PortfolioService;
import com.realestate.real_estate_platform.service.PropertyService;
import com.realestate.real_estate_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PropertyService propertyService;
    private final AdminService adminService;
    private final PortfolioService portfolioService;

    @DeleteMapping("portfolios/{portfolioId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deletePortfolio(@PathVariable Long portfolioId)
    {
        portfolioService.deletePortfolioById(portfolioId);
        return ResponseEntity.ok("Portfolio deleted successfully.");
    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @DeleteMapping("/properties/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        adminService.deletePropertyById(id);
        return ResponseEntity.ok("Property deleted successfully.");
    }



    @GetMapping("/properties")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(adminService.getAllProperties());
    }
}

