package com.realestate.real_estate_platform.mapper;

import com.realestate.real_estate_platform.dto.PropertyDTO;
import com.realestate.real_estate_platform.entity.Property;

public class PropertyMapper {

    public static PropertyDTO toDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
       // dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setLocation(property.getLocation());
        dto.setPrice(property.getPrice());
        dto.setType(String.valueOf(property.getType()));
        dto.setPostedAt(property.getPostedAt());
        return dto;
    }

    }

