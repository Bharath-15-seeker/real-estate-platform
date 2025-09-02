package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.PortfolioDTO;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.PortfolioContact;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PortfolioContactRepository;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import com.realestate.real_estate_platform.service.EmailService;
import com.realestate.real_estate_platform.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private PortfolioContactRepository portfolioContactRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPortfolio(
            @RequestPart("portfolio") Portfolio portfolio,
            @RequestPart(value = "dp", required = false) MultipartFile dp,
            @RequestPart(value = "workimages", required = false) MultipartFile[] images,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        portfolio.setOwner(user);

        portfolioService.createPortfolio(portfolio, dp, images);

        return "Portfolio posted successfully";
    }


    @GetMapping("/{id}")
    public ResponseEntity<Optional<Portfolio>> searchbyid(@PathVariable Long id)
    {
        return ResponseEntity.ok(portfolioService.getbypropertyId(id));
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





}
