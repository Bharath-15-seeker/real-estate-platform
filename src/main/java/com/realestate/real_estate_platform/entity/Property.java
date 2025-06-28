package com.realestate.real_estate_platform.entity;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;

    private Double price;

    @Enumerated(EnumType.STRING)
    private PropertyType type; // RENT, SALE, LAND

    private LocalDateTime postedAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("property") // prevents infinite recursion during JSON serialization
    private List<PropertyImage> images = new ArrayList<>();

    // Link to the user who posted the property
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}

