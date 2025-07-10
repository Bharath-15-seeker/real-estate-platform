// BuilderProfileRepository.java
package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.BuilderProfile;
import com.realestate.real_estate_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuilderProfileRepository extends JpaRepository<BuilderProfile, Long> {
    Optional<BuilderProfile> findByUser(User user);
}