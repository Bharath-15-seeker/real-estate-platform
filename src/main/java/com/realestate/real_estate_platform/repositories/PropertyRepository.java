package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.Prop_type;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.PropertyType;
import com.realestate.real_estate_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwnerId(Long ownerId);
    List<Property> findByType(PropertyType type);

    @Query("SELECT p FROM Property p WHERE "
            + "(:location IS NULL OR p.location = :location) AND "
            + "(:type IS NULL OR p.type = :type) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(:bhk IS NULL OR p.bhk = :bhk) AND "
            + "(:facing IS NULL OR p.facing = :facing) AND "
            + "(:propType IS NULL OR p.prop_type = :propType)")
    List<Property> search(@Param("location") String location,
                          @Param("type") PropertyType type,
                          @Param("minPrice") Double minPrice,
                          @Param("maxPrice") Double maxPrice,
                          @Param("bhk") Integer bhk,
                          @Param("facing") String facing,
                          @Param("propType") Prop_type propType);




    List<Property> findByLocationContainingIgnoreCase(String location);

    List<Property> findByOwner(User owner);
}
