package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyImage;
import com.realestate.real_estate_platform.repositories.PropertyImageRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyImageService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository imageRepository;

    public String uploadImage(Long propertyId, MultipartFile file) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/" + fileName);
        Files.write(uploadPath, file.getBytes());

        PropertyImage image = new PropertyImage();
        image.setUrl("/uploads/" + fileName);
        image.setProperty(property);
        imageRepository.save(image);

        return "Image uploaded successfully!";
    }
}

