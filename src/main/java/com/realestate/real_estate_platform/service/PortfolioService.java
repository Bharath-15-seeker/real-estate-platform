package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.PortfolioDTO;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepo;

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
