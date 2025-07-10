package com.realestate.real_estate_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuilderProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String imageUrl; // Link to uploaded image

    private LocalDate completedOn;

    @ManyToOne
    @JoinColumn(name = "builder_id", nullable = false)
    private BuilderProfile builder;
}
