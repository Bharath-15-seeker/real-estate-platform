package com.realestate.real_estate_platform.service;



import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.entity.Prop_type;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;  // ✅ correct
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepo;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;


    public void createProperty(Property property, MultipartFile[] images) {
        property.setPostedAt(LocalDateTime.now());

        List<String> imagePaths = new ArrayList<>();

        if (images != null) {
            // Ensure upload directory exists
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            for (MultipartFile image : images) {
                try {
                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    File destinationFile = new File(uploadPath, filename);

                    image.transferTo(destinationFile);

                    // Store relative path (for frontend access)
                    imagePaths.add("/uploads/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        property.setImageUrls(imagePaths);
        propertyRepo.save(property);
    }


    public List<Property> getByType(PropertyType type) {
        return propertyRepo.findByType(type);
    }

    public List<Property> getByLocation(String location) {
        return propertyRepo.findByLocationContainingIgnoreCase(location);
    }

    public List<Property> getPropertiesByOwner(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        return propertyRepo.findByOwner(seller);
    }

    public Property updateProperty(Long id, Property updatedData, String sellerEmail) {
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("You are not the owner of this property");
        }

       // property.setTitle(updatedData.getTitle());
        property.setDescription(updatedData.getDescription());
        property.setLocation(updatedData.getLocation());
        property.setPrice(updatedData.getPrice());
        property.setType(updatedData.getType());
        return propertyRepo.save(property);
    }


    public List<Property> getAllProperties() {
        return propertyRepo.findAll();
    }

//    public List<PropertyDTO> searchProperties(String location, String type, Double minPrice, Double maxPrice) {
//        List<Property> properties = propertyRepo.search(location, type, minPrice, maxPrice);
//        return properties.stream().map(PropertyDTO::from).collect(Collectors.toList());
//    }

    public List<PropertyDTO> searchProperties(
            String location, String title, String typeStr,
            Double minPrice, Double maxPrice, Integer bhk,
            String facing, String proptype,
            Double lat, Double lng, Double radiusKm
    ) {
        PropertyType type = null;
        if (typeStr != null && !typeStr.isBlank()) {
            try {
                type = PropertyType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid property type: " + typeStr);
            }
        }

        Prop_type prop_type = null;
        if (proptype != null && !proptype.isBlank()) {
            try {
                prop_type = Prop_type.valueOf(proptype.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid property type: " + proptype);
            }
        }

        List<Property> properties = propertyRepo.search(
                location, title, type, minPrice, maxPrice, bhk, facing, prop_type
        );

        // ✅ If lat/lng provided, filter properties by distance
        if (lat != null && lng != null && radiusKm != null) {
            properties = properties.stream()
                    .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                    .filter(p -> {
                        double dist = distanceKm(lat, lng, p.getLatitude(), p.getLongitude());
                        return dist <= radiusKm;
                    })
                    .toList();
        }

        return properties.stream().map(PropertyDTO::from).toList();
    }

    // ✅ Haversine formula for distance (km)
    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }



    public List<Property> getPropertiesByUser(Long userId) {
        return propertyRepo.findByOwnerId(userId);
    }

    @Transactional
    public void deleteProperty(Long id, String sellerEmail) {
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("You are not the owner of this property");
        }

        contactRepository.deleteByPropertyId(id);
        propertyRepo.delete(property);
    }


    public Optional<Property> getbypropertyId(Long id) {
        return propertyRepo.findById(id);
    }
}

