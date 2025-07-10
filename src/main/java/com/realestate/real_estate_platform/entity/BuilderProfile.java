package com.realestate.real_estate_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuilderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int experience; // years of experience

    private String specialization; // e.g., "Villas", "Apartments"

    @Column(length = 1000)
    private String description;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "builder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuilderProject> projects;
}
