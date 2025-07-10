package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.BuilderProfileRequest;
import com.realestate.real_estate_platform.dto.BuilderProjectRequest;
import com.realestate.real_estate_platform.dto.BuilderProjectResponse;
import com.realestate.real_estate_platform.entity.*;
import com.realestate.real_estate_platform.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuilderService {

    private final UserRepository userRepo;
    private final BuilderProfileRepository builderProfileRepo;
    private final BuilderProjectRepository builderProjectRepo;

    public void createOrUpdateProfile(String email, BuilderProfileRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BuilderProfile profile = builderProfileRepo.findByUser(user)
                .orElse(BuilderProfile.builder().user(user).build());

        profile.setExperience(Integer.parseInt(request.getExperience()));
        profile.setSpecialization(request.getSpecialization());
        profile.setDescription(request.getDescription());

        builderProfileRepo.save(profile);
    }

    public void addProject(String email, BuilderProjectRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BuilderProfile profile = builderProfileRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Builder profile not found"));

        BuilderProject project = BuilderProject.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .completedOn(request.getCompletedOn())
                .builder(profile)
                .build();

        builderProjectRepo.save(project);
    }

    public List<BuilderProjectResponse> getProjects(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BuilderProfile profile = builderProfileRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Builder profile not found"));

        return builderProjectRepo.findByBuilder(profile)
                .stream()
                .map(project -> new BuilderProjectResponse(
                        project.getId(),
                        project.getTitle(),
                        project.getDescription(),
                        project.getImageUrl()
                ))
                .toList();
    }

}
