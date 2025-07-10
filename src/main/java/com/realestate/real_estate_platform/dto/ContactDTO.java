package com.realestate.real_estate_platform.dto;

import com.realestate.real_estate_platform.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String message;
    private Long propertyId;
    private String propertyTitle;



    public static ContactDTO from(Contact contact) {
        return ContactDTO.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .message(contact.getMessage())
                .propertyId(contact.getProperty().getId())
                .propertyTitle(contact.getProperty().getTitle())
                .build();
    }

}
