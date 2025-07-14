package com.jobhunt.repository;

import com.jobhunt.model.Education;
import com.jobhunt.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByJobSeeker(JobSeekerProfile jobSeeker);
}
