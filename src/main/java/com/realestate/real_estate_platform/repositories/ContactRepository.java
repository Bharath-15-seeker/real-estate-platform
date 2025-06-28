package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByPropertyOwnerId(Long ownerId); // to view who contacted my property
}

