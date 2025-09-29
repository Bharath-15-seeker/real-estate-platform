package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.PortfolioDTO;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void createPortfolio(Portfolio portfolio, MultipartFile dp, MultipartFile[] images) {
        List<String> imagePaths = new ArrayList<>();

        // Ensure upload directory exists
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // ✅ Save Display Picture (if provided)
        if (dp != null && !dp.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + "_" + dp.getOriginalFilename();
                File destinationFile = new File(uploadPath, filename);
                dp.transferTo(destinationFile);

                // Store relative path for frontend
                portfolio.setDp("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ✅ Save multiple work images
        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    File destinationFile = new File(uploadPath, filename);
                    image.transferTo(destinationFile);

                    imagePaths.add("/uploads/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        portfolio.setWorkimages(imagePaths);
        portfolioRepo.save(portfolio);
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

    public Optional<Portfolio> getbyportfolioId(Long id) {
        return portfolioRepo.findById(id);
    }

    @Transactional
    public void deletePortfolio(Long id, String sellerEmail) {
        Portfolio portfolio = portfolioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolio.getOwner().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("You are not the authorized person to delete");
        }

        contactRepository.deleteByPortfolioId(id);
        portfolioRepo.delete(portfolio);
    }
}
