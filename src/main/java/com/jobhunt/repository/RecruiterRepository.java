package com.jobhunt.repository;

import com.jobhunt.model.RecruiterProfile;
import com.jobhunt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<RecruiterProfile, Long> {
    Optional<RecruiterProfile> findByUser(User user);
    List<RecruiterProfile> findByCompanyNameContainingIgnoreCase(String companyName);
    List<RecruiterProfile> findByIndustryContainingIgnoreCase(String industry);
}
