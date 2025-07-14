package com.jobhunt.controller;

import com.jobhunt.model.*;
import com.jobhunt.repository.EducationRepository;
import com.jobhunt.repository.ExperienceRepository;
import com.jobhunt.service.JobService;
import com.jobhunt.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private EducationRepository educationRepository;
    
    @Autowired
    private ExperienceRepository experienceRepository;

    @GetMapping("/job-seeker")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<JobSeekerProfile> getJobSeekerProfile(@AuthenticationPrincipal User user) {
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/job-seeker")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<JobSeekerProfile> updateJobSeekerProfile(
            @RequestBody JobSeekerProfile profileUpdates,
            @AuthenticationPrincipal User user) {
        
        JobSeekerProfile updated = profileService.updateJobSeekerProfile(user, profileUpdates);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<RecruiterProfile> getRecruiterProfile(@AuthenticationPrincipal User user) {
        RecruiterProfile profile = profileService.getRecruiterProfile(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/recruiter")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<RecruiterProfile> updateRecruiterProfile(
            @RequestBody RecruiterProfile profileUpdates,
            @AuthenticationPrincipal User user) {
        
        RecruiterProfile updated = profileService.updateRecruiterProfile(user, profileUpdates);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/recommended-jobs")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<List<Job>> getRecommendedJobs(@AuthenticationPrincipal User user) {
        List<Job> recommendedJobs = jobService.getRecommendedJobs(user);
        return ResponseEntity.ok(recommendedJobs);
    }

    @PostMapping("/education")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<Education> addEducation(
            @RequestBody Education education,
            @AuthenticationPrincipal User user) {
        
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        education.setJobSeeker(profile);
        Education savedEducation = educationRepository.save(education);
        return ResponseEntity.ok(savedEducation);
    }

    @PutMapping("/education/{id}")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<Education> updateEducation(
            @PathVariable Long id,
            @RequestBody Education educationDetails,
            @AuthenticationPrincipal User user) {
        
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + id));
        
        // Verify ownership
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        if (!education.getJobSeeker().getId().equals(profile.getId())) {
            throw new RuntimeException("You are not authorized to update this education record");
        }
        
        education.setInstitutionName(educationDetails.getInstitutionName());
        education.setDegree(educationDetails.getDegree());
        education.setFieldOfStudy(educationDetails.getFieldOfStudy());
        education.setStartDate(educationDetails.getStartDate());
        education.setEndDate(educationDetails.getEndDate());
        education.setCurrent(educationDetails.isCurrent());
        education.setDescription(educationDetails.getDescription());
        education.setGrade(educationDetails.getGrade());
        
        Education updatedEducation = educationRepository.save(education);
        return ResponseEntity.ok(updatedEducation);
    }

    @DeleteMapping("/education/{id}")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<?> deleteEducation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + id));
        
        // Verify ownership
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        if (!education.getJobSeeker().getId().equals(profile.getId())) {
            throw new RuntimeException("You are not authorized to delete this education record");
        }
        
        educationRepository.delete(education);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/experience")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<Experience> addExperience(
            @RequestBody Experience experience,
            @AuthenticationPrincipal User user) {
        
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        experience.setJobSeeker(profile);
        Experience savedExperience = experienceRepository.save(experience);
        return ResponseEntity.ok(savedExperience);
    }

    @PutMapping("/experience/{id}")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<Experience> updateExperience(
            @PathVariable Long id,
            @RequestBody Experience experienceDetails,
            @AuthenticationPrincipal User user) {
        
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + id));
        
        // Verify ownership
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        if (!experience.getJobSeeker().getId().equals(profile.getId())) {
            throw new RuntimeException("You are not authorized to update this experience record");
        }
        
        experience.setCompanyName(experienceDetails.getCompanyName());
        experience.setTitle(experienceDetails.getTitle());
        experience.setLocation(experienceDetails.getLocation());
        experience.setStartDate(experienceDetails.getStartDate());
        experience.setEndDate(experienceDetails.getEndDate());
        experience.setCurrent(experienceDetails.isCurrent());
        experience.setDescription(experienceDetails.getDescription());
        experience.setSkills(experienceDetails.getSkills());
        
        Experience updatedExperience = experienceRepository.save(experience);
        return ResponseEntity.ok(updatedExperience);
    }

    @DeleteMapping("/experience/{id}")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<?> deleteExperience(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + id));
        
        // Verify ownership
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user);
        if (!experience.getJobSeeker().getId().equals(profile.getId())) {
            throw new RuntimeException("You are not authorized to delete this experience record");
        }
        
        experienceRepository.delete(experience);
        return ResponseEntity.ok().build();
    }
}
