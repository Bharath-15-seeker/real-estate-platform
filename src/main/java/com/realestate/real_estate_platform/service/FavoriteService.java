package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.entity.Favorite;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.mapper.PropertyMapper;
import com.realestate.real_estate_platform.repositories.FavoriteRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepo;
    private final UserRepository userRepo;
    private final PropertyRepository propertyRepo;

    public void addToFavorites(String userEmail, Long propertyId) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Prevent duplicates
        Optional<Favorite> existing = favoriteRepo.findByUserAndProperty(user, property);
        if (existing.isPresent()) {
            throw new RuntimeException("Property is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        favoriteRepo.save(favorite);
    }


    @Transactional
    public void removeFromFavorites(String userEmail, Long propertyId) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Property property = propertyRepo.findById(propertyId).orElseThrow();
        favoriteRepo.deleteByUserAndProperty(user, property);
    }

    public List<PropertyDTO> getFavorites(String userEmail) {
        return favoriteRepo.findByUserEmail(userEmail).stream()
                .map(fav -> PropertyMapper.toDTO(fav.getProperty()))
                .toList();
    }
}
