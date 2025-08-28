package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.AuthResponse;
import com.realestate.real_estate_platform.dto.LoginRequest;
import com.realestate.real_estate_platform.dto.RegisterRequest;

import com.realestate.real_estate_platform.entity.Role;
import com.realestate.real_estate_platform.entity.User;

import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;



    public AuthResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.USER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepo.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }


    public AuthResponse login(LoginRequest request) {
        User user = (User) userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String jwt = jwtService.generateToken(user); // no cast needed
        return new AuthResponse(jwt);
    }
}
