package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Property;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByPropertyOwnerId(Long ownerId);
    List<Contact> findByPropertyIn(List<Property> properties);


    void deleteByPropertyId(Long id);

    List<Contact> findByEmail(String email);

    @Modifying // Indicates that this method modifies the database state
    @Transactional // Ensures the operation runs within a transaction
    @Query("DELETE FROM Contact c WHERE c.portfolio.id = :portfolioId")
    int deleteByPortfolioId(@Param("portfolioId") Long portfolioId);
// to view who contacted my property
}

