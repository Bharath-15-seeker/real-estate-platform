package com.realestate.real_estate_platform.controller;



import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;
    private final PropertyRepository propertyRepository;

    // 📨 User sends a contact request for a property
    @PostMapping("/{propertyId}")
    public ResponseEntity<Contact> contactPropertyOwner(@PathVariable Long propertyId,
                                                        @RequestBody Contact contactRequest) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        contactRequest.setProperty(property);
        Contact saved = contactRepository.save(contactRequest);
        return ResponseEntity.ok(saved);
    }

    // 👀 Property owner sees all requests for their properties
    @GetMapping("/my")
    public ResponseEntity<List<Contact>> getMyContactRequests(@RequestParam Long ownerId) {
        return ResponseEntity.ok(contactRepository.findByPropertyOwnerId(ownerId));
    }
}

