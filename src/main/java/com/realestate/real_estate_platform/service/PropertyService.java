package com.realestate.real_estate_platform.service;



import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepo;

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

    public List<Property> getAllProperties() {
        return propertyRepo.findAll();
    }

    public List<Property> getPropertiesByUser(Long userId) {
        return propertyRepo.findByOwnerId(userId);
    }

    public void deleteProperty(Long propertyId) {
        propertyRepo.deleteById(propertyId);
    }

    public Optional<Property> getbypropertyId(Long id) {
        return propertyRepo.findById(id);
    }
}

