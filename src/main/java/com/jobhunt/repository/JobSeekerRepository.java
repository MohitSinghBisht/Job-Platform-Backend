package com.jobhunt.repository;

import com.jobhunt.model.JobSeekerProfile;
import com.jobhunt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeekerProfile, Long> {
    Optional<JobSeekerProfile> findByUser(User user);
    
    @Query("SELECT js FROM JobSeekerProfile js JOIN js.skills s WHERE s = :skill")
    List<JobSeekerProfile> findBySkill(String skill);
    
    @Query("SELECT DISTINCT js FROM JobSeekerProfile js JOIN js.skills s WHERE s IN :skills")
    List<JobSeekerProfile> findBySkills(List<String> skills);
}
