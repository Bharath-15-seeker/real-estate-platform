package com.realestate.real_estate_platform.dto;

import com.realestate.real_estate_platform.entity.Prop_type;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class PropertyDTO {
    private Long id;
    private String description;
    private Double price;
    private String location;
    private PropertyType type;   // SALE / RENT
    private Prop_type prop_type; // APARTMENT / VILLA / PLOT etc
    private int bhk;
    private String facing;
    private String address;
    private Double sqft;
    private LocalDateTime postedAt;
    private List<String> imageUrls;

    public static PropertyDTO from(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setLocation(property.getLocation());
        dto.setType(property.getType());           // ✅ now set
        dto.setProp_type(property.getProp_type()); // ✅ now set
        dto.setBhk(property.getBhk());             // ✅ now set
        dto.setFacing(property.getFacing());       // ✅ now set
        dto.setAddress(property.getAddress());     // ✅ now set
        dto.setSqft(property.getSqft());           // ✅ now set
        dto.setPostedAt(property.getPostedAt());

        if (property.getImageUrls() != null) {
            dto.setImageUrls(new ArrayList<>(property.getImageUrls()));
        }

        return dto;
    }
}
