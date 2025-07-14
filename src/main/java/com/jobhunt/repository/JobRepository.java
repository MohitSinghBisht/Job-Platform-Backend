package com.jobhunt.repository;

import com.jobhunt.model.Job;
import com.jobhunt.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByRecruiter(RecruiterProfile recruiter);
    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findByLocationContainingIgnoreCase(String location);
    List<Job> findByActiveTrue();
    List<Job> findByActiveTrueAndClosingDateAfter(LocalDateTime now);
    
    @Query("SELECT j FROM Job j JOIN j.requiredSkills s WHERE s IN :skills AND j.active = true AND j.closingDate > :now")
    List<Job> findMatchingJobsBySkills(List<String> skills, LocalDateTime now);
    
    @Query("SELECT j FROM Job j WHERE j.experienceLevel = :level AND j.active = true AND j.closingDate > :now")
    List<Job> findByExperienceLevel(String level, LocalDateTime now);
    
    @Query("SELECT j FROM Job j WHERE j.jobType = :type AND j.active = true AND j.closingDate > :now")
    List<Job> findByJobType(String type, LocalDateTime now);
    
    @Query("SELECT j FROM Job j WHERE j.remoteAllowed = true AND j.active = true AND j.closingDate > :now")
    List<Job> findRemoteJobs(LocalDateTime now);
}
