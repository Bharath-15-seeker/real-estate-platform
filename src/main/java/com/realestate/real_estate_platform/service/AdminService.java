package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.UserDTO;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepo;
    private final PropertyRepository propertyRepo;

    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().toString()))
                .collect(Collectors.toList());
    }

    public List<Property> getAllProperties() {
        return propertyRepo.findAll();
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 1: Delete properties first
        List<Property> properties = propertyRepo.findByOwner(user);
        for (Property property : properties) {
            propertyRepo.delete(property); // This will also delete contacts, favorites etc. if cascaded
        }

        // Step 2: Delete user
        userRepo.delete(user);
    }

    @Transactional
    public void deletePropertyById(Long id) {
        if (!propertyRepo.existsById(id)) {
            throw new RuntimeException("Property not found with id: " + id);
        }
        propertyRepo.deleteById(id);
    }

}
