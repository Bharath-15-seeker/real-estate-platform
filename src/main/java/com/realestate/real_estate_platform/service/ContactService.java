package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.ContactDTO;
import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    private ContactDTO mapToDTO(Contact contact) {
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getPhone());
        dto.setMessage(contact.getMessage());

        if (contact.getProperty() != null) {
            dto.setPropertyId(contact.getProperty().getId());
            dto.setPropertyTitle(contact.getProperty().getTitle());
        }

        return dto;
    }

    public List<ContactDTO> getContactsForOwner(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Property> properties = propertyRepository.findByOwner(owner);
        List<Contact> allContacts = contactRepository.findByPropertyIn(properties);

        return allContacts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

}
