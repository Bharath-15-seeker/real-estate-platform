package com.realestate.real_estate_platform.controller;

import com.realestate.real_estate_platform.dto.*;
import com.realestate.real_estate_platform.entity.BuilderProject;
import com.realestate.real_estate_platform.service.BuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/builder")
@RequiredArgsConstructor
public class BuilderController {

    private final BuilderService builderService;

    @PostMapping("/profile")
    public ResponseEntity<Void> createOrUpdateProfile(@RequestBody BuilderProfileRequest request,
                                                      Principal principal) {
        builderService.createOrUpdateProfile(principal.getName(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects")
    public ResponseEntity<Void> addProject(@RequestBody BuilderProjectRequest request,
                                           Principal principal) {
        builderService.addProject(principal.getName(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projects")
    public ResponseEntity<List<BuilderProjectResponse>> getProjects(Principal principal) {
        return ResponseEntity.ok(builderService.getProjects(principal.getName()));
    }
}

