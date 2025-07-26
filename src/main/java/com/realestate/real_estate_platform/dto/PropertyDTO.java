package com.realestate.real_estate_platform.dto;

import com.realestate.real_estate_platform.entity.Property;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class PropertyDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String location;
    private String type;
    private LocalDateTime postedAt;
    private List<String> imageUrls;
    private String ownerName;
    private String ownerEmail;



    public static PropertyDTO from(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
       // dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setLocation(property.getLocation());
        dto.setType(String.valueOf(property.getType()));
        dto.setPostedAt(property.getPostedAt());

        // Optional: Include image URLs
        if (property.getImages() != null) {
            dto.setImageUrls(property.getImages()
                    .stream()
                    .map(img -> img.getUrl())
                    .collect(Collectors.toList()));
        }

        // Optional: Include owner name/email
        if (property.getOwner() != null) {
            dto.setOwnerName(property.getOwner().getName());
            dto.setOwnerEmail(property.getOwner().getEmail());
        }

        return dto;
    }

}
