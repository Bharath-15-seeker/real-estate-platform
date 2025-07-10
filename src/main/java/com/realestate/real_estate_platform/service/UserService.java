package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.BuyerDashboardDTO;
import com.realestate.real_estate_platform.dto.ContactDTO;
import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.dto.ReviewResponse;
import com.realestate.real_estate_platform.dto.UserDTO;
import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Favorite;
import com.realestate.real_estate_platform.entity.Review;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.FavoriteRepository;
import com.realestate.real_estate_platform.repositories.ReviewRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final ContactRepository contactRepo;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    public BuyerDashboardDTO getDashboardData(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = contactRepo.findByEmail(email);
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        List<Review> reviews = reviewRepository.findByUser(user);

        return BuyerDashboardDTO.builder()
                .contactedProperties(contacts.stream().map(ContactDTO::from).toList())
                .favoriteProperties(favorites.stream().map(fav -> PropertyDTO.from(fav.getProperty())).toList())
                .reviews(reviews.stream().map(ReviewResponse::from).toList())
                .build();
    }


}
