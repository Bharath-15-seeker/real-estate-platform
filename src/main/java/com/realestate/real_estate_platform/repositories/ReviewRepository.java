package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.Review;
import com.realestate.real_estate_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPortfolioId(Long portfolioId);

    List<Review> findByUser(User user);


}

