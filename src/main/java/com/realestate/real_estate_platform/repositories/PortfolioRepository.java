package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByOwner(User user);
    List<Portfolio> findByIsPublicTrue();


    List<Portfolio> findByIsPublicTrueAndCategoryIgnoreCase(String category);

}
