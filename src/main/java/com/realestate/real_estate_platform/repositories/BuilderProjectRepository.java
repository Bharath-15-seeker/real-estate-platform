package com.realestate.real_estate_platform.repositories;

import com.realestate.real_estate_platform.entity.BuilderProject;
import com.realestate.real_estate_platform.entity.BuilderProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuilderProjectRepository extends JpaRepository<BuilderProject, Long> {
    List<BuilderProject> findByBuilder(BuilderProfile builder);
}
