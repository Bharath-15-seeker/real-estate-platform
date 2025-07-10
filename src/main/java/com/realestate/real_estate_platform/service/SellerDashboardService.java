package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.*;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.ReviewRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerDashboardService {

    private final UserRepository userRepo;
    private final PropertyRepository propertyRepo;
    private final ContactRepository contactRepo;
    private final ReviewRepository reviewRepo;

    public SellerDashboardDTO getDashboard(String sellerEmail) {
        User seller = userRepo.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        var properties = propertyRepo.findByOwner(seller)
                .stream()
                .map(PropertyDTO::from)
                .collect(Collectors.toList());

        var contacts = contactRepo.findByPropertyIn(seller.getProperties())
                .stream()
                .map(ContactDTO::from)
                .collect(Collectors.toList());

        var reviews = reviewRepo.findByPropertyIn(seller.getProperties())
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());

        return SellerDashboardDTO.builder()
                .properties(properties)
                .contactRequests(contacts)
                .reviews((List<ReviewResponse>) reviews)
                .build();
    }
}
