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

    private String description;
    private String location;
    private int bhk;

    private String facing;
    private String Address;
    private Double sqft;

    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "prop_type")
    private Prop_type prop_type;

    @Enumerated(EnumType.STRING)
    private PropertyType type; // RENT, SALE, LAND

    private LocalDateTime postedAt;

//    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties("property") // prevents infinite recursion during JSON serialization
//    private List<PropertyImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("property")
    private List<Review> reviews;

    // Link to the user who posted the property

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties("properties") // ignore the "properties" field inside owner
    private User owner;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts;


    @ElementCollection
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();


}

