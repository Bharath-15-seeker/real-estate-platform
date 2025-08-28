package com.realestate.real_estate_platform.controller;



import com.realestate.real_estate_platform.dto.ContactDTO;
import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.PortfolioContact;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PortfolioContactRepository;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.service.ContactService;
import com.realestate.real_estate_platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;
    private final PropertyRepository propertyRepository;
    private final ContactService contactService;

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private PortfolioContactRepository portfolioContactRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/{propertyId}")
    public ResponseEntity<Contact> contactPropertyOwner(@PathVariable Long propertyId,
                                                        @RequestBody Contact contactRequest) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        contactRequest.setProperty(property);
        Contact saved = contactRepository.save(contactRequest);

        // Send email to owner
        String ownerEmail = property.getOwner().getEmail(); // Ensure this is valid
        sendContactEmail(ownerEmail, contactRequest);

        return ResponseEntity.ok(saved);
    }


    @PostMapping("/{portfolioId}/portfolio")
    public ResponseEntity<PortfolioContact> contactPortfolioOwner(
            @PathVariable Long portfolioId,
            @RequestBody PortfolioContact contactRequest) {

        Portfolio portfolio = portfolioRepo.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        contactRequest.setPortfolio(portfolio);
        PortfolioContact saved = portfolioContactRepository.save(contactRequest);

        // Send email to portfolio owner
        String ownerEmail = portfolio.getOwner().getEmail();
        sendPortfolioContactEmail(ownerEmail, contactRequest);

        return ResponseEntity.ok(saved);
    }


    private void sendPortfolioContactEmail(String to, PortfolioContact contactRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Inquiry for Your Portfolio");
        message.setText("You have a new contact from: " + contactRequest.getName() +
                "\nEmail: " + contactRequest.getEmail() +
                "\nMessage: " + contactRequest.getMessage() +
                "\nPhone: " + contactRequest.getPhone());
        mailSender.send(message);
    }


    private void sendContactEmail(String to, Contact contactRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Inquiry for Your Property");
        message.setText("You have a new contact from: " + contactRequest.getName() +
                "\nEmail: " + contactRequest.getEmail() +
                "\nMessage: " + contactRequest.getMessage()+
                "\nPhone:" + contactRequest.getPhone());
        mailSender.send(message);
    }




    @GetMapping("/received")
   // @PreAuthorize("hasAuthority('SELLER')")  // Optional: restrict to sellers
    public ResponseEntity<List<ContactDTO>> getReceivedContacts(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(contactService.getContactsForOwner(email));
    }

    // 👀 Property owner sees all requests for their properties
    @GetMapping("/my")
    public ResponseEntity<List<Contact>> getMyContactRequests(@RequestParam Long ownerId) {
        return ResponseEntity.ok(contactRepository.findByPropertyOwnerId(ownerId));
    }
}

