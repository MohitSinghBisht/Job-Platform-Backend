package com.jobhunt.repository;

import com.jobhunt.model.Experience;
import com.jobhunt.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByJobSeeker(JobSeekerProfile jobSeeker);
}
