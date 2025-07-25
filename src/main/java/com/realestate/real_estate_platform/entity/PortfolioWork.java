package com.realestate.real_estate_platform.entity;

import jakarta.persistence.*;

@Entity
public class PortfolioWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String imageUrl;

    @ManyToOne
    private Portfolio portfolio;
}

