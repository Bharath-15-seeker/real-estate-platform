package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwnerId(Long ownerId);
    List<Property> findByType(PropertyType type);

    List<Property> findByLocationContainingIgnoreCase(String location);

}
