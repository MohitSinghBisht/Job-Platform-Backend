package com.jobhunt.repository;

import com.jobhunt.model.Job;
import com.jobhunt.model.JobApplication;
import com.jobhunt.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobSeeker(JobSeekerProfile jobSeeker);
    List<JobApplication> findByJob(Job job);
    Optional<JobApplication> findByJobAndJobSeeker(Job job, JobSeekerProfile jobSeeker);
    List<JobApplication> findByStatus(JobApplication.ApplicationStatus status);
    List<JobApplication> findByJobSeekerAndStatus(JobSeekerProfile jobSeeker, JobApplication.ApplicationStatus status);
    List<JobApplication> findByJobAndStatus(Job job, JobApplication.ApplicationStatus status);
}
