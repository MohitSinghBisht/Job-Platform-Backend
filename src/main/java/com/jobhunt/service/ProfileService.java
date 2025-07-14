package com.jobhunt.service;

import com.jobhunt.model.JobSeekerProfile;
import com.jobhunt.model.RecruiterProfile;
import com.jobhunt.model.User;
import com.jobhunt.repository.JobSeekerRepository;
import com.jobhunt.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    public JobSeekerProfile createJobSeekerProfile(User user) {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        return jobSeekerRepository.save(profile);
    }

    public RecruiterProfile createRecruiterProfile(User user) {
        RecruiterProfile profile = new RecruiterProfile();
        profile.setUser(user);
        return recruiterRepository.save(profile);
    }

    public JobSeekerProfile getJobSeekerProfile(User user) {
        return jobSeekerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("JobSeeker profile not found"));
    }

    public RecruiterProfile getRecruiterProfile(User user) {
        return recruiterRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
    }

    public JobSeekerProfile updateJobSeekerProfile(User user, JobSeekerProfile profileUpdates) {
        JobSeekerProfile existingProfile = getJobSeekerProfile(user);
        
        // Update the profile with new values
        if (profileUpdates.getResumeUrl() != null) {
            existingProfile.setResumeUrl(profileUpdates.getResumeUrl());
        }
        if (profileUpdates.getSkills() != null && !profileUpdates.getSkills().isEmpty()) {
            existingProfile.setSkills(profileUpdates.getSkills());
        }
        if (profileUpdates.getJobPreferences() != null) {
            existingProfile.setJobPreferences(profileUpdates.getJobPreferences());
        }
        
        return jobSeekerRepository.save(existingProfile);
    }

    public RecruiterProfile updateRecruiterProfile(User user, RecruiterProfile profileUpdates) {
        RecruiterProfile existingProfile = getRecruiterProfile(user);
        
        // Update the profile with new values
        if (profileUpdates.getCompanyName() != null) {
            existingProfile.setCompanyName(profileUpdates.getCompanyName());
        }
        if (profileUpdates.getCompanyWebsite() != null) {
            existingProfile.setCompanyWebsite(profileUpdates.getCompanyWebsite());
        }
        if (profileUpdates.getCompanySize() != null) {
            existingProfile.setCompanySize(profileUpdates.getCompanySize());
        }
        if (profileUpdates.getIndustry() != null) {
            existingProfile.setIndustry(profileUpdates.getIndustry());
        }
        if (profileUpdates.getCompanyDescription() != null) {
            existingProfile.setCompanyDescription(profileUpdates.getCompanyDescription());
        }
        if (profileUpdates.getCompanyLogoUrl() != null) {
            existingProfile.setCompanyLogoUrl(profileUpdates.getCompanyLogoUrl());
        }
        if (profileUpdates.getHiringManagerTitle() != null) {
            existingProfile.setHiringManagerTitle(profileUpdates.getHiringManagerTitle());
        }
        
        return recruiterRepository.save(existingProfile);
    }
}
