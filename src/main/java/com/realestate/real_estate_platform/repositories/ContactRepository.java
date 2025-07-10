package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Contact;
import com.realestate.real_estate_platform.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByPropertyOwnerId(Long ownerId);
    List<Contact> findByPropertyIn(List<Property> properties);


    void deleteByPropertyId(Long id);

    List<Contact> findByEmail(String email);
// to view who contacted my property
}

