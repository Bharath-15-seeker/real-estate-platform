package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.service.PropertyImageService;
import com.realestate.real_estate_platform.service.PropertyService;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyImageService imageService;
    private final PropertyService propertyService;
    private final UserRepository userRepository;

    // 🔼 Post a new property (only for authenticated users)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createProperty(
            @RequestPart("property") Property property,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        property.setOwner(user);
        propertyService.createProperty(property, images);
        return "Property posted successfully";
    }


    // to filter the property by type RENT,SALE
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Property>> getByType(@PathVariable PropertyType type) {
        return ResponseEntity.ok(propertyService.getByType(type));
    }

    //to filter the property by location
    @GetMapping("/searc")
    public ResponseEntity<List<Property>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(propertyService.getByLocation(location));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PropertyDTO>> searchProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer bhk,
            @RequestParam(required = false) String facing,
            @RequestParam(required = false) String prop_type

    ) {
        List<PropertyDTO> results = propertyService.searchProperties(location, type, minPrice, maxPrice,bhk,facing, prop_type);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Property>> searchbyid(@PathVariable Long id)
    {
        return ResponseEntity.ok(propertyService.getbypropertyId(id));
    }


    // PropertyController.java
    @GetMapping("/my-properties")

    public ResponseEntity<List<Property>> getMyProperties(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(propertyService.getPropertiesByOwner(email));
    }

    @PostMapping("/{propertyId}/images")
    public ResponseEntity<String> uploadImage(@PathVariable Long propertyId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String message = imageService.uploadImage(propertyId, file);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long propertyId,
                                                   @RequestBody Property updatedProperty,
                                                   Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(propertyService.updateProperty(propertyId, updatedProperty, email));
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

    @DeleteMapping("/{propertyId}")

    public ResponseEntity<String> deleteProperty(@PathVariable Long propertyId,
                                                 Authentication authentication) {
        String email = authentication.getName();
        propertyService.deleteProperty(propertyId, email);
        return ResponseEntity.ok("Property deleted successfully");
    }


}
