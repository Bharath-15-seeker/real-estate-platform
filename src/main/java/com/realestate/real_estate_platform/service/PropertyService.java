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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // --- Helper method to manage file creation (reused from createProperty) ---
    private String saveFile(MultipartFile file) throws IOException {
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destinationFile = new File(uploadPath, filename);

        file.transferTo(destinationFile);
        // Returns the relative path used by the frontend
        return "/uploads/" + filename;
    }

    // --- Helper method to delete file ---
    private void deleteFile(String urlPath) throws IOException {
        // The URL path is expected to be "/uploads/filename.jpg"
        // We need to extract just "filename.jpg" and prepend the absolute uploadDir.
        if (urlPath == null || !urlPath.startsWith("/uploads/")) return;

        String filename = urlPath.substring("/uploads/".length());
        Path filePath = Path.of(uploadDir, filename);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }


    public void createProperty(Property property, MultipartFile[] images) {
        property.setPostedAt(LocalDateTime.now());

        List<String> imagePaths = new ArrayList<>();

        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String path = saveFile(image);
                    imagePaths.add(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Consider throwing a custom exception here
                }
            }
        }

        property.setImageUrls(imagePaths);
        propertyRepo.save(property);
    }


    // Removed redundant updateProperty method. The combined one is below.

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

    // ✅ CORRECTED AND CONSOLIDATED UPDATE METHOD
    public Property updatePropertyWithImages(
            Long id,
            Property updatedData,
            MultipartFile[] newImages,
            List<String> imagesToDelete,
            String sellerEmail
    ) {
        // 1. Fetch Property and Authorization Check
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (property.getOwner() == null) {
            throw new AccessDeniedException("Property owner is missing");
        }

        if (sellerEmail == null || sellerEmail.trim().isEmpty()) {
            throw new AccessDeniedException("Seller email is required");
        }

        String ownerEmail = property.getOwner().getEmail();
        if (!ownerEmail.trim().equalsIgnoreCase(sellerEmail.trim())) {
            throw new AccessDeniedException("You are not the owner of this property");
        }

        // 2. Update ALL non-image fields
        property.setTitle(updatedData.getTitle());
        property.setDescription(updatedData.getDescription());
        property.setLocation(updatedData.getLocation());
        property.setPrice(updatedData.getPrice());
        property.setType(updatedData.getType());
        property.setAddress(updatedData.getAddress());
        property.setBhk(updatedData.getBhk());
        property.setSqft(updatedData.getSqft());
        property.setFacing(updatedData.getFacing());
        property.setProp_type(updatedData.getProp_type());
        property.setLatitude(updatedData.getLatitude());
        property.setLongitude(updatedData.getLongitude());

        // 3. Handle Image Deletions
        List<String> currentImageUrls = property.getImageUrls() != null
                ? new ArrayList<>(property.getImageUrls()) // ✅ Create new list to avoid reference issues
                : new ArrayList<>();

        // ✅ Add debug logging
        System.out.println("Current images before deletion: " + currentImageUrls);
        System.out.println("Images to delete in service: " + imagesToDelete);

        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            for (String urlKey : imagesToDelete) {
                System.out.println("Attempting to delete: " + urlKey);
                try {
                    deleteFile(urlKey); // delete from storage
                    System.out.println("File deleted from storage: " + urlKey);
                } catch (IOException e) {
                    System.err.println("Failed to delete old image: " + urlKey + ". Reason: " + e.getMessage());
                }

                boolean removed = currentImageUrls.remove(urlKey);
                System.out.println("Removed from list: " + removed + " for " + urlKey);
            }
        }

        System.out.println("Current images after deletion: " + currentImageUrls);
        property.setImageUrls(currentImageUrls);

        // 4. Handle New Image Uploads
        if (newImages != null && newImages.length > 0) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    try {
                        String newUrlKey = saveFile(file);
                        currentImageUrls.add(newUrlKey);
                        System.out.println("New image added: " + newUrlKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Failed to save new image: " + file.getOriginalFilename());
                    }
                }
            }
        }

        // 5. Save final property to DB
        Property savedProperty = propertyRepo.save(property);
        System.out.println("Property saved with images: " + savedProperty.getImageUrls());
        return savedProperty;
    }

    public List<Property> getAllProperties() {
        return propertyRepo.findAll();
    }

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

        // If lat/lng provided, filter properties by distance
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

    // Haversine formula for distance (km)
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

        // Delete all associated files before deleting the entity
        if (property.getImageUrls() != null) {
            for(String urlKey : property.getImageUrls()) {
                try {
                    deleteFile(urlKey);
                } catch (IOException e) {
                    System.err.println("Failed to delete property file during deletion: " + urlKey);
                }
            }
        }

        contactRepository.deleteByPropertyId(id);
        propertyRepo.delete(property);
    }


    public Optional<Property> getbypropertyId(Long id) {
        return propertyRepo.findById(id);
    }
}