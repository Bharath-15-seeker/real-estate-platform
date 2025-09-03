package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.entity.Favorite;
import com.realestate.real_estate_platform.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/portfolio/{id}")
    public ResponseEntity<String> addportfavorite(@PathVariable Long id, Authentication auth) {
        favoriteService.addportfavorite(auth.getName(), id);
        return ResponseEntity.ok("Added to favorites");
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long propertyId, Authentication auth) {
        favoriteService.addToFavorites(auth.getName(), propertyId);
        return ResponseEntity.ok("Added to favorites");
    }

    @DeleteMapping("/portfolio/{id}")
    public ResponseEntity<String> removeportFavorite(@PathVariable Long id, Authentication auth) {
        favoriteService.removeportFavorites(auth.getName(), id);
        return ResponseEntity.ok("Removed from favorites");
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long propertyId, Authentication auth) {
        favoriteService.removeFromFavorites(auth.getName(), propertyId);
        return ResponseEntity.ok("Removed from favorites");
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(Authentication auth) {
        return ResponseEntity.ok(favoriteService.getFavorites(auth.getName()));
    }
}

