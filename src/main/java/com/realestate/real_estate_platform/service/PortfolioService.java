package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.PortfolioDTO;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void createProperty(Portfolio property, MultipartFile[] images) {
       //property.setPostedAt(LocalDateTime.now());

        List<String> imagePaths = new ArrayList<>();

        if (images != null) {
            // Ensure upload directory exists
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            for (MultipartFile image : images) {
                try {
                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    File destinationFile = new File(uploadPath, filename);

                    image.transferTo(destinationFile);

                    // Store relative path (for frontend access)
                    imagePaths.add("/uploads/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        property.setWorkimages(imagePaths);
        portfolioRepo.save(property);
    }

    public Portfolio createPortfolio(PortfolioDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Portfolio portfolio = new Portfolio();
        portfolio.setTitle(dto.getTitle());
        portfolio.setDescription(dto.getDescription());
        portfolio.setCategory(dto.getCategory());
        portfolio.setIsPublic(dto.isPublic());
        portfolio.setOwner(user);

        return portfolioRepo.save(portfolio);
    }

    public List<Portfolio> getPublicPortfolios() {
        return portfolioRepo.findByIsPublicTrue();
    }

    public List<Portfolio> getPortfoliosByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return portfolioRepo.findByOwner(user);
    }


    public List<Portfolio> getPublicPortfolios(String category) {
        if (category == null || category.isBlank()) {
            return portfolioRepo.findByIsPublicTrue();
        } else {
            return portfolioRepo.findByIsPublicTrueAndCategoryIgnoreCase(category);
        }
    }

    public Portfolio updatePortfolio(Long id, Portfolio updatedData, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Portfolio portfolio = portfolioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolio.getOwner().getId().equals(user.getId())) {

            throw new RuntimeException("You are not authorized to update this portfolio.");
        }

        portfolio.setTitle(updatedData.getTitle());
        portfolio.setDescription(updatedData.getDescription());
        portfolio.setCategory(updatedData.getCategory());
        portfolio.setIsPublic(updatedData.getIsPublic());


            return portfolioRepo.save(portfolio);
    }
}
