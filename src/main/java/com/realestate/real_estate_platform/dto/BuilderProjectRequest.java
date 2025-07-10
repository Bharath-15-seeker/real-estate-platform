package com.realestate.real_estate_platform.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class BuilderProjectRequest {
    private String title;
    private String description;
    private String location;
    private MultipartFile image;
    private String imageUrl; // or MultipartFile if uploading
    private LocalDate completedOn; // optional image of the project
}

