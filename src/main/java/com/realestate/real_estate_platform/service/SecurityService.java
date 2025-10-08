package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository; // Inject your UserRepository

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the Long database ID of the currently authenticated user.
     * Throws an exception if the user is not found or not authenticated.
     */
    public Long getCurrentUserId() {
        // 1. Get the Authentication object from the Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Basic check for authentication
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("User is not authenticated.");
        }

        // 2. Get the principal identifier (usually the email in a JWT setup)
        String userEmail = authentication.getName();

        // 3. Look up the User entity by email to get the Long ID
        // Note: This relies on your UserRepository having a findByEmail method
        // and your User entity having a getId() method.
        return userRepository.findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }
}