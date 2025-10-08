package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.Review;
import com.realestate.real_estate_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPortfolioId(Long portfolioId);

    List<Review> findByUser(User user);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.portfolio.id = :portfolioId")
    Double findAverageRatingByPortfolioId(@Param("portfolioId") Long portfolioId);


    void deleteAllByPortfolioId(Long id);
}

