package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Favorite;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserEmail(String email);
    boolean existsByUserAndProperty(User user, Property property);
    long deleteByUserAndProperty(User user, Property property);
    Optional<Favorite> findByUserAndProperty(User user, Property property);

    List<Favorite> findByUser(User user);

    long deleteByUserAndPortfolio(User user, Portfolio portfolio);

    List<Favorite> findByUserId(Long userId);

    List<Favorite> findByUser_Id(Long id);
}

