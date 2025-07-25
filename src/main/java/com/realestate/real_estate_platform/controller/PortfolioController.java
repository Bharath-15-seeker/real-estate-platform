package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.PortfolioDTO;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.PortfolioContact;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PortfolioContactRepository;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.service.EmailService;
import com.realestate.real_estate_platform.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
 import java.util.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private PortfolioContactRepository portfolioContactRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(
            @RequestBody PortfolioDTO dto,
            Principal principal
    ) {
        Portfolio portfolio = portfolioService.createPortfolio(dto, principal.getName());
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Portfolio>> getMyPortfolios(Principal principal) {
        String email = principal.getName(); // gets the currently authenticated user's email
        List<Portfolio> myPortfolios = portfolioService.getPortfoliosByUser(email);
        return ResponseEntity.ok(myPortfolios);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Portfolio>> getPublicPortfoliosByCategory(
            @RequestParam(required = false) String category) {

        List<Portfolio> portfolios = portfolioService.getPublicPortfolios(category);
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/public")
    public List<Portfolio> getAllPublicPortfolios() {
        return portfolioService.getPublicPortfolios();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long id,
                                                     @RequestBody Portfolio updatedPortfolio,
                                                     Principal principal) {
        Portfolio portfolio = portfolioService.updatePortfolio(id, updatedPortfolio, principal.getName());
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping("/{portfolioId}/contact")
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

}
