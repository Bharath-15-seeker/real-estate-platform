package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.service.PropertyImageService;
import com.realestate.real_estate_platform.service.PropertyService;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyImageService imageService;
    private final PropertyService propertyService;
    private final UserRepository userRepository;

    // 🔼 Post a new property (only for authenticated users)
    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        property.setOwner(user);
        return ResponseEntity.ok(propertyService.createProperty(property));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Property>> getByType(@PathVariable PropertyType type) {
        return ResponseEntity.ok(propertyService.getByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(propertyService.getByLocation(location));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Property>> searchbyid(@PathVariable Long id)
    {
        return ResponseEntity.ok(propertyService.getbypropertyId(id));
    }


    @PostMapping("/{propertyId}/images")
    public ResponseEntity<String> uploadImage(@PathVariable Long propertyId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String message = imageService.uploadImage(propertyId, file);
        return ResponseEntity.ok(message);
    }

    // 👀 Get all properties
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // 👤 Get properties by logged-in user
    @GetMapping("/my")
    public ResponseEntity<List<Property>> getMyProperties(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(propertyService.getPropertiesByUser(user.getId()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
