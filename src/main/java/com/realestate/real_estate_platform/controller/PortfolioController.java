package com.realestate.real_estate_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final ObjectMapper objectMapper;

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

    @DeleteMapping("/{portfolioId}")

    public ResponseEntity<String> deleteProperty(@PathVariable Long portfolioId,
                                                 Authentication authentication) {
        String email = authentication.getName();
        portfolioService.deletePortfolio(portfolioId, email);
        return ResponseEntity.ok("Property deleted successfully");
    }


    @GetMapping("/{id}")
    public ResponseEntity<Optional<Portfolio>> searchbyid(@PathVariable Long id)
    {
        return ResponseEntity.ok(portfolioService.getbyportfolioId(id));
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
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable Long id,
            @RequestPart("portfolio") String portfolioJson,
            @RequestPart(value = "workimages", required = false) MultipartFile[] newWorkImages,
            @RequestPart(value = "dp", required = false) MultipartFile dpImage
    ) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            System.out.println("Portfolio JSON received: " + portfolioJson);

            // Deserialize portfolio data
            Portfolio updatedData = objectMapper.readValue(portfolioJson, Portfolio.class);

            // Extract imagesToDelete list
            List<String> workImagesToDelete = updatedData.getImagesToDelete();
            System.out.println("Work images to delete: " + workImagesToDelete);

            // Get authenticated user email
            String ownerEmail = getAuthenticatedUserEmail();

            // Update portfolio
            Portfolio updatedPortfolio = portfolioService.updatePortfolioWithImages(
                    id,
                    updatedData,
                    newWorkImages,
                    dpImage,
                    workImagesToDelete,
                    ownerEmail
            );

            return ResponseEntity.ok(updatedPortfolio);

        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("Portfolio not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e instanceof AccessDeniedException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        throw new AccessDeniedException("Unable to determine user email");
    }




}
