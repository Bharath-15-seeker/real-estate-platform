package com.realestate.real_estate_platform.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url; // or file path if storing locally

    @ManyToOne
    @JoinColumn(name = "property_id")
    @JsonIgnore // optional, prevents back-reference when serializing
    private Property property;
}

