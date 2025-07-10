package com.realestate.real_estate_platform.service;



import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepo;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public Property createProperty(Property property) {
        property.setPostedAt(LocalDateTime.now());
        return propertyRepo.save(property);
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

        property.setTitle(updatedData.getTitle());
        property.setDescription(updatedData.getDescription());
        property.setLocation(updatedData.getLocation());
        property.setPrice(updatedData.getPrice());
        property.setType(updatedData.getType());
        return propertyRepo.save(property);
    }


    public List<Property> getAllProperties() {
        return propertyRepo.findAll();
    }

    public List<PropertyDTO> searchProperties(String location, String type, Double minPrice, Double maxPrice) {
        List<Property> properties = propertyRepo.search(location, type, minPrice, maxPrice);
        return properties.stream().map(PropertyDTO::from).collect(Collectors.toList());
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

